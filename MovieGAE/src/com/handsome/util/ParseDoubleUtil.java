package com.handsome.util;

public class ParseDoubleUtil {

	public ParseDoubleUtil() {
		super();
		
	}
	
	//1.didn't work to [java.lang.NumberFormatException: empty String]
	public static double parseDouble(String strNumber) {
		if (strNumber != null && strNumber.length() > 0 && !strNumber.isEmpty()) {
			try {
				return Double.parseDouble(""+strNumber);//failed => 2.try to add "" to fix [java.lang.NumberFormatException: empty String at sun.misc.FloatingDecimal.readJavaFormatString]
			} catch (Exception e) {
				return 0; 
			}
		} else
			return 0;
	}

}
