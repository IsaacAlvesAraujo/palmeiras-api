package com.example.palmeiras_api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;

@Entity
@Schema(description = "Contrato de um jogador, contendo salário e duração.")
public class Contrato {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único do contrato", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @NotNull(message = "O salário não pode ser nulo")
    @Positive(message = "O salário deve ser positivo")
    @Schema(description = "Valor mensal do salário previsto no contrato", example = "850000.00", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal salario;

    @Positive(message = "A duração deve ser positiva")
    @Schema(description = "Duração do contrato em meses", example = "24")
    private Integer duracaoMeses;

    @OneToOne
    @JsonIgnoreProperties({"contrato", "equipe"})
    @JoinColumn(name = "jogador_id")
    @Schema(description = "Jogador associado ao contrato")
    private Jogador jogador;

    public Contrato(){}
    public Long getId(){return id;}
    public void setId(Long id){ this.id = id; }
    public BigDecimal getSalario(){return salario;}
    public void setSalario(BigDecimal salario){this.salario=salario;}
    public Integer getDuracaoMeses() { return duracaoMeses; }
    public void setDuracaoMeses(Integer duracaoMeses) { this.duracaoMeses = duracaoMeses; }
    public Jogador getJogador() { return jogador; }
    public void setJogador(Jogador jogador) { this.jogador = jogador; }
}
