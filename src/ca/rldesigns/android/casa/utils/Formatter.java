package ca.rldesigns.android.casa.utils;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class Formatter {
	static DecimalFormat decimalFormatter = new DecimalFormat("'$'#,###");
	static NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US);

	public static String formatDecimal(double input) {
		String output = "";
		output = decimalFormatter.format(input);
		return output;
	}

	public static String formatCurrency(double input) {
		String output = "";
		output = currencyFormatter.format(input);
		return output;
	}
}
