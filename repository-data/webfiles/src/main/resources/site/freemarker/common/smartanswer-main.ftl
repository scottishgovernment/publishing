<#ftl output_format="HTML">
<#include "include/imports.ftl">
<#include "macros/content-blocks.ftl">
<@hst.webfile var="iconspath" path="/assets/images/icons/icons.stack.svg"/>

<#assign variables = hstRequestContext.getAttribute("variables")/>
<@hst.messagesReplace escapeMessageXml=false bundle=variables variablePrefix="[[" variableSuffix="]]">

<#macro dynamicResultsForItem item>
    <#list item.dynamicresults as dynamicresult>
        <#if dynamicresult.prologueContentBlocks??>
            <@renderContentBlocks dynamicresult.prologueContentBlocks />
        </#if>

        <div class="mg_smart-answer__dynamic-result"
            id="dynamic-result-${item.name}-${dynamicresult.question.name}"
            data-location="<@hst.link fullyQualified=true hippobean=root/>fragments${dynamicresult.folder.path}"
            data-question="${dynamicresult.question.name}">
        </div>

        <#if dynamicresult.epilogueContentBlocks??>
            <@renderContentBlocks dynamicresult.epilogueContentBlocks />
        </#if>
    </#list>
</#macro>

<#if document??>
<div class="cms-editable" xmlns="http://www.w3.org/1999/html">
    <@hst.manageContent hippobean=document />

    <div class="ds_wrapper">
        <#if document.startpage??>
            <a class="ds_back-link" href="<@hst.link hippobean=document.startpage/>">Back to '${document.startpage.title}'</a>
        </#if>

        <main id="main-content" class="ds_layout  ds_layout--article">
            <div class="ds_layout__header">
                <div tabindex="-1" class="ds_error-summary  fully-hidden  client-error" aria-labelledby="error-summary-title" role="alert">
                    <h2 class="ds_error-summary__title" id="error-summary-title">There is a problem</h2>

                    <div class="form-errors">

                    </div>
                </div>

                <noscript>
                    <div class="ds_page-header">
                        <h1 class="ds_page-header__title  mg_smart-answer__step-title">${document.title}</h1>
                    </div>

                    <div class="ds_warning-text">
                        <strong class="ds_warning-text__icon" aria-hidden="true"></strong>
                        <strong class="visually-hidden">Warning</strong>
                        <div class="ds_warning-text__text">
                            We've detected from your browser that JavaScript is disabled.
                            Please enable JavaScript to use this page.
                        </div>
                    </div>
                </noscript>
            </div>

            <div class="ds_layout__content">
                <div class="mg_smart-answer" data-module="smartanswer" data-rooturl="<@hst.link hippobean=document />">

                    <form class="mg_smart-answer__form">
                        <#list questions as question>
                            <#if question.style??>
                                <#assign questionType = question.style />
                            <#else>
                                <#assign questionType = 'checkboxes' />
                            </#if>
                            <section class="mg_smart-answer__question  mg_smart-answer__step" data-type="${questionType}" id="step-${question.name}">

                                <#if questionType == 'confirm' || questionType == 'confirmcheckbox'>
                                    <#assign fieldsetElName = 'div'/>
                                    <#assign legendElName = 'div'/>
                                <#else>
                                    <#assign fieldsetElName = 'fieldset'/>
                                    <#assign legendElName = 'legend'/>
                                </#if>

                                <div class="ds_question">

                                    <${fieldsetElName} class="mg_no-margin--last-child  " id="question-${question.name}" data-validation="<#if questionType='radiobuttons'>requiredRadio<#elseif questionType='checkboxes'>atLeastOneCheckbox</#if>">
                                        <${legendElName} class="ds_page-header">
                                            <span aria-hidden="true" id="question-${question.name}-parent" class="mg_smart-answer__parent-title"><span class="visually-hidden">Part of:</span> <span class="js-parent-title">${document.title}</span></span>
                                            <h1 aria-describedby="question-${question.name}-parent" class="ds_page-header__title  mg_smart-answer__step-title  js-question-title">
                                                ${question.title}
                                            </h1>
                                        </${legendElName}>

                                        <#if question.contentBlocks??>
                                            <@renderContentBlocks question.contentBlocks />
                                        </#if>

                                        <#switch questionType>
                                            <#case 'dropdown'>
                                                <div>
                                                    <label class="ds_label" for="step-${question.name}-${question.name}">
                                                        <#if question.dropdownLabel?has_content>
                                                            ${question.dropdownLabel}<#else>Choose an option
                                                        </#if>
                                                    </label>
                                                    <div class="ds_select-wrapper  ds_input--fixed-20  ds_!_margin-bottom--0">
                                                        <select class="ds_select" id="step-${question.name}-${question.name}" data-validation="requiredDropdown">
                                                            <option></option>
                                                            <#list question.options as option>
                                                                <#assign nextStep><#if option.nextPage??>${option.nextPage.name}<#elseif question.defaultNextPage??>${question.defaultNextPage.name}<#else></#if></#assign>
                                                                <option value="${option.value}" data-nextstep="step-${nextStep}">${option.label}</option>
                                                            </#list>
                                                        </select>
                                                        <span class="ds_select-arrow" aria-hidden="true"></span>
                                                    </div>
                                                </div>
                                                <#break>
                                            <#case 'radiobuttons'>
                                                <#list question.options as option>
                                                    <div class="ds_radio">
                                                        <#assign nextStep><#if option.nextPage??>${option.nextPage.name}<#elseif question.defaultNextPage??>${question.defaultNextPage.name}<#else></#if></#assign>
                                                        <input required
                                                            data-form="radio-step-${question.name}-${option.value}"
                                                            class="ds_radio__input"
                                                            type="radio"
                                                            id="step-${question.name}-${option.value}"
                                                            name="question-${question.name}"
                                                            value="${option.value}"
                                                            data-nextstep="step-${nextStep}">
                                                        <label class="ds_radio__label" for="step-${question.name}-${option.value}">${option.label}</label>
                                                    </div>
                                                </#list>
                                                <#break>
                                            <#case 'confirm'>
                                                <@dynamicResultsForItem question />

                                                <input type="hidden" value="confirm" data-nextstep="step-${question.defaultNextPage.name}" />
                                                <#break>
                                            <#case 'confirmcheckbox'>
                                                <@dynamicResultsForItem question />

                                                <div class="ds_checkbox">
                                                    <input required
                                                        data-validation="requiredCheckbox"
                                                        data-form="checkbox-step-${question.name}-confirm"
                                                        class="ds_checkbox__input"
                                                        type="checkbox"
                                                        id="step-${question.name}-confirm"
                                                        name="question-${question.name}"
                                                        value="confirm"
                                                        data-nextstep="step-${question.defaultNextPage.name}">
                                                    <label class="ds_checkbox__label" for="step-${question.name}-confirm">
                                                        <#if question.confirmLabel?has_content>
                                                            ${question.confirmLabel}
                                                        <#else>
                                                            I understand
                                                        </#if>
                                                    </label>
                                                </div>
                                                <#break>
                                            <#case 'checkboxes'>
                                                <div class="ds_checkboxes  mg_no-margin--last-child" data-module="ds-checkboxes" data-validation="atLeastOneCheckbox">
                                                    <#list question.options?sort_by('exclusive') as option>
                                                        <#if option.exclusive == true && question.options?size gt 1>
                                                            <p class="ds_checkbox-separator">or</p>
                                                        </#if>

                                                        <div class="ds_checkbox">
                                                            <#assign nextStep><#if option.nextPage??>${option.nextPage.name}<#elseif
                                                                    question.defaultNextPage??>${question.defaultNextPage.name}<#else></#if></#assign>
                                                            <input
                                                                data-form="checkbox-step-${question.name}-${option.value}"
                                                                class="ds_checkbox__input" type="checkbox"
                                                                id="step-${question.name}-${option.value}"
                                                                name="question-${question.name}"
                                                                value="${option.value}"
                                                                data-nextstep="step-${nextStep}"
                                                                <#if option.exclusive>data-behaviour="exclusive"</#if>>
                                                            <label class="ds_checkbox__label"
                                                                for="step-${question.name}-${option.value}">${option.label}</label>
                                                        </div>
                                                    </#list>
                                                </div>
                                                <#break>
                                        </#switch>
                                    </${fieldsetElName}>
                                </div>

                                <button class="js-next-button  ds_button  ds_button--has-icon  ds_no-margin--bottom">
                                    Next
                                    <svg class="ds_icon" aria-hidden="true" role="img"><use href="${iconspath}#chevron_right"></use></svg>
                                </button>
                            </section>
                        </#list>

                        <#list answers as answer>
                            <section class="mg_smart-answer__answer  mg_smart-answer__step" id="step-${answer.name}" <#if answer.eligible><#else>data-ineligible="true"</#if>>
                                <header class="ds_page-header">
                                    <span aria-hidden="true" class="mg_smart-answer__parent-title"><span class="visually-hidden">Part of:</span> <span class="js-parent-title">${document.title}</span></span>
                                    <h1 class="ds_page-header__title  mg_smart-answer__step-title  js-question-title">
                                        ${answer.title}
                                    </h1>
                                </header>

                                <#if answer.answerContentBlocks??>
                                    <@renderContentBlocks answer.answerContentBlocks />
                                </#if>

                                <@dynamicResultsForItem answer/>
                            </section>
                        </#list>
                    </form>

                    <div id="answered-questions"></div>
                </div>
            </div>
        </main>
    </div>
</div>
</#if>
</@hst.messagesReplace>

<@hst.headContribution category="meta">
    <#if document??>
        <@hst.messagesReplace escapeMessageXml=false bundle=variables variablePrefix="[[" variableSuffix="]]">
        <meta name="description" content="${document.metaDescription}" />
        </@hst.messagesReplace>
    </#if>
</@hst.headContribution>

<#assign scriptName="smart-answer">
<#include 'scripts.ftl'/>
