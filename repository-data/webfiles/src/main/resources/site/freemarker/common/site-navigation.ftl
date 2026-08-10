<#ftl output_format="HTML">
<#include "../common/include/imports.ftl">
<#if menu??>
    <#-- a page can nominate the top level nav item it lives under, see PageNavigationComponent -->
    <#assign topLevelNavItemOverride = (hstRequest.requestContext.getAttribute("topLevelNavItem"))!"" />
    <#if menu.siteMenuItems?? && menu.siteMenuItems?has_content>
        <div class="ds_site-header__navigation">
            <div class="ds_wrapper">
                <nav class="ds_site-navigation">
                    <ul class="ds_site-navigation__list">
                        <#list menu.siteMenuItems as item>
                            <#assign isCurrent = item.selected || item.expanded || (item.hstLink?? && topLevelNavItemOverride?has_content && topLevelNavItemOverride == item.hstLink.path) />
                            <li class="ds_site-navigation__item">
                                <#if item.externalLink??>
                                    <a href="${item.externalLink}" class="ds_site-navigation__link  <#if isCurrent>ds_current" aria-current="true</#if>"><span class="label-nav">${item.name}</span> </a>
                                <#elseif item.hstLink??>
                                    <a href="<@hst.link link=item.hstLink/>" class="ds_site-navigation__link  <#if isCurrent>ds_current" aria-current="true</#if>"><span class="label-nav">${item.name}</span> </a>
                                <#else>
                                    <span class="ds_site-navigation__link  <#if isCurrent>ds_current" aria-current="true</#if>"><span class="label-nav">${item.name}</span> </span>
                                </#if>
                            </li>
                        </#list>
                    </ul>
                </nav>
            </div>
        </div>
    </#if>

    <@hst.cmseditmenu menu=menu/>
</#if>
