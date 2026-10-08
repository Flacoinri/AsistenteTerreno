package com.st.asistenteterreno;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

public class SegundaVista extends AppCompatActivity {

    private ImageView imgFotoEvidencia;
    private Button btnTomarFoto;
    private Button btnVolver;

    private ActivityResultLauncher<Intent> camaraLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_segunda_vista);


        //Configuramos qué hacer cuando la cámara nos devuelva una respuesta
        camaraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    // Validación: verificamos que el usuario haya aceptado la foto (RESULT_OK)
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        // La cámara devuelve la miniatura en un objeto llamado Bitmap
                        Bundle extras = result.getData().getExtras();
                        if (extras != null) {
                            Bitmap imagenBitmap = (Bitmap) extras.get("data");
                            // Asignamos la imagen al ImageView para que se vea en pantalla
                            imgFotoEvidencia.setImageBitmap(imagenBitmap);
                        }
                    }
                }
        );

        //Conectar variables con los ID de los XML
        imgFotoEvidencia = findViewById(R.id.imgFotoEvidencia);
        btnTomarFoto = findViewById(R.id.btnTomarFoto);
        btnVolver = findViewById(R.id.btnVolver);

        //Acción del botón Volver: cierra la pantalla actual
        btnVolver.setOnClickListener(view -> finish());

        //Acción del botón Tomar Foto: lanza el Intent Implícito de Cámara
        btnTomarFoto.setOnClickListener(view -> {
            try {
                Intent intentCamara = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                camaraLauncher.launch(intentCamara);
            } catch (Exception e) {
                Toast.makeText(this, "No se encontró una aplicación de cámara", Toast.LENGTH_SHORT).show();
            }
        });
    }
}