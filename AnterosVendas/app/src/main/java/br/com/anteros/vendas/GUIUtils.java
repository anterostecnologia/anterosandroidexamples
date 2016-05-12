package br.com.anteros.vendas;

import android.app.Activity;
import android.util.DisplayMetrics;
import java.util.Locale;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;

public class GUIUtils {

	private static Locale ptBr;
	public static DecimalFormat moedaFormat;
	private static DecimalFormatSymbols symbols;
	private static final String PERCENTUAL = "0.01";
	private static final String PATTERN = "###,###,##0.00";

	static {
		ptBr = new Locale("pt", "BR");
		moedaFormat = (DecimalFormat) NumberFormat.getCurrencyInstance(ptBr);
		moedaFormat.setNegativePrefix("-");
		moedaFormat.setNegativeSuffix("");
		symbols = new DecimalFormatSymbols(ptBr);
	}

	public static boolean isTablet(Activity activity) {
		if (activity == null)
			return false;

		DisplayMetrics displayMetrics = activity.getResources().getDisplayMetrics();
		int widthPixels = displayMetrics.widthPixels;
		int heightPixels = displayMetrics.heightPixels;

		float widthDpi = displayMetrics.xdpi;
		float heightDpi = displayMetrics.ydpi;

		float widthInches = widthPixels / widthDpi;
		float heightInches = heightPixels / heightDpi;

		double diagonalInches = Math.sqrt((widthInches * widthInches) + (heightInches * heightInches));

		BigDecimal bd = new BigDecimal(Double.toString(diagonalInches));
		bd = bd.setScale(0, BigDecimal.ROUND_CEILING);

		return !(Math.round(diagonalInches) < 7 || (Math.round(diagonalInches) >= 7 && (displayMetrics.densityDpi >= DisplayMetrics.DENSITY_HIGH)));
	}

	public static String formatMoeda(BigDecimal valor) {
		if (valor == null) {
			valor = BigDecimal.ZERO;
		}
		return moedaFormat.format(valor).replace("R$", "");
	}
}
