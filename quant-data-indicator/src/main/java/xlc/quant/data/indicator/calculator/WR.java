package xlc.quant.data.indicator.calculator;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Comparator;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import xlc.quant.data.indicator.Indicator;
import xlc.quant.data.indicator.IndicatorCalculator;
import xlc.quant.data.indicator.IndicatorCalculatorCarrier;

/**
 * 威廉指标
 * @author Rootfive
 * 威廉指标（Williams %R或简称W%R）
 * 百度百科：https://baike.baidu.com/item/%E5%A8%81%E5%BB%89%E6%8C%87%E6%A0%87
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class WR extends Indicator {

	private BigDecimal value;

	
	//=============
	//内部类分隔符 XXX
	//=============
	/**
	 * 构建-计算器
	 * @param capacity
	 * @return
	 */
	public static IndicatorCalculator<WR> buildCalculator(int capacity) {
		return new WRIndicatorCalculateExecutor(capacity);
	}

	/**
	 * 计算器
	 * @author Rootfive
	 */
	private static class WRIndicatorCalculateExecutor extends IndicatorCalculator<WR> {

		/**
		 * @param capacity
		 */
		public WRIndicatorCalculateExecutor(int capacity) {
			super(capacity, true);
		}

		@Override
		protected WR executeCalculate() {
			IndicatorCalculatorCarrier<WR> head = getHead();

			BigDecimal headClose = head.getClose();
			BigDecimal maxHigh = null;
			BigDecimal minLow = null;

			maxHigh = Arrays.stream(super.circularElementData).max(Comparator.comparing(IndicatorCalculatorCarrier::getHigh))
					.get().getHigh();
			minLow = Arrays.stream(super.circularElementData).min(Comparator.comparing(IndicatorCalculatorCarrier::getLow))
					.get().getLow();
			// 计算公式：W%R=（Hn—C）÷（Hn—Ln）×100其中
			BigDecimal wrValue = divideByPct(maxHigh.subtract(headClose), maxHigh.subtract(minLow));
			return new WR(wrValue);
		}

	}

}
