# xianlaocai-quant

## 介绍
xianlaocai-quant是一个父工程，目前仅仅开源一个包，会逐步开源子模块。

### quant-data-indicator
主要是用来计算技术指标的，后续会持续更新。欢迎大家多提bug和建议，参与贡献.

### quant-data-indicator
基于Java实现常见指标MACD,RSI,BOLL,KDJ,CCI,MA,EMA,BIAS,TD,WR,DMI等,全部封装，简洁且准确，能非常方便的应用在各自股票股市技术分析，股票自动程序化交易,数字货币BTC等量化等领域.

## 软件架构
java最低最低JDK1.8（Java8），Maven聚合父子项目，会逐步开源子模块。

### quant-data-indicator中指标计算实现说明
#### 4个重要的类
1、FixedWindowCalculator：固定窗口 计算器

```java
/**
 * 环形固定窗口 计算器
 * 
 * @author Rootfive
 * 
 */
public abstract class CircularFixedWindowCalculator<T, FWC extends CircularFixedWindowCalculable> {

	/** 需要计算的数据：指[固定窗口环形数组]中的数据 */
	protected final transient Object [] circularData;

	/** 环形数组最大长度，[固定窗口环形数组]时间周期 */
	protected final transient BigDecimal fwcPeriod;

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
		this.fwcPeriod = new BigDecimal(fwcMax);
		this.isFullCapacityCalculate = isFullCapacityCalculate;
	}

	// ==========XXX===================
	/**
	 * 执行计算，由子类具体某个指标的计算器实现
	 * 
	 * @return
	 */
	protected abstract T executeCalculate();

	// ==========XXX===================

	/**
	 * 输入数据
	 * @param newFwc 新的固定窗口数据
	 * @return
	 */
	public synchronized T input(FWC newFwc) {
		boolean addResult = addFirst(newFwc);
		if (addResult) {
			// 新增成功
			if (!isFullCapacityCalculate || (isFullCapacityCalculate && this.isFullCapacity())) {
				// 1、不是满容计算 [或] 2满容计算且已经满容，二者条件满足其中一种。均可执行计算（指标）
				return executeCalculate();
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
public abstract  class IndicatorCalculator<T extends Indicator>  extends  CircularFixedWindowCalculator<T,IndicatorCalculatorCallback<T>> {


	//........
	//其他代码请看代码实现
	//........

	/**
	 * @param callback 新窗口数据
	 * @return
	 */
	@Override
	public T execute(IndicatorCalculatorCallback<T> callback) {
		T indicator = super.execute(callback);
		callback.setIndicator(indicator);
		return indicator;
	}
	

	
	
	//........
	//其他代码请看代码实现
	//........
}
```

4、IndicatorCalculatorCallback：指标计算载体，用来计算指标并返回

```java
/**
 * 指标计算载体
 * 
 * 用来计算指标并返回
 * 
 * @author Rootfive
 * 
 * 注意：下面的行情数据，如果是A股。请一定要使用复权数据，前复权和后复权均可
 */
@Data
@NoArgsConstructor
public  class IndicatorCalculatorCallback<T extends Indicator> implements CircularFixedWindowCalculable{

	/** 计算出来的指标结果 XXX */
	protected T indicator;

	// =======================
	// 下面的是计算条件
	// =======================
	/** 证券代码 */
	protected String symbol;

	/** 交易日期时间 */
	protected LocalDateTime tradeDateTime;

	/** 收盘价(元) */
	protected BigDecimal close;

	/** 开盘价(元) */
	protected BigDecimal open;

	/** 最高价(元) */
	protected BigDecimal high;

	/** 最低价(元) */
	protected BigDecimal low;

	/** 成交量(股/份/个) */
	protected BigDecimal volume;

	/** 成交额(元) */
	protected BigDecimal amount;

	// =======================
	// 上面的属性值，一般情况下，分时和日行情都有
	// 下面的属性值，一般情况下，分时和日行情可能有，即便是没有，也可以通过上面的属性计算得出
	// =======================
	/** 前收 (元) */
	protected BigDecimal preClose;

	/** 前收 涨跌额(元) */
	protected BigDecimal priceChange;

	/** 涨跌幅（%） */
	protected BigDecimal pctChange;

	/** 价格震幅（%） */
	protected BigDecimal amplitude;
	
	

	/**
	 * 复合指标计算时，同一个行情，可能需要同时计算多种指标时，需要转换
	 * @param carrier
	 */
	public IndicatorCalculatorCallback(IndicatorCalculatorCallback<?> carrier) {
		super();
		// this.indicator = indicator;
		this.symbol = carrier.getSymbol();
		this.tradeDateTime = carrier.getTradeDateTime();
		this.close = carrier.getClose();
		this.open = carrier.getOpen();
		this.high = carrier.getHigh();
		this.low = carrier.getLow();
		this.volume = carrier.getVolume();
		this.amount = carrier.getAmount();
		this.preClose = carrier.getPreClose();
		this.priceChange = carrier.getPriceChange();
		this.pctChange = carrier.getPctChange();
		this.amplitude = carrier.getAmplitude();
	}
}
```

