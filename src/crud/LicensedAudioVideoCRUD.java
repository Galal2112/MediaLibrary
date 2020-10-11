package crud;

import mediaDB.LicensedAudioVideo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class LicensedAudioVideoCRUD implements CRUD<LicensedAudioVideo> {

    private static final LinkedList<LicensedAudioVideo> licensedAudioVideoList = new LinkedList<>();

    @Override
    public List<LicensedAudioVideo> getAll() {
        return new ArrayList<>(licensedAudioVideoList);
    }

    @Override
    public void create(LicensedAudioVideo licensedAudioVideo) {
        licensedAudioVideoList.add(licensedAudioVideo);
    }

    @Override
    public void update(LicensedAudioVideo licensedAudioVideo) {
        Iterator<LicensedAudioVideo> it = licensedAudioVideoList.iterator();
        int index = 0;
        while (it.hasNext()) {
            if (it.next().getAddress().equals(licensedAudioVideo.getAddress())) {
                licensedAudioVideoList.set(index, licensedAudioVideo);
                break;
            }
            index ++;
        }
    }

    @Override
    public void delete(LicensedAudioVideo licensedAudioVideo) {
        delete(licensedAudioVideo.getAddress());
    }

    public void delete(String address) {
        licensedAudioVideoList.removeIf(v -> v.getAddress().equals(address));
    }
}
