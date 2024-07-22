<#ftl output_format="HTML">
<#include "include/imports.ftl">
<#include "macros/content-blocks.ftl">

<div class="cms-editable">
    <div class="ds_wrapper">
        <main id="main-content" class="ds_layout  ds_layout--search-results">

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

            <div id="search-results" class="ds_layout__list  ds_search-results">
                <@hst.include ref="results"/>
            </div>

            <#include 'feedback-wrapper.ftl'>
        </main>
    </div>
</div>

<#assign scriptName="search">
<#include 'scripts.ftl'/>
