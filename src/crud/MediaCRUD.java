package crud;

import mediaDB.MediaContent;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class MediaCRUD implements CRUD<MediaContent> {

    private static final LinkedList<MediaContent> mediaList = new LinkedList<>();

    @Override
    public List<MediaContent> getAll() {
        return new LinkedList<>(mediaList);
    }

    @Override
    public void create(MediaContent media) {
        mediaList.add(media);
    }

    @Override
    public void update(MediaContent media) {
        Iterator<MediaContent> it = mediaList.iterator();
        int index = 0;
        while (it.hasNext()) {
            if (it.next().getAddress().equals(media.getAddress())) {
                mediaList.set(index, media);
                break;
            }
            index ++;
        }
    }

    @Override
    public Optional<MediaContent> get(String address) {
        return mediaList.stream().filter(v -> v.getAddress().equals(address)).findFirst();
    }

    @Override
    public void delete(MediaContent media) {
        mediaList.removeIf( v -> v.getAddress().equals(media.getAddress()));
    }

    @Override
    public void deleteById(String address) {
        mediaList.removeIf(v -> v.getAddress().equals(address));
    }
}
