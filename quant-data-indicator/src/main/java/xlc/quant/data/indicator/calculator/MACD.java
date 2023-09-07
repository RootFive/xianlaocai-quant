package xlc.quant.data.indicator.calculator;

import java.math.BigDecimal;
import java.math.RoundingMode;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import xlc.quant.data.indicator.Indicator;
import xlc.quant.data.indicator.IndicatorCalculator;
import xlc.quant.data.indicator.IndicatorCalculatorCallback;

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
	private BigDecimal fastEma;

	/** MACD_慢 */
	private BigDecimal slowEma;

	/** MACD_DIF */
	private BigDecimal dif;

	/** MACD_DEA */
	private BigDecimal dea;

	/** MACD值 */
	private BigDecimal macdValue;

	/** MACD连续上涨 */
	private Integer continueRise;

	public MACD(BigDecimal fastEma, BigDecimal slowEma, BigDecimal dif, BigDecimal dea, BigDecimal macdValue,
			Integer continueRise) {
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

		private final BigDecimal dividend = INT_2;
		private final BigDecimal difDivisor;

		MACDCalculator(int fastCycle, int slowCycle, int difCycle) {
			super(slowCycle, false);
			this.fastEMAFactor = EMA.buildCalculator(fastCycle);
			this.slowEMAFactor = EMA.buildCalculator(slowCycle);
			this.difDivisor = new BigDecimal(difCycle + 1);
		}

		@Override
		protected MACD executeCalculate() {
			IndicatorCalculatorCallback<MACD> current = getHead();

			// 快线EMA
			BigDecimal fastEma = fastEMAFactor.execute(new IndicatorCalculatorCallback<EMA>(current)).getValue();
			// 慢线EMA
			BigDecimal slowEma = slowEMAFactor.execute(new IndicatorCalculatorCallback<EMA>(current)).getValue();
			// 计算离差值(DIF)
			BigDecimal dif = fastEma.subtract(slowEma);

			// 前一个计算指数
			MACD prevMacd = null;
			IndicatorCalculatorCallback<MACD> prev = getPrev();
			if (prev != null) {
				prevMacd = prev.getIndicator();
			}
			// 计算DIF的9日EMA
			BigDecimal dea = null;
			if (prevMacd == null) {
				dea = dif;
			} else {
				dea = getEma(dif, prevMacd, difDivisor);
			}

			/** MACD称为异同移动平均线:表达式MACD=2×（DIF-DEA） */
			BigDecimal macdValue = INT_2.multiply(dif.subtract(dea)).setScale(2, RoundingMode.HALF_UP);

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
		private BigDecimal getEma(BigDecimal currentUse, MACD prevMacd, BigDecimal divisor) {
			return dividend.multiply(currentUse.subtract(prevMacd.getDea())).divide(divisor, 2, RoundingMode.HALF_UP).add(prevMacd.getDea());
					
		}
	}

}
