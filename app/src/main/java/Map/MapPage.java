package Map;

/*
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import Database.DatabaseAdapter;
import com.example.scaledrone.app.MainActivity;
import com.example.scaledrone.app.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

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

    */
/**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     *//*

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        databaseAdapter.open();
        makeMarkerFromDatabase();
    }

    private void makeMarkerFromDatabase() {
        List<Place> places = databaseAdapter.getUsers();
        for (Place place : places) {
            LatLng position = new LatLng(place.getLatitude(), place.getLongitude());
            mMap.addMarker(new MarkerOptions().position(position).title(String.valueOf(place.getLabel())));
        }
        LatLng msc = new LatLng(55.74, 37.62);
        float zoom = 10;
        //TODO
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(msc,zoom));
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
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.List;

import Database.DatabaseAdapter;

/**
 * This shows how to change the camera position for the map.
 */
// [START maps_camera_events]
public class MapPage extends AppCompatActivity implements
        OnCameraMoveStartedListener,
        OnCameraMoveListener,
        OnCameraMoveCanceledListener,
        OnCameraIdleListener,
        OnMapReadyCallback {
    // [START_EXCLUDE silent]


    /**
     * The amount by which to scroll the camera. Note that this amount is in raw pixels, not dp
     * (density-independent pixels).
     */
    private static final int SCROLL_BY_PX = 100;



    private GoogleMap map;
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
        map.setOnCameraIdleListener(this);
        map.setOnCameraMoveStartedListener(this);
        map.setOnCameraMoveListener(this);
        map.setOnCameraMoveCanceledListener(this);
        // [START_EXCLUDE silent]
        // We will provide our own zoom controls.
        map.getUiSettings().setZoomControlsEnabled(false);
        map.getUiSettings().setMyLocationButtonEnabled(true);
        // [END_EXCLUDE]

        // Show Sydney

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
     * When the map is not ready the CameraUpdateFactory cannot be used. This should be called on
     * all entry points that call methods on the Google Maps API.
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
     * Called when the left arrow button is clicked. This causes the camera to move to the left
     */
    public void onScrollLeft(View view) {
        if (!checkReady()) {
            return;
        }

        changeCamera(CameraUpdateFactory.scrollBy(-SCROLL_BY_PX, 0));
    }

    /**
     * Called when the right arrow button is clicked. This causes the camera to move to the right.
     */
    public void onScrollRight(View view) {
        if (!checkReady()) {
            return;
        }

        changeCamera(CameraUpdateFactory.scrollBy(SCROLL_BY_PX, 0));
    }

    /**
     * Called when the up arrow button is clicked. The causes the camera to move up.
     */
    public void onScrollUp(View view) {
        if (!checkReady()) {
            return;
        }

        changeCamera(CameraUpdateFactory.scrollBy(0, -SCROLL_BY_PX));
    }

    /**
     * Called when the down arrow button is clicked. This causes the camera to move down.
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

    @Override
    public void onCameraMoveStarted(int reason) {
        // [START_EXCLUDE silent]
        if (!isCanceled) {
            map.clear();
        }
        // [END_EXCLUDE]

        String reasonText = "UNKNOWN_REASON";
        // [START_EXCLUDE silent]
        currPolylineOptions = new PolylineOptions().width(5);
        // [END_EXCLUDE]
        switch (reason) {
            case OnCameraMoveStartedListener.REASON_GESTURE:
                // [START_EXCLUDE silent]
                currPolylineOptions.color(Color.BLUE);
                // [END_EXCLUDE]
                reasonText = "GESTURE";
                break;
            case OnCameraMoveStartedListener.REASON_API_ANIMATION:
                // [START_EXCLUDE silent]
                currPolylineOptions.color(Color.RED);
                // [END_EXCLUDE]
                reasonText = "API_ANIMATION";
                break;
            case OnCameraMoveStartedListener.REASON_DEVELOPER_ANIMATION:
                // [START_EXCLUDE silent]
                currPolylineOptions.color(Color.GREEN);
                // [END_EXCLUDE]
                reasonText = "DEVELOPER_ANIMATION";
                break;
        }

        // [START_EXCLUDE silent]
        addCameraTargetToPath();
        // [END_EXCLUDE]
    }

    @Override
    public void onCameraMove() {
        // [START_EXCLUDE silent]
        // When the camera is moving, add its target to the current path we'll draw on the map.
        if (currPolylineOptions != null) {
            addCameraTargetToPath();
        }
        // [END_EXCLUDE]

    }

    @Override
    public void onCameraMoveCanceled() {
        // [START_EXCLUDE silent]
        // When the camera stops moving, add its target to the current path, and draw it on the map.
        if (currPolylineOptions != null) {
            addCameraTargetToPath();
            map.addPolyline(currPolylineOptions);
        }
        isCanceled = true;  // Set to clear the map when dragging starts again.
        currPolylineOptions = null;
        // [END_EXCLUDE]

    }

    @Override
    public void onCameraIdle() {
        // [START_EXCLUDE silent]
        if (currPolylineOptions != null) {
            addCameraTargetToPath();
            map.addPolyline(currPolylineOptions);
        }
        currPolylineOptions = null;
        isCanceled = false;  // Set to *not* clear the map when dragging starts again.
        // [END_EXCLUDE]

    }

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