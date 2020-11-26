package util;

import businessLogic.MediaAdmin;
import businessLogic.MediaLibraryAdmin;
import crud.CRUD;
import crud.MediaCRUD;
import crud.UploaderCRUD;
import mediaDB.MediaContent;
import observer.BusinessLogicObserverImpl;

public final class MediaAdminFactory {
    private MediaAdminFactory() {
    }

    public static MediaAdmin getMediaAdminInstance() {
        // create media admin
        CRUD<MediaContent> mediaCRUD = new MediaCRUD();
        UploaderCRUD uploaderCRUD = new UploaderCRUD();
        MediaLibraryAdmin mediaAdmin = new MediaLibraryAdmin(uploaderCRUD, mediaCRUD);
        mediaAdmin.registerObserver(new BusinessLogicObserverImpl());
        return mediaAdmin;
    }
}