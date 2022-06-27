<#ftl output_format="HTML">
<#include "include/imports.ftl">
<@hst.webfile var="iconspath" path="/assets/images/icons/icons.stack.svg"/>
<#setting url_escaping_charset='utf-8'>
<#assign queryString = hstRequestContext.servletRequest.getQueryString() />
<#assign query = hstRequestContext.servletRequest.getParameter('q') />

<#if response??>
    <#if (response.resultPacket.resultsSummary.totalMatching)!?has_content &&
    response.resultPacket.resultsSummary.totalMatching == 0>
        <section id="search-results" class="ds_search-results">

            <h2 class="visually-hidden">Search results</h2>

            <div class="ds_no-search-results">
                <p><strong>There are no matching results.</strong></p>

                <p>Improve your search results by:</p>
                <ul>
                    <li>double-checking your spelling</li>
                    <li>using fewer keywords</li>
                    <li>searching for something less specific</li>
                </ul>
            </div>
        </section>
    </#if>

    <#if (response.resultPacket.qsups)!?size &gt; 0>
        <#list response.resultPacket.qsups as qsup>
            <nav>
                <p>Also showing results for <a href="?q=${qsup.query?url('UTF-8')}"> ${qsup.query}<#if qsup_has_next>, </#if></a><br />
                Show results only for <a href="?${queryString}&amp;qsup=off">${question.originalQuery}</a></p>
            </nav>
        </#list>
    </#if>

    <#if (response.resultPacket.resultsSummary.totalMatching)!?has_content &&
    response.resultPacket.resultsSummary.totalMatching &gt; 0>
        <p>
            <#if response.resultPacket.resultsSummary.fullyMatching <= response.resultPacket.resultsSummary.numRanks ||
                response.resultPacket.resultsSummary.currStart <= response.resultPacket.resultsSummary.numRanks >

                ${response.resultPacket.resultsSummary.totalMatching} results for <b>${question.originalQuery}</b>
                <#if (response.resultPacket.qsups)!?size &gt; 0>
                    <#list response.resultPacket.qsups as qsup>or <b>${qsup.query}</b></#list>
                </#if>
            <#else>
                Showing ${response.resultPacket.resultsSummary.currStart} to ${response.resultPacket.resultsSummary.currEnd}
                of ${response.resultPacket.resultsSummary.totalMatching} results for <b>${question.originalQuery}</b>
                <#if (response.resultPacket.qsups)!?size &gt; 0>
                    <#list response.resultPacket.qsups as qsup>or <b>${qsup.query}</b></#list>
                </#if>
            </#if>
        </p>
    </#if>

    <ol start="${response.resultPacket.resultsSummary.currStart}" id="search-results-list" class="ds_search-results__list" data-total="${response.resultPacket.resultsSummary.totalMatching}">
        <#if response.pagination.currentPageIndex = 1>
            <#list response.curator.exhibits as exhibit>
                <li class="ds_search-result">
                    <h3 class="ds_search-result__title">
                        CURATOR ${exhibit.category} <a class="ds_search-result__link" href="${exhibit.displayUrl}">${exhibit.titleHtml?no_esc}</a>
                    </h3>

                    <p class="ds_search-result__summary">
                        ${exhibit.descriptionHtml?no_esc}
                    </p>
                </li>
            </#list>
        </#if>
        <#list response.resultPacket.results as result>
            <li class="ds_search-result">
                <h3 class="ds_search-result__title">
                    <a class="ds_search-result__link" href="${result.liveUrl}">${result.listMetadata["t"]?first!}</a>
                </h3>

                <p class="ds_search-result__summary">
                    ${result.listMetadata["c"]?first}
                </p>

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
    </ol>

    <nav id="pagination" class="ds_pagination" aria-label="Search result pages">
        <ul class="ds_pagination__list">
            <#if response.pagination.previous??>
                <li class="ds_pagination__item">
                    <a class="ds_pagination__link  ds_pagination__link--text  ds_pagination__link--icon" href="${response.pagination.previous.url}">
                        <svg class="ds_icon" aria-hidden="true" role="img">
                            <use href="${iconspath}#chevron_left"></use>
                        </svg>
                        <span class="ds_pagination__link-label">${response.pagination.previous.label}</span>
                    </a>
                </li>
            </#if>

            <#if response.pagination.first??>
                <li class="ds_pagination__item">
                    <a class="ds_pagination__link" href="${response.pagination.first.url}">
                        <span class="ds_pagination__link-label">${response.pagination.first.label}</span>
                    </a>
                </li>
                <li class="ds_pagination__item" aria-hidden="true">
                    <span class="ds_pagination__link">&hellip;</span>
                </li>
            </#if>

            <#list response.pagination.pages as page>
                <li class="ds_pagination__item">
                    <#if page.selected>
                        <span class="ds_pagination__link  ds_current">${page.label}</span>
                    <#else>
                        <a class="ds_pagination__link" href="${page.url}">
                            <span class="ds_pagination__link-label">${page.label}</span>
                        </a>
                    </#if>
                </li>
            </#list>

            <#if response.pagination.last??>
                <li class="ds_pagination__item" aria-hidden="true">
                    <span class="ds_pagination__link">&hellip;</span>
                </li>
                <li class="ds_pagination__item">
                    <a class="ds_pagination__link" href="${response.pagination.last.url}">${response.pagination.last.label}</a>
                </li>
            </#if>

            <#if response.pagination.next??>
                <li class="ds_pagination__item">
                    <a class="ds_pagination__link  ds_pagination__link--text  ds_pagination__link--icon" href="${response.pagination.next.url}">
                        <span class="ds_pagination__link-label">${response.pagination.next.label}</span>
                        <svg class="ds_icon" aria-hidden="true" role="img">
                            <use href="${iconspath}#chevron_right"></use>
                        </svg>
                    </a>
                </li>
            </#if>
        </ul>
    </nav>

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
