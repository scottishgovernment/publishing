<#include "include/imports.ftl">

<#-- @ftlvariable name="pageable" type="org.onehippo.cms7.essentials.components.paging.Pageable" -->
<#-- @ftlvariable name="parameters" type="java.util.Map" -->
<#-- this div is here to make use of 'pageable' -->

<#if pageable??>
<div class="cms-editable">
    <div class="ds_wrapper">
        <main class="ds_layout  ds_layout--tn-search">
            <div class="ds_layout__header">
                <header class="ds_page-header">
                    <h1 class="hidden">Search results</h1>
                    <#include 'include/search.ftl'/>

                </header>
                <div class="grid"><!--
                --><div class="grid__item medium--ten-twelfths large--eight-twelfths">
                    <section id="search-results" class="search-results">
                        <div id="results-top"></div>

                        <p id="result-count" class="search-results__count js-search-results-count">Showing <b>${pageable.total}</b> <#if pageable.total == 1>result<#else>results</#if></p>

                        <ol id="search-results-list" class="content-items" start="1">
                            <#list pageable.items as item>
                                <@hst.manageContent hippobean=item/>
                                <@hst.link var="link" hippobean=item/>
                                <li class="listed-content-item">

                                    <#assign position = item_index + ((pageable.currentPage-1) * pageable.pageSize) />

                                    <a class="listed-content-item__link" href="${link}" title="${item.title?html}" data-gtm="search-pos-${position}">
                                        <article class="listed-content-item__article listed-content-item__article--top-border">
                                            <h3 class="listed-content-item__title">${item.title?html}</h3>

                                            <#if item.summary??>
                                                <p class="listed-content-item__summary">
                                                ${item.summary?html}
                                                </p>
                                            </#if>
                                        </article>
                                    </a>
                                </li>
                            </#list>
                        </ol>

                    <#--Pagination here-->
                        <div id="pagination" class="search-results__pagination pagination">
                            <#if cparam.showPagination??>
                            <#assign gtmslug = relativeContentPath />
                            <#include "include/pagination.ftl">
                        </#if>
                        </div>

                    </section>
                </div><!--
            --></div>
            </div>
        </main>
    </div>
</div>
</#if>
<@hst.headContribution category="title">
    <#if document??>
    <title>${document.title} - A Trading Nation</title>
    </#if>
</@hst.headContribution>

<@hst.headContribution category="meta">
    <#if document??>
    <meta name="description" content="${document.metaDescription?html}"/>
    </#if>
</@hst.headContribution>
