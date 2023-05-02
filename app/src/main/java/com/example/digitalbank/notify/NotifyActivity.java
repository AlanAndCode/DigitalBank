package com.example.digitalbank.notify;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.digitalbank.R;
import com.example.digitalbank.adapter.NotifyAdapter;
import com.example.digitalbank.cobranca.PagarCobrancaFormActivity;
import com.example.digitalbank.helper.FirebaseHelper;
import com.example.digitalbank.model.Notify;
import com.example.digitalbank.transfer.TransferReceiptActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.tsuryo.swipeablerv.SwipeLeftRightCallback;
import com.tsuryo.swipeablerv.SwipeableRecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NotifyActivity extends AppCompatActivity implements NotifyAdapter.OnClick {

    private NotifyAdapter notificacaoAdapter;
    private final List<Notify> notifyList = new ArrayList<>();

    private SwipeableRecyclerView rvNotificacoes;
    private ProgressBar progressBar;
    private TextView textInfo;

    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notify);

        configToolbar();

        iniciaComponentes();

        configRv();

        recuperaNotificacoes();

    }

    private void configRv(){
        rvNotificacoes.setLayoutManager(new LinearLayoutManager(this));
        rvNotificacoes.setHasFixedSize(true);
        notificacaoAdapter = new NotifyAdapter(notifyList, getBaseContext(), this);
        rvNotificacoes.setAdapter(notificacaoAdapter);

        rvNotificacoes.setListener(new SwipeLeftRightCallback.Listener() {
            @Override
            public void onSwipedLeft(int position) {
                showDialogRemover(notifyList.get(position));
            }

            @Override
            public void onSwipedRight(int position) {
                showDialogStatusNotificacao(notifyList.get(position));
            }
        });
    }

    private void showDialogStatusNotificacao(Notify notificacao){
        AlertDialog.Builder builder = new AlertDialog.Builder(
                this, R.style.CustomAlertDialog
        );

        View view = getLayoutInflater().inflate(R.layout.layout_dialog, null);

        TextView textTitulo = view.findViewById(R.id.textTitulo);
        TextView textMensagem = view.findViewById(R.id.textMensagem);

        if(notificacao.isLida()){
            textTitulo.setText("Deseja marcar esta notificação como Não lida ?");
            textMensagem.setText("Aperte em sim para marcar esta notificação como Não lida ou aperte em fechar para cancelar.");
        }else {
            textTitulo.setText("Deseja marcar esta notificação como Lida ?");
            textMensagem.setText("Aperte em sim para marcar esta notificação como Lida ou aperte em fechar para cancelar.");
        }

        view.findViewById(R.id.btnOK).setOnClickListener(v -> {

            notificacao.salvar();

            dialog.dismiss();
        });

        view.findViewById(R.id.btnFechar).setOnClickListener(v -> {
            dialog.dismiss();
            notificacaoAdapter.notifyDataSetChanged();
        });

        builder.setView(view);

        dialog = builder.create();
        dialog.show();
    }

    private void showDialogRemover(Notify notificacao){
        AlertDialog.Builder builder = new AlertDialog.Builder(
                this, R.style.CustomAlertDialog
        );

        View view = getLayoutInflater().inflate(R.layout.layout_dialog, null);

        TextView textTitulo = view.findViewById(R.id.textTitulo);
        TextView textMensagem = view.findViewById(R.id.textMensagem);

        textTitulo.setText("Deseja remover a notificação ?");
        textMensagem.setText("Aperte em sim para remover esta notificação ou aperte em fechar para cancelar.");

        view.findViewById(R.id.btnOK).setOnClickListener(v -> {
            removerNotificacoes(notificacao);
            dialog.dismiss();
        });

        view.findViewById(R.id.btnFechar).setOnClickListener(v -> {
            dialog.dismiss();
            notificacaoAdapter.notifyDataSetChanged();
        });

        builder.setView(view);

        dialog = builder.create();
        dialog.show();
    }

    private void removerNotificacoes(Notify notificacao){
        DatabaseReference notificacaoRef = FirebaseHelper.getDatabaseReference()
                .child("notify")
                .child(FirebaseHelper.getIdFirebase())
                .child(notificacao.getId());
        notificacaoRef.removeValue().addOnCompleteListener(task -> {
            if(task.isSuccessful()){

                notifyList.remove(notificacao);

                if(notifyList.isEmpty()){
                    textInfo.setText("Nenhuma notificação recebida.");
                }else {
                    textInfo.setText("");
                }

                Toast.makeText(this, "Notificação removida com sucesso!", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(this, "Erro ao remover a Notificação!", Toast.LENGTH_SHORT).show();
            }
            notificacaoAdapter.notifyDataSetChanged();
        });

    }

    private void recuperaNotificacoes(){
        DatabaseReference notificacaoRef = FirebaseHelper.getDatabaseReference()
                .child("notify")
                .child(FirebaseHelper.getIdFirebase());
        notificacaoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    notifyList.clear();
                    for (DataSnapshot ds : snapshot.getChildren()){
                        Notify notificacao = ds.getValue(Notify.class);
                        notifyList.add(notificacao);
                    }
                    textInfo.setText("");
                }else {
                    textInfo.setText("Você não tem nenhuma notificação.");
                }

                Collections.reverse(notifyList);
                progressBar.setVisibility(View.GONE);
                notificacaoAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void configToolbar(){
        TextView textTitulo = findViewById(R.id.textTitulo);
        textTitulo.setText("Notify");

        findViewById(R.id.ibVoltar).setOnClickListener(v -> finish());
    }

    private void iniciaComponentes() {
        progressBar = findViewById(R.id.progressBar);
        textInfo = findViewById(R.id.textInfo);
        rvNotificacoes = findViewById(R.id.rvNotificacoes);
    }

    @Override
    public void OnClickListener(Notify notificacao) {
        if(notificacao.getOperacao().equals("COBRANCA")){
            Intent intent = new Intent(this, PagarCobrancaFormActivity.class);
            intent.putExtra("notify", notificacao);
            startActivity(intent);
        }else if(notificacao.getOperacao().equals("TRANSFERENCIA")){
            Intent intent = new Intent(this, TransferReceiptActivity.class);
            intent.putExtra("idTransferencia", notificacao.getIdOperacao());
            startActivity(intent);
        }else {

        }
    }

}