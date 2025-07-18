<#ftl output_format="HTML">
<#include "../common/include/imports.ftl">

<#assign variables = hstRequestContext.getAttribute("variables")/>
<@hst.messagesReplace escapeMessageXml=false bundle=variables variablePrefix="[[" variableSuffix="]]">
<#if navigation.children?size gt 0>
<!--noindex-->
    <div class="ds_layout__sidebar">
        <nav aria-label="Sections" class="ds_side-navigation" data-module="ds-side-navigation">
        <input type="checkbox" class="fully-hidden  js-toggle-side-navigation" id="show-side-navigation" aria-controls="side-navigation">
        <label class="ds_side-navigation__expand ds_link fully-hidden" for="show-side-navigation">
            <span class="visually-hidden">Show all</span> Pages in this section <span class="ds_side-navigation__expand-indicator">
            </span>
        </label>

        <ul id="side-navigation" class="ds_side-navigation__list">
            <#list navigation.children as child>
               <li class="ds_side-navigation__item">

                    <#if child.currentItem>111
                        <a class="ds_side-navigation__link  ds_current" href="<@hst.link link=child.link/>" aria-current="page">
                            ${child.title}
                        </a>
                    <#else>222
                        <a class="ds_side-navigation__link" href="<@hst.link link=child.link/>">
                            ${child.title}
                        </a>
                    </#if>

                    <#if child.children?size gt 0>333
                        <ul id="side-navigation" class="ds_side-navigation__list">
                            <#list child.children as grandchild>444
                                <li class="ds_side-navigation__item">
                                    <#if grandchild.currentItem>555
                                        <a class="ds_side-navigation__link  ds_current" href="<@hst.link link=grandchild.link/>" aria-current="page">
                                            ${grandchild.title}
                                        </a>
                                    <#else>666
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
    </div>
<!--endnoindex-->
</#if>
</@hst.messagesReplace>
