<#include "include/imports.ftl">

    <#if document??>
        <div class="cms-editable">
            <@hst.manageContent hippobean=document />

            <@hst.include ref="breadcrumbs"/>

            <div class="ds_wrapper">
                <main class="ds_layout  ds_layout--tn-article">
                    <div class="ds_layout__header">
                        <header class="ds_page-header">
                            <h1 class="ds_page-header__title">${document.title}</h1>
                        </header>
                    </div>

                    <div class="ds_layout__content">
                        <@hst.html hippohtml=document.content/>
                    </div>
                </main>
            </div>
        </div>
    </#if>

    <@hst.headContribution category="resourcehints">
    <#if nextlink??>
        <link rel="prerender" href="${nextlink}"/>
    </#if>
</@hst.headContribution>

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

<#assign scriptName="${document.formtype}">
<#include 'scripts.ftl'/>