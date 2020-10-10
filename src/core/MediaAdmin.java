package core;

import mediaDB.MediaContent;
import mediaDB.Uploadable;

public interface MediaAdmin {
    <T extends MediaContent & Uploadable> void upload(T media);
}
