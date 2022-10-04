<#ftl output_format="HTML">
<#include "../common/include/imports.ftl">

<@hst.link var="home" siteMapItemRefId="root" />
<header class="ds_site-header  ds_site-header--gradient  ds_site-header--shadow" role="banner">
    <div class="ds_wrapper">
        <div class="ds_site-header__content">
            <div class="ds_site-branding">
                <a class="ds_site-branding__logo  ds_site-branding__link" href="${home}">
                    <img class="ds_site-branding__logo-image" src="<@hst.webfile path="/assets/images/logos/scottish-government--monochrome.svg"/>" alt="The Scottish Government" width="269" height="40" />
                </a>

                <div class="ds_site-branding__title">
                    <a class="ds_site-branding__link" href="${home}">A Trading Nation</a>
                </div>
            </div>
        </div>
    </div>
</header>
