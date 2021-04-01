package com.example.scaledrone.app;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class ProfilePage extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_page);
        System.out.println("                                                                     ");
        System.out.println("--------------------------------------------------------------------");
        System.out.println("                                 Profile open");
        System.out.println("--------------------------------------------------------------------");
    }
    // Обработчик нажатия кнопки "назад"
    public void buttonClickBack(View view) {
        System.out.println("The *Back* button is pressed");
        Intent intent = new Intent(ProfilePage.this, MainActivity.class);
        startActivity(intent);

    }
}

