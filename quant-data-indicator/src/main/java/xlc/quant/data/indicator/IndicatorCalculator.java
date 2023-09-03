package xlc.quant.data.indicator;

import static java.math.BigDecimal.valueOf;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 指标计算 计算器
 * 
 * @author Rootfive
 */
public abstract class IndicatorCalculator<T extends Indicator> {
	
	/** 负整数：-1 */
	public static final BigDecimal MINUS_INT_1 = valueOf(-1);
	
	/** 正整数：2 */
	public static final BigDecimal INT_2 = valueOf(2);
	/** 正整数：3 */
	public static final BigDecimal INT_3 = valueOf(3);
	/**  正整数：50 */
	public static final BigDecimal INT_50 = valueOf(50);
	/** 百:10x10 */
	public static final BigDecimal HUNDRED = valueOf(100);
	
	
	
	/** 环形数组 */
	protected final transient IndicatorCalculatorCarrier<T>[] circularElementData;

	/** 指标的计算周期或时间周期 */
	protected final transient BigDecimal periodCapacity;

	/** 满容计算 */
	private final transient boolean isFullCapacityCalculate;

	/** 是否满容量 */
	protected transient boolean isFullCapacity = false;

	/** 头数据角标 */
	private int headIndex = 0;

	@SuppressWarnings("unchecked")
	public IndicatorCalculator(int period, boolean isFullCapacityCalculate) {
		super();
		this.circularElementData = new IndicatorCalculatorCarrier[period];
		this.periodCapacity = new BigDecimal(period);
		this.isFullCapacityCalculate = isFullCapacityCalculate;
	}

	// ==========XXX===================
	/**
	 * 执行计算，新的数据
	 * @param carrier  指标计算载体(未包含结算结果)
	 * @return 指标计算载体(包含了已经计算的指标结果)
	 */
	public T execute(IndicatorCalculatorCarrier<T> carrier) {
		boolean addResult = addFirst(carrier);
		if (addResult) {
			// 新增成功
			if (!isFullCapacityCalculate || (isFullCapacityCalculate && this.isFullCapacity)) {
				// 1、不是满容计算 [或] 2满容计算且已经满容，二者条件满足其中一种。均可执行计算（指标）
				T indicator = executeCalculate();
				carrier.setIndicator(indicator);
				return indicator;
			}
		}
		return null;
	}
	

	/**
	 * 执行计算，由子类具体某个指标的计算器实现
	 * 
	 * @return
	 */
	protected abstract T executeCalculate();
	
	
	

	/**
	 * 将指定的元素追加到此列表的第一个位置（头插法）。
	 * @param e
	 * @return
	 */
	private synchronized boolean addFirst(IndicatorCalculatorCarrier<T> e) {
		if (e == null) {
			throw new NullPointerException("指标计算 执行器 新增数据不能为:null");
		}

		//取出执行器内的 环形数组 [头数据head] 
		IndicatorCalculatorCarrier<T> head = getHead();
		if (head != null) {
			// [当前数据e]-交易日期时间
			LocalDateTime tradeDateTime = e.getTradeDateTime();
			// [头数据head]-交易日期时间
			LocalDateTime headTradeDateTime = head.getTradeDateTime();

			if (tradeDateTime.isBefore(headTradeDateTime)) {
				// 说明是历史数据，不刷新
				return false;
			} else if (tradeDateTime.isAfter(headTradeDateTime)) {
				// [当前数据e]相较于[头数据head],是一个新的数据，需要刷新 头数据角标
				if (this.headIndex == (this.circularElementData.length - 1)) {
					// 当前[头数据head] 等于 缓冲区长度-1 说明：此时环形数组的已经满了，环形数组中[头数据head]角标指向数组最后一个空位，要想更新新元素，头元素需要指定数组角标为0的位置
					this.headIndex = 0;
				} else {
					//
					++this.headIndex;
				}

				// 此时环形数组 [头数据head]角标已经变化：需要判断[是否满容]
				if (!this.isFullCapacity && (this.circularElementData.length == this.headIndex + 1)) {
					//执行器内的环形数组最初是没有满容的。 环形数组的长度，等于[头数据head]角标+1时，说明 环形数组达到满容状态
					this.isFullCapacity = true;
				}
			}
		}
		//设置头数据-不更新元素和元素的角标
		setHead(e);
		//
		return true;
	}

	/**
	 * 设置头数据-不更新元素和元素的角标
	 * 
	 * @param head
	 */
	private synchronized boolean setHead(IndicatorCalculatorCarrier<T> head) {
		this.circularElementData[this.headIndex] = head;
		return true;
	}


