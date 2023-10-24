package xlc.quant.data.indicator;

import java.util.List;

/**
 * 指标计算-管理员
 * @author Rootfive
 * @param <CARRIER> 计算载体，该载体是一个时序数据
 */
public abstract class IndicatorCalculateManager<CARRIER extends IndicatorCalculateCarrier<?>>
		extends TimeSeriesDataRollingWindowManager<CARRIER> {

	/** 
	 * 指标计算器-配置列表
	 * <pre>
	 * 存储基于当前载体的计算器配置，该配置总计三个属性，具体包含：1个指标计算器、1个获取载体中指标委托方法、1个设置载体中指标值的委托方法
	 * </pre> 
	 */
	private final List<IndicatorCalculatorConfig<CARRIER, ?>> calculatorConfigList;

	/**
	 * 构造
	 * @param maxCarrier 载体最大数量
	 * @param calculatorConfigList  指标计算器-配置列表
	 */
	public IndicatorCalculateManager(int maxCarrier, List<IndicatorCalculatorConfig<CARRIER, ?>> calculatorConfigList) {
		super(maxCarrier);
		this.calculatorConfigList = calculatorConfigList;
	}

	/**
	 * 加入管理，并进行指标的批量刷新
	 */
	public synchronized boolean enqueue(CARRIER enter) {
		boolean enqueue = super.enqueue(enter);
		if (!enqueue) {
			return false;
		}
		
		// 循环指标计算器配置列表 每个计算器进行一次指标计算
		for (IndicatorCalculatorConfig<CARRIER, ?> calculatorConfig : calculatorConfigList) {
			calculatorConfig.tryCalculate(enter);
		}
		return true;
	}
}