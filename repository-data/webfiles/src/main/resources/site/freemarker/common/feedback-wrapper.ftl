<#if document.showFeedback?? && document.showFeedback>
    <!--noindex-->
    <div class="ds_layout__feedback">
        <#if document??>
            <#assign documentUuid = document.uuid/>
        <#else>
            <#assign documentUuid = ""/>
        </#if>
        <input id="documentUuid" type="hidden" name="uuid" value="${documentUuid}"/>
        <@hst.include ref="feedback"/>
    </div>
    <!--endnoindex-->
</#if>