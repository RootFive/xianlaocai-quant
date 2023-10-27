package xlc.quant.data.indicator.test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import xlc.quant.data.indicator.IndicatorCalculator;
import xlc.quant.data.indicator.IndicatorWarehouseManager;
import xlc.quant.data.indicator.calculator.BIAS;
import xlc.quant.data.indicator.calculator.BOLL;
import xlc.quant.data.indicator.calculator.CCI;
import xlc.quant.data.indicator.calculator.DMI;
import xlc.quant.data.indicator.calculator.EMA;
import xlc.quant.data.indicator.calculator.KDJ;
import xlc.quant.data.indicator.calculator.MA;
import xlc.quant.data.indicator.calculator.MACD;
import xlc.quant.data.indicator.calculator.RSI;
import xlc.quant.data.indicator.calculator.TD;
import xlc.quant.data.indicator.calculator.WR;
import xlc.quant.data.indicator.calculator.innovate.XlcQPCV;
import xlc.quant.data.indicator.calculator.innovate.XlcTOPMV;

public class DemoTest {
    public static void main(String[] args) {
    	//行情数据
    	List<MarketQuotation> listMarketQuotation = new ArrayList<>();
    	//行情按照收盘时间正序排序。
    	List<MarketQuotation> listMarketQuotationOrderByCloseTimeAsc= listMarketQuotation .stream().sorted(Comparator.comparing(MarketQuotation::getCloseTime)).collect(Collectors.toList());
    	
    	//演示计算单个指标
    	singleIndicatorCalculate(listMarketQuotationOrderByCloseTimeAsc);
    	
    	//演示计算多个指标
    	singleIndicatorCalculate(listMarketQuotationOrderByCloseTimeAsc);
    	
    }
    
    /**
     * 演示计算单个指标
     */
    public static void singleIndicatorCalculate(List<MarketQuotation> listMarketQuotationOrderByCloseTimeAsc) {
    	//KDJ-计算器
    	IndicatorCalculator<MarketQuotation,KDJ> kdjCalculator = KDJ.buildCalculator(9, 3, 3,MarketQuotation::setKdj,MarketQuotation::getKdj);
    	
    	for (MarketQuotation mq : listMarketQuotationOrderByCloseTimeAsc) {
			//KDJ-计算
			kdjCalculator.input(mq);
		}
    	
	}
    
