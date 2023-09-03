# xianlaocai-quant

## 介绍
xianlaocai-quant是一个父工程，目前仅仅开源一个包，会逐步开源子模块。

### quant-data-indicator
主要是用来计算技术指标的，后续会持续更新。欢迎大家多提bug和建议，参与贡献.

### quant-data-indicator
基于Java实现常见指标MACD,RSI,BOLL,KDJ,CCI,MA,EMA,BIAS,TD,WR等,全部封装，简洁且准确，能非常方便的应用在各自股票股市技术分析，股票自动程序化交易,数字货币BTC等量化等领域.

## 软件架构
java最低最低JDK1.8（Java8），Maven聚合父子项目，会逐步开源子模块。

### quant-data-indicator中指标计算实现说明
#### 3个重要的类
1、Indicator：[指标]父类（所有指标都必须继承的抽象父类）

```java
public abstract class Indicator {

}
```

2、IndicatorCalculator：[指标计算器]父类（所有[指标计算器]都必须继承的抽象父类）

```java
/**
 * 指标计算 计算器
 * 
 * @author Rootfive
 */
public abstract class IndicatorCalculator<T extends Indicator> {

	//........
	//其他代码请看代码实现
	//........


	/** 环形数组 */
	protected final transient IndicatorCalculatorCarrier<T>[] circularElementData;

	/** 指标的计算周期或时间周期 */
	protected final transient BigDecimal periodCapacity;
	
	

	/**
	 * 执行计算，新的数据
	 * @param carrier  指标计算载体(未包含结算结果)
	 * @return 指标计算载体(包含了已经计算的指标结果)
	 */
	public T execute(IndicatorCalculatorCarrier<T> carrier) {
		//........
		//具体代码请看代码实现
		//........
	}

	/**
	 * 执行计算，由子类具体某个指标的计算器实现
	 * 
	 * @return
	 */
	protected abstract T executeCalculate();
	
	
	//........
	//其他代码请看代码实现
	//........
}
```

3、IndicatorCalculatorCarrier：指标计算载体，用来计算指标并返回

```java
/**
 * 指标计算载体 IndicatorCalculatorCarrier
 * 
 * 用来计算指标并返回
 * 
 * @author Rootfive
 * 
 * 注意：本载体下面的行情数据，如果是A股。请一定要使用复权数据，前复权和后复权均可，未复权的数据计算，可能会有问题
 */
@Data
@NoArgsConstructor
public  class IndicatorCalculatorCarrier<T extends Indicator> {

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
	public IndicatorCalculatorCarrier(IndicatorCalculatorCarrier<?> carrier) {
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
 * 
 * 移动平均线，英文名称为MovingAverage，简称MA，原本意思是移动平均。由于我们将其制作成线形，所以一般称为移动平均线，简称均线。
 * 均线是将某一段吋间的收盘价之和除以该周期，比如日线MA5指5天内的收盘价除以5,
 * 其计算公式为： MA(5)=(C1+C2+C3十C4+C5)/5
 * 其中：
 *    Cn为第n日收盘价。例如C1，则为第1日收盘价。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class MA extends Indicator {

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
			if (isFullCapacity) {
				BigDecimal closeSumValue = Arrays.stream(super.circularElementData).map(IndicatorCalculatorCarrier::getClose).reduce(BigDecimal::add).get();
				maValue = divide(closeSumValue, periodCapacity, 2);
				return new MA(maValue);
			}
			return null;
		}

	}

}

```


## 安装教程
1.  Ecplse或者IDEA

## 使用说明
1. Maven项目的 pom.xml直接引用

```xml
	<dependency>
		<groupId>com.xianlaocai.quant</groupId>
		<artifactId>quant-data-indicator</artifactId>
		<version>XLCQ20230902</version>
	</dependency>
```


2.  请看项目中的测试例子：/quant-data-indicator/src/test/java/xlc/quant/data/indicator/test/DemoTest.java



## 参与贡献

1.  Fork 本仓库
2.  新建 Feat_xxx 分支
3.  提交代码
4.  新建 Pull Request

### 指标实现示例
如果你想自定义实现一个的的指标，可以按照下上述指标计算实现说明


## 交流群信息
暂时没有，后续筹备，可以先加QQ：2236067977（备注：quant），欢迎指出不足。谢谢

