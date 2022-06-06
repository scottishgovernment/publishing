package scot.mygov.publishing.components.funnelback;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import scot.mygov.publishing.components.funnelback.model.Suggestion;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import java.util.Collection;
import java.util.List;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Service
@Component("scot.mygov.publishing.components.funnelback.Suggestions")
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SuggestionsResource {

    @Autowired
    private FunnelbackService funnelbackService;

    @Path("suggestions")
    @Produces(APPLICATION_JSON)
    @GET
    public Collection<Suggestion> getSuggestions(@QueryParam("partial_query") String partialQuery) {
        return funnelbackService.getSuggestions(partialQuery);
    }
}
