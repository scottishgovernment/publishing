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

                <#if sequenceable?? && sequenceable == true>
                    <nav class="ds_sequential-nav" aria-label="Article navigation">
                        <#if prev??>
                            <div class="ds_sequential-nav__item  ds_sequential-nav__item--prev">
                                <@hst.link var="prevlink" hippobean=prev/>
                                <a data-navigation="sequential-previous" title="Previous section" href="${prevlink}" class="ds_sequential-nav__button  ds_sequential-nav__button--left">
                                    <span class="ds_sequential-nav__text" data-label="previous">
                                        ${prev.title?html}
                                    </span>
                                </a>
                            </div>
                        </#if>
                        <#if next??>
                            <div class="ds_sequential-nav__item  ds_sequential-nav__item--next">
                                <@hst.link var="nextlink" hippobean=next/>
                                <a data-navigation="sequential-next" title="Next section" href="${nextlink}" class="ds_sequential-nav__button  ds_sequential-nav__button--right">
                                    <span class="ds_sequential-nav__text" data-label="next">
                                        ${next.title?html}
                                    </span>
                                </a>
                            </div>
                        </#if>
                    </nav>
                </#if>
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
