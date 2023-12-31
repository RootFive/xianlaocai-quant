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
 * 平滑异同移动平均线指标MACD指标
 * @author Rootfive
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class MACD extends Indicator {

	/** MACD_快 */
	private Double fastEma;

	/** MACD_慢 */
	private Double slowEma;

	/** MACD_DIF */
	private Double dif;

	/** MACD_DEA */
	private Double dea;

	/** MACD值 */
	private Double macdValue;

	/** MACD连续上涨 */
	private int continueRise;

	public MACD(Double fastEma, Double slowEma, Double dif, Double dea, Double macdValue, int continueRise) {
		super();
		this.fastEma = fastEma;
		this.slowEma = slowEma;
		this.dif = dif;
		this.dea = dea;
		this.macdValue = macdValue;
		this.continueRise = continueRise;
	}

	//=============
	//内部类分隔符 XXX
	//=============
	/**
	 * 构建-计算器
	 * @param fastCycle
	 * @param slowCycle
	 * @param difCycle
	 * @return
	 */
	public static <CARRIER extends IndicatorCalculateCarrier<?>> IndicatorCalculator<CARRIER, MACD> buildCalculator(
			int fastCycle, int slowCycle, int difCycle,int indicatorSetScale,
			BiConsumer<CARRIER, MACD> propertySetter,Function<CARRIER, MACD> propertyGetter) {
		return new MACDCalculator<>(fastCycle, slowCycle, difCycle,indicatorSetScale,propertySetter, propertyGetter);
	}

	/**
	 * 计算器
	 * @author Rootfive
	 */
	private static class MACDCalculator<CARRIER extends IndicatorCalculateCarrier<?>> extends IndicatorCalculator<CARRIER, MACD> {
		
		/** 快线平滑系数 设定值为（2/n+1); */
		private final Double fastEMA_α;
		/** 慢线平滑系数 设定值为（2/n+1); */
		private final Double slowEMA_α;
		/** 离差值(DIF) 平滑系数 设定值为（2/n+1); */
		private final Double difEMA_α;
		
		
		/** 指标精度 */
		private final int indicatorSetScale;
		private final Function<CARRIER, MACD> propertyGetter;
		
		
		MACDCalculator(int fastCycle, int slowCycle, int difCycle,int indicatorSetScale,BiConsumer<CARRIER, MACD> propertySetter,Function<CARRIER, MACD> propertyGetter) {
			super(slowCycle, false,propertySetter);
			this.fastEMA_α = DoubleUtils.divide(2, fastCycle + 1, DoubleUtils.MAX_SCALE);
			this.slowEMA_α = DoubleUtils.divide(2, slowCycle + 1, DoubleUtils.MAX_SCALE);
			this.difEMA_α =  DoubleUtils.divide(2, difCycle + 1, DoubleUtils.MAX_SCALE);
			this.indicatorSetScale =  indicatorSetScale;
			this.propertyGetter = propertyGetter;
		}

		@Override
		protected MACD executeCalculate() {
			CARRIER head = getHead();
			CARRIER prev = getPrev();
			
			// 前一个计算指数
			MACD prevMacd = null;
			
			// 快线EMA
			Double fastEma = null;
			// 慢线EMA
			Double slowEma = null; 
			
			if (prev == null) {
				// 快线EMA
				fastEma =  DoubleUtils.average(DoubleUtils.MAX_SCALE,head.getHigh(),head.getLow(),head.getClose());
				// 慢线EMA
				slowEma =  DoubleUtils.average(DoubleUtils.MAX_SCALE,head.getHigh(),head.getLow(),head.getClose());
			}else {
				// 当前使用价格
				Double headClose = head.getClose();
				
				prevMacd = propertyGetter.apply(prev);
				fastEma = EMA.calculateEma(headClose, prevMacd.getFastEma(), fastEMA_α,DoubleUtils.MAX_SCALE);
				slowEma = EMA.calculateEma(headClose, prevMacd.getSlowEma(), slowEMA_α,DoubleUtils.MAX_SCALE);
			}
			
			// 计算离差值(DIF)
			Double dif = DoubleUtils.setScale(fastEma - slowEma, indicatorSetScale);
			
			// 计算DIF的9日EMA
			Double dea = null;
			if (prevMacd == null) {
				dea = dif;
			} else {
				dea = EMA.calculateEma(dif, prevMacd.getDea(), difEMA_α,indicatorSetScale);
			}

			/** MACD称为异同移动平均线:表达式MACD=2×（DIF-DEA） */
			Double macdValue = DoubleUtils.setScale(2 * (dif-dea), indicatorSetScale);

			MACD macd = null;
			if (prevMacd == null) {
				macd = new MACD(fastEma, slowEma, dif, dea, macdValue, 0);
			} else {
				int continueResult = getContinueValue(macdValue, prevMacd.getMacdValue(), prevMacd.getContinueRise());
				macd = new MACD(fastEma, slowEma, dif, dea, macdValue, continueResult);
			}
			return macd;
		}


	}

}
