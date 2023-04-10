package com.example.digitalbank.model;

import com.example.digitalbank.helper.FirebaseHelper;
import com.google.firebase.database.DatabaseReference;

public class Statement {

    private String id;
    private String operation;
    private long date;
    private double value;
    private String type;

    public Statement() {
        DatabaseReference statementRef = FirebaseHelper.getDatabaseReference();
        setId(statementRef.push().getKey());
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

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
