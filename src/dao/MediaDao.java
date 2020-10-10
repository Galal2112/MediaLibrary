package dao;

import mediaDB.MediaContent;

import java.util.ArrayList;
import java.util.List;

public class MediaDao implements Dao<MediaContent> {

    private static final ArrayList<MediaContent> mediaLibrary = new ArrayList<>();

    @Override
    public List<MediaContent> getAll() {
        return mediaLibrary;
    }

    @Override
    public void create(MediaContent mediaContent) {
        mediaLibrary.add(mediaContent);
    }

    @Override
    public void update(MediaContent mediaContent) {
        int index = mediaLibrary.indexOf(mediaContent);
        mediaLibrary.set(index, mediaContent);
    }

    @Override
    public void delete(MediaContent mediaContent) {
        mediaLibrary.remove(mediaContent);
    }
}
