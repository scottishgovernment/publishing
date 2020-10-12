<#include "include/imports.ftl">

<#-- @ftlvariable name="pageable" type="org.onehippo.cms7.essentials.components.paging.Pageable" -->
<#-- @ftlvariable name="parameters" type="java.util.Map" -->
<#-- this div is here to make use of 'pageable' -->

<#if pageable??>
<div class="cms-editable">
    <div class="ds_wrapper">
        <main class="ds_layout  ds_layout--article">

            <div class="ds_layout__header">
                <header class="ds_page-header">
                    <h1 class="hidden">Search results</h1>
                </header>
            </div>

            <div class="ds_layout__content">
                <#include 'include/search.ftl'/>

                <#--  <nav id="suggestions" class="ds_search-suggestions" aria-label="Alternative search suggestions">
                    <span aria-hidden="true">Did you mean:</span>

                    <ul>
                        <li>
                            <a data-search="suggestion-result-1/1" aria-label="Did you mean 'crafting'?" href="#">crafting</a>
                        </li>
                    </ul>
                </nav>  -->

                <section id="search-results" class="ds_search-results">

                    <h2 class="visually-hidden">Search results</h2>

                    <p class="ds_search-results__count js-search-results-count"><b>${pageable.total}</b> results</p>

                    <ol id="search-results-list" class="ds_search-results__list" data-search-term="crofting">
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
                                        <a class="ds_search-result__link" href="${link}" data-search="search-result-${position}/${pageable.total}">${item.title?html}</a>
                                    </h2>
                                </header>

                                <p class="ds_search-result__summary">${item.summary?html}</p>
                            </li>
                        </#list>
                    </ol>
                </section>
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
