package com.example.digitalbank.deposit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.blackcat.currencyedittext.CurrencyEditText;
import com.example.digitalbank.R;
import com.example.digitalbank.helper.FirebaseHelper;
import com.example.digitalbank.model.Deposity;
import com.example.digitalbank.model.Statement;
import com.example.digitalbank.model.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;


import java.util.Locale;

public class DeposityActivity extends AppCompatActivity {
    private CurrencyEditText edtValor;
    private AlertDialog dialog;
    private ProgressBar progressBar;

    private Usuario usuario;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deposity);

        configToolbar();
        iniciaComponentes();
    }
    public void validaDeposito(View view){
        double valorDeposito = (double) edtValor.getRawValue() / 100;

        if(valorDeposito > 0){

            ocultarTeclado();

            progressBar.setVisibility(View.VISIBLE);




        }else {
            showDialog("Digite um valor maior que 0.");
        }

    }
    private void showDialog(String msg){
        AlertDialog.Builder builder = new AlertDialog.Builder(
                this, R.style.CustomAlertDialog
        );

        View view = getLayoutInflater().inflate(R.layout.layout_dialog_info, null);

        TextView textTitulo = view.findViewById(R.id.textTitulo);
        textTitulo.setText("Atenção");

        TextView mensagem = view.findViewById(R.id.textMensagem);
        mensagem.setText(msg);

        Button btnOK = view.findViewById(R.id.btnOK);
        btnOK.setOnClickListener(v -> dialog.dismiss());

        builder.setView(view);

        dialog = builder.create();
        dialog.show();

    }

    private void salvarExtrato(double valorDeposito){

        Statement statement = new Statement();
        statement.setOperation("DEPOSITY");
        statement.setValue(valorDeposito);
        statement.setType("ENTER");

        DatabaseReference statementRef = FirebaseHelper.getDatabaseReference()
                .child("statements")
                .child(FirebaseHelper.getIdFirebase())
                .child(statement.getId());
        statementRef.setValue(statement).addOnCompleteListener(task -> {
            if(task.isSuccessful()){

                DatabaseReference updateStatement = statementRef
                        .child("date");
                updateStatement.setValue(ServerValue.TIMESTAMP);

                salvarDeposito(statement);

            }else {
                showDialog("Não foi possível efetuar o deposito, tente mais tarde.");
            }
        });

    }

    private void salvarDeposito(Statement statement) {

        Deposity deposity = new Deposity();
        deposity.setId(deposity.getId());
        deposity.setValue(deposity.getValue());

        DatabaseReference depositoRef = FirebaseHelper.getDatabaseReference()
                .child("depositys")
                .child(deposity.getId());

        depositoRef.setValue(deposity).addOnCompleteListener(task -> {
            if(task.isSuccessful()){

                DatabaseReference updateDeposito = depositoRef
                        .child("date");
                updateDeposito.setValue(ServerValue.TIMESTAMP);

                usuario.setSaldo(usuario.getSaldo() + deposity.getValue());
                usuario.updateSaldo();

                Intent intent = new Intent(this, DepositReceiptActivity.class);
                intent.putExtra("idDeposito", deposity.getId());
                startActivity(intent);
                finish();

            }else {
                progressBar.setVisibility(View.GONE);
                showDialog("Não foi possível efetuar o deposito, tente mais tarde.");
            }
        });

    }

    private void recuperaUsuario(){
        DatabaseReference usuarioRef = FirebaseHelper.getDatabaseReference()
                .child("usuarios")
                .child(FirebaseHelper.getIdFirebase());
        usuarioRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usuario = snapshot.getValue(Usuario.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void configToolbar(){
        TextView textTitulo = findViewById(R.id.textTitulo);
        textTitulo.setText("Depositar");

        findViewById(R.id.ibVoltar).setOnClickListener(v -> finish());
    }

    private void iniciaComponentes(){
        edtValor = findViewById(R.id.edtValor);
        edtValor.setLocale(new Locale("PT", "br"));

        progressBar = findViewById(R.id.progressBar);
    }

    private void ocultarTeclado() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(edtValor.getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }

}