	// ==========XXX===================

	/**
	 * 获取头元素
	 * 
	 * @return
	 */
	public IndicatorCalculatorCarrier<T> getHead() {
		return this.circularElementData[this.headIndex];
	}

	/**
	 * 获取尾元素
	 * 
	 * @return
	 */
	public IndicatorCalculatorCarrier<T> getTail() {
		return this.circularElementData[getTailIndex()];
	}

	/**
	 * 尾元素角标
	 * 
	 * @return int
	 */
	public int getTailIndex() {
		int tailIndex = this.headIndex + 1 - this.circularElementData.length;
		if (tailIndex < 0) {
			tailIndex = tailIndex + this.circularElementData.length;
		}
		return tailIndex;
	}

	/**
	 * 指定角标获取元素
	 * 
	 * @param index，有效范围是：[1,capability]
	 * @return
	 * 
	 *         <pre>
	 * 		注意：index的有效范围是：[1,capability]
	 * 		小于1返回第一个
	 * 		大于capability返回最后
	 *         </pre>
	 */
	public IndicatorCalculatorCarrier<T> get(int index) {
		if (index < 1) {
			return this.circularElementData[this.headIndex];
		} else if (index > this.circularElementData.length) {
			return this.circularElementData[getTailIndex()];
		}

		int needIndex = this.headIndex - (index - 1);
		if (needIndex < 0) {
			needIndex = needIndex + this.circularElementData.length;
		}
		return this.circularElementData[needIndex];
	}

	/**
	 * 前一个数据
	 * 
	 * @return
	 */
	public IndicatorCalculatorCarrier<T> getPrev() {
		return get(2);
	}

	/**
	 * 获取执行器数据-List格式(list只能查询，不能增删)
	 */
	public List<IndicatorCalculatorCarrier<T>> getCalculatorListData() {
		if (isFullCapacity) {
			return Arrays.asList(circularElementData);

		} else {
			return Arrays.stream(circularElementData).filter(e -> e != null).collect(Collectors.toList());
		}
	}

	/**
	 * 是否满容量
	 */
	public boolean isFullCapacity() {
		return this.isFullCapacity;
	}
	

	/**
	 * @param current  当前值
	 * @param prev	   前值	
	 * @param preContinueValue  前连续值
	 * @return
	 */
	public static int getContinueValue(BigDecimal current, BigDecimal prev, Integer preContinueValue) {
		if (current == null || prev == null) {
			return 0;
		}

		if (preContinueValue == null) {
			preContinueValue = 0;
		}

		int compareResult = current.compareTo(prev);
		switch (compareResult) {
		case 1:
			//1,current [>] prev
			if (preContinueValue > 0) {
				//前值 > 0
				return preContinueValue + 1;
			} else if (preContinueValue == 0) {
				//前值 = 0
				return 1;
			} else {
				//前值 < 0
				return 1;
			}
		case 0:
			//0,current [=] prev
			return 0;
		default:
			//-1,current [<] prev
			if (preContinueValue > 0) {
				//前值 > 0
				return -1;
			} else if (preContinueValue == 0) {
				//前值 = 0
				return -1;
			} else {
				//前值 < 0
				return preContinueValue - 1;
			}
		}
	}
	
	
	/**
	 * @param dividend 被除数:除号前面的数
	 * @param divisor  除数:除号后面的数的
	 * @param scale
	 * @return
	 */
	public static BigDecimal divide(BigDecimal dividend, BigDecimal divisor, int scale) {
		return dividend.divide(divisor, scale, RoundingMode.HALF_UP);
	}

	/**
	 * @param dividend 被除数:除号前面的数
	 * @param divisor  除数:除号后面的数的
	 * @return 百分点
	 */
	public static BigDecimal divideByPct(BigDecimal dividend, BigDecimal divisor) {
		return divide(dividend, divisor, 4).multiply(HUNDRED).setScale(2);
	}
	
	/**
	 * 求差额或净额
	 * 
	 * @param minuend    被减数
	 * @param subtrahend 减数
	 * @return 减数是减法算式中从被减数中扣除的数。在减法运算中，例如a-b=c,读作a减b等于c,a称为被减数,b称为减数。
	 */
	public static BigDecimal balance(BigDecimal minuend, BigDecimal subtrahend) {
		if (minuend == null || subtrahend == null) {
			return null;
		}
		return minuend.subtract(subtrahend);
	}

}