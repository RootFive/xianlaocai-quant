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
import xlc.quant.data.indicator.calculator.innovate.TOPMV;

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
    	IndicatorCalculator<CoinIndicatorCarrierDomain,TOPMV> topmvCalculator20_3 = TOPMV.buildCalculator(20, 3,2,2);
    	IndicatorCalculator<CoinIndicatorCarrierDomain,TOPMV> topmvCalculator30_4 = TOPMV.buildCalculator(30, 4,2,2);
    	IndicatorCalculator<CoinIndicatorCarrierDomain,TOPMV> topmvCalculator60_5 = TOPMV.buildCalculator(60, 5,2,2);
    	

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
			//KDJ-计算
			kdjCalculator.input(current,CoinIndicatorCarrierDomain::getKdj,i ->current.setKdj(i));
			//MACD-计算
			macdCalculator.input(current,CoinIndicatorCarrierDomain::getMacd,i ->current.setMacd(i));
			//BOLL-计算
			bollCalculator.input(current,CoinIndicatorCarrierDomain::getBoll,i ->current.setBoll(i));
			//TD九转序列-计算
			tdCalculator.input(current,CoinIndicatorCarrierDomain::getTd,i ->current.setTd(i));
			//TD九转序列-计算
			dmiCalculator.input(current,CoinIndicatorCarrierDomain::getDmi,i ->current.setDmi(i));
			
			
			//技术指标===单值指标 XXX
			//TOPMV-计算
			topmvCalculator20_3.input(current,CoinIndicatorCarrierDomain::getTop3In20,i ->current.setTop3In20(i));
			topmvCalculator30_4.input(current,CoinIndicatorCarrierDomain::getTop4In30,i ->current.setTop4In30(i));
			topmvCalculator60_5.input(current,CoinIndicatorCarrierDomain::getTop5In60,i ->current.setTop5In60(i));

			
			//MA-计算
			ma5Calculator.input(current, CoinIndicatorCarrierDomain::getMa5,i ->current.setMa5(i));
			ma10Calculator.input(current, CoinIndicatorCarrierDomain::getMa10,i ->current.setMa10(i));
			ma20Calculator.input(current, CoinIndicatorCarrierDomain::getMa20,i ->current.setMa20(i));
			ma40Calculator.input(current, CoinIndicatorCarrierDomain::getMa40,i ->current.setMa40(i));
			ma60Calculator.input(current, CoinIndicatorCarrierDomain::getMa60,i ->current.setMa60(i));
			
			//EMA-计算
			ema5Calculator.input(current, CoinIndicatorCarrierDomain::getEma5,i ->current.setEma5(i));
			ema10Calculator.input(current, CoinIndicatorCarrierDomain::getEma10,i ->current.setEma10(i));
			ema20Calculator.input(current, CoinIndicatorCarrierDomain::getEma20,i ->current.setEma20(i));
			ema60Calculator.input(current, CoinIndicatorCarrierDomain::getEma60,i ->current.setEma60(i));

			//RSI-计算
			rsi6Calculator.input(current, CoinIndicatorCarrierDomain::getRsi6,i ->current.setRsi6(i));
			rsi12Calculator.input(current, CoinIndicatorCarrierDomain::getRsi12,i ->current.setRsi12(i));
			rsi24Calculator.input(current, CoinIndicatorCarrierDomain::getRsi24,i ->current.setRsi24(i));
					

			//BIAS-计算
			bias6Calculator.input(current,CoinIndicatorCarrierDomain::getBias6,i ->current.setBias6(i));
			bias12Calculator.input(current,CoinIndicatorCarrierDomain::getBias12,i ->current.setBias12(i));
			bias24Calculator.input(current,CoinIndicatorCarrierDomain::getBias24,i ->current.setBias24(i));

			//CCI-计算
			cci14Calculator.input(current,CoinIndicatorCarrierDomain::getCci14,i ->current.setCci14(i));

			//WR-计算
			wr6Calculator.input(current,CoinIndicatorCarrierDomain::getWr6,i ->current.setWr6(i));
			wr10Calculator.input(current,CoinIndicatorCarrierDomain::getWr10,i ->current.setWr10(i));
			wr14Calculator.input(current,CoinIndicatorCarrierDomain::getWr14,i ->current.setWr14(i));
			wr20Calculator.input(current,CoinIndicatorCarrierDomain::getWr20,i ->current.setWr20(i));
			

		}
		return listStockDailyOrderByTradeDateAsc;
	}
	
}
