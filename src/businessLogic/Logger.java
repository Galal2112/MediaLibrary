package businessLogic;

import mediaDB.MediaContent;

public interface Logger {

    void didCreateUploader(String name);

    void uploaderAlreadyRegistered(String name);

    void didUpload(MediaContent media);

    void didListProducersAndUploadsCount();

    void didListMedia(int listSize);

    void didListTags();

    void didDeleteUploaderWithName(String name);

    void didDeleteMediaAtAddress(String address);

    void requestedUploaderNotFount(String name);

    void didRetrieveUploader(String name);

    void didRetrieveMediaAtAddress(String address);

    void mediaNotFoundAtAddress(String address);
}
