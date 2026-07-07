package com.yms.exception;

public class DocaEmUsoException extends RuntimeException {
    public DocaEmUsoException(int numeroDoca) {
        super("Doca número " + numeroDoca + " possui operações vinculadas e não pode ser excluída.");
    }
}