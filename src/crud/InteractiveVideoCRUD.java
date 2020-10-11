package crud;

import mediaDB.InteractiveVideo;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class InteractiveVideoCRUD implements CRUD<InteractiveVideo> {

    private static final LinkedList<InteractiveVideo> interactiveVideos = new LinkedList<>();

    @Override
    public List<InteractiveVideo> getAll() {
        return new LinkedList<>(interactiveVideos);
    }

    @Override
    public void create(InteractiveVideo interactiveVideo) {
        interactiveVideos.add(interactiveVideo);
    }

    @Override
    public void update(InteractiveVideo interactiveVideo) {
        Iterator<InteractiveVideo> it = interactiveVideos.iterator();
        int index = 0;
        while (it.hasNext()) {
            if (it.next().getAddress().equals(interactiveVideo.getAddress())) {
                interactiveVideos.set(index, interactiveVideo);
                break;
            }
            index ++;
        }
    }

    @Override
    public void delete(InteractiveVideo interactiveVideo) {
        interactiveVideos.removeIf(v -> v.getAddress().equals(interactiveVideo.getAddress()));
    }

    public void delete(String address) {
        interactiveVideos.removeIf(v -> v.getAddress().equals(address));
    }
}
