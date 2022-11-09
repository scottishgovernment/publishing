<#ftl output_format="HTML">
<#include "../../include/imports.ftl">
<#include "../../include/cms-placeholders.ftl">

<#assign items = []>
<#if document1??>
    <#assign items = items + [document1]>
<#elseif editMode>
    <#assign items = items + ['']>
</#if>
<#if document2??>
    <#assign items = items + [document2]>
<#elseif editMode>
    <#assign items = items + ['']>
</#if>
<#if document3??>
    <#assign items = items + [document3]>
<#elseif editMode>
    <#assign items = items + ['']>
</#if>
<#if document4??>
    <#assign items = items + [document4]>
<#elseif editMode>
    <#assign items = items + ['']>
</#if>

<#if items?size != 0>
<div class="ds_cb  ds_cb--feature-grid  ds_cb--feature-grid-4-items
<#if fullwidth>  ds_cb--fullwidth</#if>
<#if backgroundcolor?? && backgroundcolor?length gt 0>  ds_cb--bg-${backgroundcolor}</#if>
<#if foregroundcolor?? && foregroundcolor?length gt 0>  ds_cb--fg-${foregroundcolor}</#if>
<#if neutrallinks>  ds_cb--neutral-links</#if>
">
    <div class="ds_wrapper">
        <div class="ds_cb__inner">

        <#list items as item>
        
            <#if item != ''>
                <div class="ds_cb--feature-grid__item">
                    <#if showimages>
                        <div class="ds_cb--feature-grid__item-media  <#if smallvariant>ds_cb--feature-grid__item-media--small-mobile</#if>">
                            <div class="ds_aspect-box">
                                <#if item.image.xlargefourcolumns??>
                                    <img class="ds_aspect-box__inner" alt="${item.alt}" src="<@hst.link hippobean=item.image.xlargefourcolumns />"
                                            width="${item.image.xlargefourcolumns.width?c}"
                                            height="${item.image.xlargefourcolumns.height?c}"
                                            loading="lazy"
                                            srcset="
                                            <@hst.link hippobean=item.image.smallcolumns/> 360w,
                                            <@hst.link hippobean=item.image.smallcolumnsdoubled/> 720w,
                                            <@hst.link hippobean=item.image.mediumfourcolumns/> 224w,
                                            <@hst.link hippobean=item.image.mediumfourcolumnsdoubled/> 448w,
                                            <@hst.link hippobean=item.image.largefourcolumns/> 288w,
                                            <@hst.link hippobean=item.image.largefourcolumnsdoubled/> 576w,
                                            <@hst.link hippobean=item.image.xlargefourcolumns/> 352w,
                                            <@hst.link hippobean=item.image.xlargefourcolumnsdoubled/> 704w"
                                            sizes="(min-width:1200px) 352px, (min-width:992px) 288px, (min-width: 768px) 224px, <#if smallvariant>360px<#else>100vw</#if>"
                                            >
                                <#else>
                                    <img loading="lazy" class="ds_aspect-box__inner" src="<@hst.link hippobean=item.image />" alt="${item.alt}"/>
                                </#if>
                            </div>
                        </div>
                    </#if>
                    <#if item.title??>
                        <${weight} class="ds_cb--feature-grid__item-title">
                            <#if item.link??>
                                <a href="<@hst.link hippobean=item.link/>">${item.title}</a>
                            <#elseif item.externalLink?has_content>
                                <a href="${item.externalLink}">${item.title}</a>
                            <#else>
                                ${item.title}
                            </#if>
                        </${weight}>
                    </#if>
                    <#if item.content??>
                    <div class="ds_cb--feature-grid__item-summary">
                        ${item.content}
                    </div>
                    </#if>

                    <@hst.manageContent hippobean=card documentTemplateQuery="new-navigationcard-document" parameterName="card" rootPath="navigationcards"/>
                </div>
            <#elseif editMode>
                <div class="ds_cb--feature-grid__item  cms-blank">
                    <#if showimages>
                    <div class="ds_cb--feature-grid__item-media  <#if smallvariant>ds_cb--feature-grid__item-media--small-mobile</#if>">
                        <@placeholderimage/>
                    </div>
                    </#if>
                    <${weight} class="ds_cb--feature-grid__item-title">
                        <@placeholdertext lines=2/>
                    </${weight}>
                    <div class="ds_cb--feature-grid__item-summary">
                        <@placeholdertext lines=8/>
                    </div>

                    <@hst.manageContent documentTemplateQuery="new-navigationcard-document" parameterName="card" rootPath="navigationcards"/>
                </div>
            </#if>

        </#list>
        </div>
    </div>
</div>
</#if>