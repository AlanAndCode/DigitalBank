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
import com.example.digitalbank.model.Deposito;
import com.example.digitalbank.model.Extrato;
import com.example.digitalbank.model.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;


import java.util.Locale;
public class DeposityActivity extends AppCompatActivity {

    private CurrencyEditText edtValue;
    private AlertDialog dialog;
    private ProgressBar progressBar;

        private Usuario usuario;
@Override
protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deposity);

        configToolbar();

        recuperaUsuario();

        iniciaComponentes();

        }

public void validaDeposito(View view){
        double valorDeposito = (double) edtValue.getRawValue() / 100;

        if(valorDeposito > 0){

        ocultarTeclado();

        progressBar.setVisibility(View.VISIBLE);

        salvarExtrato(valorDeposito);


        }else {
        showDialog("Digite um valor maior que 0.");
        }

        }

        private void salvarExtrato(double valorDeposito){

                Extrato extrato = new Extrato();
                extrato.setOperation("DEPOSITO");
                extrato.setValor(valorDeposito);
                extrato.setType("ENTRADA");

                DatabaseReference extratoRef = FirebaseHelper.getDatabaseReference()
                        .child("extratos")
                        .child(FirebaseHelper.getIdFirebase())
                        .child(extrato.getId());
                extratoRef.setValue(extrato).addOnCompleteListener(task -> {
                        if(task.isSuccessful()){

                                DatabaseReference updateExtrato = extratoRef
                                        .child("data");
                                updateExtrato.setValue(ServerValue.TIMESTAMP);

                                salvarDeposito(extrato);

                        }else {
                                showDialog("Não foi possível efetuar o deposito, tente mais tarde.");
                        }
                });

        }

        private void salvarDeposito(Extrato extrato) {

                Deposito deposito = new Deposito();
                deposito.setId(extrato.getId());
                deposito.setValor(extrato.getValor());

                DatabaseReference depositoRef = FirebaseHelper.getDatabaseReference()
                        .child("depositos")
                        .child(deposito.getId());

                depositoRef.setValue(deposito).addOnCompleteListener(task -> {
                        if(task.isSuccessful()){

                                DatabaseReference updateDeposito = depositoRef
                                        .child("data");
                                updateDeposito.setValue(ServerValue.TIMESTAMP);

                                usuario.setSaldo(usuario.getSaldo() + deposito.getValor());
                                usuario.atualizarSaldo();

                                Intent intent = new Intent(this, DepositReceiptActivity.class);
                                intent.putExtra("idDeposito", deposito.getId());
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

private void configToolbar(){
        TextView textTitulo = findViewById(R.id.textTitulo);
        textTitulo.setText("Depositar");

        findViewById(R.id.ibVoltar).setOnClickListener(v -> finish());
        }

private void iniciaComponentes(){
        edtValue = findViewById(R.id.edtValor);
        edtValue.setLocale(new Locale("PT", "br"));

        progressBar = findViewById(R.id.progressBar);
        }

// Oculta o teclado do dispositivo
private void ocultarTeclado() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(edtValue.getWindowToken(),
        InputMethodManager.HIDE_NOT_ALWAYS);
        }

        }