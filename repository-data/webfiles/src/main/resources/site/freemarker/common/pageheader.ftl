<#ftl output_format="HTML">
<#include "include/imports.ftl">
<#include "include/cms-placeholders.ftl">
<#-- @ftlvariable name="document" type="scot.mygov.publishing.beans.Pageheading" -->

<div class="ds_cb  ds_cb--page-title
    <#if backgroundcolor?? && backgroundcolor?length gt 0>ds_cb--bg-${backgroundcolor}</#if>
    <#if foregroundcolor?? && foregroundcolor?length gt 0>ds_cb--fg-${foregroundcolor}</#if>
    <#if fullwidth>ds_cb--fullwidth</#if>
">
    <div class="ds_wrapper">
        <div class="ds_cb__inner">
            <#if document??>
            <div class="ds_cb__text  ds_cb__content">
                <header class="ds_page-header">
                    <h1 class="ds_page-header__title">${document.title}</h1>
                </header>

                <@hst.html hippohtml=document.description/>
            </div>

            <#if document.image??>
                <div class="ds_cb__poster">
                    <#if document.image.xlargesixcolumns??>
                        <img class="" alt="${document.alt}" src="<@hst.link hippobean=document.image.smalltwelvecolumns />"
                                width="${document.image.smalltwelvecolumns.width?c}"
                                height="${document.image.smalltwelvecolumns.height?c}"
                                srcset="
                                <@hst.link hippobean=document.image.smalltwelvecolumns/> 448w,
                                <@hst.link hippobean=document.image.smalltwelvecolumnsdoubled/> 896w,
                                <@hst.link hippobean=document.image.mediumsixcolumns/> 352w,
                                <@hst.link hippobean=document.image.mediumsixcolumnsdoubled/> 704w,
                                <@hst.link hippobean=document.image.largesixcolumns/> 448w,
                                <@hst.link hippobean=document.image.largesixcolumnsdoubled/> 896w,
                                <@hst.link hippobean=document.image.xlargesixcolumns/> 544w,
                                <@hst.link hippobean=document.image.xlargesixcolumnsdoubled/> 1088w"
                                sizes="(min-width:1200px) 544px, (min-width:992px) 448px, (min-width: 768px) 352px, 448px"
                                >
                    <#else>
                        <img src="<@hst.link hippobean=document.image />" alt="${document.alt}"/>
                    </#if>
                </div>
            </#if>

            <@hst.manageContent hippobean=document documentTemplateQuery="new-pageheading-document" parameterName="document" rootPath="pageheadings"/>

            <#elseif editMode>
                <div class="ds_cb__text  ds_cb__content">
                    <header class="ds_page-header">
                        <h1 class="ds_page-header__title"><@placeholdertext lines=2/></h1>
                    </header>

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
