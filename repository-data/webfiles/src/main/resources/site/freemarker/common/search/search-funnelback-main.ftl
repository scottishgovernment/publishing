<#ftl output_format="HTML">
<#include "../include/imports.ftl">
<#include "../macros/content-blocks.ftl">

<div class="cms-editable">
    <div class="ds_wrapper">
        <main id="main-content" class="ds_layout  <#if document.showFilters>ds_layout--search-results--filters<#else>ds_layout--search-results</#if>">

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
                <#include '../include/search.ftl'/>
            </div>

            <@hst.include ref="side-filter"/>

            <div id="search-results" class="ds_layout__list  ds_search-results">
                <section class="ds_search-results">
                    <@hst.include ref="results"/>
                </section>
            </div>
            <@hst.include ref="feedback"/>

        </main>
    </div>
</div>

<#assign scriptName="search">
<#include '../scripts.ftl'/>
