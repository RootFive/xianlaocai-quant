package xlc.quant.data.indicator.calculator.innovate;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import xlc.quant.data.indicator.Indicator;
import xlc.quant.data.indicator.IndicatorCalculateCarrier;
import xlc.quant.data.indicator.IndicatorCalculator;

/**
 * @author Rootfive XLC-量价形态
 * <pre>
 * 公式说明：量价连续值
 * </pre>
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class XlcQPCV extends Indicator {

	/** K线-连续上阳 */
	private int yang;
	
	/** 收盘价-连续上涨 */
	private int cRise;
	
	/** 交易量-连续上涨 */
	private int vRise;
	
	/** 交易额-连续上涨 */
	private int aRise;


	public XlcQPCV(int yang, int cRise, int vRise, int aRise) {
		super();
		this.yang = yang;
		this.cRise = cRise;
		this.vRise = vRise;
		this.aRise = aRise;
	}


	//=============
	//内部类分隔符 XXX
	//=============
	/**
	 * 构建计算器
	 * @param capacity
	 * @param k
	 * @param indicatorSetScale        指标精度
	 * @return
	 */
	public static <CARRIER extends IndicatorCalculateCarrier<?>> IndicatorCalculator<CARRIER,XlcQPCV> buildCalculator(BiConsumer<CARRIER, XlcQPCV> propertySetter,Function<CARRIER, XlcQPCV> propertyGetter) {
		return new XlcQPCVCalculator<>(propertySetter,propertyGetter);
	}



	private static class XlcQPCVCalculator<CARRIER extends IndicatorCalculateCarrier<?>> extends IndicatorCalculator<CARRIER,XlcQPCV> {
		
		/** 委托方法，从载体类获取计算结果的方法 */
		private final Function<CARRIER, XlcQPCV> propertyGetter;
		
		
		XlcQPCVCalculator(BiConsumer<CARRIER, XlcQPCV> propertySetter,Function<CARRIER, XlcQPCV> propertyGetter) {
			super(2, true,propertySetter);
			this.propertyGetter = propertyGetter;
		}

		@Override
		protected XlcQPCV executeCalculate() {
			CARRIER head = getHead();
			CARRIER prev = getPrev();
			
			XlcQPCV prevQPCV = Optional.ofNullable(propertyGetter.apply(prev)).orElse(new XlcQPCV()); 
			
			/** K线-连续上阳 */
			int yang = getContinueValue2(head.getClose(), head.getOpen(), prevQPCV.getYang());
			/** 收盘价-连续上涨 */
			int cRise= getContinueValue2(head.getClose(), head.getPreClose(), prevQPCV.getCRise());
			/** 交易量-连续上涨 */
			int vRise= getContinueValue2(head.getVolume(), prev.getVolume(), prevQPCV.getVRise());
			/** 交易额-连续上涨 */
			int aRise= getContinueValue2(head.getAmount(), prev.getAmount(), prevQPCV.getARise());
			
			return new XlcQPCV(yang, cRise, vRise, aRise);
		}
		
		/**
		 * @param current  当前值
		 * @param prev	   前值	
		 * @param preContinueValue  前连续值
		 * @return
		 */
		public static int getContinueValue2(Double current, Double prev, int preContinueValue) {
			int compareResult = current.compareTo(prev);
			switch (compareResult) {
			case 1:
				//1,current [>] prev
				if (preContinueValue > 0) {
					//前值 > 0
					return preContinueValue + 1;
				} else if (preContinueValue == 0) {
					//前值 = 0
					return 1;
				} else {
					//前值 < 0
					return 1;
				}
			case 0:
				//0,current [=] prev
				return 0;
			default:
				//-1,current [<] prev
				if (preContinueValue > 0) {
					//前值 > 0
					return -1;
				} else if (preContinueValue == 0) {
					//前值 = 0
					return -1;
				} else {
					//前值 < 0
					return preContinueValue - 1;
				}
			}
		}
		
		
		

	}

}
