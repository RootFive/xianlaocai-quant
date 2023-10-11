/**
 * 
 */
package xlc.quant.data.indicator.test;

import java.time.LocalDate;

import lombok.Data;
import lombok.NoArgsConstructor;
import xlc.quant.data.indicator.calculator.BOLL;
import xlc.quant.data.indicator.calculator.KDJ;
import xlc.quant.data.indicator.calculator.MACD;


/**
 * 
 * @author Rootfive
 */
@Data
@NoArgsConstructor
public class StockDailyDTO  {

	/** 证交所（SZ深证 SH上证 BJ北证） */
	private String exchange;

	/** 股票板块（1沪主板 2深主板 3创业板 4科创板 5北证A） */
	private String stockPlate;

	/** 股票简称 */
	private String stockName;

	//日行情信息=XXX
	/** 交易日期  */
	private LocalDate tradeDate;

	/** 股票代码 */
	private String stockCode;

	/** 开盘价(元) */
	private Double open;

	/** 最高价(元) */
	private Double high;

	/** 最低价(元) */
	private Double low;

	/** 收盘价(元) */
	private Double close;

	/** 成交量(股/份) */
	private Double volume;

	/** 成交额(元) */
	private Double amount;

	//=======================
	//上面的属性，分时和日行情一定会有
	//=======================
	/** 前收 前复权(未复权，元)  */
	private Double preClose;

	/** 前收 涨跌额(未复权，元)  */
	private Double priceChange;

	/** 涨跌幅（未复权，%） */
	private Double pctChange;

	/** 价格震幅（未复权，%） */
	private Double amplitude;

	/** 成交均价(元) */
	private Double avgPrice;

	//涨跌、复权信息= XXX
	/** 涨停(基于前复权前收，元) */
	private Double upLimit;

	/** 跌停(基于前复权前收，元) */
	private Double downLimit;

	/** 换手率（%） */
	private Double turnoverRate;

	/** 量比（%） */
	private Double volumeRatio;

	/** 总股本 （股） */
	private Double totalShare;

	/** 流通股本（股） */
	private Double floatShare;

	/** 总市值 （元） */
	private Double totalMv;

	/** 流通市值 （元） */
	private Double circMv;

	/** 外（买）盘(股) */
	private Double buyVolume;

	/** 内（卖）盘(股) */
	private Double sellVolume;

	/** 强弱度(%) */
	private Double strength;

	/** 活跃度(%) */
	private Double activity;

	/** 复权因子 */
	private Double adjFactor;


	//复权 信息= XXX
	/** 前复权行情 */
	private FuQuanDomainDTO qfq;

	/** 后复权行情 */
	private FuQuanDomainDTO hfq;

	//技术指标= XXX
	/** KDJ随机指标 */
	private KDJ kdj;

	/** MACD平滑异同移动平均线指标 */
	private MACD macd;

	/** 布林线指标 */
	private BOLL boll;


	/** TD序列 */
	private IndicatorDomainDTO indicatorDomain;
	

}