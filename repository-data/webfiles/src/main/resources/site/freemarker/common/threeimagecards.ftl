<#include "../common/include/imports.ftl">

<#-- @ftlvariable name="document1" type="scot.mygov.publishing.beans.Imageandtext" -->
<#-- @ftlvariable name="document2" type="scot.mygov.publishing.beans.Imageandtext" -->
<#-- @ftlvariable name="document3" type="scot.mygov.publishing.beans.Imageandtext" -->

<#if document1??>

    <img src="<@hst.link hippobean=document1.image />" alt="${document1.alt?html}"/>

    <#if document2??>
        <img src="<@hst.link hippobean=document2.image />" alt="${document2.alt?html}"/>
    <#else>
        <@hst.manageContent hippobean=item parameterName="document2" rootPath="images"/>
        <div>Choose image 2</div>
    </#if>

    <#if document3??>
        <img src="<@hst.link hippobean=document3.image />" alt="${document3.alt?html}"/>
    <#else>
        <@hst.manageContent hippobean=item parameterName="document3" rootPath="images"/>
        <div>Choose image 3</div>
    </#if>

<#elseif editMode>
    <div>
        <img src="<@hst.link path='/images/essentials/catalog-component-icons/carousel.svg'/>"> Click to edit Images
    </div>
    <div class="has-new-content-button">
        <@hst.manageContent documentTemplateQuery="new-imageandtext-document" parameterName="document1" rootPath="images"/>
    </div>
</#if>
