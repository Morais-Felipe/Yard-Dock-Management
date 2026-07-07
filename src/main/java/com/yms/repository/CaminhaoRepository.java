package com.yms.repository;

import com.yms.model.Caminhao;
import com.yms.persistence.JsonStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CaminhaoRepository {
    private static final String ARQUIVO = "caminhoes.json";

    //lista em memoria (buscas serao feitas em memoria)
    private List<Caminhao> caminhoes;

    //repo criado -> carrega dados json
    public CaminhaoRepository(){
        this.caminhoes = JsonStorage.carregar(ARQUIVO, Caminhao.class);
    }

    //salva ou att um caminhao
    //se ja houver mesma placa -> substituir. Se não, adiciona
    public void salvar(Caminhao caminhao){
        //remove existente (se houver)
        caminhoes.removeIf(c -> c.getPlaca().equals(caminhao.getPlaca()));
        caminhoes.add(caminhao);
        JsonStorage.salvar(caminhoes, ARQUIVO);
    }

    //busca caminhao pela placa
    //optional = pode retornar caminhao ou nada, se nao encontrar
    public Optional<Caminhao> buscarPorPlaca(String placa) {
        return caminhoes.stream()
                .filter(c -> c.getPlaca().equalsIgnoreCase(placa.trim()))
                .findFirst();
    }

    //deleta um caminhao pela placa. Retorna true se encontrou e deletou, false se nao existia
    public boolean deletar(String placa){
        boolean removido = caminhoes.removeIf(c -> c.getPlaca().equalsIgnoreCase(placa.trim()));
        if(removido){
            JsonStorage.salvar(caminhoes, ARQUIVO);
        }
        return removido;
    }

    //retorna todos os caminhoes
    public List<Caminhao> listarTodos(){
        return new ArrayList<>(caminhoes); //cópia, protege lista interna
    }

    //verifica se existe caminhao com placa informada
    public boolean existe(String placa){
        return buscarPorPlaca(placa).isPresent();
    }
}
