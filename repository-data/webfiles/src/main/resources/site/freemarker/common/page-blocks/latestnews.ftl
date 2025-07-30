<#ftl output_format="HTML">
<#include "../include/imports.ftl">
<@hst.webfile var="iconspath" path="/assets/images/icons/icons.stack.svg"/>

<div class="ds_pb  ds_pb--link-list
<#if removebottompadding>  ds_!_padding-bottom--0</#if>">
    <div class="ds_wrapper">
        <div class="ds_pb__inner">
            <h2>News</h2>
            <ul class="ds_link-list  ds_link-list--no-border
            <#if count == 2>  ds_link-list-2-items</#if>
            ">
            <#list news as newsItem>
                <li class="ds_link-item">
                <#if allowImages && showImages>
                <a class="ds_link-item__image-link" href="<@hst.link hippobean=newsItem/>" aria-hidden="true" tabindex="-1">
                    <div class="ds_aspect-box  ds_link-item__image">
                    <#if newsItem.image.image.xlargeeightcolumnsdoubled?has_content>
                        <img class="ds_aspect-box__inner" alt="${newsItem.image.alt}" src="<@hst.link hippobean=newsItem.image.image.xlargeeightcolumns />"
                            loading="lazy"
                            width="${newsItem.image.image.xlargeeightcolumns.width?c}"
                            height="${newsItem.image.image.xlargeeightcolumns.height?c}"
                            srcset="
                            <@hst.link hippobean=newsItem.image.image.mediumfourcolumns/> 224w,
                            <@hst.link hippobean=newsItem.image.image.largefourcolumns/> 288w,
                            <@hst.link hippobean=newsItem.image.image.xlargefourcolumns/> 352w,
                            <@hst.link hippobean=newsItem.image.image.smallcolumns/> 448w,
                            <@hst.link hippobean=newsItem.image.image.xlargesixcolumns/> 544w,
                            <@hst.link hippobean=newsItem.image.image.largefourcolumnsdoubled/> 576w,
                            <@hst.link hippobean=newsItem.image.image.xlargefourcolumnsdoubled/> 704w,
                            <@hst.link hippobean=newsItem.image.image.xlargeeightcolumns/> 736w,
                            <@hst.link hippobean=newsItem.image.image.smallcolumnsdoubled/> 896w,
                            <@hst.link hippobean=newsItem.image.image.xlargesixcolumnsdoubled/> 1088w,
                            <@hst.link hippobean=newsItem.image.image.xlargeeightcolumnsdoubled/> 1472w"
                            <#if count == 2>
                            sizes="(min-width:1200px) 544px, (min-width:992px) 448px, (min-width: 768px) 352px, 736px"
                            <#else>
                            sizes="(min-width:1200px) 352px, (min-width:992px) 288px, (min-width: 768px) 224px, 736px"
                            </#if>
                            >
                    <#else>
                        <img class="ds_aspect-box__inner" loading="lazy" alt="${newsItem.image.alt}" src="<@hst.link hippobean=newsItem.image.image/>">
                    </#if>
                    </div>
                </a>
                </#if>
                    <div class="ds_link-item__metadata">
                        <dl class="ds_metadata ds_metadata--inline">
                            <div class="ds_metadata__item">
                                <dt class="ds_metadata__key visually-hidden">Published</dt>
                                <dd class="ds_metadata__value"><@fmt.formatDate value=newsItem.publicationDate.time type="both" pattern="d MMM yyyy"/></dd>
                            </div>
                        </dl>
                    </div>
                    <h3 class="ds_link-item__title">
                        <a href="<@hst.link hippobean=newsItem/>" class="ds_link-item__link">${newsItem.title}</a>
                    </h3>
                    <p class="ds_link-item__summary">${newsItem.summary}</p>
                </li>
            </#list>
            </ul>
            <div>
                <a href="<@hst.link path="/news"/>" class="ds_cb__link-major">See all news<svg class="ds_icon" aria-hidden="true" role="img">
                    <use href="${iconspath}#chevron_right"></use>
                </svg></a>
            </div>
        </div>
    </div>
</div>