package scot.gov.migration;

class DocumentRequest {
    private String path;

    private String slug;

    private String url;

    private String filename;

    private String contentType;

    private long pageCount;

    private long createdDate;

    private String rewritesFolder;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public long getPageCount() {
        return pageCount;
    }

    public void setPageCount(long pageCount) {
        this.pageCount = pageCount;
    }

    public long getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(long createdDate) {
        this.createdDate = createdDate;
    }

    public String getRewritesFolder() {
        return rewritesFolder;
    }

    public void setRewritesFolder(String rewritesFolder) {
        this.rewritesFolder = rewritesFolder;
    }
}