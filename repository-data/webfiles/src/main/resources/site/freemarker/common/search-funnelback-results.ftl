<#ftl output_format="HTML">
<#include "include/imports.ftl">
<@hst.webfile var="iconspath" path="/assets/images/icons/icons.stack.svg"/>
<#setting url_escaping_charset='utf-8'>
<#macro highlightSearchTerm text>
    <#if response.resultPacket.queryHighlightRegex??>
        <#assign pattern = "(?i)(" + response.resultPacket.queryHighlightRegex?replace("(?i)","") + ")" />
        ${text?replace(pattern, "<mark>$1</mark>", 'ri')?no_esc!}
    <#else>
        ${text}
    </#if>
</#macro>

<@hst.headContribution category="googleTagManagerDataLayer">
<script id="gtm-datalayer-search">
    window.dataLayer = window.dataLayer || [];
    if (window.dataLayer == 0) {
        window.dataLayer.push({});
    }
    window.dataLayer[0].searchEnabled = '${enabled?c}';
    window.dataLayer[0].searchType = '${searchType}';
</script>
</@hst.headContribution>
<#if enabled>
    <#if response??>
        <#if (response.resultPacket.resultsSummary.totalMatching)!?has_content &&
        response.resultPacket.resultsSummary.totalMatching == 0 &&
        !(response.curator.simpleHtmlExhibits)?has_content &&
        !(response.curator.advertExhibits)?has_content>

            <h2 class="visually-hidden">Search</h2>

            <div id="no-search-results" class="ds_no-search-results">
                <@hst.html hippohtml=document.noResultsMessage/>
            </div>
        </#if>

        <#if (response.resultPacket.resultsSummary.totalMatching)!?has_content &&
        response.resultPacket.resultsSummary.totalMatching == -1 &&
        !(response.curator.simpleHtmlExhibits)?has_content &&
        !(response.curator.advertExhibits)?has_content>
        <h2 class="visually-hidden">Search</h2>
        <div id="no-search-results" class="ds_no-search-results">
            <@hst.html hippohtml=document.blankSearchQueryMessage/>
        </div>
        </#if>

        <#if pagination??>
            <#if pagination.currentPageIndex = 1>
                <#list response.curator.simpleHtmlExhibits as exhibit>
                    <div class="ds_inset-text">
                        <div class="ds_inset-text__text">
                            ${exhibit.messageHtml?no_esc}
                        </div>
                    </div>
                </#list>
            </#if>
        </#if>

        <#if (response.resultPacket.qsups)!?size &gt; 0>
            <#list response.resultPacket.qsups as qsup>
            <nav class="ds_search-suggestions" aria-label="Alternative search suggestions">
                <h2 class="visually-hidden">Also showing results for ${qsup.query}</h2>
                <p><span aria-hidden="true">Also showing results for</span> <a aria-label="Show results only for ${qsup.query}" href="?q=${qsup.query?url('UTF-8')}">${qsup.query}<#if qsup_has_next>, </#if></a><br />
                <span aria-hidden="true">Show results only for</span> <a aria-label="Show results only for ${question.originalQuery}" href="?${queryString}&amp;qsup=off">${question.originalQuery}</a></p>
            </nav>
            </#list>
        </#if>

        <#if (response.resultPacket.resultsSummary.totalMatching)!?has_content &&
        response.resultPacket.resultsSummary.totalMatching &gt; 0>
            <h2 class="ds_search-results__title" aria-live="polite">
                <#if response.resultPacket.resultsSummary.totalMatching <= response.resultPacket.resultsSummary.numRanks ||
                response.resultPacket.resultsSummary.currStart <= response.resultPacket.resultsSummary.numRanks >

                ${response.resultPacket.resultsSummary.totalMatching} results for <span class="ds_search-results__title-query">${question.originalQuery}</span>
                    <#if (response.resultPacket.qsups)!?size &gt; 0>
                        <#list response.resultPacket.qsups as qsup>or <span class="ds_search-results__title-query">${qsup.query}</span></#list>
                    </#if>
                <#else>
                    Showing ${response.resultPacket.resultsSummary.currStart} to ${response.resultPacket.resultsSummary.currEnd}
                    of ${response.resultPacket.resultsSummary.totalMatching} results for <span class="ds_search-results__title-query">${question.originalQuery}</span>
                    <#if (response.resultPacket.qsups)!?size &gt; 0>
                        <#list response.resultPacket.qsups as qsup>or <span class="ds_search-results__title-query">${qsup.query}</span></#list>
                    </#if>
                </#if>
            </h2>
        </#if>

        <#if pagination??>
            <ol start="${response.resultPacket.resultsSummary.currStart?c}" id="search-results-list" class="ds_search-results__list" data-total="${response.resultPacket.resultsSummary.totalMatching?c}">
            <#if pagination.currentPageIndex = 1>
                <#list response.curator.advertExhibits as exhibit>
                <li class="ds_search-result  ds_search-result--promoted">
                    <div class="ds_search-result--promoted-content">
                        <header class="ds_search-result--promoted-title">Recommended</header>
                        <h3 class="ds_search-result__title">
                            <a class="ds_search-result__link" href="${exhibit.linkUrl}">${exhibit.titleHtml?no_esc}</a>
                        </h3>

                        <p class="ds_search-result__summary">
                            <@highlightSearchTerm exhibit.descriptionHtml />
                        </p>
                    </div>
                </li>
                </#list>
            </#if>

            <#if bloomreachresults??>
                <#list bloomreachresults as result>
                <li class="ds_search-result">
                    <h3 class="ds_search-result__title">
                        <@hst.link var="link" hippobean=result/>
                        <a class="ds_search-result__link"href="${link}">${result.title}</a>
                    </h3>
                    <p class="ds_search-result__summary">
                        <#if result.summary?has_content>
                            <@highlightSearchTerm result.summary />
                        <#else>
                            <@highlightSearchTerm result.metaDescription />
                        </#if>
                    </p>
                </li>
                </#list>
            <#else>
                <#list response.resultPacket.results as result>
                <li class="ds_search-result">
                    <h3 class="ds_search-result__title">
                        <a class="ds_search-result__link" href="${result.liveUrl}">
                        <#if (result.listMetadata["dcTitle"]?first)?has_content>
                            ${result.listMetadata["dcTitle"]?first!}
                        <#else>
                            ${result.listMetadata["t"]?first!}
                        </#if>
                        </a>
                    </h3>

                    <p class="ds_search-result__summary">
                        <@highlightSearchTerm result.listMetadata["c"]?first />
                    </p>

                    <#if (result.listMetadata["f"]?first)!?has_content
                    || (result.listMetadata["d"]?first)!?has_content>
                    <dl class="ds_search-result__metadata  ds_metadata  ds_metadata--inline">
                        <#if (result.listMetadata["f"]?first)!?has_content>
                            <span class="ds_metadata__item">
                                <dt class="ds_metadata__key  visually-hidden">Format</dt>
                                <dd class="ds_metadata__value">
                                ${result.listMetadata["f"]?first!}
                                </dd>
                            </span>
                        </#if>
                        <#if (result.listMetadata["d"]?first)!?has_content>
                            <span class="ds_metadata__item">
                                <dt class="ds_metadata__key  visually-hidden">Date</dt>
                                <dd class="ds_metadata__value">
                                ${result.listMetadata.displayDate}
                                </dd>
                            </span>
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
                </#list>
            </#if>
            </ol>

            <#if pagination.pages?has_content>
            <nav id="pagination" class="ds_pagination" aria-label="Search result pages">
                <ul class="ds_pagination__list">
                <#if pagination.previous??>
                    <li class="ds_pagination__item">
                        <a aria-label="Previous page" class="ds_pagination__link  ds_pagination__link--text  ds_pagination__link--icon" href="${pagination.previous.url}">
                            <svg class="ds_icon" aria-hidden="true" role="img">
                                <use href="${iconspath}#chevron_left"></use>
                            </svg>
                            <span class="ds_pagination__link-label">${pagination.previous.label}</span>
                        </a>
                    </li>
                </#if>

                <#if pagination.first??>
                    <li class="ds_pagination__item">
                        <a aria-label="Page ${pagination.first.label}" class="ds_pagination__link" href="${pagination.first.url}">
                            <span class="ds_pagination__link-label">${pagination.first.label}</span>
                        </a>
                    </li>
                    <li class="ds_pagination__item" aria-hidden="true">
                        <span class="ds_pagination__link  ds_pagination__link--ellipsis">&hellip;</span>
                    </li>
                </#if>

                <#list pagination.pages as page>
                    <li class="ds_pagination__item">
                        <#if page.selected>
                            <a aria-label="Page ${page.label}" aria-current="page" class="ds_pagination__link  ds_current" href="${page.url}">
                                <span class="ds_pagination__link-label">${page.label}</span>
                            </a>
                        <#else>
                            <a aria-label="Page ${page.label}" class="ds_pagination__link" href="${page.url}">
                                <span class="ds_pagination__link-label">${page.label}</span>
                            </a>
                        </#if>
                    </li>
                </#list>

                <#if pagination.last??>
                    <li class="ds_pagination__item" aria-hidden="true">
                        <span class="ds_pagination__link  ds_pagination__link--ellipsis">&hellip;</span>
                    </li>
                    <li class="ds_pagination__item">
                        <a aria-label="Last page, page ${pagination.last.label}" class="ds_pagination__link" href="${pagination.last.url}">${pagination.last.label}</a>
                    </li>
                </#if>

                <#if pagination.next??>
                    <li class="ds_pagination__item">
                        <a aria-label="Next page" class="ds_pagination__link  ds_pagination__link--text  ds_pagination__link--icon" href="${pagination.next.url}">
                            <span class="ds_pagination__link-label">${pagination.next.label}</span>
                            <svg class="ds_icon" aria-hidden="true" role="img">
                                <use href="${iconspath}#chevron_right"></use>
                            </svg>
                        </a>
                    </li>
                </#if>
                </ul>
            </nav>
            </#if>
    
        </#if>

        <#if (response.resultPacket.contextualNavigation.categories)!?size &gt; 0>
            <aside class="ds_search-results__related" aria-labelledby="search-results__related-title">
                <h2 class="ds_search-results__related-title" id="search-results__related-title">Related searches</h2>
                <ul class="ds_no-bullets">
                    <#list response.resultPacket.contextualNavigation.categories as category>
                        <#list category.clusters as cluster>
                            <li>
                                <a href="?q=%60${cluster.query?url}%60">${cluster.query}</a>
                            </li>
                        </#list>
                    </#list>
                </ul>
            </aside>
        </#if>
    
    </#if>

<#else>
    <p>This page has been temporarily disabled.</p>
</#if>
