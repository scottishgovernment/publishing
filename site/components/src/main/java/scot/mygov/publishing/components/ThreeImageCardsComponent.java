package scot.mygov.publishing.components;

import org.apache.commons.lang3.StringUtils;
import org.hippoecm.hst.content.beans.standard.HippoDocument;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.hippoecm.hst.core.parameters.ParametersInfo;
import org.onehippo.cms7.essentials.components.CommonComponent;
import org.onehippo.cms7.essentials.components.info.EssentialsCarouselComponentInfo;
import org.onehippo.cms7.essentials.components.paging.DefaultPagination;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.IntStream;

import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.partitioningBy;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@ParametersInfo(type = ThreeImageCardsComponentInfo.class)
public class ThreeImageCardsComponent extends CommonComponent {

    @Override
    public void doBeforeRender(final HstRequest request, final HstResponse response) {
        super.doBeforeRender(request, response);

        ThreeImageCardsComponentInfo paramInfo = getComponentParametersInfo(request);
        List<String> imageStrings = getImageStrings(paramInfo);
        List<HippoDocument> images = getImages(imageStrings);
        request.setModel(REQUEST_ATTR_PAGEABLE, new DefaultPagination<>(images));

        final Map<Boolean, List<Integer>> configuredItemNumbers = getConfiguredItemNumbers(imageStrings);
        request.setAttribute("configuredItems", configuredItemNumbers.get(true));
        request.setAttribute("freeItems", configuredItemNumbers.get(false));
    }

    private Map<Boolean, List<Integer>> getConfiguredItemNumbers(final List<String> imageIds) {
        return IntStream.rangeClosed(1, 1)
                .boxed()
                .collect(partitioningBy(i -> isNotBlank(imageIds.get(i - 1))));
    }

    public List<String> getImageStrings(final ThreeImageCardsComponentInfo info) {
        List<String> imagesStrings = new ArrayList<>();
        imagesStrings.add(info.getImage1());
        imagesStrings.add(info.getImage2());
        imagesStrings.add(info.getImage3());
        return imagesStrings;
    }

    List<HippoDocument> getImages(List<String> imageStrings) {
        return imageStrings.stream().map(this::getHippoDocument).filter(Objects::nonNull).collect(toList());
    }

    HippoDocument getHippoDocument(String id) {
        return getHippoBeanForPath(id, HippoDocument.class);
    }
}

