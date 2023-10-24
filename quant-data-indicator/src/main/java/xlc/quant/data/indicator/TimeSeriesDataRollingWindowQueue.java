package xlc.quant.data.indicator;

/**
 * 时序数据-滚动窗口队列
 * @author Rootfive
 * @param <TSD> 泛型，时序数据
 */
public abstract class TimeSeriesDataRollingWindowQueue<TSD extends TimeSeriesData<?>> {
	/** 滚动窗口队列中存储数据的数组 */
	private final Object[] queueData;
	/** 滚动队列容量，存储数据的最大数量 */
	private final int capacity;
	/** 滚动队列容量中的元素数量 */
	private int size;
	/** 滚动队列的头部索引 */
	private int headIndex;

	/**
	 * @param capacity 滚动队列容量，存储数据的最大数量
	 */
	public TimeSeriesDataRollingWindowQueue(int capacity) {
		this.capacity = capacity;
		this.queueData = new Object[capacity];
		this.size = 0;
		this.headIndex = 0;
	}

	/**
	 * 入队
	 * @param enter
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	synchronized boolean enqueue(TSD enter) {
		if (enter == null) {
			throw new NullPointerException("禁止空（null）数据入队");
		}

		TSD head = getHead();
		if (head != null) {
			//头数据的时间戳
			Comparable headTimestamp = head.getTimestamp();
			//输入数据的时间戳
			Comparable enterTimestamp = enter.getTimestamp();

			//比较两个时间戳
			if (enterTimestamp.compareTo(headTimestamp) <= 0) {
				// 说明是历史数据，不刷新
				return false;
			} else {
				// 说明不是历史数据，再去比较结束时间，判断是否需要更新头部索引
				Comparable headCloseTime = head.getCloseTime();
				Comparable enterCloseTime = enter.getCloseTime();
				if (enterCloseTime.compareTo(headCloseTime) > 0) {
					//说明是一个新的时序数据，需要更新头部索引
					if (size == capacity) {
						// 队列已满，需要进行覆盖写入
						headIndex = (headIndex + 1) % capacity; // 更新头部索引，实现循环
					} else {
						// 队列未满，按照数组角标依次添加
						headIndex = size;
						size++;
					}
				}
			}
		} else {
			this.size++;
		}
		queueData[headIndex] = enter;
		return true;
	}

	/**
	 * 是不是空队列
	 * @return
	 */
	public boolean isEmpty() {
		return size == 0;
	}

	/**
	 * 队列是否满
	 * @return
	 */
	public boolean isFull() {
		return size == capacity;
	}

	/**
	 * 队列大小，当前队列中的元素数量
	 * @return
	 */
	public int size() {
		return size;
	}

	/**
	 * 队列长度，当前队列中的元素数量
	 * @return
	 */
	public int capacity() {
		return capacity;
	}

	/**
	 * 指定索引，滚动窗口队列中存储数据的数组中获取元素
	 * @param arrayDataIndex 数组的角标
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private TSD getRollingDataElement(int arrayDataIndex) {
		return (TSD) this.queueData[arrayDataIndex];
	}

	/**
	 * 指定索引，从队列中获取元素
	 * @param queueIndex 队列的角标
	 * @return
	 */
	public TSD get(int queueIndex) {
		if (queueIndex < 0 || queueIndex >= capacity) {
			throw new IndexOutOfBoundsException("无效索引");
		}
		
		int dataIndex = this.headIndex - queueIndex; // 计算指定元素的索引
		if (dataIndex <= -1) {
			dataIndex = dataIndex + capacity;
		}
		
		return getRollingDataElement(dataIndex);
	}

	/**
	 * 头元素
	 * @return
	 */
	public TSD getHead() {
		return getRollingDataElement(headIndex);
	}

	/**
	 * 上一个
	 * @return
	 */
	public TSD getPrev() {
		return get(1);
	}

	/**
	 * 尾元素
	 * @return
	 */
	public TSD getTail() {
		if (isFull()) {
			if (headIndex == capacity - 1) {
				return getRollingDataElement(0);
			} else {
				return getRollingDataElement(this.headIndex + 1);
			}
		} else {
			return getRollingDataElement(0);
		}
	}
}
