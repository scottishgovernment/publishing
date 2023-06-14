<#ftl output_format="HTML">
<#include "../common/include/imports.ftl">
<!--noindex-->

<#if navigation.children?size gt 0>
    <div class="ds_layout__sidebar">
        <nav aria-label="Sections" class="ds_side-navigation js-initialised" data-module="ds-side-navigation">
        <input type="checkbox" class="fully-hidden  js-toggle-side-navigation" id="show-side-navigation" aria-controls="side-navigation">
        <label class="ds_side-navigation__expand ds_link fully-hidden" for="show-side-navigation">
            <span class="visually-hidden">Show all</span> Pages in this section <span class="ds_side-navigation__expand-indicator">
            </span>
        </label>

        <ul id="side-navigation" class="ds_side-navigation__list">
            <#list navigation.children as child>
               <li class="ds_side-navigation__item">

                    <#if child.currentItem>
                        <span class="ds_side-navigation__link  ds_current">
                            ${child.title}
                        </span>
                    <#else>
                        <a class="ds_side-navigation__link" href="<@hst.link link=child.link/>">
                            ${child.title}
                        </a>
                    </#if>

                    <#if child.children?size gt 0>
                        <ul id="side-navigation" class="ds_side-navigation__list">
                            <#list child.children as grandchild>
                                <#if grandchild.currentItem>
                                    <span class="ds_side-navigation__link  ds_current">
                                        ${grandchild.title}
                                    </span>
                                <#else>
                                    <a class="ds_side-navigation__link" href="<@hst.link link=grandchild.link/>">
                                        ${grandchild.title}
                                    </a>
                                </#if>
                            </#list>
                        </ul>
                    </#if>
                </li>
            </#list>
        </ul>
    </div>
</#if>
