package xlc.quant.data.indicator.calculator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import xlc.quant.data.indicator.Indicator;
import xlc.quant.data.indicator.IndicatorCalculator;
import xlc.quant.data.indicator.IndicatorCalculatorCallback;


/**
 * @author Rootfive
 * 百度百科：https://baike.baidu.com/item/KDJ%E6%8C%87%E6%A0%87
 * 
 * KDJ指标又叫随机指标，随机指标在图表上共有三根线，K线、D线和J线。
 * 随机指标在计算中考虑了计算周期内的最高随机指标价、最低价，兼顾了股价波动中的随机振幅，因而人们认为随机指标更真实地反映股价的波动，其提示作用更加明显。
 * kdj指标K表示什么KDJ是随机指标，
 * K是RSV的M天的移动平均价，
 * D是K的M1天的移动平均价，
 * J是3K减2D
 * 
 * 933 这3种数值是经过长期投资实操得出的满意参数，
 * 比较适合短线的进出，而若是在中长线来看，9天的参数仍然不够用，所以有的人会选用36，3，3的参数。
 * kdj指标也叫做随机指标，是一种股票技术分析指标，主要用于股市的中短期趋势分析，是股票市场上最常用的技术分析工具之一。
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class KDJ extends Indicator {

	/** KDJ-K值 */
	private BigDecimal k;

	/** KDJ-D值 */
	private BigDecimal d;

	/** KDJ-J值 */
	private BigDecimal j;

	public KDJ(BigDecimal k, BigDecimal d) {
		super();
		this.k = k;
		this.d = d;
	}

	public KDJ(BigDecimal k, BigDecimal d, BigDecimal j) {
		super();
		this.k = k;
		this.d = d;
		this.j = j;
	}

	//=============
	//内部类分隔符 XXX
	//=============
	/**
	 * 构建-计算器
	 * 
	 * @param capacity
	 * @return
	 */
	public static IndicatorCalculator<KDJ> buildCalculator(int capacity, int kCycle, int dCycle) {
		return new KDJCalculator(capacity, kCycle, dCycle);
	}

	/**
	 * 计算器
	 * @author Rootfive
	 */
	private static class KDJCalculator extends IndicatorCalculator<KDJ> {

		/** K值的计算周期 */
		private final BigDecimal kCycle;

		/** D值的计算周期 */
		private final BigDecimal dCycle;

		KDJCalculator(int capacity, int kCycle, int dCycle) {
			super(capacity, true);
			this.kCycle = new BigDecimal(kCycle);
			this.dCycle = new BigDecimal(dCycle);
		}

		/***
		 * 公式说明：以日KDJ数值的计算为例， 计算公式为：n日RSV=（Cn－Ln）÷（Hn－Ln）×100
		 * RSV=(收盘价-最近N日最低价)+(最近N日最高价-最近N日最低价)x100) 公式说明: Cn为第n日收盘价； Ln为n日内的最低价；
		 * Hn为n日内的最高价。 RSV值始终在1—100间波动。
		 */
		@Override
		protected KDJ executeCalculate() {
			IndicatorCalculatorCallback<KDJ> headData = getHead();

			// 第收盘价
			BigDecimal valueCn = headData.getClose();
			// Hn为n日内的最高价
			BigDecimal valueHn = super.getCalculatorListData().stream().max(Comparator.comparing(IndicatorCalculatorCallback::getHigh)).get().getHigh();
			// Ln为n日内的最低价
			BigDecimal valueLn = super.getCalculatorListData().stream().min(Comparator.comparing(IndicatorCalculatorCallback::getLow)).get().getLow();
					

			// 计算公式为：n日RSV=（Cn－Ln）÷（Hn－Ln）×100,四舍五入，保留4位小数
			BigDecimal rsvValue = (valueCn.subtract(valueLn)).divide((valueHn.subtract(valueLn)), 4, RoundingMode.HALF_UP).multiply(HUNDRED);
					
			/**
			 * 计算K值: 当日K值=2/3×前一日K值＋1/3×当日RSV 计算D值： 当日D值=2/3×前一日D值＋1/3×当日K值 计算J值：
			 * 当日J值=3*当日K值-2*当日D值 【注意】若无前一日K 值与D值，则可分别用50（1-100的中间值）来代替。
			 */
			IndicatorCalculatorCallback<KDJ> prev = getPrev();

			// 前一个交易统计的KDJ
			KDJ prevKdj = null;
			if (prev != null) {
				// 前一个交易统计的KDJ
				prevKdj = prev.getIndicator();
			}
			if (prevKdj == null) {
				// prevK 为空，取默认值 50
				prevKdj = new KDJ(INT_50, INT_50);
			}
			// ====================================================================

			// 求K的系数,四舍五入，保留4位小数
			BigDecimal kRatioA = BigDecimal.ONE.divide(this.kCycle, 4, RoundingMode.HALF_UP);
			BigDecimal kRatioB = BigDecimal.ONE.subtract(kRatioA);

			// 计算K值:当日K值=2/3×前一日K值＋1/3×当日RSV
			BigDecimal kValue = (kRatioB.multiply(prevKdj.getK())).add(kRatioA.multiply(rsvValue));

			// 求D的系数,四舍五入，保留4位小数
			BigDecimal dRatioA = BigDecimal.ONE.divide(this.dCycle, 4, RoundingMode.HALF_UP);
			BigDecimal dRatioB = BigDecimal.ONE.subtract(dRatioA);

			// 计算D值：当日D值=2/3×前一日D值＋1/3×当日K值
			BigDecimal dValue = (dRatioB.multiply(prevKdj.getD())).add(dRatioA.multiply(kValue));

			// ====================================================================
			// 当日J值=3*当日K值-2*当日D值
			BigDecimal jValue = (INT_3.multiply(kValue)).subtract((INT_2.multiply(dValue)));
					

			// ====================================================================
			BigDecimal headK = kValue.setScale(2, RoundingMode.HALF_UP);
			BigDecimal headD = dValue.setScale(2, RoundingMode.HALF_UP);
			BigDecimal headJ = jValue.setScale(2, RoundingMode.HALF_UP);
			return new KDJ(headK, headD, headJ);
		}

	}

}
