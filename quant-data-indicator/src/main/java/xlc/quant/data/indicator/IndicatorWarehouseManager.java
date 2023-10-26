package xlc.quant.data.indicator;

import java.util.List;

/**
 * 指标-仓库管理员
 * @author Rootfive
 * @param <CARRIER> 计算载体，该载体是一个时序数据
 */
public class IndicatorWarehouseManager<CARRIER extends IndicatorCalculateCarrier<?>> extends TimeSeriesDataRollingWindowQueue<CARRIER> {

	/** 
	 * 指标计算器列表
	 * <pre>
	 * 存储基于当前载体的计算器配置，该配置总计三个属性，具体包含：1个指标计算器、1个获取载体中指标委托方法、1个设置载体中指标值的委托方法
	 * </pre> 
	 */
	private final List<IndicatorCalculator<CARRIER, ?>> indicatorCalculatorList;

	/**
	 * 构造
	 * @param maximum 管理载体的最大数量
	 * @param calculatorConfigList  指标计算器-配置列表
	 */
	public IndicatorWarehouseManager(int maximum, List<IndicatorCalculator<CARRIER, ?>> indicatorCalculatorList) {
		super(maximum);
		this.indicatorCalculatorList = indicatorCalculatorList;
	}

	/**
	 * 接受数据，并进行指标的批量刷新
	 */
	public synchronized boolean accept(CARRIER enter) {
		boolean enqueue = super.enqueue(enter);
		if (!enqueue) {
			return false;
		}
		
		// 循环指标计算器配置列表 每个计算器进行一次指标计算
		for (IndicatorCalculator<CARRIER, ?> indicatorCalculator : indicatorCalculatorList) {
			indicatorCalculator.input(enter);
		}
		return true;
	}
}