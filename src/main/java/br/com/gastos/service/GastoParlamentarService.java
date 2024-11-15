package br.com.gastos.service;

import br.com.gastos.model.GastoParlamentar;
import br.com.gastos.repository.GastoParlamentarRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class GastoParlamentarService {

    private final GastoParlamentarRepository repository;
    private final RestTemplate restTemplate = new RestTemplate();

    public GastoParlamentarService(GastoParlamentarRepository repository) {
        this.repository = repository;
    }

    public void coletarDados() {
        List<Integer> deputadosIds = obterDeputadosIds();
        if (deputadosIds.isEmpty()) {
            System.out.println("Nenhum deputado encontrado na API.");
            return;
        }

        String urlTemplate = "https://dadosabertos.camara.leg.br/api/v2/deputados/{id}/despesas?ano=2022";

        for (Integer deputadoId : deputadosIds) {
            String url = urlTemplate.replace("{id}", deputadoId.toString());

            try {
                ResponseWrapper response = restTemplate.getForObject(url, ResponseWrapper.class);

                if (response != null && response.getDados() != null) {
                    response.getDados().forEach(despesa -> {
                        GastoParlamentar gasto = new GastoParlamentar();
                        gasto.setDeputadoId(deputadoId);
                        gasto.setTipoDespesa(despesa.getTipoDespesa());
                        gasto.setFornecedor(despesa.getNomeFornecedor());

                        // Ajustando o parsing da data
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
                        gasto.setDataEmissao(LocalDate.parse(despesa.getDataDocumento(), formatter));

                        gasto.setValorLiquido(BigDecimal.valueOf(despesa.getValorLiquido()));

                        // Salvar no banco
                        repository.save(gasto);
                    });
                } else {
                    System.out.println("Nenhuma despesa encontrada para deputado ID: " + deputadoId);
                }
            } catch (Exception e) {
                System.err.println("Erro ao coletar dados para deputado ID: " + deputadoId);
                e.printStackTrace();
            }
        }
    }


    /**
     * Obtém a lista de IDs dos deputados reais usando a API da Câmara.
     */
    private List<Integer> obterDeputadosIds() {
        String url = "https://dadosabertos.camara.leg.br/api/v2/deputados";
        List<Integer> deputadosIds = new ArrayList<>();

        try {
            ResponseDeputadosWrapper response = restTemplate.getForObject(url, ResponseDeputadosWrapper.class);

            if (response != null && response.getDados() != null) {
                response.getDados().forEach(deputado -> deputadosIds.add(deputado.getId()));
            }
        } catch (Exception e) {
            System.err.println("Erro ao obter IDs de deputados.");
            e.printStackTrace();
        }

        return deputadosIds;
    }

    // Classe para mapear a resposta da lista de deputados
    private static class ResponseDeputadosWrapper {
        private List<Deputado> dados;

        public List<Deputado> getDados() {
            return dados;
        }

        public void setDados(List<Deputado> dados) {
            this.dados = dados;
        }
    }

    // Classe para representar um deputado
    private static class Deputado {
        private Integer id;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }
    }

    // Classe para mapear a resposta da API de despesas
    private static class ResponseWrapper {
        private List<GastoResponse> dados;

        public List<GastoResponse> getDados() {
            return dados;
        }

        public void setDados(List<GastoResponse> dados) {
            this.dados = dados;
        }
    }

    // Classe para representar um gasto parlamentar
    private static class GastoResponse {
        private String tipoDespesa;
        private String nomeFornecedor;
        private String dataDocumento;
        private Double valorLiquido;

        public String getTipoDespesa() {
            return tipoDespesa;
        }

        public void setTipoDespesa(String tipoDespesa) {
            this.tipoDespesa = tipoDespesa;
        }

        public String getNomeFornecedor() {
            return nomeFornecedor;
        }

        public void setNomeFornecedor(String nomeFornecedor) {
            this.nomeFornecedor = nomeFornecedor;
        }

        public String getDataDocumento() {
            return dataDocumento;
        }

        public void setDataDocumento(String dataDocumento) {
            this.dataDocumento = dataDocumento;
        }

        public Double getValorLiquido() {
            return valorLiquido;
        }

        public void setValorLiquido(Double valorLiquido) {
            this.valorLiquido = valorLiquido;
        }
    }
}
