package scot.mygov.publishing.components.funnelback;


import scot.mygov.publishing.beans.Searchsettings;

public interface SearchService {

    SearchResponse performSearch(Search search, Searchsettings searchSettings);

}
