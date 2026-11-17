package com.example.palmeiras_api.repository;
import com.example.palmeiras_api.model.Salario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
public interface SalarioRepository extends JpaRepository<Salario, Long> {
    Page<Salario> findByCargoContainingIgnoreCase(String cargo, Pageable pageable);
}
