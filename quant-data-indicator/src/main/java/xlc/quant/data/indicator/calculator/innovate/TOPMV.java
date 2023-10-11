package xlc.quant.data.indicator.calculator.innovate;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import xlc.quant.data.indicator.Indicator;
import xlc.quant.data.indicator.IndicatorCalculator;
import xlc.quant.data.indicator.IndicatorCalculatorCallback;
import xlc.quant.data.indicator.util.DoubleUtils;

/**
 * @author Rootfive
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TOPMV extends Indicator {

	/** 成交额-最高值-前X个均值 */
	private Double ath;
	/** 成交额-最低值-前X个均值 */
	private Double atl;

	/** 成交量-最高值-前X个均值 */
	private Double vth;
	/** 成交量-最低值-前X个均值 */
	private Double vtl;

	public TOPMV(double ath, double atl, double vth, double vtl) {
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

		/**
		 * @param capacity
		 * @param top
		 */
		TOPMVCalculator(int capacity, int top) {
			super(capacity, true);
			this.top = top;
		}

		/**
		 *
		 */
		@Override
		protected TOPMV executeCalculate() {
			// 成交额-所有
			List<Double> listAmount = super.getCalculatorDataList().stream().map(IndicatorCalculatorCallback::getAmount).collect(Collectors.toList());
			// 倒叙
			Double reverseOrderSumAmount = listAmount.stream().sorted(Comparator.reverseOrder()).limit(top).mapToDouble(Double::doubleValue).sum();
	                
			/** 成交额-最高值-前X个均值 */
			Double ath = DoubleUtils. divide(reverseOrderSumAmount, top, 2);

			// 正序
			Double naturalOrderSumAmount = listAmount.stream().sorted(Comparator.naturalOrder()).limit(top).mapToDouble(Double::doubleValue).sum();
			/** 成交额-最低值-前X个均值 */
			Double atl = DoubleUtils.divide(naturalOrderSumAmount, top, 2);

			// 成交量-所有
			List<Double> listVolume = super.getCalculatorDataList().stream().map(IndicatorCalculatorCallback::getVolume).collect(Collectors.toList());

					
			// 倒叙
			Double reverseOrderSumVolume = listVolume.stream().sorted(Comparator.reverseOrder()).limit(top).mapToDouble(Double::doubleValue).sum();
			/** 成交量-最高值-前X个均值 */
			Double vth = DoubleUtils.divide(reverseOrderSumVolume, top, 2);
			// 正序
			Double naturalOrderSumVolume = listVolume.stream().sorted(Comparator.naturalOrder()).limit(top).mapToDouble(Double::doubleValue).sum();
					
			/** 成交量-最低值-前X个均值 */
			Double vtl = DoubleUtils.divide(naturalOrderSumVolume, top, 2);
			return new TOPMV(ath, atl, vth, vtl);
		}

	}
}
