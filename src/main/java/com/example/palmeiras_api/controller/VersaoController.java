package com.example.palmeiras_api.controller;
import com.example.palmeiras_api.repository.JogadorRepository;
import org.springframework.web.bind.annotation.*; import org.springframework.web.server.ResponseStatusException; import org.springframework.http.HttpStatus; import org.springframework.hateoas.*; import java.util.*;
import io.swagger.v3.oas.annotations.Operation; import io.swagger.v3.oas.annotations.Parameter;
@RestController public class VersaoController {
 private final JogadorRepository repository; public VersaoController(JogadorRepository repository){this.repository=repository;}
 @GetMapping("/jogadores/{id}/resumo") @Operation(summary="Resumo do jogador: versões 1 e 2 por header")
 public EntityModel<Map<String,Object>> resumo(@PathVariable Long id,@Parameter(description="Versão 1 ou 2",example="2") @RequestHeader(value="X-API-Version",defaultValue="1") String version){
 if(!version.equals("1")&&!version.equals("2"))throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Versão inválida: use 1 ou 2");
 var jogador=repository.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Jogador não encontrado"));
 Map<String,Object> data=new LinkedHashMap<>();data.put("id",id);data.put("nome",jogador.getNome());if(version.equals("2")){data.put("posicao",jogador.getPosicao());data.put("equipeId",jogador.getEquipe()==null?null:jogador.getEquipe().getId());}return EntityModel.of(data,Link.of("/jogadores/"+id+"/resumo").withSelfRel(),Link.of("/jogadores/"+id).withRel("jogador"));
 }
}
