<#ftl output_format="HTML">
<#include "include/imports.ftl">

<#if document??>
<div class="cms-editable" xmlns="http://www.w3.org/1999/html">
    <@hst.manageContent hippobean=document />

    <@hst.include ref="breadcrumbs"/>

    <div class="ds_wrapper">
        <main id="main-content" class="ds_layout  ds_layout--guide">

            <div class="ds_layout__header">
                <header class="ds_page-header">
                    <h1 class="ds_page-header__title">${document.title}</h1>
                </header>
            </div>

            <div class="ds_layout__content">
                <@hst.html hippohtml=document.content/>

                <#list questions as question>
                    <div class="smartanswer-question" id="${question.name}">

                        <@hst.html hippohtml=question.content/>

                        <#list question.options as option>
                            <div>
                                <input type="radio" id="" name="${question.name}" value="${option.value}">
                                <label for="${question.name}">${option.label}</label><br>
                                <span style="display:none" class="smartanswer-nextpage">${option.nextPage.name}</span>
                            </div>
                        </#list>

                        <p>
                            <button class="ds_button smartanswer-nextbutton">Next</button>
                        </p>
                    </div>
                </#list>

                <#list answers as answer>
                    <div class="smartanswer-answer" id="${answer.name}">
                        <@hst.html hippohtml=answer.answer/>
                    </div>
                </#list>

                <div id="answered-questions">
                    <h2>Your Answers</h2>
                    <ul>
                        <#list questions as question>
                            <li data-answer="${question.name}">
                                <strong>${question.title}</strong>
                                <span data-answer-to-question="${question.name}"></span>
                            </li>
                        </#list>
                    </ul>
                </div>
            </div>

            <#include 'feedback-wrapper.ftl'>
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
