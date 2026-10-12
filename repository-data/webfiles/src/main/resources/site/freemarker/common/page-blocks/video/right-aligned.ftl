<#ftl output_format="HTML">
<#include "../../include/imports.ftl">
<#include "../../include/cms-placeholders.ftl">
<#include "../../macros/content-blocks.ftl">
<@hst.webfile var="iconspath" path="/assets/images/icons/icons.stack.svg"/>

<#assign variables = hstRequestContext.getAttribute("variables")/>
<@hst.messagesReplace escapeMessageXml=false bundle=variables variablePrefix="[[" variableSuffix="]]">

<#-- @ftlvariable name="document" type="scot.mygov.publishing.beans.Video" -->

<div class="ds_pb  ds_pb--video-text
<#if verticalcenter?has_content><#if verticalcenter>  ds_pb--video-text--center</#if></#if>
<#if removebottompadding>  ds_!_padding-bottom--0</#if>
<#if backgroundcolor?has_content> 
<#switch backgroundcolor?lower_case> 
  <#case 'secondary'>
  ds_pb--background-secondary
  <#break>
  <#case 'tertiary'>
  ds_pb--background-tertiary
  <#break>
  <#case 'theme'>
  ds_pb__theme--background-secondary
  <#break>
</#switch>
</#if>">
    <div class="ds_wrapper">
        <div class="ds_pb__inner">
            <#if document?has_content>

                <div class="ds_pb__text">
                    <#if document.contentBlocks?has_content>
                        <@renderContentBlocks document.contentBlocks />
                    </#if>
                </div>

                <div class="ds_pb__poster">
                    <#if document.image?has_content>
                    <a target="_blank" class="ds_pb__poster__link" href="${document.url}">
                        <#if document.image.xlargesixcolumns?has_content>
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
                            <img class="ds_pb__poster-video" loading="lazy" src="<@hst.link hippobean=document.image />" alt="${document.alt}" width="${document.image.original.width?c}" height="${document.image.original.height?c}"/>
                        </#if>

                        <svg class="ds_pb__poster__overlay" xmlns="http://www.w3.org/2000/svg" xml:space="preserve" viewBox="0 0 160 90">
                            <circle class="background" cx="82" cy="45.5" r="16" fill="#0065db"/>
                            <path class="foreground" fill="#fff" d="M76 45.3v-9.2l8 4.6 8 4.6-8 4.7-8 4.6z"/>
                        </svg>
                    </a>
                    </#if>
                </div>

                <@hst.manageContent hippobean=document documentTemplateQuery="new-video-document" parameterName="document" rootPath="videos"/>
            <#elseif editMode>

                <div class="ds_pb__text cms-blank">
                    <@placeholdertext lines=7/>
                </div>

                <div class="ds_pb__poster cms-blank">
                    <@placeholderimage/>
                </div>
                <@hst.manageContent documentTemplateQuery="new-video-document" parameterName="document" rootPath="videos"/>
            </#if>
        </div>
    </div>
</div>
</@hst.messagesReplace>