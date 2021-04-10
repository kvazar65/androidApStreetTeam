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
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.scaledrone.app.MainActivity;
import com.example.scaledrone.app.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.CancelableCallback;
import com.google.android.gms.maps.GoogleMap.OnCameraIdleListener;
import com.google.android.gms.maps.GoogleMap.OnCameraMoveCanceledListener;
import com.google.android.gms.maps.GoogleMap.OnCameraMoveListener;
import com.google.android.gms.maps.GoogleMap.OnCameraMoveStartedListener;
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
        // Any showing info window closes when the map is clicked.
        // Clear the currently selected marker.
        mSelectedMarker = null;
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        // The user has re-tapped on the marker which was already showing an info window.
        if (marker.equals(mSelectedMarker)) {
            // The showing info window has already been closed - that's the first thing to happen
            // when any marker is clicked.
            // Return true to indicate we have consumed the event and that we do not want the
            // the default behavior to occur (which is for the camera to move such that the
            // marker is centered and for the marker's info window to open, if it has one).
            mSelectedMarker = null;
            return true;
        }

        mSelectedMarker = marker;

        // Return false to indicate that we have not consumed the event and that we wish
        // for the default behavior to occur.
        return false;
    }
    // [START_EXCLUDE silent]
    @Override
    protected void onResume() {
        super.onResume();
        updateEnabledState();
    }
    // [END_EXCLUDE]

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        databaseAdapter.open();
        makeMarkerFromDatabase();
        map.getUiSettings().setZoomControlsEnabled(false);
        map.getUiSettings().setMyLocationButtonEnabled(true);
        // [END_EXCLUDE]

        // Set listener for marker click event.  See the bottom of this class for its behavior.
        map.setOnMarkerClickListener(this);

        // Set listener for map click event.  See the bottom of this class for its behavior.
        map.setOnMapClickListener(this);


    }
    private void makeMarkerFromDatabase() {
        List<Place> places = databaseAdapter.getUsers();
        for (Place place : places) {
            LatLng position = new LatLng(place.getLatitude(), place.getLongitude());
            map.addMarker(new MarkerOptions().position(position).title(String.valueOf(place.getLabel())));
        }
        LatLng msc = new LatLng(55.74, 37.62);
        float zoom = 10;
        //TODO
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(msc,zoom));
    }

    // [START_EXCLUDE silent]
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
                // The duration must be strictly positive so we make it at least 1.
                map.animateCamera(update, Math.max(duration, 1), callback);
            } else {
                map.animateCamera(update, callback);
            }
        } else {
            map.moveCamera(update);
        }
    }
    // [END_EXCLUDE]


    // [START_EXCLUDE silent]
    private void addCameraTargetToPath() {
        LatLng target = map.getCameraPosition().target;
        currPolylineOptions.add(target);
    }
    // Обработчик нажатия кнопки "назад"
    public void buttonClickBack(View view) {
        System.out.println("The *Back* button is pressed");
        Intent intent = new Intent(MapPage.this, MainActivity.class);
        startActivity(intent);

    }

    // Обработчик нажатия кнопки "назад"
    public void markersClick(View view) {
        System.out.println("The *Back* button is pressed");
        Intent intent = new Intent(MapPage.this, MarkersPage.class);
        startActivity(intent);
    }

}
    // [END_EXCLUDE]

// [END maps_camera_events]