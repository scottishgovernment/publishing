<#include "../include/imports.ftl">
<p>Right aligned template</p>
<#-- @ftlvariable name="document" type="scot.mygov.publishing.beans.Imageandtext" -->
<#if document??>

<div class="content-block  content-block--image-text  content-block--blue  content-block--fullwidth">
    <@hst.manageContent hippobean=document parameterName="document" rootPath="images"/>
    <div class="ds_wrapper">
        <div class="content-block__inner">

            <div class="content-block__text">
                <@hst.html hippohtml=document.content/>
            </div>

            <div class="content-block__poster">
                <img src="<@hst.link hippobean=document.image />" alt="${document.alt?html}"/>
            </div>
        </div>
    </div>
</div>

<#elseif editMode>

<div>
    <figure>
        <@hst.manageContent documentTemplateQuery="new-imageandtext-document" parameterName="document" rootPath="images"/>
        Click to edit Image and text
    </figure>
</div>
</#if>
