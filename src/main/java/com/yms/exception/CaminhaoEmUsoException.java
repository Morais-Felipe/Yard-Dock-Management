package com.yms.exception;

public class CaminhaoEmUsoException extends RuntimeException{
    public CaminhaoEmUsoException(String placa){
        super("Caminhão com placa '" + placa + "' possui operações vinculadas e não pode ser excluído.");
    }
}
