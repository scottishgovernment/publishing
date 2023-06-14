<#ftl output_format="HTML">
<#if document.showFeedback?? && document.showFeedback>
    <!--noindex-->
    <div lang="en" class="ds_layout__feedback">
        <div class="mg_feedback">
            <h2>Feedback, help and support</h2>

            <p class="ds_!_margin-bottom--6">If you need help or support you can e-mail us at <a href="mailto:designsystem@gov.scot">designsystem@gov.scot</a></p>

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
