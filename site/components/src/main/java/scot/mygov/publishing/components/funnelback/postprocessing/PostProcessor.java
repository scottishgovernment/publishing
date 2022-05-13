package scot.mygov.publishing.components.funnelback.postprocessing;

import scot.mygov.publishing.components.funnelback.model.FunnelbackSearchResponse;

/**
 * Created by z418868 on 17/05/2022.
 */
public interface PostProcessor {

    void process(FunnelbackSearchResponse response);
}
