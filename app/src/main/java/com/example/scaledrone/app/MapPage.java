package com.example.scaledrone.app;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class MapPage extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private DatabaseAdapter databaseAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_page);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        databaseAdapter = new DatabaseAdapter(this);
        System.out.println("                                                                     ");
        System.out.println("--------------------------------------------------------------------");
        System.out.println("                                 Map open");
        System.out.println("--------------------------------------------------------------------");
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        databaseAdapter.open();
        makeMarkerFromDatabase();
    }

    private void makeMarkerFromDatabase(){
        List<User> users = databaseAdapter.getUsers();
        List<LatLng> cords = new ArrayList<>();
        for (User user : users) {
            cords.add(new LatLng(user.getName(), user.getYear()));
        }
        for (LatLng position : cords) {
            mMap.addMarker(new MarkerOptions().position(position).title("Marker in position from list"));
        }
        LatLng msc = new LatLng(55,37);
        //TODO
        mMap.moveCamera(CameraUpdateFactory.newLatLng(msc));
    }

    // Обработчик нажатия кнопки "назад"
    public void buttonClickBack(View view) {
        System.out.println("The *Back* button is pressed");
        Intent intent = new Intent(MapPage.this, MainActivity.class);
        startActivity(intent);

    }
}
