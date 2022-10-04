<#ftl output_format="HTML">
<#include "include/imports.ftl">

<#if document??>

<div class="cms-editable">
    <@hst.manageContent hippobean=document />

    <@hst.include ref="breadcrumbs"/>

    <div class="ds_wrapper">
        <main id="main-content" class="ds_layout  ds_layout--article">


            <div class="ds_layout__content">
                <@hst.html hippohtml=document.content/>
            </div>

            <#include 'feedback-wrapper.ftl'>

        </main>
    </div>
</div>

    <@hst.headContribution category="meta">
    <meta name="dc.format" content="StepByStep"/>
    </@hst.headContribution>

</#if>

<@hst.headContribution category="resourcehints">
    <#if nextlink??>
    <link rel="prerender" href="${nextlink}"/>
    </#if>
</@hst.headContribution>



<#assign scriptName="article">
<#include 'scripts.ftl'/>
