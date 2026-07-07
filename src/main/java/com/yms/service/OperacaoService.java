package com.yms.service;

import com.yms.exception.DocaOcupadaException;
import com.yms.exception.OperacaoNaoEncontradaException;
import com.yms.model.Doca;
import com.yms.model.Operacao;
import com.yms.model.enums.StatusDoca;
import com.yms.model.enums.StatusOperacao;
import com.yms.model.enums.TipoOperacao;
import com.yms.repository.OperacaoRepository;

import java.time.LocalDateTime;
import java.util.List;

public class OperacaoService {

    private final OperacaoRepository operacaoRepository;
    private final CaminhaoService caminhaoService;
    private final DocaService docaService;

    public OperacaoService(OperacaoRepository operacaoRepository, CaminhaoService caminhaoService, DocaService docaService){
        this.operacaoRepository = operacaoRepository;
        this.caminhaoService = caminhaoService;
        this.docaService = docaService;
    }

    public Operacao cadastrar(String placaCaminhao, int numeroDoca, LocalDateTime dataHoraChegada, TipoOperacao tipoOperacao, String produto, double pesoCarga){
        caminhaoService.buscarPorPlaca(placaCaminhao);

        Doca doca = docaService.buscarPorNumero(numeroDoca);
        if(doca.getStatusDoca() == StatusDoca.OCUPADA){
            throw new DocaOcupadaException(numeroDoca);
        }

        int codigo = operacaoRepository.maiorCodigo() + 1;

        Operacao operacao = new Operacao(codigo, placaCaminhao, numeroDoca, dataHoraChegada, tipoOperacao, produto, pesoCarga);

        operacaoRepository.salvar(operacao);

        return operacao;
    }

    public Operacao buscarPorCodigo(int codigo){
        return operacaoRepository.buscarPorCodigo(codigo).orElseThrow(() -> new OperacaoNaoEncontradaException(codigo));
    }

    public void iniciar(int codigo){
        Operacao operacao = buscarPorCodigo(codigo);

        if(operacao.getStatusOperacao() != StatusOperacao.AGENDADA){
            throw new IllegalStateException("Operacao " +codigo + "não pode ser iniciada. Status atual: " +operacao.getStatusOperacao());
        }

        operacao.setStatusOperacao(StatusOperacao.EM_ANDAMENTO);
        operacao.setHoraInicio(LocalDateTime.now());
        operacaoRepository.salvar(operacao);

        docaService.atualizarStatus(operacao.getNumeroDoca(), StatusDoca.OCUPADA);
    }

    public void concluir(int codigo){
        Operacao operacao = buscarPorCodigo(codigo);

        if (operacao.getStatusOperacao() != StatusOperacao.EM_ANDAMENTO) {
            throw new IllegalStateException("Operacao " +codigo + "não pode ser iniciada. Status atual: " +operacao.getStatusOperacao());
        }

        operacao.setStatusOperacao(StatusOperacao.CONCLUIDA);
        operacao.setHoraTermino(LocalDateTime.now());
        operacaoRepository.salvar(operacao);

        docaService.atualizarStatus(operacao.getNumeroDoca(), StatusDoca.LIVRE);
    }

    public void cancelar(int codigo){
        Operacao operacao = buscarPorCodigo(codigo);

        if(operacao.getStatusOperacao() == StatusOperacao.CONCLUIDA || operacao.getStatusOperacao() == StatusOperacao.CANCELADA){
            throw new IllegalStateException("Operacao " + codigo + "não pode ser cancelada. Status atual: " +operacao.getStatusOperacao());
        }

        if(operacao.getStatusOperacao() == StatusOperacao.EM_ANDAMENTO){
            docaService.atualizarStatus(operacao.getNumeroDoca(), StatusDoca.LIVRE);
        }

        operacao.setStatusOperacao(StatusOperacao.CANCELADA);
        operacaoRepository.salvar(operacao);
    }

    public void editar(int codigo, String novoProduto, double novoPeso, LocalDateTime novaChegada, TipoOperacao novoTipo){
        Operacao operacao = buscarPorCodigo(codigo);

        if(operacao.getStatusOperacao() != StatusOperacao.AGENDADA){
            throw new IllegalStateException("Só é possivel editar operações com status AGENDADA.");
        }

        operacao.setProduto(novoProduto);
        operacao.setPesoCarga(novoPeso);
        operacao.setDataHoraChegada(novaChegada);
        operacao.setTipoOperacao(novoTipo);
        operacaoRepository.salvar(operacao);
    }

    public void deletar(int codigo){
        Operacao operacao = buscarPorCodigo(codigo);

        if(operacao.getStatusOperacao() == StatusOperacao.EM_ANDAMENTO){
            throw new IllegalStateException("Não é possível deeletar uma operação em andamento.");
        }

        operacaoRepository.deletar(codigo);
    }

    public List<Operacao> listarTodas(){
        return operacaoRepository.listarTodos();
    }

    public List<Operacao> listarAtivasPorTipo(TipoOperacao tipo){
        return operacaoRepository.listarAtivasPorTipo(tipo);
    }

    public List<Operacao> listarPorCaminhao(String placa){
        caminhaoService.buscarPorPlaca(placa);
        return operacaoRepository.listarPorCaminhao(placa);
    }

    public List<Operacao> listarPorDoca(int numeroDoca){
        docaService.buscarPorNumero(numeroDoca);
        return operacaoRepository.listarPorDoca(numeroDoca);
    }
}
