package com.example.alfredo.marvel;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class DetalleActivity extends Activity {

    private TextView id, nombre, fecha, descripcion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle);

        String identificador = (String)getIntent().getSerializableExtra("id");

        id = (TextView)findViewById(R.id.idSuperheroe);
        nombre = (TextView)findViewById(R.id.nombreSuperheroe);
        fecha = (TextView)findViewById(R.id.fechaSuperheroe);
        descripcion = (TextView)findViewById(R.id.descripcionSuperheroe);

        id.setText("ID: " + identificador);

    }
}
