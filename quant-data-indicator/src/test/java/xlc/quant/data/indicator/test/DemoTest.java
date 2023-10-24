package xlc.quant.data.indicator.test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import xlc.quant.data.indicator.IndicatorCalculateManager;
import xlc.quant.data.indicator.IndicatorCalculator;
import xlc.quant.data.indicator.IndicatorCalculatorConfig;
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
    	IndicatorCalculator<MarketQuotation,KDJ> kdjCalculator = KDJ.buildCalculator(9, 3, 3);
    	
    	for (MarketQuotation mq : listMarketQuotationOrderByCloseTimeAsc) {
			//KDJ-计算
			kdjCalculator.input(mq,MarketQuotation::getKdj,MarketQuotation::setKdj);
		}
    	
	}
    
    /**
     * @param listMarketQuotationOrderByCloseTimeAsc
     * @param indicatorSetScale   量价指标保留的小数点位数
     */
    public static void multipleIndicatorCalculate(List<MarketQuotation> listMarketQuotationOrderByCloseTimeAsc,int indicatorSetScale) {
    	List<IndicatorCalculatorConfig<MarketQuotation, ?>> calculatorConfig = buildCalculatorConfig(2);
    	int maximum =200;//管理指标载体的最大数量
    	IndicatorCalculateManager<MarketQuotation> calculateManager = new IndicatorCalculateManager<>(maximum, calculatorConfig);
    	
    	for (MarketQuotation mq : listMarketQuotationOrderByCloseTimeAsc) {
			//入队指标批量计算所有指标
    		calculateManager.enqueue(mq);
		}
    	
    }
    
    
    
    
    /**
	 * @param indicatorSetScale  指标精度的小数位
	 * @return
	 */
	private static List<IndicatorCalculatorConfig<MarketQuotation, ?>> buildCalculatorConfig(int indicatorSetScale) {
		List<IndicatorCalculatorConfig<MarketQuotation, ?>> calculatorConfigList =  new ArrayList<>(); 
		//技术指标===多值指标 XXX
		//XLC-量价形态-计算器-配置
		calculatorConfigList.add( new IndicatorCalculatorConfig<>(XlcQPCV.buildCalculator(),MarketQuotation::getQpcv,MarketQuotation::setQpcv));
		//TOPMV-计算器-配置
		calculatorConfigList.add(new IndicatorCalculatorConfig<>(XlcTOPMV.buildCalculator(20, 3,indicatorSetScale,indicatorSetScale),MarketQuotation::getTop3In20,MarketQuotation::setTop3In20));
		calculatorConfigList.add(new IndicatorCalculatorConfig<>(XlcTOPMV.buildCalculator(30, 4,indicatorSetScale,indicatorSetScale),MarketQuotation::getTop4In30,MarketQuotation::setTop4In30));
		calculatorConfigList.add(new IndicatorCalculatorConfig<>(XlcTOPMV.buildCalculator(60, 5,indicatorSetScale,indicatorSetScale),MarketQuotation::getTop5In60,MarketQuotation::setTop5In60));

		// KDJ-计算器
		calculatorConfigList.add(new IndicatorCalculatorConfig<>(KDJ.buildCalculator(9,3,3),MarketQuotation::getKdj,MarketQuotation::setKdj));
		// MACD-计算器
		calculatorConfigList.add(new IndicatorCalculatorConfig<>(MACD.buildCalculator(12, 26, 9,indicatorSetScale),MarketQuotation::getMacd,MarketQuotation::setMacd));
		// BOLL-计算器
		calculatorConfigList.add(new IndicatorCalculatorConfig<>(BOLL.buildCalculator(20, 2,indicatorSetScale),MarketQuotation::getBoll,MarketQuotation::setBoll));
		// DMI-计算
		calculatorConfigList.add(new IndicatorCalculatorConfig<>(DMI.buildCalculator(14, 6),MarketQuotation::getDmi,MarketQuotation::setDmi));
	
		// 技术指标===单属性值指标 XXX
		// TD九转序列-计算器
		calculatorConfigList.add(new IndicatorCalculatorConfig<>(TD.buildCalculator(13, 4),MarketQuotation::getTd,MarketQuotation::setTd));
		// CCI-计算器: 顺势指标
		calculatorConfigList.add(new IndicatorCalculatorConfig<>(CCI.buildCalculator(14,indicatorSetScale),MarketQuotation::getCci14,MarketQuotation::setCci14));
		
		//MA-计算器: 移动平均线
		calculatorConfigList.add(new IndicatorCalculatorConfig<>(MA.buildCalculator(5,indicatorSetScale),MarketQuotation::getMa5,MarketQuotation::setMa5));
		calculatorConfigList.add(new IndicatorCalculatorConfig<>(MA.buildCalculator(10,indicatorSetScale),MarketQuotation::getMa10,MarketQuotation::setMa10));
		calculatorConfigList.add(new IndicatorCalculatorConfig<>(MA.buildCalculator(20,indicatorSetScale),MarketQuotation::getMa20,MarketQuotation::setMa20));
		calculatorConfigList.add(new IndicatorCalculatorConfig<>(MA.buildCalculator(40,indicatorSetScale),MarketQuotation::getMa40,MarketQuotation::setMa40));
		calculatorConfigList.add(new IndicatorCalculatorConfig<>(MA.buildCalculator(60,indicatorSetScale),MarketQuotation::getMa60,MarketQuotation::setMa60));
		
		//EMA-计算器: 指数平滑移动平均线，简称指数平均线。
		calculatorConfigList.add(new IndicatorCalculatorConfig<>(EMA.buildCalculator(5,indicatorSetScale),MarketQuotation::getEma10,MarketQuotation::setEma5));
		calculatorConfigList.add(new IndicatorCalculatorConfig<>(EMA.buildCalculator(10,indicatorSetScale),MarketQuotation::getEma10,MarketQuotation::setEma10));
		calculatorConfigList.add(new IndicatorCalculatorConfig<>(EMA.buildCalculator(20,indicatorSetScale),MarketQuotation::getEma20,MarketQuotation::setEma20));
		calculatorConfigList.add(new IndicatorCalculatorConfig<>(EMA.buildCalculator(60,indicatorSetScale),MarketQuotation::getEma60,MarketQuotation::setEma60));
		
		//RSI-计算器: 相对强弱指标
		calculatorConfigList.add(new IndicatorCalculatorConfig<>(RSI.buildCalculator(6),MarketQuotation::getRsi6,MarketQuotation::setRsi6));
		calculatorConfigList.add(new IndicatorCalculatorConfig<>(RSI.buildCalculator(12),MarketQuotation::getRsi12,MarketQuotation::setRsi12));
		calculatorConfigList.add(new IndicatorCalculatorConfig<>(RSI.buildCalculator(24),MarketQuotation::getRsi24,MarketQuotation::setRsi24));
		
		//BIAS-计算器: 乖离率指标
		calculatorConfigList.add(new IndicatorCalculatorConfig<>(BIAS.buildCalculator(6),MarketQuotation::getBias6,MarketQuotation::setBias6));
		calculatorConfigList.add(new IndicatorCalculatorConfig<>(BIAS.buildCalculator(12),MarketQuotation::getBias12,MarketQuotation::setBias12));
		calculatorConfigList.add(new IndicatorCalculatorConfig<>(BIAS.buildCalculator(24),MarketQuotation::getBias24,MarketQuotation::setBias24));
		
		//WR-计算器: 威廉指标
		calculatorConfigList.add(new IndicatorCalculatorConfig<>(WR.buildCalculator(6),MarketQuotation::getWr6,MarketQuotation::setWr6));
		calculatorConfigList.add(new IndicatorCalculatorConfig<>(WR.buildCalculator(10),MarketQuotation::getWr10,MarketQuotation::setWr10));
		calculatorConfigList.add(new IndicatorCalculatorConfig<>(WR.buildCalculator(14),MarketQuotation::getWr14,MarketQuotation::setWr14));
		calculatorConfigList.add(new IndicatorCalculatorConfig<>(WR.buildCalculator(20),MarketQuotation::getWr20,MarketQuotation::setWr20));
		
		return calculatorConfigList;
	}
    
	
}
