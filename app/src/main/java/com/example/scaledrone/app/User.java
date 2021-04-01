package com.example.scaledrone.app;

import androidx.annotation.NonNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class User {

    private final long id;
    private final int name;
    private final int year;

    @NonNull
    @Override
    public String toString() {
        return String.format("Lat %s : Long %s",this.name,this.year);
    }
}