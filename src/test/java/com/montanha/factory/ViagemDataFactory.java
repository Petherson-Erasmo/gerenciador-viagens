package com.montanha.factory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.montanha.pojo.Viagem;

import java.io.FileInputStream;
import java.io.IOException;

public class ViagemDataFactory {
    public static Viagem criarViagemValida() {
        Viagem viagem = new Viagem();
        viagem.setAcompanhante("Parente Teste");
        viagem.setDataPartida("2022-07-10");
        viagem.setDataRetorno("2022-07-30");
        viagem.setLocalDeDestino("Gramado");
        viagem.setRegiao("sul");

        return viagem;
    }

    public static Viagem criarViagemComResource() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        Viagem viagem = objectMapper.readValue(
            new FileInputStream("src/test/resources/requestBody/postV1Viagens.json"),
            Viagem.class);
        return viagem;
    }

    public static Viagem criarViagemSemLocalDeDestino() throws IOException {
        Viagem viagemSemLocalDeDestino = criarViagemComResource();
        viagemSemLocalDeDestino.setLocalDeDestino("");

        return viagemSemLocalDeDestino;
    }
}
