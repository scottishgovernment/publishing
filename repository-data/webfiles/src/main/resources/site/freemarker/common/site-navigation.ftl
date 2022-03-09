<#include "../common/include/imports.ftl">
<#if menu??>
    <#if menu.siteMenuItems?? && menu.siteMenuItems?has_content>
        <div class="ds_site-header__navigation">
            <div class="ds_wrapper">
                <nav class="ds_site-navigation">
                    <ul class="ds_site-navigation__list">
                        <#list menu.siteMenuItems as item>
                            <li class="ds_site-navigation__item">
                                <a href="<@hst.link link=item.hstLink/>" class="ds_site-navigation__link  <#if item.selected || item.expanded>ds_current</#if>"><span class="label-nav">${item.name?html}</span> </a>
                            </li>
                        </#list>
                    </ul>
                </nav>
                <@hst.cmseditmenu menu=menu/>
            </div>
        </div>
    </#if>
</#if>
