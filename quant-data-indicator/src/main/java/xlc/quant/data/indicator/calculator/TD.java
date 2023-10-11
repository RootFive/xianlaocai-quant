package xlc.quant.data.indicator.calculator;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import xlc.quant.data.indicator.Indicator;
import xlc.quant.data.indicator.IndicatorCalculator;
import xlc.quant.data.indicator.IndicatorCalculatorCallback;


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
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TD extends Indicator {

	/** TD序列值 */
	private Integer value;

	//=============
	//内部类分隔符 XXX
	//=============
	/**
	 * 构建-计算器-====== XXX 
	 * @param capacity
	 * @return
	 */
	public static IndicatorCalculator<TD> buildCalculator(int capacity, int moveSize) {
		return new TDCalculator(capacity, moveSize);
	}


	/**
	 * 计算器
	 * @author Rootfive
	 */
	private static class TDCalculator extends IndicatorCalculator<TD> {

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
		protected TD executeCalculate() {
			List<IndicatorCalculatorCallback<TD>> calculatorListData = getCalculatorDataList();

			if (calculatorListData.size() < (1 + moveSize)) {
				return null;
			}

			// 第一根
			IndicatorCalculatorCallback<TD> current = getHead();
			IndicatorCalculatorCallback<TD> compareMove = getPrevByNum(moveSize);
			IndicatorCalculatorCallback<TD> prev = getPrev();

			double currentClose = current.getClose();
			double compareMoveClose = compareMove.getClose();

			int tdValue = getCurrentTD(currentClose, compareMoveClose, prev.getIndicator());
			return new TD(tdValue);
		}

		private static int getCurrentTD(Double current, Double prev, TD preTD) {
			if (current == null || prev == null || preTD == null) {
				return 0;
			}
			int preContinueValue = preTD.getValue();
			int compareResult = current.compareTo(prev);
			if (compareResult > 0) {
				if (preContinueValue > 0) {
					return preContinueValue + 1;
				} else {
					return 1;
				}
			} else if (compareResult < 0) {
				if (preContinueValue >= 0) {
					return -1;
				} else {
					return preContinueValue - 1;
				}
			}
			return 0;
		}
	}

}
