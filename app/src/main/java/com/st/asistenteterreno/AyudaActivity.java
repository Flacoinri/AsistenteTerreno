package com.st.asistenteterreno;

import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class AyudaActivity extends AppCompatActivity {

    private Button btnVolverAyuda;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ayuda);

        // Vinculación y acción de retorno explícito
        btnVolverAyuda = findViewById(R.id.btnVolverAyuda);
        btnVolverAyuda.setOnClickListener(v -> finish());
    }
}