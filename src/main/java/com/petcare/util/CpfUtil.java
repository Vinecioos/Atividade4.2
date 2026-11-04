package com.petcare.util;

public final class CpfUtil {

    private CpfUtil() {
    }

    /** Remove pontos, traços e espaços: "529.982.247-25" -> "52998224725". */
    public static String normalizar(String cpf) {
        return cpf == null ? null : cpf.replaceAll("\\D", "");
    }

    /** Valida CPF já normalizado (11 dígitos + dígitos verificadores). */
    public static boolean valido(String cpf) {
        if (cpf == null || !cpf.matches("\\d{11}") || cpf.chars().distinct().count() == 1) {
            return false;
        }
        return digito(cpf, 9) == cpf.charAt(9) - '0' && digito(cpf, 10) == cpf.charAt(10) - '0';
    }

    private static int digito(String cpf, int quantidade) {
        int soma = 0;
        for (int i = 0; i < quantidade; i++) {
            soma += (cpf.charAt(i) - '0') * (quantidade + 1 - i);
        }
        int resto = (soma * 10) % 11;
        return resto == 10 ? 0 : resto;
    }
}
