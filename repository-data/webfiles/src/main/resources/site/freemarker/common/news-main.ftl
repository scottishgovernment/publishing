<#ftl output_format="HTML">
<#include "include/imports.ftl">
<#include "macros/content-blocks.ftl">
<#include "macros/lang-attributes.ftl">

<#assign variables = hstRequestContext.getAttribute("variables")/>
<@hst.messagesReplace escapeMessageXml=false bundle=variables variablePrefix="[[" variableSuffix="]]">

<@hst.link var="link" canonical=true fullyQualified=true/>

<#if document??>
<div class="cms-editable">
    <@hst.manageContent hippobean=document />

    <@hst.include ref="breadcrumbs"/>

    <div class="ds_wrapper">
        <main <@lang document/> id="main-content" class="ds_layout  ds_layout--article">

            <div class="ds_layout__header">
                <header class="ds_page-header">
                    <h1 class="ds_page-header__title"><#if document.title?has_content>${document.title}</#if></h1>
                    <dl <@revertlang document /> class="ds_page-header__metadata  ds_metadata">
                        <#if document.publicationDate?has_content>
                            <div class="ds_metadata__item">
                                <dt class="ds_metadata__key">Published</dt>
                                <dd class="ds_metadata__value"><@fmt.formatDate value=document.publicationDate.time type="both" pattern="d MMM yyyy"/></dd>
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
                <#if document.summary?has_content>
                <div class="ds_leader-first-paragraph">
                    <p>${document.summary}</p>
                </div>
        <#if document.image??>
            <#if document.image.image?has_content>
                <figure class="dp_image">
                <#if document.image.image.xlargeeightcolumnsdoubled??>
                    <img alt="${document.image.alt}" src="<@hst.link hippobean=document.image.image.xlargeeightcolumns />"
                        loading="lazy"
                        width="${document.image.image.xlargeeightcolumns.width?c}"
                        height="${document.image.image.xlargeeightcolumns.height?c}"
                        srcset="
                        <@hst.link hippobean=document.image.image.smallcolumns/> 448w,
                        <@hst.link hippobean=document.image.image.smallcolumnsdoubled/> 896w,
                        <@hst.link hippobean=document.image.image.mediumeightcolumns/> 480w,
                        <@hst.link hippobean=document.image.image.mediumeightcolumnsdoubled/> 960w,
                        <@hst.link hippobean=document.image.image.largeeightcolumns/> 608w,
                        <@hst.link hippobean=document.image.image.largeeightcolumnsdoubled/> 1216w,
                        <@hst.link hippobean=document.image.image.xlargeeightcolumns/> 736w,
                        <@hst.link hippobean=document.image.image.xlargeeightcolumnsdoubled/> 1472w"
                        sizes="(min-width:1200px) 736px, (min-width:992px) 608px, (min-width: 768px) 480px, 448px"
                        >
                <#else>
                    <img loading="lazy" alt="${document.image.alt}" src="<@hst.link hippobean=document.image.image/>">
                </#if>
                <#if document.image.caption?has_content || document.image.credit?has_content>
                    <figcaption class="dp_image__caption">
                    <#if document.image.caption?has_content>${(document.image.caption)?ensure_ends_with(".")} </#if>
                    <#if document.image.credit?has_content>Credit: ${document.image.credit}</#if>
                    </figcaption>
                </figure>
                </#if>
            </#if>
        </#if>
            </div>

            <div class="ds_layout__content">
                </#if>
                <#if document.contentBlocks??>
                    <@renderContentBlocks document.contentBlocks />
                </#if>

                
            </div>

            <#include 'feedback-wrapper.ftl'>

        </main>
    </div>
</div>

<@hst.headContribution category="meta">
    <meta name="dc.format" content="News"/>
</@hst.headContribution>

<@hst.headContribution category="schema">
    <script type="application/ld+json">
{
    "@context": "https://schema.org",
    "@type": "NewsArticle",
    <#if image??>"image": "<@hst.link hippobean=image.original fullyQualified=true />",</#if>
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
    "headline": "${document.title?json_string}",
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