package com.mgg.mibiblioteca;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;



import kotlin.Unit;

public class Splash extends AppCompatActivity {

        ProgressBar progressBar;
        TextView byM, miBiblioteca;
        ImageView logoImageView;
        final int TIME_SPLASH = 3000;
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            setContentView(R.layout.activity_splash1);
            getSupportActionBar().hide();

            // Agregar animaciones
            Animation animacion1 = AnimationUtils.loadAnimation(this, R.anim.desplazamiento_arriba);
            Animation animacion2 = AnimationUtils.loadAnimation(this, R.anim.desplazamiento_abajo);

            progressBar = findViewById(R.id.progressBar);
            byM =findViewById(R.id.byMSplash);
            miBiblioteca = findViewById(R.id.miBibliotecaSplash);
            logoImageView = findViewById(R.id.imagenSplash);

            byM.setAnimation(animacion2);
            miBiblioteca.setAnimation(animacion2);
            logoImageView.setAnimation(animacion1);

            new Handler().postDelayed(()-> {

                Intent intent = new Intent(Splash.this, Login.class);
                startActivity(intent);
                finish();
            },TIME_SPLASH);

        }
    }
