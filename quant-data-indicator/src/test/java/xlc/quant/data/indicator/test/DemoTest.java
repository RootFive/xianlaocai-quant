package xlc.quant.data.indicator.test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import xlc.quant.data.indicator.Indicator;
import xlc.quant.data.indicator.IndicatorCalculator;
import xlc.quant.data.indicator.IndicatorCalculatorCallback;
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

public class DemoTest {
    public static void main(String[] args) {
    	
    	List<StockDailyDTO> listStockDaily = new ArrayList<>();
    	
    	//个股日线行情按照日期时间正序排序。
    	List<StockDailyDTO> listStockDailyOrderByTradeDateAsc= listStockDaily .stream().sorted(Comparator.comparing(StockDailyDTO::getTradeDate)).collect(Collectors.toList());
    	calculateDailyIndicator(listStockDailyOrderByTradeDateAsc);
    }
    
    
    
    /**
	 * 日报指标计算 
	 * 
	 * @param listStockDailyOrderByTradeDateAsc  个股日线行情按照时间正序排序。
	 * @return
	 */
    @SuppressWarnings("unused")
	public static List<StockDailyDTO> calculateDailyIndicator(List<StockDailyDTO> listStockDailyOrderByTradeDateAsc) {
		//KDJ-计算器
		IndicatorCalculator<KDJ> kdjCalculator = KDJ.buildCalculator(9, 3, 3);
		//MACD-计算器
		IndicatorCalculator<MACD> macdCalculator = MACD.buildCalculator(12, 26, 9);
		//BOLL-计算器
		IndicatorCalculator<BOLL> bollCalculator = BOLL.buildCalculator(20, 2);

		//TOPMV-计算器
		IndicatorCalculator<TOPMV> topmvCalculator20_3 = TOPMV.buildCalculator(20, 3);
		IndicatorCalculator<TOPMV> topmvCalculator30_4 = TOPMV.buildCalculator(30, 4);
		IndicatorCalculator<TOPMV> topmvCalculator60_5 = TOPMV.buildCalculator(60, 5);

		//TD九转序列-计算器
		IndicatorCalculator<TD> tdCalculator = TD.buildCalculator(13, 4);

		//MA-计算器: 移动平均线
		IndicatorCalculator<MA> ma5Calculator = MA.buildCalculator(5);
		IndicatorCalculator<MA> ma10Calculator = MA.buildCalculator(10);
		IndicatorCalculator<MA> ma20Calculator = MA.buildCalculator(20);
		IndicatorCalculator<MA> ma40Calculator = MA.buildCalculator(40);
		IndicatorCalculator<MA> ma60Calculator = MA.buildCalculator(60);

		//EMA-计算器: 移动平均值
		IndicatorCalculator<EMA> ema5Calculator = EMA.buildCalculator(5);
		IndicatorCalculator<EMA> ema10Calculator = EMA.buildCalculator(10);
		IndicatorCalculator<EMA> ema20Calculator = EMA.buildCalculator(20);
		IndicatorCalculator<EMA> ema60Calculator = EMA.buildCalculator(60);

		//RSI-计算器: 相对强弱指标
		IndicatorCalculator<RSI> rsi6Calculator = RSI.buildCalculator(6);
		IndicatorCalculator<RSI> rsi12Calculator = RSI.buildCalculator(12);
		IndicatorCalculator<RSI> rsi24Calculator = RSI.buildCalculator(24);

		//BIAS-计算器: 乖离率指标
		IndicatorCalculator<BIAS> bias6Calculator = BIAS.buildCalculator(6);
		IndicatorCalculator<BIAS> bias12Calculator = BIAS.buildCalculator(12);
		IndicatorCalculator<BIAS> bias24Calculator = BIAS.buildCalculator(24);

		//CCI-计算器: 顺势指标
		IndicatorCalculator<CCI> cci14Calculator = CCI.buildCalculator(14);

		//WR-计算器: 威廉指标
		IndicatorCalculator<WR> wr6Calculator = WR.buildCalculator(6);
		IndicatorCalculator<WR> wr10Calculator = WR.buildCalculator(10);
		IndicatorCalculator<WR> wr14Calculator = WR.buildCalculator(14);
		IndicatorCalculator<WR> wr20Calculator = WR.buildCalculator(20);

		
		IndicatorCalculator<DMI> dmiCalculator = DMI.buildCalculator(14, 6);
		
		//上一份日报(下面的循环变量)
		StockDailyDTO preDaily = null;
		for (StockDailyDTO daily : listStockDailyOrderByTradeDateAsc) {
			//DMI-计算
			DMI dmi = dmiCalculator.input(createIndicatorCarrier(DMI.class, daily));
			
			
			//KDJ-计算
			KDJ kdj = kdjCalculator.input(createIndicatorCarrier(KDJ.class, daily));
			daily.setKdj(kdj);

			//MACD-计算
			MACD macd = macdCalculator.input(createIndicatorCarrier(MACD.class, daily));
			daily.setMacd(macd);

			//BOLL-计算
			BOLL boll = bollCalculator.input(createIndicatorCarrier(BOLL.class, daily));
			daily.setBoll(boll);

			//TOPMV-计算
			TOPMV top3In20 = topmvCalculator20_3.input(createIndicatorCarrier(TOPMV.class, daily));
			TOPMV top4In30 = topmvCalculator30_4.input(createIndicatorCarrier(TOPMV.class, daily));
			TOPMV top5In60 = topmvCalculator60_5.input(createIndicatorCarrier(TOPMV.class, daily));

			// XXX 多参数

			//指标领域-对象
			IndicatorDomainDTO indicatorDomain = Optional.ofNullable(daily.getIndicatorDomain()).orElse(new IndicatorDomainDTO());

			//指标连续性校验 
			if (preDaily != null) {
				
				
				
				//前收指标领域
				IndicatorDomainDTO preIndicatorDomain = preDaily.getIndicatorDomain();

				//前收指标
				Integer preKlineRise = Optional.ofNullable(preIndicatorDomain.getKlineRise()).orElse(null);
				Integer preKlineYang = Optional.ofNullable(preIndicatorDomain.getKlineYang()).orElse(null);
				Integer preVolumeRise = Optional.ofNullable(preIndicatorDomain.getVolumeRise()).orElse(null);
				Integer preAmountRise = Optional.ofNullable(preIndicatorDomain.getAmountRise()).orElse(null);

				// K线-连续上涨
				Integer klineRise = IndicatorCalculator.getContinueValue(daily.getClose(), daily.getPreClose(),preKlineRise);
				indicatorDomain.setKlineRise(klineRise);
				
				// K线-连续上阳
				Integer klineYang = IndicatorCalculator.getContinueValue(daily.getClose(), daily.getOpen(),preKlineYang);
				indicatorDomain.setKlineYang(klineYang);
				
				//交易量-连续上涨
				Integer volumeRise = IndicatorCalculator.getContinueValue(daily.getVolume(), preDaily.getVolume(),preVolumeRise);
				indicatorDomain.setVolumeRise(volumeRise);
				
				//交易额-连续上涨		
				Integer amountRise = IndicatorCalculator.getContinueValue(daily.getAmount(), preDaily.getAmount(),preAmountRise);
				indicatorDomain.setAmountRise(amountRise);
			}
			preDaily = daily;

			//TD九转序列-计算
			indicatorDomain.setTd(Optional.ofNullable(tdCalculator.input(createIndicatorCarrier(TD.class, daily)))
					.map(TD::getValue).orElse(null));

			//MA-计算
			indicatorDomain.setMa5(Optional.ofNullable(ma5Calculator.input(createIndicatorCarrier(MA.class, daily)))
					.map(MA::getValue).orElse(null));
			indicatorDomain.setMa10(Optional.ofNullable(ma10Calculator.input(createIndicatorCarrier(MA.class, daily)))
					.map(MA::getValue).orElse(null));
			indicatorDomain.setMa20(Optional.ofNullable(ma20Calculator.input(createIndicatorCarrier(MA.class, daily)))
					.map(MA::getValue).orElse(null));
			indicatorDomain.setMa40(Optional.ofNullable(ma40Calculator.input(createIndicatorCarrier(MA.class, daily)))
					.map(MA::getValue).orElse(null));
			indicatorDomain.setMa60(Optional.ofNullable(ma60Calculator.input(createIndicatorCarrier(MA.class, daily)))
					.map(MA::getValue).orElse(null));
					

			//EMA-计算
			indicatorDomain.setEma5(Optional.ofNullable(ema5Calculator.input(createIndicatorCarrier(EMA.class, daily)))
					.map(EMA::getValue).orElse(null));
			indicatorDomain.setEma10(Optional.ofNullable(ema10Calculator.input(createIndicatorCarrier(EMA.class, daily)))
					.map(EMA::getValue).orElse(null));
			indicatorDomain.setEma20(Optional.ofNullable(ema20Calculator.input(createIndicatorCarrier(EMA.class, daily)))
					.map(EMA::getValue).orElse(null));
			indicatorDomain.setEma60(Optional.ofNullable(ema60Calculator.input(createIndicatorCarrier(EMA.class, daily)))
					.map(EMA::getValue).orElse(null));
					

			//RSI-计算
			indicatorDomain.setRsi6(Optional.ofNullable(rsi6Calculator.input(createIndicatorCarrier(RSI.class, daily)))
					.map(RSI::getValue).orElse(null));
			indicatorDomain.setRsi12(Optional.ofNullable(rsi12Calculator.input(createIndicatorCarrier(RSI.class, daily)))
					.map(RSI::getValue).orElse(null));
			indicatorDomain.setRsi24(Optional.ofNullable(rsi24Calculator.input(createIndicatorCarrier(RSI.class, daily)))
					.map(RSI::getValue).orElse(null));
					

			//BIAS-计算
			indicatorDomain.setBias6(Optional.ofNullable(bias6Calculator.input(createIndicatorCarrier(BIAS.class, daily)))
					.map(BIAS::getValue).orElse(null));
			indicatorDomain.setBias12(Optional.ofNullable(bias12Calculator.input(createIndicatorCarrier(BIAS.class, daily)))
					.map(BIAS::getValue).orElse(null));
			indicatorDomain.setBias24(Optional.ofNullable(bias24Calculator.input(createIndicatorCarrier(BIAS.class, daily)))
					.map(BIAS::getValue).orElse(null));

			//CCI-计算
			Double cci14 = cci14Calculator.input(createIndicatorCarrier(CCI.class, daily)).getValue();
			indicatorDomain.setCci14(cci14);

			//WR-计算
			indicatorDomain.setWr6(Optional.ofNullable(wr6Calculator.input(createIndicatorCarrier(WR.class, daily)))
					.map(WR::getValue).orElse(null));
			indicatorDomain.setWr10(Optional.ofNullable(wr10Calculator.input(createIndicatorCarrier(WR.class, daily)))
					.map(WR::getValue).orElse(null));
			indicatorDomain.setWr14(Optional.ofNullable(wr14Calculator.input(createIndicatorCarrier(WR.class, daily)))
					.map(WR::getValue).orElse(null));
			indicatorDomain.setWr20(Optional.ofNullable(wr20Calculator.input(createIndicatorCarrier(WR.class, daily)))
					.map(WR::getValue).orElse(null));

			//指标领域-赋值
			daily.setIndicatorDomain(indicatorDomain);

		}
		return listStockDailyOrderByTradeDateAsc;
	}
	
	
	public  static <T extends Indicator> IndicatorCalculatorCallback<T> createIndicatorCarrier(Class<T> clazz, StockDailyDTO daily) {
		IndicatorCalculatorCallback<T> indicatorCarrier = new IndicatorCalculatorCallback<>();
		
		//赋值属性
		indicatorCarrier.setSymbol(daily.getStockCode());
		indicatorCarrier.setTradeDateTime(daily.getTradeDate().atStartOfDay());
		indicatorCarrier.setClose(daily.getQfq().getClose());
		indicatorCarrier.setOpen(daily.getQfq().getOpen());
		indicatorCarrier.setHigh(daily.getQfq().getHigh());
		indicatorCarrier.setLow(daily.getQfq().getLow());
		indicatorCarrier.setVolume(daily.getVolume());
		indicatorCarrier.setAmount(daily.getAmount());
		indicatorCarrier.setPreClose(daily.getQfq().getPreClose());
		indicatorCarrier.setPriceChange(daily.getQfq().getPriceChange());
		indicatorCarrier.setPctChange(daily.getPctChange());
		indicatorCarrier.setAmplitude(daily.getAmplitude());

		//IndicatorCarrier<KDJ> kdjCarrier = createIndicatorCarrier(KDJ.class,daily);
		//IndicatorCarrier<BOLL> BOLLCarrier  = createIndicatorCarrier(BOLL.class,daily);

		return indicatorCarrier;
	}
}
