package br.com.anteros.vendas.gui;

public class MaskUtils {

	final static String MASK_TELEFONE8 = "(##) ##-####";
	final static String MASK_TELEFONE9 = "(##) ###-####";
	final static String MASK_TELEFONE10 = "(##) ####-####";
	final static String MASK_TELEFONE11 = "(##) #####-####";
	final static String MASK_CEP = "#####-###";

	public static String formatTelefone(String n) {
		final String numero = n.trim().replace(" ", "");
		final int size = numero.length();

		String mask = "";

		if (size == 8) {
			mask = MASK_TELEFONE8;
		} else if (size == 9) {
			mask = MASK_TELEFONE9;
		} else if (size == 10) {
			mask = MASK_TELEFONE10;
		} else if (size == 11) {
			mask = MASK_TELEFONE11;
		} else {
			return n;
		}

		return format(n, mask);
	}

	public static String formatCEP(String cep) {
		return format(cep, MASK_CEP);
	}

	public static String format(String n, String mask) {
		final String numero = n.trim().replace(" ", "");
		String maskedNumber = "";

		int j = 0;
		for (int i = 0; (i < mask.length() && j < numero.length()); i++) {
			char m = mask.charAt(i);
			if (m == '#') {
				char f = numero.charAt(j);
				maskedNumber += f;
				j++;
			} else {
				maskedNumber += m;
			}
		}

		return maskedNumber;
	}
	
	public static boolean isTelefoneValido(String telefone) {
		// Retira a mascara do telefone, se possuir
		telefone = telefone.replaceAll("[^0-9]", "");
	
		// Verifica se a quantidade de digitos é maior que 10
		return telefone.length() >= 10;
	}
}
