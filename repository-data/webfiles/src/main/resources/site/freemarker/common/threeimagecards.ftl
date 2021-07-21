<#include "../common/include/imports.ftl">

<#-- @ftlvariable name="document1" type="scot.mygov.publishing.beans.Imageandtext" -->
<#-- @ftlvariable name="document2" type="scot.mygov.publishing.beans.Imageandtext" -->
<#-- @ftlvariable name="document3" type="scot.mygov.publishing.beans.Imageandtext" -->

<#if document1??>
<div class="ds_cb  ds_cb--cards  ds_cb--bg-grey  <#if fullwidth>ds_cb--fullwidth</#if>">
    <div class="ds_wrapper">
        <div class="ds_cb__inner">
            <div class="ds_card  ds_card--has-hover">
                <#if document1??>
                <div class="ds_card__media">
                    <img class="ds_card__image" src="<@hst.link hippobean=document1.image />" alt="${document1.alt?html}"/>
                </div>
                <div class="ds_card__content  ds_category-item">
                    <h2 class="ds_category-item__title"><a class="ds_card__cover-link" href="#">Image card block</a></h2>

                    <div class="ds_category-item__summary"><@hst.html hippohtml=document1.content/></div>
                </div>
                <#else>
                    <div class="ds_card__content">
                        <@hst.manageContent hippobean=item parameterName="document1" rootPath="images"/>
                        <div>Choose card</div>
                    </div>
                </#if>
            </div>

            <div class="ds_card  ds_card--has-hover">
                <#if document2??>
                <div class="ds_card__media">
                    <img class="ds_card__image" src="<@hst.link hippobean=document2.image />" alt="${document2.alt?html}" />
                </div>
                <div class="ds_card__content  ds_category-item">
                    <h2 class="ds_category-item__title"><a class="ds_card__cover-link" href="#">Image card block</a></h2>

                    <div class="ds_category-item__summary"><@hst.html hippohtml=document2.content/></div>
                </div>
                <#else>
                    <div class="ds_card__content">
                        <@hst.manageContent hippobean=item parameterName="document2" rootPath="images"/>
                        <div>Choose card</div>
                    </div>
                </#if>
            </div>

            <div class="ds_card  ds_card--has-hover">
                <#if document3??>
                <div class="ds_card__media">
                    <img class="ds_card__image" src="<@hst.link hippobean=document3.image />" alt="${document3.alt?html}" />
                </div>
                <div class="ds_card__content  ds_category-item">
                    <h2 class="ds_category-item__title"><a class="ds_card__cover-link" href="#">Image card block</a></h2>

                    <div class="ds_category-item__summary"><@hst.html hippohtml=document3.content/></div>
                </div>
                <#else>
                    <div class="ds_card__content">
                        <@hst.manageContent hippobean=item parameterName="document3" rootPath="images"/>
                        <div>Choose card</div>
                    </div>
                </#if>
            </div>
        </div>
    </div>
</div>

<#elseif editMode>
    <div class="ds_wrapper">
        <div>
            <img src="<@hst.link path='/images/essentials/catalog-component-icons/carousel.svg'/>"> Click to edit Images
        </div>
        <div class="has-new-content-button">
            <@hst.manageContent documentTemplateQuery="new-imageandtext-document" parameterName="document1" rootPath="images"/>
        </div>
    </div>
</#if>
