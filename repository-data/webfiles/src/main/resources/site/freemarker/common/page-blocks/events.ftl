<#ftl output_format="HTML">
<#include "../include/imports.ftl">
<@hst.webfile var="iconspath" path="/assets/images/icons/icons.stack.svg"/>
<#if events??>
    <div class="ds_pb  ds_pb--link-list ds_!_padding-bottom--0">
        <div class="ds_wrapper">
            <div class="ds_pb__inner">
                <h2>${title}</h2>
                <#if errorState>
                    <p>Sorry, events are not loading right now.</p>
                <#else>
                    <#if events?size == 0>
                        There are no upcoming events.
                    </#if>
                    <ul class="ds_link-list  ds_link-list--no-border">
                        <#list events as event>
                            <li class="ds_link-item">
                                <#if showImages>
                                    <a class="ds_link-item__image-link" href="${event.url}" aria-hidden="true" tabindex="-1">
                                        <div class="ds_aspect-box  ds_link-item__image">
                                            <img class="ds_aspect-box__inner" src="${event.imageurl}" >
                                        </div>
                                    </a>
                                </#if>

                                <div class="ds_link-item__metadata">
                                    <dl class="ds_metadata ds_metadata--inline">
                                        <div class="ds_metadata__item">
                                            <dt class="ds_metadata__key visually-hidden">Published</dt>
                                            <dd class="ds_metadata__value">${event.dateTime}</dd>
                                        </div>
                                    </dl>
                                </div>
                                <h3 class="ds_link-item__title">
                                    <a href="${event.url}" class="ds_link-item__link">${event.title}</a>
                                </h3>
                                <p class="ds_link-item__summary">${event.summary}</p>
                            </li>
                        </#list>
                    </ul>
                </#if>
                <#if organizationId??>
                    <div>
                        <a href="https://www.eventbrite.co.uk/o/${organizationId}" class="ds_cb__link-major">See all events <svg class="ds_icon" aria-hidden="true" role="img">
                            <use href="${iconspath}#chevron_right"></use>
                        </svg></a>
                    </div>
                </#if>
            </div>
        </div>


    </div>
</#if>