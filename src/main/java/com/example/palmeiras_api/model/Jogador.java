package com.example.palmeiras_api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotBlank;

@Entity
@Schema(description = "Representa um jogador cadastrado na API.")
public class Jogador {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único do jogador", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @NotBlank(message = "O nome do jogador não pode estar vazio")
    @Size(min = 2, max = 100, message = "O nome deve ter entre 2 e 100 caracteres")
    @Schema(description = "Nome completo do jogador", example = "Raphael Veiga", requiredMode = Schema.RequiredMode.REQUIRED)
    private String nome;

    @NotNull(message = "A posição é obrigatória")
    @Enumerated(EnumType.STRING)
    @Schema(description = "Posição em que o jogador atua", example = "MEIA")
    private Posicao posicao;

    @OneToOne(mappedBy = "jogador")
    @com.fasterxml.jackson.annotation.JsonIgnore
    @Schema(description = "Contrato vinculado ao jogador")
    private Contrato contrato;

    @ManyToOne
    @JsonIgnoreProperties({"jogadores", "campeonatos"})
    @JoinColumn(name = "equipe_id")
    @Schema(description = "Equipe à qual o jogador pertence")
    private Equipe equipe;

    public Jogador(){}

    public Long getId(){return id;}
    public void setId(Long id){ this.id = id;}
    public String getNome() {return nome;}
    public void setNome(String nome){this.nome=nome;}
    public Posicao getPosicao(){return posicao;}
    public void setPosicao(Posicao posicao){this.posicao= posicao;}
    public Contrato getContrato(){return contrato;}
    public void setContrato(Contrato contrato){ this.contrato = contrato;}
    public Equipe getEquipe(){return equipe;}
    public void setEquipe(Equipe equipe){this.equipe=equipe;}
}
