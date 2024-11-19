<#ftl output_format="HTML">
<#include "../common/include/imports.ftl">
<@hst.webfile var="iconspath" path="/assets/images/icons/icons.stack.svg"/>

<@hst.link var="home" siteMapItemRefId="root" />

<header class="ds_site-header" role="banner">
    <div class="ds_wrapper">
        <div class="ds_site-header__content">
            <div class="ds_site-branding">
                <a class="ds_site-branding__logo  ds_site-branding__link" href="<@hst.link path="/"/>">
                    <img width="300" height="58" class="ds_site-branding__logo-image" src="<@hst.webfile path="/assets/images/logos/mygov.svg" />" alt="mygov.scot">
                </a>

                <#if isServicePage>
                    <div class="ds_site-branding__title">
                        ${serviceName}
                    </div>
                </#if>
            </div>

            <#if !isServicePage>
                <@hst.include ref="search-bar"/>
            </#if>
        </div>
    </div>
</header>
