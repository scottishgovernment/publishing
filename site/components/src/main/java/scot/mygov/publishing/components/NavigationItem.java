package scot.mygov.publishing.components;

import org.hippoecm.hst.core.linking.HstLink;

import java.util.ArrayList;
import java.util.List;

public class NavigationItem {

    String title;

    HstLink link;

    boolean currentItem;

    List<NavigationItem> children = new ArrayList<>();

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public HstLink getLink() {
        return link;
    }

    public void setLink(HstLink link) {
        this.link = link;
    }

    public boolean isCurrentItem() {
        return currentItem;
    }

    public void setCurrentItem(boolean currentItem) {
        this.currentItem = currentItem;
    }

    public List<NavigationItem> getChildren() {
        return children;
    }

    public void setChildren(List<NavigationItem> children) {
        this.children = children;
    }
}