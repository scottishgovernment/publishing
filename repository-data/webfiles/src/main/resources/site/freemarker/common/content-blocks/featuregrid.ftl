<#ftl output_format="HTML">
<#include "../include/imports.ftl">
<#include "../include/cms-placeholders.ftl">


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

<#if items?size != 0>
<div class="ds_cb  ds_cb--feature-grid
<#if fullwidth>  ds_cb--fullwidth</#if>
<#if backgroundcolor?? && backgroundcolor?length gt 0>  ds_cb--bg-${backgroundcolor}</#if>
<#if foregroundcolor?? && foregroundcolor?length gt 0>  ds_cb--fg-${foregroundcolor}</#if>
<#if neutrallinks>  ds_cb--neutral-links</#if>
">
    <div class="ds_wrapper">
        <div class="ds_cb__inner">

        <#list items as item>

        <!-- set link where internal link has priority over external link -->
        <#assign link>
        <#if item.link??>
            <@hst.link hippobean=item.link/>
        <#elseif item.externalLink?has_content>
            ${item.externalLink}
        </#if>
        </#assign>
        
            <#if item != ''>
                <div class="ds_cb--feature-grid__item">
                    <#if showimages>
                        <div class="ds_cb--feature-grid__item-media  <#if smallvariant>ds_cb--feature-grid__item-media--small-mobile</#if>">
                            <div class="ds_aspect-box">
                            <#if link?has_content>
                                <a href="${link}" tabindex="-1">
                            </#if>    
                                <#if item.image.xlargesixcolumns??>
                                    <img class="ds_aspect-box__inner" alt="${item.alt}" src="<@hst.link hippobean=item.image.xlargesixcolumns />"
                                            width="${item.image.xlargesixcolumns.width?c}"
                                            height="${item.image.xlargesixcolumns.height?c}"
                                            loading="lazy"
                                            srcset="
                                            <@hst.link hippobean=item.image.smallcolumns/> 360w,
                                            <@hst.link hippobean=item.image.smallcolumnsdoubled/> 720w,
                                            <@hst.link hippobean=item.image.mediumsixcolumns/> 352w,
                                            <@hst.link hippobean=item.image.mediumsixcolumnsdoubled/> 704w,
                                            <@hst.link hippobean=item.image.largesixcolumns/> 448w,
                                            <@hst.link hippobean=item.image.largesixcolumnsdoubled/> 896w,
                                            <@hst.link hippobean=item.image.xlargesixcolumns/> 544w,
                                            <@hst.link hippobean=item.image.xlargesixcolumnsdoubled/> 1088w"
                                            sizes="(min-width:1200px) 544px, (min-width:992px) 448px, (min-width: 768px) 352px, <#if smallvariant>360px<#else>100vw</#if>"
                                            >
                                <#else>
                                    <img loading="lazy" class="ds_aspect-box__inner" src="<@hst.link hippobean=item.image />" alt="${item.alt}"/>
                                </#if>
                            <#if link?has_content>
                                </a>
                            </#if>    
                            </div>
                        </div>
                    </#if>
                    <#if item.title??>
                        <${weight} class="ds_cb--feature-grid__item-title">
                            <#if link?has_content>
                                <a href="${link}">${item.title}</a>
                            <#else>
                                ${item.title}
                            </#if>
                        </${weight}>
                    </#if>
                    <@hst.html var="htmlcontent" hippohtml=item.content/>
                    <#if htmlcontent?has_content>
                    <div class="ds_cb--feature-grid__item-summary">
                        <@hst.html hippohtml=item.content/>
                    </div>
                    </#if>

                    <@hst.manageContent hippobean=item documentTemplateQuery="new-featuregriditem-document" parameterName="item" rootPath="featuregriditems"/>
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

                    <@hst.manageContent documentTemplateQuery="new-featuregriditem-document" parameterName="item" rootPath="featuregriditems"/>
                </div>
            </#if>

        </#list>
        </div>
    </div>
</div>
</#if>