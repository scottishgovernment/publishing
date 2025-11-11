<#ftl output_format="HTML">
<#include "include/imports.ftl">
<#include "macros/content-blocks.ftl">

<div class="cms-editable">
    <div class="ds_wrapper">
        <main id="main-content" class="ds_layout  <#if showFilters>ds_layout--search-results--filters<#else>ds_layout--search-results</#if>">

            <div class="ds_layout__header">
                <header class="ds_page-header">
                    <h1 class="ds_page-header__title">${document.title}</h1>
                </header>
            </div>

            <div class="ds_layout__content">
                <#if document.contentBlocks??>
                    <@renderContentBlocks document.contentBlocks />
                </#if>

                <#if autoCompleteEnabled>
                    <#assign ds_autocomplete = true />
                </#if>
                <#assign searchpagepath = hstRequestContext.servletRequest.pathInfo />

                <#assign searchcategory = "sitesearch" />
                <#include 'include/search.ftl'/>
            </div>

            <@hst.include ref="side-filter"/>

            <#if document.title == "Search">
                <#assign ariaLabel = 'Search results'/>
            <#elseif document.title == "Search results">
                <#assign ariaLabel = 'Search results'/>
            <#else>
                <#assign ariaLabel = document.title + ' search results'/>
            </#if>

            <div id="search-results" class="ds_layout__list">
                <section role="region" aria-label="${ariaLabel}" class="ds_search-results">
                    <@hst.include ref="results"/>
                </section>
            </div>

            <@hst.include ref="feedback"/>
        </main>
    </div>
</div>

<#if showFilters>
    <#assign scriptName="filtered-list">
<#else>
    <#assign scriptName="search">
</#if>

<#include 'scripts.ftl'/>
