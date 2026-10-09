package com.st.asistenteterreno;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;

public class Inicio extends AppCompatActivity {

    // Componentes de interfaz de usuario
    private Button btnSegundaVista;
    private Button btnLinterna;
    private Button btnAyuda;
    private Button btnConfiguracion;

    // Variables para el control de Hardware (Linterna)
    private CameraManager cameraManager;
    private String idCamara = null;
    private boolean linternaEncendida = false;

    // Launcher para solicitar permiso de cámara en tiempo de ejecución
    private ActivityResultLauncher<String> permisoCamaraLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Cargar el tema (Modo Oscuro / Claro) guardado antes de inflar la vista
        aplicarTemaGuardado();

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_inicio);

        // 1. Vincular componentes visuales
        btnSegundaVista = findViewById(R.id.btnSegundaVista);
        btnLinterna = findViewById(R.id.btnLinterna);
        btnAyuda = findViewById(R.id.btnAyuda);
        btnConfiguracion = findViewById(R.id.btnConfiguracion);

        // 2. Configurar launcher para permiso en tiempo de ejecución
        permisoCamaraLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        alternarLinterna();
                    } else {
                        Toast.makeText(this, "Se requiere permiso de cámara para controlar la linterna", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        // 3. Inicializar CameraManager para control de Flash
        inicializarLinterna();

        // 4. Configuración de navegación (Intents Explícitos)
        btnSegundaVista.setOnClickListener(v -> {
            Intent intentSegundaVista = new Intent(Inicio.this, SegundaVista.class);
            intentSegundaVista.putExtra("NUM_ORDEN", "OT-8492: Mantenimiento Preventivo");
            startActivity(intentSegundaVista);
        });

        btnAyuda.setOnClickListener(v -> {
            Intent intentAyuda = new Intent(Inicio.this, AyudaActivity.class);
            startActivity(intentAyuda);
        });

        btnConfiguracion.setOnClickListener(v -> {
            Intent intentConfig = new Intent(Inicio.this, ConfigActivity.class);
            startActivity(intentConfig);
        });

        // 5. Control de Linterna con verificación de permisos en tiempo de ejecución
        btnLinterna.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                alternarLinterna();
            } else {
                permisoCamaraLauncher.launch(Manifest.permission.CAMERA);
            }
        });
    }

    /**
     * Aplica la preferencia de Modo Oscuro / Claro guardada en SharedPreferences.
     */
    private void aplicarTemaGuardado() {
        SharedPreferences prefs = getSharedPreferences("AjustesApp", MODE_PRIVATE);
        if (prefs.contains("modo_oscuro")) {
            boolean modoOscuro = prefs.getBoolean("modo_oscuro", false);
            if (modoOscuro) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        }
    }

    /**
     * Inicializa el servicio de cámara y detecta el identificador de flash disponible.
     */
    private void inicializarLinterna() {
        boolean tieneFlash = getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
        if (!tieneFlash) {
            return;
        }

        cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        if (cameraManager != null) {
            try {
                String[] camaras = cameraManager.getCameraIdList();
                if (camaras.length > 0) {
                    idCamara = camaras[0];
                }
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Alterna el estado de la linterna mediante CameraManager.setTorchMode.
     */
    private void alternarLinterna() {
        if (cameraManager == null || idCamara == null) {
            Toast.makeText(this, "El dispositivo no cuenta con flash disponible", Toast.LENGTH_SHORT).show();
            return;
        }

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
            Toast.makeText(this, "Error al acceder a la linterna: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (linternaEncendida && cameraManager != null && idCamara != null) {
            try {
                cameraManager.setTorchMode(idCamara, false);
                linternaEncendida = false;
                if (btnLinterna != null) {
                    btnLinterna.setText("🔦 Encender Linterna");
                }
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }
    }
}