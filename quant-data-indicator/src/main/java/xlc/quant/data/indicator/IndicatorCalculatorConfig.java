package xlc.quant.data.indicator;

import java.util.function.BiConsumer;
import java.util.function.Function;

import lombok.Data;

/**
 * 指标计算器-配置
 * @author Rootfive
 * @param <CARRIER>  泛型，指标计算载体
 * @param <INDI>	 泛型，计算指标
 */
@Data
public class IndicatorCalculatorConfig<CARRIER extends IndicatorCalculateCarrier<?>, INDI> {

	/** 指标计算器 */
	IndicatorCalculator<CARRIER, INDI> indicatorCalculator;

	/** 委托方法：获取载体中的指标 */
	Function<CARRIER, INDI> propertyGetter;

	/** 委托方法：设置载体中指标值 */
	BiConsumer<CARRIER, INDI> propertySetter;

	/**
	 * @param indicatorCalculator 指标计算器
	 * @param propertyGetter  委托方法：获取载体中的指标 
	 * @param propertySetter  委托方法：设置载体中指标值
	 */
	public IndicatorCalculatorConfig(
			IndicatorCalculator<CARRIER, INDI> indicatorCalculator,
			Function<CARRIER, INDI> propertyGetter, 
			BiConsumer<CARRIER, INDI> propertySetter) {
		super();
		this.indicatorCalculator = indicatorCalculator;
		this.propertyGetter = propertyGetter;
		this.propertySetter = propertySetter;
	}
	
	/**
	 * 按照指标计算器配置，使用计算器进行一次指标计算
	 * @param enter  计算载体
	 */
	public INDI tryCalculate(CARRIER enter) {
		return indicatorCalculator.input(enter, propertyGetter, propertySetter);
	}
	
	
	
}