package com.example.palmeiras_api.repository;
import com.example.palmeiras_api.model.Campeonato;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
public interface CampeonatoRepository extends JpaRepository<Campeonato, Long> {
    Page<Campeonato> findByNomeContainingIgnoreCase(String nome, Pageable pageable);
}
