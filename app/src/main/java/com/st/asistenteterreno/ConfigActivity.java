package com.st.asistenteterreno;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.material.switchmaterial.SwitchMaterial;

public class ConfigActivity extends AppCompatActivity {

    // Constante para persistencia de tema
    private static final String PREFS_NAME = "AjustesApp";
    private static final String KEY_MODO_OSCURO = "modo_oscuro";

    // Componentes de interfaz
    private SwitchMaterial switchModoOscuro;
    private TextView tvEstadoSync;
    private TextView tvDetalleSync;
    private ProgressBar progressBarSync;
    private Button btnSincronizar;
    private Button btnVolverConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_config);

        // 1. Vincular componentes visuales
        switchModoOscuro = findViewById(R.id.switchModoOscuro);
        tvEstadoSync = findViewById(R.id.tvEstadoSync);
        tvDetalleSync = findViewById(R.id.tvDetalleSync);
        progressBarSync = findViewById(R.id.progressBarSync);
        btnSincronizar = findViewById(R.id.btnSincronizar);
        btnVolverConfig = findViewById(R.id.btnVolverConfig);

        // 2. Configurar el Switch de Modo Oscuro / Claro
        configurarModoOscuro();

        // 3. Acción para botón Volver (Intent Explícito con retorno finish)
        btnVolverConfig.setOnClickListener(v -> finish());

        // 4. Acción para Sincronizar mediante Hilo Secundario (Thread)
        btnSincronizar.setOnClickListener(v -> iniciarSincronizacionSegundoPlano());
    }

    /**
     * Inicializa y escucha los cambios del Switch para alternar entre Modo Oscuro y Modo Claro.
     */
    private void configurarModoOscuro() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        // Detectar si el sistema o la preferencia guardada está actualmente en modo oscuro
        int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        boolean isCurrentDark = (currentNightMode == Configuration.UI_MODE_NIGHT_YES);
        boolean modoOscuroGuardado = prefs.getBoolean(KEY_MODO_OSCURO, isCurrentDark);

        switchModoOscuro.setChecked(modoOscuroGuardado);

        switchModoOscuro.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Guardar preferencia del usuario
            prefs.edit().putBoolean(KEY_MODO_OSCURO, isChecked).apply();

            // Aplicar el modo inmediatamente en toda la aplicación
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                Toast.makeText(this, "🌙 Modo Oscuro activado", Toast.LENGTH_SHORT).show();
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                Toast.makeText(this, "☀️ Modo Claro activado", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Inicia un hilo secundario independiente para realizar tareas pesadas
     * de red/sincronización sin bloquear el hilo principal de la interfaz de usuario (UI Thread).
     */
    private void iniciarSincronizacionSegundoPlano() {
        btnSincronizar.setEnabled(false);
        progressBarSync.setVisibility(View.VISIBLE);
        tvEstadoSync.setText("⏳ Sincronización en curso...");
        tvDetalleSync.setText("Iniciando conexión con el servidor central...");

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Simulación Etapa 1: Handshake y autenticación (1.5 seg)
                    Thread.sleep(1500);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tvDetalleSync.setText("Autenticación exitosa. Subiendo reportes y fotos tomadas en terreno...");
                        }
                    });

                    // Simulación Etapa 2: Subida y procesamiento de paquetes (2.0 seg)
                    Thread.sleep(2000);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tvDetalleSync.setText("Subida finalizada. Descargando nuevas órdenes de mantenimiento...");
                        }
                    });

                    // Simulación Etapa 3: Descarga y almacenamiento local (1.5 seg)
                    Thread.sleep(1500);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressBarSync.setVisibility(View.GONE);
                            tvEstadoSync.setText("✅ ¡Sincronización Completada con Éxito!");
                            tvDetalleSync.setText("Base de datos local actualizada. Todas las evidencias fueron respaldadas.");
                            btnSincronizar.setEnabled(true);
                            Toast.makeText(ConfigActivity.this, "Sincronización finalizada correctamente", Toast.LENGTH_LONG).show();
                        }
                    });

                } catch (InterruptedException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressBarSync.setVisibility(View.GONE);
                            tvEstadoSync.setText("❌ Sincronización Interrumpida");
                            tvDetalleSync.setText("Ocurrió un error o se abortó la sincronización con el servidor.");
                            btnSincronizar.setEnabled(true);
                            Toast.makeText(ConfigActivity.this, "Error en el proceso de sincronización", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();
    }
}