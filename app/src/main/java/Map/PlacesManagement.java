package Map;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import Database.DatabaseAdapter;
import ru.streetteam.app.R;

public class PlacesManagement extends AppCompatActivity {

    private EditText latBox;
    private EditText infoBox;
    private EditText labelBox;
    private EditText lonBox;
    private Button delButton;

    private DatabaseAdapter adapter;
    private long userId=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.place_adder);
        System.out.println("                                                                     ");
        System.out.println("--------------------------------------------------------------------");
        System.out.println("                                 Place Management open");
        System.out.println("--------------------------------------------------------------------");
        labelBox = (EditText) findViewById(R.id.label);
        infoBox = (EditText) findViewById(R.id.info);
        latBox = (EditText) findViewById(R.id.latitude);
        lonBox = (EditText) findViewById(R.id.longitude);
        delButton = (Button) findViewById(R.id.deleteButton);
        adapter = new DatabaseAdapter(this);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            userId = extras.getLong("id");
        }
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
        System.out.println("The *Save* button is pressed");
        String label = labelBox.getText().toString();
        String info = infoBox.getText().toString();
        float latitude = Float.parseFloat(latBox.getText().toString());
        float longitude = Float.parseFloat(lonBox.getText().toString());
        Place place = new Place(userId, label,info ,latitude, longitude);

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
        System.out.println("The *Delete* button is pressed");
        adapter.open();
        adapter.delete(userId);
        adapter.close();
        goHome();
    }
    private void goHome(){
        Intent intent = new Intent(this, MarkersPage.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }
}