/**
 * 
 */
package xlc.quant.data.indicator.test;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 复权行情：前复权和后复权模型都支持
 * @author Rootfive
 */
@Data
@NoArgsConstructor
public class FuQuanDomainDTO {
	

    /** 开盘价(元) */
    private Double open;

    /** 最高价(元) */
    private Double high;

    /** 最低价(元) */
    private Double low;
    /** 收盘价(元) */
    private Double close;
    
    /** 前收价(元) */
    private Double preClose;

    /** 涨跌额,价格变动(元) */
    private Double priceChange;
	
}
