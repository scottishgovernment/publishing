<#ftl output_format="HTML">
<#if document.showFeedback?? && document.showFeedback>
    <!--noindex-->
    <div lang="en" class="ds_layout__feedback">
        <div class="mg_feedback">
            <#if document??>
                <#assign documentUuid = document.uuid/>
            <#else>
                <#assign documentUuid = ""/>
            </#if>
            <input id="documentUuid" type="hidden" name="uuid" value="${documentUuid}"/>
            <@hst.include ref="feedback"/>
        </div>
    </div>
    <!--endnoindex-->
</#if>
