package xlc.quant.data.indicator.calculator;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import xlc.quant.data.indicator.Indicator;
import xlc.quant.data.indicator.IndicatorCalculator;
import xlc.quant.data.indicator.IndicatorCalculatorCallback;
import xlc.quant.data.indicator.util.DoubleUtils;

/**
 * 
 * @author Rootfive
 * 平滑异同移动平均线指标MACD指标
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
	public static IndicatorCalculator<MACD> buildCalculator(int fastCycle, int slowCycle, int difCycle) {
		return new MACDCalculator(fastCycle, slowCycle, difCycle);
	}

	/**
	 * 计算器
	 * @author Rootfive
	 */
	private static class MACDCalculator extends IndicatorCalculator<MACD> {

		private final IndicatorCalculator<EMA> fastEMAFactor;
		private final IndicatorCalculator<EMA> slowEMAFactor;

		private final int difDivisor;

		MACDCalculator(int fastCycle, int slowCycle, int difCycle) {
			super(slowCycle, false);
			this.fastEMAFactor = EMA.buildCalculator(fastCycle);
			this.slowEMAFactor = EMA.buildCalculator(slowCycle);
			this.difDivisor = difCycle + 1;
		}

		@Override
		protected MACD executeCalculate() {
			IndicatorCalculatorCallback<MACD> current = getHead();

			// 快线EMA
			Double fastEma = fastEMAFactor.input(new IndicatorCalculatorCallback<EMA>(current)).getValue();
			// 慢线EMA
			Double slowEma = slowEMAFactor.input(new IndicatorCalculatorCallback<EMA>(current)).getValue();
			// 计算离差值(DIF)
			Double dif = DoubleUtils.setScale(fastEma - slowEma, 2);

			// 前一个计算指数
			MACD prevMacd = null;
			IndicatorCalculatorCallback<MACD> prev = getPrev();
			if (prev != null) {
				prevMacd = prev.getIndicator();
			}
			// 计算DIF的9日EMA
			Double dea = null;
			if (prevMacd == null) {
				dea = dif;
			} else {
				dea = DoubleUtils.setScale(getEma(dif, prevMacd, difDivisor), 2);
			}

			/** MACD称为异同移动平均线:表达式MACD=2×（DIF-DEA） */
			Double macdValue = DoubleUtils.setScale(2 * (dif-dea), 2);
			

			MACD macd = null;
			if (prevMacd == null) {
				macd = new MACD(fastEma, slowEma, dif, dea, macdValue, 0);
			} else {
				int continueResult = getContinueValue(macdValue, prevMacd.getMacdValue(), prevMacd.getContinueRise());
				macd = new MACD(fastEma, slowEma, dif, dea, macdValue, continueResult);
			}
			return macd;
		}

		/**
		 * 计算EMA
		 * 
		 * @param currentUse
		 * @param prevMacd
		 * @param divisor
		 * @return
		 */
		private Double getEma(Double currentUse, MACD prevMacd, double divisor) {
			return DoubleUtils.divide(2 * (currentUse - prevMacd.getDea()), divisor, 2) + prevMacd.getDea();
		}
	}

}
