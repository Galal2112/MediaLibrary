package mvc;

public enum Command {
    CREATE(":c", "Wechsel in den Einfügemodus"),
    VIEW(":r", "Wechsel in den Anzeigemodus"),
    DELETE(":d","Wechsel in den Löschmodus" ),
    UPDATE(":u", "Wechsel in den Änderungsmodus"),
    PRESISTENCE_MODE(":p","Wechsel in den Persistenzmodus"),
    CONFIG(":config","Wechsel in den Konfigurationsmodus");

    private String key;
    private String description;

    Command(String key, String description) {
        this.key = key;
        this.description = description;
    }

    @Override
    public String toString() {
        return key + ' ' + description;
    }

    public String getKey() {
        return key;
    }
}
