package scot.mygov.publishing.components.funnelback.postprocess;

import scot.gov.publishing.hippo.funnelback.model.FunnelbackSearchResponse;

/**
 * Interface for class capablke of post processing a funnelback response.
 */
public interface PostProcessor {

    void process(FunnelbackSearchResponse response);
}