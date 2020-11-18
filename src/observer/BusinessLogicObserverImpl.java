package observer;

import businessLogic.BusinessLogicObserver;
import mediaDB.MediaContent;

public class BusinessLogicObserverImpl implements BusinessLogicObserver {

    @Override
    public synchronized void didCreateUploader(String name) {
        System.out.println("Logger: Did create uploader with name: " + name);
    }

    @Override
    public synchronized void uploaderAlreadyRegistered(String name) {
        System.out.println("Logger: Failed to create uploader, \"\" already registered");
    }

    @Override
    public synchronized void didUpload(MediaContent media) {
        System.out.println("Logger: Did upload media of size " + media.getSize());
    }

    @Override
    public synchronized void didListProducersAndUploadsCount() {
        System.out.println("Logger: Did retrieve uploader and uploads count");
    }

    @Override
    public synchronized void didListMedia(int listSize) {
        System.out.println("Logger: did list media, list size: " + listSize);
    }

    @Override
    public synchronized void didListTags() {
        System.out.println("Logger: did list system tags");
    }

    @Override
    public synchronized void didDeleteUploaderWithName(String name) {
        System.out.println("Logger: did delete uploader with name: " + name);
    }

    @Override
    public synchronized void didDeleteMediaAtAddress(String address) {
        System.out.println("Logger: did delete media at address: " + address);
    }

    @Override
    public synchronized void requestedUploaderNotFount(String name) {
        System.out.println("Logger: did request a non existing uploader: " + name);
    }

    @Override
    public synchronized void didRetrieveUploader(String name) {
        System.out.println("Logger: did request uploader with name: " + name);
    }

    @Override
    public synchronized void didRetrieveMediaAtAddress(String address) {
        System.out.println("Logger: did request media at address: " + address);
    }

    @Override
    public synchronized void mediaNotFoundAtAddress(String address) {
        System.out.println("Logger: requested address: " + address + " not found");
    }
}
