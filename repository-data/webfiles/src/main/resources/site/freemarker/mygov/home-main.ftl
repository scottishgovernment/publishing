<#ftl output_format="HTML">
<#include "../common/include/imports.ftl">
<#include "../common/macros/content-blocks.ftl">
<@hst.webfile var="iconspath" path="/assets/images/icons/icons.stack.svg"/>

<#if document??>
<#assign variables = hstRequestContext.getAttribute("variables")/>
<@hst.messagesReplace escapeMessageXml=false bundle=variables variablePrefix="[[" variableSuffix="]]">
<div class="cms-editable">
    <@hst.manageContent hippobean=document />

    <main id="main-content">
        <div class="category-upper">
            <div class="ds_wrapper">

                <header class="ds_feature-header  ds_feature-header--wide  mg_hero">
                    <div class="ds_feature-header__primary">
                        <h1 class="ds_feature-header__title">Access to public services in Scotland</h1>

                        <#if autoCompleteEnabled>
                            <#assign ds_autocomplete = true />
                        </#if>
                        <#if searchEnabled>
                            <#assign searchcategory = "sitesearch" />
                            <#include '../common/include/search.ftl'/>
                        </#if>
                    </div>
                </header>
            </div>
        </div>


        <div class="category-lower  ds_pre-footer-background">
            <div class="ds_wrapper">
                <#if document.prologueContentBlocks??>
                    <@renderContentBlocks document.prologueContentBlocks />
                </#if>

                <div class="ds_layout  ds_layout--category-list">
                    <div class="ds_layout__content">
                        <#if document.popularpanelitem?has_content>
                            <div class="popular">
                                <h2 class="popular__title">Popular on mygov.scot:</h2>
                                <ul class="popular__items">
                                    <#list document.popularpanelitem as items>
                                        <li class="popular__item">
                                            <@hst.link var="link" hippobean=items.link/>
                                            <svg class="popular__icon" role="img" focusable="false" aria-hidden="true"></svg>
                                            <a class="popular__link" href="${link}" data-popular="${items.title}">${items.title}</a>
                                        </li>
                                    </#list>
                                </ul>
                            </div>
                        </#if>

                        <div class="org-az">
                            <@hst.link var="organisations" path="/organisations"/>
                            <div class="org-az__description">
                                <p>We're working with over <a href="${organisations}">150 organisations</a> to deliver their services online.</p>
                            </div>
                            <div>
                                <a href="${organisations}" class="org-az__link  ds_button  ds_button--max  ds_button--secondary  ds_button--has-icon  ds_no-margin">
                                    A-Z of Scotland's organisations
                                    <span class="org-az__link-icon" aria-hidden="true"></span>
                                </a>
                            </div>
                        </div>
                    </div>

                    <#if document.navigationType == "list">
                    <div class="ds_layout__list">
                    <#else>
                    <div class="ds_layout__grid">
                    </#if>
                        <div class="ds_category-list-container">
                            <#if document.navigationType == "image-card">
                                <#include '../common/card-navigation--image.ftl'/>
                            </#if>
                            <#if document.navigationType == "card">
                                <#include '../common/card-navigation.ftl'/>
                            </#if>
                            <#if document.navigationType == "grid">
                                <#include '../common/grid-navigation.ftl'/>
                            </#if>
                            <#if document.navigationType == "list">
                                <#include '../common/list-navigation.ftl'/>
                            </#if>
                        </div>
                    </div>
                </div>

                <#if document.epilogueContentBlocks??>
                    <@renderContentBlocks document.epilogueContentBlocks />
                </#if>

                <div class="ds_layout  ds_layout--category-list">
                    <@hst.include ref="feedback"/>
                </div>
            </div>
        </div>

    </main>
</div>
</@hst.messagesReplace>
</#if>

<#assign scriptName="home">
<#include '../common/scripts.ftl'/>
