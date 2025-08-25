<#ftl output_format="HTML">
<#include "../include/imports.ftl">
<#include "../macros/content-blocks.ftl">
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

<#-- @ftlvariable name="pageable" type="org.onehippo.cms7.essentials.components.paging.Pageable" -->
<#-- @ftlvariable name="parameters" type="java.util.Map" -->
<#-- @ftlvariable name="item" type="scot.gov.www.beans.News" -->
<#-- @ftlvariable name="searchTermSingular" type="java.lang.String" -->
<#-- @ftlvariable name="searchTermPlural" type="java.lang.String" -->
<#-- @ftlvariable name="showSort" type="java.lang.Boolean" -->

<#-- Set number format to exclude comma separators -->
<#setting number_format="0.##">

<#if response??>
    <#if (response.resultPacket.resultsSummary.totalMatching)!?has_content &&
        response.resultPacket.resultsSummary.totalMatching &gt; 0>
        <h2 aria-live="polite" class="ds_search-results__title">
            <#if response.resultPacket.resultsSummary.fullyMatching <= response.resultPacket.resultsSummary.numRanks ||
            response.resultPacket.resultsSummary.currStart <= response.resultPacket.resultsSummary.numRanks >

            ${response.resultPacket.resultsSummary.totalMatching} <#if response.resultPacket.resultsSummary.totalMatching gt 1>results<#else>result</#if><#if question.originalQuery?has_content> for <span class="ds_search-results__title-query">${question.originalQuery}</span></#if>
            <#else>
                Showing ${response.resultPacket.resultsSummary.currStart} to ${response.resultPacket.resultsSummary.currEnd}
                of ${response.resultPacket.resultsSummary.totalMatching} <#if response.resultPacket.resultsSummary.totalMatching gt 1>results<#else>result</#if><#if question.originalQuery?has_content> for <span class="ds_search-results__title-query">${question.originalQuery}</span></#if>
            </#if>
        </h2>
    </#if>

    <#include "../include/filter-buttons.ftl"/>

    <#if (response.resultPacket.resultsSummary.totalMatching)!?has_content &&
        response.resultPacket.resultsSummary.totalMatching == 0 &&
        !(response.curator.simpleHtmlExhibits)?has_content &&
        !(response.curator.advertExhibits)?has_content>
        <h2 class="visually-hidden">Search</h2>
        <div id="no-search-results" class="ds_no-search-results">
                <#if document.noResultsMessageContentBlocks??>
                    <@renderContentBlocks document.noResultsMessageContentBlocks />
                </#if>
        </div>
    </#if>

<#if pagination??>
    <#if ((response.resultPacket.resultsSummary.totalMatching)!?has_content &&
        response.resultPacket.resultsSummary.totalMatching &gt; 0 ) >
    <ol start="${response.resultPacket.resultsSummary.currStart?c}" id="search-results" class="ds_search-results__list" data-total="${response.resultPacket.resultsSummary.totalMatching?c}">
    <#-- Using same result template as search -->
    <#list pageable.items as item>
       <#include "result.ftl">
    </#list>
    </ol>
    </#if>

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

</#if>
