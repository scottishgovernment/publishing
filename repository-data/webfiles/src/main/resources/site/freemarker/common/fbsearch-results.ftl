<#ftl output_format="HTML">
<#include "include/imports.ftl">
<@hst.webfile var="iconspath" path="/assets/images/icons/icons.stack.svg"/>

<div>
    <#if implementation == 'funnelback'>🟢<#else>🔴</#if> <span class="ds_content-label">${implementation}</span>
</div>

<#assign pattern = "(?i)(" + response.resultPacket.queryHighlightRegex?replace("(?i)","") + ")" />

<#assign page = 1 />
<#assign resultsPerPage = 10 />

<#if hstRequestContext.servletRequest.getParameter("page")??>
    <#assign page = hstRequestContext.servletRequest.getParameter("page") />
</#if>

<#assign start = page?number * resultsPerPage - (resultsPerPage - 1) />

<#assign totalResults = response.resultPacket.resultsSummary.totalMatching />
<#assign perPage = response.resultPacket.resultsSummary.numRanks />

<#if (response.resultPacket.qsups)!?size &gt; 0>
    <#list response.resultPacket.qsups as qsup>
    <nav class="ds_search-suggestions" aria-label="Alternative search suggestions">
        <p>Also showing results for <a href="?${removeParam(QueryString, "query")}&query=${qsup.query}"> ${qsup.query}<#if qsup_has_next>, </#if></a><br />
            Show results only for <a href="?${QueryString}&amp;qsup=off">${question.originalQuery}</a></p>
    </nav>
    </#list>
</#if>

<h2 class="ds_search-results__title"><span class="ds_search-results__title-count">${totalResults}</span> results for <span class="ds_search-results__title-query">${question.originalQuery}</span></h2>

<#if (response.resultPacket.qsups)!?size &gt; 0>
    <#list response.resultPacket.qsups as qsup>
    <nav class="ds_search-suggestions" aria-label="Alternative search suggestions">
        <p>Also showing results for <a href="?${removeParam(QueryString, "query")}&query=${qsup.query}"> ${qsup.query}<#if qsup_has_next>, </#if></a><br />
            Show results only for <a href="?${QueryString}&amp;qsup=off">${question.originalQuery}</a></p>
    </nav>
    </#list>
</#if>

<ol start="${start}" id="search-results-list" class="ds_search-results__list" data-total="${totalResults}">
    <#list response.resultPacket.results as result>

        <li class="ds_search-result">
            <#if result.bean??>
                <h3 class="ds_search-result__title">
                    <@hst.link var="link" hippobean=result.bean/>
                    <a class="ds_search-result__link" href="${link}">${result.bean.title}</a>
                </h3>
                <p class="ds_search-result__summary">
                   ${result.bean.summary?replace(pattern, "<mark>$1</mark>", 'ri')?no_esc}
                </p>
            <#else>
                <h3 class="ds_search-result__title">
                    <a class="ds_search-result__link" href="${result.liveUrl}">${result.title}</a>
                </h3>
                <p class="ds_search-result__summary">
                    <#if (result.listMetadata["c"]?first)!?has_content>
                        ${(result.listMetadata["c"]?first)?replace(pattern, "<mark>$1</mark>", 'ri')?no_esc!}
                    <#else>
                        ${result.summary?replace(pattern, "<mark>$1</mark>", 'ri')?no_esc!}
                    </#if>
                </p>

                <#if (result.listMetadata["titleSeriesLink"]?first)!?has_content>
                    <dl class="ds_search-result__context">
                        <dt class="ds_search-result__context-key">Part of:</dt>
                        <dd class="ds_search-result__context-value"><a href="${(result.listMetadata["titleSeriesLink"]?first)!}">${(result.listMetadata["titleSeries"]?first)!}</a></dd>
                    </dl>
                </#if>
            </#if>
        </li>
    </#list>
</ol>

<!-- base.Paging -->




