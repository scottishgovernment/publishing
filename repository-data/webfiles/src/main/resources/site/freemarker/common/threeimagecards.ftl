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
                    <div class="ds_card__media">
                        <img class="ds_card__image" src="<@hst.link hippobean=document1.image />" alt="${document1.alt}"/>
                    </div>
                    <div class="ds_card__content  ds_category-item">
                        <h2 class="ds_category-item__title"><a class="ds_card__cover-link" href="<@hst.link hippobean=document1.link/>">${document1.title}</a></h2>

                        <div class="ds_category-item__summary">${document1.text}</div>
                    </div>

                    <@hst.manageContent hippobean=document1 documentTemplateQuery="new-navigationcard-document" parameterName="document1" rootPath="images"/>
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

                    <@hst.manageContent documentTemplateQuery="new-navigationcard-document" parameterName="document1" rootPath="images"/>
                </div>
            </#if>

            <#if document2??>
                <div class="ds_card  ds_card--has-hover">
                    <div class="ds_card__media">
                        <img class="ds_card__image" src="<@hst.link hippobean=document2.image />" alt="${document2.alt}"/>
                    </div>
                    <div class="ds_card__content  ds_category-item">
                        <h2 class="ds_category-item__title"><a class="ds_card__cover-link" href="<@hst.link hippobean=document2.link/>">${document2.title}</a></h2>

                        <div class="ds_category-item__summary">${document2.text}</div>
                    </div>

                    <@hst.manageContent hippobean=document1 documentTemplateQuery="new-navigationcard-document" parameterName="document2" rootPath="images"/>
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

                    <@hst.manageContent documentTemplateQuery="new-navigationcard-document" parameterName="document2" rootPath="images"/>
                </div>
            </#if>

            <#if document3??>
                <div class="ds_card  ds_card--has-hover">
                    <div class="ds_card__media">
                        <img class="ds_card__image" src="<@hst.link hippobean=document3.image />" alt="${document3.alt}"/>
                    </div>
                    <div class="ds_card__content  ds_category-item">
                        <h2 class="ds_category-item__title"><a class="ds_card__cover-link" href="<@hst.link hippobean=document3.link/>">${document3.title}</a></h2>

                        <div class="ds_category-item__summary">${document3.text}</div>
                    </div>

                    <@hst.manageContent hippobean=document1 documentTemplateQuery="new-navigationcard-document" parameterName="document3" rootPath="images"/>
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

                    <@hst.manageContent documentTemplateQuery="new-navigationcard-document" parameterName="document3" rootPath="images"/>
                </div>
            </#if>
        </div>
    </div>
</div>
