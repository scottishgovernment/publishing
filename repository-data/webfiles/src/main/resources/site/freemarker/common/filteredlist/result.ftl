<@hst.link var="link" hippobean=item/>

<li class="ds_search-result">
    <h3 class="ds_search-result__title">
        <a class="ds_search-result__link" href="${link}">${item.title}</a>
    </h3>
    <#if item.summary??>
    <p class="ds_search-result__summary">
    ${item.summary}
    </p>
    </#if>

    <#if item.publicationDate?? || item.label?has_content>
    <dl class="ds_search-result__metadata  ds_metadata  ds_metadata--inline">
        <#if item.label?has_content>
            <div class="ds_metadata__item">
                <dt class="ds_metadata__key">Type</dt>
                <dd class="ds_metadata__value">${item.label?cap_first}</dd>
            </div>
        </#if>

        <#if item.publicationDate??>
            <#assign dateFormat = "dd MMMM yyyy">
            <#assign displayDate = (item.displayDate.time)!(item.publicationDate.time)>
            <#if hst.isBeanType(item, "scot.gov.www.beans.News")>
                <#assign dateFormat = "dd MMMM yyyy HH:mm">
            </#if>
            <div class="ds_metadata__item">
                <dt class="ds_metadata__key">Publication date</dt>
                <dd class="ds_metadata__value"><@fmt.formatDate value=displayDate type="both" pattern=dateFormat /></dd>
            </div>
        </#if>
    </dl>
    </#if>

</li>
