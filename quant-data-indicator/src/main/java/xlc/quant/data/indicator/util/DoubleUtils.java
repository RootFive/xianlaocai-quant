/**
 * 
 */
package xlc.quant.data.indicator.util;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.stat.StatUtils;

import com.google.common.primitives.Doubles;

/**
 * 
 * @author Rootfive
 */
public class DoubleUtils {


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
	 * 获得增长率，
	 * 
	 * @param preClose 前收
	 * @param close    新收
	 * @return 百分比，保留2位小数（不包含百分号）
	 */
	public static double getChgRate(Double preClose, Double close) {
		return getChgRateByDValue(preClose, close-preClose);
	}

	/**
	 * 根据差值数据，获得增长率，
	 * 
	 * @param preClose 前收
	 * @param dValue   差值
	 * @return 百分比，保留2位小数（不包含百分号）
	 */
	public static Double getChgRateByDValue(Double preClose, Double dValue) {
		if (preClose == null || preClose == 0) {
			return null;
		}
		return divideByPct(dValue, preClose);
	}

	/**
	 * 获得平均价
	 * 
	 * @param amount       成交额，单位元
	 * @param volume       成交量,股/份
	 * @param scale        保留的小数位
	 * @param defaultClose
	 * @return
	 */
	public static Double getAvgPrice(Double amount, Double volume, int scale, Double close) {
		if (amount == null || amount == 0d || volume == null || volume == 0d) {
			return close;
		}
		double quotient  = amount/volume; 
		return  setScale(quotient,scale);
	}

	/**
	 * 获得价格振幅
	 * 
	 * @param preClose 前收价，单位元
	 * @param high     最高价，单位元
	 * @param low      最低价，单位元
	 * @return 价格振幅，百分比，保留2位小数
	 */
	public static Double getAmplitude(Double preClose, Double high, Double low) {
		if (preClose == null || high == null || low == null || preClose == 0d) {
			return null;
		}
		
		double quotient  = (high-low)/preClose; 
		return convertMultiple(quotient, 100, 2);
	}

	/**
	 * 转换倍数
	 * 
	 * @param source   源数据
	 * @param multiple 倍数
	 * @return
	 */
	public static double convertMultiple(double source, double multiple, int scale) {
		return  setScale(source*multiple,scale);
	}

	/**
	 * 求差额或净额
	 * 
	 * @param minuend    被减数
	 * @param subtrahend 减数
	 * @return 减数是减法算式中从被减数中扣除的数。在减法运算中，例如a-b=c,读作a减b等于c,a称为被减数,b称为减数。
	 */
	public static double balance(double minuend, double subtrahend) {
		return minuend-subtrahend;
	}

	/**
	 * @param dividend 被除数:除号前面的数
	 * @param divisor  除数:除号后面的数的
	 * @param scale
	 * @return
	 */
	public static double divide(double dividend, double divisor) {
		return  setScale(dividend/divisor,2);
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
	 * @param dividend 被除数:除号前面的数
	 * @param divisor  除数:除号后面的数的
	 * @return 百分点
	 */
	public static double divideByPct(double dividend, double divisor) {
		double quotient  = dividend/divisor; 
		return setScale(quotient*100,2);
	}
	
	/**
	 * @param dividend 被除数:除号前面的数
	 * @param divisor  除数:除号后面的数的
	 * @return 4位小数
	 */
	public static double divideByScale4(double dividend, double divisor) {
		double quotient  = dividend/divisor; 
		return setScale(quotient,4);
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
	 * 求和
	 * @param numbers
	 * @return
	 */
	public static double getSum(Double ...numbers) {
		return getSum(2 , numbers);
	}
	
	/**
	 * 求和
	 * @param scale
	 * @param numbers
	 * @return
	 */
	public static double getSum(Integer scale,Double ...numbers) {
		if (ArrayUtils.isEmpty(numbers)) {
			return ZERO;
		}
		
		double sum = 0;
		for (Double d : numbers) {
			if (d != null) {
				sum = sum +d;
			}
		}
		return setScale(sum , scale);
	}
	
	

	
	
	/**
	 * 求平均数
	 * @param scale
	 * @param numbers
	 * @return
	 */
	public static double mean(int scale,double ...numbers) {
		if (ArrayUtils.isEmpty(numbers)) {
			return ZERO;
		}
		double average = StatUtils.mean(numbers);
		return setScale(average , scale);
	}
	
	/**
	 * 求最大值
	 * @param numbers
	 * @return
	 */
	public static double max(double ...numbers) {
		return  Doubles.max(numbers);
	}
}
