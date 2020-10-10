package dao;

import mediaDB.LicensedAudioVideo;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class LicensedAudioVideoDao implements Dao<LicensedAudioVideo> {

    private static final LinkedList<LicensedAudioVideo> licensedAudioVideoList = new LinkedList<>();

    @Override
    public List<LicensedAudioVideo> getAll() {
        return licensedAudioVideoList;
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
        licensedAudioVideoList.removeIf(v -> v.getAddress() == licensedAudioVideo.getAddress());
    }
}
