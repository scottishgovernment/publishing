<#include "include/imports.ftl">
<@hst.webfile var="iconspath" path="/assets/images/icons/icons.stack.svg"/>
<div id="browser-notice" class="ds_notification  fully-hidden" data-module="ds-notification" data-customcondition="true">
    <div class="ds_wrapper">
        <div class="ds_notification__content  ds_notification__content--has-close">
            <h2 class="visually-hidden">Information</h2>

            <div class="ds_notification__text">
                <p>You appear to be using an unsupported browser, and it may not be able to display this site properly. You may wish to <a href="/browsers/">upgrade your browser</a>.</p>
            </div>

            <button class="ds_notification__close  js-close-notification" type="button">
                <span class="visually-hidden">Close this notification</span>
                <svg class="ds_icon  ds_icon--fill" role="img"><use xlink:href="${iconspath}#close"></use></svg>
            </button>
        </div>
    </div>
</div>
