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
		<version>XLCQ20231024</version>
	</dependency>
```


Gradle

```xml
// https://mvnrepository.com/artifact/com.xianlaocai.quant/quant-data-indicator
implementation group: 'com.xianlaocai.quant', name: 'quant-data-indicator', version: 'XLCQ20231024'

```
### 基表计算示例 
示例地址：        /quant-data-indicator/src/test/java/xlc/quant/data/indicator/test/DemoTest.java

## 软件架构
java最低最低JDK1.8（Java8），Maven聚合父子项目，会逐步开源子模块。


### quant-data-indicator中指标计算实现说明

#### 实现一个新的指标计算

1.  指标类 继承 Indicator指标顶级父类 ，并定义指标值（如果有）
2.  指标类 构建一个计算器静态内部类 继承 IndicatorCalculator指标计算器抽象父类 ，并实现TimeSeriesDataRollingWindowQueue抽象类中的 protected abstract INDI executeCalculate(Function<CARRIER, INDI> propertyGetter) 方法

```java
	/**
	 * 执行计算，由子类具体某个指标的计算器实现
	 * @param propertyGetter  委托方法，从载体类获取计算结果的方法
	 * @return
	 */
	protected abstract INDI executeCalculate(Function<CARRIER, INDI> propertyGetter);
```

#### 指标实现举例：移动平均线MA

1.  指标类 继承 Indicator指标顶级父类 ，并定义指标值（如果有）
2.  指标类 构建一个计算器静态内部类 继承 IndicatorCalculator指标计算器抽象父类 ，并实现TimeSeriesDataRollingWindowQueue抽象类中的 protected abstract INDI executeCalculate(Function<CARRIER, INDI> propertyGetter) 方法

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
	public static <CARRIER extends IndicatorCalculateCarrier<?>>  IndicatorCalculator<CARRIER, Double> buildCalculator(int capacity,int indicatorSetScale) {
		return new MACalculator<>(capacity,indicatorSetScale);
	}

	/**
	 * 内部类实现MA计算器
	 * @author Rootfive
	 */
	private static class MACalculator<CARRIER extends IndicatorCalculateCarrier<?>>  extends IndicatorCalculator<CARRIER, Double> {
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
			for (int i = 0; i < size(); i++) {
				closeSumValue = closeSumValue+ get(i).getClose();
			}
			return DoubleUtils.divide(closeSumValue, size(), indicatorSetScale);
		}
	}
}


```






## 升级日志 倒叙


### XLCQ20231024
1、修复xlc.quant.data.indicator.TimeSeriesDataRollingWindowQueue.getTail()在队列未满时，获取尾元素异常

2、增加指标管理员：xlc.quant.data.indicator.IndicatorCalculateManager<CARRIER>，可以计算多个指标


### 早起版本：省略
7.XLCQ20231020  
6.XLCQ20231019  
5.XLCQ20230920  
4.XLCQ20230911  
3.XLCQ20230910  
2.XLCQ20230907  
1.XLCQ20230902  