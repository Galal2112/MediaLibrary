package util;

import businessLogic.MediaAdmin;
import businessLogic.MediaLibraryAdmin;
import crud.CRUD;
import crud.MediaCRUD;
import crud.UploaderCRUD;
import mediaDB.MediaContent;
import observer.BusinessLogicObserverImpl;
import storage.MediaStorage;

public final class MediaAdminFactory {
    private MediaAdminFactory() {
    }

    public static MediaAdmin getMediaAdminInstance(MediaStorage mediaStorage) {
        // create media admin
        CRUD<MediaContent> mediaCRUD = new MediaCRUD();
        UploaderCRUD uploaderCRUD = new UploaderCRUD();
        MediaLibraryAdmin mediaAdmin = new MediaLibraryAdmin(mediaStorage, uploaderCRUD, mediaCRUD);
        mediaAdmin.registerObserver(new BusinessLogicObserverImpl());
        return mediaAdmin;
    }
}
