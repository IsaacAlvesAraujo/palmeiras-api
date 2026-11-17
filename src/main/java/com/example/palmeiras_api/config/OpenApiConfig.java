package com.example.palmeiras_api.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Palmeiras API",
                version = "2.0.0",
                description = """
                        API REST desenvolvida com Spring Boot para gerenciamento de informações relacionadas ao Palmeiras,
                        incluindo jogadores, equipes, estádios, campeonatos, contratos e salários.

                        O projeto foi desenvolvido para fins acadêmicos, aplicando conceitos de APIs RESTful, persistência
                        de dados, relacionamentos entre entidades, validação e documentação de serviços.
                        """,
                contact = @Contact(name = "Palmeiras API"),
                license = @License(name = "MIT License", url = "https://opensource.org/license/mit/")
        ),
        servers = {
                @Server(url = "/", description = "Servidor atual")
        }
)
public class OpenApiConfig {
 @org.springframework.context.annotation.Bean
 public org.springdoc.core.customizers.OpenApiCustomizer advancedDocs(){return api->{
 if(api.getComponents()==null)api.setComponents(new io.swagger.v3.oas.models.Components());
 api.getComponents().addSecuritySchemes("apiKey",new io.swagger.v3.oas.models.security.SecurityScheme().type(io.swagger.v3.oas.models.security.SecurityScheme.Type.APIKEY).in(io.swagger.v3.oas.models.security.SecurityScheme.In.HEADER).name("X-API-Key"));
 api.getPaths().forEach((path,item)->{if(!path.matches("/(jogadores|equipes|estadios|campeonatos|contratos|salarios|api-keys)(/.*)?"))return;
 item.readOperationsMap().forEach((method,op)->{
 boolean protectedRoute=path.startsWith("/api-keys")||path.startsWith("/contratos")||path.startsWith("/salarios")||method!=io.swagger.v3.oas.models.PathItem.HttpMethod.GET;
 if(protectedRoute){op.addSecurityItem(new io.swagger.v3.oas.models.security.SecurityRequirement().addList("apiKey"));op.getResponses().addApiResponse("401",new io.swagger.v3.oas.models.responses.ApiResponse().description("Chave ausente ou inválida"));}
 if(path.startsWith("/api-keys"))op.getResponses().addApiResponse("403",new io.swagger.v3.oas.models.responses.ApiResponse().description("Requer administrador"));
 if(method==io.swagger.v3.oas.models.PathItem.HttpMethod.POST){op.addParametersItem(new io.swagger.v3.oas.models.parameters.Parameter().in("header").name("X-Idempotency-Key").description("Chave única da operação; repetir com o mesmo corpo retorna a resposta original").schema(new io.swagger.v3.oas.models.media.StringSchema()).example("operacao-001"));op.getResponses().addApiResponse("409",new io.swagger.v3.oas.models.responses.ApiResponse().description("Conflito ou operação em andamento"));op.getResponses().addApiResponse("422",new io.swagger.v3.oas.models.responses.ApiResponse().description("Chave reutilizada com corpo diferente"));}
 op.getResponses().addApiResponse("429",new io.swagger.v3.oas.models.responses.ApiResponse().description("Limite excedido; aguarde Retry-After"));
 });});};}
}
