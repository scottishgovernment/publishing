package scot.mygov.publishing.components.funnelback.postprocess;

import scot.mygov.publishing.components.funnelback.model.FunnelbackSearchResponse;

/**
 * Interface for class capablke of post processing a funnelback response.
 */
public interface PostProcessor {

    void process(FunnelbackSearchResponse response);
}