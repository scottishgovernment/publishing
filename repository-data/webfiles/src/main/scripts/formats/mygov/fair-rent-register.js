'use strict';

import gup from '../../tools/gup';
import Paginator from '../../tools/paginator';
import currency from '../../templates/currency.js';
import date from '../../templates/date.js';
import bloomreachWebfile from '../../tools/bloomreach-webfile';

import PromiseRequest from '@scottish-government/design-system/src/base/tools/promise-request/promise-request';

const fairRentRegister = {

    settings: {
        name: 'fair-rent',
        endpoint: '/service/housing/fairrent'
    },

    searchParams: {
        from: 0,
        size: 10
    },

    listElement: document.getElementById('search-results-list'),

    init: function () {
        this.ancestors = [];

        this.paginator = new Paginator(document.getElementById('pagination'), 2, this);

        const breadcrumbs = [].slice.call(document.querySelectorAll('.ds_breadcrumbs__item'));
        breadcrumbs.forEach(breadcrumb => {
            const link = breadcrumb.querySelector('a');
            if (link) {
                this.ancestors.push({
                    title: breadcrumb.innerText,
                    url: link.href
                });
            } else {
                this.ancestors.push({
                    title: breadcrumb.innerText,
                    url: window.location.pathname
                });
            }
        });

        this.getParamsAndDisplaySection();
        this.attachEventHandlers();
    },

    /**
     * Attach event handlers to:
     * * browser back/forward nav
     */
    attachEventHandlers: function() {
        // makes back/next buttons render the desired page
        window.onpopstate = () => {
            this.getParamsAndDisplaySection();
        };
    },

    /**
     * Get parameters from the query string and determine which section to show
     */
    getParamsAndDisplaySection: function() {
        this.searchParams.query = gup('query');
        this.searchParams.caseId = gup('caseId');

        const page = parseInt(gup('page'), 10) || 1;

        this.searchParams.from = (page - 1) * this.searchParams.size;

        // determine which section to show
        if (this.searchParams.caseId) {
            this.doCase(this.searchParams.caseId);
        } else if (this.searchParams.query || this.searchParams.query === '' || gup('page')) {
            this.searchParams.query = this.searchParams.query || '';
            this.doSearch(this.searchParams);
        } else {
            this.doIntro();
        }
    },

    updateBreadcrumbs: function (additionalBreadcrumb) {
        const breadcrumbsArray = this.ancestors.slice();

        if (additionalBreadcrumb) {
            breadcrumbsArray.push(additionalBreadcrumb);
        }

        const breadcrumbsListElement = document.querySelector('.ds_breadcrumbs');
        if (breadcrumbsListElement) {
            breadcrumbsListElement.innerHTML = '';

            for (let i = 0, il = breadcrumbsArray.length; i < il; i++) {
                let breadcrumbItem;
                if (i !== il - 1) {
                    breadcrumbItem = `<li class="ds_breadcrumbs__item" id="${breadcrumbsArray[i].id}">
                    <a class="ds_breadcrumbs__link" href="${breadcrumbsArray[i].url}">
                        ${breadcrumbsArray[i].title}
                    </a>
                </li>`;
                }
                else {
                    breadcrumbItem = `<li class="ds_breadcrumbs__item" id="${breadcrumbsArray[i].id}">
                    ${breadcrumbsArray[i].title}
                </li>`;
                }
                breadcrumbsListElement.innerHTML = breadcrumbsListElement.innerHTML + breadcrumbItem;
            }

            window.DS.tracking.init(breadcrumbsListElement);
        }
    },

    /**
     * Hide all sections apart from the current one
     * @param {string} sectionName
     */
    showSection: function (sectionName) {
        const targetSection = document.querySelector(`#fair-rent-${sectionName}`);
        if (!targetSection) {
            return;
        }

        const sections = document.querySelectorAll('.js-fair-rent-section');

        // hide all sections
        for (let i = 0, il = sections.length; i < il; i++) {
            sections[i].classList.add('fully-hidden');
        }

        // show target section
        targetSection.classList.remove('fully-hidden');

        window.DS.tracking.init(targetSection);
    },

    /**
     * Show the introduction section
     */
    doIntro: function() {
        // show fair rent section header (it might have been hidden)
        const fairRentHeader = document.getElementById('fair-rent-register-header');
        if (fairRentHeader) {
            fairRentHeader.classList.remove('fully-hidden');
        }

        this.showSection('introduction');

        this.updateBreadcrumbs();
    },

    /**
     * Fetch search results and show the search results section
     * @param {object} searchParams
     * @param {boolean} append
     */
    doSearch: function (searchParams = this.searchParams) {
        this.removeFetchErrorMessage();

        // show fair rent section header (it might have been hidden)
        const fairRentHeader = document.getElementById('fair-rent-register-header');
        fairRentHeader.classList.remove('fully-hidden');

        const url = `${this.settings.endpoint}/search?query=${searchParams.query.toUpperCase()}&from=${Math.floor(searchParams.from)}&size=${searchParams.size}`;

        PromiseRequest(url)
            .then(response => {
                const responseJSON = JSON.parse(response.responseText);
                this.renderList(responseJSON, responseJSON.totalRecordCount, searchParams.query);
                this.showSection('list');

                this.updateBreadcrumbs({
                    title: `Search: "${searchParams.query}"`
                });
            })
            .catch(() => {
                this.addFetchErrorMessage('Unable to load results. Please try again later.');
            });
    },

    /**
     * Render a list of results from the fetched data
     * Update the results count
     * @param {object} result
     * @param {number} total
     */
    renderList: function (result, total, query) {

        this.listElement.innerHTML = '';
        this.listElement.start = result.index + 1;
        this.listElement.setAttribute('data-total', total);

        if (total === 0) {
            const itemHTML = `
            <h2>Sorry, no results were found for "${decodeURIComponent(query).replace(/[+]/g, ' ')}"</h2>
            <p>
                Please search again using an alternative street, town or postcode.
            </p>

             <div class="ds_site-search">
                <form id="fair-rent-search-no-results" role="search" class="ds_site-search__form" method="GET" action="/fair-rent-register/">
                    <label class="ds_label  visually-hidden" for="site-search">Search</label>

                    <div class="ds_input__wrapper  ds_input__wrapper--has-icon">
                        <input name="query" id="fair-rent-search-box" class="ds_input  ds_site-search__input" type="text" placeholder="Search the Fair Rent Register" value="${decodeURIComponent(query).replace(/[+]/g, ' ')}">

                        <button type="submit" class="ds_button  ds_button--icon-only  js-site-search-button">
                            <span class="visually-hidden">Search the Fair Rent Register</span>
                            <svg class="ds_icon" role="img"><use href="${bloomreachWebfile('/assets/images/icons/icons.stack.svg')}#search"></use></svg>
                        </button>
                    </div>
                </form>
            </div>
            `;

            const itemElement = document.createElement('div');
            itemElement.innerHTML = itemHTML;
            this.listElement.appendChild(itemElement);
        } else {
            for (let i = 0, il = result.data.length; i < il; i++) {
                const item = result.data[i];

                const itemHTML = `<h3 class="ds_search-result__title">
                            <a data-caseid="${item.caseNo}" data-gtm="search-pos-${i}" href="/fair-rent-register/?caseId=${item.caseNo}" title="${item.propertyAddressLine}" class="js-fair-rent-item  ds_search-result__link" href="#">${item.propertyAddressLine}</a>
                        </h3>
                    <p class="ds_search-result__summary">
                        Registered ${date.ddMMyyyy(new Date(item.registered))}
                        Rent: ${currency(item.rent)}
                    </p>`;

                const itemElement = document.createElement('li');
                itemElement.classList.add('ds_search-result');
                itemElement.innerHTML = itemHTML;
                this.listElement.appendChild(itemElement);
            }
        }

        // update result count
        // build an object with the values we need in the template literal for better readability
        const resultCountData = {};
        resultCountData.from = result.index + 1;
        resultCountData.to = Math.min(result.index + result.numberOfRecordsDisplayed, result.totalRecordCount);
        resultCountData.total = result.totalRecordCount;
        resultCountData.query = this.searchParams.query;

        if (resultCountData.total > 0) {
            const resultCountElement = document.getElementById('result-count');
            if (result.totalRecordCount > result.numberOfRecordsRequested) {
                resultCountElement.innerHTML = `Showing <b>${resultCountData.from} - ${resultCountData.to}</b> of <b>${resultCountData.total}</b> ${resultCountData.total === 1 ? 'result' : 'results'}`;
            } else {
                resultCountElement.innerHTML = `Showing <b>${resultCountData.total}</b> ${resultCountData.total === 1 ? 'result' : 'results'}`;
            }

            if (resultCountData.query) {
                resultCountElement.innerHTML += ` for <b>${decodeURIComponent(resultCountData.query).replace(/[+]/g, ' ')}</b>`;
            }
        }

        this.paginator.setParams(this.searchParams.from, this.searchParams.size, total);
        this.paginator.renderPages();

    },

    /**
     * Fetch property/case details and display the property section
     * @param {string} caseNo
     */
    doCase: function (caseNo) {
        this.removeFetchErrorMessage();

        const url = `${this.settings.endpoint}/cases/${caseNo}`;

        PromiseRequest(url)
            .then(response => {
                // hide fair rent header
                const fairRentHeader = document.getElementById('fair-rent-register-header');
                fairRentHeader.classList.add('fully-hidden');

                const responseJSON = JSON.parse(response.responseText);
                this.renderProperty(responseJSON.data);
                this.showSection('property');

                this.updateBreadcrumbs({
                    title: responseJSON.data.dwellingAddress.houseName !== '' ? responseJSON.data.dwellingAddress.houseName : responseJSON.data.dwellingAddress.addressLine
                });
            })
            .catch(() => {
                this.addFetchErrorMessage('Unable to load property details. Please try again later.');
            });
    },

    /**
     * Render a property page from the fetched data
     * @param {object} item
     */
    renderProperty: function(item) {
        const itemElement = document.getElementById('fair-rent-property');

        /** Tenancy */

        const itemTenantAgentNameArray = JSON.stringify(item.tenancy.tenantAgent.name).replace(/null/g, 'n/a');
        const itemLandlordAgentNameArray = JSON.stringify(item.tenancy.landlordAgent.name).replace(/null/g, 'n/a');

        if (item.tenancy.rentalPeriod === null) {
            item.tenancy.rentalPeriod = 'n/a';
        }

        if (item.tenancy.dateCommenced === null) {
            item.tenancy.dateCommenced = 'n/a';
        } else {
            item.tenancy.dateCommenced = date.ddMMyyyy(new Date(item.tenancy.dateCommenced));
        }

        /** Application */

        if (item.application.rent.dateEffective === null) {
            item.application.rent.dateEffective = 'n/a';
        } else {
            item.application.rent.dateEffective = date.ddMMyyyy(new Date(item.application.rent.dateEffective));
        }

        if (item.application.rent.amount === null) {
            item.application.rent.amount = 'n/a';
        } else {
            item.application.rent.amount = JSON.stringify(currency(item.application.rent.amount)).replace(/null/g, 'n/a').replace(/"/g, '') + ' / year';
        }

        if (item.application.furniture.code === 'N') {
            item.application.furniture.amount = 'n/a';
        } else {
            item.application.furniture.amount = JSON.stringify(currency(item.application.furniture.amount)).replace(/null/g, 'n/a').replace(/"/g, '');
        }

        if (item.application.services.code === 'N') {
            item.application.services.notedAmount = 'n/a';
            item.application.services.elementAmount = 'n/a';
        } else {
            item.application.services.provided = JSON.stringify(item.application.services.provided).replace(/null/g, 'n/a').replace(/""/g, 'n/a').replace(/"/g, '');
            item.application.services.notedAmount = JSON.stringify(currency(item.application.services.notedAmount)).replace(/null/g, 'n/a').replace(/"/g, '');
            item.application.services.elementAmount = JSON.stringify(currency(item.application.services.elementAmount)).replace(/null/g, 'n/a').replace(/"/g, '');
        }

        if (item.application.remarks === null) {
            item.application.remarks = 'n/a';
        }


        /** Appeals */

        if (item.appeal.datePassed === null) {
            item.appeal.datePassed = 'n/a';
            item.appeal.rent.amount = 'n/a';
            item.appeal.rent.dateRegistered = 'n/a';
            item.appeal.rent.dateEffective = 'n/a';
            item.appeal.appealFurnitureAmount = 'n/a';
            item.appeal.servicesNotedAmount = 'n/a';
            item.appeal.servicesElementAmount = 'n/a';
            item.appeal.remarks = 'n/a';

        } else {

            item.appeal.datePassed = date.ddMMyyyy(new Date(item.appeal.datePassed));
            item.appeal.rent.amount = JSON.stringify(currency(item.appeal.rent.amount)).replace(/null/g, 'n/a').replace(/"/g, '');
            item.appeal.rent.dateRegistered = date.ddMMyyyy(new Date(item.appeal.rent.dateRegistered));
            item.appeal.rent.dateEffective = date.ddMMyyyy(new Date(item.appeal.rent.dateEffective));
            item.appeal.appealFurnitureAmount = JSON.stringify(currency(item.appeal.appealFurnitureAmount)).replace(/null/g, 'n/a').replace(/"/g, '');
            item.appeal.servicesNotedAmount = JSON.stringify(currency(item.appeal.servicesNotedAmount)).replace(/null/g, 'n/a').replace(/"/g, '');
            item.appeal.servicesElementAmount = JSON.stringify(currency(item.appeal.servicesElementAmount)).replace(/null/g, 'n/a').replace(/"/g, '');
        }

        const valuesPolyfill = function(object) {
            return Object.keys(object).map(key => object[key]);
        };

        const values = Object.values || valuesPolyfill;


        const itemAddressArray = (values(item.dwellingAddress).filter(el => el !== '').filter(el => el !== null));

        const itemTenantAddressArray = (values(item.tenancy.tenant.address).filter(el => el !== '').filter(el => el !== null));

        const itemTenantAgentAddressArray = (values(item.tenancy.tenantAgent.address).filter(el => el !== '').filter(el => el !== null));

        const itemLandlordAddressArray = (values(item.tenancy.landlord.address).filter(el => el !== '').filter(el => el !== null));

        const itemLandlordAgentAddressArray = (values(item.tenancy.landlordAgent.address).filter(el => el !== '').filter(el => el !== null));


        const itemHTML = `
        <div class="grid"><!--
        --><div class="grid__item  medium--ten-twelfths  large--eight-twelfths">
        <h1>${itemAddressArray.join(', ')}</h1>
        <h2>Overview</h2>
        <h3>Dwelling address:</h3>
        <p>${itemAddressArray.join('<br>')}</p>

        <h3>Property information:</h3>
        <p>
        Building type: ${item.propertyDetails.buildingType} <br>
        Property type: ${item.propertyDetails.propertyType} <br>
        Property age: ${item.propertyDetails.propertyAge} <br>
        Heating: ${item.propertyDetails.buildingHeating}
        </p>

        <h3>Floor and accommodation:</h3>
        <p>
        Rooms: ${item.propertyDetails.noRooms} <br>
        ${item.propertyDetails.sfac.sfaC1Text.replace(/(\r\n|\n|\r)/gm, '<br>')}
        </p>

        <div class="ds_accordion" data-module="ds-accordion">
            <div class="ds_accordion-item">
                <input type="checkbox" class="visually-hidden  ds_accordion-item__control" id="panel-1" aria-labelledby="panel-1-heading">
                <div class="ds_accordion-item__header">
                    <h3 id="panel-1-heading" class="ds_accordion-item__title">
                        Tenant
                    </h3>
                    <span class="ds_accordion-item__indicator"></span>
                    <label class="ds_accordion-item__label" for="panel-1"><span class="visually-hidden">Show this section</span></label>
                </div>

                <div class="ds_accordion-item__body">

                    <h3>Tenant:</h3>
                        <p>
                            ${item.tenancy.tenant.name} <br>
                            ${itemTenantAddressArray.join('<br>')}
                        </p>

                    <h3>Tenant agent:</h3>
                        <p>

                            ${itemTenantAgentNameArray} <br>
                            ${itemTenantAgentAddressArray.join('<br>')}

                        </p>

                    <h3>Landlord:</h3>
                        <p>
                            ${item.tenancy.landlord.name} <br>
                            ${itemLandlordAddressArray.join('<br>')}
                        </p>

                    <h3>Landlord agent:</h3>
                        <p>
                            ${itemLandlordAgentNameArray} <br>
                            ${itemLandlordAgentAddressArray.join('<br>')}
                        </p>
                </div>
            </div>

            <div class="ds_accordion-item">
                <input type="checkbox" class="visually-hidden  ds_accordion-item__control" id="panel-2" aria-labelledby="panel-2-heading">
                <div class="ds_accordion-item__header">
                    <h3 id="panel-2-heading" class="ds_accordion-item__title">
                    Case details
                    </h3>
                    <span class="ds_accordion-item__indicator"></span>
                    <label class="ds_accordion-item__label" for="panel-2"><span class="visually-hidden">Show this section</span></label>
                </div>

                <div class="ds_accordion-item__body">

                <h3>Tenancy type:</h3>
                <p>${item.tenancy.type}</p>

                <h3>Commenced on:</h3>
                <p>${item.tenancy.dateCommenced}</p>

                <h3>Rental period:</h3>
                <p>${item.tenancy.rentalPeriod}</p>

                <h3>File number:</h3>
                <p>${item.tenancy.fileNumber}</p>

                <h3>Allocation of liability for repairs:</h3>
                <p>${item.tenancy.repairLiability}</p>

                <h3>Service provided by landlord:</h3>
                <p>${item.application.services.provided}</p>

                <h3>Furniture provided by landlord:</h3>
                <p>${item.application.furniture.provided}</p>

                <h3>Other items of tenancy taken into consideration for determining a fair rent:</h3>
                <p>${item.tenancy.otherTerms}</p>

                </div>
            </div>

            <div class="ds_accordion-item">
            <input type="checkbox" class="visually-hidden  ds_accordion-item__control" id="panel-3" aria-labelledby="panel-3-heading">
            <div class="ds_accordion-item__header">
                <h3 id="panel-3-heading" class="ds_accordion-item__title">
                Registration details
                </h3>
                <span class="ds_accordion-item__indicator"></span>
                <label class="ds_accordion-item__label" for="panel-3"><span class="visually-hidden">Show this section</span></label>
            </div>

            <div class="ds_accordion-item__body">

                <h3>Application made by:</h3>
                <p>${item.application.by}</p>

                <h3>Date application received:</h3>
                <p>${date.ddMMyyyy(new Date(item.application.dateReceived))}</p>

                <h3>Registration number:</h3>
                <p>${item.application.registrationNumber}</p>

                <h3>Last registration date:</h3>
                <p>${date.ddMMyyyy(new Date(item.application.previousDateRegistered))}</p>

                <h3>Last effective date:</h3>
                <p>${date.ddMMyyyy(new Date(item.application.previousDateEffective))}</p>

                <h3>Last Registration number:</h3>
                <p>${item.application.previousRegistrationNumber}</p>

                <h3>(a) Rent determined:</h3>
                <p>${item.application.rent.amount}</p>

                <h3>(b) Registration on:</h3>
                <p>${date.ddMMyyyy(new Date(item.application.rent.dateRegistered))}</p>

                <h3>(c) Effective from:</h3>
                <p>${item.application.rent.dateEffective}</p>

                <h3>(d) Furniture amount:</h3>
                <p>${item.application.furniture.amount}</p>

                <h3>(e) Noted services amount:</h3>
                <p>${item.application.services.notedAmount}</p>

                <h3>(f) Service element:</h3>
                <p>${item.application.services.elementAmount}</p>

                <h3>Remarks:</h3>
                <p>${item.application.remarks}</p>

                </div>
            </div>

            <div id="appealdetails" class="ds_accordion-item">
            <input type="checkbox" class="visually-hidden  ds_accordion-item__control" id="panel-4" aria-labelledby="panel-4-heading">
            <div class="ds_accordion-item__header">
                <h3 id="panel-4-heading" class="ds_accordion-item__title">
                Appeals details
                </h3>
                <span class="ds_accordion-item__indicator"></span>
                <label class="ds_accordion-item__label" for="panel-4"><span class="visually-hidden">Show this section</span></label>
            </div>

            <div class="ds_accordion-item__body">

                <h3>Referred to First Tier Tribunal on:</h3>
                <p>${item.appeal.datePassed}</p>

                <h3>(a) Rent determined:</h3>
                <p>${item.appeal.rent.amount}</p>

                <h3>(b) Register on:</h3>
                <p>${item.appeal.rent.dateRegistered}</p>

                <h3>(c) Effective from:</h3>
                <p>${item.appeal.rent.dateEffective}</p>

                <h3>(d) Furniture amount:</h3>
                <p>${item.appeal.appealFurnitureAmount}</p>

                <h3>(e) Noted services amount:</h3>
                <p>${item.appeal.servicesNotedAmount}</p>

                <h3>(f) Service element:</h3>
                <p>${item.appeal.servicesElementAmount}</p>

                <h3>Remarks:</h3>
                <p>${item.appeal.remarks}</p>

                </div>
            </div>
        </div>
        </div><!--
        --></div>`;

        itemElement.innerHTML = itemHTML;

        // init accordion
        if (window.DS) {
            const accordions = [].slice.call(itemElement.querySelectorAll('[data-module="ds-accordion"]'));
            accordions.forEach(accordion => new window.DS.components.Accordion(accordion).init());
        }
    },

    addFetchErrorMessage: function (message = 'Unable to load data. Please try again later.') {
        const errorMessageContainer = document.createElement('div');

        errorMessageContainer.classList.add('ds_error-summary');
        errorMessageContainer.id = 'fetch-error-message';
        errorMessageContainer.innerHTML = `
            <h2 class="ds_error-summary__title" id="error-summary-title">There is a problem</h2>
            <p>${message}</p>`;

        document.querySelector('.ds_layout__content').appendChild(errorMessageContainer);
    },

    removeFetchErrorMessage: function () {
        const errorMessage = document.getElementById('fetch-error-message');
        if (errorMessage) {
            errorMessage.parentNode.removeChild(errorMessage);
        }
    }
};

window.format = fairRentRegister;
window.format.init();

export default fairRentRegister;
