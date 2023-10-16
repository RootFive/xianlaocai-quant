package xlc.quant.data.indicator;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;


public class VarianceExample2 {
    public static void main(String[] args) {
    	ArrayDeque<Object> arrayDeque = new ArrayDeque<>(3);
    	arrayDeque.add(arrayDeque);
    	
    	arrayDeque.getFirst();
    	ArrayList<ArrayDeque<Object>> arrayList = new ArrayList<>(3);
    	arrayList.add(arrayDeque);
    	arrayList.get(0);
    	arrayList.toArray();
    	
    	
    	Arrays.asList(arrayDeque);
    	
    	new LinkedList<>();
    	
    	
    	
    }
}
