package xlc.quant.data.indicator.calculator;

import java.math.BigDecimal;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import xlc.quant.data.indicator.Indicator;
import xlc.quant.data.indicator.IndicatorCarrier;

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
	private BigDecimal tp;

	/** CCI值 */
	private BigDecimal value;

	public CCI(BigDecimal tp) {
		super();
		this.tp = tp;
	}

	public CCI(BigDecimal tp, BigDecimal value) {
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
	 * 顺势指标
	 */
	private static class CCICalculator extends IndicatorCalculator<CCI> {

		/** 
		 * 偏离系数（Multiplier）：CCI指标的计算中，常用的偏离系数为0.015。
		 * 该系数用于调整CCI指标的灵敏度，可以根据需要进行微调。 
		 */
		private static final BigDecimal multiplier = new BigDecimal("0.015");

		/**
		 * @param capacity
		 */
		public CCICalculator(int capacity) {
			super(capacity, false);
		}

		@Override
		protected CCI executeCalculate() {
			IndicatorCarrier<CCI> head = getHead();
			BigDecimal TP = divide((head.getHigh().add(head.getLow()).add(head.getClose())),INT_3, 4);
			if (!isFullCapacity) {
				return new CCI(TP);
			}
			BigDecimal tpSumValueForMD = TP;
			for (IndicatorCarrier<CCI> indicatorCarrier : super.circularArrayElementData) {
				CCI cci = indicatorCarrier.getIndicator();
				if (cci !=null) {
					tpSumValueForMD = tpSumValueForMD.add(cci.getTp());
				}
			}
					
			BigDecimal MA = divide(tpSumValueForMD, periodCapacity, 4);
			BigDecimal sumValueForMD = BigDecimal.ZERO;
			for (IndicatorCarrier<CCI> calculator : super.circularArrayElementData) {

				CCI cci = calculator.getIndicator();
				BigDecimal tp = cci !=null ?cci.getTp():TP;
				BigDecimal subtract = MA.subtract(tp);
				
				if (subtract.compareTo(BigDecimal.ZERO) < 0) {
					sumValueForMD = sumValueForMD.add(subtract.multiply(MINUS_INT_1));
				} else {
					sumValueForMD = sumValueForMD.add(subtract);
				}
			}
			BigDecimal MD = divide(sumValueForMD, periodCapacity, 4);
			BigDecimal cciValue = divide(divide(TP.subtract(MA), MD, 4), multiplier, 4);
			return new CCI(TP, cciValue);
		}

	}

}
