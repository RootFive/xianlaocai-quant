package xlc.quant.data.indicator.demo;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class CandlestickByLocalDateTime  implements CircularFixedWindowCalculable<LocalDateTime > {

	/** 开盘时间 	*/
	private LocalDateTime openTime;

	/** 收盘时间 */
	private LocalDateTime closeTime;

	/** 交易时间 */
	private LocalDateTime tradeTime;
	
	/** 交易代码 */
	private String symbol;

	/** 开盘价 */
	private double open;

	/** 最低价 */
	private double low;

	/** 最高价 */
	private double high;

	/**  收盘价(当前K线未结束的即为最新交易价) */
	private double close;

	/**  成交量 */
	private long volume;

	/**  成交额  */
	private double amount;

	
	// =======================
	// 上面的属性值，一般情况下，分时和日行情都有
	// 下面的属性值，一般情况下，分时和日行情可能有，即便是没有，也可以通过上面的属性计算得出
	// =======================
	
	/** 前收 (元) */
	private Double preClose;

	/** 前收 涨跌额(元) */
	private Double priceChange;

	/** 涨跌幅（%） */
	private Double pctChange;

	/** 价格震幅（%） */
	private Double amplitude;
	
}
