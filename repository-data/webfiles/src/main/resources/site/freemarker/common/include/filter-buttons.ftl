
<#if showFilters>
<div class="ds_skip-links  ds_skip-links--static">
    <ul class="ds_skip-links__list">
        <li class="ds_skip-links__item"><a class="ds_skip-links__link" href="#search-results">Skip to results</a></li>
    </ul>
</div>

<div class="ds_search-controls">
    <#assign filtersCount = filterButtons.types?size +
        filterButtons.topics?size + filterButtons.dates?size />

    <#if filtersCount gt 0>

    <div class="ds_facets">
        <p class="visually-hidden">
            <#if filtersCount == 1>
                There is 1 search filter applied.
            <#else>
                There are ${filtersCount} search filters applied.
            </#if>
        </p>

        <dl class="ds_facets__list">
            <#if filterButtons.types?? && filterButtons.types?size gt 0>
                <div class="ds_facet-group">
                    <dt class="ds_facet__group-title">
                        Content type:
                    </dt>
                    <#list filterButtons.types as item>
                        <dd class="ds_facet-wrapper">
                            <span class="ds_facet">
                                ${item.label}

                                <a href="?${item.url}" role="button" aria-label="Remove '${item.label}' filter" class="ds_facet__button  js-remove-facet" data-slug="${item.id}">
                                    <svg class="ds_facet__button-icon" aria-hidden="true" role="img" focusable="false"><use href="${iconspath}#cancel"></use></svg>
                                </a>
                            </span>
                        </dd>
                    </#list>
                </div>
            </#if>

            <#if filterButtons.topics?? && filterButtons.topics?size gt 0>
                <div class="ds_facet-group">
                    <dt class="ds_facet__group-title">
                        Topic:
                    </dt>
                    <#list filterButtons.topics as item>
                        <dd class="ds_facet-wrapper">
                            <span class="ds_facet">
                                ${item.label}

                                <a href="?${item.url}" role="button" aria-label="Remove '${item.label}' filter" class="ds_facet__button  js-remove-facet" data-slug="${item.id}">
                                    <svg class="ds_facet__button-icon" aria-hidden="true" role="img" focusable="false"><use href="${iconspath}#cancel"></use></svg>
                                </a>
                            </span>
                        </dd>
                    </#list>
                </div>
            </#if>

            <#if filterButtons.dates?? && filterButtons.dates?size gt 0>
                <#if filterButtons.dates.begin?? && filterButtons.dates.end??>
                    <#assign dateLabel = "Updated between"/>
                <#elseif filterButtons.dates.begin??>
                    <#assign dateLabel = "Updated after"/>
                <#elseif filterButtons.dates.end??>
                    <#assign dateLabel = "Updated before"/>
                </#if>

                <div class="ds_facet-group">
                    <dt class="ds_facet__group-title">
                        ${dateLabel}:
                    </dt>

                    <#if filterButtons.dates.begin?? && filterButtons.dates.end??>
                        <dd class="ds_facet-wrapper">
                            <span class="ds_facet">
                                ${filterButtons.dates.begin.label}

                                <a href="?${filterButtons.dates.begin.url}" aria-label="Remove 'updated after ${filterButtons.dates.begin.label}' filter" class="ds_facet__button  js-remove-facet" data-slug="date-from">
                                    <svg class="ds_facet__button-icon" aria-hidden="true" role="img" focusable="false"><use href="${iconspath}#cancel"></use></svg>
                                </a>
                            </span> and
                        </dd>

                        <dd class="ds_facet-wrapper">
                            <span class="ds_facet">
                                ${filterButtons.dates.end.label}

                                <a href="?${filterButtons.dates.end.url}" aria-label="Remove 'updated before ${filterButtons.dates.end.label}' filter" class="ds_facet__button  js-remove-facet" data-slug="date-to">
                                    <svg class="ds_facet__button-icon" aria-hidden="true" role="img" focusable="false"><use href="${iconspath}#cancel"></use></svg>
                                </a>
                            </span>
                        </dd>
                    <#elseif filterButtons.dates.begin??>
                        <dd class="ds_facet-wrapper">
                            <span class="ds_facet">
                                ${filterButtons.dates.begin.label}

                                <a href="?${filterButtons.dates.begin.url}" role="button" aria-label="Remove 'updated after ${filterButtons.dates.begin.label}' filter" class="ds_facet__button  js-remove-facet" data-slug="date-from">
                                    <svg class="ds_facet__button-icon" aria-hidden="true" role="img" focusable="false"><use href="${iconspath}#cancel"></use></svg>
                                </a>
                            </span>
                        </dd>
                    <#elseif filterButtons.dates.end??>
                        <dd class="ds_facet-wrapper">
                            <span class="ds_facet">
                                ${filterButtons.dates.end.label}

                                <a href="?${filterButtons.dates.end.url}" role="button" aria-label="Remove 'updated before ${filterButtons.dates.end.label}' filter" class="ds_facet__button  js-remove-facet" data-slug="date-to">
                                    <svg class="ds_facet__button-icon" aria-hidden="true" role="img" focusable="false"><use href="${iconspath}#cancel"></use></svg>
                                </a>
                            </span>
                        </dd>
                    </#if>
                </div>
            </#if>
        </dl>

        <#if (RequestParameters.q)?has_content>
            <#assign clearAll = "?q=" + RequestParameters.q/>
        <#else>
            <#assign clearAll = "?"/>
        </#if>
        <a href="${clearAll}" role="button" class="ds_facets__clear-button  ds_button  ds_button--secondary  js-clear-filters">
            Clear all filters
            <svg class="ds_facet__button-icon" aria-hidden="true" role="img" focusable="false"><use href="${iconspath}#cancel"></use></svg>
        </a>
    </div>

    </#if>

    <hr class="ds_search-results__divider">

    <#if (response.resultPacket.resultsSummary.totalMatching)!?has_content &&
        response.resultPacket.resultsSummary.totalMatching &gt; 0>

        <div class="ds_sort-options">
            <label class="ds_label" for="sort-by">Sort by</label>
            <span class="ds_select-wrapper">
                <select form="filters" name="sort" class="ds_select  js-sort-by" id="sort-by">
                    <option <#if hstRequest.request.getParameter('sort')?? && hstRequest.request.getParameter('sort') == "date">selected</#if> value="date">Updated (newest)</option>
                    <option <#if hstRequest.request.getParameter('sort')?? && hstRequest.request.getParameter('sort') == "adate">selected</#if> value="adate">Updated (oldest)</option>
                </select>
                <span class="ds_select-arrow" aria-hidden="true"></span>
            </span>

            <button form="filters" class="ds_button  ds_button--secondary  ds_button--small  js-apply-sort" type="submit" data-button="button-apply-sort">Apply sort</button>
        </div>
    </#if>
</div>
</#if>