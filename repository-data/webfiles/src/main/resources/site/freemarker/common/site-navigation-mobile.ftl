<#ftl output_format="HTML">
<#include "../common/include/imports.ftl">

<@hst.webfile var="iconspath" path="/assets/images/icons/icons.stack.svg"/>

<#if menu??>
    <#if menu.siteMenuItems?? && menu.siteMenuItems?has_content>
        <div class="ds_site-header__controls">
            <label aria-controls="mobile-navigation" class="ds_site-header__control  js-toggle-menu" for="menu">
                <span class="ds_site-header__control-text">Menu</span>
                <svg class="ds_icon  ds_site-header__control-icon" aria-hidden="true" role="img"><use href="${iconspath}#menu"></use></svg>
                <svg class="ds_icon  ds_site-header__control-icon  ds_site-header__control-icon--active-icon" aria-hidden="true" role="img"><use href="${iconspath}#close"></use></svg>
            </label>
        </div>

        <input class="ds_site-navigation__toggle" id="menu" type="checkbox" />
        <nav id="mobile-navigation" class="ds_site-navigation  ds_site-navigation--mobile" data-module="ds-mobile-navigation-menu">
            <ul class="ds_site-navigation__list">
                <#list menu.siteMenuItems as item>
                    <li class="ds_site-navigation__item">
                        <#if item.externalLink??>
                            <a href="${item.externalLink}" class="ds_site-navigation__link  <#if item.selected || item.expanded>ds_current" aria-current="true</#if>"><span class="label-nav">${item.name}</span> </a>
                        <#elseif item.hstLink??>
                            <a href="<@hst.link link=item.hstLink/>" class="ds_site-navigation__link  <#if item.selected || item.expanded || hstRequest.requestContext.baseURL.requestPath?ends_with("/"+item.hstLink.path)>ds_current" aria-current="true</#if>"><span class="label-nav">${item.name}</span> </a>
                        <#else>
                            <span class="ds_site-navigation__link  <#if item.selected || item.expanded>ds_current" aria-current="true</#if>"><span class="label-nav">${item.name}</span> </span>
                        </#if>
                    </li>
                </#list>
            </ul>
        </nav>
    </#if>
</#if>
