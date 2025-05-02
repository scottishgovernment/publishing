<#ftl output_format="HTML">
<#include "include/imports.ftl">
<#assign variables = hstRequestContext.getAttribute("variables")/>
<@hst.messagesReplace escapeMessageXml=false bundle=variables variablePrefix="[[" variableSuffix="]]">

<ol class="ds_category-list  ds_category-list--grid">
    <#list children as child>
        <@hst.link var="link" hippobean=child.bean/>
        <li class="ds_card  ds_card--hover">
            <div class="ds_card__media  ds_card__media--small-mobile">
                <div class="ds_aspect-box">
                <#if child.bean.cardImage?has_content>
                <#if child.bean.cardImage.xlargeeightcolumnsdoubled?has_content>
                    <img alt="${document.title}" class="ds_aspect-box__inner"
                        loading="lazy"
                        width="${child.bean.cardImage.xlargeeightcolumns.width?c}"
                        height="${child.bean.cardImage.xlargeeightcolumns.height?c}"
                        src="<@hst.link hippobean=child.bean.cardImage.xlargeeightcolumns/>"
                        srcset="<@hst.link hippobean=child.bean.cardImage.mediumfourcolumns/> 224w,
                            <@hst.link hippobean=child.bean.cardImage.largefourcolumns/> 288w,
                            <@hst.link hippobean=child.bean.cardImage.xlargefourcolumns/> 352w,
                            <@hst.link hippobean=child.bean.cardImage.smallcolumns/> 448w,
                            <@hst.link hippobean=child.bean.cardImage.xlargesixcolumns/> 544w,
                            <@hst.link hippobean=child.bean.cardImage.largefourcolumnsdoubled/> 576w,
                            <@hst.link hippobean=child.bean.cardImage.xlargefourcolumnsdoubled/> 704w,
                            <@hst.link hippobean=child.bean.cardImage.xlargeeightcolumns/> 736w,
                            <@hst.link hippobean=child.bean.cardImage.smallcolumnsdoubled/> 896w,
                            <@hst.link hippobean=child.bean.cardImage.xlargesixcolumnsdoubled/> 1088w,
                            <@hst.link hippobean=child.bean.cardImage.xlargeeightcolumnsdoubled/> 1472w"
                        sizes="(min-width:1200px) 352px, (min-width:992px) 288px, (min-width: 768px) 224px, 736px"/>
                    <#else>
                        <img class="ds_aspect-box__inner" loading="lazy" alt="${document.title}" src="<@hst.link hippobean=child.bean.cardImage/>">
                    </#if>
                </#if>
                </div>
            </div>
            <div class="ds_card__content">
                <h2 class="ds_card__title">
                    <a class="ds_card__link--cover" href="${link}">${child.bean.title}</a>
                </h2>
                <p>${child.bean.summary}</p>
            </div>
        </li>
    </#list>
</ol>
</@hst.messagesReplace>