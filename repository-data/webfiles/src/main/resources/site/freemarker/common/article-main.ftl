<#ftl output_format="HTML">
<#include "include/imports.ftl">
<#include "macros/content-blocks.ftl">
<#include "macros/lang-attributes.ftl">

<#assign variables = hstRequestContext.getAttribute("variables")/>
<@hst.messagesReplace escapeMessageXml=false bundle=variables variablePrefix="[[" variableSuffix="]]">

<#if document??>
<div class="cms-editable">
    <@hst.manageContent hippobean=document />

    <@hst.include ref="breadcrumbs"/>

    <div class="ds_wrapper">
        <main <@lang document/> id="main-content" class="ds_layout  ds_layout--article">

            <#if document.sensitive?? && document.sensitive>
                <!--noindex-->
                <#include "hide-this-page.ftl">
                <!--endnoindex-->
            </#if>

            <div class="ds_layout__header">
                <header class="ds_page-header">
                    <h1 class="ds_page-header__title"><#if document.title??>${document.title}</#if></h1>
                    <dl <@revertlang document /> class="ds_page-header__metadata  ds_metadata">
                        <#if document.lastUpdatedDate??>
                            <div class="ds_metadata__item">
                                <dt class="ds_metadata__key">Last updated</dt>
                                <dd class="ds_metadata__value"><@fmt.formatDate value=document.lastUpdatedDate.time type="both" pattern="d MMM yyyy"/></dd>
                            </div>
                        </#if>
                    </dl>

                    <@hst.include ref="stepbysteptopbar"/>
                </header>
            </div>

            <#if document.logo??>
                <div <@revertlang document />  class="ds_layout__partner  mg_partner-logo">
                    <#if document.logo.xlargefourcolumns??>
                        <img alt="" src="<@hst.link hippobean=document.logo.xlargefourcolumns />"
                            loading="lazy"
                            srcset="
                            <@hst.link hippobean=document.logo.smallcolumns/> 448w,
                            <@hst.link hippobean=document.logo.smallcolumnsdoubled/> 896w,
                            <@hst.link hippobean=document.logo.mediumfourcolumns/> 224w,
                            <@hst.link hippobean=document.logo.mediumfourcolumnsdoubled/> 448w,
                            <@hst.link hippobean=document.logo.largefourcolumns/> 288w,
                            <@hst.link hippobean=document.logo.largefourcolumnsdoubled/> 576w,
                            <@hst.link hippobean=document.logo.xlargefourcolumns/> 352w,
                            <@hst.link hippobean=document.logo.xlargefourcolumnsdoubled/> 704w"
                            sizes="(min-width:1200px) 352px, (min-width:992px) 288px, (min-width: 768px) 224px, 448px"
                            >
                    <#else>
                        <img loading="lazy" alt="" src="<@hst.link hippobean=document.logo/>">
                    </#if>
                </div>
            </#if>

            <div class="ds_layout__content">
                <#if document.contentBlocks??>
                    <@renderContentBlocks document.contentBlocks />
                </#if>

                <#if sequenceable?? && sequenceable == true>
                    <!--noindex-->
                    <nav class="ds_sequential-nav" aria-label="Article navigation">
                        <#if prev??>
                            <div class="ds_sequential-nav__item  ds_sequential-nav__item--prev">
                                <@hst.link var="prevlink" hippobean=prev/>
                                <a <@langcompare prev document/>  title="Previous section" href="${prevlink}" class="ds_sequential-nav__button  ds_sequential-nav__button--left">
                                    <span class="ds_sequential-nav__text" data-label="previous">
                                        ${prev.title}
                                    </span>
                                </a>
                            </div>
                        </#if>
                        <#if next??>
                            <div class="ds_sequential-nav__item  ds_sequential-nav__item--next">
                                <@hst.link var="nextlink" hippobean=next/>
                                <a <@langcompare next document/> title="Next section" href="${nextlink}" class="ds_sequential-nav__button  ds_sequential-nav__button--right">
                                    <span class="ds_sequential-nav__text" data-label="next">
                                        ${next.title}
                                    </span>
                                </a>
                            </div>
                        </#if>
                    </nav>
                    <!--endnoindex-->
                </#if>
            </div>

            <@hst.include ref="sidebar"/>

            <#include 'feedback-wrapper.ftl'>

        </main>
    </div>
</div>

<@hst.headContribution category="meta">
    <meta name="dc.format" content="Article">
</@hst.headContribution>

</#if>
</@hst.messagesReplace>

<@hst.headContribution category="resourcehints">
    <#if nextlink??>
        <link rel="prerender" href="${nextlink}">
    </#if>
</@hst.headContribution>



<#assign scriptName="article">
<#include 'scripts.ftl'/>
