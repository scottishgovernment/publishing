<#ftl output_format="HTML">
<#include "include/imports.ftl">

<!--noindex-->
<div class="ds_layout__sidebar">


<#list stepbysteps as stepbystep>
    <div class="ds_article-aside">
        <h2 class="gamma">
            Part of<br />
            <a href="<@hst.link var=link hippobean=stepbystep/>">${stepbystep.title}</a>
        </h2>
    </div>

    <div class="ds_accordion  ds_step-navigation" data-module="ds-accordion">
        <button data-accordion="accordion-open-all" type="button" class="ds_link  ds_accordion__open-all  js-open-all">Open all <span class="visually-hidden">sections</span></button>

        <#list stepbystep.steps as step>
            <#assign isactivestep = step.identifier == currentStep.identifier />
            <div class="<#if isactivestep>ds_accordion-item--open</#if>  ds_accordion-item  <#if step.labeltype == "or">ds_step-navigation__or<#elseif step.labeltype == "and">ds_step-navigation__and</#if>">
                <input <#if isactivestep>checked</#if> type="checkbox" class="visually-hidden  ds_accordion-item__control" id="panel-${step?index}" aria-labelledby="panel-${step?index}-heading" />
                <div class="ds_accordion-item__header">
                    <h3 id="panel-${step?index}-heading" class="ds_accordion-item__title">
                    ${step.title}
                    </h3>
                    <span class="ds_accordion-item__indicator"></span>
                    <label class="ds_accordion-item__label" for="panel-${step?index}"><span class="visually-hidden">Show this section</span></label>
                </div>

                <div class="ds_accordion-item__body" style="padding-right: 16px;">
                    <@hst.html hippohtml=step.content/>
                </div>
            </div>
        </#list>

    </div>

</#list>



<#if document.relateditems?has_content >
    <aside class="ds_article-aside">
        <h2 class="gamma">Related content</h2>
        <ul class="ds_no-bullets">
            <#list document.relateditems as item>
                <#list item.relatedItem as link>
                    <@hst.link var="url" hippobean=link/>
                    <li>
                        <a href="${url}">${link.title}</a>
                    </li>
                </#list>
            </#list>
        </ul>
    </aside>
</#if>

<#if document.sensitive?? && document.sensitive>
    <aside class="ds_article-aside" id="stay-safe-online">
        <h3>Stay safe online &hellip;</h3>
        <ul class="ds_no-bullets">
            <li>
                <a href="/staying-safe-online/deleting-your-browser-history" data-navigation="staysafe-yes">Deleting your history and staying safe online</a>
            </li>
        </ul>
    </aside>
</#if>
</div>
<!--endnoindex-->
