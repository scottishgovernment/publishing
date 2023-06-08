<#ftl output_format="HTML">
<#include "../common/include/imports.ftl">
<#include "../common/macros/lang-attributes.ftl">

<#if document??>
<div class="cms-editable">
    <@hst.manageContent hippobean=document />

    <@hst.include ref="breadcrumbs"/>

    <div class="ds_wrapper">
        <main <@lang document/> id="main-content" class="ds_layout  ds_layout--pl-component">

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
                        <img loading="lazy" alt="" src="<@hst.link hippobean=document.logo/>" />
                    </#if>
                </div>
            </#if>


            <div class="ds_layout__content">

                <div class="ds_leader">
                    ${document.summary}
                </div>

                <#if document.contentblocks??>

                    <#list document.contentblocks as contentblock>
                        <#if hst.isNodeType(contentblock.node, 'publishing:dsexampleblock')>
                            <!-- example block -->
                            <@hst.link var="example" hippobean=contentblock.example/>
                            <figure class="example  overflow--large--2 overflow--xlarge--2">
                                <div class="example__content">
                                    <#if contentblock.showdemo>
                                        <div class="example__demo">
                                            <a class="example__link" href="${example}" target="_blank">Open this example in a new window</a>

                                            <iframe title="Accordion example" style="" data-style="" src="${example}" class="example__iframe">
                                            </iframe>
                                        </div>
                                    </#if>

                                    <#if contentblock.note?has_content>
                                        <p class="example-frame__note">Note: ${contentblock.note}</p>
                                    </#if>

                                    <div class="example__accordion  ds_accordion" data-module="ds-accordion">
                                        <#if contentblock.showcode>
                                            <div class="ds_accordion-item  <#if contentblock.htmlexpanded>ds_accordion-item--open</#if>">
                                                <input type="checkbox" <#if contentblock.htmlexpanded>checked</#if> class="visually-hidden  ds_accordion-item__control" id="panel-main-1" aria-labelledby="panel-main-1-heading">
                                                <div class="ds_accordion-item__header">
                                                    <h3 id="panel-main-1-heading" class="ds_accordion-item__title">
                                                        Sample ${contentblock.type}
                                                    </h3>
                                                    <span class="ds_accordion-item__indicator"></span>
                                                    <label class="ds_accordion-item__label" for="panel-main-1"><span class="visually-hidden">Show this section</span></label>
                                                </div>
                                                <div class="ds_accordion-item__body  example__accordion-body--code">
                                                    <pre><code class="language-html">${contentblock.example.code}</code></pre>
                                                </div>
                                            </div>
                                        </#if>
                                    </div>
                                </div>
                            </figure>
                            <!-- end example block -->
                        </#if>

                        <#if hst.isNodeType(contentblock.node, 'publishing:dscontentblock')>
                            <!-- content block -->
                            <@hst.html hippohtml=contentblock.content/>
                            <!-- end content block -->
                        </#if>

                    </#list>
                </#if>

            </div>

            <#include '../common/feedback-wrapper.ftl'>

        </main>
    </div>
</div>

<@hst.headContribution category="meta">
    <meta name="dc.format" content="Article"/>
</@hst.headContribution>

</#if>

<@hst.headContribution category="resourcehints">
    <#if nextlink??>
        <link rel="prerender" href="${nextlink}"/>
    </#if>
</@hst.headContribution>



<#assign scriptName="ds-article">
<#include '../common/scripts.ftl'/>
