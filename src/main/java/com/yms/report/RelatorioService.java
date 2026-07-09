package com.yms.report;

import com.yms.model.Doca;
import com.yms.model.Operacao;
import com.yms.model.enums.StatusOperacao;
import com.yms.repository.DocaRepository;
import com.yms.repository.OperacaoRepository;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class RelatorioService {

    // Formatadores de data dos relatórios
    private static final DateTimeFormatter FMT_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private static final DateTimeFormatter FMT_ARQUIVO = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    private static final String RELATORIO_DIR = "relatorios/";

    private final OperacaoRepository operacaoRepository;
    private final DocaRepository docaRepository;

    public RelatorioService(OperacaoRepository operacaoRepository, DocaRepository docaRepository){
        this.operacaoRepository = operacaoRepository;
        this.docaRepository = docaRepository;
    }

    /*
    *
    * Relatório 1: Por transportadora e período
    *
    * */

    public String gerarPorTransportadora(String transportadora, LocalDate dataInicio, LocalDate dataFim){

        //Converte datas para LocalDateTime
        LocalDateTime inicio = dataInicio.atStartOfDay();
        LocalDateTime fim = dataFim.atTime(23,59,59);

        //Filtra as operacoes pelo nome da transportadora e período
        List<Operacao> filtradas = operacaoRepository.listarTodos().stream()
                .filter(o-> {return o.getPlacaCaminhao() != null;})
                .filter(o -> !o.getDataHoraChegada().isBefore(inicio) && !o.getDataHoraChegada().isAfter(fim))
                .collect(Collectors.toList());

        String nomeArquivo = RELATORIO_DIR + "transportadora_" + transportadora.replaceAll("\\s+", "_") + "_" + LocalDateTime.now().format(FMT_ARQUIVO) + ".txt";

        escreverRelatorio(nomeArquivo, writer -> {
            writer.println("=".repeat(60));
            writer.println(" RELATÓRIO POR TRANSPORTADORA");
            writer.println(" Transportadora: " + transportadora.toUpperCase());
            writer.println(" Período: " + dataInicio.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + " a " +dataFim.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            writer.println(" Gerado em: " + LocalDateTime.now().format(FMT_DATA));
            writer.println("=".repeat(60));
            writer.println();

            if(filtradas.isEmpty()){
                writer.println("Nenhuma operação encontrada para este período.");
            }else{
                writer.println("Total de operações encontradas: " +filtradas.size());
                writer.println();

                for(Operacao op : filtradas){
                    writer.println("  Código    : " + op.getCodigoOperacao());
                    writer.println("  Placa     : " + op.getPlacaCaminhao());
                    writer.println("  Doca      : " + op.getNumeroDoca());
                    writer.println("  Produto   : " + op.getProduto());
                    writer.println("  Peso      : " + op.getPesoCarga() + " ton");
                    writer.println("  Tipo      : " + op.getTipoOperacao());
                    writer.println("  Status    : " + op.getStatusOperacao());
                    writer.println("  Chegada   : " + op.getDataHoraChegada().format(FMT_DATA));
                    writer.println("-".repeat(40));
                }
            }

            writer.println();
            writer.println("=".repeat(60));
            writer.println(" FIM DO RELATÓRIO");
            writer.println("=".repeat(60));
        });

        return nomeArquivo;
    }

    /*
    *
    * Relatório 2: Por produto e período
    *
    * */

    public String gerarPorProduto(String produto, LocalDate dataInicio, LocalDate dataFim){

        LocalDateTime inicio = dataInicio.atStartOfDay();
        LocalDateTime fim = dataFim.atTime(23,59,59);

        List<Operacao> filtradas = operacaoRepository.listarTodos().stream()
                .filter(o->o.getProduto().equalsIgnoreCase(produto.trim()))
                .filter(o-> !o.getDataHoraChegada().isBefore(inicio) && !o.getDataHoraChegada().isAfter(fim))
                .collect(Collectors.toList());

        double totalPeso = filtradas.stream()
                .mapToDouble(Operacao::getPesoCarga)
                .sum();

        String nomeArquivo = RELATORIO_DIR + "produto_" +produto.replaceAll("\\s+", "_") + "_" +LocalDateTime.now().format(FMT_ARQUIVO) + ".txt";

        escreverRelatorio(nomeArquivo, writer -> {
            writer.println("=".repeat(60));
            writer.println(" RELATÓRIO POR PRODUTO");
            writer.println(" Produto: " + produto.toUpperCase());
            writer.println(" Período: " + dataInicio.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + " a " + dataFim.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            writer.println(" Gerado em: " +LocalDateTime.now().format(FMT_DATA));
            writer.println("=".repeat(60));
            writer.println();

            if(filtradas.isEmpty()){
                writer.println("Nenhuma operação encontrada para este produto no período.");
            }else{
                writer.println("Total de operações : " + filtradas.size());
                writer.println("Peso total movimentado: " + String.format("%.1f", totalPeso) + " ton");
                writer.println();
                for (Operacao op : filtradas) {
                    writer.println("  Código  : " + op.getCodigoOperacao());
                    writer.println("  Placa   : " + op.getPlacaCaminhao());
                    writer.println("  Doca    : " + op.getNumeroDoca());
                    writer.println("  Peso    : " + op.getPesoCarga() + " ton");
                    writer.println("  Tipo    : " + op.getTipoOperacao());
                    writer.println("  Status  : " + op.getStatusOperacao());
                    writer.println("  Chegada : " + op.getDataHoraChegada().format(FMT_DATA));
                    writer.println("-".repeat(40));
                }
            }

            writer.println();
            writer.println("=".repeat(60));
            writer.println(" FIM DO RELATÓRIO");
            writer.println("=".repeat(60));
        });

        return nomeArquivo;
    }

    /*
    *
    * Relatório 3: Sumário de ocuapção das docas
    *
    * */

    public String gerarSumarioDocas(){
        List<Doca> docas = docaRepository.listarTodos();
        List<Operacao> todasOperacoes = operacaoRepository.listarTodos();

        String nomeArquivo = RELATORIO_DIR + "sumario_docas_" + LocalDateTime.now().format(FMT_ARQUIVO) + ".txt";

        escreverRelatorio(nomeArquivo, writer -> {
            writer.println("=".repeat(60));
            writer.println(" SUMÁRIO DE OCUPAÇÃO DAS DOCAS");
            writer.println(" Gerado em: " + LocalDateTime.now().format(FMT_DATA));
            writer.println("=".repeat(60));
            writer.println();

            if(docas.isEmpty()){
                writer.println("Nenhuma doca cadastrada.");
            }else{
                for(Doca doca : docas){
                    List<Operacao> opsDoca = todasOperacoes.stream()
                            .filter(o -> o.getNumeroDoca() == doca.getNumeroDoca())
                            .collect(Collectors.toList());

                    long concluidas = opsDoca.stream()
                            .filter(o -> o.getStatusOperacao() == StatusOperacao.CONCLUIDA)
                            .count();

                    long emAndamento = opsDoca.stream()
                            .filter(o -> o.getStatusOperacao() == StatusOperacao.EM_ANDAMENTO)
                            .count();

                    long agendadas = opsDoca.stream()
                            .filter(o -> o.getStatusOperacao() == StatusOperacao.AGENDADA)
                            .count();

                    writer.println("  Doca Nº       : " + doca.getNumeroDoca());
                    writer.println("  Tipo          : " + doca.getTipoDoca());
                    writer.println("  Status atual  : " + doca.getStatusDoca());
                    writer.println("  Cap. máxima   : " + doca.getCapacidadeMaxima() + " ton");
                    writer.println("  Op. concluídas: " + concluidas);
                    writer.println("  Op. andamento : " + emAndamento);
                    writer.println("  Op. agendadas : " + agendadas);
                    writer.println("  Total históric: " + opsDoca.size());
                    writer.println("-".repeat(40));
                }
            }

            writer.println();
            writer.println("=".repeat(60));
            writer.println(" FIM DO RELATÓRIO");
            writer.println("=".repeat(60));
        });

        return nomeArquivo;
    }

    //Método auxiliar \\ escreve o relatório no arquivo

    private void escreverRelatorio(String nomeArquivo, RelatorioWriter conteudo){
        //garante que a pasta existe
        java.io.File dir = new java.io.File(RELATORIO_DIR);
        if(!dir.exists()){
            dir.mkdirs();
        }

        try(PrintWriter writer = new PrintWriter(new FileWriter(nomeArquivo))){
            conteudo.escrever(writer);
            System.out.println("Relatorio gerado com sucesso: " + nomeArquivo);
        }catch(IOException e){
            System.err.println("Erro ao gerar relatório: " + e.getMessage());
        }
    }

    //Interface funcional - permite passar o conteudo do relatório como parâmetro
    @FunctionalInterface
    private interface RelatorioWriter{
        void escrever(PrintWriter writer) throws IOException;
    }
}


