package com.montanha.isolada;

import com.montanha.config.Configuracoes;
import com.montanha.factory.UsuarioDataFactory;
import com.montanha.factory.ViagemDataFactory;
import com.montanha.pojo.Usuario;
import com.montanha.pojo.Viagem;
import io.restassured.http.ContentType;
import org.aeonbits.owner.ConfigFactory;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class ViagensTest {

    private String token;
    private String tokenUsuario;

    @Before
    public void setUp(){
        Configuracoes configuracoes = ConfigFactory.create(Configuracoes.class);

        baseURI = configuracoes.baseURI();
        port = configuracoes.port();
        basePath = configuracoes.basePath();

        Usuario usuarioAdmin = UsuarioDataFactory.criarUsuarioAdministrador();

        this.token = given()
            .contentType(ContentType.JSON)
            .body(usuarioAdmin)
        .when()
            .post("/v1/auth")
        .then()
            .extract()
                .path("data.token");

        Usuario usuarioComum = UsuarioDataFactory.criarUsuarioComum();

        this.tokenUsuario = given()
            .contentType(ContentType.JSON)
            .body(usuarioComum)
        .when()
            .post("/v1/auth")
        .then()
            .extract()
                .path("data.token");
    }

    @Test
    public void testCadastroDeViagemValidaRetornaSucesso(){

        Viagem viagemValida = ViagemDataFactory.criarViagemValida();

        given()
            .contentType(ContentType.JSON)
            .body(viagemValida)
            .header("Authorization", token)
        .when()
            .post("/v1/viagens")
        .then()
            .assertThat()
                .statusCode(201)
                .body("data.localDeDestino", equalTo("Gramado"))
                .body("data.acompanhante", equalToIgnoringCase("parente Teste"));
    }

    @Test
    public void testViagensNaoPodemSerCadastradasSemLocalDeDestino() throws IOException {

        Viagem viagemSemLocalDeDestino = ViagemDataFactory.criarViagemSemLocalDeDestino();

        given()
            .contentType(ContentType.JSON)
            .body(viagemSemLocalDeDestino)
            .header("Authorization", token)
        .when()
            .post("/v1/viagens")
        .then()
            .assertThat()
                .statusCode(400)
                .body("errors[0].defaultMessage", equalTo("Local de Destino deve estar entre 3 e 40 caracteres"));
    }

    @Test
    public void testRetornaUmaViagemPossuiStatusCode200EMostraLocalDeDestino() {
        given()
            .header("Authorization", tokenUsuario)
        .when()
            .get("/v1/viagens/1")
        .then()
            .assertThat()
                .statusCode(200)
                .body("data.localDeDestino", equalTo("Campos do Jordão"));
    }

    @Test
    public void testViagemProcessaCorretamenteORetornoDaAPIDoTempo() {
        given()
            .header("Authorization", tokenUsuario)
        .when()
            .get("/v1/viagens/1")
        .then()
            .assertThat()
                .statusCode(200)
                .body("data.temperatura", equalTo(15.4f));
    }
}
