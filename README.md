# Media Library
### erledigte Punkte

1. zip Archiv
2. IntelliJ-Projekt (kein Gradle, Maven o.ä.)
3. readme vorhanden
4. Java8 (language level)
5. kompilierbar
6. main-Methoden nur im default package
7. ausführbar
8. Simulation 1
10. 10.Trennung zwischen Test- und Produktiv-Code
11. 11.JUnit5 als Testframework
12. 12.Mockito als Mock/Spy-framework
13. 13.keine Verwendung von Thread.sleep bzw. nur mit 0-Werten
14. 14.ändernde Aktionen der threads produzieren Ausgaben auf der Konsole
15. 15.Änderungen an der Geschäftslogik produzieren Ausgaben auf der Konsole
16. 16.Trennung zwischen Verwaltungs- und Simulationslogik
17. 17.Simulation 2
18. 18.mindestens je ein Test für alle in der Simulation verwendeten Methoden die auf die Geschäftslogik zugreifen
19. 19.Simulation 3
20. 20.alle Tests sind deterministisch



* For simplicity I am using LinkedList to simulate database table.
* The CRUD operations for each entity type is implemented in a separate file (InteractiveVideoCRUD, LicensedAudioVideoCRUD and UploaderCRUD).
* The business logic is implemented inside MediaLibraryAdmin.
* Only two media types are supported (InteractiveVideo, LicensedAudioVideo)
* Access count is increased with each list request.

-------
For video address generation I used the following:
```java
private String getAddress(Object o) {
   return o.getClass().getName() + '@' + Integer.toHexString(o.hashCode());
}
```
-------
Storage available is 10 TB, implement is inside model -> MediaStorage
```java
private final BigDecimal diskSize = BigDecimal.valueOf(1024.0 * 1024.0 * 10);


```

VideoSize = ((bitrate(Mb/s)/8) * length(seconds)) / 1024 * 1024 MB;
Storage 25 GB
-----
#### References
* Dao pattern in java: https://www.baeldung.com/java-dao-pattern
* Wildcards: https://www.geeksforgeeks.org/wildcards-in-java/
* Mockito: https://www.youtube.com/watch?v=HsQ9OwKA79s
* https://www.geeksforgeeks.org/mvc-design-pattern/
* https://www.techyourchance.com/thread-safe-observer-design-pattern-in-java