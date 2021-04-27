<#ftl output_format="HTML">
<#include "include/imports.ftl">
<#include "macros/format-file-size.ftl">

<#if document??>
<div class="cms-editable">
    <@hst.manageContent hippobean=document />

    <@hst.include ref="breadcrumbs"/>

    <div class="ds_wrapper">
        <main id="main-content" class="ds_layout  ds_layout--article">

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

            <div class="ds_layout__content">
                <div class="body-content">
                    <@hst.html hippohtml=document.content/>
                </div>

                <#list document.documents as doc>
                    <#assign filenameExtension = doc.document.filename?keep_after_last(".")?upper_case/>

                    <@hst.link var="documentdownload" hippobean=doc.document>
                        <@hst.param name="forceDownload" value="true"/>
                    </@hst.link>

                    <@hst.link var="documentinline" hippobean=doc.document>
                    </@hst.link>

                    <div class="document-info  document-info--limelight">
                        <div class="document-info__body">
                            <div class="document-info__thumbnail  document-info__thumbnail--${filenameExtension}">

                                <#if filenameExtension == "PDF">
                                    <a data-title="${doc.title}" title="View this document" class="document-info__thumbnail-link" href="${documentinline}">
                                        <img
                                                alt="View this document"
                                                class="document-info__thumbnail-image"
                                                src="<@hst.link hippobean=doc.thumbnails[0]/>"
                                                srcset="
                                                    <#list doc.thumbnails as thumbnail>
                                                        <@hst.link hippobean=thumbnail/> ${thumbnail.filename?keep_before_last(".")?keep_after_last("_")}w<#sep>, </#sep>
                                                    </#list>"
                                                sizes="(min-width: 768px) 165px, 107px" />
                                    </a>
                                <#else>
                                    <a data-title="${mainDocument.title}" title="View this document" href="${documentinline}" class="file-icon--large file-icon file-icon--${filenameExtension}"></a>
                                </#if>
                            </div>
                        </div>


                        <div class="document-info__text">
                            <h2 class="gamma  document-info__title"><a class="no-icon" href="${documentinline}" onclick="saAutomatedLink(this, &quot;download&quot;); return false;">${doc.title}</a></h2>
                            <dl class="ds_metadata  ds_metadata--inline">
                                <div class="ds_metadata__item">
                                    <dt class="ds_metadata__key  visually-hidden">File type</dt>
                                    <dd class="ds_metadata__value"><b><#if doc.pageCount != 0 >${doc.pageCount} page </#if>${filenameExtension}</b></dd>
                                </div>

                                <div class="ds_metadata__item">
                                    <dt class="ds_metadata__key  visually-hidden">File size</dt>
                                    <dd class="ds_metadata__value"><@formatFileSize document=doc/></dd>
                                </div>
                            </dl>
                            <div class="document-info__download">
                                <a href="${documentdownload}" class="ds_button  ds_no-margin" onclick="saAutomatedLink(this, &quot;download&quot;); return false;">
                                    Download <span class="visually-hidden">${filenameExtension}</span>
                                </a>
                            </div>
                        </div>
                    </div>
                </#list>
            </div>

            <#if document.relateditems?has_content >
                <aside class="ds_layout__sidebar">
                    <aside class="ds_article-aside">
                        <h2 class="gamma">Related content</h2>
                        <ul class="ds_no-bullets">
                            <#list document.relateditems as item>
                                <#list item.relatedItem as link>
                                    <@hst.link var="url" hippobean=link/>
                                    <li>
                                        <a href="${url}">${link.title}</a>
                                    </li>
                                </#list>
                            </#list>
                        </ul>
                    </aside>
                </aside>
            </#if>

            <#include 'feedback-wrapper.ftl'>
        </main>
    </div>
</div>
</#if>

<@hst.headContribution category="resourcehints">
    <#if nextlink??>
    <link rel="prerender" href="${nextlink}"/>
    </#if>
</@hst.headContribution>

<@hst.headContribution category="meta">
    <#if document??>
    <meta name="description" content="${document.metaDescription}"/>
    </#if>
</@hst.headContribution>

<#assign scriptName="article">
<#include 'scripts.ftl'/>
