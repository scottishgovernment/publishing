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
        <div <@lang document/> class="ds_layout  mg_layout--manual" >
            <div class="ds_layout__header">
                <div class="ds_page-header  ds_!_margin-bottom--4">
                    <span <@revertlang document /> class="ds_page-header__label  ds_content-label">
                        Publication<#if publication.label?has_content> - <span id="sg-meta__publication-type">${publication.label}</span></#if></span>
                    </span>
                    <h2 id="parentDocumentTitle" class="ds_page-header__title  ds_h3">
                        <span class="visually-hidden">Part of</span> <#if publication.title?has_content>${publication.title}</#if>
                    </h2>
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
                </div>
            </div>

            <div class="ds_layout__separator">
                <hr />
            </div>

            <@hst.include ref="sidebar"/>

            <main id="main-content" class="ds_layout__content">
                <header class="ds_page-header  ds_!_margin-top--3">
                    <#if parentTitle?has_content><span class="ds_page-header__label  ds_content-label">${parentTitle}</span></#if>
                    <h1 class="ds_page-header__title" aria-describedby="parentDocumentTitle">
                        <#if document.title?has_content>${document.title}</#if>
                    </h1>
                </header>

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
                    <#else>
                        <div class="ds_sequential-nav__item  ds_sequential-nav__item--prev">
                            <a href="<@hst.link hippobean=publication/>" class="ds_sequential-nav__button  ds_sequential-nav__button--left" data-navigation="sequential-previous">
                                <span class="ds_sequential-nav__text" data-label="previous">
                                    ${publication.title}
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

                <#if publication.relatedItems>
                    <aside <@revertlang document /> class="ds_article-aside">
                        <h2 class="gamma">Related content</h2>
                        <ul class="ds_no-bullets">
                            <#list publication.relateditems as item>
                                <#list item.relatedItem as link>
                                    <@hst.link var="url" hippobean=link/>
                                    <li>
                                        <a <@langcompare link document/> href="${url}">${link.title}</a>
                                    </li>
                                </#list>
                            </#list>
                        </ul>
                    </aside>
                </#if>
            </main>

            <@hst.include ref="feedback"/>
        </div>
    </div>
</div>

<@hst.headContribution category="meta">
    <meta name="dc.format" content="Publication"/>
</@hst.headContribution>

<@hst.headContribution category="meta">
    <@hst.messagesReplace escapeMessageXml=false bundle=variables variablePrefix="[[" variableSuffix="]]">
    <meta name="dc.title.series" content="${publication.title}"/>
    </@hst.messagesReplace>
</@hst.headContribution>
<@hst.headContribution category="meta">
    <@hst.messagesReplace escapeMessageXml=false bundle=variables variablePrefix="[[" variableSuffix="]]">
    <meta name="dc.title.series.link" content="<@hst.link hippobean=publication/>"/>
    </@hst.messagesReplace>
</@hst.headContribution>
<#if publication.publicationDate?has_content>
    <@hst.headContribution category="meta">
    <meta name="dc.date.modified" content="<@fmt.formatDate value=publication.publicationDate.time type="both" pattern="YYYY-MM-dd HH:mm"/>"/>
    </@hst.headContribution>
</#if>

<@hst.headContribution category="schema">
    <script type="application/ld+json" nonce="${nonce}">
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
