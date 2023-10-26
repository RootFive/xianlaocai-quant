package xlc.quant.data.indicator.calculator;

import java.util.function.BiConsumer;

import lombok.Data;
import lombok.EqualsAndHashCode;
import xlc.quant.data.indicator.Indicator;
import xlc.quant.data.indicator.IndicatorCalculateCarrier;
import xlc.quant.data.indicator.IndicatorCalculator;
import xlc.quant.data.indicator.util.DoubleUtils;

/**
* @author Rootfive
* 百度百科：BIAS 乖离率=（当日收盘价-N日内移动平均价）/N日内移动平均价╳100%
* <pre>
* 乖离率又称为y值，是反映股价在波动过程中与移动平均线偏离程度的技术指标。
* 它的理论基础是：不论股价在移动平均线之上或之下，只要偏离距离过远，就会向移动平均线趋近，
* 据此计算股价偏离移动平均线百分比的大小来判断买卖时机。
* 由于股价相对于不同日数的移动平均线有不同的乖离率，除去暴涨或暴跌会使乖离率瞬间达到高百分比外，短、中、长线的乖离率一般均有规律可循。
* <p>	
* 下面是国外不同日数移动平均线达到买卖讯事号要求的参考数据：
* 		6日平均值乖离：－3%是买进时机，+3．5是卖出时机；
* 		12日平均值乖离：－4．5%是买进时机，+5%是卖出时机；
* 		24日平均值乖离：－7%是买进时机，+8%是卖出时机；
* 		72日平均值乖离：－11%是买进时机，+11%是卖出时机
* </pre>
*/
@Data
@EqualsAndHashCode(callSuper = true)
public class BIAS extends Indicator {
	//=============
	//内部类分隔符 XXX
	//=============
	/**
	 * @param <CARRIER>
	 * @param capacity
	 * @param propertySetter
	 * @return
	 */
	public static <CARRIER extends IndicatorCalculateCarrier<?>> IndicatorCalculator<CARRIER,Double> buildCalculator(int capacity, BiConsumer<CARRIER, Double> propertySetter) {
		return new BIASCalculator<>(capacity,propertySetter);
	}

	/**
	 * BIAS-计算器
	 * 
	 * @author Rootfive
	 */
	private static class BIASCalculator<CARRIER extends IndicatorCalculateCarrier<?>> extends IndicatorCalculator<CARRIER,Double> {

		/**
		 * @param capacity
		 * @param propertySetter
		 */
		BIASCalculator(int capacity, BiConsumer<CARRIER, Double> propertySetter) {
			super(capacity, true,propertySetter);
		}

		@Override
		protected Double executeCalculate() {
			CARRIER head = getHead();
			// 当前使用价格
			double currentUsePrice = head.getClose();
			double sumValue = head.getClose();
			for (int i = 1; i < capacity(); i++) {
				sumValue = sumValue+get(i).getClose();
			}
			// 缓冲区内所有 平均值
			Double ma = DoubleUtils.divide(sumValue, capacity(), DoubleUtils.MAX_SCALE);
			// 计算公式：BIAS 乖离率=（当日收盘价-N日内移动平均价）/N日内移动平均价╳100%
			return DoubleUtils.divideByPct(currentUsePrice-ma, ma);
			
		}
	}

}
