<#ftl output_format="HTML">
<#include "../common/include/imports.ftl">
<@hst.webfile var="iconspath" path="/assets/images/icons/icons.stack.svg"/>
<#assign variables = hstRequestContext.getAttribute("variables")/>
<@hst.messagesReplace escapeMessageXml=false bundle=variables variablePrefix="[[" variableSuffix="]]">

<@hst.link var="home" siteMapItemRefId="root" />
<header class="ds_site-header" role="banner">
    <div class="ds_wrapper">
        <div class="ds_site-header__content">
            <div class="ds_site-branding">
                <a class="ds_site-branding__logo  ds_site-branding__link" href="/">
                    <img width="300" height="58" class="ds_site-branding__logo-image" src="<@hst.webfile path="/assets/images/logos/${logo}.svg" />" alt="${logoAltText}">
                </a>

                <#if siteTitle?? && displaySiteTitleInHeader>
                    <div class="ds_site-branding__title">
                        ${siteTitle}
                    </div>
                </#if>
            </div>

            <@hst.include ref="mobilemenu"/>

            <@hst.include ref="search-bar"/>
        </div>
    </div>

    <#if phasebanner??>
        <div class="ds_phase-banner">
            <div class="ds_wrapper">
                <p class="ds_phase-banner__content">
                    <strong class="ds_tag  ds_phase-banner__tag">
                        ${phasebanner.tag}
                    </strong>
                    <span class="ds_phase-banner__text">
                        <@hst.html hippohtml=phasebanner.content/>
                    </span>
                </p>
            </div>
        </div>
    </#if>

    <@hst.include ref="menu"/>
</header>
</@hst.messagesReplace>