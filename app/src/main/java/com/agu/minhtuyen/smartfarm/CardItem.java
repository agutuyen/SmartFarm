package com.agu.minhtuyen.smartfarm;


import java.util.HashMap;
import java.util.Map;

public class CardItem {

    private int age;
    private Map<String, Boolean> control = new HashMap<>();
    private Map<String, Float> sensor = new HashMap<>();
    private String state, title;

    public CardItem() {
    }

    public CardItem(int age, String state, String title, Map<String, Boolean> control, Map<String, Float> sensor) {
        this.age = age;
        this.state = state;
        this.title = title;
        this.control = control;
        this.sensor = sensor;

    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Map<String, Boolean> getControl() {
        return control;
    }

    public void setControl(Map<String, Boolean> control) {
        this.control = control;
    }

    public Map<String, Float> getSensor() {
        return sensor;
    }

    public void setSensor(Map<String, Float> sensor) {
        this.sensor = sensor;
    }
}
