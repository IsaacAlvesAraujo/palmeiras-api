package com.example.palmeiras_api.repository;
import com.example.palmeiras_api.model.Estadio;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
public interface EstadioRepository extends JpaRepository<Estadio, Long> {
    Page<Estadio> findByNomeContainingIgnoreCase(String nome, Pageable pageable);
}
