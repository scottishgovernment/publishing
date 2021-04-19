<#ftl output_format="HTML">
<#include 'include/search.ftl'/>

<p class="ds_search-results__count js-search-results-count"><b>${pageable.total}</b> results</p>

<ol id="search-results-list" class="ds_search-results__list" data-total="${pageable.total}">
    <#list pageable.items as item>
        <@hst.manageContent hippobean=item/>
        <@hst.link var="link" hippobean=item/>
        <li class="ds_search-result">
            <#assign position = item_index + ((pageable.currentPage-1) * pageable.pageSize) />
            <header class="ds_search-result__header">
            <#--  <dl class="ds_search-result__metadata  ds_metadata  ds_metadata--inline">
                <span class="ds_metadata__item">
                    <dt class="ds_metadata__key  visually-hidden">Type</dt>
                    <dd class="ds_metadata__value  ds_content-label">Policy</dd>
                </span>
            </dl>  -->

                <h2 class="gamma  ds_search-result__title">
                    <a class="ds_search-result__link" href="${link}">${item.title}</a>
                </h2>
            </header>

            <p class="ds_search-result__summary">${item.summary}</p>
        </li>
    </#list>
</ol>

<div id="pagination">
    <#if cparam.showPagination??>
        <#include "include/pagination.ftl">
    </#if>
</div>
