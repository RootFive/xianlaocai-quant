package xlc.quant.data.indicator.test;

import java.time.LocalDate;

import xlc.quant.data.indicator.struct.RangeInXLC;

public class DemoTest2 {
    public static void main(String[] args) {
    	RangeInXLC<LocalDate> allLocalDate = RangeInXLC.all();
    	
    	System.out.println(allLocalDate.isContains(LocalDate.of(1099,1,3)));
    }
    
    
}
