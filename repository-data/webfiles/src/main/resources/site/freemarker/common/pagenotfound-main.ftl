<#ftl output_format="HTML">
<#include "include/imports.ftl">
<div class="cms-editable">
    <div class="ds_wrapper">
        <main id="main-content" class="ds_layout  ds_layout--article">
            <#if document??>
                <div class="ds_layout__header">
                    <header class="ds_page-header">
                        <h1 class="ds_page-header__title">${document.title}</h1>
                    </header>
                </div>

                <div class="ds_layout__content">
                    <@hst.html hippohtml=document.content/>
                </div>
            </#if>
        </main>
    </div>
</div>

<#assign scriptName="default">
<#include 'scripts.ftl'/>
