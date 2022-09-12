<#ftl output_format="HTML">
<#include "include/imports.ftl">
<#include "macros/format-file-size.ftl">

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

                    <#switch filenameExtension?lower_case>
                        <#case "csv">
                            <#assign fileDescription = "CSV file" />
                            <#assign fileThumbnailPath = '/assets/images/documents/svg/csv.svg' />
                            <#break>
                        <#case "xls">
                        <#case "xlsx">
                        <#case "xlsm">
                            <#assign fileDescription = "Excel document" />
                            <#assign fileThumbnailPath = '/assets/images/documents/svg/excel.svg' />
                            <#break>
                        <#case "kml">
                        <#case "kmz">
                            <#assign fileDescription = "${filenameExtension} data" />
                            <#assign fileThumbnailPath = '/assets/images/documents/svg/geodata.svg' />
                            <#break>
                        <#case "gif">
                        <#case "jpg">
                        <#case "jpeg">
                        <#case "png">
                        <#case "svg">
                            <#assign fileDescription = "Image" />
                            <#assign fileThumbnailPath = '/assets/images/documents/svg/image.svg' />
                            <#break>
                        <#case "pdf">
                            <#assign fileDescription = "PDF" />
                            <#assign fileThumbnailPath = '/assets/images/documents/svg/pdf.svg' />
                            <#break>
                        <#case "ppt">
                        <#case "pptx">
                        <#case "pps">
                        <#case "ppsx">
                            <#assign fileDescription = "Powerpoint document" />
                            <#assign fileThumbnailPath = '/assets/images/documents/svg/ppt.svg' />
                            <#break>
                        <#case "rtf">
                            <#assign fileDescription = "Rich text file" />
                            <#assign fileThumbnailPath = '/assets/images/documents/svg/rtf.svg' />
                            <#break>
                        <#case "txt">
                            <#assign fileDescription = "Text file" />
                            <#assign fileThumbnailPath = '/assets/images/documents/svg/txt.svg' />
                            <#break>
                        <#case "doc">
                        <#case "docx">
                            <#assign fileDescription = "Word document" />
                            <#assign fileThumbnailPath = '/assets/images/documents/svg/word.svg' />
                            <#break>
                        <#case "xml">
                        <#case "xsd">
                            <#assign fileDescription = "${filenameExtension} file" />
                            <#assign fileThumbnailPath = '/assets/images/documents/svg/xml.svg' />
                            <#break>
                        <#default>
                            <#assign fileDescription = "${filenameExtension} file" />
                            <#assign fileThumbnailPath = '/assets/images/documents/svg/generic.svg' />
                    </#switch>

                    <div class="ds_file-download">
                        <div class="ds_file-download__thumbnail">
                            <a data-button="document-cover" class="ds_file-download__thumbnail-link" aria-hidden="true" tabindex="-1" href="${documentinline}">
                                <span class="visually-hidden">Document cover image</span>
                                <#if filenameExtension == "PDF">
                                    <img class="ds_file-download__thumbnail-image"
                                        src="<@hst.link hippobean=doc.thumbnails[0]/>"
                                        loading="lazy"
                                        srcset="
                                            <#list doc.thumbnails as thumbnail>
                                                <@hst.link hippobean=thumbnail/> ${thumbnail.filename?keep_before_last(".")?keep_after_last("_")}w<#sep>, </#sep>
                                            </#list>"
                                        sizes="(min-width: 768px) 104px, 72px"
                                        alt="" />
                                <#else>
                                    <img class="ds_file-download__thumbnail-image  ds_file-download__thumbnail-image--generic"
                                        width="104"
                                        height="152"
                                        src="<@hst.link path=fileThumbnailPath />"
                                        loading="lazy"
                                        alt="" />
                                </#if>
                            </a>
                        </div>

                        <div class="ds_file-download__content">
                            <p class="ds_file-download__title" id="file-title-${doc?counter}">${doc.title}</p>

                            <div id="file-download-${doc?counter}" class="ds_file-download__details">
                                <dl class="ds_metadata  ds_metadata--inline">
                                    <div class="ds_metadata__item">
                                        <dt class="ds_metadata__key  visually-hidden">File type</dt>
                                        <dd class="ds_metadata__value"><#if doc.pageCount?? && doc.pageCount != 0 >${doc.pageCount} page </#if>${fileDescription}<span class="visually-hidden">,</span></dd>
                                    </div>

                                    <div class="ds_metadata__item">
                                        <dt class="ds_metadata__key  visually-hidden">File size</dt>
                                        <dd class="ds_metadata__value"><@formatFileSize document=doc/></dd>
                                    </div>
                                </dl>
                            </div>

                            <div>
                                <a aria-labelledby="file-title-${doc?counter}" aria-describedby="file-download-${doc?counter}" href="${documentinline}" class="ds_file-download__button  ds_button  ds_button--small  ds_button--secondary  ds_no-margin">
                                    Download
                                </a>
                            </div>
                        </div>
                    </div>
                </#list>
            </div>

            <!--noindex-->
            <div class="ds_layout__sidebar">
                <#if document.relateditems?has_content >
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
                </#if>

                <#if document.sensitive?? && document.sensitive>
                    <aside class="ds_article-aside" id="stay-safe-online">
                        <h3>Stay safe online &hellip;</h3>
                        <ul class="ds_no-bullets">
                            <li>
                                <a href="/staying-safe-online/deleting-your-browser-history" data-navigation="staysafe-yes">Deleting your history and staying safe online</a>
                            </li>
                        </ul>
                    </aside>
                </#if>
            </div>
            <!--endnoindex-->

            <#include 'feedback-wrapper.ftl'>
        </main>
    </div>
</div>

<@hst.headContribution category="meta">
    <meta name="dc.format" content="Documents"/>
</@hst.headContribution>

</#if>

<@hst.headContribution category="resourcehints">
    <#if nextlink??>
    <link rel="prerender" href="${nextlink}"/>
    </#if>
</@hst.headContribution>

<#assign scriptName="article">
<#include 'scripts.ftl'/>
