package xlc.quant.data.indicator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 指标数仓
 * 
 * @param <CARRIER> 指标计算载体
 * @param <MANAGER> 指标仓管
 */
public abstract class IndicatorDataWarehouse<CARRIER extends IndicatorCalculateCarrier<?>, MANAGER extends IndicatorWarehouseManager<?,CARRIER>> {

	/**
	 * 指标数仓
	 * 
	 * <pre>
	 * Key:   代码、交易对  
	 * Value: 指标仓管
	 * </pre>
	 */
	protected final ConcurrentHashMap<String, MANAGER> dataWarehouse = new ConcurrentHashMap<>();

	/**
	 * 入仓-接收载体数据，适用于T+0模式
	 * @param carrierData
	 * @return
	 */
	public boolean receiving(CARRIER carrierData) {
		//交易代码->
		String symbol = carrierData.getSymbol();
		
		//交易代码->仓管
		MANAGER warehouseManager = getManager(symbol);

		if (warehouseManager == null) {
			warehouseManager = assignManager(carrierData);
			dataWarehouse.put(symbol, warehouseManager);
		}
		//交易代码->仓管->接受数据
		return warehouseManager.accept(carrierData);
	}
	
	
	/**
	 * 入仓-批量接收载体数据，适用于T+N模式
	 * @param carrierDataList
	 * @return
	 */
	public  boolean receiving(List<CARRIER> carrierDataList) {
		//循环批量接收指标
		for (CARRIER carrierData : carrierDataList) {
			this.receiving(carrierData);
		}
		return true;
	}
	
	/**
	 * 指定交易代码-按照时间倒叙
	 * @param symbol
	 * @return
	 */
	public List<CARRIER> getDataList(String symbol) {
		MANAGER manager = getManager(symbol);
		if (manager == null || manager.isEmpty()) {
			return Collections.emptyList();
		}else {
			int size = manager.size();
			List<CARRIER> dataList = new ArrayList<>(size);
			for (int i = 0; i < size; i++) {
				dataList.add(manager.get(i));
			}
			return dataList;
		}
	}
	

	/**
	 * 分配指标仓管
	 * 
	 * @param symbol
	 * @return
	 */
	protected abstract MANAGER assignManager(CARRIER carrierData);
	
	/**
	 * 重置-指标仓管
	 * 
	 * @param symbol
	 * @param manager
	 * @return
	 */
	public boolean resetManager(CARRIER carrierData, MANAGER manager) {
		//交易代码->
		String symbol = carrierData.getSymbol();
		if (manager == null) {
			manager = this.assignManager(carrierData);
		}
		dataWarehouse.put(symbol, manager);
		return true;
	}

	/**
	 * 重置-指标仓管
	 * 
	 * @param symbol
	 * @return
	 */
	public boolean resetManager(CARRIER carrierData) {
		MANAGER manager = this.assignManager(carrierData);
		//交易代码->
		String symbol = carrierData.getSymbol();
		dataWarehouse.put(symbol, manager);
		return true;
	}
	
	/**
	 * @return 获取当前-数仓
	 */
	public ConcurrentHashMap<String, MANAGER> getDataWarehouse() {
		return dataWarehouse;
	}
	
	/**
	 * 数仓管理员
	 * @return
	 */
	public Collection<MANAGER> getDataWarehouseManager() {
		return dataWarehouse.values();
	}
	
	/**
	 * @return 获取当前-数仓
	 */
	public MANAGER getManager(String symbol) {
		return dataWarehouse.get(symbol);
	}
	
	
}
