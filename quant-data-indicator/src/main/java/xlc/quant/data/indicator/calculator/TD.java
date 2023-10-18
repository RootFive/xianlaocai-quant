package xlc.quant.data.indicator.calculator;

import java.util.function.Consumer;
import java.util.function.Function;

import lombok.Data;
import lombok.EqualsAndHashCode;
import xlc.quant.data.indicator.Indicator;
import xlc.quant.data.indicator.IndicatorCalculator;
import xlc.quant.data.indicator.IndicatorComputeCarrier;


/**
 * 源自于TD序列, 神奇九转，九转序列,是因TD序列的9天收盘价研判标准而得名。
 * 
 * @author Rootfive 
 * https://zhuanlan.zhihu.com/p/354262959
 *         
 * 好文：https://www.cnblogs.com/proxukun/p/4988771.html
 * 
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
	@SuppressWarnings("rawtypes")
	public static <C extends IndicatorComputeCarrier> IndicatorCalculator<C, Integer> buildCalculator(int capacity, int moveSize) {
		return new TDCalculator<>(capacity, moveSize);
	}


	/**
	 * 计算器
	 * @author Rootfive
	 */
	private static class TDCalculator<C extends IndicatorComputeCarrier<?>> extends IndicatorCalculator<C, Integer> {

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
		protected Integer executeCalculate(Function<C, Integer> propertyGetter,Consumer<Integer> propertySetter) {
			if (executeTotal < (1 + moveSize)) {
				return null;
			}

			// 第一根
			C current = getHead();
			C compareMove = getPrevByNum(moveSize);
			C prev = getPrev();

			double currentClose = current.getClose();
			double compareMoveClose = compareMove.getClose();

			int tdValue = getCurrentTD(currentClose, compareMoveClose, propertyGetter.apply(prev));
			//设置计算结果
			propertySetter.accept(tdValue);
			return tdValue;
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
