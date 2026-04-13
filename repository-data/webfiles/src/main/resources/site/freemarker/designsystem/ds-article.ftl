<#ftl output_format="HTML">
<#include "../common/include/imports.ftl">
<#include "../common/macros/content-blocks.ftl">
<#include "../common/macros/lang-attributes.ftl">


<#if document??>
<#assign variables = hstRequestContext.getAttribute("variables")/>
<@hst.messagesReplace escapeMessageXml=false bundle=variables variablePrefix="[[" variableSuffix="]]">

<div class="cms-editable">
    <@hst.manageContent hippobean=document />

    <@hst.include ref="breadcrumbs"/>

    <#assign accordionCount = 0 />

    <div class="ds_wrapper">
        <main <@lang document/> id="main-content" class="ds_layout  ds_layout--pl-component">

            <@hst.include ref="sidebar"/>

            <div class="ds_layout__header">
                <header class="ds_page-header">
                    <#if type?has_content>
                    <#if isNew><strong class="ds_page-header__tag ds_tag">New</strong></#if><span class="ds_page-header__label  ds_content-label">${type}</span>
                    </#if>
                    <h1 class="ds_page-header__title"><#if document.title??>${document.title}</#if></h1>
                    <#include "./metadata.ftl"/>
                </header>
            </div>

            <#if document.logo??>
                <div <@revertlang document />  class="ds_layout__partner  mg_partner-logo">
                    <#if document.logo.xlargefourcolumns??>
                        <img alt="" src="<@hst.link hippobean=document.logo.xlargefourcolumns />"
                            loading="lazy"
                            srcset="
                            <@hst.link hippobean=document.logo.smallcolumns/> 448w,
                            <@hst.link hippobean=document.logo.smallcolumnsdoubled/> 896w,
                            <@hst.link hippobean=document.logo.mediumfourcolumns/> 224w,
                            <@hst.link hippobean=document.logo.mediumfourcolumnsdoubled/> 448w,
                            <@hst.link hippobean=document.logo.largefourcolumns/> 288w,
                            <@hst.link hippobean=document.logo.largefourcolumnsdoubled/> 576w,
                            <@hst.link hippobean=document.logo.xlargefourcolumns/> 352w,
                            <@hst.link hippobean=document.logo.xlargefourcolumnsdoubled/> 704w"
                            sizes="(min-width:1200px) 352px, (min-width:992px) 288px, (min-width: 768px) 224px, 448px"
                            >
                    <#else>
                        <img loading="lazy" alt="" src="<@hst.link hippobean=document.logo/>" />
                    </#if>
                </div>
            </#if>

            <div class="ds_layout__content" data-prismjs-copy="Copy code" data-prismjs-copy-success="Copied to clipboard" data-prismjs-copy-error="Unable to copy">
                <#if document.deprecated>
                    <div class="ds_warning-text">
                        <strong class="ds_warning-text__icon" aria-hidden="true"></strong>
                        <strong class="visually-hidden">Warning</strong>
                        <strong class="ds_tag">Deprecated</strong>
                        <#if document.deprecatednote?has_content>
                            <@hst.html hippohtml=document.deprecatednote/>
                        <#else>
                            <p>This component has been deprecated. Use the <a href="/components/feature-header/" data-navigation="warning-link">'feature header' component</a> instead.</p>
                        </#if>
                    </div>
                </#if>

                <#if document.experimental>
                    <div class="ds_inset-text">
                        <div class="ds_inset-text__text">
                            <strong class="ds_tag">Experimental</strong>
                            <p>This is currently experimental because we need more research to validate it.</p>
                        </div>
                    </div>
                </#if>

                <#if document.summary?has_content>
                    <div class="ds_leader">
                        ${document.summary}
                    </div>
                </#if>

                <#if document.contentblocks??>

                    <#list document.contentblocks as contentBlock>

                        <#if hst.isNodeType(contentBlock.node, 'publishing:dsexampleblock')>
                            <!-- example block -->

                            <@hst.link var="example" hippobean=contentBlock.example/>
                            <div class="mg_example  overflow--large--2 overflow--xlarge--2">
                                <div class="mg_example__preview  mg_preview">
                                    <@hst.link var="example" hippobean=contentBlock.codepreview.document/>

                                    <#if contentBlock.example.illustration??>
                                        <img class="mg_preview__illustration" alt="<#if contentBlock.example.alt?has_content>${contentBlock.example.alt}</#if>" src="<@hst.link hippobean=contentBlock.example.illustration.original/>">
                                    <#elseif contentBlock.showdemo>
                                        <div class="mg_preview__link">
                                            <a href="${example}" target="_blank">Open this example in a new window</a>
                                        </div>

                                        <div class="mg_preview__frame">
                                            <iframe title="${contentBlock.codepreview.document.title}" <#if contentBlock.minheight??>style="min-height: ${contentBlock.minheight}px;"</#if> src="${example}" class="mg_preview__iframe">
                                            </iframe>
                                        </div>
                                    </#if>
                                </div>

                                <#if contentBlock.showcode>
                                    <div class="mg_example__code  mg_code">
                                        <details class="mg_code__details" <#if contentBlock.htmlexpanded>open</#if>>
                                            <summary class="mg_code__summary  ds_link">
                                                ${contentBlock.type}
                                            </summary>
                                            <div class="mg_code__details-content">
                                                <pre class="mg_code__pre" tabindex="0"><code class="language-${contentBlock.example.language?lower_case}" tabindex="-1">${contentBlock.example.code}</code></pre>
                                            </div>
                                        </details>
                                    </div>
                                </#if>
                            </div>
                            <!-- end example block -->
                        </#if>

                        <#if hst.isNodeType(contentBlock.node, 'publishing:dscontentblock')>
                            <!-- content block -->
                            <@hst.html hippohtml=contentBlock.content/>
                            <!-- end content block -->
                        </#if>

                        <#if hst.isNodeType(contentBlock.node, 'publishing:dsfigureblock')>
                            <!-- figure block -->
                            <figure class="<#if contentBlock.overflow>overflow--large--2  overflow--xlarge--2</#if>">
                                <img alt="${contentBlock.alt}" src="<@hst.link hippobean=contentBlock.image/>" loading="lazy">
                                <#if contentBlock.caption?has_content>
                                    <figcaption>${contentBlock.caption}</figcaption>
                                </#if>
                            </figure>
                            <!-- end figure block -->
                        </#if>

                        <#if hst.isNodeType(contentBlock.node, 'publishing:dsattachmentblock') || hst.isNodeType(contentBlock.node, 'publishing:cb_fileDownload')>
                            <!-- attachment block -->

                            <#if contentBlock.external?has_content>
                                <#assign link = contentBlock.external>
                            <#else>
                                <@hst.link var="link2" hippobean=contentBlock.internal />
                                <#assign link = link2/>
                            </#if>

                            <#if contentBlock.noindex?? && contentBlock.noindex>
                            <!--noindex-->
                            </#if>

                            <div class="ds_file-download <#if contentBlock.highlight> ds_file-download--highlighted</#if>">
                                <div class="ds_file-download__thumbnail">
                                    <a data-button="document-cover" class="ds_file-download__thumbnail-link" aria-hidden="true" tabindex="-1" href="${link}">
                                        <#if contentBlock.image??>
                                            <@hst.link var="icon" hippobean=contentBlock.image.original />
                                            <img class="ds_file-download__thumbnail-image" src="${icon}" alt="">
                                        <#else>
                                            <#assign fileThumbnailPath = '/assets/images/documents/svg/' + contentBlock.type + '.svg' />
                                            <#switch contentBlock.icon?trim?lower_case>
                                            <#case "csv">
                                                <#assign fileThumbnailPath = '/assets/images/documents/svg/csv.svg' />
                                                <#break>
                                            <#case "excel">
                                                <#assign fileThumbnailPath = '/assets/images/documents/svg/excel.svg' />
                                                <#break>
                                            <#case "geodata">
                                                <#assign fileThumbnailPath = '/assets/images/documents/svg/geodata.svg' />
                                                <#break>
                                            <#case "image">
                                                <#assign fileThumbnailPath = '/assets/images/documents/svg/image.svg' />
                                                <#break>
                                            <#case "odf">
                                                <#assign fileThumbnailPath = '/assets/images/documents/svg/odf.svg' />
                                                <#break>
                                            <#case "odg">
                                                <#assign fileThumbnailPath = '/assets/images/documents/svg/odg.svg' />
                                                <#break>
                                            <#case "odp">
                                                <#assign fileThumbnailPath = '/assets/images/documents/svg/odp.svg' />
                                                <#break>
                                            <#case "ods">
                                                <#assign fileThumbnailPath = '/assets/images/documents/svg/ods.svg' />
                                                <#break>
                                            <#case "odt">
                                                <#assign fileThumbnailPath = '/assets/images/documents/svg/odt.svg' />
                                                <#break>
                                            <#case "pdf">
                                                <#assign fileThumbnailPath = '/assets/images/documents/svg/pdf.svg' />
                                                <#break>
                                            <#case "ppt">
                                                <#assign fileThumbnailPath = '/assets/images/documents/svg/ppt.svg' />
                                                <#break>
                                            <#case "rtf">
                                                <#assign fileThumbnailPath = '/assets/images/documents/svg/rtf.svg' />
                                                <#break>
                                            <#case "text">
                                                <#assign fileThumbnailPath = '/assets/images/documents/svg/text.svg' />
                                                <#break>
                                            <#case "word">
                                                <#assign fileThumbnailPath = '/assets/images/documents/svg/word.svg' />
                                                <#break>
                                            <#case "xml">
                                                <#assign fileThumbnailPath = '/assets/images/documents/svg/xml.svg' />
                                                <#break>
                                            <#default>
                                                <#assign fileThumbnailPath = '/assets/images/documents/svg/generic.svg' />
                                        </#switch>
                                            <img class="ds_file-download__thumbnail-image" src="<@hst.link path=fileThumbnailPath />" alt="" />
                                        </#if>
                                    </a>
                                </div>

                                <div class="ds_file-download__content">
                                    <a href="${link}" aria-describedby="file-download-${contentBlock?keep_after("@")}" class="ds_file-download__title">${contentBlock.title}</a>

                                    <div id="file-download-${contentBlock?keep_after("@")}" class="ds_file-download__details">
                                        <dl class="ds_metadata  ds_metadata--inline">
                                            <div class="ds_metadata__item">
                                                <dt class="ds_metadata__key">File type</dt>
                                                <dd class="ds_metadata__value">${contentBlock.type}<span class="visually-hidden">,</span></dd>
                                            </div>


                                            <div class="ds_metadata__item">
                                                <dt class="ds_metadata__key">File size</dt>
                                                <dd class="ds_metadata__value">${contentBlock.size}</dd>
                                            </div>

                                        </dl>
                                    </div>
                                </div>
                            </div>


                            <#if contentBlock.noindex?? && contentBlock.noindex>
                            <!--endnoindex-->
                            </#if>
                            <!-- end attachment block -->
                        </#if>

                        <#if hst.isNodeType(contentBlock.node, 'publishing:dspaletteblock')>
                            <!-- palette block -->
                            <${contentBlock.headinglevel}>${contentBlock.title}</${contentBlock.headinglevel}>

                            <table class="dss_palette">
                                <thead class="visually-hidden">
                                    <tr>
                                        <th>Colour</th>
                                        <th>SCSS variable</th>
                                        <th>Hex code</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <#list contentBlock.paletteitems as item>
                                        <tr>
                                            <td class="dss_palette__colour">
                                                <svg class="dss_palette__swatch" viewBox="0 0 40 40" xmlns="http://www.w3.org/2000/svg">
                                                    <circle fill="${item.code}" cx="20" cy="20" r="20"></circle>
                                                </svg>
                                                <span class="visually-hidden">${item.colourname}</span>
                                            </td>
                                            <td class="dss_palette__name">
                                                <code>${item.varname}</code>
                                            </td>
                                            <td class="dss_palette__hex"><code>${item.code}</code></td>
                                        </tr>
                                    </#list>

                                </tbody>
                            </table>
                            <!-- end palette block -->
                        </#if>

                        <#if hst.isNodeType(contentBlock.node, 'publishing:cb_fragment')>
                            <#-- fragment block -->
                            <#if contentBlock.noindex?? && contentBlock.noindex>
                            <!--noindex-->
                            </#if>
                            <@renderContentBlocks contentBlock.fragment.contentBlocks />
                            <#if contentBlock.noindex?? && contentBlock.noindex>
                            <!--endnoindex-->
                            </#if>
                            <#-- end fragment block -->
                        </#if>
                    </#list>
                </#if>

                <#if document.updateHistory?has_content>
                    <div class="update-history">
                        <#include './ds-update-history.ftl' />
                    </div>
                </#if>

            </div>

            <@hst.include ref="feedback"/>

        </main>
    </div>
</div>
</@hst.messagesReplace>

<@hst.headContribution category="meta">
    <meta name="dc.format" content="${format}"/>
</@hst.headContribution>

<#if seriesLink??>
    <@hst.headContribution category="meta">
        <@hst.messagesReplace escapeMessageXml=false bundle=variables variablePrefix="[[" variableSuffix="]]">
        <meta name="dc.title.series" content="${seriesLink.title}"/>
        </@hst.messagesReplace>
    </@hst.headContribution>

    <@hst.headContribution category="meta">
        <meta name="dc.title.series.link" content="<@hst.link hippobean=seriesLink/>"/>
    </@hst.headContribution>
</#if>

<@hst.headContribution category="meta">
    <meta name="dc.date.modified" content="<@fmt.formatDate value=date type="both" pattern="YYYY-MM-dd HH:mm"/>"/>
</@hst.headContribution>
</#if>



<@hst.headContribution category="resourcehints">
    <#if nextlink??>
        <link rel="prerender" href="${nextlink}"/>
    </#if>
</@hst.headContribution>



<#assign scriptName="ds-article">
<#include '../common/scripts.ftl'/>
