package xlc.quant.data.indicator;

/**
 * @author Rootfive
 * 将时间划分为固定大小的窗口（年、月、日、时、分），统计每个窗口内的请求行情.
 * PS:思路来源于：限流算法-固定窗口算法（Fixed Window Algorithm）
 * 
 * 时间窗口的 终点是收盘时间，时间可以是【时间戳long】 或者是LocalDateTime、LocalDate、LocalTime
 */
public interface CircularFixedWindowCalculable<C extends Comparable<? super C>> {

	/**
	 * @return 收盘时间 
	 */
	C getCloseTime();
	/**
	 * @param closeTime 收盘时间  
	 */
	void setCloseTime(C closeTime);
	
	

	/**
	 * @return 交易时间
	 */
	C getTradeTime();
	/**
	 * @param tradeTime 交易时间 
	 */
	void setTradeTime(C tradeTime);

}
