package br.com.ifrs.meuifpoa.utils;

import android.util.Log;

import br.com.caelum.stella.validation.CPFValidator;
import br.com.caelum.stella.validation.InvalidStateException;

public class CPFValidador {

    public static boolean validarCpf(String cpf) {
        cpf = cpf.replaceAll("[^\\d]", ""); // Remove formatação do CPF
        CPFValidator cpfValidator = new CPFValidator();
        try {
            cpfValidator.assertValid(cpf);
            return true;
        } catch (InvalidStateException e) {
            Log.e("CPFValidation", "Erro ao validar CPF: " + e.getInvalidMessages());
            return false;
        }
    }

}
