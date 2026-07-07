package com.yms.service;

import com.yms.exception.DocaEmUsoException;
import com.yms.exception.DocaNaoEncontradaException;
import com.yms.model.Doca;
import com.yms.model.enums.StatusDoca;
import com.yms.model.enums.TipoDoca;
import com.yms.repository.DocaRepository;
import com.yms.repository.OperacaoRepository;

import java.util.List;

public class DocaService {
    private final DocaRepository docaRepository;
    private final OperacaoRepository operacaoRepository;

    public DocaService(DocaRepository docaRepository, OperacaoRepository operacaoRepository){
        this.docaRepository = docaRepository;
        this.operacaoRepository = operacaoRepository;
    }

    public void cadastrar(Doca doca){
        if(docaRepository.existe(doca.getNumeroDoca())){
            throw new IllegalArgumentException("Já existe uma doca cadastrada com u numero: " + doca.getNumeroDoca());
        }
        docaRepository.salvar(doca);
    }

    public Doca buscarPorNumero(int numeroDoca){
        return docaRepository.buscarPorNumero(numeroDoca).orElseThrow(()-> new DocaNaoEncontradaException(numeroDoca));
    }

    public void editar(int numeroDoca, TipoDoca novoTipo, double novaCapacidade){
        Doca doca = buscarPorNumero(numeroDoca);
        doca.setTipoDoca(novoTipo);
        doca.setCapacidadeMaxima(novaCapacidade);
        docaRepository.salvar(doca);
    }

    public void atualizarStatus(int numeroDoca, StatusDoca novoStatus){
        Doca doca = buscarPorNumero(numeroDoca);
        doca.setStatusDoca(novoStatus);
        docaRepository.salvar(doca);
    }

    public void deletar(int numeroDoca){
        buscarPorNumero(numeroDoca);
        if(operacaoRepository.docaTemOperacao(numeroDoca)){
            throw new DocaEmUsoException(numeroDoca);
        }
        docaRepository.deletar(numeroDoca);
    }

    public List<Doca> listarTodos(){
        return docaRepository.listarTodos();
    }
}
