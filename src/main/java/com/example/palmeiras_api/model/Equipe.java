package com.example.palmeiras_api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotBlank;
import java.util.List;

@Entity
@Schema(description = "Equipe esportiva cadastrada na API.")
public class Equipe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único da equipe", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @NotBlank(message = "O nome da equipe é obrigatório")
    @Size(min=2, max=100)
    @Schema(description = "Nome da equipe", example = "Palmeiras", requiredMode = Schema.RequiredMode.REQUIRED)
    private String nome;

    @OneToMany(mappedBy = "equipe")
    @com.fasterxml.jackson.annotation.JsonIgnore
    @Schema(description = "Jogadores vinculados à equipe")
    private List<Jogador> jogadores;

    @ManyToOne
    @JsonIgnoreProperties("equipes")
    @JoinColumn(name = "estadio_id")
    @Schema(description = "Estádio utilizado pela equipe")
    private Estadio estadio;

    @ManyToMany
    @JsonIgnoreProperties("equipes")
    @JoinTable(name = "equipe_campeonato", joinColumns = @JoinColumn(name = "equipe_id"), inverseJoinColumns = @JoinColumn(name = "campeonato_id"))
    @Schema(description = "Campeonatos relacionados à equipe")
    private List<Campeonato> campeonatos;

    public Equipe(){}
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public List<Jogador> getJogadores() { return jogadores; }
    public void setJogadores(List<Jogador> jogadores) { this.jogadores = jogadores; }
    public Estadio getEstadio() { return estadio; }
    public void setEstadio(Estadio estadio) { this.estadio = estadio; }
    public List<Campeonato> getCampeonatos() { return campeonatos; }
    public void setCampeonatos(List<Campeonato> campeonatos) { this.campeonatos = campeonatos; }
}
