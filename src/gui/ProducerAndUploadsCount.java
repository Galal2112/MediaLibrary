package gui;

public class ProducerAndUploadsCount {

    private String name;
    private int uploadsCount;

    public ProducerAndUploadsCount(String name, int uploadsCount) {
        this.name = name;
        this.uploadsCount = uploadsCount;
    }

    public String getName() {
        return name;
    }

    public int getUploadsCount() {
        return uploadsCount;
    }
}
