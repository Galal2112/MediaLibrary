package observer;

import businessLogic.MediaAdmin;
import mediaDB.Tag;

import java.util.HashSet;
import java.util.Set;

public class TagsObserver implements Observer {

    private MediaAdmin mediaAdmin;
    private Set<Tag> usedTags;

    public TagsObserver(MediaAdmin mediaAdmin) {
        this.mediaAdmin = mediaAdmin;
        this.usedTags = new HashSet<>(mediaAdmin.getUsedTags());
    }

    @Override
    public void updateObserver() {
        Set<Tag> currentUsedTags = new HashSet<>(mediaAdmin.getUsedTags());
        if (!currentUsedTags.equals(usedTags)) {
            usedTags = currentUsedTags;
            System.out.println("Used tags updated: " + usedTags);
        }
    }
}
