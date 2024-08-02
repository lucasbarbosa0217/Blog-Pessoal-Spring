# Blog Pessoal - API REST

## Descrição
Este projeto é uma API REST para criação de Blogs, desenvolvido como parte de uma atividade no bootcamp da Generation Brasil. O objetivo inicial era aprender a construir uma API básica, mas adicionei várias funcionalidades adicionais conforme fui me aprofundando no desenvolvimento.

## Tecnologias Utilizadas
- **Spring Boot**
- **Hibernate JPA**
- **Spring Security**
- **Springdocs Swagger**
- **Firebase Storage**

## Funcionalidades
- **CRUD de Postagens:** Criação, leitura, atualização e exclusão de postagens de blog.
- **Autenticação e Autorização:** Implementação do sistema de autorizações do Spring Security com roles para usuários (admin e usuário padrão).
- **Upload de Fotos:** Upload das fotos de usuário para o Firebase Storage.
- **Comentários em Posts:** Funcionalidade de adicionar comentários em posts do blog.

## Instalação
1. Clone o repositório:
    ```bash
    git clone https://github.com/lucasbarbosa0217/Blog-Pessoal-Spring.git
    ```
2. Navegue até o diretório do projeto:
    ```bash
    cd Blog-Pessoal-Spring
    ```
3. Configure o banco de dados no arquivo `application.properties`.

4. Execute o projeto:
    ```bash
    ./mvnw spring-boot:run
    ```

## Documentação da API
A documentação da API pode ser acessada através do Swagger em: https://blogpessoal-zdcv.onrender.com/
