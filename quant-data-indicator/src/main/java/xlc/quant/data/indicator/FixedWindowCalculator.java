package xlc.quant.data.indicator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


/**
 * 固定窗口 计算器
 * 
 * @author Rootfive
 * 
 */
public abstract class FixedWindowCalculator<T, FWC extends FixedWindowCalculable> implements Executor<T, FWC> {

	/** 需要计算的数据：指环形固定窗口组成数组中的数据 */
	protected final transient Object [] circularfixedWindowData;

	/** 环形数组最大长度，固定窗口的时间周期 */
	protected final transient BigDecimal fwcPeriod;

	/** 执行总数 */
	protected int executeTotal = 0;

	/** 满容计算：指环形数组满容时才会执行计算 */
	private final transient boolean isFullCapacityCalculate;

	/** 头数据角标：已经插入环形数组的最新的数据数组角标 */
	private int headIndex = 0;

	public FixedWindowCalculator(int fwcPeriod, boolean isFullCapacityCalculate) {
		super();
		this.circularfixedWindowData =  new Object [fwcPeriod];
		this.fwcPeriod = new BigDecimal(fwcPeriod);
		this.isFullCapacityCalculate = isFullCapacityCalculate;
	}

	// ==========XXX===================
	/**
	 * 执行计算，由子类具体某个指标的计算器实现
	 * 
	 * @return
	 */
	protected abstract T executeCalculate();

	// ==========XXX===================

	/**
	 * @param newFwc 新的固定窗口数据
	 * @return
	 */
	@Override
	public synchronized T execute(FWC newFwc) {
		boolean addResult = addFirst(newFwc);
		if (addResult) {
			// 新增成功
			if (!isFullCapacityCalculate || (isFullCapacityCalculate && this.isFullCapacity())) {
				// 1、不是满容计算 [或] 2满容计算且已经满容，二者条件满足其中一种。均可执行计算（指标）
				return executeCalculate();
			}
		}
		return null;
	}

	/**
	 * @param newFwc 新的固定窗口数据
	 * @return 将newFwc追加环形数组的第一个位置（头插法）。
	 */
	private synchronized boolean addFirst(FWC newFwc) {
		if (newFwc == null) {
			throw new NullPointerException("执行器 数据不能为:null");
		}

		// 取出环形数组[head]
		FWC head = getHead();
		if (head != null) {
			// [newFwc]-交易日期时间
			LocalDateTime newFwcTradeDateTime = newFwc.getTradeDateTime();
			// [head]-交易日期时间
			LocalDateTime headTradeDateTime = head.getTradeDateTime();

			if (newFwcTradeDateTime.isBefore(headTradeDateTime)) {
				// 说明是历史数据，不刷新
				return false;
			} else if (newFwcTradeDateTime.isAfter(headTradeDateTime)) {
				++this.executeTotal;
				// 指针移动-循环数组核心
				// [newFwc]相较于[head],是一个新的数据，需要刷新 头数据角标
				if (this.headIndex == (this.circularfixedWindowData.length - 1)) {
					// 当前[head] 等于 缓冲区长度-1
					// 说明：此时环形数组的已经满了，环形数组中[head]角标指向数组最后一个空位，要想更新新元素，头元素需要指定数组角标为0的位置
					this.headIndex = 0;
				} else {
					++this.headIndex;
				}
			}
		} else {
			this.executeTotal = 1;
		}
		// 设置头数据-不更新元素和元素的角标
		this.circularfixedWindowData[this.headIndex] = newFwc;
		//
		return true;
	}

	/**
	 * 尾元素角标
	 * 
	 * @return int
	 */
	private int getTailIndex() {
		int tailIndex = this.headIndex + 1 - this.circularfixedWindowData.length;
		if (tailIndex < 0) {
			tailIndex = tailIndex + this.circularfixedWindowData.length;
		}
		return tailIndex;
	}

	@SuppressWarnings("unchecked")
	FWC getCircularArrayElement(int index) {
        return (FWC) this.circularfixedWindowData[index];
    }
	
	
	/**
	 * 获取头元素
	 * 
	 * @return
	 */
	public FWC getHead() {
		return this.getCircularArrayElement(this.headIndex);
	}

	/**
	 * 获取尾元素
	 * 
	 * @return
	 */
	public FWC getTail() {
		return this.getCircularArrayElement(getTailIndex());
	}

	/**
	 * 指定角标获取元素
	 * 
	 * @param descOrderNum，倒序的执行顺序，有效范围是：[1,circularfixedWindowData.length]
	 * @return
	 * 
	 * <pre>
	 * 	 注意：descOrderNum的有效范围是：[1,circularfixedWindowData.length]
	 * 	 小于1返回第一个
	 * 	 大于circularfixedWindowData.length 返回环形数组中最早插入的数据
	 * </pre>
	 */
	public FWC getByOrderDesc(int descOrderNum) {
		if (descOrderNum < 1) {
			return this.getHead();
		} else if (descOrderNum > this.circularfixedWindowData.length) {
			return this.getTail();
		}

		int needIndex = this.headIndex - (descOrderNum - 1);
		if (needIndex < 0) {
			needIndex = needIndex + this.circularfixedWindowData.length;
		}

		return this.getCircularArrayElement(needIndex);
	}

	/**
	 * 前一个数据
	 * 
	 * @return
	 */
	public FWC getPrev() {
		return getByOrderDesc(2);
	}

	/**
	 * 获取执行器数据-List格式(list按照日期倒叙)
	 * @param limit
	 * @return
	 */
	public List<FWC> getCalculatorListData(int limit ) {
		return getCalculatorListData().stream().sorted(Comparator.comparing(FWC::getTradeDateTime).reversed()).limit(limit).collect(Collectors.toList());
	}
	
	/**
	 * 获取执行器数据-List格式(list按照日期倒叙)
	 */
	@SuppressWarnings("unchecked")
	public List<FWC> getCalculatorListData() {
		List<FWC> arrayList = new ArrayList<>(circularfixedWindowData.length);
		for (Object fixedWindowData : circularfixedWindowData) {
			if (fixedWindowData != null) {
				arrayList.add((FWC)fixedWindowData);
			}
		}
		return arrayList.stream().sorted(Comparator.comparing(FWC::getTradeDateTime).reversed()).collect(Collectors.toList());
	}

	/**
	 * 是否满容量：指环形数组是否满容量
	 */
	public boolean isFullCapacity() {
		return executeTotal >= this.circularfixedWindowData.length ? true : false;
	}

	/**
	 * 环形数组 元素数量
	 * 
	 * @return
	 */
	public int size() {
		return isFullCapacity() ? this.circularfixedWindowData.length : executeTotal;
	}
}