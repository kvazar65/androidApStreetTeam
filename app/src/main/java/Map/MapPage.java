package Map;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import Chat.ChannelInfoMarker;
import Map.locations.PermissionUtils;
import Map.locations.Place;
import ru.streetteam.app.R;

import com.appolica.interactiveinfowindow.InfoWindow;
import com.appolica.interactiveinfowindow.InfoWindowManager;
import com.appolica.interactiveinfowindow.fragment.MapInfoWindowFragment;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.CancelableCallback;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import Database.DatabaseAdapter;


public class MapPage extends AppCompatActivity implements
        GoogleMap.OnMarkerClickListener,
        GoogleMap.OnMapClickListener,
        ActivityCompat.OnRequestPermissionsResultCallback,
        OnMapReadyCallback {

    private static final int SCROLL_BY_PX = 100; // Величина, на которую прокручивается камера. Величина  в необработанных пикселях, а не в dp (density-independent pixels).
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private boolean permissionDenied = false;
    private GoogleMap map;
    private DatabaseAdapter databaseAdapter;
    private CompoundButton animateToggle;
    private CompoundButton customDurationToggle;
    private SeekBar customDurationBar;
    private Map<ChannelInfoMarker, InfoWindow> infoWindowMap;
    private InfoWindowManager infoWindowManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_page);
        animateToggle = findViewById(R.id.animate);
        customDurationToggle = findViewById(R.id.duration_toggle);
        customDurationBar = findViewById(R.id.duration_bar);
        updateEnabledState();
        infoWindowMap = new HashMap<>();
        MapInfoWindowFragment mapFragment = (MapInfoWindowFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        databaseAdapter = new DatabaseAdapter(this);
        infoWindowManager = mapFragment.infoWindowManager();
        System.out.println("                                                                     ");
        System.out.println("--------------------------------------------------------------------");
        System.out.println("                                 Map open");
        System.out.println("--------------------------------------------------------------------");
    }


    @Override
    public boolean onMarkerClick(final Marker marker) {
        Set<ChannelInfoMarker> channelInfoMarkers = infoWindowMap.keySet();
        for (ChannelInfoMarker mark : channelInfoMarkers) {
            if (mark.getDefaultMarker().equals(marker)) {
                infoWindowManager.toggle(infoWindowMap.get(mark), true);
            }
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateEnabledState();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        enableMyLocation();
        databaseAdapter.open();
        makeMarkerFromDatabase();
        map.getUiSettings().setZoomControlsEnabled(false);
        map.getUiSettings().setMyLocationButtonEnabled(true);
        map.setOnMarkerClickListener(this);
        map.setOnMapClickListener(this);
    }

    private void makeMarkerFromDatabase() {
        List<Place> places = databaseAdapter.getPlaces();

        for (Place place : places) {
            LatLng position = new LatLng(place.getLatitude(), place.getLongitude());
            Marker marker = map.addMarker(
                    new MarkerOptions().position(position)
                            .snippet(String.valueOf(place.getInfo()))
                            .title(String.valueOf(place.getLabel())));

            ChannelInfoMarker infoMarker = new ChannelInfoMarker(marker,
                    place.getChannelId(),
                    place.getRoomName());

            InfoWindow infoWindowObj = new InfoWindow(
                    marker,
                    new InfoWindow.MarkerSpecification(5, 89),
                    new MarkerFormWindow(infoMarker, this));


            infoWindowMap.put(infoMarker, infoWindowObj);
        }

        LatLng msc = new LatLng(55.74, 37.62);
        float zoom = 10;
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(msc, zoom));
    }


    // кнопка остановки анимации
    public void onStopAnimation(View view) {
        if (checkNotReady()) {
            return;
        }
        map.stopAnimation();
    }

    // приближение карты
    public void onZoomIn(View view) {
        changeZoom(CameraUpdateFactory.zoomIn());
    }

    // отдаление карты
    public void onZoomOut(View view) {
        changeZoom(CameraUpdateFactory.zoomOut());
    }

    // увеличение наклона
    public void onTiltMore(View view) {
        changeTilt(true);
    }

    // умненьшение наклона
    public void onTiltLess(View view) {
        changeTilt(false);
    }

    // Стрелка влево
    public void onScrollLeft(View view) {
        changePos(-SCROLL_BY_PX, 0);
    }

    // Стрелка вправо
    public void onScrollRight(View view) {
        changePos(SCROLL_BY_PX, 0);
    }

    // Стрелка вверх
    public void onScrollUp(View view) {
        changePos(0, -SCROLL_BY_PX);
    }

    // Стрелка вниз
    public void onScrollDown(View view) {
        changePos(0, SCROLL_BY_PX);
    }

    private void changePos(int i, int scrollByPx) {
        changeZoom(CameraUpdateFactory.scrollBy(i, scrollByPx));
    }

    //Вызывается при переключении кнопки анимации.
    public void onToggleAnimate(View view) {
        updateEnabledState();
    }

    //Вызывается при переключении флажка пользовательского управления скоростью анимации.
    public void onToggleCustomDuration(View view) {
        updateEnabledState();
    }


    // Обработчик нажатия кнопки "Список локаций"
    public void markersClick(View view) {
        System.out.println("Кнопка *Список локаций* нажата");
        Intent intent = new Intent(MapPage.this, MarkersPage.class);
        startActivity(intent);
    }

    /**
     * Когда карта не готова, CameraUpdateFactory не может быть использован. Это должно быть вызвано во
     * всех точках входа, которые вызывают методы в API Google Maps.
     */
    private boolean checkNotReady() {
        if (map == null) {
            Toast.makeText(this, R.string.map_not_ready, Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

    private void changeZoom(CameraUpdate cameraUpdate) {
        if (checkNotReady()) {
            return;
        }
        changeCamera(cameraUpdate);
    }

    private void changeTilt(boolean more) {
        if (checkNotReady()) {
            return;
        }
        CameraPosition currentCameraPosition = map.getCameraPosition();
        float currentTilt = currentCameraPosition.tilt;
        float newTilt;
        if (more) {
            newTilt = currentTilt + 10;
            newTilt = (newTilt > 90) ? 90 : newTilt;
        } else {
            newTilt = currentTilt - 10;
            newTilt = (newTilt > 0) ? newTilt : 0;
        }

        CameraPosition cameraPosition = new CameraPosition.Builder(currentCameraPosition)
                .tilt(newTilt).build();
        changeCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }


    //Обновление активного состояния пользовательского управления скоростью анимации и анимации.
    private void updateEnabledState() {
        customDurationToggle.setEnabled(animateToggle.isChecked());
        customDurationBar
                .setEnabled(animateToggle.isChecked() && customDurationToggle.isChecked());
    }

    private void changeCamera(CameraUpdate update) {
        changeCamera(update, null);
    }

    // Меняет положение камеры с анимацией или нет в зависимости от нажатия кнопки анимации
    private void changeCamera(CameraUpdate update, CancelableCallback callback) {
        if (animateToggle.isChecked()) {
            if (customDurationToggle.isChecked()) {
                int duration = customDurationBar.getProgress();
                // duration строго положительная
                map.animateCamera(update, Math.max(duration, 1), callback);
            } else {
                map.animateCamera(update, callback);
            }
        } else {
            map.moveCamera(update);
        }
    }


    /*
     * Часть кода отвечающуая за определение собственной локации. Начало
     */
    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            if (map != null) {
                map.setMyLocationEnabled(true);
            }
        } else {
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }
        if (PermissionUtils.isPermissionGranted(permissions, grantResults, Manifest.permission.ACCESS_FINE_LOCATION)) {
            enableMyLocation();
        } else {
            permissionDenied = true;
        }
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (permissionDenied) {
            showMissingPermissionError();
            permissionDenied = false;
        }
    }

    private void showMissingPermissionError() {
        PermissionUtils.PermissionDeniedDialog
                .newInstance(true).show(getSupportFragmentManager(), "dialog");
    }


    @Override
    public void onMapClick(final LatLng point) {
    }
}
