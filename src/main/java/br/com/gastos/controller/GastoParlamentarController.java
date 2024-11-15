package br.com.gastos.controller;

import br.com.gastos.service.GastoParlamentarService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GastoParlamentarController {

    private final GastoParlamentarService service;

    public GastoParlamentarController(GastoParlamentarService service) {
        this.service = service;
    }

    @GetMapping("/coletar-dados")
    public String coletarDados() {
        service.coletarDados();
        return "Dados coletados com sucesso!";
    }
}
