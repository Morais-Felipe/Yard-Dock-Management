package com.yms.service;

import com.yms.exception.CaminhaoEmUsoException;
import com.yms.exception.CaminhaoNaoEncontradoException;
import com.yms.model.Caminhao;
import com.yms.repository.CaminhaoRepository;
import com.yms.repository.OperacaoRepository;

import java.util.List;

public class CaminhaoService {

    private final CaminhaoRepository caminhaoRepository;
    private final OperacaoRepository operacaoRepository;

    public CaminhaoService(CaminhaoRepository caminhaoRepository, OperacaoRepository operacaoRepository){
        this.caminhaoRepository = caminhaoRepository;
        this.operacaoRepository = operacaoRepository;
    }

    public void cadastrar(Caminhao caminhao){
        if(caminhaoRepository.existe(caminhao.getPlaca())){
            throw new IllegalArgumentException("Já existe um caminhão cadastrado com a placa " + caminhao.getPlaca());
        }
        caminhaoRepository.salvar(caminhao);
    }

    public Caminhao buscarPorPlaca(String placa){
        return caminhaoRepository.buscarPorPlaca(placa).orElseThrow(() -> new CaminhaoNaoEncontradoException(placa));
    }

    public void editar(String placa, String novaTransportadora, String novoTipoVeiculo, String novoMotorista, double novaCapacidade){
        Caminhao caminhao = buscarPorPlaca(placa);

        caminhao.setTransportadora(novaTransportadora);
        caminhao.setTipoVeiculo(novoTipoVeiculo);
        caminhao.setMotorista(novoMotorista);
        caminhao.setCapacidadeCarga(novaCapacidade);
        caminhaoRepository.salvar(caminhao);
    }

    public void deletar(String placa){
        buscarPorPlaca(placa);
        if(operacaoRepository.caminhaoTemOperacao(placa)){
            throw new CaminhaoEmUsoException(placa);
        }
        caminhaoRepository.deletar(placa);
    }

    public List<Caminhao> listarTodos(){
        return caminhaoRepository.listarTodos();
    }
}
