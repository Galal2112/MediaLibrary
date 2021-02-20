package model;

import mediaDB.Uploader;

import java.util.Objects;

public class Producer implements Uploader {
    private String name;

    public Producer() {}

    public Producer(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Producer producer = (Producer) o;
        return name.equals(producer.name);
    }

    @Override
    public Uploader copy() {
        return new Producer(name);
    }
}
