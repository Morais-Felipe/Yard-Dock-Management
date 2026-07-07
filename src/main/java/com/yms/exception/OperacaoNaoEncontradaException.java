package com.yms.exception;

public class OperacaoNaoEncontradaException extends RuntimeException {
    public OperacaoNaoEncontradaException(int codigo) {
        super("Operação com código " + codigo + " não encontrada.");
    }
}