package xlc.quant.data.indicator.calculator;

import java.util.function.Function;

import lombok.Data;
import lombok.EqualsAndHashCode;
import xlc.quant.data.indicator.Indicator;
import xlc.quant.data.indicator.IndicatorCalculator;
import xlc.quant.data.indicator.IndicatorCalculateCarrier;


/**
 * 源自于TD序列, 神奇九转，九转序列,是因TD序列的9天收盘价研判标准而得名。
 * @author Rootfive 
 * 
 * <pre>
 * https://zhuanlan.zhihu.com/p/354262959
 * 好文：https://www.cnblogs.com/proxukun/p/4988771.html
 * </pre>
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TD extends Indicator {

	//=============
	//内部类分隔符 XXX
	//=============
	/**
	 * 构建-计算器-====== XXX 
	 * @param capacity
	 * @return
	 */
	public static <CARRIER extends IndicatorCalculateCarrier<?>> IndicatorCalculator<CARRIER, Integer> buildCalculator(int capacity, int moveSize) {
		return new TDCalculator<>(capacity, moveSize);
	}


	/**
	 * 计算器
	 * @author Rootfive
	 */
	private static class TDCalculator<CARRIER extends IndicatorCalculateCarrier<?>> extends IndicatorCalculator<CARRIER, Integer> {

		private final int moveSize;

		/**
		 * @param capacity
		 * @param isFullCapacityCalculate
		 */
		public TDCalculator(int capacity, int moveSize) {
			super(capacity, false);
			this.moveSize = moveSize;
		}

		@Override
		protected Integer executeCalculate(Function<CARRIER, Integer> propertyGetter) {
			if (size() < (1 + moveSize)) {
				return null;
			}

			// 第一根
			CARRIER head = getHead();
			CARRIER compareMove = get(moveSize);
			CARRIER prev = getPrev();

			double currentClose = head.getClose();
			double compareMoveClose = compareMove.getClose();

			return getCurrentTD(currentClose, compareMoveClose, propertyGetter.apply(prev));
		}

		private static int getCurrentTD(Double current, Double prev, Integer preTD) {
			if (current == null || prev == null || preTD == null) {
				return 0;
			}
			
			int compareResult = current.compareTo(prev);
			if (compareResult > 0) {
				if (preTD > 0) {
					return preTD + 1;
				} else {
					return 1;
				}
			} else if (compareResult < 0) {
				if (preTD >= 0) {
					return -1;
				} else {
					return preTD - 1;
				}
			}
			return 0;
		}
	}

}
