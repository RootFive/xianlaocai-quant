package xlc.quant.data.indicator;

/**
 * 执行器
 * 
 * @author Rootfive
 */
public interface Executor<RESULT, T> {

	RESULT  execute(T e);
}
