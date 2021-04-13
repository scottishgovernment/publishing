<#include "include/imports.ftl">
    <@hst.webfile var="iconspath" path="/assets/images/icons/icons.stack.svg"/>
    <#list notificationbanners as banner>

        <div <#if banner.id??>id="${banner.id}"</#if> class="ds_notification  ds_reversed" data-module="ds-notification">
            <div class="ds_wrapper">
                <div class="ds_notification__content  <#if banner.closeable>ds_notification__content--has-close</#if>">
                    <div role="heading" class="visually-hidden">Information</div>

                    <#if banner.priority??>
                        <span class="ds_notification__icon ds_notification__icon--inverse ds_notification__icon--colour" aria-hidden="true">
                            <svg class="ds_icon" aria-hidden="true" role="img"><use href="${iconspath}#priority_high"></use></svg>
                        </span>
                    </#if>

                    <div class="ds_notification__text">
                        <@hst.html var="content" hippohtml=banner.content/>
                        <#if content?has_content>
                            ${content}
                        </#if>
                    </div>

                    <#if banner.closeable>
                        <button class="ds_notification__close  js-close-notification" type="button">
                            <span class="visually-hidden">Close this notification</span>
                            <svg class="ds_icon" aria-hidden="true" role="img"><use href="${iconspath}#close"></use></svg>
                        </button>
                    </#if>
                </div>
            </div>
        </div>
    </#list>
