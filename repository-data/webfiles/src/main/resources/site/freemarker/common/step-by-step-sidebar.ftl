<div class="ds_article-aside">
    <h2 class="gamma">
        Part of<br />
        <a href="#">Adopting a child from abroad: step by step</a>
    </h2>
</div>

<div class="ds_accordion  ds_step-navigation" data-module="ds-accordion">
    <button data-accordion="accordion-open-all" type="button" class="ds_link  ds_accordion__open-all  js-open-all">Open all <span class="visually-hidden">sections</span></button>

    <#list document.steps as step>
        <#assign isactivestep = false />
        <!-- for demonstration purposes only, force the second item to be selected -->
        <#if step?index == 1>
            <#assign isactivestep = true />
        </#if>
        <div class="<#if isactivestep>ds_accordion-item--open</#if>  ds_accordion-item  <#if step.labeltype != "numbered">ds_step-navigation__paused</#if>">
            <input <#if isactivestep>checked</#if> type="checkbox" class="visually-hidden  ds_accordion-item__control" id="panel-${step?index}" aria-labelledby="panel-${step?index}-heading" />
            <div class="ds_accordion-item__header">
                <h3 id="panel-${step?index}-heading" class="ds_accordion-item__title">
                    ${step.title}
                </h3>
                <span class="ds_accordion-item__indicator"></span>
                <label class="ds_accordion-item__label" for="panel-${step?index}"><span class="visually-hidden">Show this section</span></label>
            </div>
            <div class="ds_accordion-item__body" style="padding-right: 16px;">
                <@hst.html hippohtml=step.content/>

                <#if step.links??>
                    <ul class="ds_no-bullets">
                        <#list step.links as link>
                            <li>
                                <a href="<@hst.link hippobean=link.link/>">${link.text}</a>
                            </li>
                        </#list>
                    </ul>
                </#if>
            </div>
        </div>
    </#list>

</div>
