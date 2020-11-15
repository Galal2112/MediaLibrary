# Media Library
### erledigte Punkte

1. zipArchiv 
2. IntelliJ-Projekt (kein Gradle, Maven o.ä.) 
3. readmevorhanden 
4. Java8(languagelevel) 
5. kompilierbar 
6. main-Methoden nur im default package 
7. ausführbar 
8. DarstellungslogikundGeschäftslogikgetrennt 
9. prototypisches CLI (nicht notwendig, wenn umfangreicheres CLI realisiert ist) 
11. Trennung zwischen Test- und Produktiv-Code 
12. JUnit5 als Testframework 
13. Mockito als Mock/Spy-framework 
14. keine leeren Test 
15. Auflisten der Mediadateien im CLI realisiert 16.Beobachterentwurfsmuster und events realisiert
17. zwei Tests für Beobachter realisiert 
18. zwei listener getestet 19.angemessene Aufzählungstypen verwendet 20.nach MVC strukturiert



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