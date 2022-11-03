<#ftl output_format="HTML">
<#include "include/imports.ftl">
<#include "macros/lang-attributes.ftl">

<#if document??>
<div class="cms-editable">
    <@hst.manageContent hippobean=document />

    <@hst.include ref="breadcrumbs"/>

    <div class="ds_wrapper">
        <main <@lang document/> id="main-content" class="ds_layout  ds_layout--guide">
            <#if guide.sensitive?? && guide.sensitive>
                <!--noindex-->
                <#include "hide-this-page.ftl">
                <!--endnoindex-->
            </#if>

            <div class="ds_layout__header">
                <header class="ds_page-header">
                    <span <@langcompare guide document/> class="ds_page-header__label  ds_content-label">${guide.title}</span>
                    <h1 class="ds_page-header__title">${document.title}</h1>
                    <dl <@revertlang document /> class="ds_page-header__metadata  ds_metadata">
                        <#if guide.lastUpdatedDate??>
                            <div class="ds_metadata__item">
                                <dt class="ds_metadata__key">Last updated</dt>
                                <dd class="ds_metadata__value"><@fmt.formatDate value=guide.lastUpdatedDate.time type="both" pattern="d MMM yyyy"/></dd>
                            </div>
                        </#if>
                    </dl>

                    <@hst.include ref="stepbysteptopbar"/>
                </header>
            </div>

            <!--noindex-->
            <div class="ds_layout__navigation">
                <nav role="navigation" class="ds_contents-nav" aria-label="Sections">
                    <h2 <@revertlang document /> class="gamma">Contents</h2>

                    <ul class="ds_contents-nav__list">
                        <#list children as child>
                        <li class="ds_contents-nav__item">
                            <#if child.bean == document>
                                <span <@langcompare child.bean document /> class="ds_contents-nav__link  ds_current">
                                    ${child.bean.title}
                                </span>
                            <#else>
                                <@hst.link var="link" hippobean=child.bean/>
                                <a <@langcompare child.bean document /> href="${link}" class="ds_contents-nav__link">
                                    ${child.bean.title}
                                </a>
                            </#if>
                        </li>
                        </#list>
                    </ul>
                </nav>
            </div>
            <!--endnoindex-->

            <#if guide.logo??>
                <div <@revertlang document /> class="ds_layout__partner  mg_partner-logo">
                    <#if guide.logo.xlargefourcolumns??>
                        <img alt="" src="<@hst.link hippobean=guide.logo.xlargefourcolumns />"
                            loading="lazy"
                            srcset="
                            <@hst.link hippobean=guide.logo.smallcolumns/> 448w,
                            <@hst.link hippobean=guide.logo.smallcolumnsdoubled/> 896w,
                            <@hst.link hippobean=guide.logo.mediumfourcolumns/> 224w,
                            <@hst.link hippobean=guide.logo.mediumfourcolumnsdoubled/> 448w,
                            <@hst.link hippobean=guide.logo.largefourcolumns/> 288w,
                            <@hst.link hippobean=guide.logo.largefourcolumnsdoubled/> 576w,
                            <@hst.link hippobean=guide.logo.xlargefourcolumns/> 352w,
                            <@hst.link hippobean=guide.logo.xlargefourcolumnsdoubled/> 704w"
                            sizes="(min-width:1200px) 352px, (min-width:992px) 288px, (min-width: 768px) 224px, 448px"
                            >
                    <#else>
                        <img loading="lazy" alt="" src="<@hst.link hippobean=guide.logo/>" />
                    </#if>
                </div>
            </#if>

            <div class="ds_layout__content">
                <@hst.html hippohtml=document.content/>

                <!--noindex-->
                <nav class="ds_sequential-nav" aria-label="Article navigation">
                    <#if prev??>
                        <div class="ds_sequential-nav__item  ds_sequential-nav__item--prev">
                            <@hst.link var="prevlink" hippobean=prev/>
                            <a <@langcompare prev document/> title="Previous section" href="${prevlink}" class="ds_sequential-nav__button  ds_sequential-nav__button--left">
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
            </div>


            <@hst.include ref="sidebar"/>

            <!--endnoindex-->


            <#include 'feedback-wrapper.ftl'>
        </main>
    </div>
</div>

<@hst.headContribution category="meta">
    <meta name="dc.format" content="Guide"/>
</@hst.headContribution>

<#if guide?? && !firstPage >
<@hst.headContribution category="meta">
    <meta name="dc.title.series" content="${guide.title}"/>
</@hst.headContribution>
<@hst.headContribution category="meta">
    <meta name="dc.title.series.link" content="<@hst.link hippobean=guide/>"/>
</@hst.headContribution>
</#if>
<#if guide.lastUpdatedDate??>
    <@hst.headContribution category="meta">
    <meta name="dc.date.modified" content="<@fmt.formatDate value=guide.lastUpdatedDate.time type="both" pattern="YYYY-MM-dd"/>"/>
    </@hst.headContribution>
</#if>
</#if>

<@hst.headContribution category="resourcehints">
    <#if nextlink??>
    <link rel="prerender" href="${nextlink}"/>
    </#if>
</@hst.headContribution>

<#assign scriptName="guide">
<#include 'scripts.ftl'/>
