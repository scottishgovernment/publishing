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
                    DOCUMENT
                </header>
            </div>



            <div class="ds_layout__content">
                <#list document.documents as doc>
                    <div>
                        <p>
                            <a href="<@hst.link hippobean=doc.document/>"
                                ${doc.title}
                            </a>
                        </p>
                        <p>
                            ${doc.document.filename}
                            <img
                                alt="View this document"
                                class="document-info__thumbnail-image"
                                src="<@hst.link hippobean=doc.thumbnails[0]/>"
                                srcset="
                            <#list doc.thumbnails as thumbnail>
                                <@hst.link hippobean=thumbnail/> ${thumbnail.filename?keep_before_last(".")?keep_after_last("_")}w<#sep>, </#sep>
                            </#list>"
                            sizes="(min-width: 768px) 165px, 107px" />
                        </p>
                    </div>
                </#list>
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
