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
        <main <@lang document/> id="main-content" class="ds_layout  mg_layout--manual-landing">
            <div class="ds_layout__header">
                <header class="ds_page-header">
                    <span <@revertlang document /> class="ds_page-header__label  ds_content-label">
                        Publication<#if document.publicationType?has_content> - <span id="sg-meta__publication-type">${document.publicationType}</span></#if></span>
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
                <#if publication.summary?has_content>
                    <div>
                        <p class="ds_leader  ds_!_margin-bottom--4">${publication.summary}</p>

                        <#if documents??>
                            <a href="<@hst.link hippobean=documents/>" class="ds_button  ds_button--small  ds_button--secondary  ds_button--has-icon">
                                ${documents.title}
                                <span class="org-az__link-icon" aria-hidden="true"></span>
                            </a>
                        </#if>
                    </div>
                </#if>
            </div>

            <div class="ds_layout__separator">
                <hr />
            </div>

            <div class="ds_layout__content">
                <h2 class="ds_!_margin-top--5">Pages in this publication:</h2>

                <div class="ds_contents-nav">
                    <ul class="ds_contents-nav__list">
                        <#list navigation.children as child>
                            <li class="ds_contents-nav__item">
                                <div class="ds_contents-nav__section-title">
                                    <a href="<@hst.link link=child.link/>">
                                        ${child.title}
                                    </a>
                                </div>

                                <#if child.children?size gt 0>
                                    <ul class="ds_contents-nav__list  ds_contents-nav__list--indent">
                                        <#list child.children as grandchild>
                                            <li class="ds_contents-nav__item">
                                                <a href="<@hst.link link=grandchild.link/>">
                                                    ${grandchild.title}
                                                </a>

                                                <#if grandchild.children?size gt 0>
                                                    <ul class="ds_contents-nav__list  ds_contents-nav__list--indent">
                                                        <#list grandchild.children as greatgrandchild>
                                                            <li class="ds_contents-nav__item">
                                                                <a  href="<@hst.link link=greatgrandchild.link/>">
                                                                    ${greatgrandchild.title}
                                                                </a>
                                                            </li>
                                                        </#list>
                                                    </ul>
                                                </#if>
                                            </li>
                                        </#list>
                                    </ul>
                                </#if>
                            </li>
                        </#list>
                    </ul>
                </div>

                <#if document.contentBlocks??>
                    <@renderContentBlocks document.contentBlocks />
                </#if>
            </div>

            <@hst.include ref="sidebar"/>
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
