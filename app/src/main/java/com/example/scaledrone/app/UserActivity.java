package com.example.scaledrone.app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class UserActivity extends AppCompatActivity {

    private EditText nameBox;
    private EditText yearBox;
    private Button delButton;

    private DatabaseAdapter adapter;
    private long userId=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.place_adder);

        nameBox = (EditText) findViewById(R.id.JOPA);
        yearBox = (EditText) findViewById(R.id.year);
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
            nameBox.setText(String.valueOf(place.getName()));
            yearBox.setText(String.valueOf(place.getYear()));
            adapter.close();
        } else {
            // скрываем кнопку удаления
            delButton.setVisibility(View.GONE);
        }
    }

    public void save(View view){

        int name = Integer.parseInt(nameBox.getText().toString());
        int year = Integer.parseInt(yearBox.getText().toString());
        Place place = new Place(userId, name, year);

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