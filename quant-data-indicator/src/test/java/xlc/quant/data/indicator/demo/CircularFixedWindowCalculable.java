package xlc.quant.data.indicator.demo;

/**
 * @author Rootfive
 * 将时间划分为固定大小的窗口（年、月、日、时、分），统计每个窗口内的请求行情.
 * PS:思路来源于：限流算法-固定窗口算法（Fixed Window Algorithm）
 * 
 * 时间窗口的起点就是 开盘时间 终点是收盘时间
 */
public interface CircularFixedWindowCalculable<C extends Comparable<? super C>> {

	/**
	 * @return 开盘时间
	 */
	C getOpenTime();

	/**
	 * @param openTime 开盘时间
	 */
	void setOpenTime(C openTime);

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

	/**
	 * @return 交易代码
	 */
	String getSymbol();

	/**
	 * @param symbol 交易代码
	 */
	void setSymbol(String symbol);

	/**
	 * @return 开盘价
	 */
	double getOpen();

	/**
	 * @param open 开盘价
	 */
	void setOpen(double open);

	/**
	 * @return 最低价
	 */
	double getLow();

	/**
	 * @param low 最低价
	 */
	void setLow(double low);

	/**
	 * @return 最高价
	 */
	double getHigh();

	/**
	 * @param high 最高价
	 */
	void setHigh(double high);

	/**
	 * @return 收盘价(当前K线未结束的即为最新交易价)
	 */
	double getClose();

	/**
	 * @param close 收盘价(当前K线未结束的即为最新交易价)
	 */
	void setClose(double close);

	/**
	 * @return 成交量
	 */
	long getVolume();

	/**
	 * @param volume 成交量
	 */
	void setVolume(long volume);

	/**
	 * @return 成交额
	 */
	double getAmount();

	/**
	 * @param amount 成交额 
	 */
	void setAmount(double amount);

	// =======================
	// 上面的属性值，一般情况下，分时和日行情都有
	// 下面的属性值，一般情况下，分时和日行情可能有，即便是没有，也可以通过上面的属性计算得出
	// =======================
	/**
	 * @return 前收价格
	 */
	Double getPreClose();

	/**
	 * @param preClose 前收价格
	 */
	void setPreClose(Double preClose);

	/**
	 * @return 前收 涨跌额
	 */
	Double getPriceChange();

	/**
	 * @param priceChange 前收 涨跌额
	 */
	void setPriceChange(Double priceChange);

	/**
	 * @return 涨跌幅（百分点）
	 */
	Double getPctChange();

	/**
	 * @param pctChange 涨跌幅（百分点）
	 */
	void setPctChange(Double pctChange);

	/**
	 * @return 价格震幅（百分点）
	 */
	Double getAmplitude();

	/**
	 * @param amplitude 价格震幅（百分点）
	 */
	void setAmplitude(Double amplitude);

}
