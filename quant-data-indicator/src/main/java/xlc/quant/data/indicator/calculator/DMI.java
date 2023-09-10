package xlc.quant.data.indicator.calculator;

import java.math.BigDecimal;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import xlc.quant.data.indicator.Indicator;
import xlc.quant.data.indicator.IndicatorCalculator;
import xlc.quant.data.indicator.IndicatorCalculatorCallback;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class DMI extends Indicator {

	/** 又名DI1或者+DI，上升方向线  */
	private BigDecimal dip;
	/** 又名DI2或者-DI，下降方向线 */
	private BigDecimal dim;
	
	/** 动向平均数ADX，趋向平均线 */
	private BigDecimal adx;
	
	/** 评估数值ADXR，趋向评估线 */
	private BigDecimal adxr;

	//辅助计算数字
	/** 真实波动范围（True Range） */
	private BigDecimal tr;

	/** 上升动向（+DM）dmPlus */
	private BigDecimal dmp;

	/** 下降动向（-DM）dmMinus */
	private BigDecimal dmm;

	/** 动向指数DX */
	private BigDecimal dx;

	/**
	 * @param tr  真实波动范围（True Range）
	 * @param dmp 上升动向（+DM）dmPlus
	 * @param dmm 下降动向（-DM）dmMinus
	 */
	public DMI(BigDecimal tr, BigDecimal dmp, BigDecimal dmm) {
		super();
		this.tr = tr;
		this.dmp = dmp;
		this.dmm = dmm;
	}

	//=============
	//内部类分隔符 XXX
	//=============
	/**
	 * 构建-计算器
	 * @param diPeriod
	 * @param adxPeriod
	 * @return
	 */
	public static IndicatorCalculator<DMI> buildCalculator(int diPeriod, int adxPeriod) {
		return new DMICalculator(diPeriod, adxPeriod);
	}

	/**
	 * 内部类实现DMI计算器
	 * @author Rootfive
	 */
	private static class DMICalculator extends IndicatorCalculator<DMI> {
		/** 正整数：2 */
		private static final BigDecimal INT_2 = new BigDecimal(2);
		//常量 XXX==========分隔符号
		
		
		/** 趋向周期 */
		private final int adxPeriod;
		private final BigDecimal adxPeriodDecimal;

		/**
		 * @param diPeriod 动向周期
		 * @param adxPeriod 趋向周期
		 */
		DMICalculator(int diPeriod, int adxPeriod) {
			super(diPeriod, false);
			this.adxPeriod = adxPeriod;
			this.adxPeriodDecimal = new BigDecimal(adxPeriod);
		}

		
		/**
		 *  百度百科：https://baike.baidu.com/item/DMI%E6%8C%87%E6%A0%87/3423254#4
		 */
		@Override
		protected DMI executeCalculate() {
			IndicatorCalculatorCallback<DMI> prev = getPrev();
			IndicatorCalculatorCallback<DMI> head = getHead();

			BigDecimal tr = null;
			BigDecimal dmp = null;
			BigDecimal dmm = null;
			if (prev == null) {
				tr = head.getHigh().subtract(head.getLow());
				dmp = BigDecimal.ZERO;
				dmm = BigDecimal.ZERO;
			} else {
				BigDecimal highLowDiff = head.getHigh().subtract(head.getLow());
				BigDecimal highPrevCloseDiff = head.getHigh().subtract(head.getPreClose());
				BigDecimal lowPrevCloseDiff = head.getLow().subtract(head.getPreClose());

				tr = highLowDiff.max(highPrevCloseDiff.abs()).max(lowPrevCloseDiff.abs());

				BigDecimal highMinusHighPrev = head.getHigh().subtract(prev.getHigh());
				dmp = highMinusHighPrev.max(BigDecimal.ZERO);

				BigDecimal LowPrevMinusLow = prev.getLow().subtract(head.getLow());
				dmm = LowPrevMinusLow.max(BigDecimal.ZERO);

				int compareTo = dmp.compareTo(dmm);
				if (compareTo > 0) {
					dmm = BigDecimal.ZERO;
				} else if (dmp.compareTo(dmm) < 0) {
					dmp = BigDecimal.ZERO;
				}
			}

			DMI dmi = new DMI(tr, dmp, dmm);
			getHead().setIndicator(dmi);
		
			
			BigDecimal trSum = super.getCalculatorDataList().stream().map(IndicatorCalculatorCallback::getIndicator)
					.map(DMI::getTr).reduce(BigDecimal.ZERO, BigDecimal::add);
			BigDecimal dmpSum = super.getCalculatorDataList().stream().map(IndicatorCalculatorCallback::getIndicator)
					.map(DMI::getDmp).reduce(BigDecimal.ZERO, BigDecimal::add);
			BigDecimal dmmSum = super.getCalculatorDataList().stream().map(IndicatorCalculatorCallback::getIndicator)
					.map(DMI::getDmm).reduce(BigDecimal.ZERO, BigDecimal::add);

			BigDecimal dip = divideByPct(dmpSum, trSum);
			BigDecimal dim = divideByPct(dmmSum, trSum);
			dmi.setDip(dip);
			dmi.setDim(dim);

			//依据DI值可以计算出DX指标值。其计算方法是将+DI和—DI间的差的绝对值除以总和的百分比得到动向指数DX
			BigDecimal diDiff = dip.subtract(dim).abs();
			BigDecimal diSum = dip.add(dim);
			BigDecimal dx = BigDecimal.ZERO;
			if (diSum.compareTo(BigDecimal.ZERO) > 0) {
				dx = divideByPct(diDiff, diSum);
			}
			
			dmi.setDx(dx);

			if (executeTotal <= adxPeriod) {
				return dmi;
			}
			
			BigDecimal adx = null;
			BigDecimal dxSum = super.getCalculatorDataList(adxPeriod).stream()
					.map(IndicatorCalculatorCallback::getIndicator).map(DMI::getDx).reduce(BigDecimal.ZERO,BigDecimal::add);
			adx = divide(dxSum, adxPeriodDecimal, 2);
			
			if (isFullCapacity()) {
			
			}
			dmi.setAdx(adx);

			BigDecimal adxr = null;
			BigDecimal adxByPrevAdxPeriod = getPrevByNum(adxPeriod).getIndicator().getAdx();
			if (adxByPrevAdxPeriod != null) {
				adxr = divide(adx.add(adxByPrevAdxPeriod), INT_2, 2);
				average(2, adx,adxByPrevAdxPeriod);
			}
			dmi.setAdxr(adxr);

			return dmi;
		}
	}

}
