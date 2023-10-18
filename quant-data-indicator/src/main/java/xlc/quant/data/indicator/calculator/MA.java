package xlc.quant.data.indicator.calculator;

import java.util.function.Consumer;
import java.util.function.Function;

import lombok.Data;
import lombok.EqualsAndHashCode;
import xlc.quant.data.indicator.Indicator;
import xlc.quant.data.indicator.IndicatorCalculator;
import xlc.quant.data.indicator.IndicatorComputeCarrier;
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
@EqualsAndHashCode(callSuper = true)
public class MA extends Indicator {
	
	//=============
	//内部类分隔符 XXX
	//=============
	/**
	 * 构建-计算器
	 * @param capacity
	 * @param indicatorSetScale        指标精度
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static <C extends IndicatorComputeCarrier>  IndicatorCalculator<C, Double> buildCalculator(int capacity,int indicatorSetScale) {
		return new MACalculator<>(capacity,indicatorSetScale);
	}

	/**
	 * 内部类实现MA计算器
	 * @author Rootfive
	 */
	private static class MACalculator<C extends IndicatorComputeCarrier<?>>  extends IndicatorCalculator<C, Double> {
		/** 指标精度 */
		private final int indicatorSetScale;
		
		/**
		 * @param capacity
		 */
		MACalculator(int capacity,int indicatorSetScale) {
			super(capacity, true);
			this.indicatorSetScale =  indicatorSetScale;
		}

		@Override
		protected Double executeCalculate(Function<C, Double> propertyGetter,Consumer<Double> propertySetter) {
			double closeSumValue = DoubleUtils.ZERO;
			for (int i = 0; i < circularData.length; i++) {
				closeSumValue = closeSumValue+ getPrevByNum(i).getClose();
			}
			
			double maValue = DoubleUtils.divide(closeSumValue, fwcPeriod, indicatorSetScale);
			//设置计算结果
			propertySetter.accept(maValue);
			return maValue;
		}

	}

}
