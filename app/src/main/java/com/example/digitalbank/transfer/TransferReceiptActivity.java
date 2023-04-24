package com.example.digitalbank.transfer;

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
import com.example.digitalbank.model.Transfer;
import com.example.digitalbank.model.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class TransferReceiptActivity extends AppCompatActivity {

    private TextView textCodigo;
    private TextView textUsuario;
    private TextView textData;
    private TextView textValor;
    private TextView textTipoTransferencia;
    private TextView textInfoTransferencia;
    private ImageView imagemUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer_receipt);

        configToolbar();

        iniciaComponentes();

        recuperaTransferencia();

        configCliques();

    }

    private void configCliques(){
        findViewById(R.id.btnOK).setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });
    }

    private void recuperaTransferencia(){
        String idTransferencia = getIntent().getStringExtra("idTransferencia");

        DatabaseReference transferenciaRef = FirebaseHelper.getDatabaseReference()
                .child("transferencias")
                .child(idTransferencia);
        transferenciaRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Transfer transferencia = snapshot.getValue(Transfer.class);
                if(transferencia != null){

                    if(transferencia.getIdUserDestino().equals(FirebaseHelper.getIdFirebase())){
                        recuperaUsuario(transferencia, FirebaseHelper.getIdFirebase());
                    }else {
                        recuperaUsuario(transferencia, transferencia.getIdUserDestino());
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void recuperaUsuario(Transfer transfer, String idUsarioDestino){
        DatabaseReference usuarioRef = FirebaseHelper.getDatabaseReference()
                .child("usuarios")
                .child(idUsarioDestino);
        usuarioRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Usuario usuario = snapshot.getValue(Usuario.class);
                if(usuario!= null){
                    configDados(transfer, usuario);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void configDados(Transfer transferencia, Usuario usuario){
        textCodigo.setText(transferencia.getId());
        textData.setText(GetMask.getDate(transferencia.getData(), 3));
        textValor.setText(getString(R.string.text_value, GetMask.getValor(transferencia.getValor())));

        if(usuario.getId().equals(FirebaseHelper.getIdFirebase())){
            textTipoTransferencia.setText(getString(R.string.text_type_transfer, "Recebida"));
            textInfoTransferencia.setText("O valor recebido via transferência já foi adicionado ao saldo da conta.");
        }else {
            textTipoTransferencia.setText(getString(R.string.text_type_transfer, "Enviada"));
            textInfoTransferencia.setText("Débito realizado com sucesso. A previsão do crédito na conta de destino é de até 30 minutos.");
        }

        if(usuario.getUrlImagem() != null){
            Picasso.get().load(usuario.getUrlImagem())
                    .placeholder(R.drawable.loading)
                    .into(imagemUsuario);
        }
        textUsuario.setText(usuario.getNome());
    }

    private void configToolbar(){
        TextView textTitulo = findViewById(R.id.textTitulo);
        textTitulo.setText("Recibo");
    }

    private void iniciaComponentes(){
        textCodigo = findViewById(R.id.textCodigo);
        textUsuario = findViewById(R.id.textUsuario);
        textData = findViewById(R.id.textData);
        textValor = findViewById(R.id.textValor);
        imagemUsuario = findViewById(R.id.imagemUsuario);
        textTipoTransferencia = findViewById(R.id.textTipoTransferencia);
        textInfoTransferencia = findViewById(R.id.textInfoTransferencia);
    }

}