package br.com.anteros.vendas;

import android.app.Activity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import java.math.BigDecimal;

public class GUIUtils {

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
}
