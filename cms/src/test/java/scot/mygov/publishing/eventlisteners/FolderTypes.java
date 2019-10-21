package scot.mygov.publishing.eventlisteners;

/**
 * Created by z418868 on 08/10/2019.
 */
public enum FolderTypes {

    // new-publishing-article

    NEW_ARTICLE("new-publishing-article"),
    NEW_CATEGORY("new-publishing-category");

    private final String action;

    FolderTypes(String action) {
        this.action = action;
    }



}
