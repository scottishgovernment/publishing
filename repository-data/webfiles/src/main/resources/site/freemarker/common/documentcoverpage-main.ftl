<#ftl output_format="HTML">
<#include "include/imports.ftl">
<#include "macros/content-blocks.ftl">
<#include "macros/format-file-size.ftl">
<#include "macros/lang-attributes.ftl">

<#if document??>
<#assign variables = hstRequestContext.getAttribute("variables")/>
<@hst.messagesReplace escapeMessageXml=false bundle=variables variablePrefix="[[" variableSuffix="]]">

<div class="cms-editable">
    <@hst.manageContent hippobean=document />

    <@hst.include ref="breadcrumbs"/>

    <div class="ds_wrapper">
        <main <@lang document/> id="main-content" class="ds_layout  ds_layout--article">
            <#if document.sensitive?? && document.sensitive>
                <!--noindex-->
                <#include "hide-this-page.ftl">
                <!--endnoindex-->
            </#if>

            <div class="ds_layout__header">
                <header class="ds_page-header">
                    <h1 class="ds_page-header__title">${document.title}</h1>
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

            <div class="ds_layout__content">

                <div class="body-content">
                    <#if document.contentBlocks??>
                        <@renderContentBlocks document.contentBlocks />
                    </#if>
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
                        <#case "odf">
                            <#assign fileDescription = "OpenDocument Formula" />
                            <#assign fileThumbnailPath = '/assets/images/documents/svg/odf.svg' />
                            <#break>
                        <#case "odg">
                            <#assign fileDescription = "OpenDocument Graphics" />
                            <#assign fileThumbnailPath = '/assets/images/documents/svg/odg.svg' />
                            <#break>
                        <#case "odp">
                            <#assign fileDescription = "OpenDocument Presentation" />
                            <#assign fileThumbnailPath = '/assets/images/documents/svg/odp.svg' />
                            <#break>
                        <#case "ods">
                            <#assign fileDescription = "OpenDocument Spreadsheet" />
                            <#assign fileThumbnailPath = '/assets/images/documents/svg/ods.svg' />
                            <#break>
                        <#case "odt">
                            <#assign fileDescription = "OpenDocument Text" />
                            <#assign fileThumbnailPath = '/assets/images/documents/svg/odt.svg' />
                            <#break>
                        <#default>
                            <#assign fileDescription = "${filenameExtension} file" />
                            <#assign fileThumbnailPath = '/assets/images/documents/svg/generic.svg' />
                    </#switch>

                    <#if doc.noindex?? && doc.noindex>
                    <!--noindex-->
                    </#if>

                    <div class="ds_file-download  <#if doc.highlight?? && doc.highlight>ds_file-download--highlighted</#if>">
                        <div class="ds_file-download__thumbnail">
                            <a data-button="document-cover" class="ds_file-download__thumbnail-link" aria-hidden="true" tabindex="-1" href="${documentinline}">
                                <#if filenameExtension == "PDF" && !doc.useGenericIcon && doc.thumbnails?has_content>
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
                                    <img class="ds_file-download__thumbnail-image"
                                        src="<@hst.link path=fileThumbnailPath />"
                                        loading="lazy"
                                        alt="" />
                                </#if>
                            </a>
                        </div>

                        <div class="ds_file-download__content">
                            <a href="${documentinline}" class="ds_file-download__title" id="file-title-${doc?counter}" aria-describedby="file-download-${doc?counter}">${doc.title}</a>

                            <div <@revertlang document /> id="file-download-${doc?counter}" class="ds_file-download__details">
                                <dl class="ds_metadata  ds_metadata--inline">
                                    <div class="ds_metadata__item">
                                        <dt class="ds_metadata__key">File type</dt>
                                        <dd class="ds_metadata__value"><#if doc.pageCount?? && doc.pageCount != 0 >${doc.pageCount} page </#if>${fileDescription}<span class="visually-hidden">,</span></dd>
                                    </div>

                                    <div class="ds_metadata__item">
                                        <dt class="ds_metadata__key">File size</dt>
                                        <dd class="ds_metadata__value"><@formatFileSize document=doc/></dd>
                                    </div>
                                </dl>
                            </div>
                        </div>
                    </div>
                    <#if doc.noindex?? && doc.noindex>
                    <!--endnoindex-->
                    </#if>
                </#list>
            </div>

            <@hst.include ref="sidebar"/>

            <@hst.include ref="feedback"/>
        </main>
    </div>
</div>
</@hst.messagesReplace>

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
