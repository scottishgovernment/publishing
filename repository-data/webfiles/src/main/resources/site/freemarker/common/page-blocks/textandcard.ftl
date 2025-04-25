<#ftl output_format="HTML">
<#include "../include/imports.ftl">
<#include "../include/cms-placeholders.ftl">
<#include "../macros/content-blocks.ftl">
<#assign variables = hstRequestContext.getAttribute("variables")/>
<@hst.messagesReplace escapeMessageXml=false bundle=variables variablePrefix="[[" variableSuffix="]]">
<#-- @ftlvariable name="document" type="scot.mygov.publishing.beans.TextAndCard" -->
<div class="ds_pb  ds_pb--card-text
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
            <#if document1?has_content>
            <div class="ds_pb__text">
                <#if document1.contentBlocks?has_content>
                    <@renderContentBlocks document1.contentBlocks />
                </#if>
                <@hst.manageContent hippobean=document1 documentTemplateQuery="new-text-document" parameterName="document1" rootPath="text"/>
            </div>
            <#elseif editMode>
            <div class="cms-blank  ds_pb__text">
                <@placeholdertext lines=7/>
                <@hst.manageContent documentTemplateQuery="new-text-document" parameterName="document1" rootPath="text"/>
            </div>
            </#if>
            <#if document2?has_content>
            <div class="ds_pb__card">
                <div class="ds_card  <#if document2.link?? || document2.externalLink?has_content>ds_card--hover  </#if><#if !backgroundcolor?has_content>  ds_card--grey</#if>">
                    <#if showimages>
                        <div class="ds_card__media  <#if smallvariant>ds_card__media--small-mobile</#if>">
                            <div class="ds_aspect-box">
                            <#if document2.image?has_content>
                                <#if document2.image.xlargefourcolumns?has_content>
                                    <img class="ds_aspect-box__inner" alt="${document2.alt}" src="<@hst.link hippobean=document2.image.xlargefourcolumns />"
                                            width="${document2.image.xlargefourcolumns.width?c}"
                                            height="${document2.image.xlargefourcolumns.height?c}"
                                            loading="lazy"
                                            srcset="
                                            <@hst.link hippobean=document2.image.smallcolumns/> 360w,
                                            <@hst.link hippobean=document2.image.smallcolumnsdoubled/> 720w,
                                            <@hst.link hippobean=document2.image.mediumfourcolumns/> 224w,
                                            <@hst.link hippobean=document2.image.mediumfourcolumnsdoubled/> 448w,
                                            <@hst.link hippobean=document2.image.largefourcolumns/> 288w,
                                            <@hst.link hippobean=document2.image.largefourcolumnsdoubled/> 576w,
                                            <@hst.link hippobean=document2.image.xlargefourcolumns/> 352w,
                                            <@hst.link hippobean=document2.image.xlargefourcolumnsdoubled/> 704w"
                                            sizes="(min-width:1200px) 352px, (min-width:992px) 288px, (min-width: 768px) 224px, <#if smallvariant>360px<#else>100vw</#if>"
                                            >
                                <#else>
                                    <img loading="lazy" class="ds_aspect-box__inner" src="<@hst.link hippobean=document2.image />" alt="${document2.alt}"/>
                                </#if>
                            </#if>
                            </div>
                        </div>
                    </#if>
                    <div class="ds_card__content">
                        <#if document2.title?has_content>
                        <h2 class="ds_card__title">
                            <#if document2.link?has_content>
                                <a class="ds_card__link--cover" href="<@hst.link hippobean=document2.link/>">${document2.title}</a>
                            <#elseif document2.externalLink?has_content>
                                <a class="ds_card__link--cover" href="${document2.externalLink}">${document2.title}</a>
                            <#else>
                                ${document2.title}
                            </#if>
                        </h2>
                        </#if>
                        <#if document2.text?has_content>
                        <p>${document2.text}</p>
                        </#if>
                    </div>

                    <@hst.manageContent hippobean=document2 documentTemplateQuery="new-navigationcard-document" parameterName="document2" rootPath="navigationcards"/>
                </div>
            </div>
            <#elseif editMode>
            <div class="ds_pb__card">
                <div class="ds_card  cms-blank  ds_card--grey">
                    <#if showimages>
                        <div class="ds_card__media  <#if smallvariant>ds_card__media--small-mobile</#if>">
                            <@placeholderimage/>
                        </div>
                    </#if>
                    <div class="ds_card__content">
                        <h2 class="ds_card__title">
                            <a><@placeholdertext lines=2/></a>
                        </h2>
                        <p><@placeholdertext lines=4/></p>
                    </div>

                    <@hst.manageContent documentTemplateQuery="new-navigationcard-document" parameterName="document2" rootPath="navigationcards"/>
                </div>
            </div>
            </#if>
        </div>
    </div>
</div>
</@hst.messagesReplace>