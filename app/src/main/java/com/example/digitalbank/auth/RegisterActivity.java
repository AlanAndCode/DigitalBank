package com.example.digitalbank.auth;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.digitalbank.MainActivity;
import com.example.digitalbank.R;
import com.example.digitalbank.helper.FireBaseHelper;
import com.example.digitalbank.model.Usuario;
import com.google.firebase.database.DatabaseReference;

public class RegisterActivity extends AppCompatActivity {


    private EditText edtNome;
    private EditText edtEmail;
    private EditText edtTelefone;
    private EditText edtSenha;
    private EditText edtConfirmaSenha;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        iniciaComponentes();
    }
public void validaDados(View view){
    String nome = edtNome.getText().toString();
    String email = edtEmail.getText().toString();
    String telefone = edtTelefone.getText().toString();
    String senha = edtSenha.getText().toString();
    String confirmaSenha = edtConfirmaSenha.getText().toString();
    if(!nome.isEmpty()){
        if(!email.isEmpty()){
            if(!telefone.isEmpty()){
                if(!senha.isEmpty()){
                    if(!confirmaSenha.isEmpty()){
                        if(senha.equals(confirmaSenha)){
                            progressBar.setVisibility(View.VISIBLE);

                            Usuario usuario = new Usuario();
                            usuario.setNome(nome);
                            usuario.setEmail(email);
                            usuario.setTelefone(telefone);
                            usuario.setSenha(senha);
                            usuario.setSaldo(0);

                            cadastrarUsuario(usuario);



}else {
        edtSenha.setError("Senhas diferentes.");
        edtConfirmaSenha.setError("Senhas diferentes.");
    }
}else {
        edtConfirmaSenha.requestFocus();
        edtConfirmaSenha.setError("Confirme sua senha.");
        }
        }else {
        edtSenha.requestFocus();
        edtSenha.setError("Informe sua senha.");
        }
        }else {
        edtTelefone.requestFocus();
        edtTelefone.setError("Informe seu telefone.");
        }
        }else {
        edtEmail.requestFocus();
        edtEmail.setError("Informe seu email.");
        }
        }else {
        edtNome.requestFocus();
        edtNome.setError("Informe seu nome.");
        }

        }


    private void cadastrarUsuario(Usuario usuario) {
        FireBaseHelper.getAuth().createUserWithEmailAndPassword(
                usuario.getEmail(), usuario.getSenha()
        ).addOnCompleteListener(task -> {
            if(task.isSuccessful()){

                String id = task.getResult().getUser().getUid();
                usuario.setId(id);

                salvarDadosUsuario(usuario);

            }else {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void salvarDadosUsuario(Usuario usuario){
        DatabaseReference usuarioRef = FireBaseHelper.getDatabaseReference()
                .child("usuarios")
                .child(usuario.getId());
        usuarioRef.setValue(usuario).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                finish();
                startActivity(new Intent(this, MainActivity.class));
            }else {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
    private void iniciaComponentes(){
        edtNome = findViewById(R.id.edtNome);
        edtEmail = findViewById(R.id.edtEmail);
        edtTelefone = findViewById(R.id.edtTelefone);
        edtSenha = findViewById(R.id.edtSenha);
        edtConfirmaSenha = findViewById(R.id.edtConfirmaSenha);
        progressBar = findViewById(R.id.progressBar);
    }



}