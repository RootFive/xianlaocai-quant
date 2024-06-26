package xlc.quant.data.indicator.calculator;

import java.util.function.BiConsumer;
import java.util.function.Function;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import xlc.quant.data.indicator.Indicator;
import xlc.quant.data.indicator.IndicatorCalculateCarrier;
import xlc.quant.data.indicator.IndicatorCalculator;
import xlc.quant.data.indicator.util.DoubleUtils;

/**
 * 中文名 RSI指标    是相对强弱指标
 * @author Rootfive
 * <pre>
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
 * </pre>
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class RSI extends Indicator {

	/** RSI值 */
	private Double value;

	private Double emaUp;

	private Double emaDown;

	public RSI(Double value, Double emaUp, Double emaDown) {
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
	public static <CARRIER extends IndicatorCalculateCarrier<?>> IndicatorCalculator<CARRIER, RSI> buildCalculator(int capacity,BiConsumer<CARRIER, RSI> propertySetter,Function<CARRIER, RSI> propertyGetter) {
		return new RSICalculator<>(capacity, propertySetter, propertyGetter);
	}

	/**
	 * 计算器
	 * @author Rootfive
	 */
	private static class RSICalculator<CARRIER extends IndicatorCalculateCarrier<?>> extends IndicatorCalculator<CARRIER, RSI> {

		private final double α;
		private final double β;
		private final Function<CARRIER, RSI> propertyGetter;

		/**
		 * @param capacity
		 */
		public RSICalculator(int capacity,BiConsumer<CARRIER, RSI> propertySetter,Function<CARRIER, RSI> propertyGetter) {
			super(capacity, true,propertySetter);
			this.α = DoubleUtils.divide(capacity - 1, capacity, DoubleUtils.MAX_SCALE);
			this.β = 1 - α;
			this.propertyGetter = propertyGetter;
		}

		@Override
		protected RSI executeCalculate() {

			Double emaUp = null;
			Double emaDown = null;

			RSI prevRSI = propertyGetter.apply(getPrev());
			if (prevRSI == null) {
				// 涨额之和
				double sumUp = DoubleUtils.ZERO;
				// 跌额之和
				double sumDown = DoubleUtils.ZERO;
				
				for (int i = 0; i < capacity(); i++) {
					CARRIER carrier_i = get(i);
					
					Double preClose = carrier_i.getOpen();
					if (i > 0) {
						preClose = get(i-1).getClose();
					}
					Double changePrice = carrier_i.getClose()-preClose;
					// 涨幅之和累加
					if (changePrice > DoubleUtils.ZERO) {
						sumUp = sumUp + Math.abs(changePrice);
					} else if (changePrice < DoubleUtils.ZERO) {
						sumDown = sumDown + Math.abs(changePrice);
					}
				}

				emaUp = DoubleUtils.divide(sumUp, capacity(), DoubleUtils.MAX_SCALE);
				emaDown = DoubleUtils.divide(sumDown, capacity(), DoubleUtils.MAX_SCALE);
			} else {
				Double prevEmaUp = prevRSI.getEmaUp();
				Double prevEmaDown = prevRSI.getEmaDown();

				CARRIER head = getHead();
				CARRIER prev = getPrev();
				Double changePrice = head.getClose()-prev.getClose();

				// 涨幅之和累加
				int compareTo = changePrice.compareTo(DoubleUtils.ZERO);
				if (compareTo > 0) {
					emaUp = prevEmaUp * α + changePrice * β;
					emaDown = prevEmaDown * α + DoubleUtils.ZERO * β;
				}

				if (compareTo < 0) {
					emaUp = prevEmaUp * α + DoubleUtils.ZERO * β;
					emaDown = prevEmaDown * α + Math.abs(changePrice) * β;
				}

				if (compareTo == 0) {
					emaUp = prevEmaUp * α + DoubleUtils.ZERO * β;
					emaDown = prevEmaDown * α + DoubleUtils.ZERO * β;
				}
			}

			Double sumEmaChanges = emaUp + emaDown;
			Double rsiValue = null;

			if (sumEmaChanges.compareTo(DoubleUtils.ZERO) != 0) {
				rsiValue = DoubleUtils.divideByPct(emaUp, sumEmaChanges);
			}
			
			return new RSI(rsiValue, emaUp, emaDown);
		}

	}

}
