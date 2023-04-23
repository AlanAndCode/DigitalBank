package com.example.digitalbank.recharge;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.example.digitalbank.R;
import com.example.digitalbank.helper.FirebaseHelper;
import com.example.digitalbank.helper.GetMask;
import com.example.digitalbank.model.Recharge;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class RechargeReceiptActivity extends AppCompatActivity {

    private TextView textCodigo;
    private TextView textData;
    private TextView textValor;
    private TextView textNumero;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recharge_receipt);

        configToolbar();

        iniciaComponentes();

        getRecarga();

        configCliques();

    }

    private void configCliques(){
        findViewById(R.id.btnOK).setOnClickListener(v -> finish());
    }

    private void getRecarga(){
        String idRecarga = (String) getIntent().getSerializableExtra("idRecarga");

        DatabaseReference recargaRef = FirebaseHelper.getDatabaseReference()
                .child("recargas")
                .child(idRecarga);

        recargaRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Recharge recharge = snapshot.getValue(Recharge.class);
                configDados(recharge);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void configDados(Recharge recharge){
        textCodigo.setText(recharge.getId());
        textData.setText(GetMask.getDate(recharge.getData(), 3));
        textValor.setText(getString(R.string.text_value, GetMask.getValor(recharge.getValor())));
        textNumero.setText(recharge.getNumero());
    }

    private void configToolbar(){
        TextView textTitulo = findViewById(R.id.textTitulo);
        textTitulo.setText("Recibo");
    }

    private void iniciaComponentes(){
        textCodigo = findViewById(R.id.textCodigo);
        textData = findViewById(R.id.textData);
        textValor = findViewById(R.id.textValor);
        textNumero = findViewById(R.id.textNumero);
    }

}