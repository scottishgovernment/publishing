<#ftl output_format="HTML">
<#include "../common/include/imports.ftl">
<#include "../common/macros/lang-attributes.ftl">

<#if document??>
<div class="cms-editable">
    <@hst.manageContent hippobean=document />

    <@hst.include ref="breadcrumbs"/>

    <div class="ds_wrapper">
        <main <@lang document/> id="main-content" class="ds_layout  ds_layout--pl-component">
            <div class="ds_layout__header">
                <header class="ds_page-header">
                    <#if type?has_content>
                        <span class="ds_page-header__label  ds_content-label">
                            <#if isNew><span class="ds_tag">New</span></#if>
                            ${type}
                        </span>
                    </#if>
                    <h1 class="ds_page-header__title"><#if document.title??>${document.title}</#if></h1>
                    <#include "./metadata.ftl"/>
                </header>
            </div>

            <@hst.include ref="sidebar"/>

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

            <div class="ds_layout__content">
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

                    <#list document.contentblocks as contentblock>
                        <#if hst.isNodeType(contentblock.node, 'publishing:dsexampleblock')>
                            <!-- example block -->
                            <@hst.link var="example" hippobean=contentblock.example/>
                            <figure class="example  overflow--large--2 overflow--xlarge--2">
                                <div class="example__content">

                                    <#if contentblock.example.illustration??>
                                        <img class="example__illustration" alt="<#if contentblock.example.alt?has_content>${contentblock.example.alt}</#if>" src="<@hst.link hippobean=contentblock.example.illustration.original/>">
                                    <#elseif contentblock.showdemo>
                                        <div class="example__demo">
                                            <a class="example__link" href="${example}" target="_blank">Open this example in a new window</a>

                                            <iframe title="${contentblock.example.title}" <#if contentblock.minheight??>style="min-height: ${contentblock.minheight}px;"</#if> src="${example}" class="example__iframe">
                                            </iframe>
                                        </div>
                                    </#if>

                                    <#if contentblock.note?has_content>
                                        <p class="example-frame__note">Note: ${contentblock.note}</p>
                                    </#if>

                                    <div class="example__accordion  ds_accordion" data-module="ds-accordion">
                                        <#if contentblock.showcode>
                                            <div class="ds_accordion-item  <#if contentblock.htmlexpanded>ds_accordion-item--open</#if>">
                                                <input type="checkbox" <#if contentblock.htmlexpanded>checked</#if> class="visually-hidden  ds_accordion-item__control" id="panel-main-1" aria-labelledby="panel-main-1-heading">
                                                <div class="ds_accordion-item__header">
                                                    <h3 id="panel-main-1-heading" class="ds_accordion-item__title">
                                                        Sample ${contentblock.type}
                                                    </h3>
                                                    <span class="ds_accordion-item__indicator"></span>
                                                    <label class="ds_accordion-item__label" for="panel-main-1"><span class="visually-hidden">Show this section</span></label>
                                                </div>
                                                <div class="ds_accordion-item__body  example__accordion-body--code">
                                                    <pre class="ds_no-margin  pre--no-border"><code class="language-${contentblock.example.language?lower_case}">${contentblock.example.code}</code></pre>
                                                </div>
                                            </div>
                                        </#if>
                                    </div>
                                </div>
                            </figure>
                            <!-- end example block -->
                        </#if>

                        <#if hst.isNodeType(contentblock.node, 'publishing:dscontentblock')>
                            <!-- content block -->
                            <@hst.html hippohtml=contentblock.content/>
                            <!-- end content block -->
                        </#if>

                        <#if hst.isNodeType(contentblock.node, 'publishing:dsfigureblock')>
                            <!-- figure block -->
                            <figure class="<#if contentblock.overflow>overflow--large--2  overflow--xlarge--2</#if>">
                                <img alt="${contentblock.alt}" src="<@hst.link hippobean=contentblock.image/>" loading="lazy">
                                <#if contentblock.caption?has_content>
                                    <figcaption>${contentblock.caption}</figcaption>
                                </#if>
                            </figure>
                            <!-- end figure block -->
                        </#if>

                        <#if hst.isNodeType(contentblock.node, 'publishing:dsattachmentblock')>
                            <!-- attachment block -->

                            <#if contentblock.external?has_content>
                                <#assign link = contentblock.external>
                            <#else>
                                <@hst.link var="link2" hippobean=contentblock.internal />
                                <#assign link = link2/>
                            </#if>

                            <div class="ds_file-download">
                                <div class="ds_file-download__thumbnail">
                                    <a data-button="document-cover" class="ds_file-download__thumbnail-link" aria-hidden="true" tabindex="-1" href="${link}">
                                        <span class="visually-hidden">Document cover image</span>
                                        <#if contentblock.image??>
                                            <@hst.link var="icon" hippobean=contentblock.image.original />
                                            <img class="ds_file-download__thumbnail-image" src="${icon}" alt="">
                                        <#else>
                                            <#assign fileThumbnailPath = '/assets/images/documents/svg/' + contentblock.type + '.svg' />
                                            <#switch contentblock.icon?trim?lower_case>
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
                                    <a href="${link}" aria-describedby="file-download-${contentblock?keep_after("@")}" class="ds_file-download__title">${contentblock.title}</a>

                                    <div id="file-download-${contentblock?keep_after("@")}" class="ds_file-download__details">
                                        <dl class="ds_metadata  ds_metadata--inline">
                                            <div class="ds_metadata__item">
                                                <dt class="ds_metadata__key  visually-hidden">File type</dt>
                                                <dd class="ds_metadata__value">${contentblock.type}<span class="visually-hidden">,</span></dd>
                                            </div>


                                            <div class="ds_metadata__item">
                                                <dt class="ds_metadata__key  visually-hidden">File size</dt>
                                                <dd class="ds_metadata__value">${contentblock.size}</dd>
                                            </div>

                                        </dl>
                                    </div>
                                </div>
                            </div>
                            <!-- end attachment block -->
                        </#if>

                        <#if hst.isNodeType(contentblock.node, 'publishing:dsliveexampleblock')>
                            <!-- live example block -->
                            <!--noindex-->
                            <h2>Live example</h2>

                            <@hst.html hippohtml=contentblock.content/>
                            <!--endnoindex-->
                            <!-- end live example block -->
                        </#if>

                        <#if hst.isNodeType(contentblock.node, 'publishing:dspaletteblock')>
                            <!-- palette block -->
                            <${contentblock.headinglevel}>${contentblock.title}</${contentblock.headinglevel}>

                            <table class="dss_palette">
                                <thead class="visually-hidden">
                                    <tr>
                                        <th>Colour</th>
                                        <th>SCSS variable</th>
                                        <th>Hex code</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <#list contentblock.paletteitems as item>
                                        <tr>
                                            <td class="dss_palette__name">
                                                <svg class="dss_palette__swatch" viewBox="0 0 40 40" xmlns="http://www.w3.org/2000/svg">
                                                    <circle fill="${item.code}" cx="20" cy="20" r="20"></circle>
                                                </svg>
                                                <span class="visually-hidden">${item.colourname}</span>
                                                <code>${item.varname}</code>
                                            </td>
                                            <td class="dss_palette__hex"><code>${item.code}</code></td>
                                        </tr>
                                    </#list>

                                </tbody>
                            </table>
                            <!-- end palette block -->
                        </#if>

                    </#list>
                </#if>

                <#if document.updateHistory?has_content>
                    <div class="update-history">
                        <#include './ds-update-history.ftl' />
                    </div>
                </#if>

            </div>

            <#include 'feedback-wrapper.ftl'>

        </main>
    </div>
</div>

<@hst.headContribution category="meta">
    <meta name="dc.format" content="${format}"/>
</@hst.headContribution>

<#if seriesLink??>
    <@hst.headContribution category="meta">
        <meta name="dc.title.series" content="${seriesLink.title}"/>
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
