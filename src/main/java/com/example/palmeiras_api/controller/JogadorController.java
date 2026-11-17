package com.example.palmeiras_api.controller;

import com.example.palmeiras_api.dto.JogadorRequest;
import com.example.palmeiras_api.model.Equipe;
import com.example.palmeiras_api.model.Jogador;
import com.example.palmeiras_api.repository.EquipeRepository;
import com.example.palmeiras_api.repository.JogadorRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/jogadores")
@Tag(name = "Jogadores", description = "CRUD, consulta personalizada, paginação e HATEOAS de jogadores.")
public class JogadorController {
    private final JogadorRepository repository;
    private final EquipeRepository equipeRepository;
    public JogadorController(JogadorRepository repository, EquipeRepository equipeRepository) {
        this.repository = repository; this.equipeRepository = equipeRepository;
    }

    @PostMapping
    @Operation(summary = "Cadastrar jogador", description = "Cria um novo recurso após validar o corpo da requisição.")
    @ApiResponses({@ApiResponse(responseCode="201", description="Criado com sucesso"), @ApiResponse(responseCode="400", description="Dados inválidos")})
    public ResponseEntity<EntityModel<Jogador>> cadastrar(@Valid @RequestBody JogadorRequest request) {
        Jogador salvo = repository.save(fromRequest(new Jogador(), request));
        return ResponseEntity.status(HttpStatus.CREATED).body(toModel(salvo));
    }

    @GetMapping
    @Operation(summary = "Listar com paginação", description = "Lista recursos usando Pageable. page começa em 0; size define itens por página; sort define ordenação. Ex.: ?page=0&size=5&sort=id,asc")
    @ApiResponse(responseCode="200", description="Página retornada com links HATEOAS")
    public ResponseEntity<PagedModel<EntityModel<Jogador>>> listar(@ParameterObject @PageableDefault(size=10, sort="id", direction=Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(toPagedModel(repository.findAll(pageable), "/jogadores"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar por ID", description = "Busca um único recurso pelo identificador.")
    @ApiResponses({@ApiResponse(responseCode="200", description="Encontrado"), @ApiResponse(responseCode="404", description="ID não encontrado")})
    public ResponseEntity<EntityModel<Jogador>> buscarPorId(@Parameter(description="ID do recurso", example="1") @PathVariable Long id) {
        return repository.findById(id).map(x -> ResponseEntity.ok(toModel(x))).orElseGet(() -> missing());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar por ID", description = "Substitui os dados do recurso existente mantendo o mesmo ID.")
    @ApiResponses({@ApiResponse(responseCode="200", description="Atualizado"), @ApiResponse(responseCode="400", description="Dados inválidos"), @ApiResponse(responseCode="404", description="ID não encontrado")})
    public ResponseEntity<EntityModel<Jogador>> atualizar(@PathVariable Long id, @Valid @RequestBody JogadorRequest request) {
        Jogador existente = repository.findById(id).orElseThrow(() -> notFound("Jogador", id));
        return ResponseEntity.ok(toModel(repository.save(fromRequest(existente, request))));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir por ID", description = "Exclui o recurso. Em sucesso retorna 204 sem corpo.")
    @ApiResponses({@ApiResponse(responseCode="204", description="Excluído"), @ApiResponse(responseCode="404", description="ID não encontrado")})
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        if (repository.excluirPorId(id) == 0) return missing();
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/buscar")
    @Operation(summary = "Consulta personalizada", description = "Consulta personalizada paginada exigida pelo projeto. O filtro é aplicado pelo Spring Data JPA.")
    @ApiResponse(responseCode="200", description="Resultado paginado da consulta")
    public ResponseEntity<PagedModel<EntityModel<Jogador>>> buscar(
            @Parameter(description="Nome do jogador", example="Raphael") @RequestParam String nome,
            @ParameterObject @PageableDefault(size=10, sort="id", direction=Sort.Direction.ASC) Pageable pageable) {
        Page<Jogador> page = repository.findByNomeContainingIgnoreCase(nome, pageable);
        return ResponseEntity.ok(toPagedModel(page, "/jogadores/buscar"));
    }

    private Jogador fromRequest(Jogador jogador, JogadorRequest request) {
        Equipe equipe = equipeRepository.findById(request.equipeId())
                .orElseThrow(() -> notFound("Equipe", request.equipeId()));
        jogador.setNome(request.nome()); jogador.setPosicao(request.posicao()); jogador.setEquipe(equipe);
        return jogador;
    }

    private ResponseStatusException notFound(String recurso, Long id) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, recurso + " de ID " + id + " não encontrado(a)");
    }

    private <T> ResponseEntity<T> missing(){throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Recurso não encontrado");}

    private EntityModel<Jogador> toModel(Jogador item) {
        Long id = item.getId();
        return EntityModel.of(item,
                item.getEquipe()==null?Link.of("/equipes").withRel("equipe"):Link.of("/equipes/"+item.getEquipe().getId()).withRel("equipe"),
                Link.of("/jogadores/" + id).withSelfRel(),
                Link.of("/jogadores/" + id).withRel("update"),
                Link.of("/jogadores/" + id).withRel("delete"),
                Link.of("/jogadores").withRel("collection"));
    }

    private PagedModel<EntityModel<Jogador>> toPagedModel(Page<Jogador> page, String basePath) {
        List<EntityModel<Jogador>> content = page.getContent().stream().map(this::toModel).toList();
        PagedModel.PageMetadata metadata = new PagedModel.PageMetadata(page.getSize(), page.getNumber(), page.getTotalElements(), page.getTotalPages());
        PagedModel<EntityModel<Jogador>> model = PagedModel.of(content, metadata);
        model.add(Link.of(org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest().replaceQueryParam("page", page.getNumber()).replaceQueryParam("size",page.getSize()).build().toUriString()).withSelfRel());
        if (page.hasPrevious()) model.add(Link.of(org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest().replaceQueryParam("page", (page.getNumber()-1)).replaceQueryParam("size",page.getSize()).build().toUriString()).withRel("prev"));
        if (page.hasNext()) model.add(Link.of(org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest().replaceQueryParam("page", (page.getNumber()+1)).replaceQueryParam("size",page.getSize()).build().toUriString()).withRel("next"));
        return model;
    }
}
