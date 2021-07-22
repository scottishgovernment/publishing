<#include "include/imports.ftl">
<#include "include/cms-placeholders.ftl">

<#-- @ftlvariable name="document1" type="scot.mygov.publishing.beans.Imageandtext" -->
<#-- @ftlvariable name="document2" type="scot.mygov.publishing.beans.Imageandtext" -->
<#-- @ftlvariable name="document3" type="scot.mygov.publishing.beans.Imageandtext" -->

<div class="ds_cb  ds_cb--cards  ds_cb--bg-grey  <#if fullwidth>ds_cb--fullwidth</#if>">
    <div class="ds_wrapper">
        <div class="ds_cb__inner">
            <#if document1??>
                <div class="ds_card  ds_card--has-hover">
                    <div class="ds_card__media">
                        <img class="ds_card__image" src="<@hst.link hippobean=document1.image />" alt="${document1.alt?html}"/>
                    </div>
                    <div class="ds_card__content  ds_category-item">
                        <h2 class="ds_category-item__title"><a class="ds_card__cover-link" href="#">Image card block</a></h2>

                        <div class="ds_category-item__summary"><@hst.html hippohtml=document1.content/></div>
                    </div>

                    <@hst.manageContent hippobean=document1 documentTemplateQuery="new-imageandtext-document" parameterName="document1" rootPath="images"/>
                </div>
            <#elseif editMode>
                <div class="ds_card  cms-blank">
                    <div class="ds_card__media">
                        <@placeholderimage/>
                    </div>
                    <div class="ds_card__content  ds_category-item">
                        <h2 class="ds_category-item__title">
                            <a><@placeholdertext lines=1/></a>
                        </h2>

                        <div class="ds_category-item__summary">
                            <@placeholdertext lines=4/>
                        </div>
                    </div>

                    <@hst.manageContent documentTemplateQuery="new-imageandtext-document" parameterName="document1" rootPath="images"/>
                </div>
            </#if>

            <#if document2??>
                <div class="ds_card  ds_card--has-hover">
                    <div class="ds_card__media">
                        <img class="ds_card__image" src="<@hst.link hippobean=document2.image />" alt="${document2.alt?html}"/>
                    </div>
                    <div class="ds_card__content  ds_category-item">
                        <h2 class="ds_category-item__title"><a class="ds_card__cover-link" href="#">Image card block</a></h2>

                        <div class="ds_category-item__summary"><@hst.html hippohtml=document2.content/></div>
                    </div>

                    <@hst.manageContent hippobean=document2 documentTemplateQuery="new-imageandtext-document" parameterName="document2" rootPath="images"/>
                </div>
            <#elseif editMode>
                <div class="ds_card  cms-blank">
                    <div class="ds_card__media">
                        <@placeholderimage/>
                    </div>
                    <div class="ds_card__content  ds_category-item">
                        <h2 class="ds_category-item__title">
                            <a><@placeholdertext lines=1/></a>
                        </h2>

                        <div class="ds_category-item__summary">
                            <@placeholdertext lines=4/>
                        </div>
                    </div>

                    <@hst.manageContent documentTemplateQuery="new-imageandtext-document" parameterName="document2" rootPath="images"/>
                </div>
            </#if>

            <#if document3??>
                <div class="ds_card  ds_card--has-hover">
                    <div class="ds_card__media">
                        <img class="ds_card__image" src="<@hst.link hippobean=document3.image />" alt="${document3.alt?html}"/>
                    </div>
                    <div class="ds_card__content  ds_category-item">
                        <h2 class="ds_category-item__title"><a class="ds_card__cover-link" href="#">Image card block</a></h2>

                        <div class="ds_category-item__summary"><@hst.html hippohtml=document3.content/></div>
                    </div>

                    <@hst.manageContent hippobean=document3 documentTemplateQuery="new-imageandtext-document" parameterName="document3" rootPath="images"/>
                </div>
            <#elseif editMode>
                <div class="ds_card  cms-blank">
                    <div class="ds_card__media">
                        <@placeholderimage/>
                    </div>
                    <div class="ds_card__content  ds_category-item">
                        <h2 class="ds_category-item__title">
                            <a><@placeholdertext lines=1/></a>
                        </h2>

                        <div class="ds_category-item__summary">
                            <@placeholdertext lines=4/>
                        </div>
                    </div>

                    <@hst.manageContent documentTemplateQuery="new-imageandtext-document" parameterName="document3" rootPath="images"/>
                </div>
            </#if>
        </div>
    </div>
</div>
