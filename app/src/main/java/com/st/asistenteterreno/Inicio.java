package com.st.asistenteterreno;

import android.content.Intent;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Toast;

public class Inicio extends AppCompatActivity {

    //Variables
    Button btnSegundaVista;
    Button btnLinterna;
    Button btnAyuda;
    Button btnConfiguracion;

    //Variables para Linterna
    CameraManager cameraManager;
    String idCamara;
    boolean linternaEncendida = false;

    //Variable ubicación
    LocationManager locationManager;
    double latitud = 0;
    double longitud = 0;
    boolean ubicacionObtenida = false;

    //Cod Permiso
    final int PERMISO_UBICACION = 100;
    final int PERMISO_CAMARA = 200;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_inicio);

        //Referencias
        btnSegundaVista = findViewById(R.id.btnSegundaVista);
        btnLinterna = findViewById(R.id.btnLinterna);
        btnAyuda = findViewById(R.id.btnAyuda);
        btnConfiguracion = findViewById(R.id.btnConfiguracion);

        // AyudaActivity

        btnAyuda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentAyuda = new Intent(Inicio.this, AyudaActivity.class);
                startActivity(intentAyuda);
            }
        });

        btnConfiguracion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentConfig = new Intent(Inicio.this, ConfigActivity.class);
                startActivity(intentConfig);
            }
        });

        // Inicializar el servicio de la cámara

        cameraManager = (CameraManager) getSystemService(CAMERA_SERVICE);
        try {
            //Obtendremos el identificador de la cámara trasera
            idCamara = cameraManager.getCameraIdList()[0];
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

        btnLinterna.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (linternaEncendida) {
                        cameraManager.setTorchMode(idCamara, false);
                        linternaEncendida = false;
                        btnLinterna.setText("🔦 Encender Linterna");
                    } else {
                        cameraManager.setTorchMode(idCamara, true);
                        linternaEncendida = true;
                        btnLinterna.setText("🔦 Apagar Linterna");
                    }
                } catch (CameraAccessException e) {
                    Toast.makeText(Inicio.this, "Error al acceder al flash", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Llamar a la siguiente vista(segunda_vista)

        btnSegundaVista.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Intent Explicito
                        Intent segunda_vista = new Intent(
                                Inicio.this, SegundaVista.class
                        );

                        segunda_vista.putExtra("NUM_ORDEN", "OT-8492: Mantenimiento Preventivo");
                        startActivity(segunda_vista);
                    }
                }
        );


    }
}