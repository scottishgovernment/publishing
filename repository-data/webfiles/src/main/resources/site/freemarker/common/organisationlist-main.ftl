<#ftl output_format="HTML">
<#include "include/imports.ftl">
<#include "macros/content-blocks.ftl">

<#assign variables = hstRequestContext.getAttribute("variables")/>
<@hst.messagesReplace escapeMessageXml=false bundle=variables variablePrefix="[[" variableSuffix="]]">

<#if document??>
<div class="cms-editable">
    <@hst.manageContent hippobean=document />

    <@hst.include ref="breadcrumbs"/>

    <div class="ds_wrapper">
        <main id="main-content" class="ds_layout  mg_layout--org-list">
            <div class="ds_layout__header">
                <header class="ds_page-header">
                    <h1 class="ds_page-header__title">${document.title}</h1>
                </header>
            </div>

            <div class="ds_layout__content">
                <#if document.contentBlocks??>
                    <@renderContentBlocks document.contentBlocks />
                </#if>
            </div>

            <div class="ds_layout__org-list">
                <div class="mg_tab-container  mg_tab-container--3" data-module="mg-tabs">
                    <input class="mg_tab__radio" type="radio" checked="" name="tabs" id="tab1" />
                    <div class="mg_tab__header"><label id="tab1-label" aria-controls="tab1-content" role="tab" class="mg_tab__label" for="tab1">Organisations by sector</label></div>
                    <div tabindex="0" id="tab1-content" class="mg_tab__content" role="tabpanel" aria-labelledby="tab1-label">
                        <!--Org list by sector-->
                        <ul class="sectors">
                            <#list orgsBySector as orgAndSector>
                                <li class="sector">
                                    <h2 class="sector__title">
                                        <a class="sector__link" href="#${orgAndSector.sector.name}" data-gtm="orgs-sector-${orgAndSector?index+1}">${orgAndSector.sector.displayName}</a>
                                    </h2>
                                    <p class="sector__summary">
                                        ${orgAndSector.sector.description}
                                    </p>
                                    <p class="sector__count">
                                        ${orgAndSector.organisations?size}<span class="visually-hidden">organisations in this sector</span>
                                    </p>
                                </li>
                            </#list>
                        </ul>

                        <#list orgsBySector as orgAndSector>
                            <ul class="sector-list">
                                <li class="sector-list__item">
                                    <h2 class="sector-list__title" id="${orgAndSector.sector.name}">${orgAndSector.sector.displayName}</h2>
                                    <ol class="sector-list__sublist">
                                        <#list orgAndSector.organisations as org>
                                            <li class="sector-list__sublist-item">
                                                <@hst.link var="link" hippobean=org/>
                                                <a href="${link}">${org.title}</a>
                                            </li>
                                        </#list>
                                    </ol>
                                </li>
                            </ul>
                        </#list>
                    </div>

                    <input class="mg_tab__radio" type="radio" name="tabs" id="tab2" />
                    <div class="mg_tab__header"><label id="tab2-label" aria-controls="tab2-content" role="tab" class="mg_tab__label" for="tab2">Organisations by name</label></div>
                    <div tabindex="0" id="tab2-content" class="mg_tab__content" role="tabpanel" aria-labelledby="tab2-label">

                        <#assign alph = 'abcdefghijklmnopqrstuvwxyz'?split('')>
                        <nav aria-label="organisations by letter" id="back-to-top">
                            <ol class="az-nav">
                                <#list 'abcdefghijklmnopqrstuvwxyz'?split('') as abc>
                                    <li class="az-nav__item">
                                        <#assign letterFound = false>
                                        <#list orgsByLetter as org>
                                            <#if org.letter?lower_case == abc>
                                                <#assign letterFound = true>
                                            </#if>
                                        </#list>
                                        <#if letterFound>
                                            <a class="az-nav__link" data-gtm="orgs-${abc}" href="#${abc?upper_case}">${abc?upper_case}</a>
                                        <#else>
                                            <span class="az-nav__link">${abc?upper_case}</span>
                                        </#if>
                                    </li>
                                </#list>
                            </ol>
                        </nav>

                        <ol class="az-list">
                            <#list orgsByLetter as orgAndLetter>
                                <li class="az-list__item">
                                    <h2 id="${orgAndLetter.letter}" class="az-list__title">${orgAndLetter.letter}</a></h2>
                                    <ol class="az-list__sublist">
                                        <#list orgAndLetter.organisations as org>
                                            <li class="az-list__sublist-item">
                                                <@hst.link var="link" hippobean=org/>
                                                <a href="${link}">${org.title}</a>
                                            </li>
                                        </#list>
                                    </ol>
                                </li>
                            </#list>
                        </ol>
                    </div>
                </div>
            </div>
        </main>
    </div>
</div>
</#if>
</@hst.messagesReplace>

<#assign scriptName="organisation-list">
<#include 'scripts.ftl'/>
