<#ftl output_format="HTML">
<#include "include/imports.ftl">
<#include "include/cms-placeholders.ftl">
<@hst.webfile var="iconspath" path="/assets/images/icons/icons.stack.svg"/>
<#-- @ftlvariable name="document" type="scot.mygov.publishing.beans.Video" -->

<div class="ds_cb  ds_cb--video
    <#if backgroundcolor?? && backgroundcolor?length gt 0>ds_cb--bg-${backgroundcolor}</#if>
    <#if foregroundcolor?? && foregroundcolor?length gt 0>ds_cb--fg-${foregroundcolor}</#if>
    <#if fullwidth>ds_cb--fullwidth</#if>
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
                            <img class="ds_cb__poster-video" alt="${document.alt}" src="<@hst.link hippobean=document.image />"
                                width="${document.image.cardsixteenninexl.width?c}"
                                height="${document.image.cardsixteenninexl.height?c}"
                                loading="lazy"
                                srcset="
                                <@hst.link hippobean=document.image.cardsixteennines/> 730w,
                                <@hst.link hippobean=document.image.cardsixteenninesdbl/> 1460w,
                                <@hst.link hippobean=document.image.cardsixteenninem/> 352w,
                                <@hst.link hippobean=document.image.cardsixteenninedbl/> 704w,
                                <@hst.link hippobean=document.image.cardsixteenninel/> 304w,
                                <@hst.link hippobean=document.image.cardsixteennineldbl/> 608w,
                                <@hst.link hippobean=document.image.cardsixteenninexl/> 371w,
                                <@hst.link hippobean=document.image.cardsixteenninexldbl/> 742w"
                                sizes="(min-width:1200px) 371px, (min-width:992px) 304px, (min-width:768px) 352px, 730px"
                            >
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
