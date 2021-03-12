<#include "include/imports.ftl">

<#-- @ftlvariable name="pageable" type="org.onehippo.cms7.essentials.components.paging.Pageable" -->
<#-- @ftlvariable name="parameters" type="java.util.Map" -->
<#-- this div is here to make use of 'pageable' -->

<div class="cms-editable">
    <div class="ds_wrapper">
        <main class="ds_layout  ds_layout--article">

            <div class="ds_layout__header">
                <header class="ds_page-header">
                    <h1 class="hidden">${document.title}</h1>
                </header>
            </div>

            <div class="ds_layout__content">

                <@hst.html hippohtml=document.content/>

                <section id="search-results" class="ds_search-results">

                    <h2 class="visually-hidden">${document.title}</h2>

                    <@hst.include ref="results"/>
                </section>
            </div>
        </main>
    </div>
</div>

<@hst.headContribution category="meta">
    <#if document??>
    <meta name="description" content="${document.metaDescription?html}"/>
    </#if>
</@hst.headContribution>
