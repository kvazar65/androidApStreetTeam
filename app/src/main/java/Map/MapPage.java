package Map;


/**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import ru.streetteam.app.MainPage;
import ru.streetteam.app.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.CancelableCallback;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.List;

import Database.DatabaseAdapter;


// [START maps_camera_events]
public class MapPage extends AppCompatActivity implements
        GoogleMap.OnMarkerClickListener,
        GoogleMap.OnMapClickListener,

        OnMapReadyCallback {
    // [START_EXCLUDE silent]


    /**
     Величина, на которую прокручивается камера. Величина  в необработанных пикселях, а не в dp
     (density-independent pixels).
     */
    private static final int SCROLL_BY_PX = 100;



    private GoogleMap map;
    /**
     * Keeps track of the selected marker.
     */
    private Marker mSelectedMarker;
    // [START_EXCLUDE silent]
    private DatabaseAdapter databaseAdapter;
    private CompoundButton animateToggle;
    private CompoundButton customDurationToggle;
    private SeekBar customDurationBar;
    private PolylineOptions currPolylineOptions;
    private boolean isCanceled = false;
    // [END_EXCLUDE]

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_page);
        // [START_EXCLUDE silent]
        animateToggle = findViewById(R.id.animate);
        customDurationToggle = findViewById(R.id.duration_toggle);
        customDurationBar = findViewById(R.id.duration_bar);

        updateEnabledState();
        // [END_EXCLUDE]

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        databaseAdapter = new DatabaseAdapter(this);
        System.out.println("                                                                     ");
        System.out.println("--------------------------------------------------------------------");
        System.out.println("                                 Map open");
        System.out.println("--------------------------------------------------------------------");
    }
    @Override
    public void onMapClick(final LatLng point) {
        // скрывает информацию о текущем маркере при нажатии на карту
        mSelectedMarker = null;
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        if (marker.equals(mSelectedMarker)) {
            mSelectedMarker = null;
            return true;
        }

        mSelectedMarker = marker;
        return false;
    }
    @Override
    protected void onResume() {
        super.onResume();
        updateEnabledState();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
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
            map.addMarker(new MarkerOptions().position(position).snippet(String.valueOf(place.getInfo())).title(String.valueOf(place.getLabel())));
        }
        LatLng msc = new LatLng(55.74, 37.62);
        float zoom = 10;
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(msc,zoom));
    }


    /**
     Когда карта не готова, CameraUpdateFactory не может быть использован. Это должно быть вызвано во
     всех точках входа, которые вызывают методы в API Google Maps.
     */
    private boolean checkReady() {
        if (map == null) {
            Toast.makeText(this, R.string.map_not_ready, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }






    /**
     * Called when the stop button is clicked.
     */
    public void onStopAnimation(View view) {
        if (!checkReady()) {
            return;
        }

        map.stopAnimation();
    }

    /**
     * Called when the zoom in button (the one with the +) is clicked.
     */
    public void onZoomIn(View view) {
        if (!checkReady()) {
            return;
        }

        changeCamera(CameraUpdateFactory.zoomIn());
    }

    /**
     * Called when the zoom out button (the one with the -) is clicked.
     */
    public void onZoomOut(View view) {
        if (!checkReady()) {
            return;
        }

        changeCamera(CameraUpdateFactory.zoomOut());
    }

    /**
     * Called when the tilt more button (the one with the /) is clicked.
     */
    public void onTiltMore(View view) {
        if (!checkReady()) {
            return;
        }

        CameraPosition currentCameraPosition = map.getCameraPosition();
        float currentTilt = currentCameraPosition.tilt;
        float newTilt = currentTilt + 10;

        newTilt = (newTilt > 90) ? 90 : newTilt;

        CameraPosition cameraPosition = new CameraPosition.Builder(currentCameraPosition)
                .tilt(newTilt).build();

        changeCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    /**
     * Called when the tilt less button (the one with the \) is clicked.
     */
    public void onTiltLess(View view) {
        if (!checkReady()) {
            return;
        }

        CameraPosition currentCameraPosition = map.getCameraPosition();

        float currentTilt = currentCameraPosition.tilt;

        float newTilt = currentTilt - 10;
        newTilt = (newTilt > 0) ? newTilt : 0;

        CameraPosition cameraPosition = new CameraPosition.Builder(currentCameraPosition)
                .tilt(newTilt).build();

        changeCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    /**
    <
     */
    public void onScrollLeft(View view) {
        if (!checkReady()) {
            return;
        }

        changeCamera(CameraUpdateFactory.scrollBy(-SCROLL_BY_PX, 0));
    }

    /**
    >
     */
    public void onScrollRight(View view) {
        if (!checkReady()) {
            return;
        }

        changeCamera(CameraUpdateFactory.scrollBy(SCROLL_BY_PX, 0));
    }

    /**
     ^
     */
    public void onScrollUp(View view) {
        if (!checkReady()) {
            return;
        }

        changeCamera(CameraUpdateFactory.scrollBy(0, -SCROLL_BY_PX));
    }

    /**
     v
     */
    public void onScrollDown(View view) {
        if (!checkReady()) {
            return;
        }

        changeCamera(CameraUpdateFactory.scrollBy(0, SCROLL_BY_PX));
    }

    /**
     * Called when the animate button is toggled
     */
    public void onToggleAnimate(View view) {
        updateEnabledState();
    }

    /**
     * Called when the custom duration checkbox is toggled
     */
    public void onToggleCustomDuration(View view) {
        updateEnabledState();
    }

    /**
     * Update the enabled state of the custom duration controls.
     */
    private void updateEnabledState() {
        customDurationToggle.setEnabled(animateToggle.isChecked());
        customDurationBar
                .setEnabled(animateToggle.isChecked() && customDurationToggle.isChecked());
    }

    private void changeCamera(CameraUpdate update) {
        changeCamera(update, null);
    }

    /**
     * Change the camera position by moving or animating the camera depending on the state of the
     * animate toggle button.
     */
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
    // Обработчик нажатия кнопки "назад"
    public void buttonClickBack(View view) {
        System.out.println("The *Back* button is pressed");
        Intent intent = new Intent(MapPage.this, MainPage.class);
        startActivity(intent);

    }

    // Обработчик нажатия кнопки "Список локаций"
    public void markersClick(View view) {
        System.out.println("The *Places list* button is pressed");
        Intent intent = new Intent(MapPage.this, MarkersPage.class);
        startActivity(intent);
    }

}
