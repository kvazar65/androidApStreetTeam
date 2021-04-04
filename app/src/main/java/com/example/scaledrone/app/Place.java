package com.example.scaledrone.app;

import androidx.annotation.NonNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Place {

    private final long id;
    private final String label;
    private final float latitude;
    private final float longitude;


    @NonNull
    @Override
    public String toString() {
        return String.format("Name: %s \n Lat %s : Long %s",this.label,this.latitude,this.longitude);
    }
}