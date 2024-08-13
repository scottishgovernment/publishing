<#ftl output_format="HTML">
<#include "include/imports.ftl">

<#assign variables = hstRequestContext.getAttribute("variables")/>
<@hst.messagesReplace escapeMessageXml=false bundle=variables variablePrefix="[[" variableSuffix="]]">

<#if document??>

<div class="cms-editable">
    <@hst.manageContent hippobean=document />

    <@hst.include ref="breadcrumbs"/>

    <div class="ds_wrapper">
        <main id="main-content" class="ds_layout  ds_layout--article">

            <#if document.sensitive?? && document.sensitive>
                <!--noindex-->
                <#include "hide-this-page.ftl">
                <!--endnoindex-->
            </#if>

            <div class="ds_layout__header">
                <header class="ds_page-header">
                    <h1 class="ds_page-header__title">${document.title}</h1>
                    <dl class="ds_page-header__metadata  ds_metadata">
                        <#if document.lastUpdatedDate??>
                            <div class="ds_metadata__item">
                                <dt class="ds_metadata__key">Last updated</dt>
                                <dd class="ds_metadata__value"><@fmt.formatDate value=document.lastUpdatedDate.time type="both" pattern="d MMM yyyy"/></dd>
                            </div>
                        </#if>
                    </dl>
                </header>
            </div>

            <#if document.logo??>
                <div class="ds_layout__partner  mg_partner-logo">
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
                <@hst.html hippohtml=document.content/>

                <div class="ds_step-navigation">
                    <div class="ds_accordion" data-module="ds-accordion">
                        <button data-accordion="accordion-open-all" type="button" class="ds_link  ds_accordion__open-all  js-open-all">Open all <span class="visually-hidden">sections</span></button>

                        <#assign stepcount = 0 />
                        <#list document.steps as step>

                            <#if step.labeltype == 'or' || step.labeltype == 'and'>
                            <#else>
                                <#assign stepcount = stepcount + 1 />
                            </#if>

                            <div class="ds_accordion-item  <#if step.labeltype == "or">ds_step-navigation__or<#elseif step.labeltype == "and">ds_step-navigation__and</#if>">
                                <span class="ds_step-navigation__count">
                                    <#if step.labeltype == 'or'>
                                        or <span class="visually-hidden">(instead of step ${stepcount})</span>
                                    <#elseif step.labeltype == 'and'>
                                        and <span class="visually-hidden">(as well as step ${stepcount})</span>
                                    <#else>
                                        <span class="visually-hidden">Step </span>${stepcount}
                                    </#if>
                                </span>
                                <input type="checkbox" class="visually-hidden  ds_accordion-item__control" id="panel-${step?index}" aria-labelledby="panel-${step?index}-heading" />
                                <div class="ds_accordion-item__header">
                                    <h3 id="panel-${step?index}-heading" class="ds_accordion-item__title">
                                        ${step.title}
                                    </h3>
                                    <span class="ds_accordion-item__indicator"></span>
                                    <label class="ds_accordion-item__label" for="panel-${step?index}"><span class="visually-hidden">Show this section</span></label>
                                </div>
                                <div class="ds_accordion-item__body">
                                    <@hst.html hippohtml=step.content/>
                                </div>
                            </div>
                        </#list>
                    </div>
                </div>
            </div>

            <div class="ds_layout__sidebar">
            </div>

            <#include 'feedback-wrapper.ftl'>

        </main>
    </div>
</div>

<@hst.headContribution category="meta">
<meta name="dc.format" content="StepByStep"/>
</@hst.headContribution>

</#if>
</@hst.messagesReplace>

<@hst.headContribution category="resourcehints">
    <#if nextlink??>
    <link rel="prerender" href="${nextlink}"/>
    </#if>
</@hst.headContribution>



<#assign scriptName="article">
<#include 'scripts.ftl'/>
