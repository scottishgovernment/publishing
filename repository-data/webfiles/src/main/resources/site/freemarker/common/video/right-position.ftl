<#include "include/imports.ftl">
<#-- @ftlvariable name="document" type="scot.mygov.publishing.beans.Video" -->
<#if document??>
<p>Right position variant</p>
<div class="content-block content-block--image-text content-block--blue content-block--fullwidth">
    <div class="ds_wrapper">
        <div class="content-block__inner">
            <div class="content-block  content-block--image-text  content-block--blue  content-block--fullwidth">
                <@hst.manageContent hippobean=document parameterName="document" rootPath="videos"/>
                <div class="ds_wrapper">
                    <div class="content-block__inner">

                        <div class="content-block__text">
                            <@hst.html hippohtml=document.content/>
                        </div>

                        <div class="content-block__poster">
                            <a href="${document.url}">
                                <img src="<@hst.link hippobean=document.image /> " alt="${document.alt?html}"/>
                            </a>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<#elseif editMode>

<div>
    <figure>
        <@hst.manageContent documentTemplateQuery="new-video-document" parameterName="document" rootPath="videos"/>
        Click to edit video component
    </figure>
</div>
</#if>
