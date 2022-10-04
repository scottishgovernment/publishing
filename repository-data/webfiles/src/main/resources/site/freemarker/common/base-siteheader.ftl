<#ftl output_format="HTML">
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
                        <#if document??>
                            <#if document.logo??>
                                <img class="ds_site-branding__logo-image" src="<@hst.link hippobean=document.logo/>" alt="The Scottish Government" />
                            </#if>
                        <#else>
                            <img width="269" height="40" class="ds_site-branding__logo-image" src="<@hst.webfile path="/assets/images/logos/scottish-government.svg"/>" alt="The Scottish Government" />
                        </#if>
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
