package scot.mygov.publishing.components.funnelback.postprocess;

import scot.mygov.publishing.components.funnelback.model.FunnelbackSearchResponse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CompositePostProcessor implements PostProcessor {

    List<PostProcessor> processors = new ArrayList<>();

    private CompositePostProcessor(List<PostProcessor> processors) {
        this.processors = processors;
    }

    @Override
    public void process(FunnelbackSearchResponse response) {
        for (PostProcessor processor : processors) {
            processor.process(response);
        }
    }

    public static PostProcessor processor(PostProcessor ... processors) {
        return new CompositePostProcessor(Arrays.asList(processors));
    }
}