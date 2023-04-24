package com.example.digitalbank.deposit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.example.digitalbank.R;
import com.example.digitalbank.helper.FirebaseHelper;
import com.example.digitalbank.helper.GetMask;
import com.example.digitalbank.model.Deposito;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class DepositReceiptActivity extends AppCompatActivity {

    private TextView textCode;
    private TextView textDate;
    private TextView textValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deposit_receipt);

        configToolbar();

        iniciaComponentes();

        getDeposito();

        configCliques();

    }

    private void configCliques(){
        findViewById(R.id.btnOK).setOnClickListener(v -> finish());
    }

    private void getDeposito(){
        String idDeposito = (String) getIntent().getSerializableExtra("idDeposito");

        DatabaseReference depositoRef = FirebaseHelper.getDatabaseReference()
                .child("depositos")
                .child(idDeposito);
        depositoRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Deposito deposito = snapshot.getValue(Deposito.class);
                configDados(deposito);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void configDados(Deposito deposito){
        textCode.setText(deposito.getId());
        textDate.setText(GetMask.getDate(deposito.getData(), 3));
        textValue.setText(getString(R.string.text_value, GetMask.getValor(deposito.getValor())));
    }

    private void configToolbar(){
        TextView textTitulo = findViewById(R.id.textTitulo);
        textTitulo.setText("Recibo");
    }


    private void iniciaComponentes(){
        textCode = findViewById(R.id.textCode);
        textDate = findViewById(R.id.textDate);
        textValue = findViewById(R.id.textValue);
    }
}