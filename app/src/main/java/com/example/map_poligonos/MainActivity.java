package com.example.map_poligonos;

import android.os.Bundle;

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
import com.google.android.gms.maps.model.PolygonOptions;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    //mapa a utilizar
    private GoogleMap myMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapPolygon);
        mapFragment.getMapAsync(this);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        myMap = googleMap;

        LatLng santaCruz = new LatLng ( -17.78629, -63.18117);
        myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(santaCruz, 20));
        LatLng punto1 = new LatLng( -17.755008388519837, -63.21588822159527);
        LatLng punto2 = new LatLng( -17.751100570179503, -63.14943397160269);
        LatLng punto3 = new LatLng(  -17.813105870286222, -63.14666876121117);
        LatLng punto4 = new LatLng ( -17.817691708477057, -63.213212211530525);

        PolygonOptions polygonOptions = new PolygonOptions()
                .add(punto1,punto2,punto3,punto4)
                .strokeColor(0xFFFF0000)
                .fillColor(0x44ff0000)
                .strokeWidth(5);
        myMap.addPolygon(polygonOptions);

        agregarMarcador(punto1, "Ubicacion 1");
        agregarMarcador(punto2, "Ubicacion 2");
        agregarMarcador(punto3, "Ubicacion 3");
        agregarMarcador(punto4, "Ubicacion 4");

    }
    private void agregarMarcador(LatLng posicion, String titulo){
        myMap.addMarker(new MarkerOptions()
                .position(posicion)
                .title(titulo)
                .icon(BitmapDescriptorFactory.defaultMarker()));
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}
