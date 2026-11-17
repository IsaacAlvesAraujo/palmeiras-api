package com.example.palmeiras_api.repository;
import com.example.palmeiras_api.model.Jogador;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
public interface JogadorRepository extends JpaRepository<Jogador, Long> {
    @org.springframework.transaction.annotation.Transactional
    @org.springframework.data.jpa.repository.Modifying(clearAutomatically = true, flushAutomatically = true)
    @org.springframework.data.jpa.repository.Query("delete from Jogador e where e.id = :id")
    int excluirPorId(@org.springframework.data.repository.query.Param("id") Long id);

    Page<Jogador> findByNomeContainingIgnoreCase(String nome, Pageable pageable);
}
