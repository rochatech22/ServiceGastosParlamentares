package br.com.gastos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import br.com.gastos.model.GastoParlamentar;

public interface GastoParlamentarRepository extends JpaRepository<GastoParlamentar, Long> {
}
