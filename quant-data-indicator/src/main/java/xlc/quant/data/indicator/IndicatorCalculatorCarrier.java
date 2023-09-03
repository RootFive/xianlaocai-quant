package xlc.quant.data.indicator;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 指标计算载体 IndicatorCalculatorCarrier
 * 
 * 用来计算指标并返回
 * 
 * @author Rootfive
 * 
 * 注意：本载体下面的行情数据，如果是A股。请一定要使用复权数据，前复权和后复权均可
 */
@Data
@NoArgsConstructor
public  class IndicatorCalculatorCarrier<T extends Indicator> {

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

	/** 成交量(股/份/个) */
	protected BigDecimal volume;

	/** 成交额(元) */
	protected BigDecimal amount;

	// =======================
	// 上面的属性值，一般情况下，分时和日行情都有
	// 下面的属性值，一般情况下，分时和日行情可能有，即便是没有，也可以通过上面的属性计算得出
	// =======================
	/** 前收 (元) */
	protected BigDecimal preClose;

	/** 前收 涨跌额(元) */
	protected BigDecimal priceChange;

	/** 涨跌幅（%） */
	protected BigDecimal pctChange;

	/** 价格震幅（%） */
	protected BigDecimal amplitude;
	
	

	/**
	 * 复合指标计算时，同一个行情，可能需要同时计算多种指标时，需要转换
	 * @param carrier
	 */
	public IndicatorCalculatorCarrier(IndicatorCalculatorCarrier<?> carrier) {
		super();
		// this.indicator = indicator;
		this.symbol = carrier.getSymbol();
		this.tradeDateTime = carrier.getTradeDateTime();
		this.close = carrier.getClose();
		this.open = carrier.getOpen();
		this.high = carrier.getHigh();
		this.low = carrier.getLow();
		this.volume = carrier.getVolume();
		this.amount = carrier.getAmount();
		this.preClose = carrier.getPreClose();
		this.priceChange = carrier.getPriceChange();
		this.pctChange = carrier.getPctChange();
		this.amplitude = carrier.getAmplitude();
	}

}
