<#ftl output_format="HTML">
<#include "../include/imports.ftl">
<#include "../include/cms-placeholders.ftl">
<#include "../macros/content-blocks.ftl">
<#-- @ftlvariable name="document" type="scot.mygov.publishing.beans.Imageandtext" -->

<#assign variables = hstRequestContext.getAttribute("variables")/>
<@hst.messagesReplace escapeMessageXml=false bundle=variables variablePrefix="[[" variableSuffix="]]">
<div class="ds_cb  ds_cb--image-text
<#if backgroundcolor?? && backgroundcolor?length gt 0>  ds_cb--bg-${backgroundcolor}</#if>
<#if foregroundcolor?? && foregroundcolor?length gt 0>  ds_cb--fg-${foregroundcolor}</#if>
<#if fullwidth>  ds_cb--fullwidth</#if>
<#if neutrallinks>  ds_cb--neutral-links</#if>
<#if removebottompadding>  ds_!_padding-bottom--0</#if>
">
    <div class="ds_wrapper">
        <div class="ds_cb__inner">
            <#if document??>
                <div class="ds_cb__text">
                    <#if document.contentBlocks??>
                        <@renderContentBlocks document.contentBlocks />
                    </#if>
                </div>

                <div class="ds_cb__poster">
                    <#if document.image.xlargesixcolumns??>
                        <img alt="${document.alt}" src="<@hst.link hippobean=document.image.xlargesixcolumns />"
                                width="${document.image.xlargesixcolumns.width?c}"
                                height="${document.image.xlargesixcolumns.height?c}"
                                loading="lazy"
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
                        <img loading="lazy" src="<@hst.link hippobean=document.image />" alt="${document.alt}"/>
                    </#if>
                </div>

                <@hst.manageContent hippobean=document documentTemplateQuery="new-imageandtext-document" parameterName="document" rootPath="images"/>
            <#elseif editMode>
                <div class="ds_cb__text cms-blank">
                    <@placeholdertext lines=7/>
                </div>

                <div class="ds_cb__poster cms-blank">
                    <@placeholderimage/>
                </div>

                <@hst.manageContent documentTemplateQuery="new-imageandtext-document" parameterName="document" rootPath="images"/>
            </#if>
        </div>
    </div>
</div>
</@hst.messagesReplace>
