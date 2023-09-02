/**
 * 
 */
package xlc.quant.data.indicator;

import java.math.BigDecimal;

import lombok.Data;

/**
 * @author Rootfive
 * 指标趋势信息
 */
@Data
public class IndicatorDomain {

	//量价形态 XXX
	/** K线-连续上涨 */
	private Integer klineRise;

	/** K线-连续上阳 */
	private Integer klineYang;

	/** 交易量-连续上涨 */
	private Integer volumeRise;

	/** 交易额-连续上涨 */
	private Integer amountRise;

	//XXX 股价-TD序列
	/**  神奇九转，九转序列、TD序列  */
	private Integer td;

	
	//XXX 股价-MA：移动平均线 
	/** MA_5 */
	private BigDecimal ma5;

	/** MA_10 */
	private BigDecimal ma10;

	/** MA_20 */
	private BigDecimal ma20;

	/** MA_40 */
	private BigDecimal ma40;

	/** MA_60 */
	private BigDecimal ma60;

	//XXX 股价-EMA：指数移动平均值
    /** EMA_5 */
	private BigDecimal ema5;

    /** EMA_10 */
    private BigDecimal ema10;

    /** EMA_20 */
    private BigDecimal ema20;

    /** EMA_60 */
    private BigDecimal ema60;
    
    
    //XXX 股价-RSI：相对强弱指标RSI是用以计测市场供需关系和买卖力道的方法及指标。
	/** RSI-6 */
	private BigDecimal rsi6;

    /** RSI-12 */
    private BigDecimal rsi12;

    /** RSI-24 */
    private BigDecimal rsi24;
    
    
    //XXX 股价-BIAS：乖离率，又称为y值，是反映股价在波动过程中与移动平均线偏离程度的技术指标。
    /** 乖离率6 */
    private BigDecimal bias6;

    /** 乖离率12 */
    private BigDecimal bias12;

    /** 乖离率24 */
    private BigDecimal bias24;
    
    //XXX 股价-CCI：顺势指标 CCI指标就一个参数，一般用14，看中短线用，还可以用84看中长线。
	/** CCI14  */
	private BigDecimal cci14;
	
	
	//XXX 股价-WR：威廉指标，W&R属于摆动类反向指标， 即：修复当股价上涨，W&R指标向下。股价下跌，W&R指标向上，则为上涨。
    /** 威廉6 */
    protected BigDecimal wr6;

    /** 威廉10 */
    protected BigDecimal wr10;

    /** 威廉14 */
    protected BigDecimal wr14;

    /** 威廉20 */
    protected BigDecimal wr20;
}
