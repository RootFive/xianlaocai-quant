package xlc.quant.data.indicator.test;

public class DemoTest2 {
    public static void main(String[] args) {
    	
    	double value = 3.14159265359;
    	double truncatedValue = truncateDouble(value, 4);
    	System.out.println(truncatedValue); 

	}
	
    
    public static double truncateDouble(double value, int decimalPlaces) {
        double factor = Math.pow(10, decimalPlaces);
        return Math.round(value * factor) / factor;
    }

	

}
