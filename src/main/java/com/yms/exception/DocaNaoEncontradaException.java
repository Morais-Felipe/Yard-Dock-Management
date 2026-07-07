package com.yms.exception;

public class DocaNaoEncontradaException extends RuntimeException {
    public DocaNaoEncontradaException(int numeroDoca) {
        super("Doca número " + numeroDoca + " não encontrada.");
    }
}