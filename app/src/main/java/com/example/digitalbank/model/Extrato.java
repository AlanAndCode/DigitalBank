package com.example.digitalbank.model;

import com.example.digitalbank.helper.FirebaseHelper;
import com.google.firebase.database.DatabaseReference;

public class Extrato {

    private String id;
    private String operation;
    private long date;
    private double valor;
    private String type;

    public Extrato() {
        DatabaseReference extratoRef = FirebaseHelper.getDatabaseReference();
        setId(extratoRef.push().getKey());
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
