package com.yms.exception;

public class DocaOcupadaException extends RuntimeException {
    public DocaOcupadaException(int numeroDoca) {
        super("Doca número " + numeroDoca + " está ocupada no momento.");
    }
}