#### 实现一个新的指标方法

1.  继承 Indicator指标父类 ，并定义指标值
2.  继承 IndicatorCalculator指标父类计算器 ，实现executeCalculate()方法，

```java
	/**
	 * 执行计算，由子类具体某个指标的计算器实现
	 * 
	 * @return
	 */
	protected abstract T executeCalculate();
```

#### 指标实现举例：移动平均线MA

1.  继承 Indicator指标父类 ，并定义指标值
2.  继承 IndicatorCalculator指标父类计算器 ，实现executeCalculate()方法，

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
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class MA extends Indicator {

	/** MA计算值  */
	private BigDecimal value;

	
	//=============
	//内部类分隔符 XXX
	//=============
	/**
	 * 构建-计算器
	 * @param capacity
	 * @return
	 */
	public static IndicatorCalculator<MA> buildCalculator(int capacity) {
		return new MACalculator(capacity);
	}

	/**
	 * 内部类实现MA计算器
	 * @author Rootfive
	 */
	private static class MACalculator extends IndicatorCalculator<MA> {

		/**
		 * @param capacity
		 */
		MACalculator(int capacity) {
			super(capacity, true);
		}

		@Override
		protected MA executeCalculate() {
			BigDecimal maValue = null;
			if (isFullCapacity()) {
				BigDecimal closeSumValue = super.getCalculatorListData().stream().map(IndicatorCalculatorCallback::getClose).reduce(BigDecimal::add).get();
				maValue = divide(closeSumValue, fwcPeriod, 2);
				return new MA(maValue);
			}
			return null;
		}

	}

}


```


## 使用说明

### Maven直接引用

Maven地址（更新较快）阿里云Maven仓库搜索关键词：quant-data-indicator， 阿里云Maven仓库地址：https://developer.aliyun.com/mvn/search
Maven地址（更新较慢）：https://mvnrepository.com/artifact/com.xianlaocai.quant/quant-data-indicator

1.  IDE: Ecplse或者IDEA均可

2. Maven

```xml
	<dependency>
		<groupId>com.xianlaocai.quant</groupId>
		<artifactId>quant-data-indicator</artifactId>
		<version>XLCQ20230911</version>
	</dependency>
```

Gradle

```xml
// https://mvnrepository.com/artifact/com.xianlaocai.quant/quant-data-indicator
implementation group: 'com.xianlaocai.quant', name: 'quant-data-indicator', version: 'XLCQ20230911'

```

2.  请看项目中的测试例子：/quant-data-indicator/src/test/java/xlc/quant/data/indicator/test/DemoTest.java



## 参与贡献

1.  Fork 本仓库
2.  新建 Feat_xxx 分支
3.  提交代码
4.  新建 Pull Request


## 交流群信息
暂时没有，后续筹备，可以先加QQ：2236067977（备注：quant），欢迎指出不足。谢谢

