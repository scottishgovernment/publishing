<#include "include/imports.ftl">
<#if mourningbanner??>
    <div id="mourning-banner" class="ds_notification  ds_reversed" data-module="ds-notification">
        <div class="ds_wrapper">
            <div class="ds_notification__content">
                <@hst.html hippohtml=mourningbanner.content/>
            </div>
        </div>
    </div>
</#if>