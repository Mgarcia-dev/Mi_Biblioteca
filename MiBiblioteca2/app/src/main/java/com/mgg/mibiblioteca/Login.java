package com.mgg.mibiblioteca;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Patterns;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {

    Button botonLogin;

    TextView botonRegistro;

    // Usaremos estas variables para registrar al usuario en el boton Login
    EditText emailText, passText;


    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);





        //Quitamos el actionBar
        getSupportActionBar().hide();

        // Inicializar Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Referanciamos las variables para iniciar sesion con login
        emailText = findViewById(R.id.cajaCorreo);
        passText = findViewById(R.id.cajaPass);


        // Ponemos el boton Login a la escucha y le damos funcionalidad
        botonLogin = findViewById(R.id.botonLogin);
        botonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = emailText.getText().toString();
                String password = passText.getText().toString();

                if (email.isEmpty() || !email.contains("@") || !email.contains(".")) {
                    emailText.setError("Correo no válido");

                }else if(password.isEmpty() || password.length() < 6){
                    passText.setError("La contraseña debe contener al menos 6 caracteres");

                }else {
                        // LOGIN EN FIREBASE
                        mAuth.signInWithEmailAndPassword(email, password)
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            // Se ha logueado el usuario con éxito y se pasa al activityMain
                                            // Creamos el intent para pasar de la pantalla de login a la main cuando pulsemos
                                            Intent intent = new Intent(Login.this, MainActivity.class);
                                            startActivity(intent);
                                        } else {
                                            // Fallo en el logueo de usuario, sale un toast con mensaje de error
                                            toastError("Usuario o contraseña incorrecto");                                        }
                                    }
                                });
                    }
                }
        });


        botonRegistro = findViewById(R.id.botonRegistro);
        botonRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // CREAR USUARIO EN FIREBASE

                String email = emailText.getText().toString();
                String password = passText.getText().toString();

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Si se crea la cuenta con éxito, pasamos al siguiente activity (main) y sacamos
                                    // un toast para mandar un mensaje al usuario de que se ha realizado con exito la
                                    //creacion de la cuenta

                                    // Añadimos un toast para avisar que se ha registrado un nuevo usuario
                                    // PERSONALIZAR TOAST CON UN NUEVO XML 2.4 TEMARIO"!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                                    toastCorrecto("Cuenta creada con éxito");

                                    //Toast.makeText(Login.this, "Usuario registrado", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(Login.this, MainActivity.class);
                                    startActivity(intent);

                                } else {
                                   // Toast.makeText(Login.this, "Fallo de autenticación",
                                     //       Toast.LENGTH_SHORT).show();
                                    toastError("Error al crear cuenta");

                                }
                            }
                        });
            }
        });


    }
    public void toastCorrecto(String mensaje) {
        LayoutInflater layoutInflater = getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.custom_toast_check, (ViewGroup) findViewById(R.id.customToastCheck));
        TextView txtMensaje = view.findViewById(R.id.txtCheckToast);
        txtMensaje.setText(mensaje);

        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.BOTTOM, 0, 200);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(view);
        toast.show();
    }

    public void toastError(String mensaje) {
        LayoutInflater layoutInflater = getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.custom_toast_alert, (ViewGroup) findViewById(R.id.customToastAlert));
        TextView txtMensaje = view.findViewById(R.id.txtAlertToast);
        txtMensaje.setText(mensaje);

        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.BOTTOM, 0, 200);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(view);
        toast.show();
    }
}