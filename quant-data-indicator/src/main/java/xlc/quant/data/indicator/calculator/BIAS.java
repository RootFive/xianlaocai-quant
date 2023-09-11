package xlc.quant.data.indicator.calculator;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import xlc.quant.data.indicator.Indicator;
import xlc.quant.data.indicator.IndicatorCalculator;
import xlc.quant.data.indicator.IndicatorCalculatorCallback;

/**
* @author Rootfive
* 百度百科：BIAS 乖离率=（当日收盘价-N日内移动平均价）/N日内移动平均价╳100%
* 
* 乖离率又称为y值，是反映股价在波动过程中与移动平均线偏离程度的技术指标。
* 它的理论基础是：不论股价在移动平均线之上或之下，只要偏离距离过远，就会向移动平均线趋近，
* 据此计算股价偏离移动平均线百分比的大小来判断买卖时机。
* 由于股价相对于不同日数的移动平均线有不同的乖离率，除去暴涨或暴跌会使乖离率瞬间达到高百分比外，短、中、长线的乖离率一般均有规律可循。
* 下面是国外不同日数移动平均线达到买卖讯事号要求的参考数据：
* 6日平均值乖离：－3%是买进时机，+3．5是卖出时机；
* 12日平均值乖离：－4．5%是买进时机，+5%是卖出时机；
* 24日平均值乖离：－7%是买进时机，+8%是卖出时机；
* 72日平均值乖离：－11%是买进时机，+11%是卖出时机
*/
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class BIAS extends Indicator {

	private BigDecimal value;

	//=============
	//内部类分隔符 XXX
	//=============
	/**
	 * 构建计算器
	 * 
	 * @param capacity
	 * @return
	 */
	public static IndicatorCalculator<BIAS> buildCalculator(int capacity) {
		return new BIASCalculator(capacity);
	}

	/**
	 * BIAS-计算器
	 * 
	 * @author Rootfive
	 */
	private static class BIASCalculator extends IndicatorCalculator<BIAS> {

		/**
		 * @param capacity
		 */
		BIASCalculator(int capacity) {
			super(capacity, true);
		}

		@Override
		protected BIAS executeCalculate() {
			IndicatorCalculatorCallback<BIAS> head = getHead();
			// 当前使用价格
			BigDecimal currentUsePrice = null;
			// 缓冲区内所有 平均值
			BigDecimal ma = null;
			if (isFullCapacity()) {
				currentUsePrice = head.getClose();
				BigDecimal sumValue = super.getCalculatorDataList().stream().map(IndicatorCalculatorCallback::getClose).reduce(BigDecimal::add).get();
				ma = divide(sumValue, fwcPeriod, 2);
			}
			
			// 计算公式：BIAS 乖离率=（当日收盘价-N日内移动平均价）/N日内移动平均价╳100%
			BigDecimal biasValue = divideByPct(currentUsePrice.subtract(ma), ma);
			return new BIAS(biasValue);
		}
	}

}
