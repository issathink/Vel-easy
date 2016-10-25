package com.veleasy.veleasy;

/**
 * Created by issa on 25/10/2016.
 */

public class FavObject {
    private String name;
    private Integer number;
    private int nbPlaces;
    private int nbVelib;

    public FavObject(String name, Integer number) {
        this.name = name;
        this.number = number;
        this.nbPlaces = 0;
        this.nbVelib = 0;
    }

    public int getNbPlaces() {
        return nbPlaces;
    }

    public void setNbPlaces(int nbPlaces) {
        this.nbPlaces = nbPlaces;
    }

    public int getNbVelib() {
        return nbVelib;
    }

    public void setNbVelib(int nbVelib) {
        this.nbVelib = nbVelib;
    }

    public Integer getNumber() {
        return number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    @Override
    public String toString() {
        return "name: " + name + " number: " + number;
    }
}
