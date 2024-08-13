<#ftl output_format="HTML">
<#include "include/imports.ftl">
<#include "macros/lang-attributes.ftl">

<#assign variables = hstRequestContext.getAttribute("variables")/>
<@hst.messagesReplace escapeMessageXml=false bundle=variables variablePrefix="[[" variableSuffix="]]">

<!--noindex-->
<div class="ds_layout__sidebar">

<#if primaryStepByStep??>
    <nav class="ds_step-navigation  ds_step-navigation--small" aria-labelledby="step-navigation-sidebar" data-module="ds-step-navigation">
        <h2 id="step-navigation-sidebar" class="ds_step-navigation__title">
            Part of
            <a class="ds_step-navigation__title-link" href="<@hst.link var=link hippobean=primaryStepByStep.stepByStepGuide/>">${primaryStepByStep.stepByStepGuide.title}</a>
        </h2>

        <div class="ds_accordion  ds_accordion--small" data-module="ds-accordion">
            <button data-accordion="accordion-open-all" type="button" class="ds_link  ds_accordion__open-all  js-open-all">Open all <span class="visually-hidden">sections</span></button>

            <#assign stepcount = 0 />

            <#list primaryStepByStep.stepByStepGuide.steps as step>
                <#assign isactivestep = step.identifier == primaryStepByStep.currentStep.identifier />

                <#if step.labeltype == 'or' || step.labeltype == 'and'>
                <#else>
                    <#assign stepcount = stepcount + 1 />
                </#if>

                <div class="<#if isactivestep>ds_accordion-item--open  ds_current</#if>  ds_accordion-item  ds_accordion-item--small  <#if step.labeltype == "or">ds_step-navigation__or<#elseif step.labeltype == "and">ds_step-navigation__and</#if>" <#if isactivestep>aria-current="step"</#if>>
                    <span class="ds_step-navigation__count">
                        <#if step.labeltype == 'or'>
                            or <span class="visually-hidden">this step</span>
                        <#elseif step.labeltype == 'and'>
                            and <span class="visually-hidden">this step</span>
                        <#else>
                            <span class="visually-hidden">Step </span>${stepcount}
                        </#if>
                    </span>
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
    </nav>

    <#if otherStepByStepGuides?has_content>
        <aside <@revertlang document /> class="ds_article-aside">
            <h2 class="gamma">Also part of</h2>
            <ul class="ds_no-bullets">
                <#list otherStepByStepGuides as stepByStepGuide>
                    <li>
                        <a <@langcompare link document/> href="<@hst.link var=link hippobean=stepByStepGuide.stepByStepGuide/>">
                            ${stepByStepGuide.stepByStepGuide.title}
                        </a>
                    </li>
                </#list>
            </ul>
        </aside>
    </#if>

<#else>
    <#if stepBySteps?size gt 0 >
        <aside <@revertlang document /> class="ds_article-aside">
            <h2 class="gamma">Part of</h2>
            <ul class="ds_no-bullets">
                <#list stepBySteps as stepbystep>
                    <li>
                        <a <@langcompare link document/> href="<@hst.link var=link hippobean=stepbystep.stepByStepGuide/>">
                            ${stepbystep.stepByStepGuide.title}
                        </a>
                    </li>
                </#list>
            </ul>
        </aside>
    </#if>
</#if>

<#if document.relatedItems>
    <aside <@revertlang document /> class="ds_article-aside">
        <h2 class="gamma">Related content</h2>
        <ul class="ds_no-bullets">
            <#list document.relateditems as item>
                <#list item.relatedItem as link>
                    <@hst.link var="url" hippobean=link/>
                    <li>
                        <a <@langcompare link document/> href="${url}">${link.title}</a>
                    </li>
                </#list>
            </#list>
        </ul>
    </aside>
</#if>

<#if document.sensitive?? && document.sensitive>
    <aside <@revertlang document /> class="ds_article-aside" id="stay-safe-online">
        <h3>Stay safe online &hellip;</h3>
        <ul class="ds_no-bullets">
            <li>
                <a href="/staying-safe-online/deleting-your-browser-history" data-navigation="staysafe-yes">Deleting your history and staying safe online</a>
            </li>
        </ul>
    </aside>
</#if>

</div>
</@hst.messagesReplace>
<!--endnoindex-->
