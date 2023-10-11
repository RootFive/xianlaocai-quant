package xlc.quant.data.indicator.calculator;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import xlc.quant.data.indicator.Indicator;
import xlc.quant.data.indicator.IndicatorCalculator;
import xlc.quant.data.indicator.IndicatorCalculatorCallback;
import xlc.quant.data.indicator.util.DoubleUtils;

/**
 * 顺势指标CCI也包括日CCI指标、周CCI指标、年CCI指标以及分钟CCI指标等很多种类型。
 * 经常被用于股市研判的是日CCI指标和周CCI指标。
 * 虽然它们计算时取值有所不同，但基本方法一样。
 * 以日CCI计算为例，其计算方法有两种。 
 * =======================================================
 * 第一种计算过程如下： CCI（N日）=（TP－MA）÷MD÷0.015 
 * 其中:
 * 	N为计算周期
 * 	TP=（最高价+最低价+收盘价）÷3，TP即中价
 * 	MA=近N日收盘价的累计之和÷N 
 * 	MD=近N日（MA－收盘价）的绝对值的累计之和÷N 
 * 	0.015为计算系数，
 * 
 * 第二种计算方法表述为：
 * 	中价与中价的N日内移动平均的差 除以0.015*N日内中价的平均绝对偏差 
 *  其中，中价等于最高价、最低价和收盘价之和除以3
 *  平均绝对偏差为统计函数
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CCI extends Indicator {

	/** TP=（最高价+最低价+收盘价）÷3 */
	private Double tp;

	/** CCI值 */
	private Double value;

	public CCI(Double tp) {
		super();
		this.tp = tp;
	}

	public CCI(Double tp, Double value) {
		super();
		this.tp = tp;
		this.value = value;
	}

	//=============
	//内部类分隔符 XXX
	//=============
	/**
	 * @param capacity
	 * @return
	 */
	public static IndicatorCalculator<CCI> buildCalculator(int capacity) {
		return new CCICalculator(capacity);
	}

	/**
	 * 计算器
	 * @author Rootfive
	 */
	private static class CCICalculator extends IndicatorCalculator<CCI> {

		/** 
		 * 偏离系数（Multiplier）：CCI指标的计算中，常用的偏离系数为0.015。
		 * 该系数用于调整CCI指标的灵敏度，可以根据需要进行微调。 
		 */
		private static final Double multiplier = 0.015D;

		/**
		 * @param capacity
		 */
		public CCICalculator(int capacity) {
			super(capacity, false);
		}

		@Override
		protected CCI executeCalculate() {
			IndicatorCalculatorCallback<CCI> head = getHead();
//			BigDecimal TP = divide((head.getHigh().add(head.getLow()).add(head.getClose())),INT_3, 4);
			Double TP = DoubleUtils.mean(4,head.getHigh(),head.getLow(),head.getClose());
			if (!isFullCapacity()) {
				return new CCI(TP);
			}
			Double tpSumValueForMD = TP;
			for (IndicatorCalculatorCallback<CCI> indicatorCarrier : super.getCalculatorDataList()) {
				CCI cci = indicatorCarrier.getIndicator();
				if (cci !=null) {
					tpSumValueForMD = tpSumValueForMD + cci.getTp();
				}
			}
					
			Double MA = DoubleUtils.divide(tpSumValueForMD, fwcPeriod, 4);
			Double sumValueForMD = DoubleUtils.ZERO;
			for (IndicatorCalculatorCallback<CCI> calculator : super.getCalculatorDataList()) {

				CCI cci = calculator.getIndicator();
				Double tp = cci !=null ?cci.getTp():TP;
				sumValueForMD = sumValueForMD + Math.abs(MA -tp);
			}
			Double MD = DoubleUtils.divide(sumValueForMD, fwcPeriod, 4);
			Double cciValue = DoubleUtils.divide(DoubleUtils.divide(TP-MA, MD, 4), multiplier, 2);
			return new CCI(TP, cciValue);
		}

	}

}
