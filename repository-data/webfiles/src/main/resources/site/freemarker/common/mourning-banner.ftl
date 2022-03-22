<#ftl output_format="HTML">
<#include "include/imports.ftl">
<#if mourningbanner??>
    <div id="mourning-banner" class="ds_notification  ds_reversed" data-module="ds-notification">
        <div class="ds_wrapper">
            <div class="ds_notification__content">
                <div class="ds_notification__text">
                    <@hst.html hippohtml=mourningbanner.content/>
                </div>
            </div>
        </div>
    </div>
</#if>
