
<@hst.webfile var="iconspath" path="/assets/images/icons/icons.stack.svg"/>

<#function slugify string>
    <#return string?lower_case?replace('[^a-zA-Z0-9 ]+','','r')?replace('[ ]+','-','r') />
</#function>

<#macro renderContentBlocks contentBlocks>
    <#list contentBlocks as contentBlock>

        <#assign idModifier = contentBlock.node?keep_after("@") />

        <#if hst.isNodeType(contentBlock.node, 'publishing:cb_accordion')>
            <#-- accordion block -->
            <#if contentBlock.noindex>
            <!--noindex-->
            </#if>

            <#assign headingLevel = "h3" />
            <#if contentBlock.headingLevel??>
                <#assign headingLevel = contentBlock.headingLevel />
            </#if>

            <div class="ds_accordion" data-module="ds-accordion">
                <#if contentBlock.items?size gt 1>
                    <button type="button" class="ds_link  ds_accordion__open-all  js-open-all">Open all <span class="visually-hidden">sections</span></button>
                </#if>
                <#list contentBlock.items as item>
                    <div class="ds_accordion-item">
                        <input type="checkbox" class="visually-hidden  ds_accordion-item__control" id="panel-${item?index}" aria-labelledby="panel-${item?index}-heading">
                        <div class="ds_accordion-item__header">
                            <${headingLevel} id="panel-${item?index}-heading" class="ds_accordion-item__title">
                                ${item.title}
                            </${headingLevel}>
                            <span class="ds_accordion-item__indicator"></span>
                            <label class="ds_accordion-item__label" for="panel-${item?index}"><span class="visually-hidden">Show this section</span></label>
                        </div>
                        <div class="ds_accordion-item__body">
                            <#if item.contentBlocks??>
                                <@renderContentBlocks item.contentBlocks />
                            </#if>
                        </div>
                    </div>
                </#list>
            </div>

            <#if contentBlock.noindex>
            <!--endnoindex-->
            </#if>
            <#-- end accordion block -->
        </#if>

        <#if hst.isNodeType(contentBlock.node, 'publishing:cb_button')>
            <!-- button block -->
            <#if contentBlock.noindex>
            <!--noindex-->
            </#if>

            <a href="<#if contentBlock.internallink??><@hst.link hippobean=contentBlock.internallink /><#else>${contentBlock.externallink}</#if>"
                class="ds_button  <#if contentBlock.type == 'Secondary'>ds_button--secondary<#elseif contentBlock.type == 'Cancel'>ds_button--cancel</#if>  <#if contentBlock.hasarrow>ds_button--has-icon</#if>"
                <#if contentBlock.arialabel?has_content>aria-label="${contentBlock.arialabel}"</#if>>
                ${contentBlock.content}
                <#if contentBlock.hasarrow>
                    <svg class="ds_icon" aria-hidden="true" role="img"><use href="${iconspath}#chevron_right"></use></svg>
                </#if>
            </a>

            <#if contentBlock.noindex>
            <!--endnoindex-->
            </#if>
            <!-- end button block -->
        </#if>

        <#if hst.isNodeType(contentBlock.node, 'publishing:cb_fileDownload')>
            <!-- file download block -->
            <#if contentBlock.noindex>
            <!--noindex-->
            </#if>

            <#if contentBlock.external?has_content>
                <#assign link = contentBlock.external>
            <#else>
                <@hst.link var="link2" hippobean=contentBlock.internal />
                <#assign link = link2/>
            </#if>

            <div class="ds_file-download  <#if contentBlock.highlight>ds_file-download--highlighted</#if>">
                <div class="ds_file-download__thumbnail">
                    <a data-button="document-cover" class="ds_file-download__thumbnail-link" aria-hidden="true" tabindex="-1" href="${link}">
                        <span class="visually-hidden">Document cover image</span>
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
            <#if contentBlock.noindex>
            <!--endnoindex-->
            </#if>
            <!-- end file download block -->
        </#if>

        <#if hst.isNodeType(contentBlock.node, 'publishing:cb_fragment')>
            <#-- fragment block -->
            <#if contentBlock.noindex>
            <!--noindex-->
            </#if>
            <@renderContentBlocks contentBlock.fragment.contentBlocks />
            <#if contentBlock.noindex>
            <!--endnoindex-->
            </#if>
            <#-- end fragment block -->
        </#if>

        <#if hst.isNodeType(contentBlock.node, 'publishing:cb_image')>
            <!-- image block -->
            <#if contentBlock.noindex>
            <!--noindex-->
            </#if>

            <#if contentBlock.image??>
            <#if contentBlock.image.image?has_content>
                <figure class="dp_image">
                <#if contentBlock.image.image.xlargeeightcolumnsdoubled??>
                    <img alt="${contentBlock.image.alt}" src="<@hst.link hippobean=contentBlock.image.image.xlargeeightcolumns />"
                        loading="lazy"
                        width="${contentBlock.image.image.xlargeeightcolumns.width?c}"
                        height="${contentBlock.image.image.xlargeeightcolumns.height?c}"
                        srcset="
                        <@hst.link hippobean=contentBlock.image.image.smallcolumns/> 448w,
                        <@hst.link hippobean=contentBlock.image.image.smallcolumnsdoubled/> 896w,
                        <@hst.link hippobean=contentBlock.image.image.mediumeightcolumns/> 480w,
                        <@hst.link hippobean=contentBlock.image.image.mediumeightcolumnsdoubled/> 960w,
                        <@hst.link hippobean=contentBlock.image.image.largeeightcolumns/> 608w,
                        <@hst.link hippobean=contentBlock.image.image.largeeightcolumnsdoubled/> 1216w,
                        <@hst.link hippobean=contentBlock.image.image.xlargeeightcolumns/> 736w,
                        <@hst.link hippobean=contentBlock.image.image.xlargeeightcolumnsdoubled/> 1472w"
                        sizes="(min-width:1200px) 736px, (min-width:992px) 608px, (min-width: 768px) 480px, 448px"
                        >
                <#else>
                    <img loading="lazy" alt="${contentBlock.image.alt}" src="<@hst.link hippobean=contentBlock.image.image/>">
                </#if>
                <#if contentBlock.image.caption?has_content || contentBlock.image.credit?has_content>
                    <figcaption class="dp_image__caption">
                    <#if contentBlock.image.caption?has_content>${(contentBlock.image.caption)?ensure_ends_with(".")} </#if>
                    <#if contentBlock.image.credit?has_content>Credit: ${contentBlock.image.credit}</#if>
                    </figcaption>
                </#if>
                </figure>
            </#if>
            </#if>

            <#if contentBlock.noindex>
            <!--endnoindex-->
            </#if>
            <!-- end image block -->
        </#if>

        <#if hst.isNodeType(contentBlock.node, 'publishing:cb_laFinder')>
            <#-- LA finder block -->
            <#if contentBlock.noindex>
            <!--noindex-->
            </#if>

            <div data-type="button" class="dd finder-hero form-box js-contentselect">
                <label for="council-select-${idModifier}" class="ds_label">
                    ${contentBlock.title}
                </label>

                <div class="ds_input--fluid-two-thirds ds_select-wrapper">
                    <select id="council-select-${idModifier}" class="ds_select">
                        <option disabled="disabled" selected="selected">${contentBlock.placeholder}</option>
                        <#list contentBlock.links as link>
                            <option data-id="${slugify(link.label)}">${link.label}</option>
                        </#list>
                    </select>
                    <span aria-hidden="true" class="ds_select-arrow"></span>
                </div>

                <#list contentBlock.links as link>
                    <a href="${link.url}" id="dd-${slugify(link.label)}"
                    class="ds_button ds_button--max fully-hidden">${link.label}</a>
                </#list>
            </div>

            <#if contentBlock.noindex>
            <!--endnoindex-->
            </#if>
            <#-- end LA finder block -->
        </#if>

        <#if hst.isNodeType(contentBlock.node, 'publishing:cb_richtext')>
            <#-- rich text block -->
            <#if contentBlock.noindex>
            <!--noindex-->
            </#if>
            <@hst.html hippohtml=contentBlock.content/>
            <#if contentBlock.noindex>
            <!--endnoindex-->
            </#if>
            <#-- end rich text block -->
        </#if>

        <#if hst.isNodeType(contentBlock.node, 'publishing:document')>
            <#-- rich text block -->
            <#if contentBlock.noindex>
            <!--noindex-->
            </#if>

            <#assign doc = contentBlock/>
            <#assign docCount = contentBlock?counter />
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

            <div class="ds_file-download <#if doc.highlight> ds_file-download--highlighted</#if>">
                <div class="ds_file-download__thumbnail">
                    <a data-button="document-cover" class="ds_file-download__thumbnail-link" aria-hidden="true" tabindex="-1" href="${documentinline}">
                        <span class="visually-hidden">Document cover image</span>
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
                    <a href="${documentinline}" class="ds_file-download__title" id="file-title-${docCount}" aria-describedby="file-download-{$docCount}">${doc.title}</a>
                    <div <@revertlang document /> id="file-download-${docCount}" class="ds_file-download__details">
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



            <#if contentBlock.noindex>
            <!--endnoindex-->
            </#if>
            <#-- end rich text block -->
        </#if>


    </#list>
</#macro>
