package xlc.quant.data.indicator.calculator.innovate;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiConsumer;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import xlc.quant.data.indicator.Indicator;
import xlc.quant.data.indicator.IndicatorCalculateCarrier;
import xlc.quant.data.indicator.IndicatorCalculator;
import xlc.quant.data.indicator.util.DoubleUtils;

/**
 * @author Rootfive
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class XlcTOPMV extends Indicator {

	/** 成交额-最高值-前X个均值 */
	private Double ath;
	/** 成交额-最低值-前X个均值 */
	private Double atl;

	/** 成交量-最高值-前X个均值 */
	private Double vth;
	/** 成交量-最低值-前X个均值 */
	private Double vtl;

	public XlcTOPMV(double ath, double atl, double vth, double vtl) {
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
	 * @param indicatorAmountSetScale  指标Amount精度
	 * @param indicatorVolumeSetScale  指标Volume精度
	 * @return
	 */
	public static <CARRIER extends IndicatorCalculateCarrier<?>> IndicatorCalculator<CARRIER, XlcTOPMV> buildCalculator(
			int capacity, int top,int indicatorAmountSetScale,int indicatorVolumeSetScale
			,BiConsumer<CARRIER, XlcTOPMV> propertySetter) {
		return  new XlcTOPMVCalculator<>(capacity,  top, indicatorAmountSetScale, indicatorVolumeSetScale,propertySetter);
    }

	/**
	 * 计算器
	 * @author Rootfive
	 */
	private static class XlcTOPMVCalculator<CARRIER extends IndicatorCalculateCarrier<?>>  extends IndicatorCalculator<CARRIER, XlcTOPMV> {

		private final int top;
		/** 指标Amount精度 */
		private final int indicatorAmountSetScale;
		/** 指标Volume精度 */
		private final int indicatorVolumeSetScale;
		
		/**
		 * @param capacity
		 * @param top
		 * @param indicatorAmountSetScale  指标Amount精度
		 * @param indicatorVolumeSetScale  指标Volume精度
		 */
		XlcTOPMVCalculator(int capacity, int top,int indicatorAmountSetScale,int indicatorVolumeSetScale,BiConsumer<CARRIER, XlcTOPMV> propertySetter) {
			super(capacity, true, propertySetter);
			this.top = top;
			this.indicatorAmountSetScale = indicatorAmountSetScale;
			this.indicatorVolumeSetScale = indicatorVolumeSetScale;
		}

		/**
		 *
		 */
		@Override
		protected XlcTOPMV executeCalculate() {
			// 成交额-所有
			List<Double> listAmount = new ArrayList<>(capacity());
			// 成交量-所有
			List<Double> listVolume = new ArrayList<>(capacity());
			
			
			for (int i = 0; i < capacity(); i++) {
				CARRIER carrier_i = get(i);
				listAmount.add(carrier_i.getAmount());
				listVolume.add(carrier_i.getVolume());
			}
			
			
			// 倒叙
			Double reverseOrderSumAmount = listAmount.stream().sorted(Comparator.reverseOrder()).limit(top).mapToDouble(Double::doubleValue).sum();
			/** 成交额-最高值-前X个均值 */
			Double ath = DoubleUtils. divide(reverseOrderSumAmount, top, indicatorAmountSetScale);

			// 正序
			Double naturalOrderSumAmount = listAmount.stream().sorted(Comparator.naturalOrder()).limit(top).mapToDouble(Double::doubleValue).sum();
			/** 成交额-最低值-前X个均值 */
			Double atl = DoubleUtils.divide(naturalOrderSumAmount, top, indicatorAmountSetScale);

			// 倒叙
			Double reverseOrderSumVolume = listVolume.stream().sorted(Comparator.reverseOrder()).limit(top).mapToDouble(Double::doubleValue).sum();
			/** 成交量-最高值-前X个均值 */
			Double vth = DoubleUtils.divide(reverseOrderSumVolume, top, indicatorVolumeSetScale);
			
			// 正序
			Double naturalOrderSumVolume = listVolume.stream().sorted(Comparator.naturalOrder()).limit(top).mapToDouble(Double::doubleValue).sum();
			/** 成交量-最低值-前X个均值 */
			Double vtl = DoubleUtils.divide(naturalOrderSumVolume, top, indicatorVolumeSetScale);
			
			return new XlcTOPMV(ath, atl, vth, vtl);
			
		}

	}
}
