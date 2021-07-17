<#include "../common/include/imports.ftl">
<div class="ds_site-header__navigation">
    <div class="ds_wrapper">
        <#if menu??>
            <#if menu.siteMenuItems??>
                <nav class="ds_site-navigation">
                    <ul class="ds_site-navigation__list">
                        <#list menu.siteMenuItems as item>
                            <li class="ds_site-navigation__item <#if item.selected || item.expanded>ds_current</#if>">
                                <a href="<@hst.link link=item.hstLink/>" class="ds_site-navigation__link"><span class="label-nav">${item.name?html}</span> </a>
                            </li>
                        </#list>
                    </ul>
                </nav>
            </#if>
            <@hst.cmseditmenu menu=menu/>
        </#if>
    </div>
</div>