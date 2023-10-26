package xlc.quant.data.indicator.calculator;

import java.util.function.BiConsumer;
import java.util.function.Function;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import xlc.quant.data.indicator.Indicator;
import xlc.quant.data.indicator.IndicatorCalculateCarrier;
import xlc.quant.data.indicator.IndicatorCalculator;
import xlc.quant.data.indicator.util.DoubleUtils;

/**
 * <pre>
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
 *  </pre>
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
	 * @param indicatorSetScale        指标精度
	 * @param assistIndicatorSetScale  辅助指标精度
	 * @return
	 */
	public static <CARRIER extends IndicatorCalculateCarrier<?>> IndicatorCalculator<CARRIER, CCI> buildCalculator(int capacity,int indicatorSetScale,BiConsumer<CARRIER, CCI> propertySetter,Function<CARRIER, CCI> propertyGetter) {
		return new CCICalculator<>(capacity, indicatorSetScale,propertySetter,propertyGetter);
	}

	/**
	 * 计算器
	 * @author Rootfive
	 */
	private static class CCICalculator<CARRIER extends IndicatorCalculateCarrier<?>> extends IndicatorCalculator<CARRIER, CCI> {

		/** 
		 * <pre>
		 * 偏离系数（Multiplier）：CCI指标的计算中，常用的偏离系数为0.015。
		 * 该系数用于调整CCI指标的灵敏度，可以根据需要进行微调。 
		 * </pre>
		 */
		private static final Double multiplier = 0.015D;
		
		/** 指标精度 */
		private final int indicatorSetScale;
		
		private final Function<CARRIER, CCI> propertyGetter;

		/**
		 * @param capacity
		 */
		CCICalculator(int capacity,int indicatorSetScale,BiConsumer<CARRIER, CCI> propertySetter,Function<CARRIER, CCI> propertyGetter) {
			super(capacity, false,propertySetter);
			this.indicatorSetScale =  indicatorSetScale;
			this.propertyGetter =  propertyGetter;
		}

		@Override
		protected CCI executeCalculate() {
			CARRIER head = getHead();
			Double tp = DoubleUtils.average(DoubleUtils.MAX_SCALE,head.getHigh(),head.getLow(),head.getClose());
			if (!isFull()) {
				return new CCI(tp);
			}
			
			
			Double tpSumValueForMD = tp;
			for (int i = 1; i < capacity(); i++) {
				CARRIER carrier_i = get(i);
				CCI cci_i = propertyGetter.apply(carrier_i);
				if (cci_i !=null) {
					tpSumValueForMD = tpSumValueForMD + cci_i.getTp();
				}
			}
			Double ma = DoubleUtils.divide(tpSumValueForMD, capacity(), DoubleUtils.MAX_SCALE);
			
			Double sumValueForMD = DoubleUtils.ZERO;
			for (int i = 0; i < capacity(); i++) {
				CARRIER carrier_i = get(i);

				CCI cci_i = propertyGetter.apply(carrier_i);
				Double tp_i = cci_i !=null ?cci_i.getTp():tp;
				sumValueForMD = sumValueForMD + Math.abs(ma -tp_i);
			}
			Double md = DoubleUtils.divide(sumValueForMD, capacity(), DoubleUtils.MAX_SCALE);
			
			Double cciValue = DoubleUtils.divide(DoubleUtils.divide(tp-ma, md, DoubleUtils.MAX_SCALE), multiplier, indicatorSetScale);
			return new CCI(tp, cciValue);
		}

	}

}
