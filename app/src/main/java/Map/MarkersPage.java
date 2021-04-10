package Map;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import Database.DatabaseAdapter;
import com.example.scaledrone.app.R;

import java.util.List;

public class MarkersPage extends AppCompatActivity {

    private ListView userList;
    ArrayAdapter<Place> arrayAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_dbpage);

        userList = (ListView)findViewById(R.id.list);

        userList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Place place =arrayAdapter.getItem(position);
                if(place !=null) {
                    Intent intent = new Intent(getApplicationContext(), PlacesActivity.class);
                    intent.putExtra("id", place.getId());
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        DatabaseAdapter adapter = new DatabaseAdapter(this);
        adapter.open();

        List<Place> places = adapter.getPlaces();

        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, places);
        userList.setAdapter(arrayAdapter);
        adapter.close();
    }
    // по нажатию на кнопку запускаем UserActivity для добавления данных
    public void add(View view){
        Intent intent = new Intent(this, PlacesActivity.class);
        startActivity(intent);
    }
}