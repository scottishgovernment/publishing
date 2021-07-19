<#include "../common/include/imports.ftl">

<#-- @ftlvariable name="document" type="scot.mygov.publishing.beans.Imageandtext" -->
<#-- @ftlvariable name="pageable" type="org.onehippo.cms7.essentials.components.paging.Pageable" -->
<#-- @ftlvariable name="cparam" type="scot.mygov.publishing.components.ThreeImageCardsComponent" -->
<#if pageable?? && pageable.items?has_content>
    <div>
        <div class="carousel-inner">
            <#list pageable.items as item>
                <div>
                    <#-- button to edit shown item -->
                    <#if configuredItems?? && configuredItems?has_content>
                        <@hst.manageContent hippobean=item parameterName="document${configuredItems[item?index]}" rootPath="images"/>
                    <#else>
                        <@hst.manageContent hippobean=item/>
                    </#if>

                    <img src="<@hst.link hippobean=item.image />" alt="${item.alt?html}"/>
                </div>
            </#list>
        </div>

        <#if editMode && freeItems?? && freeItems?has_content>

            <div>
                <#list freeItems as number>
                    <div class="has-new-content-button item-button"><@hst.manageContent documentTemplateQuery="new-imageandtext-document" parameterName="document${number}" rootPath="images"/></div>
                </#list>
            </div>
        </#if>
    </div>

<#elseif editMode>
    <div>
        <img src="<@hst.link path='/images/essentials/catalog-component-icons/carousel.svg'/>"> Click to edit Images
    </div>
    <div class="has-new-content-button">
        <@hst.manageContent documentTemplateQuery="new-imageandtext-document" parameterName="document1" rootPath="images"/>
    </div>
<#elseif editMode>
<div>
    <figure>
        <@hst.manageContent documentTemplateQuery="new-imageandtext-document" parameterName="document" rootPath="images"/>
        Click to edit Image and text
    </figure>
</div>
</#if>
