<li class="ds_search-result <#if result.listMetadata["image"]!?has_content></#if>">
    <h3 class="ds_search-result__title">
        <a class="ds_search-result__link" href="${result.liveUrl}">
        <#if (result.listMetadata["dcTitle"]?first)?has_content>
            ${result.listMetadata["dcTitle"]?first!}
        <#else>
            ${result.listMetadata["t"]?first!}
        </#if>
        </a>
    </h3>

    <#if result.listMetadata["image"]!?has_content>
        <div class="ds_search-result__has-media">
            <div class="ds_search-result__media-wrapper">
                <div class="ds_search-result__media  ds_aspect-box  ds_aspect-box--square">
                    <a class="ds_search-result__media-link" href="${result.liveUrl}" tabindex="-1">

                        <#assign path = (result.listMetadata['image']?first)! />
                        <#assign norm = path?trim?replace("/+$", "", "r") />
                        <#assign lastElement = norm?split("/")?last />

                        <img alt=""
                                aria-hidden="true"
                                class="ds_aspect-box__inner"
                                width="96"
                                height="96"
                                loading="lazy"
                                srcset="$${norm}/${lastElement}/publishing%3Amediumtwocolumnssquare 96w,
                                        ${norm}/${lastElement}/publishing%3Alargetwocolumnssquare 128w,
                                        ${norm}/${lastElement}/publishing%3Amediumtwocolumnsdoubledsquare 192w,
                                       ${norm}/${lastElement}/publishing%3Alargetwocolumnsdoubledsquare 256w"
                                sizes="(min-width:480px) 128px, 96px"
                                src="${norm}/${lastElement}/publishing%3Amediumtwocolumnssquare">
                    </a>
                </div>
            </div>
        <div>
    </#if>


    <p class="ds_search-result__summary">
        <@highlightSearchTerm result.listMetadata["c"]?first />
    </p>

    <#if result.listMetadata["f"]?first = 'Publication'>
        <dl class="ds_search-result__metadata  ds_metadata  ds_metadata--inline">
            <#if result.listMetadata["publicationType"]!?has_content>
            <div class="ds_metadata__item">
                <dt class="ds_metadata__key">Publication type</dt>
                <dd class="ds_metadata__value">
                ${result.listMetadata["publicationType"]?first!}
                </dd>
            </div>
            </#if>
            <#if (result.listMetadata["d"]?first)!?has_content>
            <div class="ds_metadata__item">
                <dt class="ds_metadata__key">Date</dt>
                <dd class="ds_metadata__value">
                ${result.listMetadata.displayDate}
                </dd>
            </div>
            </#if>
        </dl>
    <#else>
        <dl class="ds_search-result__metadata  ds_metadata  ds_metadata--inline">
            <#if result.listMetadata["f"]!?has_content>
            <div class="ds_metadata__item">
                <dt class="ds_metadata__key">Format</dt>
                <dd class="ds_metadata__value">
                ${result.listMetadata["f"]?first!}
                </dd>
            </div>
            </#if>
            <#if (result.listMetadata["d"]?first)!?has_content>
            <div class="ds_metadata__item">
                <dt class="ds_metadata__key">Date</dt>
                <dd class="ds_metadata__value">
                ${result.listMetadata.displayDate}
                </dd>
            </div>
            </#if>
        </dl>
    </#if>

    <#if (result.listMetadata["titleSeriesLink"]?first)!?has_content>
        <dl class="ds_search-result__context">
            <dt class="ds_search-result__context-key">Part of:</dt>
            <dd class="ds_search-result__context-value">
                <a href="${(result.listMetadata["titleSeriesLink"]?first)!}">${(result.listMetadata["titleSeries"]?first)!}</a>
            </dd>
        </dl>
    </#if>
</li>