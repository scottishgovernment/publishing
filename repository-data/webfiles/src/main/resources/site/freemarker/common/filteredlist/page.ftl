<#ftl output_format="HTML">
<#include "../include/imports.ftl">
<#include "../macros/content-blocks.ftl">

<#if document??>
    <@hst.include ref="breadcrumbs"/>
    <div class="ds_wrapper">
        <main id="main-content" class="ds_layout  ds_layout--search-results--filters">
            <div class="ds_layout__header">
                <header class="ds_page-header">
                    <h1 class="ds_page-header__title">${document.title}</h1>
                </header>
            </div>

            <div class="ds_layout__content">
                <#if document.contentBlocks??>
                    <@renderContentBlocks document.contentBlocks />
                </#if>
            </div>

            <div class="ds_layout__sidebar">
                <@hst.include ref="side-filter"/>
            </div>

            <div class="ds_layout__list">
                <section class="ds_search-results">
                    <@hst.include ref="results"/>
                </section>
            </div>

            <@hst.include ref="feedback"/>
        </main>
    </div>
</#if>

<#assign scriptName="filtered-list">
<#include '../scripts.ftl'/>
