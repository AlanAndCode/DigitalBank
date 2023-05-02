package com.example.digitalbank.cobranca;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.digitalbank.R;
import com.example.digitalbank.app.MainActivity;
import com.example.digitalbank.helper.FirebaseHelper;
import com.example.digitalbank.helper.GetMask;
import com.example.digitalbank.model.Usuario;
import com.example.digitalbank.model.payment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ReceiptPayActivity extends AppCompatActivity {

    private TextView textCodigo;
    private TextView textData;
    private TextView textValor;

    private TextView textUsuario;
    private ImageView imagemUsuario;

    private Usuario usuario;
    private payment payment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt_pay);

        configToolbar();

        iniciaComponentes();

        configCliques();

        recuperaDados();

    }

    private void recuperaDados(){
        String idPayment = getIntent().getStringExtra("idPayment");

        DatabaseReference pagamentoRef = FirebaseHelper.getDatabaseReference()
                .child("payment")
                .child(idPayment);
        pagamentoRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                payment = snapshot.getValue(payment.class);

                recuperaUsuario();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void recuperaUsuario() {
        DatabaseReference usuarioRef = FirebaseHelper.getDatabaseReference()
                .child("usuarios")
                .child(payment.getIdUserDestino());
        usuarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usuario = snapshot.getValue(Usuario.class);

                configDados();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void configCliques(){
        findViewById(R.id.btnOK).setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });
    }

    private void configDados(){
        textUsuario.setText(usuario.getNome());
        if(usuario.getUrlImagem() != null){
            Picasso.get().load(usuario.getUrlImagem())
                    .placeholder(R.drawable.loading)
                    .into(imagemUsuario);
        }

        textCodigo.setText(payment.getId());
        textData.setText(GetMask.getDate(payment.getData(), 3));
        textValor.setText(getString(R.string.text_value, GetMask.getValor(payment.getValor())));

    }

    private void configToolbar(){
        TextView textTitulo = findViewById(R.id.textTitulo);
        textTitulo.setText("Recebido");
    }

    private void iniciaComponentes(){
        textCodigo = findViewById(R.id.textCodigo);
        textData = findViewById(R.id.textData);
        textValor = findViewById(R.id.textValor);
        textUsuario = findViewById(R.id.textUsuario);
        imagemUsuario = findViewById(R.id.imagemUsuario);
    }

}