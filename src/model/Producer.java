package model;

import mediaDB.Uploader;

import java.util.Objects;

public class Producer implements Uploader {
    private String name;
    private int uploadsCount;

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

    public int getUploadsCount() {
        return uploadsCount;
    }

    public void setUploadsCount(int uploadsCount) {
        this.uploadsCount = uploadsCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Producer producer = (Producer) o;
        return name.equals(producer.name);
    }
}
