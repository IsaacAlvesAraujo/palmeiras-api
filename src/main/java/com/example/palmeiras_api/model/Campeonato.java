package com.example.palmeiras_api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotBlank;
import java.util.List;

@Entity
@Schema(description = "Campeonato relacionado às equipes. Possui CRUD, consulta personalizada, paginação e HATEOAS.")
public class Campeonato {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único do campeonato", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @NotBlank(message = "O nome do campeonato é obrigatório")
    @Size(min=2, max=120)
    @Schema(description = "Nome do campeonato", example = "Campeonato Brasileiro", requiredMode = Schema.RequiredMode.REQUIRED)
    private String nome;

    @ManyToMany(mappedBy = "campeonatos")
    @JsonIgnoreProperties({"campeonatos", "jogadores"})
    private List<Equipe> equipes;

    public Campeonato() {}
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public List<Equipe> getEquipes() { return equipes; }
    public void setEquipes(List<Equipe> equipes) { this.equipes = equipes; }
}
