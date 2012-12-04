package fr.inria.wimmics.util;

import com.ibm.icu.text.DecimalFormat;

public class Util {
	public static double round(double number) {
		return Double.parseDouble(new DecimalFormat("#.####").format(number));
	}
}
