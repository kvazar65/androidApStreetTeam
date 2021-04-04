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
    private final int label;
    private final int latitude;
    private final int longitude;


    @NonNull
    @Override
    public String toString() {
        return String.format("Name: %s \n Lat %s : Long %s",this.label,this.latitude,this.longitude);
    }
}