package Map;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import Database.DatabaseAdapter;
import com.example.scaledrone.app.R;

public class PlacesActivity extends AppCompatActivity {

    private EditText latBox;
    private EditText labelBox;
    private EditText lonBox;
    private Button delButton;

    private DatabaseAdapter adapter;
    private long userId=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.place_adder);

        labelBox = (EditText) findViewById(R.id.label);
        latBox = (EditText) findViewById(R.id.latitude);
        lonBox = (EditText) findViewById(R.id.longitude);
        delButton = (Button) findViewById(R.id.deleteButton);
        adapter = new DatabaseAdapter(this);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            userId = extras.getLong("id");
        }
        // если 0, то добавление
        if (userId > 0) {
            // получаем элемент по id из бд
            adapter.open();
            Place place = adapter.getUser(userId);
            labelBox.setText(place.getLabel());
            latBox.setText(String.valueOf(place.getLatitude()));
            lonBox.setText(String.valueOf(place.getLongitude()));
            adapter.close();
        } else {
            // скрываем кнопку удаления
            delButton.setVisibility(View.GONE);
        }
    }

    public void save(View view){
        String label = labelBox.getText().toString();
        float latitude = Float.parseFloat(latBox.getText().toString());
        float longitude = Float.parseFloat(lonBox.getText().toString());
        Place place = new Place(userId, label,latitude, longitude);

        adapter.open();
        if (userId > 0) {
            adapter.update(place);
        } else {
            adapter.insert(place);
        }
        adapter.close();
        goHome();
    }
    public void delete(View view){

        adapter.open();
        adapter.delete(userId);
        adapter.close();
        goHome();
    }
    private void goHome(){
        // переход к главной activity
        Intent intent = new Intent(this, MarkersPage.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }
}