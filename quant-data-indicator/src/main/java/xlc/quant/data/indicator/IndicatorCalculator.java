package xlc.quant.data.indicator;

import static java.math.BigDecimal.valueOf;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 指标计算 计算器
 * 
 * @author Rootfive
 * 
 */
public abstract  class IndicatorCalculator<T extends Indicator>  extends  FixedWindowCalculator<T,IndicatorCalculatorCallback<T>> {
	
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


	public IndicatorCalculator(int period, boolean isFullCapacityCalculate) {
		super(period,  isFullCapacityCalculate);
	}

	// ==========XXX===================
	
	
	/**
	 * @param callback 新窗口数据
	 * @return
	 */
	@Override
	public T execute(IndicatorCalculatorCallback<T> callback) {
		T indicator = super.execute(callback);
		callback.setIndicator(indicator);
		return indicator;
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