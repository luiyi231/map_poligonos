package com.example.map_poligonos;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap myMap;
    private Spinner spinnerProvincias;
    private Button btnAgregarProvincia, btnDibujarPoligono;
    private ArrayAdapter<String> spinnerAdapter;

    private Map<String, LatLng> provinciasMap = new LinkedHashMap<>();
    private List<LatLng> seleccionMarcadores = new ArrayList<>();
    private Polygon poligonoActual = null; // Para borrar polígono anterior

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        inicializarProvincias();

        spinnerProvincias = findViewById(R.id.spinnerProvincias);
        btnAgregarProvincia = findViewById(R.id.btnAgregarProvincia);
        btnDibujarPoligono = findViewById(R.id.btnDibujarPoligono);

        // Spinner con ítem en blanco al inicio
        List<String> listaProvincias = new ArrayList<>();
        listaProvincias.add("Selecciona una provincia...");
        listaProvincias.addAll(provinciasMap.keySet());

        spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, listaProvincias);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerProvincias.setAdapter(spinnerAdapter);
        spinnerProvincias.setSelection(0);

        // Botón Agregar Provincia
        btnAgregarProvincia.setOnClickListener(v -> {
            String provincia = (String) spinnerProvincias.getSelectedItem();
            if (provincia != null && !provincia.equals("Selecciona una provincia...")) {
                LatLng capital = provinciasMap.get(provincia);
                agregarMarcador(capital, provincia);
                seleccionMarcadores.add(capital);
                spinnerAdapter.remove(provincia); // quitar del spinner
                spinnerProvincias.setSelection(0); // volver al ítem en blanco
            }
        });

        // Botón Dibujar Polígono ordenado
        btnDibujarPoligono.setOnClickListener(v -> dibujarPoligonoOrdenado());

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapPolygon);
        mapFragment.getMapAsync(this);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void inicializarProvincias() {
        provinciasMap.put("Andrés Ibáñez", new LatLng(-17.78333, -63.18222));
        provinciasMap.put("Chiquitos", new LatLng(-17.83873955088404, -60.74286013622246));
        provinciasMap.put("Cordillera", new LatLng(-19.65133273036605, -63.67409822204914));
        provinciasMap.put("Florida", new LatLng(-18.121356091613574, -63.95851386299854));
        provinciasMap.put("Germán Busch", new LatLng(-18.96815926184302, -57.798666002786874));
        provinciasMap.put("Ichilo", new LatLng(-17.45729525800194, -63.66270294141225));
        provinciasMap.put("Sandoval", new LatLng(-16.365201326040385, -58.399948219394645));
        provinciasMap.put("Sara", new LatLng(-17.352809192624317, -63.39541476039336));
        provinciasMap.put("Vallegrande", new LatLng(-18.488498593671242, -64.10907891916536));
        provinciasMap.put("Warnes", new LatLng(-17.51342490286226, -63.16359890868409));
        provinciasMap.put("Ñuflo de Chávez", new LatLng(-16.13026749457487, -62.027414315726055));
        provinciasMap.put("Caballero", new LatLng(-17.91609324964081, -64.52798781224406));
        provinciasMap.put("Santistevan", new LatLng(-17.325516829379183, -63.2677886866776));
        provinciasMap.put("Guarayos", new LatLng(-15.893334115567917, -63.18652230950609));
        provinciasMap.put("Velasco", new LatLng(-16.377685954288328, -60.964056992761044));
    }

    private void agregarMarcador(LatLng posicion, String titulo){
        if(myMap != null) {
            myMap.addMarker(new MarkerOptions()
                    .position(posicion)
                    .title(titulo)
                    .icon(BitmapDescriptorFactory.defaultMarker()));
            myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(posicion, 8f));
        }
    }

    private void dibujarPoligonoOrdenado() {
        if (seleccionMarcadores.isEmpty()) return;

        // Borrar polígono anterior
        if (poligonoActual != null) poligonoActual.remove();

        // Calcular centroide
        double sumLat = 0, sumLng = 0;
        for (LatLng p : seleccionMarcadores) {
            sumLat += p.latitude;
            sumLng += p.longitude;
        }
        final LatLng centro = new LatLng(sumLat / seleccionMarcadores.size(), sumLng / seleccionMarcadores.size());

        // Ordenar por ángulo respecto al centro
        List<LatLng> puntosOrdenados = new ArrayList<>(seleccionMarcadores);
        puntosOrdenados.sort((p1, p2) -> {
            double angle1 = Math.atan2(p1.latitude - centro.latitude, p1.longitude - centro.longitude);
            double angle2 = Math.atan2(p2.latitude - centro.latitude, p2.longitude - centro.longitude);
            return Double.compare(angle1, angle2);
        });

        PolygonOptions polygonOptions = new PolygonOptions()
                .addAll(puntosOrdenados)
                .strokeColor(0xFFFF0000)
                .fillColor(0x44FF0000)
                .strokeWidth(5);

        poligonoActual = myMap.addPolygon(polygonOptions);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        myMap = googleMap;
        LatLng santaCruz = new LatLng(-17.78629, -63.18117);
        myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(santaCruz, 7f));
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}
