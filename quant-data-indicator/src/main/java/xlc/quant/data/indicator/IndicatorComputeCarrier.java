package xlc.quant.data.indicator;

/**
 * 指标计算载体
 * 注意：下面的行情数据，如果是A股。请一定要使用复权数据，前复权和后复权均可
 */
public interface IndicatorComputeCarrier<C extends Comparable<? super C>> extends CircularFixedWindowCalculable<C>{
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
	double getVolume();
	/**
	 * @param volume 成交量
	 */
	void setVolume(double volume);

	
	/**
	 * @return 成交额
	 */
	double getAmount();
	/**
	 * @param amount 成交额 
	 */
	void setAmount(double amount);
	
	
	// =======================XXX 
	// 上面的属性值，一般情况下，分时和日行情都有
	// 下面的属性值，一般情况下，分时和日行情可能有，即便是没有，也可以通过上面的属性计算得出
	// =======================XXX
	
	/**
	 * @return 前收价格
	 */
	double getPreClose();
	/**
	 * @param preClose 前收价格
	 */
	void setPreClose(double preClose);
	
	

	/**
	 * @return 前收 涨跌额
	 */
	double getPriceChange();
	/**
	 * @param priceChange 前收 涨跌额
	 */
	void setPriceChange(double priceChange);

	
	/**
	 * @return 涨跌幅（百分点）
	 */
	double getPctChange();
	/**
	 * @param pctChange 涨跌幅（百分点）
	 */
	void setPctChange(double pctChange);
	

	/**
	 * @return 价格震幅（百分点）
	 */
	double getAmplitude();
	/**
	 * @param amplitude 价格震幅（百分点）
	 */
	void setAmplitude(double amplitude);

}
