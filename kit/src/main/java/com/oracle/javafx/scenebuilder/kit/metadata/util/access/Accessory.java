package com.oracle.javafx.scenebuilder.kit.metadata.util.access;

public class Accessory {

    private String name;
    private Class<?> clazz;
    private String propertyName;

    public Accessory(String name, Class<?> clazz, String propertyName) {
        this.name = name;
        this.clazz = clazz;
        this.propertyName = propertyName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public void setClazz(Class<?> clazz) {
        this.clazz = clazz;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    @Override
    public String toString() {
        return name;
    }
}
