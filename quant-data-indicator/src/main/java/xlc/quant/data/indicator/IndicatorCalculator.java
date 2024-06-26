package xlc.quant.data.indicator;

import java.util.function.BiConsumer;

/**
 * 指标计算器-抽象父类
 * @author Rootfive
 * @param <CARRIER> 泛型，计算指标的载体，有限制
 * @param <INDI>    泛型，根据载体计算出的指标，没有限制，可以是任何值
 */
public abstract class IndicatorCalculator<CARRIER extends IndicatorCalculateCarrier<?>, INDI>
		extends TimeSeriesDataRollingWindowQueue<CARRIER> {
	
	/** 
	 * 是否满容计算
	 *  <pre>
	 *  使用场景是有些指标必须需要一定数量的载体才能计算，比如:BOLL、KDJ等
	 *  </pre>
	 */
	private final boolean isFullCapacityCalculate;
	
	/** 委托方法，计算结果设置到载体的哪个属性 */
	private final BiConsumer<CARRIER, INDI> propertySetter;

	/**
	 * @param period   	周期			
	 * @param isFullCapacityCalculate  是否满容计算
	 * @param propertySetter  委托方法，计算结果设置到载体的哪个属性
	 */
	public IndicatorCalculator(int period, boolean isFullCapacityCalculate,BiConsumer<CARRIER, INDI> propertySetter) {
		super(period);
		this.isFullCapacityCalculate = isFullCapacityCalculate;
		this.propertySetter = propertySetter;
	}

	/**
	 * @param enterCarrier 输入的计算载体
	 * @return
	 */
	public synchronized INDI input(CARRIER enterCarrier) {
		boolean addResult = super.accept(enterCarrier);
		if (addResult) {
			// 新增成功
			if (!isFullCapacityCalculate || (isFullCapacityCalculate && this.isFull())) {
				// 二者满足其中一种。均可执行计算，条件：1、不是满容计算 [或] 2满容计算且已经满容，
				INDI calculateResult = executeCalculate();
				if (propertySetter != null) {
					//设置计算结果 到 输入新数据[enterCarrier]属性值上，做到指标和数据对应
					propertySetter.accept(enterCarrier, calculateResult);
				}
				return calculateResult;
			}
		}
		return null;
	}

	/**
	 * 执行计算，由子类具体某个指标的计算器实现
	 * @param propertyGetter  委托方法，从载体类获取计算结果的方法
	 * @return
	 */
	protected abstract INDI executeCalculate();

	/**
	 * @param current  当前值
	 * @param prev	   前值	
	 * @param preContinueValue  前连续值
	 * @return
	 */
	public static int getContinueValue(Double current, Double prev, Integer preContinueValue) {
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

}