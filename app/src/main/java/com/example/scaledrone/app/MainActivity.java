package com.example.scaledrone.app;



import android.content.Intent;
import android.os.Bundle;

import android.view.View;

import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.home_page);
        System.out.println("                                                                     ");
        System.out.println("--------------------------------------------------------------------");
        System.out.println("                                 Main page open");
        System.out.println("--------------------------------------------------------------------");
    }
    // Обработчик нажатия для перехода к активити профиля
    public void buttonClickProfile(View view) {
        System.out.println("The *Profile* button is pressed");
        Intent intent = new Intent(MainActivity.this, db.class);
        startActivity(intent);

    }
    // Обработчик нажатия для перехода к активити карты
    public void buttonClickMap(View view) {
        System.out.println("The *Map* button is pressed");
        Intent intent = new Intent(MainActivity.this, MapPage.class);
        startActivity(intent);

    }
    // Обработчик нажатия для перехода к активити чата
    public void buttonClickChat(View view) {
        System.out.println("The *Chat* button is pressed");
        Intent intent = new Intent(MainActivity.this, ChatActivity.class);
        startActivity(intent);

    }
}
