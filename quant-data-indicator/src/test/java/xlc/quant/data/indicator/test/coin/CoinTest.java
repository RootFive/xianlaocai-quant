package xlc.quant.data.indicator.test.coin;

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

public class CoinTest {
    public static void main(String[] args) {
    	
    	List<CoinIndicatorCarrierDomain> listStockDaily = new ArrayList<>();
    	
    	//币圈 日线行情按照收盘日期时间正序排序。
    	List<CoinIndicatorCarrierDomain> listStockDailyOrderByTradeDateAsc= listStockDaily .stream().sorted(Comparator.comparing(CoinIndicatorCarrierDomain::getCloseTime)).collect(Collectors.toList());
    	calculateDailyIndicator(listStockDailyOrderByTradeDateAsc);
    }
    
    
    
    /**
	 * 日报指标计算 
	 * 
	 * @param listStockDailyOrderByTradeDateAsc  个股日线行情按照时间正序排序。
	 * @return
	 */
	public static List<CoinIndicatorCarrierDomain> calculateDailyIndicator(List<CoinIndicatorCarrierDomain> listStockDailyOrderByTradeDateAsc) {
		
    	//技术指标===多值指标 XXX
    	//KDJ-计算器
    	IndicatorCalculator<CoinIndicatorCarrierDomain,KDJ> kdjCalculator = KDJ.buildCalculator(9, 3, 3);
    	//MACD-计算器
    	IndicatorCalculator<CoinIndicatorCarrierDomain,MACD> macdCalculator = MACD.buildCalculator(12, 26, 9,2);
    	//BOLL-计算器
    	IndicatorCalculator<CoinIndicatorCarrierDomain,BOLL> bollCalculator = BOLL.buildCalculator(20, 2,2);
    	//TD九转序列-计算器
    	IndicatorCalculator<CoinIndicatorCarrierDomain,Integer> tdCalculator = TD.buildCalculator(13, 4);
    	//DMI-计算
    	IndicatorCalculator<CoinIndicatorCarrierDomain,DMI> dmiCalculator = DMI.buildCalculator(14, 6);
    	
    	
    	//技术指标===单值指标 XXX
    	//TOPMV-计算器
    	IndicatorCalculator<CoinIndicatorCarrierDomain,XlcTOPMV> topmvCalculator20_3 = XlcTOPMV.buildCalculator(20, 3,2,2);
    	IndicatorCalculator<CoinIndicatorCarrierDomain,XlcTOPMV> topmvCalculator30_4 = XlcTOPMV.buildCalculator(30, 4,2,2);
    	IndicatorCalculator<CoinIndicatorCarrierDomain,XlcTOPMV> topmvCalculator60_5 = XlcTOPMV.buildCalculator(60, 5,2,2);
    	

    	//MA-计算器: 移动平均线
    	IndicatorCalculator<CoinIndicatorCarrierDomain,Double> ma5Calculator = MA.buildCalculator(5,2);
    	IndicatorCalculator<CoinIndicatorCarrierDomain,Double> ma10Calculator = MA.buildCalculator(10,2);
    	IndicatorCalculator<CoinIndicatorCarrierDomain,Double> ma20Calculator = MA.buildCalculator(20,2);
    	IndicatorCalculator<CoinIndicatorCarrierDomain,Double> ma40Calculator = MA.buildCalculator(40,2);
    	IndicatorCalculator<CoinIndicatorCarrierDomain,Double> ma60Calculator = MA.buildCalculator(60,2);
    	

    	//EMA-计算器: 移动平均值
    	IndicatorCalculator<CoinIndicatorCarrierDomain,Double> ema5Calculator = EMA.buildCalculator(5,2);
    	IndicatorCalculator<CoinIndicatorCarrierDomain,Double> ema10Calculator = EMA.buildCalculator(10,2);
    	IndicatorCalculator<CoinIndicatorCarrierDomain,Double> ema20Calculator = EMA.buildCalculator(20,2);
    	IndicatorCalculator<CoinIndicatorCarrierDomain,Double> ema60Calculator = EMA.buildCalculator(60,2);

    	//RSI-计算器: 相对强弱指标
    	IndicatorCalculator<CoinIndicatorCarrierDomain,RSI> rsi6Calculator = RSI.buildCalculator(6);
    	IndicatorCalculator<CoinIndicatorCarrierDomain,RSI> rsi12Calculator = RSI.buildCalculator(12);
    	IndicatorCalculator<CoinIndicatorCarrierDomain,RSI> rsi24Calculator = RSI.buildCalculator(24);

    	//BIAS-计算器: 乖离率指标
    	IndicatorCalculator<CoinIndicatorCarrierDomain,Double> bias6Calculator = BIAS.buildCalculator(6);
    	IndicatorCalculator<CoinIndicatorCarrierDomain,Double> bias12Calculator = BIAS.buildCalculator(12);
    	IndicatorCalculator<CoinIndicatorCarrierDomain,Double> bias24Calculator = BIAS.buildCalculator(24);

    	//CCI-计算器: 顺势指标
    	IndicatorCalculator<CoinIndicatorCarrierDomain,CCI> cci14Calculator = CCI.buildCalculator(14,2);

    	//WR-计算器: 威廉指标
    	IndicatorCalculator<CoinIndicatorCarrierDomain,Double> wr6Calculator = WR.buildCalculator(6);
    	IndicatorCalculator<CoinIndicatorCarrierDomain,Double> wr10Calculator = WR.buildCalculator(10);
    	IndicatorCalculator<CoinIndicatorCarrierDomain,Double> wr14Calculator = WR.buildCalculator(14);
    	IndicatorCalculator<CoinIndicatorCarrierDomain,Double> wr20Calculator = WR.buildCalculator(20);
    	
    	
    	
		
		//上一份日报(下面的循环变量)
		for (CoinIndicatorCarrierDomain current : listStockDailyOrderByTradeDateAsc) {
			//技术指标===多值指标 XXX
			//技术指标===多值指标 XXX
			//KDJ-计算
			kdjCalculator.input(current,CoinIndicatorCarrierDomain::getKdj,CoinIndicatorCarrierDomain::setKdj);
			kdjCalculator.input(current,CoinIndicatorCarrierDomain::getKdj,CoinIndicatorCarrierDomain::setKdj);
			//MACD-计算
			macdCalculator.input(current,CoinIndicatorCarrierDomain::getMacd,CoinIndicatorCarrierDomain::setMacd);
			//BOLL-计算
			bollCalculator.input(current,CoinIndicatorCarrierDomain::getBoll,CoinIndicatorCarrierDomain::setBoll);
			//TD九转序列-计算
			tdCalculator.input(current,CoinIndicatorCarrierDomain::getTd,CoinIndicatorCarrierDomain::setTd);
			//TD九转序列-计算
			dmiCalculator.input(current,CoinIndicatorCarrierDomain::getDmi,CoinIndicatorCarrierDomain::setDmi);
			
			
			//技术指标===单值指标 XXX
			//TOPMV-计算
			topmvCalculator20_3.input(current,CoinIndicatorCarrierDomain::getTop3In20,CoinIndicatorCarrierDomain::setTop3In20);
			topmvCalculator30_4.input(current,CoinIndicatorCarrierDomain::getTop4In30,CoinIndicatorCarrierDomain::setTop4In30);
			topmvCalculator60_5.input(current,CoinIndicatorCarrierDomain::getTop5In60,CoinIndicatorCarrierDomain::setTop5In60);

			
			//MA-计算
			ma5Calculator.input(current, CoinIndicatorCarrierDomain::getMa5,CoinIndicatorCarrierDomain::setMa5);
			ma10Calculator.input(current, CoinIndicatorCarrierDomain::getMa10,CoinIndicatorCarrierDomain::setMa10);
			ma20Calculator.input(current, CoinIndicatorCarrierDomain::getMa20,CoinIndicatorCarrierDomain::setMa20);
			ma40Calculator.input(current, CoinIndicatorCarrierDomain::getMa40,CoinIndicatorCarrierDomain::setMa40);
			ma60Calculator.input(current, CoinIndicatorCarrierDomain::getMa60,CoinIndicatorCarrierDomain::setMa60);
			
			//EMA-计算
			ema5Calculator.input(current, CoinIndicatorCarrierDomain::getEma5,CoinIndicatorCarrierDomain::setEma5);
			ema10Calculator.input(current, CoinIndicatorCarrierDomain::getEma10,CoinIndicatorCarrierDomain::setEma10);
			ema20Calculator.input(current, CoinIndicatorCarrierDomain::getEma20,CoinIndicatorCarrierDomain::setEma20);
			ema60Calculator.input(current, CoinIndicatorCarrierDomain::getEma60,CoinIndicatorCarrierDomain::setEma60);

			//RSI-计算
			rsi6Calculator.input(current, CoinIndicatorCarrierDomain::getRsi6,CoinIndicatorCarrierDomain::setRsi6);
			rsi12Calculator.input(current, CoinIndicatorCarrierDomain::getRsi12,CoinIndicatorCarrierDomain::setRsi12);
			rsi24Calculator.input(current, CoinIndicatorCarrierDomain::getRsi24,CoinIndicatorCarrierDomain::setRsi24);
					

			//BIAS-计算
			bias6Calculator.input(current,CoinIndicatorCarrierDomain::getBias6,CoinIndicatorCarrierDomain::setBias6);
			bias12Calculator.input(current,CoinIndicatorCarrierDomain::getBias12,CoinIndicatorCarrierDomain::setBias12);
			bias24Calculator.input(current,CoinIndicatorCarrierDomain::getBias24,CoinIndicatorCarrierDomain::setBias24);

			//CCI-计算
			cci14Calculator.input(current,CoinIndicatorCarrierDomain::getCci14,CoinIndicatorCarrierDomain::setCci14);

			//WR-计算
			wr6Calculator.input(current,CoinIndicatorCarrierDomain::getWr6,CoinIndicatorCarrierDomain::setWr6);
			wr10Calculator.input(current,CoinIndicatorCarrierDomain::getWr10,CoinIndicatorCarrierDomain::setWr10);
			wr14Calculator.input(current,CoinIndicatorCarrierDomain::getWr14,CoinIndicatorCarrierDomain::setWr14);
			wr20Calculator.input(current,CoinIndicatorCarrierDomain::getWr20,CoinIndicatorCarrierDomain::setWr20);
			

		}
		return listStockDailyOrderByTradeDateAsc;
	}
	
}
