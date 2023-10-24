package xlc.quant.data.indicator.calculator;

import java.util.function.Function;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import xlc.quant.data.indicator.Indicator;
import xlc.quant.data.indicator.IndicatorCalculator;
import xlc.quant.data.indicator.IndicatorCalculateCarrier;
import xlc.quant.data.indicator.util.DoubleUtils;

/**
 * @author Rootfive 布林线指标
 * <pre>
 * 公式说明：
 * 	布林线指标的参数最好设为20,以日BOLL指标计算为例，其计算方法如下： 日BOLL指标的计算公式： 中轨线=N日的移动平均线
 * 上轨线=中轨线+两倍的标准差 下轨线=中轨线－两倍的标准差
 * <p>
 * 公式说明: 日BOLL指标的计算过程 1）计算MA： MA=N日内的收盘价之和÷N
 * 2）计算标准差MD：MD=平方根（N-1）日的（C－MA）的两次方之和除以N。（C指收盘价） 3）计算MB、UP、DN线 MB=N日的MA
 * UP=MB+k×MD DN=MB－k×MD
 * 各大股票交易软件默认N是20，所以MB等于当日20日均线值，（K为参数，可根据股票的特性来做相应的调整，一般默认为2）
 * 
 * 在股市分析软件中，BOLL指标一共由四条线组成，即上轨线UP 、中轨线MB、下轨线DN和价格线。 其中上轨线UP是UP数值的连线，用黄色线表示；
 * 中轨线MB是MB数值的连线，用白色线表示； 下轨线DN是DN数值的连线，用紫色线表示； 价格线是以美国线表示，颜色为浅蓝色。
 * 
 * 和其他技术指标一样，在实战中，投资者不需要进行BOLL指标的计算，主要是了解BOLL的计算方法和过程，以便更加深入地掌握BOLL指标的实质，为运用指标打下基础。
 * </pre>
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class BOLL extends Indicator {

	/** 上轨（Upper Band）：也称为上限线，表示价格的上限或压力区域。 */
	private Double u;

	/** 中轨（Middle Band）：也称为中线，表示价格的平均线或基准线。 */
	private Double m;

	/** 下轨（Lower Band）：也称为下限线，表示价格的下限或支撑区域。 */
	private Double d;

	/** BOLL连续扩大 */
	private Integer continueExpand;

	public BOLL(Double u, Double m, Double d, Integer continueExpand) {
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
	 * @param indicatorSetScale        指标精度
	 * @return
	 */
	public static <CARRIER extends IndicatorCalculateCarrier<?>> IndicatorCalculator<CARRIER,BOLL> buildCalculator(int capacity, double k,int indicatorSetScale) {
		return new BOLLCalculator<>(capacity, k, indicatorSetScale);
	}

	/**
	 * BOLL计算器
	 * @author Rootfive
	 */
	private static class BOLLCalculator<CARRIER extends IndicatorCalculateCarrier<?>> extends IndicatorCalculator<CARRIER,BOLL> {
		/** K为参数，可根据股票的特性来做相应的调整，一般默认为2 */
		private static final double DEFAULT_K = 2;
		/** 布林线指标的参数N,指的是K线的个数,默认20 */
		private static final int DEFAULT_PERIOD = 20;
		
		/** K为参数，可根据股票的特性来做相应的调整，一般默认为2 */
		private final Double k;
		
		/** 指标精度 */
		private final int indicatorSetScale;

		/**
		 * 各大股票交易软件默认N是20，所以MB等于当日20日均线值，（K为参数，可根据股票的特性来做相应的调整，一般默认为2）
		 * 
		 * @param period 布林线指标的参数N,指的是K线的个数,默认20
		 * @param k        K为参数，可根据股票的特性来做相应的调整，一般默认为2
		 * @param indicatorSetScale        指标精度
		 */
		BOLLCalculator(int period, double k,int indicatorSetScale) {
			super((period <= 0 ? DEFAULT_PERIOD : period), true);
			this.k =  k <= 0 ? DEFAULT_K : k;
			this.indicatorSetScale =  indicatorSetScale;
		}

		@Override
		protected BOLL executeCalculate(Function<CARRIER, BOLL> propertyGetter) {
			CARRIER head = getHead();
			// 1）计算MA: MA=N日内的收盘价之和÷N

			// 求收盘价 平均值
			double sumClose = head.getClose();
			for (int i = 1; i < capacity(); i++) {
				sumClose =  sumClose+ get(i).getClose();
			}
			// 收盘价 平均值
			Double closeMA = sumClose/capacity();
			
			// BOLL线
			if (!isFull()) {
				return null;
			}

			// 2）计算标准差MD MD=平方根（N-1）日的（C－MA）的两次方之和除以N
			// 2.1 计算方差
			Double varianceS2 = DoubleUtils.ZERO;
			
			for (int i = 0; i < capacity(); i++) {
				CARRIER carrier_i = get(i);
				// 差值
				Double DValue = carrier_i.getClose() -closeMA;
				// 差值的平方只和
				varianceS2 = varianceS2 + DValue * DValue;
			}
			
			// 方差
			Double variance = DoubleUtils.divide(varianceS2,capacity(), DoubleUtils.MAX_SCALE);
			// 标准差
			Double MD = Math.sqrt(variance);

			/*
			 * （K为参数，可根据股票的特性来做相应的调整，一般默认为2） MB=N日的MA UP=MB+k×MD DN=MB－k×MD
			 */
			Double MB = DoubleUtils.setScale(closeMA, indicatorSetScale);
			Double UP = DoubleUtils.setScale(MB+k * MD, indicatorSetScale);
			Double DN = DoubleUtils.setScale(MB-k * MD, indicatorSetScale);
			
			BOLL prevBoll = propertyGetter.apply(getPrev());
			BOLL boll = null;
			if (prevBoll == null) {
				boll = new BOLL(UP, MB, DN, 0);
			} else {
				// BOLL通道连续扩大
				Integer bollContinueExpand = getContinueValue(UP - DN,prevBoll.getU()- prevBoll.getD(), prevBoll.getContinueExpand());
				boll = new BOLL(UP, MB, DN, bollContinueExpand);
			}
			return boll;
		}

	}

}
