<#ftl output_format="HTML">
<#include "include/imports.ftl">
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
</#if>
</section>

<#if (response.resultPacket.qsups)!?size &gt; 0>
    <#list response.resultPacket.qsups as qsup>
        <nav>
            <p>Also showing results for <a href="?q=${qsup.query?url('UTF-8')}"> ${qsup.query}<#if qsup_has_next>, </#if></a><br />
            Show results only for <a href="?${queryString}&amp;qsup=off">${question.originalQuery}</a></p>
        </nav>
    </#list>
</#if>

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

<ol>

    <#if response.pagination.currentPageIndex = 1>
        <#list response.curator.exhibits as exhibit>
            <li>
                <h3>
                    CURATOR ${exhibit.category} <a href="${exhibit.displayUrl}">${exhibit.titleHtml?no_esc}</a>
                </h3>

                <p>
                    ${exhibit.descriptionHtml?no_esc}
                </p>
            </li>
        </#list>
    </#if>
    <#list response.resultPacket.results as result>
        <li>
            <h3>
                <a href="${result.liveUrl}">${result.listMetadata["t"]?first!}</a>
            </h3>

            <p>
                ${result.listMetadata["c"]?first}
            </p>

            <#if (result.listMetadata["titleSeriesLink"]?first)!?has_content>
                <dl>
                    <dt>Part of:</dt>
                    <dd><a href="${(result.listMetadata["titleSeriesLink"]?first)!}">${(result.listMetadata["titleSeries"]?first)!}</a></dd>
                </dl>
            </#if>
        </li>
    </#list>
</ol>

<ol>
    <#if response.pagination.previous??>
        <li>
            <a href="${response.pagination.previous.url}">${response.pagination.previous.label}</a>
        </li>
    </#if>

    <#if response.pagination.first??>
        <li>
            <a href="${response.pagination.first.url}">${response.pagination.first.label}</a>
        </li>
        <li>...</li>
    </#if>

    <#list response.pagination.pages as page>
        <li>
            <#if page.selected>
                ${page.label}</span>
            <#else>
                <a href="${page.url}">${page.label}</a>
            </#if>
        </li>
    </#list>

    <#if response.pagination.last??>
        <li>...</li>
        <li>
            <a href="${response.pagination.last.url}">${response.pagination.last.label}</a>
        </li>
    </#if>

    <#if response.pagination.next??>
        <li>
            <a href="${response.pagination.next.url}">${response.pagination.next.label}</a>
        </li>
    </#if>

</ol>

<#if (response.resultPacket.contextualNavigation.categories)!?size &gt; 0>
    <h2>Related searches</h2>
    <ol>
        <#list response.resultPacket.contextualNavigation.categories as category>
            <#list category.clusters as cluster>
                <li>
                    <a href="?q=%60${cluster.query?url}%60">${cluster.query}</a>
                </li>
            </#list>
        </#list>
    </ol>
</#if>
</#if>