package xlc.quant.data.indicator.test;

import java.time.LocalDateTime;

import lombok.Data;
import xlc.quant.data.indicator.IndicatorCalculateCarrier;
import xlc.quant.data.indicator.calculator.BOLL;
import xlc.quant.data.indicator.calculator.CCI;
import xlc.quant.data.indicator.calculator.DMI;
import xlc.quant.data.indicator.calculator.KDJ;
import xlc.quant.data.indicator.calculator.MACD;
import xlc.quant.data.indicator.calculator.RSI;

/**
 * 行情数据，股市请使用复权数据
 *  收盘时间和时间戳是泛型可以根据自己的实际情况自定义
 */
@Data
public class MarketQuotation implements IndicatorCalculateCarrier<LocalDateTime> {

	/** 收盘时间 */
	private LocalDateTime closeTime;

	/** 时间戳 */
	private LocalDateTime timestamp;

	/** 交易代码 */
	private String symbol;
	
	/** 开盘价 */
	private double open;

	/** 最低价 */
	private double low;

	/** 最高价 */
	private double high;

	/** 收盘价(当前K线未结束的即为最新交易价) */
	private double close;

	/** 成交量 */
	private double volume;

	/** 成交额 */
	private double amount;

	// =======================
	// 上面的属性值，一般情况下，分时和日行情都有
	// 下面的属性值，一般情况下，分时和日行情可能有，即便是没有，也可以通过上面的属性计算得出
	// =======================

	/** 前收 */
	private double preClose;

	/** 前收 涨跌额 */
	private double priceChange;

	/** 涨跌幅（%） */
	private double pctChange;

	/** 价格震幅（%） */
	private double amplitude;

	// =======================XXX 以上是指标计算依赖属性字段

	// 技术指标-多属性值指标 XXX
	
	/** KDJ随机指标 */
	private KDJ kdj;

	/** MACD平滑异同移动平均线指标 */
	private MACD macd;

	/** 布林线指标 */
	private BOLL boll;

	/** 动向指标 */
	private DMI dmi;

	// 技术指标===单属性值指标 XXX
	// 股价-TD序列
	/** 神奇九转，九转序列、TD序列 */
	private Integer td;

	// 股价-CCI：顺势指标 CCI指标就一个参数，一般用14，看中短线用，还可以用84看中长线。
	/** CCI14 */
	private CCI cci14;
	
	
	// 股价-MA：移动平均线
	/** MA_5 */
	private Double ma5;

	/** MA_10 */
	private Double ma10;

	/** MA_20 */
	private Double ma20;

	/** MA_40 */
	private Double ma40;

	/** MA_60 */
	private Double ma60;

	// 股价-EMA：指数移动平均值
	/** EMA_5 */
	private Double ema5;

	/** EMA_10 */
	private Double ema10;

	/** EMA_20 */
	private Double ema20;

	/** EMA_60 */
	private Double ema60;

	// 股价-RSI：相对强弱指标RSI是用以计测市场供需关系和买卖力道的方法及指标。
	/** RSI-6 */
	private RSI rsi6;

	/** RSI-12 */
	private RSI rsi12;

	/** RSI-24 */
	private RSI rsi24;

	// 股价-BIAS：乖离率，又称为y值，是反映股价在波动过程中与移动平均线偏离程度的技术指标。
	/** 乖离率6 */
	private Double bias6;

	/** 乖离率12 */
	private Double bias12;

	/** 乖离率24 */
	private Double bias24;

	// 股价-WR：威廉指标，W&R属于摆动类反向指标， 即：修复当股价上涨，W&R指标向下。股价下跌，W&R指标向上，则为上涨。
	/** 威廉6 */
	private Double wr6;

	/** 威廉10 */
	private Double wr10;

	/** 威廉14 */
	private Double wr14;

	/** 威廉20 */
	private Double wr20;

}
