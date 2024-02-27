<#ftl output_format="HTML">
<#assign variables = hstRequestContext.getAttribute("variables")/>
<@hst.messagesReplace escapeMessageXml=false bundle=variables variablePrefix="[[" variableSuffix="]]">
<div class="category-lower  ds_pre-footer-background">
    <div class="ds_wrapper">
        <div class="ds_layout  ds_layout--category-list">
            <div class="ds_layout__grid">
                <div class="ds_category-list-container">
                    <#include '../card-navigation--image.ftl'/>
                </div>
            </div>
        </div>
    </div>
</div>
</@hst.messagesReplace>
