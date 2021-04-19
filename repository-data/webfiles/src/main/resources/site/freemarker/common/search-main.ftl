<#ftl output_format="HTML">
<#include "include/imports.ftl">

<#-- @ftlvariable name="pageable" type="org.onehippo.cms7.essentials.components.paging.Pageable" -->
<#-- @ftlvariable name="parameters" type="java.util.Map" -->
<#-- this div is here to make use of 'pageable' -->

<div class="cms-editable">
    <div class="ds_wrapper">
        <main id="main-content" class="ds_layout  ds_layout--article">

            <div class="ds_layout__header">
                <header class="ds_page-header">
                    <h1>${document.title}</h1>
                </header>
            </div>

            <div class="ds_layout__content">

                <@hst.html hippohtml=document.content/>

                <section id="search-results" class="ds_search-results">
                    <@hst.include ref="results"/>
                </section>
            </div>
        </main>
    </div>
</div>

<@hst.headContribution category="meta">
    <#if document?? && document.metaDescription??>
    <meta name="description" content="${document.metaDescription}"/>
    </#if>
</@hst.headContribution>
