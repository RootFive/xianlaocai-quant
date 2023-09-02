package xlc.quant.data.indicator;

import static java.math.BigDecimal.valueOf;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 抽象指标
 * @author Rootfive
 */
public abstract class Indicator {

	//=============
	//内部类分隔符 XXX
	//=============
	/**
	 * 指标计算 执行器
	 * 
	 * @author GuoHonglin
	 */
	public static abstract class IndicatorCalculator<T extends Indicator> {
		
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
		
		
		
		/** 环形数组元素值 */
		protected final transient IndicatorCarrier<T>[] circularArrayElementData;

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
			this.circularArrayElementData = new IndicatorCarrier[period];
			this.periodCapacity = new BigDecimal(period);
			this.isFullCapacityCalculate = isFullCapacityCalculate;
		}

		// ==========XXX===================
		/**
		 * @param e
		 * @return
		 */
		public T execute(IndicatorCarrier<T> e) {
			boolean addResult = addFirst(e);
			if (addResult) {
				// 新增成功
				if (!isFullCapacityCalculate || (isFullCapacityCalculate && this.isFullCapacity)) {
					// 1、不是满容计算 [或] 2满容计算且已经满容，二者条件满足其中一种。均可执行计算（指标）
					T indicator = executeCalculate();
					e.setIndicator(indicator);
					return indicator;
				}
			}
			return null;
		}

		/**
		 * 将指定的元素追加到此列表的第一个位置（头插法）。
		 * @param e
		 * @return
		 */
		private synchronized boolean addFirst(IndicatorCarrier<T> e) {
			if (e == null) {
				throw new NullPointerException("指标计算 执行器 新增数据不能为:null");
			}

			//取出执行器内的 环形数组 [头数据head] 
			IndicatorCarrier<T> head = getHead();
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
					if (this.headIndex == (this.circularArrayElementData.length - 1)) {
						// 当前[头数据head] 等于 缓冲区长度-1 说明：此时环形数组的已经满了，环形数组中[头数据head]角标指向数组最后一个空位，要想更新新元素，头元素需要指定数组角标为0的位置
						this.headIndex = 0;
					} else {
						//
						++this.headIndex;
					}

					// 此时环形数组 [头数据head]角标已经变化：需要判断[是否满容]
					if (!this.isFullCapacity && (this.circularArrayElementData.length == this.headIndex + 1)) {
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
		private synchronized boolean setHead(IndicatorCarrier<T> head) {
			this.circularArrayElementData[this.headIndex] = head;
			return true;
		}

		/**
		 * 执行计算
		 * 
		 * @return
		 */
		protected abstract T executeCalculate();

		// ==========XXX===================

		/**
		 * 获取头元素
		 * 
		 * @return
		 */
		public IndicatorCarrier<T> getHead() {
			return this.circularArrayElementData[this.headIndex];
		}

		/**
		 * 获取尾元素
		 * 
		 * @return
		 */
		public IndicatorCarrier<T> getTail() {
			return this.circularArrayElementData[getTailIndex()];
		}

		/**
		 * 尾元素角标
		 * 
		 * @return int
		 */
		private int getTailIndex() {
			int tailIndex = this.headIndex + 1 - this.circularArrayElementData.length;
			if (tailIndex < 0) {
				tailIndex = tailIndex + this.circularArrayElementData.length;
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
		public IndicatorCarrier<T> get(int index) {
			if (index < 1) {
				return this.circularArrayElementData[this.headIndex];
			} else if (index > this.circularArrayElementData.length) {
				return this.circularArrayElementData[getTailIndex()];
			}

			int needIndex = this.headIndex - (index - 1);
			if (needIndex < 0) {
				needIndex = needIndex + this.circularArrayElementData.length;
			}
			return this.circularArrayElementData[needIndex];
		}

		/**
		 * 前一个数据
		 * 
		 * @return
		 */
		public IndicatorCarrier<T> prev() {
			return get(2);
		}

		/**
		 * 获取执行器数据-List格式(list只能查询，不能增删)
		 */
		public List<IndicatorCarrier<T>> getCalculatorListData() {
			if (isFullCapacity) {
				return Arrays.asList(circularArrayElementData);

			} else {
				return Arrays.stream(circularArrayElementData).filter(e -> e != null).collect(Collectors.toList());
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

}
