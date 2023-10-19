package xlc.quant.data.indicator;

/**
 * 指标计算计算器(抽象父类)
 * @author Rootfive
 * @param <CARRIER>  计算指标的载体
 * @param <INDI>     根据载体计算出的指标
 */
public abstract class IndicatorCalculator<CARRIER extends IndicatorComputeCarrier<?>, INDI> 	extends FixedWindowCircularCalculator<CARRIER, INDI> {

	/**
	 * @param maxPeriod   	最大周期			
	 * @param isFullCapacityCalculate  是否满容计算
	 */
	public IndicatorCalculator(int maxPeriod, boolean isFullCapacityCalculate) {
		super(maxPeriod, isFullCapacityCalculate);
	}

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