<#include "../common/include/imports.ftl">
<@hst.webfile var="iconspath" path="/assets/images/icons/icons.stack.svg"/>

<@hst.link var="home" siteMapItemRefId="root" />
<header class="ds_site-header" role="banner">
    <div class="ds_wrapper">
        <div class="ds_site-header__content">
            <div class="ds_site-branding">
                <a class="ds_site-branding__logo  ds_site-branding__link" href="/">
                    <#if logo??>
                         <@hst.link var="img" hippobean=logo />
                        <img class="ds_site-branding__logo-image" src="${img}" alt="The Scottish Government" width="${logo.original.width}" height="${logo.original.height}"/>
                    <#else>
                        <img class="ds_site-branding__logo-image" src="<@hst.webfile path="/assets/images/logos/scottish-government.svg"/>" alt="The Scottish Government" width="300" height="56"/>
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
