package xlc.quant.data.indicator;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 指标载体 IndicatorCarrier
 * 
 * @author GuoHonglin
 */
@Data
@NoArgsConstructor
public  class IndicatorCarrier<T extends Indicator> {

	/** 计算出来的指标结果 XXX */
	protected T indicator;

	// =======================
	// 下面的是计算条件
	// =======================
	/** 证券代码 */
	protected String symbol;

	/** 交易日期时间 */
	protected LocalDateTime tradeDateTime;

	/** 收盘价(元) */
	protected BigDecimal close;

	/** 开盘价(元) */
	protected BigDecimal open;

	/** 最高价(元) */
	protected BigDecimal high;

	/** 最低价(元) */
	protected BigDecimal low;

	// =======================
	/** 成交量(股/份/个) */
	protected BigDecimal volume;

	/** 成交额(元) */
	protected BigDecimal amount;

	// =======================
	// 上面的属性，分时和日行情一定会有
	// =======================
	/** 前收 前复权(未复权，元) */
	protected BigDecimal preClose;

	/** 前收 涨跌额(未复权，元) */
	protected BigDecimal priceChange;

	/** 涨跌幅（未复权，%） */
	protected BigDecimal pctChange;

	/** 价格震幅（未复权，%） */
	protected BigDecimal amplitude;
	
	

	public IndicatorCarrier(IndicatorCarrier<?> indicatorCalculate) {
		super();
		// this.indicator = indicator;
		this.symbol = indicatorCalculate.getSymbol();
		this.tradeDateTime = indicatorCalculate.getTradeDateTime();
		this.close = indicatorCalculate.getClose();
		this.open = indicatorCalculate.getOpen();
		this.high = indicatorCalculate.getHigh();
		this.low = indicatorCalculate.getLow();
		this.volume = indicatorCalculate.getVolume();
		this.amount = indicatorCalculate.getAmount();
		this.preClose = indicatorCalculate.getPreClose();
		this.priceChange = indicatorCalculate.getPriceChange();
		this.pctChange = indicatorCalculate.getPctChange();
		this.amplitude = indicatorCalculate.getAmplitude();
	}

}
