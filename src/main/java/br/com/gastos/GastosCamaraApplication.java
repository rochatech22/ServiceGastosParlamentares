package br.com.gastos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "br.com.gastos")
public class GastosCamaraApplication {

    public static void main(String[] args) {
        SpringApplication.run(GastosCamaraApplication.class, args);
    }

}
