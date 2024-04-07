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
	Double getOpen();
	
	/**
	 * @return 最低价
	 */
	Double getLow();
	/**
	 * @return 最高价
	 */
	Double getHigh();
	
	/**
	 * @return 收盘价(当前K线未结束的即为最新交易价)
	 */
	Double getClose();
	
	/**
	 * @return 成交量（个/份/股）
	 */
	Double getVolume();
	
	/**
	 * @return 成交额
	 */
	Double getAmount();
}
