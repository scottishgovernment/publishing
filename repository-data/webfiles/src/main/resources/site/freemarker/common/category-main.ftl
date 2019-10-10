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
                        <#if document.heroimage??>
                            <img class="ds_category-header__media" alt="" 
                                src="<@hst.link document.heroimage.original/>" />
                            Image lowercase i            
                            <#else>
                            No image lowercase i 
                        </#if> 

                        <#if document.heroImage??>
                            <img class="ds_category-header__media" alt="" 
                                src="<@hst.link document.heroImage.original/>" />
                            Image uppercase I            
                            <#else>
                            No image uppercase I 
                        </#if>  
                    </div>
            </div>
        </div>
    </div>
</div>

<div class="category-lower">
    <#if document.prologue??>
        <div class="ds_wrapper">
            <@hst.html hippohtml=document.prologue/>
        </div>
    </#if>

    <div class="ds_category-list-container">
        <#if document.navigationType == "grid">
            <#include 'grid-navigation.ftl'/>
        </#if>

        <#if document.navigationType == "list">
            <#include 'list-navigation.ftl'/>
        </#if>
    </div>

    <#if document.epilogue??>
        <div class="ds_wrapper">
            <@hst.html hippohtml=document.epilogue/>
        </div>
    </#if>
</div>
</#if>

<@hst.headContribution category="title">
    <#if document??>
        <title>${document.title} - A Trading Nation</title>
    </#if>
</@hst.headContribution>
