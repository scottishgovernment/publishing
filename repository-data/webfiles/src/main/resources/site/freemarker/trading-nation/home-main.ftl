<#ftl output_format="HTML">
<#include "../common/include/imports.ftl">

<#if document??>
<div class="cms-editable">
    <@hst.manageContent hippobean=document />

    <div class="category-upper">
        <div class="ds_wrapper">
            <header class="ds_feature-header  ds_feature-header--wide">
                <div class="ds_feature-header__primary">
                    <h1 class="ds_feature-header__title">A Trading Nation - ${document.title}</h1>
                    <p>${document.summary}</p>
                </div>

                <#if document.heroImage??>
                    <div class="ds_feature-header__secondary">
                        <img class="ds_feature-header__image" alt="" src="<@hst.link hippobean=document.heroImage />"
                            width="${document.heroImage.xlargefourcolumns.width?c}"
                            height="${document.heroImage.xlargefourcolumns.height?c}"
                            loading="lazy"
                            srcset="
                            <@hst.link hippobean=document.heroImage.mediumfourcolumns/> 224w,
                            <@hst.link hippobean=document.heroImage.mediumfourcolumnsdoubled/> 448w,
                            <@hst.link hippobean=document.heroImage.largefourcolumns/> 288w,
                            <@hst.link hippobean=document.heroImage.largefourcolumnsdoubled/> 576w,
                            <@hst.link hippobean=document.heroImage.xlargefourcolumns/> 352w,
                            <@hst.link hippobean=document.heroImage.xlargefourcolumnsdoubled/> 704w"
                            sizes="(min-width:1200px) 352px, (min-width:992px) 288px, 224px"
                        >
                    </div>
                </#if>
            </header>
        </div>
    </div>
</div>

<div class="category-lower  ds_pre-footer-background">
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
                    <#include '../common/card-navigation--image.ftl'/>
                </#if>
                    <#if document.navigationType == "card">
                <#include '../common/card-navigation.ftl'/>
            </#if>
                    <#if document.navigationType == "grid">
                <#include '../common/grid-navigation.ftl'/>
            </#if>
                    <#if document.navigationType == "list">
                <#include '../common/list-navigation.ftl'/>
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

<@hst.headContribution category="meta">
    <#if document??>
        <meta name="description" content="${document.metaDescription}"/>
    </#if>
</@hst.headContribution>

<#assign scriptName="home">
<#include '../common/scripts.ftl'/>
