<#ftl output_format="HTML">
<#include "include/imports.ftl">
<#include "macros/content-blocks.ftl">
<#include "macros/format-file-size.ftl">
<#include "macros/lang-attributes.ftl">

<#assign variables = hstRequestContext.getAttribute("variables")/>
<@hst.messagesReplace escapeMessageXml=false bundle=variables variablePrefix="[[" variableSuffix="]]">

<@hst.link var="link" canonical=true fullyQualified=true/>

<#if publication??>
<div class="cms-editable">
    <@hst.manageContent hippobean=document />

    <@hst.include ref="breadcrumbs"/>

    <div class="ds_wrapper">
        <main <@lang document/> id="main-content" class="ds_layout  ds_layout--pl-component" >

            <@hst.include ref="sidebar"/>

            <div class="ds_layout__header">
                <header class="ds_page-header">
                    <span <@revertlang document /> class="ds_page-header__label  ds_content-label">
                    ${publication.title}
                    </span>
                    <h1 class="ds_page-header__title">
                    <#if document.title?has_content>${document.title}</#if>
                    </h1>
                    <dl <@revertlang document /> class="ds_page-header__metadata  ds_metadata">
                        <#if publication.publicationDate?has_content>
                            <div class="ds_metadata__item">
                                <dt class="ds_metadata__key">Published</dt>
                                <dd class="ds_metadata__value"><@fmt.formatDate value=publication.publicationDate.time type="both" pattern="d MMM yyyy"/></dd>
                            </div>
                        </#if>
                        <#if topics?has_content>
                            <div class="ds_metadata__item">
                                <dt class="ds_metadata__key">Topic</dt>
                                <dd class="ds_metadata__value">
                                <#list topics as topic>
                                    <span class="sg-meta__topic">${topicsMap[topic]}</span><#sep>, </#sep>
                                </#list></dd>
                            </div>
                        </#if>
                    </dl>
                </header>
            </div>

            <div class="ds_layout__content">

                <#if document.contentBlocks??>
                    <@renderContentBlocks document.contentBlocks />
                </#if>

                <nav class="ds_sequential-nav" aria-label="Article navigation">
                    <#if prev??>
                        <div class="ds_sequential-nav__item  ds_sequential-nav__item--prev">
                            <a href="<@hst.link link=prev.link/>" class="ds_sequential-nav__button  ds_sequential-nav__button--left" data-navigation="sequential-previous">
                                <span class="ds_sequential-nav__text" data-label="previous">
                                    ${prev.title}
                                </span>
                            </a>
                        </div>
                    </#if>

                    <#if next??>
                        <div class="ds_sequential-nav__item  ds_sequential-nav__item--next">
                            <a href="<@hst.link link=next.link/>" class="ds_sequential-nav__button  ds_sequential-nav__button--right" data-navigation="sequential-next">
                                <span class="ds_sequential-nav__text" data-label="next">
                                    ${next.title}
                                </span>
                            </a>
                        </div>
                    </#if>
                </nav>
            </div>

            <@hst.include ref="feedback"/>

        </main>
    </div>
</div>

<@hst.headContribution category="meta">
    <meta name="dc.format" content="Publication"/>
</@hst.headContribution>

<@hst.headContribution category="schema">
    <script type="application/ld+json">
{
    "@context": "https://schema.org",
    "@type": "Article",
    "mainEntityOfPage": {
        "@type": "WebPage",
        "@id": "${link}"
    },
    "author": {
        "@type": "Organization",
        "name": "${logoalttext}",
        "url": "${orgurl}",
        "logo": {
            "@type": "ImageObject",
            "url": "<@hst.link path="/assets/images/logos/${logo}.png" fullyQualified=true />"
        }
    },
    "name": "${document.title?json_string}",
    <#if document.publicationDate??>"datePublished": "${document.publicationDate.time?datetime?iso_local}",</#if>
    <#if document.metaDescription??>"description": "${document.metaDescription?json_string}",</#if>
    "publisher": {
        "@type": "Organization",
        "name": "${logoalttext}",
        "url": "${orgurl}",
        "logo": {
            "@type": "ImageObject",
            "url": "<@hst.link path="/assets/images/logos/${logo}.png" fullyQualified=true />"
        }
    }
}
    </script>
</@hst.headContribution>

</#if>
</@hst.messagesReplace>



<#assign scriptName="article">
<#include 'scripts.ftl'/>