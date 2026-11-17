package com.example.palmeiras_api.repository;
import com.example.palmeiras_api.model.Equipe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
public interface EquipeRepository extends JpaRepository<Equipe, Long> {
    Page<Equipe> findByNomeContainingIgnoreCase(String nome, Pageable pageable);
}
