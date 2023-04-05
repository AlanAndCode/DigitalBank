package com.example.digitalbank.app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.digitalbank.MainActivity;
import com.example.digitalbank.R;
import com.example.digitalbank.auth.LoginActivity;
import com.example.digitalbank.helper.FireBaseHelper;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
    }

    private void getAutenticacao(){
        if(FireBaseHelper.getAutenticado()){
            startActivity(new Intent(this, MainActivity.class));
        }else {
            startActivity(new Intent(this, LoginActivity.class));
        }
        finish();
    }

}