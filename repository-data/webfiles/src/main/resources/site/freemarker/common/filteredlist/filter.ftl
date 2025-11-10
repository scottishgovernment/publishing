<#ftl output_format="HTML">
<#include "../include/imports.ftl">
<@hst.webfile var="iconspath" path="/assets/images/icons/icons.stack.svg"/>

<div class="ds_search-filters">
    <input type="hidden" id="imagePath" value="<@hst.webfile path='/assets/images/icons/' />/">

    <div class="ds_details  ds_no-margin" data-module="ds-details">
        <input id="filters-toggle" type="checkbox" class="ds_details__toggle  visually-hidden">

        <label for="filters-toggle" class="ds_details__summary">
            <span class="visually-hidden">Show </span>Search filters
        </label>

        <div class="ds_skip-links  ds_skip-links--static">
            <ul class="ds_skip-links__list">
                <li class="ds_skip-links__item"><a class="ds_skip-links__link" href="#search-results-list">Skip to results</a></li>
            </ul>
        </div>

        <div class="ds_details__text">

            <form id="filters" method="GET" action="#">

                <fieldset id="filter-search" class="ds_search-filters__search">
                    <legend class="visually-hidden"><h3>Keyword search</h3></legend>
                    <label class="ds_label" for="filters-search-term">Search</label>

                    <div class="ds_input__wrapper  ds_input__wrapper--has-icon">
                        <input class="ds_input" type="search" name="q" id="filters-search-term" maxlength="160" value="<#if search??>${(search.query)!}</#if>" />
                        <button class="ds_button  js-filter-search-submit" type="submit" title="Submit" id="filters-search-submit">
                            <span class="visually-hidden">Search</span>
                            <svg class="ds_icon" aria-hidden="true" role="img"><use href="${iconspath}#search"></use></svg>
                        </button>
                    </div>
                </fieldset>
                <h2 class="ds_search-filters__title  ds_h4  ds_!_padding-top--0">Filter by</h2>

                <div class="ds_accordion  ds_accordion--small  ds_!_margin-top--0" data-module="ds-accordion">

                <#if publicationTypesMap?has_content && includePublicationTypesFilter>
                    <div class="ds_accordion-item">
                        <input type="checkbox" class="visually-hidden  ds_accordion-item__control" id="panel-1" aria-labelledby="panel-1-heading" />
                        <div class="ds_accordion-item__header">
                            <h3 id="panel-1-heading" class="ds_accordion-item__title">
                                Content type
                                <div class="ds_search-filters__filter-count">
                                    <#assign count = 0/>
                                    <#list publicationTypesMap?keys as item>
                                        <#list search.publicationTypes?keys as selectedItem>
                                            <#if selectedItem == item>
                                                <#assign count = count + 1 />
                                            </#if>
                                        </#list>
                                    </#list>

                                    <#if count gt 0>
                                        (${count} selected)
                                    </#if>
                                </div>
                            </h3>
                            <span class="ds_accordion-item__indicator"></span>
                            <label class="ds_accordion-item__label" for="panel-1"><span class="visually-hidden">Show this section</span></label>
                        </div>
                        <div class="ds_accordion-item__body">
                            <fieldset>
                                <legend class="visually-hidden">Select which publication types you would like to see</legend>

                                <div class="ds_search-filters__scrollable">
                                    <div class="ds_search-filters__checkboxes">
                                        <#list publicationTypesMap?keys as item>
                                            <#assign isSelected = false/>

                                            <#list search.publicationTypes?keys as selectedItem>
                                                <#if selectedItem == item>
                                                    <#assign isSelected = true/>
                                                </#if>
                                            </#list>

                                            <div class="ds_checkbox  ds_checkbox--small">
                                                <input
                                                    <#if isSelected == true>
                                                        checked=true
                                                    </#if>
                                                    id="${item}" name="type" value="${item}" class="ds_checkbox__input" type="checkbox">
                                                <label for="${item}" class="ds_checkbox__label">${(publicationTypesMap[item])?replace("/","/<wbr>")?no_esc}</label>
                                            </div>
                                        </#list>
                                    </div>
                                </div>
                            </fieldset>
                        </div>
                    </div>
                </#if>

                <#if document.showTopics>
                <#if topicsMap?has_content>
                    <div class="ds_accordion-item">
                        <input type="checkbox" class="visually-hidden  ds_accordion-item__control" id="panel-2" aria-labelledby="panel-2-heading" />
                        <div class="ds_accordion-item__header">
                            <h3 id="panel-2-heading" class="ds_accordion-item__title">
                                Topic
                                <div class="ds_search-filters__filter-count">
                                    <#assign count = 0/>
                                    <#list topicsMap?keys as item>
                                        <#list search.topics?keys as selectedItem>
                                            <#if selectedItem == item>
                                                <#assign count = count + 1 />
                                            </#if>
                                        </#list>
                                    </#list>

                                    <#if count gt 0>
                                        (${count} selected)
                                    </#if>
                                </div>
                            </h3>
                            <span class="ds_accordion-item__indicator"></span>
                            <label class="ds_accordion-item__label" for="panel-2"><span class="visually-hidden">Show this section</span></label>
                        </div>
                        <div class="ds_accordion-item__body">
                            <fieldset>
                                <legend class="visually-hidden">Select which topics you would like to see</legend>

                                <div class="ds_search-filters__scrollable">
                                    <div class="ds_search-filters__checkboxes">
                                        <#list topicsMap?keys as item>
                                            <#assign isSelected = false/>

                                            <#list search.topics?keys as selectedItem>
                                                <#if selectedItem == item>
                                                    <#assign isSelected = true/>
                                                </#if>
                                            </#list>

                                            <div class="ds_checkbox  ds_checkbox--small">
                                                <input
                                                    <#if isSelected == true>
                                                        checked=true
                                                    </#if>
                                                    id="${item}" name="topic" value="${item}" class="ds_checkbox__input" type="checkbox">
                                                <label for="${item}" class="ds_checkbox__label">${(topicsMap[item])?replace("/","/<wbr>")?no_esc}</label>
                                            </div>
                                        </#list>
                                    </div>
                                </div>
                            </fieldset>
                        </div>
                    </div>
                </#if>
                </#if>

                <div class="ds_accordion-item">
                    <input type="checkbox" class="visually-hidden  ds_accordion-item__control" id="panel-3" aria-labelledby="panel-3-heading" />
                    <div class="ds_accordion-item__header">
                        <h3 id="panel-3-heading" class="ds_accordion-item__title">
                            Updated

                            <div class="ds_search-filters__filter-count">
                                <#assign count = 0/>
                                <#if search.fromDate?? && search.fromDate?length gt 0>
                                    <#assign count = count + 1/>
                                </#if>
                                <#if search.toDate?? && search.toDate?length gt 0>
                                    <#assign count = count + 1/>
                                </#if>

                                <#if count gt 0>
                                    (${count} selected)
                                </#if>
                            </div>
                        </h3>
                        <span class="ds_accordion-item__indicator"></span>
                        <label class="ds_accordion-item__label" for="panel-3"><span class="visually-hidden">Show this section</span></label>
                    </div>
                    <div class="ds_accordion-item__body">
                        <fieldset id="filter-date-range" class="filters__fieldset">
                            <legend class="visually-hidden">Filter by date</legend>

                            <div class="ds_question">
                                <div data-module="ds-datepicker" class="ds_datepicker" id="fromDatePicker">
                                    <label class="ds_label  ds_no-margin--bottom" for="date-from">Updated after</label>
                                    <p id="hint-date-from" class="ds_hint-text  ds_!_margin-bottom--1">For example, 21/01/2024</p>
                                    <div class="ds_input__wrapper">
                                        <input aria-describedby="hint-date-from" value="<#if search.fromDate??>${search.fromDate}</#if>" name="begin" id="date-from" class="ds_input  ds_input--fixed-10" type="text">
                                    </div>
                                </div>
                            </div>

                            <div class="ds_question">
                                <div data-module="ds-datepicker" class="ds_datepicker" id="toDatePicker">
                                    <label class="ds_label  ds_no-margin--bottom" for="date-to">Updated before</label>
                                    <p id="hint-date-to" class="ds_hint-text  ds_!_margin-bottom--1">For example, 21/01/2024</p>
                                    <div class="ds_input__wrapper">
                                        <input aria-describedby="hint-date-to" value="<#if search.toDate??>${search.toDate}</#if>" name="end" id="date-to" class="ds_input  ds_input--fixed-10" type="text">
                                    </div>
                                </div>
                            </div>
                        </fieldset>
                    </div>
                </div>

                <#if document.showLanguages?? && document.showLanguages>
                <div class="ds_accordion-item">
                    <input type="checkbox" class="visually-hidden  ds_accordion-item__control" id="panel-4" aria-labelledby="panel-4-heading" />
                    <div class="ds_accordion-item__header">
                        <h3 id="panel-4-heading" class="ds_accordion-item__title">
                            Languages
                            <div class="ds_search-filters__filter-count">
                                <#assign count = 0/>
                                <#list languagesMap?keys as item>
                                    <#list search.languages?keys as selectedItem>
                                        <#if selectedItem == item>
                                            <#assign count = count + 1 />
                                        </#if>
                                    </#list>
                                </#list>

                                <#if count gt 0>
                                    (${count} selected)
                                </#if>
                            </div>
                        </h3>
                        <span class="ds_accordion-item__indicator"></span>
                        <label class="ds_accordion-item__label" for="panel-4"><span class="visually-hidden">Show this section</span></label>
                    </div>
                    <div class="ds_accordion-item__body">
                        <fieldset>
                            <legend class="visually-hidden">Select which languages you would like to see</legend>

                            <div class="ds_search-filters__scrollable">
                                <div class="ds_search-filters__checkboxes">
                                    <#list languagesMap?keys as item>
                                        <#assign isSelected = false/>

                                        <#list search.languages?keys as selectedItem>
                                            <#if selectedItem == item>
                                                <#assign isSelected = true/>
                                            </#if>
                                        </#list>

                                        <div class="ds_checkbox  ds_checkbox--small">
                                            <input
                                                <#if isSelected == true>
                                                    checked=true
                                                </#if>
                                                id="${item}" name="lang" value="${item}" class="ds_checkbox__input" type="checkbox">
                                            <label for="${item}" class="ds_checkbox__label">${(languagesMap[item])?replace("/","/<wbr>")?no_esc}</label>
                                        </div>
                                    </#list>
                                </div>
                            </div>
                        </fieldset>
                    </div>
                </div>
                </#if>
                <#if document.showAccessibilityFeatures?? && document.showAccessibilityFeatures>
                    <div class="ds_accordion-item">
                        <input type="checkbox" class="visually-hidden  ds_accordion-item__control" id="panel-5" aria-labelledby="panel-5-heading" />
                        <div class="ds_accordion-item__header">
                            <h3 id="panel-5-heading" class="ds_accordion-item__title">
                                Accessibility
                                <div class="ds_search-filters__filter-count">
                                    <#assign count = 0/>
                                    <#list accessibilityFeaturesMap?keys as item>
                                        <#list search.accessibilityFeatures?keys as selectedItem>
                                            <#if selectedItem == item>
                                                <#assign count = count + 1 />
                                            </#if>
                                        </#list>
                                    </#list>

                                    <#if count gt 0>
                                        (${count} selected)
                                    </#if>
                                </div>
                            </h3>
                            <span class="ds_accordion-item__indicator"></span>
                            <label class="ds_accordion-item__label" for="panel-5"><span class="visually-hidden">Show this section</span></label>
                        </div>
                        <div class="ds_accordion-item__body">
                            <fieldset>
                                <legend class="visually-hidden">Select which accessibility features you would like to see</legend>

                                <div class="ds_search-filters__scrollable">
                                    <div class="ds_search-filters__checkboxes">
                                        <#list accessibilityFeaturesMap?keys as item>
                                            <#assign isSelected = false/>

                                            <#list search.accessibilityFeatures?keys as selectedItem>
                                                <#if selectedItem == item>
                                                    <#assign isSelected = true/>
                                                </#if>
                                            </#list>

                                            <div class="ds_checkbox  ds_checkbox--small">
                                                <input
                                                    <#if isSelected == true>
                                                        checked=true
                                                    </#if>
                                                    id="${item}" name="assist" value="${item}" class="ds_checkbox__input" type="checkbox">
                                                <label for="${item}" class="ds_checkbox__label">${(accessibilityFeaturesMap[item])?replace("/","/<wbr>")?no_esc}</label>
                                            </div>
                                        </#list>
                                    </div>
                                </div>
                            </fieldset>
                        </div>
                    </div>
                </#if>
                </div>
                <button class="ds_button  ds_button--primary  ds_button--small  ds_button--max  ds_no-margin  js-apply-filter">
                    Apply filter
                </button>
            </form>
        </div>
    </div>
</div>
