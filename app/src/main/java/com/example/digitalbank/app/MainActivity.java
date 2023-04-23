package com.example.digitalbank.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.digitalbank.R;
import com.example.digitalbank.auth.LoginActivity;
import com.example.digitalbank.recharge.RechargeFormActivity;
import com.example.digitalbank.transfer.TransferFormActivity;
import com.example.digitalbank.user.MinhaContaActivity;
import com.example.digitalbank.deposit.DeposityActivity;
import com.example.digitalbank.helper.FirebaseHelper;
import com.example.digitalbank.helper.GetMask;
import com.example.digitalbank.model.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {
    private TextView textSaldo;
    private ProgressBar progressBar;
    private TextView textInfo;
    private ImageView imagemPerfil;
    private TextView textNotificacao;

    private Usuario usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        iniciaComponentes();

        configCliques();

    }
    @Override
    protected void onStart() {
        super.onStart();
        recuperaUsuario();

    }
    private void configDados() {
        textSaldo.setText(getString(R.string.text_value, GetMask.getValor(usuario.getSaldo())));

        if (usuario.getUrlImagem() != null) {
            Picasso.get().load(usuario.getUrlImagem())
                    .placeholder(R.drawable.loading)
                    .into(imagemPerfil);
        }

        textInfo.setText("");
        progressBar.setVisibility(View.GONE);

    }

    private void recuperaUsuario() {
        DatabaseReference usuarioRef = FirebaseHelper.getDatabaseReference()
                .child("usuarios")
                .child(FirebaseHelper.getIdFirebase());
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

    private void perfilUsuario() {
        if (usuario != null) {
            Intent intent = new Intent(this, MinhaContaActivity.class);
            intent.putExtra("usuario", usuario);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Ainda estamos recuperando as informações.", Toast.LENGTH_SHORT).show();
        }
    }

    private void redirecionaUsuario(Class clazz){
        startActivity(new Intent(this, clazz));
    }
    private void iniciaComponentes() {
        textSaldo = findViewById(R.id.textSaldo);
        progressBar = findViewById(R.id.progressBar);
        textInfo = findViewById(R.id.textInfo);
        imagemPerfil = findViewById(R.id.imagemPerfil);
        textNotificacao = findViewById(R.id.textNotificacao);
    }

    private void configCliques() {
        findViewById(R.id.cardTransferir).setOnClickListener(v ->
                redirecionaUsuario(TransferFormActivity.class));
        findViewById(R.id.cardDeslogar).setOnClickListener(v ->
                redirecionaUsuario(LoginActivity.class));
        findViewById(R.id.cardRecarga).setOnClickListener(v ->
                redirecionaUsuario(RechargeFormActivity.class));
        imagemPerfil.setOnClickListener(v -> perfilUsuario());
        findViewById(R.id.cardDeposito).setOnClickListener(v -> startActivity(new Intent(this, DeposityActivity.class)));
        findViewById(R.id.cardMinhaConta).setOnClickListener(v -> perfilUsuario());
    }
}
