package xlc.quant.data.indicator.calculator;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import xlc.quant.data.indicator.Indicator;
import xlc.quant.data.indicator.IndicatorCalculator;
import xlc.quant.data.indicator.IndicatorCalculatorCallback;
import xlc.quant.data.indicator.util.DoubleUtils;

/**
 * 计算器
 * @author Rootfive
 * 百度百科：https://baike.baidu.com/item/KDJ%E6%8C%87%E6%A0%87
 * 
 * 移动平均线，英文名称为MovingAverage，简称MA，原本意思是移动平均。由于我们将其制作成线形，所以一般称为移动平均线，简称均线。
 * 均线是将某一段吋间的收盘价之和除以该周期，比如日线MA5指5天内的收盘价除以5,
 * 其计算公式为： MA(5)=(C1+C2+C3十C4+C5)/5
 * 其中：
 *    Cn为第n日收盘价。例如C1，则为第1日收盘价。
 *
 *    用EMA追底，用MA识顶。 例如，用20天EMA判断底部，用20天MA判断顶部。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class MA extends Indicator {

	/** MA计算值  */
	private Double value;

	
	//=============
	//内部类分隔符 XXX
	//=============
	/**
	 * 构建-计算器
	 * @param capacity
	 * @return
	 */
	public static IndicatorCalculator<MA> buildCalculator(int capacity) {
		return new MACalculator(capacity);
	}

	/**
	 * 内部类实现MA计算器
	 * @author Rootfive
	 */
	private static class MACalculator extends IndicatorCalculator<MA> {

		/**
		 * @param capacity
		 */
		MACalculator(int capacity) {
			super(capacity, true);
		}

		@Override
		protected MA executeCalculate() {
			Double maValue = null;
			if (isFullCapacity()) {
				double closeSumValue = super.getCalculatorDataList().stream().mapToDouble(IndicatorCalculatorCallback::getClose).sum();
				maValue = DoubleUtils.divide(closeSumValue, fwcPeriod, 2);
				return new MA(maValue);
			}
			return null;
		}

	}

}
