package com.st.asistenteterreno;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class SegundaVista extends AppCompatActivity {

    // Componentes visuales
    private TextView tvDetalleOrden;
    private ImageView imgFotoEvidencia;
    private Button btnTomarFoto;
    private Button btnMapa;
    private Button btnLlamar;
    private Button btnCorreo;
    private Button btnWifi;
    private Button btnVolver;

    // Launchers modernos para captura de foto y solicitud de permisos
    private ActivityResultLauncher<Intent> camaraLauncher;
    private ActivityResultLauncher<String> permisoCamaraLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_segunda_vista);

        // 1. Registro de Launchers (Cámara y Permiso en tiempo de ejecución)
        configurarLaunchers();

        // 2. Conectar variables con los IDs del layout XML
        inicializarVistas();

        // 3. Recepción y validación del Intent Explícito con putExtra
        recibirDatosOrden();

        // 4. Configuración de los 5 Intents Implícitos y botón volver
        configurarListeners();
    }

    private void configurarLaunchers() {
        // Launcher para el resultado de la captura de foto
        camaraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Bundle extras = result.getData().getExtras();
                        if (extras != null) {
                            Bitmap imagenBitmap = (Bitmap) extras.get("data");
                            if (imagenBitmap != null) {
                                imgFotoEvidencia.setImageBitmap(imagenBitmap);
                                Toast.makeText(this, "Evidencia fotográfica capturada con éxito", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(this, "No se obtuvo la imagen de la cámara", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {
                        Toast.makeText(this, "Captura fotográfica cancelada", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        // Launcher para solicitar permiso CAMERA en tiempo de ejecución (Evita SecurityException)
        permisoCamaraLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        ejecutarCapturaCamara();
                    } else {
                        Toast.makeText(this, "Permiso de cámara denegado. No se puede capturar la evidencia.", Toast.LENGTH_LONG).show();
                    }
                }
        );
    }

    private void inicializarVistas() {
        tvDetalleOrden = findViewById(R.id.tvDetalleOrden);
        imgFotoEvidencia = findViewById(R.id.imgFotoEvidencia);
        btnTomarFoto = findViewById(R.id.btnTomarFoto);
        btnMapa = findViewById(R.id.btnMapa);
        btnLlamar = findViewById(R.id.btnLlamar);
        btnCorreo = findViewById(R.id.btnCorreo);
        btnWifi = findViewById(R.id.btnWifi);
        btnVolver = findViewById(R.id.btnVolver);
    }

    private void recibirDatosOrden() {
        if (getIntent() != null && getIntent().hasExtra("NUM_ORDEN")) {
            String orden = getIntent().getStringExtra("NUM_ORDEN");
            if (orden != null && !orden.trim().isEmpty()) {
                tvDetalleOrden.setText("Orden Asignada: " + orden);
            }
        }
    }

    private void configurarListeners() {
        // Intent Implícito 1: Cámara fotográfica con verificación de permisos en tiempo de ejecución
        btnTomarFoto.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                ejecutarCapturaCamara();
            } else {
                permisoCamaraLauncher.launch(Manifest.permission.CAMERA);
            }
        });

        // Intent Implícito 2: Mapa GPS con URI geo
        btnMapa.setOnClickListener(v -> {
            try {
                Uri gmmIntentUri = Uri.parse("geo:0,0?q=Santiago, Chile");
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                startActivity(mapIntent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(this, "No se encontró aplicación de mapas instalada", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(this, "Error al abrir el mapa: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Intent Implícito 3: Llamada telefónica (marcador numérico)
        btnLlamar.setOnClickListener(v -> {
            try {
                Uri telUri = Uri.parse("tel:+56912345678");
                Intent dialIntent = new Intent(Intent.ACTION_DIAL, telUri);
                startActivity(dialIntent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(this, "No se encontró marcador telefónico", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(this, "Error al iniciar el marcador: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Intent Implícito 4: Correo electrónico con Asunto y Cuerpo
        btnCorreo.setOnClickListener(v -> {
            try {
                Uri mailUri = Uri.parse("mailto:soporte@terreno.cl");
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, mailUri);
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Reporte Técnico - Asistente de Terreno");
                emailIntent.putExtra(Intent.EXTRA_TEXT, "Estimado soporte,\n\nAdjunto el reporte de la faena realizada en terreno.\n\nSaludos cordiales.");
                startActivity(emailIntent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(this, "No se encontró cliente de correo electrónico", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(this, "Error al abrir el cliente de correo: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Intent Implícito 5: Ajustes de Red Wi-Fi
        btnWifi.setOnClickListener(v -> {
            try {
                Intent wifiIntent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                startActivity(wifiIntent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(this, "No se pudo abrir los ajustes de Wi-Fi", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(this, "Error al acceder a configuración: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Botón Volver (Cierra la actividad actual regresando a Inicio)
        btnVolver.setOnClickListener(v -> finish());
    }

    private void ejecutarCapturaCamara() {
        try {
            Intent intentCamara = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            camaraLauncher.launch(intentCamara);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "No se encontró una aplicación de cámara disponible", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Error al iniciar la cámara: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}