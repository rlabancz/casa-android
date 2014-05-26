package ca.rldesigns.android.casa.utils;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class Formatter {
	private static DecimalFormat coordinateFormatter = new DecimalFormat("##.#######");
	private static NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US);
	private static DecimalFormat decimalFormatter = new DecimalFormat("'$'#,###");
	private static DecimalFormat radiusFormatter = new DecimalFormat("## km");

	public static String formatCoordinate(double coord) {
		String output = "";
		output = coordinateFormatter.format(coord);
		return output;
	}

	public static String formatCurrency(double input) {
		String output = "";
		output = currencyFormatter.format(input);
		return output;
	}

	public static String formatDecimal(double input) {
		String output = "";
		output = decimalFormatter.format(input);
		return output;
	}

	public static String formatRadius(int input) {
		String output = "";
		output = radiusFormatter.format(input);
		return output;
	}
}