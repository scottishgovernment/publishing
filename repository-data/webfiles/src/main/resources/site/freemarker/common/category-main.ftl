<#ftl output_format="HTML">
<#include "include/imports.ftl">

<#if document??>
<div class="cms-editable">
    <@hst.manageContent hippobean=document />

    <main id="main-content">

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
                    <#if document.heroImage??>
                        <div class="ds_category-header__media-container">
                            <img class="ds_category-header__media" alt="" src="<@hst.link hippobean=document.heroImage />"
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
                </div>
            </div>
        </div>

        <div class="category-lower">
            <div class="ds_wrapper">
                <#if document.prologue??>
                    <@hst.html hippohtml=document.prologue/>
                </#if>

                <!--noindex-->
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
                <!--endnoindex-->

                <#if document.epilogue??>
                    <@hst.html hippohtml=document.epilogue/>
                </#if>

                <#include 'feedback-wrapper.ftl'>
            </div>
        </div>

    </main>
</div>

<@hst.headContribution category="meta">
    <meta name="dc.format" content="Category"/>
</@hst.headContribution>

</#if>

<#assign scriptName="category">
<#include 'scripts.ftl'/>
