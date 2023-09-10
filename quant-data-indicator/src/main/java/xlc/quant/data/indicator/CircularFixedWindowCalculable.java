package xlc.quant.data.indicator;

import java.time.LocalDateTime;

/**
 * @author Rootfive
 * 将时间划分为固定大小的窗口（年、月、日、时、分），统计每个窗口内的请求行情.
 * PS:思路来源于：限流算法-固定窗口算法（Fixed Window Algorithm）
 */
public interface CircularFixedWindowCalculable {

	/**
	 * @return the tradeDateTime
	 */
	LocalDateTime getTradeDateTime();

	/**
	 * @param tradeDateTime the tradeDateTime to set
	 */
	void setTradeDateTime(LocalDateTime tradeDateTime);

}
