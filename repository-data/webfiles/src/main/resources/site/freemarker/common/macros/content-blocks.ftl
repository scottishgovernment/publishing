<@hst.webfile var="iconspath" path="/assets/images/icons/icons.stack.svg"/>

<#function slugify string>
    <#return string?lower_case?replace(' ', '-')?replace(',', '')?replace('(', '')?replace(')', '') />
</#function>

<#macro renderContentBlocks contentBlocks>
    <#list contentBlocks as contentBlock>

        <#assign idModifier = contentBlock.node?keep_after("@") />

        <#if hst.isNodeType(contentBlock.node, 'publishing:cb_accordion')>
            <#-- accordion block -->
            <#if contentBlock.noindex>
            <!--noindex-->
            </#if>

            <div class="ds_accordion" data-module="ds-accordion">
                <#if contentBlock.items?size gt 1>
                    <button type="button" class="ds_link  ds_accordion__open-all  js-open-all">Open all <span class="visually-hidden">sections</span></button>
                </#if>
                <#list contentBlock.items as item>
                    <div class="ds_accordion-item">
                        <input type="checkbox" class="visually-hidden  ds_accordion-item__control" id="panel-${item?index}" aria-labelledby="panel-${item?index}-heading">
                        <div class="ds_accordion-item__header">
                            <h3 id="panel-${item?index}-heading" class="ds_accordion-item__title">
                                ${item.title}
                            </h3>
                            <span class="ds_accordion-item__indicator"></span>
                            <label class="ds_accordion-item__label" for="panel-${item?index}"><span class="visually-hidden">Show this section</span></label>
                        </div>
                        <div class="ds_accordion-item__body">
                            <#if item.contentBlocks??>
                                <@renderContentBlocks item.contentBlocks />
                            </#if>
                        </div>
                    </div>
                </#list>
            </div>

            <#if contentBlock.noindex>
            <!--endnoindex-->
            </#if>
            <#-- end accordion block -->
        </#if>

        <#if hst.isNodeType(contentBlock.node, 'publishing:cb_button')>
            <!-- button block -->
            <#if contentBlock.noindex>
            <!--noindex-->
            </#if>

            <a href="<#if contentBlock.internallink??><@hst.link hippobean=contentBlock.internallink /><#else>${contentBlock.externallink}</#if>" class="
                ds_button
                <#if contentBlock.type == 'Secondary'>ds_button--secondary
                <#elseif contentBlock.type == 'Cancel'>ds_button--cancel</#if>
                <#if contentBlock.hasarrow??>ds_button--has-icon</#if>">
                ${contentBlock.content}

                <#if contentBlock.hasarrow??>
                    <svg class="ds_icon" aria-hidden="true" role="img"><use href="${iconspath}#chevron_right"></use></svg>
                </#if>
            </a>

            <#if contentBlock.noindex>
            <!--endnoindex-->
            </#if>
            <!-- end button block -->
        </#if>

        <#if hst.isNodeType(contentBlock.node, 'publishing:cb_fragment')>
            <#-- fragment block -->
            <#if contentBlock.noindex>
            <!--noindex-->
            </#if>
            <@renderContentBlocks contentBlock.fragment.contentBlocks />
            <#if contentBlock.noindex>
            <!--endnoindex-->
            </#if>
            <#-- end fragment block -->
        </#if>

        <#if hst.isNodeType(contentBlock.node, 'publishing:cb_insetText')>
            <#-- inset block -->
            <#if contentBlock.noindex>
            <!--noindex-->
            </#if>
            <div class="ds_inset-text">
                <div class="ds_inset-text__text">
                    <@hst.html hippohtml=contentBlock.content/>
                </div>
            </div>
            <#if contentBlock.noindex>
            <!--endnoindex-->
            </#if>
            <#-- end inset block -->
        </#if>

        <#if hst.isNodeType(contentBlock.node, 'publishing:cb_laFinder')>
            <#-- LA finder block -->
            <#if contentBlock.noindex>
            <!--noindex-->
            </#if>

            <div data-type="button" class="dd finder-hero form-box js-contentselect">
                <label for="council-select-${idModifier}" class="ds_label">
                    ${contentBlock.title}
                </label>

                <div class="ds_input--fluid-two-thirds ds_select-wrapper">
                    <select id="council-select-${idModifier}" class="ds_select">
                        <option disabled="disabled" selected="selected">${contentBlock.placeholder}</option>
                        <#list contentBlock.links as link>
                            <option data-id="${slugify(link.label)}">${link.label}</option>
                        </#list>
                    </select>
                    <span aria-hidden="true" class="ds_select-arrow"></span>
                </div>

                <#list contentBlock.links as link>
                    <a href="${link.url}" id="dd-${slugify(link.label)}"
                    class="ds_button ds_button--max fully-hidden">${link.label}</a>
                </#list>
            </div>

            <#if contentBlock.noindex>
            <!--endnoindex-->
            </#if>
            <#-- end LA finder block -->
        </#if>

        <#if hst.isNodeType(contentBlock.node, 'publishing:cb_richtext')>
            <#-- rich text block -->
            <#if contentBlock.noindex>
            <!--noindex-->
            </#if>
            <@hst.html hippohtml=contentBlock.content/>
            <#if contentBlock.noindex>
            <!--endnoindex-->
            </#if>
            <#-- end rich text block -->
        </#if>

        <#if hst.isNodeType(contentBlock.node, 'publishing:cb_warningText')>
            <#-- warning text block -->
            <#if contentBlock.noindex>
            <!--noindex-->
            </#if>
            <div class="ds_warning-text">
                <strong class="ds_warning-text__icon" aria-hidden="true"></strong>
                <strong class="visually-hidden">Warning</strong>
                <div class="ds_warning-text__text">
                    <@hst.html hippohtml=contentBlock.content/>
                </div>
            </div>
            <#if contentBlock.noindex>
            <!--endnoindex-->
            </#if>
            <#-- end warning text block -->
        </#if>

    </#list>
</#macro>
