package fr.inria.wimmics.openrdf.util;

import com.ibm.icu.text.DecimalFormat;

public class Util {
	public static double round(double number) {
		return Double.parseDouble(new DecimalFormat("#.####").format(number));
	}
}
