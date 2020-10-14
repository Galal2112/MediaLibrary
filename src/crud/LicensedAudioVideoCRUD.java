package crud;

import mediaDB.LicensedAudioVideo;

import java.util.LinkedList;
import java.util.List;

public class LicensedAudioVideoCRUD extends MediaCRUD<LicensedAudioVideo> {

    private static final LinkedList<LicensedAudioVideo> licensedAudioVideoList = new LinkedList<>();

    @Override
    protected List<LicensedAudioVideo> getList() {
        return licensedAudioVideoList;
    }
}
