package xlc.quant.data.indicator.calculator;

import java.math.BigDecimal;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import xlc.quant.data.indicator.Indicator;
import xlc.quant.data.indicator.IndicatorCalculator;
import xlc.quant.data.indicator.IndicatorCalculatorCallback;


/**
 * @author Rootfive
 * 百度百科：https://baike.baidu.com/item/%E7%9B%B8%E5%AF%B9%E5%BC%BA%E5%BC%B1%E6%8C%87%E6%A0%87
 * 
 * 中文名 RSI指标    是相对强弱指标
 * 
 * 相对强弱指标RSI是用以计测市场供需关系和买卖力道的方法及指标。
 * 计算公式：
 * N日RSI =A/（A+B）×100
 * A=N日内收盘涨额之和
 * B=N日内收盘跌额之和（取正值）
 * 由上面算式可知RSI指标的技术含义，即以向上的力量与向下的力量进行比较，若向上的力量较大，则计算出来的指标上升；若向下的力量较大，则指标下降，由此测算出市场走势的强弱。
 * 以日为计算周期为例，计算RSI值一般是以5日、10日、14日为一周期。
 * 另外也有以6日、12日、24日为计算周期。
 * 一般而言，若采用的周期的日数短，RSI指标反应可能比较敏感；日数较长，可能反应迟钝。
 * ===============================================================
 * RSI的变动范围在0—100之间，强弱指标值一般分布在20—80。
 * 80-100 极强 卖出
 * 50-80 强 买入
 * 20-50 弱 观望
 * 0-20 极弱 买入
 * 这里的“极强”、“强”、“弱”、“极弱”只是一个相对的分析概念。
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class RSI extends Indicator {

	/** RSI值 */
	private BigDecimal value;

	private BigDecimal emaUp;

	private BigDecimal emaDown;

	public RSI(BigDecimal value, BigDecimal emaUp, BigDecimal emaDown) {
		super();
		this.value = value;
		this.emaUp = emaUp;
		this.emaDown = emaDown;
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
	public static IndicatorCalculator<RSI> buildCalculator(int capacity) {
		return new RSICalculator(capacity);
	}

	/**
	 * 计算器
	 * @author Rootfive
	 */
	private static class RSICalculator extends IndicatorCalculator<RSI> {

		private final BigDecimal α;
		private final BigDecimal β;

		/**
		 * @param capacity
		 */
		public RSICalculator(int capacity) {
			super(capacity, true);
			this.α = divide(new BigDecimal(capacity - 1), new BigDecimal(capacity), 4);
			this.β = BigDecimal.ONE.subtract(α);
		}

		@Override
		protected RSI executeCalculate() {

			BigDecimal emaUp = null;
			BigDecimal emaDown = null;

			RSI prevRSI = getPrev().getIndicator();
			if (prevRSI == null) {
				// 涨额之和
				BigDecimal sumUp = BigDecimal.ZERO;
				// 跌额之和
				BigDecimal sumDown = BigDecimal.ZERO;
				for (IndicatorCalculatorCallback<RSI> calculate : super.getCalculatorListData()) {
					BigDecimal changePrice = calculate.getPriceChange();
					if (changePrice == null) {
						// 第一根K线可能没有统计到涨跌幅，跳过
						continue;
					}

					// 涨幅之和累加
					
					if (changePrice.compareTo(BigDecimal.ZERO) > 0) {
						sumUp = sumUp.add(changePrice);
					} else if (changePrice.compareTo(BigDecimal.ZERO) < 0) {
						sumDown = sumDown.subtract(changePrice);
					}
				}

				emaUp = divide(sumUp, fwcPeriod, 4);
				emaDown = divide(sumDown, fwcPeriod, 4);
				;
			} else {
				BigDecimal prevEmaUp = prevRSI.getEmaUp();
				BigDecimal prevEmaDown = prevRSI.getEmaDown();

				IndicatorCalculatorCallback<RSI> head = getHead();
				BigDecimal changePrice = head.getPriceChange();

				// 涨幅之和累加
				int compareTo = changePrice.compareTo(BigDecimal.ZERO);
				if (compareTo > 0) {
					emaUp = (prevEmaUp.multiply(α)).add((changePrice.multiply(β)));
					emaDown = (prevEmaDown.multiply(α)).add((BigDecimal.ZERO.multiply(β)));
				}

				if (compareTo < 0) {
					emaUp = (prevEmaUp.multiply(α)).add((BigDecimal.ZERO.multiply(β)));

					BigDecimal changesNegative = changePrice.multiply(MINUS_INT_1);
					emaDown = (prevEmaDown.multiply(α)).add((changesNegative.multiply(β)));
				}

				if (compareTo == 0) {
					emaUp = (prevEmaUp.multiply(α)).add((BigDecimal.ZERO.multiply(β)));
					emaDown = (prevEmaDown.multiply(α)).add((BigDecimal.ZERO.multiply(β)));
				}
			}

			BigDecimal sumEmaChanges = emaUp.add(emaDown);
			BigDecimal rsi = null;
			
			if (sumEmaChanges.compareTo(BigDecimal.ZERO) !=0) {
				rsi = divideByPct(emaUp, sumEmaChanges);
			}
			return new RSI(rsi, emaUp, emaDown);
		}

	}

}
