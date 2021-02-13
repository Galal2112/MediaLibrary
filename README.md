# Media Library
### erledigte Punkte

1. zip Archiv
2. IntelliJ-Projekt (kein Gradle, Maven o.ä.)
3. readme vorhanden
4. Java8 (language level)
5. kompilierbar
6. main-Methoden nur im default package
7. ausführbar
8. saubere Trennung zwischen Oberfläche (Client) und Geschäftslogik (Server)
9. Trennung zwischen Test- und Produktiv-Code
10. JUnit5 als Testframework
11. Mockito als Mock/Spy-framework
12. keine leeren Test
13. Client-Server-Verbindung über TCP oder UDP
14. je ein Stellvertreter-Test für Einfügen, Auflisten und Löschen pro Server
15. Implementierung von Client und Server für TCP und UDP
16. Unterstützung mehrerer Clients pro Server (TCP oder UDP)


* For simplicity I am using LinkedList to simulate database table.
* The CRUD operations for each entity type is implemented in a separate file (InteractiveVideoCRUD, LicensedAudioVideoCRUD and UploaderCRUD).
* The business logic is implemented inside MediaLibraryAdmin.
* Only two media types are supported (InteractiveVideo, LicensedAudioVideo)
* Access count is increased with each list request.

-----
#### References
* Dao pattern in java: https://www.baeldung.com/java-dao-pattern
* Wildcards: https://www.geeksforgeeks.org/wildcards-in-java/
* Mockito: https://www.youtube.com/watch?v=HsQ9OwKA79s
* https://www.geeksforgeeks.org/mvc-design-pattern/
* https://www.techyourchance.com/thread-safe-observer-design-pattern-in-java
* https://stackoverflow.com/questions/20412445/how-to-create-a-reorder-able-tableview-in-javafx
* https://stackoverflow.com/questions/37440987/javafx-drag-view-for-transparent-png-image
* https://stackoverflow.com/questions/11096353/javafx-re-sorting-a-column-in-a-tableview