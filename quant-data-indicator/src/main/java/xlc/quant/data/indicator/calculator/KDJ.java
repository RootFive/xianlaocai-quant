package xlc.quant.data.indicator.calculator;

import java.util.function.Function;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import xlc.quant.data.indicator.Indicator;
import xlc.quant.data.indicator.IndicatorCalculator;
import xlc.quant.data.indicator.IndicatorCalculateCarrier;
import xlc.quant.data.indicator.util.DoubleUtils;


/**
 * @author Rootfive
 * 百度百科：https://baike.baidu.com/item/KDJ%E6%8C%87%E6%A0%87
 * <pre>
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
 * </pre>
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class KDJ extends Indicator {

	/** KDJ-K值 */
	private Double k;

	/** KDJ-D值 */
	private Double d;

	/** KDJ-J值 */
	private Double j;

	/**
	 * @param k
	 * @param d
	 * 
	 */
	public KDJ(double k, double d) {
		super();
		this.k = k;
		this.d = d;
	}

	public KDJ(Double k, Double d, Double j) {
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
	public static <CARRIER extends IndicatorCalculateCarrier<?>>  IndicatorCalculator<CARRIER, KDJ> buildCalculator(int capacity, int kCycle, int dCycle) {
		return new KDJCalculator<>(capacity, kCycle, dCycle);
	}

	/**
	 * 计算器
	 * @author Rootfive
	 */
	private static class KDJCalculator<CARRIER extends IndicatorCalculateCarrier<?>> extends IndicatorCalculator<CARRIER, KDJ> {
		/** K值的计算周期 */
		private final int kCycle;

		/** D值的计算周期 */
		private final int dCycle;

		KDJCalculator(int capacity, int kCycle, int dCycle) {
			super(capacity, true);
			this.kCycle = kCycle;
			this.dCycle = dCycle;
		}

		/***
		 * <pre>
		 * 公式说明：以日KDJ数值的计算为例， 计算公式为：n日RSV=（Cn－Ln）÷（Hn－Ln）×100
		 * RSV=(收盘价-最近N日最低价)+(最近N日最高价-最近N日最低价)x100) 公式说明: Cn为第n日收盘价； Ln为n日内的最低价；
		 * Hn为n日内的最高价。 RSV值始终在1—100间波动。
		 * </pre>
		 */
		@Override
		protected KDJ executeCalculate(Function<CARRIER, KDJ> propertyGetter) {
			CARRIER headData = getHead();
			// 第收盘价
			double valueCn = headData.getClose();
			// Hn为n日内的最高价
			double valueHn = headData.getHigh();
			// Ln为n日内的最低价
			double valueLn = headData.getLow();
			for (int i = 1; i < capacity(); i++) {
				CARRIER carrier_i = get(i);
				valueHn = Math.max(valueHn, carrier_i.getHigh());
				valueLn = Math.min(valueLn, carrier_i.getLow());
			}
			
			// 计算公式为：n日RSV=（Cn－Ln）÷（Hn－Ln）×100,四舍五入，保留4位小数
//			BigDecimal rsvValue = (valueCn.subtract(valueLn)).divide((valueHn.subtract(valueLn)), 4, RoundingMode.HALF_UP).multiply(HUNDRED);
			Double rsvValue =null;
			if (valueHn == valueLn) {
				//连续横盘的极端情况valueHn=valueLn
				rsvValue=DoubleUtils.ZERO;
			}else {
				rsvValue = DoubleUtils.divideByPct(valueCn-valueLn,valueHn-valueLn);
			}
					
			/**
			 * 计算K值: 当日K值=2/3×前一日K值＋1/3×当日RSV 计算D值： 当日D值=2/3×前一日D值＋1/3×当日K值 计算J值：
			 * 当日J值=3*当日K值-2*当日D值 【注意】若无前一日K 值与D值，则可分别用50（1-100的中间值）来代替。
			 */
			CARRIER prev = getPrev();

			// 前一个交易统计的KDJ
			KDJ prevKdj = null;
			if (prev != null) {
				// 前一个交易统计的KDJ
				prevKdj = propertyGetter.apply(prev);
			}
			if (prevKdj == null) {
				// prevK 为空，取默认值 50
				prevKdj = new KDJ(50, 50);
			}
			// ====================================================================

			// 求K的系数,四舍五入，保留4位小数
			Double kRatioA = DoubleUtils.divide(1,this.kCycle, 4);
			Double kRatioB = 1 -kRatioA;

			// 计算K值:当日K值=2/3×前一日K值＋1/3×当日RSV
			Double kValue =  kRatioB * prevKdj.getK() + kRatioA * rsvValue;

			// 求D的系数,四舍五入，保留4位小数
			Double dRatioA = DoubleUtils.divide(1,this.dCycle, 4);
			Double dRatioB = 1-dRatioA;

			// 计算D值：当日D值=2/3×前一日D值＋1/3×当日K值
			Double dValue = dRatioB *prevKdj.getD() + dRatioA * kValue;

			// ====================================================================
			// 当日J值=3*当日K值-2*当日D值
			Double jValue = 3* kValue -2*dValue;
					

			// ====================================================================
			Double headK = DoubleUtils.setScale(kValue,2);
			Double headD = DoubleUtils.setScale(dValue,2);
			Double headJ = DoubleUtils.setScale(jValue,2);
			
			return new KDJ(headK, headD, headJ);
		}

	}

}
