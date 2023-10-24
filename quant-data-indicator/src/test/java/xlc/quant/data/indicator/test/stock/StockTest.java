package xlc.quant.data.indicator.test.stock;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import xlc.quant.data.indicator.IndicatorCalculator;
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
import xlc.quant.data.indicator.calculator.innovate.XlcTOPMV;

public class StockTest {
    public static void main(String[] args) {
    	
    	List<StockIndicatorCarrierDomain> listStockDaily = new ArrayList<>();
    	
    	//个股日线行情按照收盘日期时间正序排序。
    	List<StockIndicatorCarrierDomain> listStockDailyOrderByTradeDateAsc= listStockDaily .stream().sorted(Comparator.comparing(StockIndicatorCarrierDomain::getCloseTime)).collect(Collectors.toList());
    	calculateDailyIndicator(listStockDailyOrderByTradeDateAsc);
    }
    
    
    
    /**
	 * 日报指标计算 
	 * 
	 * @param listStockDailyOrderByTradeDateAsc  个股日线行情按照时间正序排序。
	 * @return
	 */
	public static List<StockIndicatorCarrierDomain> calculateDailyIndicator(List<StockIndicatorCarrierDomain> listStockDailyOrderByTradeDateAsc) {
		
    	//技术指标===多值指标 XXX
    	//KDJ-计算器
    	IndicatorCalculator<StockIndicatorCarrierDomain,KDJ> kdjCalculator = KDJ.buildCalculator(9, 3, 3);
    	//MACD-计算器
    	IndicatorCalculator<StockIndicatorCarrierDomain,MACD> macdCalculator = MACD.buildCalculator(12, 26, 9,2);
    	//BOLL-计算器
    	IndicatorCalculator<StockIndicatorCarrierDomain,BOLL> bollCalculator = BOLL.buildCalculator(20, 2,2);
    	//TD九转序列-计算器
    	IndicatorCalculator<StockIndicatorCarrierDomain,Integer> tdCalculator = TD.buildCalculator(13, 4);
    	//DMI-计算
    	IndicatorCalculator<StockIndicatorCarrierDomain,DMI> dmiCalculator = DMI.buildCalculator(14, 6);
    	
    	
    	//技术指标===单值指标 XXX
    	//TOPMV-计算器
    	IndicatorCalculator<StockIndicatorCarrierDomain,XlcTOPMV> topmvCalculator20_3 = XlcTOPMV.buildCalculator(20, 3,2,2);
    	IndicatorCalculator<StockIndicatorCarrierDomain,XlcTOPMV> topmvCalculator30_4 = XlcTOPMV.buildCalculator(30, 4,2,2);
    	IndicatorCalculator<StockIndicatorCarrierDomain,XlcTOPMV> topmvCalculator60_5 = XlcTOPMV.buildCalculator(60, 5,2,2);
    	

    	//MA-计算器: 移动平均线
    	IndicatorCalculator<StockIndicatorCarrierDomain,Double> ma5Calculator = MA.buildCalculator(5,2);
    	IndicatorCalculator<StockIndicatorCarrierDomain,Double> ma10Calculator = MA.buildCalculator(10,2);
    	IndicatorCalculator<StockIndicatorCarrierDomain,Double> ma20Calculator = MA.buildCalculator(20,2);
    	IndicatorCalculator<StockIndicatorCarrierDomain,Double> ma40Calculator = MA.buildCalculator(40,2);
    	IndicatorCalculator<StockIndicatorCarrierDomain,Double> ma60Calculator = MA.buildCalculator(60,2);
    	

    	//EMA-计算器: 移动平均值
    	IndicatorCalculator<StockIndicatorCarrierDomain,Double> ema5Calculator = EMA.buildCalculator(5,2);
    	IndicatorCalculator<StockIndicatorCarrierDomain,Double> ema10Calculator = EMA.buildCalculator(10,2);
    	IndicatorCalculator<StockIndicatorCarrierDomain,Double> ema20Calculator = EMA.buildCalculator(20,2);
    	IndicatorCalculator<StockIndicatorCarrierDomain,Double> ema60Calculator = EMA.buildCalculator(60,2);

    	//RSI-计算器: 相对强弱指标
    	IndicatorCalculator<StockIndicatorCarrierDomain,RSI> rsi6Calculator = RSI.buildCalculator(6);
    	IndicatorCalculator<StockIndicatorCarrierDomain,RSI> rsi12Calculator = RSI.buildCalculator(12);
    	IndicatorCalculator<StockIndicatorCarrierDomain,RSI> rsi24Calculator = RSI.buildCalculator(24);

    	//BIAS-计算器: 乖离率指标
    	IndicatorCalculator<StockIndicatorCarrierDomain,Double> bias6Calculator = BIAS.buildCalculator(6);
    	IndicatorCalculator<StockIndicatorCarrierDomain,Double> bias12Calculator = BIAS.buildCalculator(12);
    	IndicatorCalculator<StockIndicatorCarrierDomain,Double> bias24Calculator = BIAS.buildCalculator(24);

    	//CCI-计算器: 顺势指标
    	IndicatorCalculator<StockIndicatorCarrierDomain,CCI> cci14Calculator = CCI.buildCalculator(14,2);

    	//WR-计算器: 威廉指标
    	IndicatorCalculator<StockIndicatorCarrierDomain,Double> wr6Calculator = WR.buildCalculator(6);
    	IndicatorCalculator<StockIndicatorCarrierDomain,Double> wr10Calculator = WR.buildCalculator(10);
    	IndicatorCalculator<StockIndicatorCarrierDomain,Double> wr14Calculator = WR.buildCalculator(14);
    	IndicatorCalculator<StockIndicatorCarrierDomain,Double> wr20Calculator = WR.buildCalculator(20);
    	
    	
    	
		
		//上一份日报(下面的循环变量)
		for (StockIndicatorCarrierDomain current : listStockDailyOrderByTradeDateAsc) {
			//技术指标===多值指标 XXX
			//KDJ-计算
			kdjCalculator.input(current,StockIndicatorCarrierDomain::getKdj,StockIndicatorCarrierDomain::setKdj);
			kdjCalculator.input(current,StockIndicatorCarrierDomain::getKdj,StockIndicatorCarrierDomain::setKdj);
			//MACD-计算
			macdCalculator.input(current,StockIndicatorCarrierDomain::getMacd,StockIndicatorCarrierDomain::setMacd);
			//BOLL-计算
			bollCalculator.input(current,StockIndicatorCarrierDomain::getBoll,StockIndicatorCarrierDomain::setBoll);
			//TD九转序列-计算
			tdCalculator.input(current,StockIndicatorCarrierDomain::getTd,StockIndicatorCarrierDomain::setTd);
			//TD九转序列-计算
			dmiCalculator.input(current,StockIndicatorCarrierDomain::getDmi,StockIndicatorCarrierDomain::setDmi);
			
			
			//技术指标===单值指标 XXX
			//TOPMV-计算
			topmvCalculator20_3.input(current,StockIndicatorCarrierDomain::getTop3In20,StockIndicatorCarrierDomain::setTop3In20);
			topmvCalculator30_4.input(current,StockIndicatorCarrierDomain::getTop4In30,StockIndicatorCarrierDomain::setTop4In30);
			topmvCalculator60_5.input(current,StockIndicatorCarrierDomain::getTop5In60,StockIndicatorCarrierDomain::setTop5In60);

			
			//MA-计算
			ma5Calculator.input(current, StockIndicatorCarrierDomain::getMa5,StockIndicatorCarrierDomain::setMa5);
			ma10Calculator.input(current, StockIndicatorCarrierDomain::getMa10,StockIndicatorCarrierDomain::setMa10);
			ma20Calculator.input(current, StockIndicatorCarrierDomain::getMa20,StockIndicatorCarrierDomain::setMa20);
			ma40Calculator.input(current, StockIndicatorCarrierDomain::getMa40,StockIndicatorCarrierDomain::setMa40);
			ma60Calculator.input(current, StockIndicatorCarrierDomain::getMa60,StockIndicatorCarrierDomain::setMa60);
			
			//EMA-计算
			ema5Calculator.input(current, StockIndicatorCarrierDomain::getEma5,StockIndicatorCarrierDomain::setEma5);
			ema10Calculator.input(current, StockIndicatorCarrierDomain::getEma10,StockIndicatorCarrierDomain::setEma10);
			ema20Calculator.input(current, StockIndicatorCarrierDomain::getEma20,StockIndicatorCarrierDomain::setEma20);
			ema60Calculator.input(current, StockIndicatorCarrierDomain::getEma60,StockIndicatorCarrierDomain::setEma60);

			//RSI-计算
			rsi6Calculator.input(current, StockIndicatorCarrierDomain::getRsi6,StockIndicatorCarrierDomain::setRsi6);
			rsi12Calculator.input(current, StockIndicatorCarrierDomain::getRsi12,StockIndicatorCarrierDomain::setRsi12);
			rsi24Calculator.input(current, StockIndicatorCarrierDomain::getRsi24,StockIndicatorCarrierDomain::setRsi24);
					

			//BIAS-计算
			bias6Calculator.input(current,StockIndicatorCarrierDomain::getBias6,StockIndicatorCarrierDomain::setBias6);
			bias12Calculator.input(current,StockIndicatorCarrierDomain::getBias12,StockIndicatorCarrierDomain::setBias12);
			bias24Calculator.input(current,StockIndicatorCarrierDomain::getBias24,StockIndicatorCarrierDomain::setBias24);

			//CCI-计算
			cci14Calculator.input(current,StockIndicatorCarrierDomain::getCci14,StockIndicatorCarrierDomain::setCci14);

			//WR-计算
			wr6Calculator.input(current,StockIndicatorCarrierDomain::getWr6,StockIndicatorCarrierDomain::setWr6);
			wr10Calculator.input(current,StockIndicatorCarrierDomain::getWr10,StockIndicatorCarrierDomain::setWr10);
			wr14Calculator.input(current,StockIndicatorCarrierDomain::getWr14,StockIndicatorCarrierDomain::setWr14);
			wr20Calculator.input(current,StockIndicatorCarrierDomain::getWr20,StockIndicatorCarrierDomain::setWr20);
			

		}
		return listStockDailyOrderByTradeDateAsc;
	}
	
}
