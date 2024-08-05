<#ftl output_format="HTML">
<#include "../include/imports.ftl">
<#include "../include/cms-placeholders.ftl">
<#include "../macros/content-blocks.ftl">

<#-- @ftlvariable name="document" type="scot.mygov.publishing.beans.Pageheading" -->
<@hst.webfile var="iconspath" path="/assets/images/icons/icons.stack.svg"/>
<#assign variables = hstRequestContext.getAttribute("variables")/>
<@hst.messagesReplace escapeMessageXml=false bundle=variables variablePrefix="[[" variableSuffix="]]">
<#if document?? || editMode>
<header>
    <div class="ds_cb  ds_cb--page-title
    <#if backgroundcolor?? && backgroundcolor?length gt 0>  ds_cb--bg-${backgroundcolor}</#if>
    <#if foregroundcolor?? && foregroundcolor?length gt 0>  ds_cb--fg-${foregroundcolor}</#if>
    <#if fullwidth>  ds_cb--fullwidth</#if>
    <#if neutrallinks>  ds_cb--neutral-links</#if>
    <#if widetext>  ds_cb--page-title--wide</#if>
    ">
        <div class="ds_wrapper">
            <div class="ds_cb__inner">
            <#if document??>
                <div class="ds_cb__text  ds_cb__content<#if verticalalign??>  ds_cb__text--${verticalalign}</#if>">
                    <div class="ds_page-header">
                        <h1 class="ds_page-header__title<#if lightheader>  ds_page-header__title--light</#if>">${document.title}</h1>
                    </div>
                    <#if document.descriptionContentBlocks??>
                        <@renderContentBlocks document.descriptionContentBlocks />
                    </#if>
                    <#if document.cta??>
                    <#if document.link??>
                    <a href="<@hst.link hippobean=document.link/>" class="ds_button ds_button--has-icon">${document.cta}<svg class="ds_icon" aria-hidden="true" role="img">
                        <use href="${iconspath}#chevron_right"></use>
                    </svg></a>
                    <#elseif document.externalLink?has_content>
                    <a href="${document.externalLink}" class="ds_button ds_button--has-icon">${document.cta}<svg class="ds_icon" aria-hidden="true" role="img">
                        <use href="${iconspath}#chevron_right"></use>
                    </svg></a>
                    </#if>
                    </#if>
                </div>
                <#if document.image??>
                <div class="ds_cb__poster<#if imagenomargin>  ds_cb__poster--no-margin</#if><#if imagealigndesktop??>  ds_cb__poster--${imagealigndesktop}</#if><#if imagealignmobile??>  ds_cb__poster--${imagealignmobile}-mobile</#if>">
                    <picture>
                    <#if imagealignmobile == "hidden">
                        <source media="(max-width: 767px)" sizes="1px" srcset="data:image/gif;base64,R0lGODlhAQABAIAAAAAAAP///yH5BAEAAAAALAAAAAABAAEAAAIBRAA7 1w" />
                    </#if>
                    <#if document.image.xlargesixcolumns??>
                        <img alt="${document.alt}" src="<@hst.link hippobean=document.image.xlargesixcolumns />"
                            <#if document.image.xlargesixcolumns.width gt 0>
                                width="${document.image.xlargesixcolumns.width?c}"
                            </#if>
                            <#if document.image.xlargesixcolumns.height gt 0>
                                height="${document.image.xlargesixcolumns.height?c}"
                            </#if>
                            srcset="
                            <@hst.link hippobean=document.image.smallcolumns/> 360w,
                            <@hst.link hippobean=document.image.smallcolumnsdoubled/> 720w,
                            <@hst.link hippobean=document.image.mediumsixcolumns/> 352w,
                            <@hst.link hippobean=document.image.mediumsixcolumnsdoubled/> 704w,
                            <@hst.link hippobean=document.image.largesixcolumns/> 448w,
                            <@hst.link hippobean=document.image.largesixcolumnsdoubled/> 896w,
                            <@hst.link hippobean=document.image.xlargesixcolumns/> 544w,
                            <@hst.link hippobean=document.image.xlargesixcolumnsdoubled/> 1088w"
                            sizes="(min-width:1200px) 544px, (min-width:992px) 448px, (min-width: 768px) 352px, 100vw"
                            >
                    <#else>
                        <img src="<@hst.link hippobean=document.image />" alt="${document.alt}"
                            <#if document.image.original.width gt 0>
                                width="${document.image.original.width?c}"
                            </#if>
                            <#if document.image.original.height gt 0>
                                height="${document.image.original.height?c}"
                            </#if>
                        >
                    </#if>
                    </picture>
                </div>
                </#if>
            <@hst.manageContent hippobean=document documentTemplateQuery="new-pageheading-document" parameterName="document" rootPath="pageheadings"/>

            <#elseif editMode>
                <div class="ds_cb__text  ds_cb__content">
                    <div class="ds_page-header">
                        <h1 class="ds_page-header__title"><@placeholdertext lines=2/></h1>
                    </div>

                    <@placeholdertext lines=4/>
                </div>

                <div class="ds_cb__poster">
                    <@placeholderimage/>
                </div>

                <@hst.manageContent documentTemplateQuery="new-pageheading-document" parameterName="document" rootPath="pageheadings"/>
            </#if>
            </div>
        </div>
    </div>
</header>
</#if>
</@hst.messagesReplace>
