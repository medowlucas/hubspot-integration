### Pré-requisitos
- 🐳 Docker e Docker Compose instalados
- Conta no HubSpot com acesso à API e configuração de OAuth 2.0
- Ngrok (ou qualquer outra ferramenta de tunelamento) para testar a integração com webhooks durante o desenvolvimento (caso a rede não tenha acesso externo)

### Passos

1. Clone o repositório:
   ```bash
   git clone https://github.com/medowlucas/hubspot-integration.git
   cd hubspot-integration
   cp .env.example .env
   ```
2. Parametrize tudo com a Hubspot:
- Configure os valores de **HUBSPOT_CLIENT_ID** e **HUBSPOT_CLIENT_SECRET** fornecidos pela Hubspot no .env
- Configure **HUBSPOT_REDIRECT_URI** do .env para o seu domínio https://seu-domínio:porta **/oauth/callback**
- Configuração na Hubspot:
   - Acesse o painel da Hubspot e configure a mesma URI acima no seu aplicativo da plataforma ex.: (https://seu-domínio:porta **/oauth/callback**)
![Aplicativo Hubspot](/images/aplicativo-hubspot.png)
   - Crie e configure também o webhook de **contact creation** para o seu domínio https://seu-domínio:porta **/webhooks/hubspot**
   (para o webhook funcionar precisa ter acesso externo na sua rede de domínio, utilizei o Ngrok para emular um túnel de conexão https com acesso externo)
![Webhook Hubspot](/images/webhook-hubspot.png)
![Túnel Ngrok](/images/tunel-ngrok.png)

3. Execute o projeto:
   ```bash
   docker-compose up --build -d
   ```

4. Acesse o Swagger da API por exemplo: http://localhost:3001/swagger-ui/index.html
   - Gerar URL de OAuth:
      - Faça um **GET** na rota **/oauth/authorize** para gerar a URL de OAuth e copie a resposta para acessar a URL no navegador 
   ![OAuth Authorize](/images/authorizeo-auth.png)
      - Acesse a URL e vincule sua conta Hubspot com a API
   
   - Criar um Contato:
      - Volte na aba do Swagger e envie um **POST** para a rota **/api/contacts**: 
   ![Contact Controller](/images/contact-controller.png)

Pronto! Seu contato foi criado com sucesso na Hubspot.
   
### 📚 Tecnologias
Java 17

Spring Boot

Maven

OAuth 2.0

HubSpot API

Swagger

Redis

PostgreSQL

Docker + Docker Compose

### 📌 Funcionalidades

- 🔐 Autenticação OAuth 2.0 com HubSpot
- 🧾 Criação de contatos no CRM via API
- 📥 Recebimento de webhooks de criação de contato (evento `contact.creation`)
- 🌐 Exposição de endpoints REST com Spring Boot

### 📖 Documentação Técnica
- 🛠️ **Decisões de implementação**:

   - **Spring Boot**: escolhido por sua alta produtividade e facilidade na construção de APIs REST.

   - **Redis**: usado para lidar com limites de taxa (rate limits) da API do HubSpot.

   - **PostgreSQL**: para armazenamento de token e refresh_token e controle dos recebimentos de webhooks

   - **Lombok**: utilizado para reduzir código repetitivo (getters, constructors).

   - **Tratamento Global de Exceções**: implementado via @RestControllerAdvice para respostas de erro.

- 📚 **Bibliotecas utilizadas**:

   - `spring-boot-starter-web`: construção de APIs REST.

   - `spring-boot-starter-validation`: validação de dados de entrada.

   - `spring-boot-starter-data-redis`: para controle de rate limit.

   - `springdoc-openapi-starter-webmvc-ui`: swagger da aplicação

   - `com.fasterxml.jackson.core`: tratamento de respostas JSON

   - `spring-security-oauth2-client`: utilizado para conexão OAuth com a Hubspot

   - `spring-boot-starter-data-jpa`: persistência no banco de dados

- 🚀 **Possíveis melhorias futuras**

   - Implementar cache local para evitar chamadas repetitivas.

   - Melhorar estrutura de resposta de erros (adicionar timestamp, detalhes da requisição).

   - Monitoramento de métricas de chamadas externas usando Micrometer + Prometheus.

- 📋 **Requisitos atendidos**

   - ✅ Spring Boot / Java 17
   - ✅ Boas práticas de segurança
   - ✅ Separação de responsabilidades
   - ✅ Tratamento global de erros
   - ✅ Rate Limit Resilience
   - ✅ Instruções de execução detalhadas
   - ✅ Documentação técnica

- 🧪 **Testes**

   - Testes **unitários** para controllers.
