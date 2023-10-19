/**
 * 
 */
package xlc.quant.data.indicator.util;

import org.apache.commons.lang3.ArrayUtils;

import com.google.common.primitives.Doubles;

/**
 * 
 * @author Rootfive
 */
public class DoubleUtils {


	/** Double最大精度.15位小数 */
	public static final int MAX_SCALE = 15;
	
	/** 零 */
	public static final double ZERO = 0;
	
	/** 百 */
	public static final double HUNDRED = 100;

	/** 千 */
	public static final double THOUSAND = 1000;

	/** 万   */
	public static final double TEN_THOUSAND = 1_0000;

	/** 十万  */
	public static final double ONE_HUNDRED_THOUSAND = 10_0000;

	/** 百万 */
	public static final double MILLION = 100_0000;
	
	/** 千万 */
	public static final double TEN_MILLION = 1000_0000;
	
	/** 亿 */
	public static final double ONE_HUNDRED_MILLION = 1_0000_0000;
	
	
	/**
	 * 设置精度
	 * @param value
	 * @param newScale
	 * @return
	 * 将给定的 double 值乘以一个放大因子，然后使用 Math.round() 方法四舍五入，最后再除以放大因子，以达到截断小数点位数的效果。
	 * 这种方法的性能相对较高，因为它只使用了简单的数学运算，没有涉及字符串操作或格式化。
	 * 它直接对 double 值进行数学计算，因此在处理大量数据时能够更高效地执行。
	 */
	public static double setScale(double value, int newScale) {
		if (newScale > 15|| newScale < 0 ) {
            throw new IllegalArgumentException("小数位必须在（1-15）位");
        }
		
		double factor = Math.pow(10, newScale);
		return Math.round(value * factor) / factor;
	}

	/**
	 * @param value
	 * @return 使用了循环和数学运算来获取 double 值的小数点位数。
	 */
	public static int getScale(double value) {
		int decimalPlaces = 0;
		while (value != Math.floor(value)) {
			value *= 10;
			decimalPlaces++;
		}
		return decimalPlaces;
	}
	
	
	
	/**
	 * 求和
	 * @param scale
	 * @param numbers
	 * @return
	 */
	public static double getSum(Double ...numbers) {
		if (ArrayUtils.isEmpty(numbers)) {
			return Double.NaN ;
		}
		double sum = 0;
		for (Double d : numbers) {
			if (d != null) {
				sum = sum +d;
			}
		}
		return sum;
	}
	
	
	
	/**
	 * @param multiplicand 被乘数指四则运算的乘法中被乘的数字，一般来说放在算式的前面。
	 * @param multiplier   乘数:乘号后面的数的
	 * @param scale
	 * @return
	 */
	public static double multiply(double multiplicand, double multiplier, int scale) {
		return setScale(multiplicand*multiplier , scale);
	}
		
	
	
	/**
	 * @param dividend 被除数:除号前面的数
	 * @param divisor  除数:除号后面的数的
	 * @param scale
	 * @return
	 */
	public static double divide(double dividend, double divisor, int scale) {
		return  setScale(dividend/divisor,scale);
	}	
	
	/**
	 * 求最大值
	 * @param numbers
	 * @return
	 */
	public static double max(double ...numbers) {
		return  Doubles.max(numbers);
	}
	
	/**
	 * 求最小值
	 * @param numbers
	 * @return
	 */
	public static double min(double ...numbers) {
		return  Doubles.min(numbers);
	}
	
	
	/**
	 * 求平均数
	 * @param numbers
	 * @return
	 */
	public static double average(int scale,double ...numbers) {
		if (ArrayUtils.isEmpty(numbers)) {
			return Double.NaN ;
		}
		
		double sum = 0;
		for (double d : numbers) {
			sum = sum +d;
		}
		return  divide(sum, numbers.length, scale);
	}
	
	
	/**
	 * @param dividend 被除数:除号前面的数
	 * @param divisor  除数:除号后面的数的
	 * @return 百分点
	 */
	public static double divideByPct(double dividend, double divisor) {
		double quotient  = dividend/divisor; 
		return setScale(quotient*100,2);
	}
	
	
	/**
	 * 获得价格振幅
	 * 
	 * @param preClose 前收价，单位元
	 * @param high     最高价，单位元
	 * @param low      最低价，单位元
	 * @return 价格振幅，百分比，保留2位小数
	 */
	public static double getAmplitude(double preClose, double high, double low) {
		return getChgRateByDValue(preClose, high-low);
	}
	
	/**
	 * 获得增长率，
	 * 
	 * @param preClose 前收
	 * @param close    新收
	 * @return 百分比，保留2位小数（不包含百分号）
	 */
	public static double getChgRate(double preClose, double close) {
		return getChgRateByDValue(preClose, close-preClose);
	}
	
	/**
	 * 根据差值数据，获得增长率，
	 * 
	 * @param preClose 前收
	 * @param dValue   差值
	 * @return 百分比，保留2位小数（不包含百分号）
	 */
	public static Double getChgRateByDValue(double preClose, double dValue) {
		return divideByPct(dValue, preClose);
	}
	
	/**
	 * 获得成交均价
	 * 
	 * @param amount       成交额，单位元
	 * @param volume       成交量,股/份
	 * @param scale        保留的小数位
	 * @param defaultFinalUseClose  默认最后使用收盘价兜底
	 * @return
	 */
	public static double getTradeAvgPrice(double amount, double volume, int scale, double defaultUseClose) {
		if ( amount == 0d || volume == 0d) {
			return defaultUseClose;
		}
		return  DoubleUtils.setScale(amount/volume,scale);
	}

}
