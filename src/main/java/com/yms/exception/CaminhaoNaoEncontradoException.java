package com.yms.exception;

public class CaminhaoNaoEncontradoException extends RuntimeException{
    public CaminhaoNaoEncontradoException(String placa){
        super("CAminhao com placa '" + placa + "' não encontrado.");
    }
}
