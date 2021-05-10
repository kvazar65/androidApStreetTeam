package Map;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import Database.DatabaseAdapter;
import Map.locations.Place;
import Map.locations.PlacesManagement;
import ru.streetteam.app.R;

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
                    Intent intent = new Intent(getApplicationContext(), PlacesManagement.class);
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
//TODO некоректно отображает их сообщение
        List<Place> places = adapter.getPlaces();

        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, places);
        userList.setAdapter(arrayAdapter);
        adapter.close();
    }
    // Обработчик нажатия кнопки "назад"
    public void buttonClickBack(View view) {
        System.out.println("Кнопка *Назад* нажата");
        Intent intent = new Intent(MarkersPage.this, MapPage.class);
        startActivity(intent);

    }
    // по нажатию на кнопку запускаем UserActivity для добавления данных
    public void add(View view){
        Intent intent = new Intent(this, PlacesManagement.class);
        startActivity(intent);
    }
}