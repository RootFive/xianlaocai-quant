package xlc.quant.data.indicator.calculator;

import java.util.Comparator;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import xlc.quant.data.indicator.Indicator;
import xlc.quant.data.indicator.IndicatorCalculator;
import xlc.quant.data.indicator.IndicatorCalculatorCallback;
import xlc.quant.data.indicator.util.DoubleUtils;

/**
 * 威廉指标
 * @author Rootfive
 * 威廉指标（Williams %R或简称W%R）
 * 百度百科：https://baike.baidu.com/item/%E5%A8%81%E5%BB%89%E6%8C%87%E6%A0%87
 * W&R属于摆动类反向指标， 即：修复当股价上涨，W&R指标向下。股价下跌，W&R指标向上，则为上涨。
 * 这种指标的最大的缺点就是买卖过于频繁，因此我们应该通过改变指标的参数来修正指标的缺陷。 主要区别 1.W&R波动于0~100，0
 * 位于顶部，100位于底部。 2.本指标以50为中轴线，高于50视为股价转强；低于50视为股价转弱。
 * 3.本指标高于20后再度向下跌破20卖出；低于80后再度向上突破80买进。
 * 4.W&R连续触顶3~4次，股价向下反转机率大；连续触底3~4次， 股价向上反转机。
 * =======================================================================
 * W&R主要可辅助RSI，确认强转弱或弱转强是否可靠；RSI向上穿越5阴阳分界时，要看W%R是否也向上空越50，如果同步，则可靠。
 * 相反，向下穿越af0时，也是同样的道理。比较两者是否同步时，其设定的参数必须是相对的比例，
 * 大致上W&R5日、10日、20日对应RSI6日、12日、24日，可以依照自己的测试结果，要自行调整其最佳对应比例。
 * W&R表示超买或超卖时，应立即寻求MACD讯号支援。当W&R表示超买时，是作为一种预警作用的效果。
 * ==============================================================================
 * 应用法则 1．当威廉指数线高于85，市场处于超卖状态，行情即将见底。 2．当威廉指数线低于15，市场处于超买状态，行情即将见顶。
 * 3．与相对强弱指数配合使用，可得出对大市走向较为准确的判断。 4．使用威廉指数作为预测市场工具，既不容易错过大的行情，也不容易在高价区套牢。 使用方法
 * 当W&R高于80，即处于超卖状态，行情即将见底，应当考虑买进。 当W&R低于20，即处于超买状态，行情即将见顶，应当考虑卖出。
 * 在W&R进入高位后，一般要回头，如果股价继续上升就产生了背离，是卖出信号。 在W&R进入低位后，一般要反弹，如果股价继续下降就产生了背离。
 * W&R连续几次撞顶（底），局部形成双重或多重顶（底），是卖出（买进）的信号。
 * 同时，使用过程中应该注意与其他技术指标相互配合。在盘整的过程中，W&R的准确性较高，而在上升或下降趋势当中，却不能只以W&R超买超卖信号作为行情判断的依据。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class WR extends Indicator {

	private Double value;

	
	//=============
	//内部类分隔符 XXX
	//=============
	/**
	 * 构建-计算器
	 * @param capacity
	 * @return
	 */
	public static IndicatorCalculator<WR> buildCalculator(int capacity) {
		return new WRIndicatorCalculateExecutor(capacity);
	}

	/**
	 * 计算器
	 * @author Rootfive
	 */
	private static class WRIndicatorCalculateExecutor extends IndicatorCalculator<WR> {

		/**
		 * @param capacity
		 */
		public WRIndicatorCalculateExecutor(int capacity) {
			super(capacity, true);
		}

		@Override
		protected WR executeCalculate() {
			IndicatorCalculatorCallback<WR> head = getHead();

			Double headClose = head.getClose();
			Double maxHigh = null;
			Double minLow = null;

			maxHigh = super.getCalculatorDataList().stream().max(Comparator.comparing(IndicatorCalculatorCallback::getHigh))
					.get().getHigh();
			minLow = super.getCalculatorDataList().stream().min(Comparator.comparing(IndicatorCalculatorCallback::getLow))
					.get().getLow();
			// 计算公式：W%R=（Hn—C）÷（Hn—Ln）×100其中
			Double wrValue = null;
			if (maxHigh.compareTo(minLow) == 0) {
				//连续横盘的极端情况maxHigh=minLow
				wrValue = DoubleUtils.ZERO;
			}else {
				wrValue = DoubleUtils.divideByPct(maxHigh-headClose, maxHigh-minLow);
			}
			return new WR(wrValue);
		}

	}

}