<nav class="ds_pagination" aria-label="">
    <ul class="ds_pagination__list">

    <#if (response.customData.stencils.pagination.pages)!?has_content &&
    response.customData.stencils.pagination.pages?size gt 1>
        <#assign firstPageInGroup = response.customData.stencils.pagination.pages[0].label?number />
        <#assign lastPageInGroup = response.customData.stencils.pagination.pages[response.customData.stencils.pagination.pages?size - 1].label?number />
        <#assign lastPage = (totalResults / perPage)?ceiling />

        <#if (response.customData.stencils.pagination.previous)??>
        <li class="ds_pagination__item">
            <a class="ds_pagination__link  ds_pagination__link--text  ds_pagination__link--icon" href="${(response.customData.stencils.pagination.first.url)!}">
                <svg class="ds_icon" aria-hidden="true" role="img">
                    <use href="${iconspath}#chevron_left"></use>
                </svg>
                <span class="ds_pagination__link-label">Previous</span>
            </a>
        </li>
        </#if>

        <#if (firstPageInGroup == 2)>
            <li class="ds_pagination__item">
                <a class="ds_pagination__link" href="${(response.customData.stencils.pagination.first.url)!}">
                    <span class="ds_pagination__link-label">1</span>
                </a>
            </li>
        </#if>

        <#if (firstPageInGroup > 2)>
            <li class="ds_pagination__item">
                <a class="ds_pagination__link" href="${(response.customData.stencils.pagination.first.url)!}">
                    <span class="ds_pagination__link-label">1</span>
                </a>
            </li>

            <li class="ds_pagination__item" aria-hidden="true">
                <span class="ds_pagination__link  ds_pagination__link--ellipsis">&hellip;</span>
            </li>
        </#if>

        <#list response.customData.stencils.pagination.pages as page>
            <#if page.selected>
                <li class="ds_pagination__item" aria-current="page">
                    <span class="ds_pagination__link  ds_current">${page.label}</span>
                </li>
            <#else>
                <li class="ds_pagination__item">
                    <a class="ds_pagination__link" href="${page.url}">
                        <span class="ds_pagination__link-label">${page.label}</span>
                    </a>
                </li>
            </#if>
        </#list>
        <#if (response.customData.stencils.pagination.pages)!?has_content &&
        response.customData.stencils.pagination.pages?size gt 10>
            <li class="ds_pagination__item" aria-hidden="true">
                <span class="ds_pagination__link  ds_pagination__link--ellipsis">&hellip;</span>
            </li>
        </#if>

        <#if (lastPageInGroup < (lastPage - 1))>
            <li class="ds_pagination__item" aria-hidden="true">
                <span class="ds_pagination__link  ds_pagination__link--ellipsis">&hellip;</span>
            </li>

            <li class="ds_pagination__item">
                <a class="ds_pagination__link" href="${(response.customData.stencils.pagination.first.url)!}&page=${lastPage}">
                    <span class="ds_pagination__link-label">${lastPage}</span>
                </a>
            </li>
        </#if>

        <#if (lastPageInGroup == (lastPage - 1))>
            <li class="ds_pagination__item">
                <a class="ds_pagination__link" href="${(response.customData.stencils.pagination.first.url)!}&page=${lastPage}">
                    <span class="ds_pagination__link-label">${lastPage}</span>
                </a>
            </li>
        </#if>

        <#if (response.customData.stencils.pagination.next)??>
            <li class="ds_pagination__item">
                <a class="ds_pagination__link  ds_pagination__link--text  ds_pagination__link--icon" href="${(response.customData.stencils.pagination.next.url)!}">
                    <span class="ds_pagination__link-label">Next</span>
                    <svg class="ds_icon" aria-hidden="true" role="img">
                        <use href="${iconspath}#chevron_right"></use>
                    </svg>
                </a>
            </li>
        </#if>
    </#if>
    </ul>
</nav>
