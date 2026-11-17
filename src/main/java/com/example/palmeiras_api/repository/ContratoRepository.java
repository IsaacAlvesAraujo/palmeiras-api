package com.example.palmeiras_api.repository;
import com.example.palmeiras_api.model.Contrato;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface ContratoRepository extends JpaRepository<Contrato, Long> {
    @org.springframework.transaction.annotation.Transactional
    @org.springframework.data.jpa.repository.Modifying(clearAutomatically = true, flushAutomatically = true)
    @org.springframework.data.jpa.repository.Query("delete from Contrato e where e.id = :id")
    int excluirPorId(@org.springframework.data.repository.query.Param("id") Long id);

    Page<Contrato> findByDuracaoMeses(Integer duracaoMeses, Pageable pageable);
    Optional<Contrato> findByJogadorId(Long jogadorId);
}
