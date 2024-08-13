<#ftl output_format="HTML">
<#include "../include/imports.ftl">
<#include "../include/cms-placeholders.ftl">
<@hst.webfile var="iconspath" path="/assets/images/icons/icons.stack.svg"/>
<#-- @ftlvariable name="document" type="scot.mygov.publishing.beans.Video" -->

<#assign variables = hstRequestContext.getAttribute("variables")/>
<@hst.messagesReplace escapeMessageXml=false bundle=variables variablePrefix="[[" variableSuffix="]]">
<div class="ds_cb  ds_cb--video
<#if backgroundcolor?? && backgroundcolor?length gt 0>  ds_cb--bg-${backgroundcolor}</#if>
<#if foregroundcolor?? && foregroundcolor?length gt 0>  ds_cb--fg-${foregroundcolor}</#if>
<#if fullwidth>  ds_cb--fullwidth</#if>
<#if neutrallinks>  ds_cb--neutral-links</#if>
<#if removebottompadding>  ds_!_padding-bottom--0</#if>
">
    <div class="ds_wrapper">
        <div class="ds_cb__inner">
            <#if document??>
                <div class="ds_cb__poster">
                    <a target="_blank" class="ds_cb__poster-link" href="${document.url}">
                        <#if document.image.xlargesixcolumns??>
                            <img class="ds_cb__poster-video" alt="${document.alt}" src="<@hst.link hippobean=document.image.xlargesixcolumns />"
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
                    </a>
                </div>

                <div class="ds_cb__text">
                    <@hst.html hippohtml=document.content/>
                </div>

                <@hst.manageContent hippobean=document documentTemplateQuery="new-video-document" parameterName="document" rootPath="videos"/>
            <#elseif editMode>
                <div class="ds_cb__poster cms-blank">
                    <@placeholdervideo/>
                </div>

                <div class="ds_cb__text cms-blank">
                    <@placeholdertext lines=7/>
                </div>

                <@hst.manageContent documentTemplateQuery="new-video-document" parameterName="document" rootPath="videos"/>
            </#if>
        </div>
    </div>
</div>
</@hst.messagesReplace>