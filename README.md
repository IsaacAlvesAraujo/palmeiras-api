
# Palmeiras API

Projeto Final Individual — Desenvolvimento de APIs RESTful | Parte 1 e Parte 2 — SENAC.

## Sobre o projeto

A Palmeiras API é uma aplicação RESTful desenvolvida com Spring Boot para o gerenciamento de informações relacionadas à Sociedade Esportiva Palmeiras.

O sistema permite cadastrar, consultar, atualizar e excluir informações sobre jogadores, equipes, estádios, campeonatos, contratos e salários.

O projeto possui finalidade acadêmica e aplica conceitos de desenvolvimento de APIs, persistência de dados, segurança, documentação e integração entre sistemas.

## Acesso online

A aplicação está hospedada no Render e pode ser acessada pelos seguintes endereços:

| Recurso | Acesso |
|---|---|
| Aplicação web | [Acessar aplicação](https://palmeiras-api.onrender.com/) |
| Swagger UI | [Documentação interativa](https://palmeiras-api.onrender.com/swagger-ui.html) |
| OpenAPI | [Especificação JSON](https://palmeiras-api.onrender.com/v3/api-docs) |
| Documentação | [Guia da API](https://palmeiras-api.onrender.com/documentation.html) |
| Health Check | [Status da aplicação](https://palmeiras-api.onrender.com/health) |

## Tecnologias utilizadas

- Java 17+
- Spring Boot 4.1.1
- Spring MVC
- Spring Data JPA
- Spring HATEOAS
- Hibernate
- Jakarta Bean Validation
- Banco de dados H2
- Maven
- Swagger / OpenAPI
- HTML e CSS
- Postman
- Docker
- Git e GitHub
- Render

## Arquitetura do projeto

A aplicação utiliza uma estrutura organizada por responsabilidades, facilitando a manutenção, a compreensão e a evolução do código.

| Camada | Responsabilidade |
|---|---|
| Controller | Recebimento e processamento das requisições HTTP |
| Repository | Comunicação com o banco de dados |
| Model | Representação das entidades e relacionamentos |
| DTO | Transferência e validação de dados |
| Security | Autenticação, idempotência e limitação de requisições |
| Config | Configurações da aplicação |
| Resources | Arquivos de configuração e interface web |

## Entidades

O sistema possui seis entidades principais:

| Entidade | Descrição |
|---|---|
| Estádio | Gerenciamento dos estádios |
| Campeonato | Gerenciamento das competições |
| Equipe | Gerenciamento das equipes |
| Jogador | Cadastro e gerenciamento dos jogadores |
| Contrato | Informações contratuais dos jogadores |
| Salário | Gerenciamento de registros salariais |

### Relacionamentos

O projeto utiliza os seguintes relacionamentos JPA:

- One-to-One: Jogador e Contrato.
- One-to-Many: Estádio e Equipes.
- One-to-Many: Equipe e Jogadores.
- Many-to-Many: Equipes e Campeonatos.

A entidade Salário é independente e representa registros de cargo e valor.

O sistema também utiliza o enum Posicao para representar as posições dos jogadores.

## Parte 1 — Funcionalidades da API

### Operações CRUD

Cada entidade possui operações de criação, leitura, atualização e exclusão.

| Método HTTP | Operação |
|---|---|
| POST | Criar um registro |
| GET | Listar registros |
| GET /{id} | Consultar por identificador |
| PUT /{id} | Atualizar um registro |
| DELETE /{id} | Excluir um registro |
| GET /buscar | Realizar consultas personalizadas |

### Principais recursos

| Entidade | Endpoint |
|---|---|
| Estádios | /estadios |
| Campeonatos | /campeonatos |
| Equipes | /equipes |
| Jogadores | /jogadores |
| Contratos | /contratos |
| Salários | /salarios |

### Paginação

As rotas de listagem utilizam Pageable, permitindo organizar os resultados em páginas.

Exemplo:

GET /estadios?page=0&size=5

- page: número da página, iniciando em zero.
- size: quantidade máxima de registros por página.

### Consultas personalizadas

A API permite realizar consultas utilizando filtros específicos de cada entidade.

Exemplo:

GET /estadios/buscar?nome=Allianz&page=0&size=5

As consultas personalizadas também utilizam paginação.

### HATEOAS

O Spring HATEOAS é utilizado para adicionar links nas respostas da API, facilitando a navegação entre os recursos.

Principais links:

- self: consulta do recurso.
- update: atualização do recurso.
- delete: exclusão do recurso.
- collection: acesso à coleção de recursos.

### Validação de dados

A aplicação utiliza Jakarta Bean Validation para validar os dados recebidos nas requisições, evitando o armazenamento de informações inválidas.

### Documentação Swagger

Os endpoints são documentados por meio do Springdoc OpenAPI, permitindo visualizar e testar as operações diretamente pelo navegador.

## Parte 2 — Recursos avançados

### 1. Autenticação com API Key

A aplicação utiliza autenticação baseada no cabeçalho X-API-Key.

O administrador possui uma chave privada utilizada para gerar, consultar e revogar chaves de acesso.

Endpoint de gerenciamento:

/api-keys

As chaves de acesso autorizam operações protegidas da API.

As chaves comuns são armazenadas em memória utilizando hash SHA-256.

A chave administrativa é configurada por variável de ambiente e não deve ser publicada no código-fonte.

### 2. Idempotência

A API utiliza o cabeçalho X-Idempotency-Key para evitar o processamento duplicado de requisições POST.

Quando uma operação é repetida utilizando a mesma chave, cliente, rota, versão e conteúdo, a aplicação retorna a resposta original sem cadastrar novamente o recurso.

O recurso evita duplicidade de registros e melhora a confiabilidade das operações.

Cabeçalho de resposta de uma operação reaproveitada:

Idempotent-Replayed: true

### 3. Rate Limiting

A aplicação possui um mecanismo de limitação de requisições para controlar o consumo dos recursos do servidor.

O limite padrão é de 60 requisições autorizadas por minuto, por endereço IP ou identidade da chave de acesso.

Cabeçalhos utilizados:

- X-Rate-Limit-Limit
- X-Rate-Limit-Remaining
- Retry-After

Quando o limite é excedido, a API retorna:

HTTP 429 — Too Many Requests.

O limite pode ser configurado pela variável de ambiente RATE_LIMIT.

### 4. CORS

A política de Cross-Origin Resource Sharing controla quais origens externas podem realizar requisições à API pelo navegador.

Origens permitidas por padrão:

- http://localhost:3000
- http://localhost:5173

As origens podem ser configuradas pela variável de ambiente CORS_ORIGINS.

### 5. Versionamento

O versionamento da API é realizado utilizando o cabeçalho X-API-Version.

Endpoint de demonstração:

GET /jogadores/{id}/resumo

| Versão | Informações retornadas |
|---|---|
| Versão 1 | Identificador e nome |
| Versão 2 | Identificador, nome, posição e equipe |

Na ausência do cabeçalho, a versão 1 é utilizada como padrão.

### 6. Tratamento global de exceções

A aplicação utiliza @RestControllerAdvice para centralizar o tratamento de erros e retornar mensagens padronizadas.

As respostas apresentam informações que auxiliam na identificação e correção de problemas nas requisições.

## Códigos de status HTTP

| Código | Descrição |
|---|---|
| 200 | OK |
| 201 | Created |
| 204 | No Content |
| 400 | Bad Request |
| 401 | Unauthorized |
| 403 | Forbidden |
| 404 | Not Found |
| 409 | Conflict |
| 422 | Unprocessable Entity |
| 429 | Too Many Requests |
| 500 | Internal Server Error |

## Testes e validação

Os testes funcionais foram realizados utilizando o Postman Collection Runner em ambiente local e no ambiente online hospedado no Render.

### Resultados

| Ambiente | Verificações | Aprovadas | Reprovadas |
|---|---:|---:|---:|
| Local | 64 | 64 | 0 |
| Render | 64 | 64 | 0 |

Taxa de aprovação da coleção: 100%.

Data da validação: 17/11/2026.

### Desempenho observado

| Indicador | Ambiente local | Ambiente online |
|---|---:|---:|
| Tempo de execução | 13,346 segundos | 25,507 segundos |
| Tempo médio de resposta | 140 ms | 356 ms |

Os resultados correspondem às execuções realizadas e podem variar conforme as condições do servidor e da rede.

A aprovação da coleção não substitui testes específicos de saturação do Rate Limiting e da política CORS.

## Como testar utilizando o Postman

1. Instale ou abra o Postman.
2. Importe o arquivo Palmeiras.postman_collection.json.
3. Abra as variáveis da coleção.
4. Configure a variável baseUrl.
5. Configure uma chave administrativa válida.
6. Execute a requisição de geração de chave.
7. Utilize o Collection Runner para executar os testes.

Endereço local:

http://localhost:8080

Endereço online:

https://palmeiras-api.onrender.com

A coleção contém 52 requisições e realiza 64 verificações automatizadas.

A execução inclui operações de cadastro, atualização e exclusão. Portanto, deve ser realizada em um ambiente destinado a testes.

## Como executar localmente

### Pré-requisitos

- Java 17 ou superior, compatível com a versão do Spring Boot.
- Git.
- Maven ou Maven Wrapper.
- IntelliJ IDEA ou outra IDE Java.

### Clonar o repositório

```bash
git clone https://github.com/IsaacAlvesAraujo/palmeiras-api.git
cd palmeiras-api
```

### Configurar a chave administrativa no Windows

No PowerShell:

```powershell
$env:ADMIN_API_KEY="sua-chave-privada"
```

### Executar os testes do projeto

```powershell
.\mvnw.cmd clean test
```

### Executar a aplicação

```powershell
.\mvnw.cmd spring-boot:run
```

No Linux ou macOS:

```bash
export ADMIN_API_KEY="sua-chave-privada"
./mvnw spring-boot:run
```

Também é possível executar diretamente a classe PalmeirasApiApplication.java pelo IntelliJ IDEA, configurando a variável ADMIN_API_KEY.

### Endereços locais

- Aplicação: http://localhost:8080/
- Swagger: http://localhost:8080/swagger-ui.html
- OpenAPI: http://localhost:8080/v3/api-docs
- Health Check: http://localhost:8080/health

## Hospedagem

A aplicação foi publicada utilizando Docker e Render, com integração ao repositório GitHub.

O Docker permite empacotar a aplicação e suas dependências, facilitando a execução em diferentes ambientes.

Configurações utilizadas na hospedagem:

- Runtime: Docker.
- Banco de dados: H2.
- Health Check: /health.
- Autenticação: variável de ambiente ADMIN_API_KEY.

## Segurança e limitações

O projeto foi desenvolvido para fins acadêmicos.

As chaves administrativas devem ser configuradas de forma privada e nunca compartilhadas no repositório público.

O banco H2 utiliza armazenamento em memória. Dessa forma, os registros, as chaves geradas, os dados de idempotência e os contadores de requisições são reiniciados quando a aplicação é reiniciada.

A hospedagem gratuita do Render pode entrar em suspensão após períodos de inatividade, aumentando o tempo de resposta do primeiro acesso.

Os dados salariais utilizados são fictícios e possuem finalidade exclusivamente demonstrativa.

A arquitetura foi desenvolvida para uma instância de demonstração e não representa uma infraestrutura distribuída de produção.

## Objetivo acadêmico

O projeto demonstra a aplicação prática dos conceitos estudados na disciplina de Desenvolvimento de APIs, incluindo:

- Desenvolvimento de Web Services RESTful.
- Modelagem de entidades e relacionamentos.
- Persistência de dados.
- Operações CRUD.
- Paginação e consultas personalizadas.
- Segurança de APIs.
- Idempotência.
- Rate Limiting.
- CORS.
- Versionamento.
- Tratamento de exceções.
- Documentação de serviços.
- Testes automatizados.
- Publicação de aplicações na nuvem.

## Autor

Isaac Alves Araujo

Curso: Sistemas para Internet — SENAC.

Projeto Final Individual — Desenvolvimento de APIs com Spring Boot.

2026.

## Licença

Projeto disponibilizado sob a licença MIT, conforme o arquivo LICENSE presente neste repositório.

Projeto acadêmico independente, sem vínculo oficial com a Sociedade Esportiva Palmeiras.
