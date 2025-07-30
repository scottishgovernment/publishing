<#ftl output_format="HTML">
<#include "../../include/imports.ftl">
<#include "../../include/cms-placeholders.ftl">
<#include "../../macros/content-blocks.ftl">

<#assign variables = hstRequestContext.getAttribute("variables")/>
<@hst.messagesReplace escapeMessageXml=false bundle=variables variablePrefix="[[" variableSuffix="]]">

<#assign items = []>
<#if document1?has_content>
    <#assign items = items + [document1]>
<#elseif editMode>
    <#assign items = items + ['']>
</#if>
<#if document2?has_content>
    <#assign items = items + [document2]>
<#elseif editMode>
    <#assign items = items + ['']>
</#if>
<#if document3?has_content>
    <#assign items = items + [document3]>
<#elseif editMode>
    <#assign items = items + ['']>
</#if>
<#if document4?has_content>
    <#assign items = items + [document4]>
<#elseif editMode>
    <#assign items = items + ['']>
</#if>

<#if items?size != 0>
<div class="ds_pb  ds_pb--feature-grid  ds_pb--feature-grid-4-items
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

        <#list items as item>
        
            <#if item != ''>

            <!-- set link where internal link has priority over external link -->
            <#assign link>
            <#if item.link?has_content>
                <@hst.link hippobean=item.link/>
            <#elseif item.externalLink?has_content>
                ${item.externalLink}
            </#if>
            </#assign>
                <div class="ds_pb--feature-grid__item">
                    <#if showimages>
                        <#if item.image?has_content>
                        <div class="ds_pb--feature-grid__item-media  <#if smallvariant>ds_pb--feature-grid__item-media--small-mobile</#if>">
                            <#if link?has_content>
                            <a href="${link}" tabindex="-1">
                            </#if>    
                                <div class="ds_aspect-box">
                                <#if item.image.xlargethreecolumns?has_content>
                                    <img class="ds_aspect-box__inner" alt="${item.alt}" src="<@hst.link hippobean=item.image.xlargethreecolumns />"
                                            width="${item.image.xlargethreecolumns.width?c}"
                                            height="${item.image.xlargethreecolumns.height?c}"
                                            loading="lazy"
                                            srcset="
                                            <@hst.link hippobean=item.image.smallcolumns/> 360w,
                                            <@hst.link hippobean=item.image.smallcolumnsdoubled/> 720w,
                                            <@hst.link hippobean=item.image.mediumsixcolumns/> 352w,
                                            <@hst.link hippobean=item.image.mediumsixcolumnsdoubled/> 704w,
                                            <@hst.link hippobean=item.image.largethreecolumns/> 208w,
                                            <@hst.link hippobean=item.image.largethreecolumnsdoubled/> 416w,
                                            <@hst.link hippobean=item.image.xlargethreecolumns/> 256w,
                                            <@hst.link hippobean=item.image.xlargethreecolumnsdoubled/> 512w"
                                            sizes="(min-width:1200px) 256px, (min-width:992px) 208px, (min-width: 768px) 352px, <#if smallvariant>360px<#else>100vw</#if>"
                                            >
                                <#else>
                                    <img loading="lazy" class="ds_aspect-box__inner" src="<@hst.link hippobean=item.image />" alt="${item.alt}"/>
                                </#if>
                                </div>
                            <#if link?has_content>
                            </a>
                            </#if>   
                        </div>
                        </#if>   
                    </#if>
                    <#if item.title?has_content>
                        <${weight} class="ds_pb--feature-grid__item-title">
                            <#if link?has_content>
                                <a href="${link}">${item.title}</a>
                            <#else>
                                ${item.title}
                            </#if>
                        </${weight}>
                    </#if>
                    
                    <#if item.contentBlocks?has_content>
                        <div class="ds_pb--feature-grid__item-summary">
                            <@renderContentBlocks item.contentBlocks />
                        </div>
                    </#if>

                    <@hst.manageContent hippobean=item documentTemplateQuery="new-featuregriditem-document" parameterName="item" rootPath="featuregriditems"/>
                </div>
            <#elseif editMode>
                <div class="ds_pb--feature-grid__item  cms-blank">
                    <#if showimages>
                    <div class="ds_pb--feature-grid__item-media  <#if smallvariant>ds_pb--feature-grid__item-media--small-mobile</#if>">
                        <@placeholderimage/>
                    </div>
                    </#if>
                    <${weight} class="ds_pb--feature-grid__item-title">
                        <@placeholdertext lines=2/>
                    </${weight}>
                    <div class="ds_pb--feature-grid__item-summary">
                        <@placeholdertext lines=8/>
                    </div>

                    <@hst.manageContent documentTemplateQuery="new-featuregriditem-document" parameterName="item" rootPath="featuregriditems"/>
                </div>
            </#if>

        </#list>
        </div>
    </div>
</div>
</#if>
</@hst.messagesReplace>