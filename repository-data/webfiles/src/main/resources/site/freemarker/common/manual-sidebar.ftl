<#ftl output_format="HTML">
<#include "include/imports.ftl">

<#assign variables = hstRequestContext.getAttribute("variables")/>
<@hst.messagesReplace escapeMessageXml=false bundle=variables variablePrefix="[[" variableSuffix="]]">
<#if navigation.children?size gt 0>
<!--noindex-->
    <div class="ds_layout__sidebar">
        <nav aria-label="Sections" class="ds_side-navigation  ds_!_margin-top--3  ds_!_margin-bottom--1" data-module="ds-side-navigation">
            <input type="checkbox" class="visually-hidden  js-toggle-side-navigation" id="show-side-navigation" aria-controls="side-navigation">
            <label class="ds_side-navigation__expand ds_link fully-hidden" for="show-side-navigation">
                Choose section <span class="ds_side-navigation__expand-indicator">
                </span>
            </label>

            <ul id="side-navigation" class="ds_side-navigation__list">
                <#list navigation.children as child>
                <li class="ds_side-navigation__item">

                        <#if child.currentItem>
                            <a class="ds_side-navigation__link  ds_current" href="<@hst.link link=child.link/>" aria-current="page">
                                ${child.title}
                            </a>
                        <#else>
                            <a class="ds_side-navigation__link" href="<@hst.link link=child.link/>">
                                ${child.title}
                            </a>
                        </#if>

                        <#assign showSubpagesForChild = false/>
                        <#list child.children as grandchild>
                            <#if grandchild.currentItem || child.currentItem>
                                <#assign showSubpagesForChild = true/>
                            </#if>
                        </#list>

                        <#if child.children?size gt 0 && showSubpagesForChild>
                            <ul id="side-navigation" class="ds_side-navigation__list">
                                <#list child.children as grandchild>
                                    <li class="ds_side-navigation__item">
                                        <#if grandchild.currentItem>
                                            <a class="ds_side-navigation__link  ds_current" href="<@hst.link link=grandchild.link/>" aria-current="page">
                                                ${grandchild.title}
                                            </a>
                                        <#else>
                                            <a class="ds_side-navigation__link" href="<@hst.link link=grandchild.link/>">
                                                ${grandchild.title}
                                            </a>
                                        </#if>
                                    </li>
                                </#list>
                            </ul>
                        </#if>
                    </li>
                </#list>
            </ul>
        </nav>
    </div>
<!--endnoindex-->
</#if>
</@hst.messagesReplace>
