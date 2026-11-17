# Execução local
Abra a pasta palmeiras-api (pom.xml) no IntelliJ. Em Run > Edit Configurations, selecione o módulo palmeiras-api e a classe com.example.palmeiras_api.PalmeirasApiApplication. Em Environment variables, use ADMIN_API_KEY=palmeiras-teste-2026 ou em Program arguments use --spring.profiles.active=local. Não use esse perfil no Render. Pare a execução antiga antes de iniciar. No Postman, mantenha baseUrl=http://localhost:8080 e adminKey correspondente. Execute a coleção completa uma vez em banco limpo.

# Correções
Inclui o pacote Spring Data correto no handler e exclusões JPQL transacionais de Contrato e Jogador. Os vínculos permanecem protegidos pelas restrições do banco. A correção é uma hipótese para os erros 500 observados; a compilação e os testes funcionais desta revisão não foram executados por falta de acesso ao Maven Central neste ambiente. O resultado anterior foi 59/64.
