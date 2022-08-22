<#ftl output_format="HTML">
<#include "../common/include/imports.ftl">
<#if menu??>
    <#if menu.siteMenuItems?? && menu.siteMenuItems?has_content>
        <div class="ds_site-header__navigation">
            <div class="ds_wrapper">
                <nav class="ds_site-navigation">
                    <ul class="ds_site-navigation__list">
                        <#list menu.siteMenuItems as item>
                            <li class="ds_site-navigation__item">
                                <a href="<@hst.link link=item.hstLink/>" class="ds_site-navigation__link  <#if item.selected || item.expanded>ds_current</#if>"><span class="label-nav">${item.name}</span> </a>
                            </li>
                        </#list>
                    </ul>
                </nav>
            </div>
        </div>
    </#if>

    <@hst.cmseditmenu menu=menu/>
</#if>
