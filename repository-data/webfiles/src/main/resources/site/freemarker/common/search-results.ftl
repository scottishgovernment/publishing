<#ftl output_format="HTML">
<#include 'include/search.ftl'/>

<#if pageable??>
    <#if pageable.total gt 0>
    <h2 class="ds_search-results__title">
        <span class="ds_search-results__title-count">${pageable.total}</span> results
    </h2>
    <#else>
    <h2 class="visually-hidden" id="search-results__heading">Search results</h2>
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
<#else>   
<span class="ds_search-results__title-count">0</span> results, please fill in the search field.
</#if>

<#if pageable??>
<ol id="search-results-list" class="ds_search-results__list" data-total="${pageable.total}">
    <#list pageable.items as item>
        <@hst.manageContent hippobean=item/>
        <@hst.link var="link" hippobean=item/>
        <li class="ds_search-result">
            <#assign position = item_index + ((pageable.currentPage-1) * pageable.pageSize) />
            <h3 class="ds_search-result__title">
                <a class="ds_search-result__link" href="${link}">${item.title}</a>
            </h3>
            <p class="ds_search-result__summary">${item.summary}</p>
        </li>
    </#list>
</ol>

<div id="pagination">
    <#if cparam.showPagination??>
        <#include "include/pagination.ftl">
    </#if>
</div>
</#if>