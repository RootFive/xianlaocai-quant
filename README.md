# xianlaocai-quant

## 交流群信息:
点击加入【QQ交流群】[![加入QQ群](https://img.shields.io/badge/223606797-blue.svg)](https://jq.qq.com/?_wv=1027&k=3l0rfaJP)  

QQ群号：223606797，（加群备注：quant，进群后找群主要微信交流群）

## 介绍
xianlaocai-quant是一个父工程，目前仅仅开源一个包，会逐步开源子模块。

### quant-data-indicator
主要是用来计算技术指标的，后续会持续更新。欢迎大家多提bug和建议，参与贡献.

### quant-data-indicator
基于Java实现常见指标MACD,RSI,BOLL,KDJ,CCI,MA,EMA,BIAS,TD,WR,DMI等,全部封装，简洁且准确，能非常方便的应用在各自股票股市技术分析，股票自动程序化交易,数字货币BTC等量化等领域.



## 使用说明

### Maven直接引用

Maven地址（更新较快）阿里云Maven仓库搜索关键词：quant-data-indicator， 

阿里云Maven仓库地址：https://developer.aliyun.com/mvn/search

mvnrepository地址（更新较慢）：https://mvnrepository.com/artifact/com.xianlaocai.quant/quant-data-indicator

1.  IDE: Ecplse或者IDEA均可

2. Maven

```xml
	<dependency>
		<groupId>com.xianlaocai.quant</groupId>
		<artifactId>quant-data-indicator</artifactId>
		<version>XLCQ20231019</version>
	</dependency>
```


Gradle

```xml
// https://mvnrepository.com/artifact/com.xianlaocai.quant/quant-data-indicator
implementation group: 'com.xianlaocai.quant', name: 'quant-data-indicator', version: 'XLCQ20231019'

```
### 基表计算示例 
1.  A股：/quant-data-indicator/src/test/java/xlc/quant/data/indicator/test/stock/StockTest.java

2.  币圈：/quant-data-indicator/src/test/java/xlc/quant/data/indicator/test/coin/CoinTest.java


## 软件架构
java最低最低JDK1.8（Java8），Maven聚合父子项目，会逐步开源子模块。


### quant-data-indicator中指标计算实现说明
#### 5个重要的类

1、FixedWindowCalculator：固定窗口 计算器

```java
/**
 * 环形固定窗口 计算器
 * 
 * @author Rootfive
 * 
 */
public abstract class CircularFixedWindowCalculator<FWC extends CircularFixedWindowCalculable<?>,I> {
	
	/** 需要计算的数据：指[固定窗口环形数组]中的数据 */
	protected final transient Object [] circularData;

	/** 环形数组最大长度，[固定窗口环形数组]时间周期 */
	protected final transient int fwcPeriod;

	/** 执行总数 */
	protected int executeTotal = 0;

	/** 满容计算：指环形数组满容时才会执行计算 */
	private final transient boolean isFullCapacityCalculate;

	/** 头数据角标：已经插入环形数组的最新的数据数组角标 */
	private int headIndex = 0;

	/**
	 * 
	 * @param fwcMax  固定窗口最大值，也就是[固定窗口环形数组]的长度
	 * @param isFullCapacityCalculate
	 */
	public CircularFixedWindowCalculator(int fwcMax, boolean isFullCapacityCalculate) {
		super();
		this.circularData =  new Object [fwcMax];
		this.fwcPeriod = fwcMax;
		this.isFullCapacityCalculate = isFullCapacityCalculate;
	}

	// ==========XXX===================
	/**
	 * 执行计算，由子类具体某个指标的计算器实现
	 * @param propertyGetter  委托方法，上一个载体获取上一个计算结果
	 * @param propertySetter  委托方法，设置计算结果到载体的哪个属性
	 * @return
	 */
	protected abstract I executeCalculate(Function<FWC, I> propertyGetter,Consumer<I> propertySetter);

	// ==========XXX===================

	/**
	 * @param newFwc         输入新数据
	 * @param propertyGetter  委托方法，上一个载体获取上一个计算结果
	 * @param propertySetter  委托方法，设置计算结果到载体的哪个属性
	 * @return
	 */
	public synchronized I input(FWC newFwc,Function<FWC, I> propertyGetter,Consumer<I> propertySetter) {
		boolean addResult = addFirst(newFwc);
		if (addResult) {
			// 新增成功
			if (!isFullCapacityCalculate || (isFullCapacityCalculate && this.isFullCapacity())) {
				// 1、不是满容计算 [或] 2满容计算且已经满容，二者条件满足其中一种。均可执行计算（指标）
				return executeCalculate(propertyGetter,propertySetter);
			}
		}
		return null;
	}


	//........
	//其他代码请看代码实现
	//........
}
```

2、Indicator：顶级指标（所有指标都必须继承的抽象父类）

```java
public abstract class Indicator {

}
```

3、IndicatorCalculator：[指标计算器]父类（所有[指标计算器]都必须继承的抽象父类）

```java
/**
 * 指标计算 计算器
 * 
 * @author Rootfive
 */
public abstract  class IndicatorCalculator<C extends IndicatorComputeCarrier<?>,I>  extends  CircularFixedWindowCalculator<C,I> {
	//........
	//其他代码请看代码实现
	//........
}
```

4、CircularFixedWindowCalculable：可以进行指标计算载体顶级接口

```java
/**
 * @author Rootfive
 * 将时间划分为固定大小的窗口（年、月、日、时、分），统计每个窗口内的请求行情.
 * PS:思路来源于：限流算法-固定窗口算法（Fixed Window Algorithm）
 * 
 * 时间窗口的 终点是收盘时间，时间可以是【时间戳long】 或者是LocalDateTime、LocalDate、LocalTime
 */
public interface CircularFixedWindowCalculable<C extends Comparable<? super C>> {

	/**
	 * @return 收盘时间 
	 */
	C getCloseTime();
	/**
	 * @param closeTime 收盘时间  
	 */
	void setCloseTime(C closeTime);

	/**
	 * @return 交易时间
	 */
	C getTradeTime();
	/**
	 * @param tradeTime 交易时间 
	 */
	void setTradeTime(C tradeTime);

}
```

5、IndicatorComputeCarrier：指标计算载体，用来计算指标并返回

```java

/**
 * 指标计算载体
 * 注意：下面的行情数据，如果是A股。请一定要使用复权数据，前复权和后复权均可
 */
public interface IndicatorComputeCarrier<C extends Comparable<? super C>> extends CircularFixedWindowCalculable<C>{
	/**
	 * @return 开盘价
	 */
	double getOpen();
	/**
	 * @param open 开盘价
	 */
	void setOpen(double open);

	
	
	/**
	 * @return 最低价
	 */
	double getLow();
	/**
	 * @param low 最低价
	 */
	void setLow(double low);
	
	

	/**
	 * @return 最高价
	 */
	double getHigh();
	/**
	 * @param high 最高价
	 */
	void setHigh(double high);
	
	
	
	/**
	 * @return 收盘价(当前K线未结束的即为最新交易价)
	 */
	double getClose();
	/**
	 * @param close 收盘价(当前K线未结束的即为最新交易价)
	 */
	void setClose(double close);

	
	
	/**
	 * @return 成交量
	 */
	double getVolume();
	/**
	 * @param volume 成交量
	 */
	void setVolume(double volume);

	
	/**
	 * @return 成交额
	 */
	double getAmount();
	/**
	 * @param amount 成交额 
	 */
	void setAmount(double amount);
	
	
	// =======================XXX 
	// 上面的属性值，一般情况下，分时和日行情都有
	// 下面的属性值，一般情况下，分时和日行情可能有，即便是没有，也可以通过上面的属性计算得出
	// =======================XXX
	
	/**
	 * @return 前收价格
	 */
	double getPreClose();
	/**
	 * @param preClose 前收价格
	 */
	void setPreClose(double preClose);
	
	

	/**
	 * @return 前收 涨跌额
	 */
	double getPriceChange();
	/**
	 * @param priceChange 前收 涨跌额
	 */
	void setPriceChange(double priceChange);

	
	/**
	 * @return 涨跌幅（百分点）
	 */
	double getPctChange();
	/**
	 * @param pctChange 涨跌幅（百分点）
	 */
	void setPctChange(double pctChange);
	

	/**
	 * @return 价格震幅（百分点）
	 */
	double getAmplitude();
	/**
	 * @param amplitude 价格震幅（百分点）
	 */
	void setAmplitude(double amplitude);

}
```

#### 实现一个新的指标方法

1.  继承 Indicator指标父类 ，并定义指标值
2.  继承 IndicatorCalculator指标父类计算器 ，实现executeCalculate(Function<C, Double> propertyGetter,Consumer<Double> propertySetter) 方法，

```java
	/**
	 * 执行计算，由子类具体某个指标的计算器实现
	 * @param propertyGetter  委托方法，上一个载体获取上一个计算结果
	 * @param propertySetter  委托方法，设置计算结果到载体的哪个属性
	 * @return
	 */
	protected abstract I executeCalculate(Function<FWC, I> propertyGetter,Consumer<I> propertySetter);
```

#### 指标实现举例：移动平均线MA

1.  继承 Indicator指标父类 ，并定义指标值
2.  继承 IndicatorCalculator指标父类计算器 ，实现顶层抽象类CircularFixedWindowCalculator中的抽象方法executeCalculate(Function<FWC, I> propertyGetter,Consumer<I> propertySetter)方法，

```java

/**
 * 计算器
 * @author Rootfive
 * 百度百科：https://baike.baidu.com/item/KDJ%E6%8C%87%E6%A0%87
 * 
 * 移动平均线，英文名称为MovingAverage，简称MA，原本意思是移动平均。由于我们将其制作成线形，所以一般称为移动平均线，简称均线。
 * 均线是将某一段吋间的收盘价之和除以该周期，比如日线MA5指5天内的收盘价除以5,
 * 其计算公式为： MA(5)=(C1+C2+C3十C4+C5)/5
 * 其中：
 *    Cn为第n日收盘价。例如C1，则为第1日收盘价。
 *
 *    用EMA追底，用MA识顶。 例如，用20天EMA判断底部，用20天MA判断顶部。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MA extends Indicator {
	
	//=============
	//内部类分隔符 XXX
	//=============
	/**
	 * 构建-计算器
	 * @param capacity
	 * @param indicatorSetScale        指标精度
	 * @return
	 */
	public static <C extends IndicatorComputeCarrier<?>>  IndicatorCalculator<C, Double> buildCalculator(int capacity,int indicatorSetScale) {
		return new MACalculator<>(capacity,indicatorSetScale);
	}

	/**
	 * 内部类实现MA计算器
	 * @author Rootfive
	 */
	private static class MACalculator<C extends IndicatorComputeCarrier<?>>  extends IndicatorCalculator<C, Double> {
		/** 指标精度 */
		private final int indicatorSetScale;
		
		/**
		 * @param capacity
		 */
		MACalculator(int capacity,int indicatorSetScale) {
			super(capacity, true);
			this.indicatorSetScale =  indicatorSetScale;
		}

		@Override
		protected Double executeCalculate(Function<C, Double> propertyGetter,Consumer<Double> propertySetter) {
			double closeSumValue = DoubleUtils.ZERO;
			for (int i = 0; i < circularData.length; i++) {
				closeSumValue = closeSumValue+ getPrevByNum(i).getClose();
			}
			
			double maValue = DoubleUtils.divide(closeSumValue, fwcPeriod, indicatorSetScale);
			//设置计算结果
			propertySetter.accept(maValue);
			return maValue;
		}

	}

}

```






## 参与贡献

多提意见！！!
1.  Fork 本仓库
2.  新建 Feat_xxx 分支
3.  提交代码
4.  新建 Pull Request




