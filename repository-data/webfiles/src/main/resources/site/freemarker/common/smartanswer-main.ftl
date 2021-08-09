<#ftl output_format="HTML">
<#include "include/imports.ftl">
<@hst.webfile var="iconspath" path="/assets/images/icons/icons.stack.svg"/>

<#if document??>
<div class="cms-editable" xmlns="http://www.w3.org/1999/html">
    <@hst.manageContent hippobean=document />

    <@hst.link var="startpage" hippobean=document.startpage/>

    <div class="ds_wrapper">
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
                <div class="mg_smart-answer" data-module="smartanswer">
                    <#list questions as question>
                        <section tabindex="-1" class="mg_smart-answer__question  mg_smart-answer__step" id="step-${question.name}">
                            <div class="ds_question">
                                <fieldset class="mg_no-margin--last-child  " id="question-${question.name}" data-validation="requiredRadio">
                                    <legend class="ds_page-header">
                                        <span class="mg_smart-answer__parent-title">${document.title}</span>
                                        <h1 class="ds_page-header__title  mg_smart-answer__step-title  js-question-title">${question.title}</h1>
                                    </legend>

                                    <#list question.options as option>
                                        <div class="ds_radio">
                                            <input required data-form="radio-step-${question.name}-${option.value}" class="ds_radio__input" type="radio" id="step-${question.name}-${option.value}" name="question-${question.name}" value="${option.value}" data-nextstep="step-${option.nextPage.name}">
                                            <label class="ds_radio__label" for="step-${question.name}-${option.value}">${option.label}</label>
                                        </div>
                                    </#list>
                                </fieldset>
                            </div>

                            <#if question?is_first>
                                <a href="${startpage}" id="start-page-link" class="ds_button  ds_button--has-icon  ds_button--has-icon--left  ds_button--cancel  ds_no-margin">
                                    <svg class="ds_icon" aria-hidden="true" role="img"><use xlink:href="${iconspath}#chevron_left"></use></svg>
                                    Back
                                </a>
                            </#if>

                            <button class="js-next-button  ds_button  ds_button--has-icon  ds_no-margin">
                                Next
                                <svg class="ds_icon" aria-hidden="true" role="img"><use xlink:href="${iconspath}#chevron_right"></use></svg>
                            </button>
                        </section>
                    </#list>

                    <#list answers as answer>
                        <section tabindex="-1" class="mg_smart-answer__answer  mg_smart-answer__step" id="step-${answer.name}">
                            <header class="ds_page-header">
                                <h1 class="ds_page-header__title  mg_smart-answer__step-title  js-question-title">${answer.title}</h1>
                            </header>
                            <@hst.html hippohtml=answer.answer/>
                        </section>
                    </#list>

                    <div id="answered-questions"></div>
                </div>
            </div>
        </main>
    </div>
</div>
</#if>

<@hst.headContribution category="meta">
    <#if document??>
    <meta name="description" content="${document.metaDescription}"/>
    </#if>
</@hst.headContribution>

<#assign scriptName="smart-answer">
<#include 'scripts.ftl'/>
