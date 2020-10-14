package crud;

import mediaDB.InteractiveVideo;

import java.util.LinkedList;
import java.util.List;

public class InteractiveVideoCRUD extends MediaCRUD<InteractiveVideo> {

    private static final LinkedList<InteractiveVideo> interactiveVideos = new LinkedList<>();

    @Override
    protected List<InteractiveVideo> getList() {
        return interactiveVideos;
    }
}
