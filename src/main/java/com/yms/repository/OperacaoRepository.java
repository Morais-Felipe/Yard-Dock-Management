package com.yms.repository;

import com.yms.model.Operacao;
import com.yms.model.enums.StatusOperacao;
import com.yms.model.enums.TipoOperacao;
import com.yms.persistence.JsonStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class OperacaoRepository {
    private static final String ARQUIVO = "operacoes.json";

    private List<Operacao> operacoes;

    public OperacaoRepository(){
        this.operacoes = JsonStorage.carregar(ARQUIVO, Operacao.class);
    }

    public void salvar(Operacao operacao){
        operacoes.removeIf(o -> o.getCodigoOperacao() == operacao.getCodigoOperacao());
        operacoes.add(operacao);
        JsonStorage.salvar(operacoes, ARQUIVO);
    }

    public Optional<Operacao> buscarPorCodigo(int codigo){
        return operacoes.stream()
                .filter(o -> o.getCodigoOperacao() == codigo)
                .findFirst();
    }

    public boolean deletar(int codigo){
        boolean removido = operacoes.removeIf(o -> o.getCodigoOperacao() == codigo);
        if(removido){
            JsonStorage.salvar(operacoes, ARQUIVO);
        }

        return removido;
    }

    public List<Operacao> listarTodos() {
        return new ArrayList<>(operacoes);
    }

    //busca por operacoes ativas por tipo
    public List<Operacao> listarAtivasPorTipo(TipoOperacao tipo){
        return operacoes.stream()
                .filter(o -> o.getTipoOperacao() == tipo)
                .filter(o -> o.getStatusOperacao() == StatusOperacao.AGENDADA || o.getStatusOperacao() == StatusOperacao.EM_ANDAMENTO)
                .collect(Collectors.toList());

    }

    //historico de operacoes de um caminhão especifico
    public List<Operacao> listarPorCaminhao(String placa){
        return operacoes.stream()
                .filter(o -> o.getPlacaCaminhao().equalsIgnoreCase(placa.trim()))
                .collect(Collectors.toList());
    }

    //operacoes de uma doca especifica
    public List<Operacao> listarPorDoca(int numeroDoca){
        return operacoes.stream()
                .filter(o -> o.getNumeroDoca() == numeroDoca)
                .collect(Collectors.toList());
    }

    //verifica se um caminhao possui uma operacao vinculada
    public boolean caminhaoTemOperacao(String placa){
        return operacoes.stream()
                .anyMatch(o -> o.getPlacaCaminhao().equalsIgnoreCase(placa.trim()));
    }

    //verifica se uma doca possui uma operacao vinculada
    public boolean docaTemOperacao(int numeroDoca){
        return operacoes.stream()
                .anyMatch(o -> o.getNumeroDoca() == numeroDoca);
    }

    //retorna o maior codigo existente (usado para gerar o proximo codigo)
    public int maiorCodigo(){
        return operacoes.stream()
                .mapToInt(Operacao::getCodigoOperacao)
                .max()
                .orElse(0); // se nao houver operacoes, starta com 0
    }
}
