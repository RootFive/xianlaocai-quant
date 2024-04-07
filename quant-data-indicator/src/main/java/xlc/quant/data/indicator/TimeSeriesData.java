package xlc.quant.data.indicator;

/**
 * 时序数据
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
 *
 * <p>
 * 这种嵌套泛型<TIME extends Comparable<? super TIME>>可以被描述为一个具有两层限制的泛型声明。
 *     1、首先，TIME是一个泛型类型参数，它被限制为实现了Comparable接口的类型。这意味着传入的类型必须具有比较能力，可以进行比较操作。
 *     2、其次，Comparable<? super TIME>表示对Comparable接口的类型参数进行了进一步的限制。
 * 这里使用了下界通配符<? super TIME>，表示传入的类型可以是TIME的超类，或者就是TIME本身。这种嵌套泛型的目的是为了提供更灵活的类型约束。它要求传入的类型必须实现了Comparable接口，并且可以是TIME的超类，以便在使用比较操作时保证类型的安全性。
 *</pre>
 */
public interface TimeSeriesData<TIME extends Comparable<? super TIME>> {
	/**
	 * @return 时序数据-结束时间，即统计时间时间分割点
	 */
	TIME getCloseTime();

	/**
	 * @return 时序数据-时间戳
	 * <pre>
	 * 时间戳存在的意义在于刷新 当前时序数据（一半都是指当前K线）
	 * 如果是历史数据，这个时间戳是等于closeTime;
	 * 如果是实时数据，这个时间就是实时时间。
	 * </pre>
	 */
	TIME getTimestamp();

}