    /**
     * @param listMarketQuotationOrderByCloseTimeAsc
     * @param indicatorSetScale   量价指标保留的小数点位数
     */
    public static void multipleIndicatorCalculate(List<MarketQuotation> listMarketQuotationOrderByCloseTimeAsc,int indicatorSetScale) {
    	List<IndicatorCalculator<MarketQuotation, ?>> calculatorConfig = buildIndicatorCalculatorList(2);
    	int maximum =200;//管理指标载体的最大数量
    	IndicatorWarehouseManager<LocalDateTime,MarketQuotation> calculateManager = new IndicatorWarehouseManager<>(maximum, calculatorConfig);
    	
    	//循环-管理员接收 新行情数据-进行批量计算所有指标
    	for (MarketQuotation mq : listMarketQuotationOrderByCloseTimeAsc) {
    		calculateManager.accept(mq);
		}
    	
    }
    
    
    
    
    /**
	 * @param indicatorSetScale  指标精度的小数位
	 * @return
	 */
	protected static List<IndicatorCalculator<MarketQuotation, ?>> buildIndicatorCalculatorList(int indicatorSetScale) {
		List<IndicatorCalculator<MarketQuotation, ?>> indicatorCalculatorList =  new ArrayList<>(); 
		//技术指标===多值指标 XXX
		//XLC-量价形态-计算器-配置
		indicatorCalculatorList.add( XlcQPCV.buildCalculator(MarketQuotation::setQpcv,MarketQuotation::getQpcv));
		//TOPMV-计算器-配置
		indicatorCalculatorList.add(XlcTOPMV.buildCalculator(20, 3,indicatorSetScale,indicatorSetScale,MarketQuotation::setTop3In20));
		indicatorCalculatorList.add(XlcTOPMV.buildCalculator(30, 4,indicatorSetScale,indicatorSetScale,MarketQuotation::setTop4In30));
		indicatorCalculatorList.add(XlcTOPMV.buildCalculator(60, 5,indicatorSetScale,indicatorSetScale,MarketQuotation::setTop5In60));

		// KDJ-计算器
		indicatorCalculatorList.add(KDJ.buildCalculator(9,3,3,MarketQuotation::setKdj,MarketQuotation::getKdj));
		// MACD-计算器
		indicatorCalculatorList.add(MACD.buildCalculator(12, 26, 9,indicatorSetScale,MarketQuotation::setMacd,MarketQuotation::getMacd));
		// BOLL-计算器
		indicatorCalculatorList.add(BOLL.buildCalculator(20, 2,indicatorSetScale,MarketQuotation::setBoll,MarketQuotation::getBoll));
		// DMI-计算
		indicatorCalculatorList.add(DMI.buildCalculator(14, 6,MarketQuotation::setDmi,MarketQuotation::getDmi));
	
		// 技术指标===单属性值指标 XXX
		// TD九转序列-计算器
		indicatorCalculatorList.add(TD.buildCalculator(13, 4,MarketQuotation::setTd,MarketQuotation::getTd));
		// CCI-计算器: 顺势指标
		indicatorCalculatorList.add(CCI.buildCalculator(14,indicatorSetScale,MarketQuotation::setCci14,MarketQuotation::getCci14));
		
		//MA-计算器: 移动平均线
		indicatorCalculatorList.add(MA.buildCalculator(5,indicatorSetScale,MarketQuotation::setMa5));
		indicatorCalculatorList.add(MA.buildCalculator(10,indicatorSetScale,MarketQuotation::setMa10));
		indicatorCalculatorList.add(MA.buildCalculator(20,indicatorSetScale,MarketQuotation::setMa20));
		indicatorCalculatorList.add(MA.buildCalculator(40,indicatorSetScale,MarketQuotation::setMa40));
		indicatorCalculatorList.add(MA.buildCalculator(60,indicatorSetScale,MarketQuotation::setMa60));
		
		//EMA-计算器: 指数平滑移动平均线，简称指数平均线。
		indicatorCalculatorList.add(EMA.buildCalculator(5,indicatorSetScale,MarketQuotation::setEma5,MarketQuotation::getEma10));
		indicatorCalculatorList.add(EMA.buildCalculator(10,indicatorSetScale,MarketQuotation::setEma10,MarketQuotation::getEma10));
		indicatorCalculatorList.add(EMA.buildCalculator(20,indicatorSetScale,MarketQuotation::setEma20,MarketQuotation::getEma20));
		indicatorCalculatorList.add(EMA.buildCalculator(60,indicatorSetScale,MarketQuotation::setEma60,MarketQuotation::getEma60));
		
		//RSI-计算器: 相对强弱指标
		indicatorCalculatorList.add(RSI.buildCalculator(6,MarketQuotation::setRsi6,MarketQuotation::getRsi6));
		indicatorCalculatorList.add(RSI.buildCalculator(12,MarketQuotation::setRsi12,MarketQuotation::getRsi12));
		indicatorCalculatorList.add(RSI.buildCalculator(24,MarketQuotation::setRsi24,MarketQuotation::getRsi24));
		
		//BIAS-计算器: 乖离率指标
		indicatorCalculatorList.add(BIAS.buildCalculator(6,MarketQuotation::setBias6));
		indicatorCalculatorList.add(BIAS.buildCalculator(12,MarketQuotation::setBias12));
		indicatorCalculatorList.add(BIAS.buildCalculator(24,MarketQuotation::setBias24));
		
		//WR-计算器: 威廉指标
		indicatorCalculatorList.add(WR.buildCalculator(6,MarketQuotation::setWr6));
		indicatorCalculatorList.add(WR.buildCalculator(10,MarketQuotation::setWr10));
		indicatorCalculatorList.add(WR.buildCalculator(14,MarketQuotation::setWr14));
		indicatorCalculatorList.add(WR.buildCalculator(20,MarketQuotation::setWr20));
		
		return indicatorCalculatorList;
	}
}
