# Media Library
### Business Logic Implementation
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
Storage available is 512 GB, implement as a static attribute inside media admin
```java
public static final BigDecimal availableStorage = new BigDecimal(1024 * 1024 * 1024);
Add sample
```

VideoSize = ((bitrate(Mb/s)/8) * length(seconds)) / 1024 * 1024 MB;
Storage 25 GB
-----
#### References
* Dao pattern in java: https://www.baeldung.com/java-dao-pattern
* Wildcards: https://www.geeksforgeeks.org/wildcards-in-java/
* Mockito: https://www.youtube.com/watch?v=HsQ9OwKA79s
* https://www.geeksforgeeks.org/mvc-design-pattern/