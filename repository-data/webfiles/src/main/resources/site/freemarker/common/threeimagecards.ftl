<#ftl output_format="HTML">
<#include "include/imports.ftl">
<#include "include/cms-placeholders.ftl">

<#-- @ftlvariable name="document1" type="scot.mygov.publishing.beans.Navigationcard" -->
<#-- @ftlvariable name="document2" type="scot.mygov.publishing.beans.Navigationcard" -->
<#-- @ftlvariable name="document3" type="scot.mygov.publishing.beans.Navigationcard" -->
<div class="ds_cb  ds_cb--cards  ds_cb--bg-grey  <#if fullwidth>ds_cb--fullwidth</#if>">
    <div class="ds_wrapper">
        <div class="ds_cb__inner">
            <#if document1??>
                <div class="ds_card  ds_card--has-hover">
                    <#if showimages>
                        <div class="ds_card__media  <#if smallvariant>ds_card__media--small-mobile</#if>">
                            <div class="ds_aspect-box">
                                <#if document1.image.xlargefourcolumns??>
                                    <img class="ds_aspect-box__inner" alt="${document1.alt}" src="<@hst.link hippobean=document1.image.xlargefourcolumns />"
                                            width="${document1.image.xlargefourcolumns.width?c}"
                                            height="${document1.image.xlargefourcolumns.height?c}"
                                            loading="lazy"
                                            srcset="
                                            <@hst.link hippobean=document1.image.smallcolumns/> 448w,
                                            <@hst.link hippobean=document1.image.smallcolumnsdoubled/> 896w,
                                            <@hst.link hippobean=document1.image.mediumfourcolumns/> 224w,
                                            <@hst.link hippobean=document1.image.mediumfourcolumnsdoubled/> 448w,
                                            <@hst.link hippobean=document1.image.largefourcolumns/> 288w,
                                            <@hst.link hippobean=document1.image.largefourcolumnsdoubled/> 576w,
                                            <@hst.link hippobean=document1.image.xlargefourcolumns/> 352w,
                                            <@hst.link hippobean=document1.image.xlargefourcolumnsdoubled/> 704"
                                            sizes="(min-width:1200px) 352px, (min-width:992px) 288px, (min-width: 768px) 224px, 448px"
                                            >
                                <#else>
                                    <img loading="lazy" class="ds_aspect-box__inner" src="<@hst.link hippobean=document1.image />" alt="${document1.alt}"/>
                                </#if>
                            </div>
                        </div>
                    </#if>
                    <div class="ds_card__content  ds_category-item">
                        <h2 class="ds_category-item__title">
                            <#if document1.link??>
                                <a class="ds_card__cover-link" href="<@hst.link hippobean=document1.link/>">${document1.title}</a>
                            <#elseif document1.externalLink?has_content>
                                <a class="ds_card__cover-link" href="${document1.externalLink}">${document1.title}</a>
                            <#else>
                                ${document1.title}
                            </#if>
                        </h2>

                        <div class="ds_category-item__summary">${document1.text}</div>
                    </div>

                    <@hst.manageContent hippobean=document1 documentTemplateQuery="new-navigationcard-document" parameterName="document1" rootPath="navigationcards"/>
                </div>
            <#elseif editMode>
                <div class="ds_card  cms-blank">
                    <#if showimages>
                        <div class="ds_card__media  <#if smallvariant>ds_card__media--small-mobile</#if>">
                            <@placeholderimage/>
                        </div>
                    </#if>
                    <div class="ds_card__content  ds_category-item">
                        <h2 class="ds_category-item__title">
                            <a><@placeholdertext lines=2/></a>
                        </h2>

                        <div class="ds_category-item__summary">
                            <@placeholdertext lines=4/>
                        </div>
                    </div>

                    <@hst.manageContent documentTemplateQuery="new-navigationcard-document" parameterName="document1" rootPath="navigationcards"/>
                </div>
            </#if>

            <#if document2??>
                <div class="ds_card  ds_card--has-hover">
                    <#if showimages>
                        <div class="ds_card__media  <#if smallvariant>ds_card__media--small-mobile</#if>">
                            <div class="ds_aspect-box">
                                <#if document2.image.xlargefourcolumns??>
                                    <img class="ds_aspect-box__inner" alt="${document2.alt}" src="<@hst.link hippobean=document2.image.xlargefourcolumns />"
                                            width="${document2.image.xlargefourcolumns.width?c}"
                                            height="${document2.image.xlargefourcolumns.height?c}"
                                            loading="lazy"
                                            srcset="
                                            <@hst.link hippobean=document2.image.smallcolumns/> 448w,
                                            <@hst.link hippobean=document2.image.smallcolumnsdoubled/> 896w,
                                            <@hst.link hippobean=document2.image.mediumfourcolumns/> 224w,
                                            <@hst.link hippobean=document2.image.mediumfourcolumnsdoubled/> 448w,
                                            <@hst.link hippobean=document2.image.largefourcolumns/> 288w,
                                            <@hst.link hippobean=document2.image.largefourcolumnsdoubled/> 576w,
                                            <@hst.link hippobean=document2.image.xlargefourcolumns/> 352w,
                                            <@hst.link hippobean=document2.image.xlargefourcolumnsdoubled/> 704"
                                            sizes="(min-width:1200px) 352px, (min-width:992px) 288px, (min-width: 768px) 224px, 448px"
                                            >
                                <#else>
                                    <img loading="lazy" class="ds_aspect-box__inner" src="<@hst.link hippobean=document2.image />" alt="${document2.alt}"/>
                                </#if>
                            </div>
                        </div>
                    </#if>
                    <div class="ds_card__content  ds_category-item">
                        <h2 class="ds_category-item__title">
                            <#if document2.link??>
                                <a class="ds_card__cover-link" href="<@hst.link hippobean=document2.link/>">${document2.title}</a>
                            <#elseif document2.externalLink?has_content>
                                <a class="ds_card__cover-link" href="${document2.externalLink}">${document2.title}</a>
                            <#else>
                                ${document2.title}
                            </#if>
                        </h2>

                        <div class="ds_category-item__summary">${document2.text}</div>
                    </div>

                    <@hst.manageContent hippobean=document1 documentTemplateQuery="new-navigationcard-document" parameterName="document2" rootPath="navigationcards"/>
                </div>
            <#elseif editMode>
                <div class="ds_card  cms-blank">
                    <#if showimages>
                        <div class="ds_card__media  <#if smallvariant>ds_card__media--small-mobile</#if>">
                            <@placeholderimage/>
                        </div>
                    </#if>
                    <div class="ds_card__content  ds_category-item">
                        <h2 class="ds_category-item__title">
                            <a><@placeholdertext lines=2/></a>
                        </h2>

                        <div class="ds_category-item__summary">
                            <@placeholdertext lines=4/>
                        </div>
                    </div>

                    <@hst.manageContent documentTemplateQuery="new-navigationcard-document" parameterName="document2" rootPath="navigationcards"/>
                </div>
            </#if>

            <#if document3??>
                <div class="ds_card  ds_card--has-hover">
                    <#if showimages>
                        <div class="ds_card__media  <#if smallvariant>ds_card__media--small-mobile</#if>">
                            <div class="ds_aspect-box">
                                <#if document1.image.xlargefourcolumns??>
                                    <img class="ds_aspect-box__inner" alt="${document3.alt}" src="<@hst.link hippobean=document3.image.xlargefourcolumns />"
                                            width="${document3.image.xlargefourcolumns.width?c}"
                                            height="${document3.image.xlargefourcolumns.height?c}"
                                            loading="lazy"
                                            srcset="
                                            <@hst.link hippobean=document3.image.smallcolumns/> 448w,
                                            <@hst.link hippobean=document3.image.smallcolumnsdoubled/> 896w,
                                            <@hst.link hippobean=document3.image.mediumfourcolumns/> 224w,
                                            <@hst.link hippobean=document3.image.mediumfourcolumnsdoubled/> 448w,
                                            <@hst.link hippobean=document3.image.largefourcolumns/> 288w,
                                            <@hst.link hippobean=document3.image.largefourcolumnsdoubled/> 576w,
                                            <@hst.link hippobean=document3.image.xlargefourcolumns/> 352w,
                                            <@hst.link hippobean=document3.image.xlargefourcolumnsdoubled/> 704"
                                            sizes="(min-width:1200px) 352px, (min-width:992px) 288px, (min-width: 768px) 224px, 448px"
                                            >
                                <#else>
                                    <img loading="lazy" class="ds_aspect-box__inner" src="<@hst.link hippobean=document3.image />" alt="${document3.alt}"/>
                                </#if>
                            </div>
                        </div>
                    </#if>
                    <div class="ds_card__content  ds_category-item">
                        <h2 class="ds_category-item__title">
                            <#if document3.link??>
                                <a class="ds_card__cover-link" href="<@hst.link hippobean=document3.link/>">${document3.title}</a>
                            <#elseif document3.externalLink?has_content>
                                <a class="ds_card__cover-link" href="${document3.externalLink}">${document3.title}</a>
                            <#else>
                                ${document3.title}
                            </#if>
                        </h2>

                        <div class="ds_category-item__summary">${document3.text}</div>
                    </div>

                    <@hst.manageContent hippobean=document1 documentTemplateQuery="new-navigationcard-document" parameterName="document3" rootPath="navigationcards"/>
                </div>
            <#elseif editMode>
                <div class="ds_card  cms-blank">
                    <#if showimages>
                        <div class="ds_card__media  <#if smallvariant>ds_card__media--small-mobile</#if>">
                            <@placeholderimage/>
                        </div>
                    </#if>
                    <div class="ds_card__content  ds_category-item">
                        <h2 class="ds_category-item__title">
                            <a><@placeholdertext lines=2/></a>
                        </h2>

                        <div class="ds_category-item__summary">
                            <@placeholdertext lines=4/>
                        </div>
                    </div>

                    <@hst.manageContent documentTemplateQuery="new-navigationcard-document" parameterName="document3" rootPath="navigationcards"/>
                </div>
            </#if>
        </div>
    </div>
</div>
