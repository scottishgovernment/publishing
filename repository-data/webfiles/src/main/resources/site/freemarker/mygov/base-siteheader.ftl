<#ftl output_format="HTML">
<#include "../common/include/imports.ftl">
<@hst.webfile var="iconspath" path="/assets/images/icons/icons.stack.svg"/>

<@hst.link var="home" siteMapItemRefId="root" />

<#-- rendered here (rather than in the seo component) so that every page always has a
     canonical link, even when no seo component has been added to it -->
<#if canonical?has_content>
    <@hst.headContribution category="canonical">
        <link rel="canonical" href="${canonical}" />
    </@hst.headContribution>
    <@hst.headContribution category="openGraph">
        <meta property="og:url" content="${canonical}" />
    </@hst.headContribution>
</#if>

<header class="ds_site-header" role="banner">
    <div class="ds_wrapper">
        <div class="ds_site-header__content">
            <div class="ds_site-branding">
                <a class="ds_site-branding__logo  ds_site-branding__link" href="<@hst.link path="/"/>">
                    <img width="210" height="40" class="ds_site-branding__logo-image" src="<@hst.webfile path="/assets/images/logos/mygov.svg" />" alt="mygov.scot">
                </a>
            </div>
            <@hst.include ref="search-bar"/>
        </div>
    </div>
</header>
