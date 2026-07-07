package com.yms.repository;

import com.yms.model.Doca;
import com.yms.persistence.JsonStorage;

import javax.print.Doc;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DocaRepository {
    private static final String ARQUIVO = "docas.json";

    private List<Doca> docas;

    public DocaRepository(){
        this.docas = JsonStorage.carregar(ARQUIVO, Doca.class);
    }

    public void salvar(Doca doca){
        docas.removeIf(d -> d.getNumeroDoca() == doca.getNumeroDoca());
        docas.add(doca);
        JsonStorage.salvar(docas, ARQUIVO);
    }

    public Optional<Doca> buscarPorNumero(int numeroDoca){
        return docas.stream()
                .filter(d -> d.getNumeroDoca() == numeroDoca)
                .findFirst();
    }

    public boolean deletar(int numeroDoca){
        boolean removido = docas.removeIf(d -> d.getNumeroDoca() == numeroDoca);
        if(removido){
            JsonStorage.salvar(docas, ARQUIVO);
        }
        return removido;
    }

    public List<Doca> listarTodos(){
        return new ArrayList<>(docas);
    }

    public boolean existe(int numeroDoca){
        return buscarPorNumero(numeroDoca).isPresent();
    }
}
