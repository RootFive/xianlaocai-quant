package xlc.quant.data.indicator;

/**
 * 指标计算-载体，该载体是一个时序数据
 * @author Rootfive
 * @param <TIME> 泛型，表示时间的类型，目前支持以下类型：
 *<pre>
 *	Long			时间戳
 *	Instant			时间戳
 *	LocalDate		只包含日期，比如：2020-05-20
 *	LocalTime		只包含时间，比如：13:14:00
 *	LocalDateTime	包含日期和时间，比如：2020-05-20 13:14:00
 *	ZonedDateTime	带时区的时间
 *	Date 			日期时间
 *</pre>
 *注意：指标计算载体数据，如果是股市数据。请一定要使用复权数据，前复权和后复权均可
 */
public interface IndicatorCalculateCarrier<TIME extends Comparable<? super TIME>> extends TimeSeriesData<TIME>{
	
	/**
	 * @return 交易代码
	 */
	String getSymbol();
	
	/**
	 * @return 开盘价
	 */
	double getOpen();
	/**
	 * @return 最低价
	 */
	double getLow();
	/**
	 * @return 最高价
	 */
	double getHigh();
	
	/**
	 * @return 收盘价(当前K线未结束的即为最新交易价)
	 */
	double getClose();
	
	/**
	 * @return 成交量（个/份/股）
	 */
	double getVolume();
	
	/**
	 * @return 成交额
	 */
	double getAmount();
	
	
	// =======================XXX 
	// 上面的属性值，一般情况下，分时和日行情都有
	// 下面的属性值，一般情况下，分时和日行情可能有，即便是没有，也可以通过上面的属性计算得出
	// =======================XXX
	
	/**
	 * @return 前收价格，上一个交易时序数据统计的收盘价格
	 */
	double getPreClose();

	/**
	 * @return 涨跌额-基于前收价格，计算公式：【涨跌额 = 收盘价-前收价】
	 */
	double getPriceChange();
	
	/**
	 * @return 涨跌幅（百分点）-基于前收价格，计算公式：【涨跌幅 = （涨跌额/前收价）x 100 】
	 */
	double getPctChange();

	/**
	 * @return 价格震幅（百分点）-基于前收价格，计算公式：【涨跌幅 = （最高价-最低价/前收价）x 100 】
	 */
	double getAmplitude();

}
