package xlc.quant.data.indicator.calculator.innovate;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import xlc.quant.data.indicator.Indicator;
import xlc.quant.data.indicator.IndicatorCalculator;
import xlc.quant.data.indicator.IndicatorCalculatorCallback;

/**
 * @author Rootfive
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TOPMV extends Indicator {

	/** 成交额-最高值-前X个均值 */
	private BigDecimal ath;
	/** 成交额-最低值-前X个均值 */
	private BigDecimal atl;

	/** 成交量-最高值-前X个均值 */
	private BigDecimal vth;
	/** 成交量-最低值-前X个均值 */
	private BigDecimal vtl;

	public TOPMV(BigDecimal ath, BigDecimal atl, BigDecimal vth, BigDecimal vtl) {
		super();
		this.ath = ath;
		this.atl = atl;
		this.vth = vth;
		this.vtl = vtl;
	}

	//=============
	//内部类分隔符 XXX
	//=============
	/**
	 * @param capacity
	 * @param top
	 * @return
	 */
	public static IndicatorCalculator<TOPMV> buildCalculator(int capacity, int top) {
		return  new TOPMVCalculator(capacity,  top);
    }

	/**
	 * 计算器
	 * @author Rootfive
	 */
	private static class TOPMVCalculator extends IndicatorCalculator<TOPMV> {

		private final int top;
		private final BigDecimal topValue;

		/**
		 * @param capacity
		 * @param top
		 */
		TOPMVCalculator(int capacity, int top) {
			super(capacity, true);
			this.top = top;
			this.topValue = new BigDecimal(top);
		}

		/**
		 *
		 */
		@Override
		protected TOPMV executeCalculate() {
			// 成交额-所有
			List<BigDecimal> listAmount = super.getCalculatorListData().stream().map(IndicatorCalculatorCallback::getAmount).collect(Collectors.toList());
			// 倒叙
			BigDecimal reverseOrderSumAmount = listAmount.stream().sorted(Comparator.reverseOrder()).limit(top).reduce(BigDecimal::add).get();
			/** 成交额-最高值-前X个均值 */
			BigDecimal ath = divide(reverseOrderSumAmount, topValue, 2);

			// 正序
			BigDecimal naturalOrderSumAmount = listAmount.stream().sorted(Comparator.naturalOrder()).limit(top).reduce(BigDecimal::add).get();
			/** 成交额-最低值-前X个均值 */
			BigDecimal atl = divide(naturalOrderSumAmount, topValue, 2);

			// 成交量-所有
			List<BigDecimal> listVolume = super.getCalculatorListData().stream().map(IndicatorCalculatorCallback::getVolume).collect(Collectors.toList());

					
			// 倒叙
			BigDecimal reverseOrderSumVolume = listVolume.stream().sorted(Comparator.reverseOrder()).limit(top).reduce(BigDecimal::add).get();
			/** 成交量-最高值-前X个均值 */
			BigDecimal vth = divide(reverseOrderSumVolume, topValue, 2);
			// 正序
			BigDecimal naturalOrderSumVolume = listVolume.stream().sorted(Comparator.naturalOrder()).limit(top).reduce(BigDecimal::add).get();
					
			/** 成交量-最低值-前X个均值 */
			BigDecimal vtl = divide(naturalOrderSumVolume, topValue, 2);
			return new TOPMV(ath, atl, vth, vtl);
		}

	}
}
