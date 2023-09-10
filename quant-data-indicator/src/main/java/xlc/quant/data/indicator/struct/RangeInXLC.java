package xlc.quant.data.indicator.struct;

import java.util.Comparator;
import java.util.Objects;
import java.util.function.Function;

import com.google.common.collect.Range;

/**
 * 范围-包装类，拓展 Google Guava 库中{@link Range}
 * @author Rootfive
 * @param <C>
 */
public class RangeInXLC<C extends Comparable<?>> {
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private enum ComparableComparator implements Comparator {
		DEFAULT_COMPARATOR;

		/**
		 * Comparable based compare implementation.
		 *
		 * @param obj1 left-hand side of comparison
		 * @param obj2 right-hand side of comparison
		 * @return negative, 0, positive comparison value
		 */
		@Override
		public int compare(final Object obj1, final Object obj2) {
			return ((Comparable) obj1).compareTo(obj2);
		}
		
	}
	
	

	/** 范围对象 */
	private final Range<C> range;

	/** 是不是-左开区间 */
	private final boolean isLeftOpen;

	/** 是不是-右开区间  */
	private final boolean isRightOpen;

	/**
	 * The ordering scheme used in this range.
	 */
	private final Comparator<C> comparator;



	@SuppressWarnings("unchecked")
	private RangeInXLC(final Range<C> range, final boolean isLeftOpen, final boolean isRightOpen,final Comparator<C> comp) {
			
		this.range = range;
		this.isLeftOpen = isLeftOpen;
		this.isRightOpen = isRightOpen;
		if (comp == null) {
			this.comparator = ComparableComparator.DEFAULT_COMPARATOR;
		} else {
			this.comparator = comp;
		}
	}

	/**
	 * 全开-区间： ( lower..upper )
	 * @param <C>
	 * @param lower
	 * @param upper
	 */
	public static <C extends Comparable<?>> RangeInXLC<C> open(final C lower, final C upper) {
		return new RangeInXLC<>(Range.open(lower, upper), true, true,null);
	}

	/**
	 * 全闭-区间： [ lower..upper ]
	 * @param <C>
	 * @param lower
	 * @param upper
	 * @return
	 */
	public static <C extends Comparable<?>> RangeInXLC<C> closed(final C lower, final C upper) {
		return new RangeInXLC<>(Range.closed(lower, upper), false, false,null);
	}

	/**
	 * 左开右闭-区间： ( lower..upper ]
	 * @param <C>
	 * @param lower
	 * @param upper
	 * @return
	 */
	public static <C extends Comparable<?>> RangeInXLC<C> openClosed(final C lower, final C upper) {
		return new RangeInXLC<>(Range.openClosed(lower, upper), true, false,null);
	}

	/**
	 * 左闭右开-区间： [ alower..upper )
	 * @param <C>
	 * @param lower
	 * @param upper
	 * @return
	 */
	public static <C extends Comparable<?>> RangeInXLC<C> closedOpen(final C lower, final C upper) {
		return new RangeInXLC<>(Range.closedOpen(lower, upper), false, true,null);
	}

	/**
	 * 全开-区间-最大值： ( -∞..endpoint )
	 * @param <C>
	 * @param endpoint
	 * @return
	 */
	public static <C extends Comparable<?>> RangeInXLC<C> lessThan(C endpoint) {
		return new RangeInXLC<>(Range.lessThan(endpoint), true, true,null);
	}

	/**
	 * 左开右闭-区间-最大值： ( -∞..endpoint ]
	 * @param <C>
	 * @param endpoint
	 * @return
	 */
	public static <C extends Comparable<?>> RangeInXLC<C> atMost(C endpoint) {
		return new RangeInXLC<>(Range.atMost(endpoint), true, false,null);
	}

	/**
	 * 全开-区间-最小值： ( endpoint..+∞ )
	 * @param <C>
	 * @param endpoint
	 * @return
	 */
	public static <C extends Comparable<?>> RangeInXLC<C> greaterThan(C endpoint) {
		return new RangeInXLC<>(Range.greaterThan(endpoint), true, true,null);
	}

	/**
	 * 左闭右开-区间-最小值： [ endpoint..+∞ )
	 * @param <C>
	 * @param endpoint
	 * @return
	 */
	public static <C extends Comparable<?>> RangeInXLC<C> atLeast(C endpoint) {
		return new RangeInXLC<>(Range.atLeast(endpoint), false, true,null);
	}

	/**
	 * 函数式接口委托方法，委托执行被包装类Range中的方法
	 * @param <R>
	 * @param function
	 * @return
	 */
	public <R> R applyRange(Function<Range<C>, R> function) {
		return function.apply(range);
	}

	
	
	@Override
	public String toString() {
		return range.toString();
	}

	/**
	 * 是否包含
	 * @param value
	 * @return
	 */
	public boolean isContains(C value) {
		return range.contains(value);
	}

	/**
	 * 是否在范围内
	 * @param value
	 * @return
	 */
	public boolean isInRange(C value) {
		return range.contains(value);
	}

	/**
	 * 是否有最小值
	 * @return
	 */
	public boolean hasLowerBound() {
		return range.hasLowerBound();
	}

	/**
	 * 最小值
	 * @return
	 */
	public C lowerEndpoint() {
		return range.lowerEndpoint();
	}

	/**
	 * 是否有最大值
	 * @return
	 */
	public boolean hasUpperBound() {
		return range.hasUpperBound();
	}

	/**
	 * 最大值
	 * @return
	 */
	public C upperEndpoint() {
		return range.upperEndpoint();
	}

	/**
	 * 判断  区间 是否在 比较值 的后面
	 * @param value
	 * @return
	 */
	public boolean isAfter(final C value) {
		Objects.requireNonNull(value);
		if (!range.hasLowerBound()) {
			return false;
		}
		C lowerEndpoint = range.lowerEndpoint();
		if (isLeftOpen) {
			return comparator.compare(value,lowerEndpoint) <= 0;
		} else {
			return comparator.compare(value,lowerEndpoint) < 0;
		}
	}
	
	public boolean gt(final C value) {
		return isAfter(value);
	}
	
	/**
	 * 判断  区间 是否在 比较值 的前面
	 * @param value
	 * @return
	 */
	public boolean isBefore(final C value) {
		Objects.requireNonNull(value);
		if (!range.hasUpperBound()) {
			return false;
		}
		
		C upperEndpoint = range.upperEndpoint();
		if (isRightOpen) {
			return comparator.compare(value,upperEndpoint) >= 0;
		} else {
			return comparator.compare(value,upperEndpoint) > 0;
		}
	}
	
	/**
	 * 判断 区间 的 最大值 小于比较值
	 * @param value
	 * @return
	 */
	public boolean lt(final C value) {
		return isBefore(value);
	}

	// 其他方法...
}
