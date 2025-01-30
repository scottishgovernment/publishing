<@hst.link var="link" hippobean=item/>

<li class="ds_search-result">
    <h3 class="ds_search-result__title">
        <a class="ds_search-result__link" href="${link}">${item.title}</a>
    </h3>
    <#if item.image??>
        <#if item.image.image?has_content>
    <div class="ds_search-result__has-media">
        <div class="ds_search-result__media-wrapper">
            <div class="ds_search-result__media  ds_aspect-box  ds_aspect-box--square">
                <a class="ds_search-result__media-link" aria-hidden="true" href="${link}" tabindex="-1">
                <#if item.image.image.mediumtwocolumnssquare??>
                    <img alt="${item.image.alt}" class="ds_aspect-box__inner" 
                        src="<@hst.link hippobean=item.image.image.mediumtwocolumnssquare />"
                        loading="lazy"
                        width="${item.image.image.mediumtwocolumnssquare.width?c}"
                        height="${item.image.image.mediumtwocolumnssquare.height?c}"
                        srcset="
                        <@hst.link hippobean=item.image.image.mediumtwocolumnssquare/> 96w,
                        <@hst.link hippobean=item.image.image.largetwocolumnssquare/> 128w,
                        <@hst.link hippobean=item.image.image.mediumtwocolumnsdoubledsquare/> 192w,
                        <@hst.link hippobean=item.image.image.largetwocolumnsdoubledsquare/> 256w"
                        sizes="(min-width: 992px) 128px, 96px"
                        >
                <#else>
                    <img loading="lazy" alt="${item.image.alt}" class="ds_aspect-box__inner" src="<@hst.link hippobean=item.image.image/>">
                </#if>
                </a>
            </div>
        </div>
        <div>
        </#if>
    </#if>
    <#if item.summary??>
    <p class="ds_search-result__summary">
    ${item.summary}
    </p>
    </#if>

    <#if item.publicationDate??>
    <dl class="ds_search-result__metadata  ds_metadata  ds_metadata--inline">
        <#if item.publicationDate??>
            <#assign dateFormat = "dd MMMM yyyy">
            <#assign displayDate = (item.displayDate.time)!(item.publicationDate.time)>
            <#if hst.isBeanType(item, "scot.gov.www.beans.News")>
                <#assign dateFormat = "dd MMMM yyyy HH:mm">
            </#if>
            <div class="ds_metadata__item">
                <dt class="ds_metadata__key">Date</dt>
                <dd class="ds_metadata__value"><@fmt.formatDate value=displayDate type="both" pattern=dateFormat /></dd>
            </div>
        </#if>
    </dl>
    </#if>


    <#if item.image??>
        <#if item.image.image?has_content>
        </div>
    </div>
        </#if>
    </#if>

</li>
