# xianlaocai-quant

## 交流群信息:
点击加入【QQ交流群】[![加入QQ群](https://img.shields.io/badge/223606797-blue.svg)](https://jq.qq.com/?_wv=1027&k=3l0rfaJP)  

QQ群号：223606797，（加群备注：quant，后期找群主要 微信 交流群）


## 介绍
xianlaocai-quant是一个父工程，目前仅仅开源一个包，会逐步开源子模块。

### quant-data-indicator
主要是用来计算技术指标的，后续会持续更新。欢迎大家多提bug和建议，参与贡献.

### quant-data-indicator
基于Java实现常见指标MACD,RSI,BOLL,KDJ,CCI,MA,EMA,BIAS,TD,WR,DMI等,全部封装，简洁且准确，能非常方便的应用在各自股票股市技术分析，股票自动程序化交易,数字货币BTC等量化等领域.


## 使用说明（如果能帮到你，烦请点个赞）

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
		<version>XLCQ20231020</version>
	</dependency>
```


Gradle

```xml
// https://mvnrepository.com/artifact/com.xianlaocai.quant/quant-data-indicator
implementation group: 'com.xianlaocai.quant', name: 'quant-data-indicator', version: 'XLCQ20231020'

```
### 基表计算示例 
示例地址：        /quant-data-indicator/src/test/java/xlc/quant/data/indicator/test/DemoTest.java

## 软件架构
java最低最低JDK1.8（Java8），Maven聚合父子项目，会逐步开源子模块。


### quant-data-indicator中指标计算实现说明
#### 5个重要的类

1、FixedWindowCarrier：顶级接口：固定窗口载体

```java
/**
 * @author Rootfive
 * <pre>
 * 顶级接口：固定窗口载体
 * 固定窗口是将时间划分为固定大小的窗口（年、月、日、时、分），划分界限是 是收盘时间。
 * PS:思路来源于：限流算法-固定窗口算法（Fixed Window Algorithm）
 * </pre>
 * 
 * @param <TIME>  枚举类型，时间，目前支持以下类型：
 *<pre>
 *	Long			时间戳
 *	Instant			时间戳
 *	LocalDate		只包含日期，比如：2020-05-20
 *	LocalTime		只包含时间，比如：13:14:00
 *	LocalDateTime	包含日期和时间，比如：2020-05-20 13:14:00
 *	ZonedDateTime	带时区的时间
 *	Date 			日期时间
 * <p>
 * 这种嵌套泛型<TIME extends Comparable<? super TIME>>可以被描述为一个具有两层限制的泛型声明。
 * 首先，TIME是一个泛型类型参数，它被限制为实现了Comparable接口的类型。这意味着传入的类型必须具有比较能力，可以进行比较操作。
 * 其次，Comparable<? super TIME>表示对Comparable接口的类型参数进行了进一步的限制。这里使用了下界通配符<? super TIME>，表示传入的类型可以是TIME的超类，或者就是TIME本身。
 * 这种嵌套泛型的目的是为了提供更灵活的类型约束。它要求传入的类型必须实现了Comparable接口，并且可以是TIME的超类，以便在使用比较操作时保证类型的安全性。
 * 总结起来，<TIME extends Comparable<? super TIME>>表示一个具有两层限制的泛型声明，要求传入的类型必须实现了Comparable接口，并且可以是TIME的超类。这种嵌套泛型可以提供更灵活的类型约束和安全性。
 *</pre>
 */
public interface FixedWindowCarrier<TIME extends Comparable<? super TIME>> {

	/**
	 * @return 收盘时间 
	 */
	TIME getCloseTime();
	/**
	 * @param closeTime 收盘时间  
	 */
	void setCloseTime(TIME closeTime);
	
	

	/**
	 * @return 交易时间
	 */
	TIME getTradeTime();
	/**
	 * @param tradeTime 交易时间 
	 */
	void setTradeTime(TIME tradeTime);

}
```

2、FixedWindowCircularCalculator：固定窗口环形计算器

```java
/**
 * 固定窗口环形计算器
 * @author Rootfive
 * @param <CARRIER> 固定窗口载体
 * @param <RESULT>  计算器的输出结果，Object 可以是任意类型
 */
public abstract class FixedWindowCircularCalculator<CARRIER extends FixedWindowCarrier<?>,RESULT> {
	
	/** 计算所需的载体数据，是一个固定长度的环形数组 */
	protected final transient Object [] carrierData;

	/** 环形数组的周期 */
	protected final transient int circularPeriod;

	/** 执行总数 */
	protected int executeTotal = 0;

	/** 满容计算：指环形数组满容时才会执行计算 */
	private final transient boolean isFullCapacityCalculate;

	/** 头数据角标：已经插入环形数组的最新的数据数组角标 */
	private int headIndex = 0;

	/**
	 * @param maxPeriodLength     [环形数组]的最大周期长度
	 * @param isFullCapacityCalculate   是否满容计算
	 */
	public FixedWindowCircularCalculator(int maxPeriodLength, boolean isFullCapacityCalculate) {
		super();
		this.carrierData =  new Object [maxPeriodLength];
		this.circularPeriod = maxPeriodLength;
		this.isFullCapacityCalculate = isFullCapacityCalculate;
	}


	/**
	 * @param newCarrier 新计算载体
	 * @param propertyGetter  委托方法，上一个载体获取上一个计算结果
	 * @param propertySetter  委托方法，设置计算结果到载体的哪个属性
	 * @return
	 */
	public synchronized RESULT input(CARRIER newCarrier,Function<CARRIER, RESULT> propertyGetter,Consumer<RESULT> propertySetter) {
		boolean addResult = addFirst(newCarrier);
		if (addResult) {
			// 新增成功
			if (!isFullCapacityCalculate || (isFullCapacityCalculate && this.isFullCapacity())) {
				// 二者满足其中一种。均可执行计算，条件：1、不是满容计算 [或] 2满容计算且已经满容，
				RESULT calculateResult = executeCalculate(propertyGetter);
				
				//设置计算结果 到 输入新数据[newCarrier]属性值上，做到指标和数据对应
				propertySetter.accept(calculateResult);
				return calculateResult;
			}
		}
		return null;
	}

	
	/**
	 * @param newCarrier 新计算载体
	 * @return 将newCarrier追加环形数组的第一个位置（头插法）。
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private synchronized boolean addFirst(CARRIER newCarrier) {
		//........
		//其他代码请看代码实现
		//........
		return true;
	}

	/**
	 * 执行计算，由子类具体某个指标的计算器实现
	 * @param propertyGetter  委托方法，从载体获取计算结果
	 * @return
	 */
	protected abstract RESULT executeCalculate(Function<CARRIER, RESULT> propertyGetter);
	
	//........
	//其他代码请看代码实现
	//........
}	
```

3、Indicator：顶级指标（所有指标都必须继承的抽象父类）

```java
/**
 * 顶级指标（所有指标都必须继承的抽象父类）
 * @author Rootfive
 */
public abstract class Indicator {

}

```

4、IndicatorCalculator：指标计算计算器(抽象父类)

```java
/**
 * 指标计算计算器(抽象父类)
 * @author Rootfive
 * @param <CARRIER>  计算指标的载体
 * @param <INDI>     根据载体计算出的指标
 */
public abstract class IndicatorCalculator<CARRIER extends IndicatorComputeCarrier<?>, INDI> 	extends FixedWindowCircularCalculator<CARRIER, INDI> {

	/**
	 * @param maxPeriod   	最大周期			
	 * @param isFullCapacityCalculate  是否满容计算
	 */
	public IndicatorCalculator(int maxPeriod, boolean isFullCapacityCalculate) {
		super(maxPeriod, isFullCapacityCalculate);
	}
	
	//........
	//其他代码请看代码实现
	//........
}
```

5、IndicatorComputeCarrier：指标计算载体

```java

/**
 * 指标计算载体
 * @author Rootfive
 * @param <TIME>  枚举类型，时间，目前支持以下类型：
 *<pre>
 *	Long			时间戳
 *	Instant			时间戳
 *	LocalDate		只包含日期，比如：2020-05-20
 *	LocalTime		只包含时间，比如：13:14:00
 *	LocalDateTime	包含日期和时间，比如：2020-05-20 13:14:00
 *	ZonedDateTime	带时区的时间
 *	Date 			日期时间
 *</pre>
 * 注意：指标计算载体数据，如果是A股数据。请一定要使用复权数据，前复权和后复权均可
 */
public interface IndicatorComputeCarrier<TIME extends Comparable<? super TIME>> extends FixedWindowCarrier<TIME>{
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
	 * @return 涨跌额-基于前收价格  
	 */
	double getPriceChange();
	/**
	 * @param priceChange 涨跌额-基于前收价格
	 */
	void setPriceChange(double priceChange);

	
	/**
	 * @return 涨跌幅（百分点）-基于前收价格
	 */
	double getPctChange();
	/**
	 * @param pctChange 涨跌幅（百分点）-基于前收价格
	 */
	void setPctChange(double pctChange);
	

	/**
	 * @return 价格震幅（百分点）-基于前收价格
	 */
	double getAmplitude();
	/**
	 * @param amplitude 价格震幅（百分点）-基于前收价格
	 */
	void setAmplitude(double amplitude);

}


```

#### 实现一个新的指标计算

1.  指标类 继承 Indicator指标父类 ，并定义指标值（如果有）
2.  指标类 构建一个计算器静态内部类 继承 IndicatorCalculator指标父类计算器 ，并实现FixedWindowCircularCalculator抽象类中的 executeCalculate(Function<CARRIER, RESULT> propertyGetter)propertySetter) 方法，

```java
	/**
	 * 执行计算，由子类具体某个指标的计算器实现
	 * @param propertyGetter  委托方法，从载体获取计算结果
	 * @return
	 */
	protected abstract RESULT executeCalculate(Function<CARRIER, RESULT> propertyGetter);
```

#### 指标实现举例：移动平均线MA

1.  指标类 继承 Indicator指标父类 ，并定义指标值（如果有）
2.  指标类 构建一个计算器静态内部类 继承 IndicatorCalculator指标父类计算器 ，并实现FixedWindowCircularCalculator抽象类中的 executeCalculate(Function<CARRIER, RESULT> propertyGetter)propertySetter) 方法，

```java

/**
 * @author Rootfive
 * </pre>
 * 移动平均线，英文名称为MovingAverage，简称MA，原本意思是移动平均。由于我们将其制作成线形，所以一般称为移动平均线，简称均线。
 * 均线是将某一段吋间的收盘价之和除以该周期，比如日线MA5指5天内的收盘价除以5,
 * 其计算公式为： MA(5)=(C1+C2+C3十C4+C5)/5
 * 其中：
 *    Cn为第n日收盘价。例如C1，则为第1日收盘价。
 *
 *    用EMA追底，用MA识顶。 例如，用20天EMA判断底部，用20天MA判断顶部。
 * </pre>
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
	public static <CARRIER extends IndicatorComputeCarrier<?>>  IndicatorCalculator<CARRIER, Double> buildCalculator(int capacity,int indicatorSetScale) {
		return new MACalculator<>(capacity,indicatorSetScale);
	}

	/**
	 * 内部类实现MA计算器
	 * @author Rootfive
	 */
	private static class MACalculator<CARRIER extends IndicatorComputeCarrier<?>>  extends IndicatorCalculator<CARRIER, Double> {
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
		protected Double executeCalculate(Function<CARRIER, Double> propertyGetter) {
			double closeSumValue = DoubleUtils.ZERO;
			for (int i = 0; i < carrierData.length; i++) {
				closeSumValue = closeSumValue+ getPrevByNum(i).getClose();
			}
			return DoubleUtils.divide(closeSumValue, circularPeriod, indicatorSetScale);
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




