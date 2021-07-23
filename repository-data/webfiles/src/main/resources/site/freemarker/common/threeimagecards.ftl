<#include "include/imports.ftl">
<#include "include/cms-placeholders.ftl">

<#-- @ftlvariable name="document1" type="scot.mygov.publishing.beans.Base" -->
<#-- @ftlvariable name="document2" type="scot.mygov.publishing.beans.Base" -->
<#-- @ftlvariable name="document3" type="scot.mygov.publishing.beans.Base" -->

<div class="ds_cb  ds_cb--cards  ds_cb--bg-grey  <#if fullwidth>ds_cb--fullwidth</#if>">
    <div class="ds_wrapper">
        <div class="ds_cb__inner">
            <#if document1??>
                <div class="ds_card  ds_card--has-hover">
                    <div class="ds_card__media">
                        <img class="ds_card__image" src="<@hst.link hippobean=document1.cardImage />" alt="111"/>
                    </div>
                    <div class="ds_card__content  ds_category-item">
                        <h2 class="ds_category-item__title"><a class="ds_card__cover-link" href="<@hst.link hippobean=document1/>">${document1.title}</a></h2>

                        <div class="ds_category-item__summary">${document1.summary}</div>
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
                        <img class="ds_card__image" src="<@hst.link hippobean=document2.cardImage />" alt="222"/>
                    </div>
                    <div class="ds_card__content  ds_category-item">
                        <h2 class="ds_category-item__title"><a class="ds_card__cover-link" href="<@hst.link hippobean=document2/>">${document2.title}</a></h2>

                        <div class="ds_category-item__summary">${document2.summary}</div>
                    </div>

                    <@hst.manageContent hippobean=document1 documentTemplateQuery="new-imageandtext-document" parameterName="document2" rootPath="images"/>
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
                        <img class="ds_card__image" src="<@hst.link hippobean=document3.cardImage />" alt="333"/>
                    </div>
                    <div class="ds_card__content  ds_category-item">
                        <h2 class="ds_category-item__title"><a class="ds_card__cover-link" href="<@hst.link hippobean=document1/>">${document3.title}</a></h2>

                        <div class="ds_category-item__summary">${document3.summary}</div>
                    </div>

                    <@hst.manageContent hippobean=document1 documentTemplateQuery="new-imageandtext-document" parameterName="document3" rootPath="images"/>
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
