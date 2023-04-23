package com.example.digitalbank.app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.example.digitalbank.R;
import com.example.digitalbank.auth.LoginActivity;
import com.example.digitalbank.helper.FirebaseHelper;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler(Looper.getMainLooper()).postDelayed(this::getAutenticacao, 3000);

    }

    private void getAutenticacao(){
        if(FirebaseHelper.getAutenticado()){
            startActivity(new Intent(this, MainActivity.class));
        }else {
            startActivity(new Intent(this, LoginActivity.class));
        }
        finish();
    }

}