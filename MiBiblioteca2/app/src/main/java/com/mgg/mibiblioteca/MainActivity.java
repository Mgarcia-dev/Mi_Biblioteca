package com.mgg.mibiblioteca;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    // Creamos la referencia a Firebase para utilizarlo en el boton logout para salir del MainActivity
    // al Login con el que podremos iniciar sesion con otro usuario o crear otra cuenta nueva
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private String idUser;
    ListView listViewLibros;
    // Tipo de dato que se utiliza para rellenar el listView
    ArrayAdapter<String> adapterLibros;

    List<String> listaLibros = new ArrayList<>();
    List<String> listaIdLibros = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




        // Inicializar Firebase Auth y firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        idUser = mAuth.getCurrentUser().getUid();
        listViewLibros = findViewById(R.id.listLibros);

        // Actualizar la interfaz de usuario y que se vean los libros del usuario logueado
        actualizarUI();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.add) {

             // Activar el cuadro de diálogo para añadir un libro

             final EditText tastEditText = new EditText(this);
             AlertDialog dialog = new AlertDialog.Builder(this)
             .setTitle("Añadir título")
             .setMessage("¿Qué libro quieres leer?")
             .setView(tastEditText)
             .setPositiveButton("Añadir", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                // Añadir tarea a la base de datos y al ListView

                    String miLibro = tastEditText.getText().toString();

                    Map<String, Object> data = new HashMap<>();
                    data.put("nombreLibro", miLibro);
                    data.put("usuario", idUser);

                    db.collection("Libros")
                            .add(data)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    // Añadimos otro Toast al añadir un nuevo libro
                                    // PERSONALIZAR TOAST
                                    toastCorrecto("Libro añadido");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                toastError("Fallo al añadir libro");
                                }
                            });
                }
                })
             .setNegativeButton("Cancelar", null)
             .create();
             dialog.show();

            return true;

        } else if (item.getItemId() == R.id.logout) {

            // Cierre de sesion de Firebase y vuelve al activity principal (Login)
             mAuth.signOut();
             Toast.makeText(MainActivity.this, "Cerrando sesión", Toast.LENGTH_SHORT).show();
             startActivity(new Intent(MainActivity.this, Login.class));
             finish();

            return true;

        } else return super.onOptionsItemSelected(item);
    }


    private void actualizarUI() {
        db.collection("Libros")
                .whereEqualTo("usuario", idUser)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {

                            return;
                        }

                        listaLibros.clear();
                        listaIdLibros.clear();
                        for (QueryDocumentSnapshot doc : value) {
                           listaLibros.add(doc.getString("nombreLibro"));
                           listaIdLibros.add(doc.getId());
                            }

                        // Rellenar el listView con el adapter
                        if(listaLibros.size() == 0) {
                            listViewLibros.setAdapter(null);

                        } else {
                            adapterLibros = new ArrayAdapter<>(MainActivity.this, R.layout.item_libro, R.id.nuevoLibro, listaLibros);
                            listViewLibros.setAdapter(adapterLibros);
                        }
                    }
                });
    }

    // Se usa en el xml de item_libro
    public void borrarLibro(View view) {
        View parent = (View) view.getParent();
        TextView libroTextView = parent.findViewById(R.id.nuevoLibro);
        String libro = libroTextView.getText().toString();
        int posicion = listaLibros.indexOf(libro);
        toastCorrecto("Libro leído! ");

        db.collection("Libros").document(listaIdLibros.get(posicion)).delete();

    }

    public void botonEditar(View view) {
        View parent = (View) view.getParent();

        TextView textViewLibro = parent.findViewById(R.id.nuevoLibro);
        String libro = textViewLibro.getText().toString();
        int posicion = listaLibros.indexOf(libro);
        dialogActualizarLibro(posicion);
    }

    private void dialogActualizarLibro(final int posicion) {

        Log.d("MainActivity", "Editar tarea en posición: " + posicion);
        final EditText tastEditText = new EditText(this);

        tastEditText.setText(listaLibros.get(posicion));

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Editar libro")
                .setView(tastEditText)
                .setPositiveButton("Actualizar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        // Editar libro en la base de datos y al ListView

                        String miLibro = tastEditText.getText().toString();
                        String libroId = listaIdLibros.get(posicion);

                        if(!miLibro.isEmpty()) {

                            db.collection("Libros").document(libroId)
                                    .update("nombreLibro", miLibro)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            // Añadimos otro Toast al añadir un nuevo libro
                                            // PERSONALIZAR TOAST
                                            toastCorrecto("Libro actualizado");
                                            return;
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            toastError("Fallo al editar el título");
                                        }
                                    });

                        } else {
                            Toast.makeText(MainActivity.this, "Título vacío", Toast.LENGTH_SHORT).show();
                        }

                    }
                })
                .setNegativeButton("Cancelar", null)
                .create();
        dialog.show();
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
