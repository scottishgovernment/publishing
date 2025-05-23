<#ftl output_format="HTML">
<#include "include/imports.ftl">
<#include "macros/lang-attributes.ftl">

<#if document??>
<#assign variables = hstRequestContext.getAttribute("variables")/>
<@hst.messagesReplace escapeMessageXml=false bundle=variables variablePrefix="[[" variableSuffix="]]">

<div class="cms-editable">
    <@hst.manageContent hippobean=document />

    <@hst.include ref="breadcrumbs"/>

    <#assign accordionCount = 0 />

    <div class="ds_wrapper">
        <main <@lang document/> id="main-content" class="ds_layout  ds_layout--pl-component">

            <@hst.include ref="sidebar"/>

            <div class="ds_layout__header">
                <header class="ds_page-header">
                    <#if type?has_content>
                    <#if isNew><strong class="ds_page-header__tag ds_tag">New</strong></#if><span class="ds_page-header__label  ds_content-label">${type}</span>
                    </#if>
                    <h1 class="ds_page-header__title"><#if document.title??>${document.title}</#if></h1>
                    <#include "./metadata.ftl"/>
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
                        <img loading="lazy" alt="" src="<@hst.link hippobean=document.logo/>" />
                    </#if>
                </div>
            </#if>

            <div class="ds_layout__content">

                <#if document.summary?has_content>
                    <div class="ds_leader">
                        ${document.summary}
                    </div>
                </#if>

            </div>

            <@hst.include ref="feedback"/>

        </main>
    </div>
</div>
</@hst.messagesReplace>

<@hst.headContribution category="meta">
    <meta name="dc.format" content="${format}"/>
</@hst.headContribution>

<#if seriesLink??>
    <@hst.headContribution category="meta">
        <@hst.messagesReplace escapeMessageXml=false bundle=variables variablePrefix="[[" variableSuffix="]]">
        <meta name="dc.title.series" content="${seriesLink.title}"/>
        </@hst.messagesReplace>
    </@hst.headContribution>

    <@hst.headContribution category="meta">
        <meta name="dc.title.series.link" content="<@hst.link hippobean=seriesLink/>"/>
    </@hst.headContribution>
</#if>

<@hst.headContribution category="meta">
    <meta name="dc.date.modified" content="<@fmt.formatDate value=date type="both" pattern="YYYY-MM-dd HH:mm"/>"/>
</@hst.headContribution>
</#if>



<@hst.headContribution category="resourcehints">
    <#if nextlink??>
        <link rel="prerender" href="${nextlink}"/>
    </#if>
</@hst.headContribution>



<#assign scriptName="ds-article">
<#include '../common/scripts.ftl'/>
