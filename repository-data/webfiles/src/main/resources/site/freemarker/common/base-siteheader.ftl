<#include "../common/include/imports.ftl">
<@hst.webfile var="iconspath" path="/assets/images/icons/icons.stack.svg"/>

<@hst.link var="home" siteMapItemRefId="root" />
<header class="ds_site-header" role="banner">
    <div class="ds_wrapper">
        <div class="ds_site-header__content">
            <div class="ds_site-branding">
                <a class="ds_site-branding__logo  ds_site-branding__link" href="/">
                    <#if document.bean.logo??>
                        <img class="ds_site-branding__logo-image" src="<@hst.link hippobean=document.bean.logo/>" alt="The Scottish Government" />
                    <#else>
                        <img class="ds_site-branding__logo-image" src="<@hst.webfile path="/assets/images/logos/scottish-government.svg"/>" alt="The Scottish Government" />
                    </#if>
                </a>

                <#if siteTitle??>
                    <div class="ds_site-branding__title">
                        ${siteTitle}
                    </div>
                </#if>
            </div>

            <@hst.include ref="mobilemenu"/>
        </div>
    </div>

    <@hst.include ref="menu"/>
</header>
