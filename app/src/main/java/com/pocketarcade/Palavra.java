package com.pocketarcade;

public class Palavra {

    private final String texto;
    private final String categoria;

    public Palavra(String texto, String categoria) {
        this.texto = texto;
        this.categoria = categoria;
    }

    public String getTexto() {
        return texto;
    }

    public String getCategoria() {
        return categoria;
    }
}
