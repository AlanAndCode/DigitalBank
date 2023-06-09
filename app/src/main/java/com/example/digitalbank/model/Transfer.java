package com.example.digitalbank.model;

import java.io.Serializable;

public class Transfer implements Serializable {

    private String id;
    private String idUserOrigem;
    private String idUserDestino;
    private long data;
    private double valor;

    public Transfer() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdUserOrigem() {
        return idUserOrigem;
    }

    public void setIdUserOrigem(String idUserOrigem) {
        this.idUserOrigem = idUserOrigem;
    }

    public String getIdUserDestino() {
        return idUserDestino;
    }

    public void setIdUserDestino(String idUserDestino) {
        this.idUserDestino = idUserDestino;
    }

    public long getData() {
        return data;
    }

    public void setData(long data) {
        this.data = data;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }
}
