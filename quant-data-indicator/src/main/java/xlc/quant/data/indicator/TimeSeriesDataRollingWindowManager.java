package xlc.quant.data.indicator;

/**
 * 时序数据-滚动窗口-管理员
 * @author Rootfive
 * @param <TSD> 泛型，时序数据
 */
public abstract class TimeSeriesDataRollingWindowManager<TSD extends TimeSeriesData<?>>
		extends TimeSeriesDataRollingWindowQueue<TSD> {

	/**
	 * 构造
	 * @param maximum 管理滚动窗口的最大数量
	 */
	public TimeSeriesDataRollingWindowManager(int maximum) {
		super(maximum);
	}

	/**
	 * 加入管理
	 */
	@Override
	public synchronized boolean enqueue(TSD enter) {
		return super.enqueue(enter);
	}
}
