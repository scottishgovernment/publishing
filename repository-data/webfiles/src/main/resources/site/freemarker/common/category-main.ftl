<#include "include/imports.ftl">

<#if document??>
<div class="cms-editable">
    <@hst.manageContent hippobean=document />

    <div class="category-upper">
        <@hst.include ref="breadcrumbs"/>

        <div class="ds_wrapper">
            <div class="ds_category-header">
                <header class="ds_category-header__header">
                    <h1 class="ds_category-header__title">${document.title}</h1>
                    <div class="ds_category-header__summary">
                        <p>${document.summary}</p>
                    </div>
                </header>
                      <div class="ds_category-header__media-container">
                        <#if document.heroImage??>
                            <img class="ds_category-header__media" alt=""
                                src="<@hst.link hippobean=document.heroImage/>" />
                        </#if>
                    </div>
            </div>
        </div>
    </div>
</div>

<div class="category-lower">
    <div class="ds_wrapper">
        <#if document.prologue??>
            <@hst.html hippohtml=document.prologue/>
        </#if>



        <div class="ds_layout  ds_layout--category-list">
            <#if document.navigationType == "list">
            <div class="ds_layout__list">
            <#else>
            <div class="ds_layout__grid">
            </#if>
                <div class="ds_category-list-container">
                    <#if document.navigationType == "image-card">
                        <#include 'card-navigation--image.ftl'/>
                    </#if>
                    <#if document.navigationType == "card">
                        <#include 'card-navigation.ftl'/>
                    </#if>
                    <#if document.navigationType == "grid">
                        <#include 'grid-navigation.ftl'/>
                    </#if>
                    <#if document.navigationType == "list">
                        <#include 'list-navigation.ftl'/>
                    </#if>
                </div>
            </div>
        </div>



        <#if document.epilogue??>
            <@hst.html hippohtml=document.epilogue/>
        </#if>
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

<#assign scriptName="category">
<#include 'scripts.ftl'/>
