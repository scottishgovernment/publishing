<#ftl output_format="HTML">
<#include "../include/imports.ftl">
<@hst.webfile var="iconspath" path="/assets/images/icons/icons.stack.svg"/>

<div class="ds_pb  ds_pb--link-list
<#if removebottompadding>  ds_!_padding-bottom--0</#if>
">
    <div class="ds_wrapper">
        <div class="ds_pb__inner">
            <h2>Publications</h2>
            <ul class="ds_link-list  ds_link-list--no-border
            <#if count == 2>  ds_link-list-2-items</#if>
            ">
            <#list publications as publication>
                <li class="ds_link-item">
                    <div class="ds_link-item__metadata">
                        <dl class="ds_metadata ds_metadata--inline">
                            <#if publication.label?has_content>
                            <div class="ds_metadata__item">
                                <dt class="ds_metadata__key">Type</dt>
                                <dd class="ds_metadata__value">
                                ${publication.label}
                                </dd>
                            </div>
                            </#if>
                            <div class="ds_metadata__item">
                                <dt class="ds_metadata__key visually-hidden">Published</dt>
                                <dd class="ds_metadata__value"><@fmt.formatDate value=publication.publicationDate.time type="both" pattern="d MMM yyyy"/></dd>
                            </div>
                        </dl>
                    </div>
                    <h3 class="ds_link-item__title">
                        <a href="<@hst.link hippobean=publication/>" class="ds_link-item__link">${publication.title}</a>
                    </h3>
                    <p class="ds_link-item__summary">${publication.summary}</p>
                </li>
            </#list>
            </ul>
            <div>
                <a href="<@hst.link path="/publications"/>" class="ds_cb__link-major">See all publications<svg class="ds_icon" aria-hidden="true" role="img">
                    <use href="${iconspath}#chevron_right"></use>
                </svg></a>
            </div>
        </div>
    </div>
</div>