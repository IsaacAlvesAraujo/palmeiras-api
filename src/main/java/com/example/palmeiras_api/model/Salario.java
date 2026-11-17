package com.example.palmeiras_api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

@Entity
@Schema(description = "Registro de salário e cargo.")
public class Salario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único do salário", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @NotNull(message = "O valor do salário é obrigatório")
    @Positive(message = "O valor deve ser positivo")
    @Schema(description = "Valor do salário", example = "15000.50", requiredMode = Schema.RequiredMode.REQUIRED)
    private Double valor;

    @NotBlank(message = "O cargo é obrigatório")
    @Schema(description = "Cargo relacionado ao salário", example = "Analista")
    private String cargo;

    public Salario() {}
    public Salario(Double valor, String cargo) { this.valor = valor; this.cargo = cargo; }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Double getValor() { return valor; }
    public void setValor(Double valor) { this.valor = valor; }
    public String getCargo() { return cargo; }
    public void setCargo(String cargo) { this.cargo = cargo; }
}
