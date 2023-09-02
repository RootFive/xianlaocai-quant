package xlc.quant.data.indicator.calculator;

import java.math.BigDecimal;
import java.math.RoundingMode;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import xlc.quant.data.indicator.Indicator;
import xlc.quant.data.indicator.IndicatorCarrier;

/**
 * @author Rootfive 百度百科：https://blog.csdn.net/zkxshg/article/details/86546159
 *  求X的N日指数平滑移动平均，
 *  在股票公式中一般表达为：EMA（X，N）。
 *  其中：
 *  	X为当日收盘价，
 *  	N为天数。 
 *  	当日指数平均值 = 平滑系数 * （当日指数值 - 昨日指数平均值） + 昨日指数平均值 ； 
 *  	平滑系数 = 2 /（周期单位+1）； 
 *  由以上公式推导开，得到：EMA(N) = 2 * X / (N+1) + (N-1) * EMA (N-1) / (N+1)。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class EMA extends Indicator {

	private BigDecimal value;

	//=============
	//内部类分隔符 XXX
	//=============
	/**
	 * 构建-计算器
	 * @param capacity
	 * @return
	 */
	public static IndicatorCalculator<EMA> buildCalculator(int capacity) {
		return new EMACalculator(capacity);
	}

	/**
	 * 
	 * @author Rootfive
	 */
	private static class EMACalculator extends IndicatorCalculator<EMA> {

		/** 平滑系数的分子。 α为平滑系数，设定值为（2/n+1); */
		private final BigDecimal α;

		/**
		 * @param capacity
		 */
		public EMACalculator(int capacity) {
			super(capacity, false);
			this.α = divide(INT_2, new BigDecimal(capacity + 1), 4);
		}

		@Override
		protected EMA executeCalculate() {
			IndicatorCarrier<EMA> head = getHead();
			// 前一个计算指数
			IndicatorCarrier<EMA> prev = get(2);
			BigDecimal emaValue = null;
			if (prev == null) {
				/*
				 * 由于第一个EMA1是没有定义的。EMA1的取值有几种不同的方法，
				 * 通常情况下取EMA1为Price1，另外有的技术是将EMA1取值为开头4到5个数值的均值。 我们这里取前，中价格，中价等于最高价、最低价和收盘价之和除以3
				 */
				emaValue = divide((head.getHigh().add(head.getLow()).add(head.getClose())),INT_3, 8);
			} else {
				// 当前使用价格
				BigDecimal headClose = head.getClose();
				/*
				 * EMA指标英文全称ExponentialMovingAverage，中文全称是指数平滑移动平均线，简称指数平均线。
				 * EMA也是一种趋向类指标，本义是以指数式递减加权的移动平均。在炒股软件中，该指标的数值多用曲线表示，所以称为指数移动平均线。
				 * 其基本计算公式为：EMA(n)=α×Cn+(1-α)×EMA(n-1) = α×[Cn-EMA(n-1)]+EMA(n-1) 其中：
				 * EMA(n)为第n日EMA; α为平滑系数，设定值为（2/n+1); Cn为第n日收盘价； EMA(n-1)为第n-1日EMA。 平滑系数
				 * 其基本计算公式为：EMA(n)=α×Cn+(1-α)×EMA(n-1)
				 */
				BigDecimal prevEmaValue = prev.getIndicator().getValue();
				emaValue = (headClose.subtract(prevEmaValue)).multiply(α).add(prevEmaValue).setScale(2,RoundingMode.HALF_UP);
						
			}
			return new EMA(emaValue);
		}

	}

}
