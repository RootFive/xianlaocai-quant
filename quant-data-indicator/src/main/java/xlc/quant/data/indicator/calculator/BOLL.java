package xlc.quant.data.indicator.calculator;

import java.math.BigDecimal;
import java.math.RoundingMode;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import xlc.quant.data.indicator.Indicator;
import xlc.quant.data.indicator.IndicatorCalculator;
import xlc.quant.data.indicator.IndicatorCalculatorCallback;

/**
 * @author Rootfive 布林线指标
 *         https://baike.baidu.com/item/%E5%B8%83%E6%9E%97%E7%BA%BF%E6%8C%87%E6%A0%87/3325894
 * 公式说明：
 * 	布林线指标的参数最好设为20,以日BOLL指标计算为例，其计算方法如下： 日BOLL指标的计算公式： 中轨线=N日的移动平均线
 * 上轨线=中轨线+两倍的标准差 下轨线=中轨线－两倍的标准差
 * 
 * 公式说明: 日BOLL指标的计算过程 1）计算MA： MA=N日内的收盘价之和÷N
 * 2）计算标准差MD：MD=平方根（N-1）日的（C－MA）的两次方之和除以N。（C指收盘价） 3）计算MB、UP、DN线 MB=N日的MA
 * UP=MB+k×MD DN=MB－k×MD
 * 各大股票交易软件默认N是20，所以MB等于当日20日均线值，（K为参数，可根据股票的特性来做相应的调整，一般默认为2）
 * 
 * 在股市分析软件中，BOLL指标一共由四条线组成，即上轨线UP 、中轨线MB、下轨线DN和价格线。 其中上轨线UP是UP数值的连线，用黄色线表示；
 * 中轨线MB是MB数值的连线，用白色线表示； 下轨线DN是DN数值的连线，用紫色线表示； 价格线是以美国线表示，颜色为浅蓝色。
 * 
 * 和其他技术指标一样，在实战中，投资者不需要进行BOLL指标的计算，主要是了解BOLL的计算方法和过程，以便更加深入地掌握BOLL指标的实质，为运用指标打下基础。
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class BOLL extends Indicator {

	/** 上轨（Upper Band）：也称为上限线，表示价格的上限或压力区域。 */
	private BigDecimal u;

	/** 中轨（Middle Band）：也称为中线，表示价格的平均线或基准线。 */
	private BigDecimal m;

	/** 下轨（Lower Band）：也称为下限线，表示价格的下限或支撑区域。 */
	private BigDecimal d;

	/** BOLL连续扩大 */
	private Integer continueExpand;

	public BOLL(BigDecimal u, BigDecimal m, BigDecimal d, Integer continueExpand) {
		super();
		this.u = u;
		this.m = m;
		this.d = d;
		this.continueExpand = continueExpand;
	}

	//=============
	//内部类分隔符 XXX
	//=============
	/**
	 * 构建计算器
	 * @param capacity
	 * @param k
	 * @return
	 */
	public static IndicatorCalculator<BOLL> buildCalculator(int capacity, BigDecimal k) {
		return new BOLLCalculator(capacity, k);
	}

	/**
	 * BOLL计算器
	 * @author Rootfive
	 */
	private static class BOLLCalculator extends IndicatorCalculator<BOLL> {
		/** K为参数，可根据股票的特性来做相应的调整，一般默认为2 */
		private static final BigDecimal DEFAULT_K = new BigDecimal(2);
		/** 布林线指标的参数N,指的是K线的个数,默认20 */
		private static final int DEFAULT_PERIOD = 20;
		
		
		/** MA计算器  */
		private final IndicatorCalculator<MA> maCalculator;

		/** K为参数，可根据股票的特性来做相应的调整，一般默认为2 */
		private final BigDecimal k;

		/**
		 * 各大股票交易软件默认N是20，所以MB等于当日20日均线值，（K为参数，可根据股票的特性来做相应的调整，一般默认为2）
		 * 
		 * @param period 布林线指标的参数N,指的是K线的个数,默认20
		 * @param k        K为参数，可根据股票的特性来做相应的调整，一般默认为2
		 */
		BOLLCalculator(int period, BigDecimal k) {
			super((period <= 0 ? DEFAULT_PERIOD : period), false);
			this.maCalculator = MA.buildCalculator(period <= 0 ? DEFAULT_PERIOD : period);
			this.k = (k == null || k.longValue() <= 0) ? DEFAULT_K : k;
		}

		@Override
		protected BOLL executeCalculate() {
			IndicatorCalculatorCallback<BOLL> head = getHead();
			// 1）计算MA: MA=N日内的收盘价之和÷N

			// 收盘价 平均值
			MA MA = maCalculator.input(new IndicatorCalculatorCallback<MA>(head));
			// BOLL线
			if (!isFullCapacity()) {
				return null;
			}
			BigDecimal maValue = MA.getValue();

			// 2）计算标准差MD MD=平方根（N-1）日的（C－MA）的两次方之和除以N
			// 2.1 计算方差
			BigDecimal varianceS2 = BigDecimal.ZERO;
			for (IndicatorCalculatorCallback<BOLL> m : super.getCalculatorDataList()) {
				// 差值
				BigDecimal DValue = m.getClose().subtract(MA.getValue());
				// 差值的平方只和
				varianceS2 = varianceS2.add((DValue).multiply(DValue));
			}
			// 方差
			BigDecimal variance = varianceS2.divide(fwcPeriod, 4, RoundingMode.HALF_UP);
			// 标准差
			BigDecimal MD = new BigDecimal(Math.sqrt(variance.doubleValue()));

			/*
			 * 保留2位小数,（K为参数，可根据股票的特性来做相应的调整，一般默认为2） MB=N日的MA UP=MB+k×MD DN=MB－k×MD
			 */
			BigDecimal MB = maValue.setScale(2, RoundingMode.HALF_UP);
			BigDecimal kMD = MD.multiply(k);
			BigDecimal UP = MB.add(kMD).setScale(2, RoundingMode.HALF_UP);
			BigDecimal DN = MB.subtract(kMD).setScale(2, RoundingMode.HALF_UP);

			getPrev().getIndicator();
			BOLL prevBoll = getPrev().getIndicator();
			BOLL boll = null;
			if (prevBoll == null) {
				boll = new BOLL(UP, MB, DN, 0);
			} else {
				// BOLL通道连续扩大
				Integer bollContinueExpand = getContinueValue(balance(UP, DN),balance(prevBoll.getU(), prevBoll.getD()), prevBoll.getContinueExpand());
				boll = new BOLL(UP, MB, DN, bollContinueExpand);
			}
			return boll;
		}

	}

}
