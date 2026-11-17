package com.example.palmeiras_api.controller;

import com.example.palmeiras_api.dto.EquipeRequest;
import com.example.palmeiras_api.model.Campeonato;
import com.example.palmeiras_api.model.Equipe;
import com.example.palmeiras_api.model.Estadio;
import com.example.palmeiras_api.repository.CampeonatoRepository;
import com.example.palmeiras_api.repository.EquipeRepository;
import com.example.palmeiras_api.repository.EstadioRepository;
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
@RequestMapping("/equipes")
@Tag(name = "Equipes", description = "CRUD, consulta personalizada, paginação e HATEOAS de equipes.")
public class EquipeController {
    private final EquipeRepository repository;
    private final EstadioRepository estadioRepository;
    private final CampeonatoRepository campeonatoRepository;

    public EquipeController(EquipeRepository repository, EstadioRepository estadioRepository, CampeonatoRepository campeonatoRepository) {
        this.repository = repository;
        this.estadioRepository = estadioRepository;
        this.campeonatoRepository = campeonatoRepository;
    }

    @PostMapping
    @Operation(summary = "Cadastrar equipe", description = "Cria um novo recurso após validar o corpo da requisição.")
    @ApiResponses({@ApiResponse(responseCode="201", description="Criado com sucesso"), @ApiResponse(responseCode="400", description="Dados inválidos")})
    public ResponseEntity<EntityModel<Equipe>> cadastrar(@Valid @RequestBody EquipeRequest request) {
        Equipe salvo = repository.save(fromRequest(new Equipe(), request));
        return ResponseEntity.status(HttpStatus.CREATED).body(toModel(salvo));
    }

    @GetMapping
    @Operation(summary = "Listar com paginação", description = "Lista recursos usando Pageable. page começa em 0; size define itens por página; sort define ordenação. Ex.: ?page=0&size=5&sort=id,asc")
    @ApiResponse(responseCode="200", description="Página retornada com links HATEOAS")
    public ResponseEntity<PagedModel<EntityModel<Equipe>>> listar(@ParameterObject @PageableDefault(size=10, sort="id", direction=Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(toPagedModel(repository.findAll(pageable), "/equipes"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar por ID", description = "Busca um único recurso pelo identificador.")
    @ApiResponses({@ApiResponse(responseCode="200", description="Encontrado"), @ApiResponse(responseCode="404", description="ID não encontrado")})
    public ResponseEntity<EntityModel<Equipe>> buscarPorId(@Parameter(description="ID do recurso", example="1") @PathVariable Long id) {
        return repository.findById(id).map(x -> ResponseEntity.ok(toModel(x))).orElseGet(() -> missing());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar por ID", description = "Substitui os dados do recurso existente mantendo o mesmo ID.")
    @ApiResponses({@ApiResponse(responseCode="200", description="Atualizado"), @ApiResponse(responseCode="400", description="Dados inválidos"), @ApiResponse(responseCode="404", description="ID não encontrado")})
    public ResponseEntity<EntityModel<Equipe>> atualizar(@PathVariable Long id, @Valid @RequestBody EquipeRequest request) {
        Equipe existente = repository.findById(id).orElseThrow(() -> notFound("Equipe", id));
        return ResponseEntity.ok(toModel(repository.save(fromRequest(existente, request))));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir por ID", description = "Exclui o recurso. Em sucesso retorna 204 sem corpo.")
    @ApiResponses({@ApiResponse(responseCode="204", description="Excluído"), @ApiResponse(responseCode="404", description="ID não encontrado")})
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        if (!repository.existsById(id)) return missing();
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/buscar")
    @Operation(summary = "Consulta personalizada", description = "Consulta personalizada paginada exigida pelo projeto. O filtro é aplicado pelo Spring Data JPA.")
    @ApiResponse(responseCode="200", description="Resultado paginado da consulta")
    public ResponseEntity<PagedModel<EntityModel<Equipe>>> buscar(
            @Parameter(description="Nome da equipe", example="Palmeiras") @RequestParam String nome,
            @ParameterObject @PageableDefault(size=10, sort="id", direction=Sort.Direction.ASC) Pageable pageable) {
        Page<Equipe> page = repository.findByNomeContainingIgnoreCase(nome, pageable);
        return ResponseEntity.ok(toPagedModel(page, "/equipes/buscar"));
    }

    private Equipe fromRequest(Equipe equipe, EquipeRequest request) {
        Estadio estadio = estadioRepository.findById(request.estadioId())
                .orElseThrow(() -> notFound("Estádio", request.estadioId()));
        List<Long> ids = request.campeonatoIds() == null ? List.of() : request.campeonatoIds();
        List<Campeonato> campeonatos = campeonatoRepository.findAllById(ids);
        if (campeonatos.size() != ids.stream().distinct().count()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Um ou mais campeonatos informados não existem");
        }
        equipe.setNome(request.nome());
        equipe.setEstadio(estadio);
        equipe.setCampeonatos(campeonatos);
        return equipe;
    }

    private ResponseStatusException notFound(String recurso, Long id) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, recurso + " de ID " + id + " não encontrado(a)");
    }

    private <T> ResponseEntity<T> missing(){throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Recurso não encontrado");}

    private EntityModel<Equipe> toModel(Equipe item) {
        Long id = item.getId();
        return EntityModel.of(item,
                item.getEstadio()==null?Link.of("/estadios").withRel("estadio"):Link.of("/estadios/"+item.getEstadio().getId()).withRel("estadio"),
                Link.of("/equipes/" + id).withSelfRel(),
                Link.of("/equipes/" + id).withRel("update"),
                Link.of("/equipes/" + id).withRel("delete"),
                Link.of("/equipes").withRel("collection"));
    }

    private PagedModel<EntityModel<Equipe>> toPagedModel(Page<Equipe> page, String basePath) {
        List<EntityModel<Equipe>> content = page.getContent().stream().map(this::toModel).toList();
        PagedModel.PageMetadata metadata = new PagedModel.PageMetadata(page.getSize(), page.getNumber(), page.getTotalElements(), page.getTotalPages());
        PagedModel<EntityModel<Equipe>> model = PagedModel.of(content, metadata);
        model.add(Link.of(org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest().replaceQueryParam("page", page.getNumber()).replaceQueryParam("size",page.getSize()).build().toUriString()).withSelfRel());
        if (page.hasPrevious()) model.add(Link.of(org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest().replaceQueryParam("page", (page.getNumber()-1)).replaceQueryParam("size",page.getSize()).build().toUriString()).withRel("prev"));
        if (page.hasNext()) model.add(Link.of(org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest().replaceQueryParam("page", (page.getNumber()+1)).replaceQueryParam("size",page.getSize()).build().toUriString()).withRel("next"));
        return model;
    }
}
