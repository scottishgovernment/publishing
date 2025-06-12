<#ftl output_format="HTML">
<#include "../include/imports.ftl">
<#include "../include/cms-placeholders.ftl">


<#list events as eventgroup>
<div class="ds_pb  ds_pb--cards ds_pb--background-secondary">
    <div class="ds_wrapper">
        <div class="ds_pb__inner">

        <#list eventgroup as event>

            <div class="ds_card  ds_card--hover">

                <div class="ds_card__media  <#if smallvariant>ds_card__media--small-mobile</#if>">
                    <div class="ds_aspect-box">
                        <img class="ds_aspect-box__inner" src="${event.logo.url}" >
                    </div>
                </div>

                <div class="ds_card__content">
                    <h2 class="ds_card__title">
                        <a class="ds_card__link--cover" href="${event.url}">${event.name.text}</a>
                    </h2>
                    <p>${event.description.text}</p>
                </div>
            </div>

        </#list>

        </div>
    </div>

    </div>

</div>
</#list>

 <a class="gov_icon-link  gov_icon-link--major" href="https://www.eventbrite.co.uk/o/${organisation}" data-navigation="news-all">




