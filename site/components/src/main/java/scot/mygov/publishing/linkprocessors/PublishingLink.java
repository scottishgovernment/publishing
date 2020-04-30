package scot.mygov.publishing.linkprocessors;

import org.apache.commons.lang3.StringUtils;
import org.hippoecm.hst.configuration.hosting.Mount;
import org.hippoecm.hst.configuration.sitemap.HstSiteMapItem;
import org.hippoecm.hst.core.linking.HstLink;
import org.hippoecm.hst.core.request.HstRequestContext;

/**
 * Wrap HstLink to provide trailing slash behaviour.
 */
class PublishingLink implements HstLink {

    private HstLink link;

    public PublishingLink(HstLink link){
        this.link = link;
    }

    @Override
    public String getPath() {
        String path = link.getPath();
        return path.endsWith("/") ? StringUtils.removeEnd(path, "/") : path;
    }

    @Override
    public void setPath(String path) {
        link.setPath(path);
    }

    @Override
    public String getSubPath() {
        return link.getSubPath();
    }

    @Override
    public void setSubPath(String subpath) {
        link.setSubPath(subpath);
    }

    @Override
    public boolean isContainerResource() {
        return link.isContainerResource();
    }

    @Override
    public void setContainerResource(boolean b) {
        link.setContainerResource(b);
    }

    @Override
    public String toUrlForm(HstRequestContext requestContext, boolean fullyQualified) {
        String linkStr =  link.toUrlForm(requestContext, fullyQualified);
        if (linkStr == null) {
            return null;
        }
        return linkStr.endsWith("/") ? StringUtils.removeEnd(linkStr, "/") : linkStr;
    }

    @Override
    public String[] getPathElements() {
        return link.getPathElements();
    }

    @Override
    public Mount getMount() {
        return link.getMount();
    }

    @Override
    public HstSiteMapItem getHstSiteMapItem() {
        return null;
    }

    @Override
    public boolean isNotFound() {
        return link.isNotFound();
    }

    @Override
    public void setNotFound(boolean b) {
        link.setNotFound(b);
    }

}
