// BUSINESS RATES CALCULATOR

'use strict';

import MultiPageForm from '../../components/multi-page-form';
import ServiceFinder from '../../components/service-finder';
import bloomreachWebfile from '../../tools/bloomreach-webfile';
import temporaryFocus from '@scottish-government/design-system/src/base/tools/temporary-focus/temporary-focus';
import PromiseRequest from '@scottish-government/design-system/src/base/tools/promise-request/promise-request';
import commonForms from '../../tools/forms';

const formTemplate = require('../../templates/mygov/business-rates-calculator');
const propertySelectTemplate = require('../../templates/mygov/business-rates-calculator-propertyselect');
const propertyResultsTemplate = require('../../templates/mygov/business-rates-calculator-results');

const formSections = [
    {
        group: {
            slug: 'part-0',
            title: 'Part zero'
        },
        hideFromSectionNav: true,
        slug: 'overview',
        title: 'Overview',
        pages: [
            {
                slug: 'overview',
                title: 'Overview',
                hideSubsectionNav: true,
                hideSectionNav: true,
                noFormBox: true
            }
        ]
    },
    {
        group: {
            slug: 'part-0',
            title: 'Part zero'
        },
        slug: 'property',
        title: 'Property',
        pages: [
            {
                slug: 'property',
                title: 'Property',
                hideSubsectionNav: true,
                hideSectionNav: true,
                noFormBox: true
            }
        ]
    },
    {
        group: {
            slug: 'part-0',
            title: 'Part zero'
        },
        slug: 'result',
        title: 'Results',
        pages: [
            {
                slug: 'result',
                title: 'Results',
                hideSubsectionNav: true,
                hideSectionNav: true,
                triggerEvent: 'createResultsPage',
                noFormBox: true
            }
        ]
    }
];

const businessRatesCalculator = {

    form: new MultiPageForm({
        formSections: formSections,
        formMapping: {},
        formObject: {},
        formEvents: {
            createResultsPage: () => {
                businessRatesCalculator.setCurrentProperty();
                businessRatesCalculator.updateOrAddProperty();
                if (businessRatesCalculator.selectedProperties.length) {
                    businessRatesCalculator.calculateResults();
                    businessRatesCalculator.showResults();
                    const tabSets = [].slice.call(document.querySelectorAll('[data-module="ds-tabs"]'));
                    tabSets.forEach(tabSet => new window.DS.components.Tabs(tabSet).init());
                } else {
                    window.location.hash = "!/property";
                }
            }
        },
        noSectionNav: true
    }),

    apiUrl: '/address/?search=',

    init: function (today = new Date()) {
        // date override from querystring
        const qsParams = new URLSearchParams(window.location.search);
        if (qsParams.get('date')) {
            const date = new Date(`${qsParams.get('date').substring(0,4)}-${qsParams.get('date').substring(4,6)}-${qsParams.get('date').substring(6,8)}`);
            if (!isNaN(date.getTime())) {
                today = date;
            }
        }

        // append form template
        const formTemplateContainer = document.querySelector('#form-container');
        if (!formTemplateContainer) {
            return false;
        }
        const overviewContent = formTemplateContainer.innerHTML;

        // somewhat jank way of moving the overview header
        const overviewHeader = document.querySelector('.ds_page-header');

        formTemplateContainer.innerHTML = formTemplate.render({
            iconsFile: bloomreachWebfile('/assets/images/icons/icons.stack.svg'),
            webfilesPath: bloomreachWebfile()
        });
        formTemplateContainer.querySelector('#overview').innerHTML = overviewHeader.outerHTML + overviewContent + formTemplateContainer.querySelector('#overview').innerHTML;

        overviewHeader.parentNode.removeChild(overviewHeader);

        this.selectedProperties = [];

        this.form.init();

        this.addressSearchForm = document.querySelector('#address-form');
        this.postcodeSearchForm = document.querySelector('#postcode-form');
        this.propertySelectWrapper = document.querySelector('#property-select-wrapper');
        this.propertySelectPlayback = document.querySelector('#property-select-playback');
        this.propertySelectForm = document.querySelector('#property-select-form');
        this.errorSummary = document.querySelector('#feedback-box');

        // taken from provided spreadsheet
        this.ratesCalculatorData = {};

        this.ratesCalculatorData.sbbs_100_rv_threshold = 12000;
        this.ratesCalculatorData.sbbs_100_percentage_relief = 1;
        this.ratesCalculatorData.sbbs_25_rv_threshold = 18000;
        this.ratesCalculatorData.sbbs_25_percentage_relief = 0.25;
        this.ratesCalculatorData.sbbs_combined_threshold = 35000;
        this.ratesCalculatorData.financial_year = '2024-2025';
        this.ratesCalculatorData.poundage = 0.498;
        this.ratesCalculatorData.intermediate_business_supplement_threshold = 51000;
        this.ratesCalculatorData.intermediate_business_supplement = 0.047;
        this.ratesCalculatorData.large_business_supplement_threshold = 100000;
        this.ratesCalculatorData.large_business_supplement = 0.061;

        const newFiscalYeardate = new Date(2025, 3, 1);
        if (today > newFiscalYeardate) {
            this.ratesCalculatorData.financial_year = '2025-2026';
            this.ratesCalculatorData.intermediate_business_supplement = 0.056;
            this.ratesCalculatorData.large_business_supplement = 0.070;
        }

        // adjust this to use different value types, e.dg. current rateable value or proposed rateable value
        this.rateableValueType = 'rv';

        // init accordions
        const accordionElements = [].slice.call(document.querySelectorAll('[data-module="ds-accordion"]'));
        accordionElements.forEach(accordionElement => new DS.components.Accordion(accordionElement).init());

        // init service finders
        const serviceFinders = [].slice.call(document.querySelectorAll('.mg_service-finder'));
        serviceFinders.forEach(serviceFinder => new ServiceFinder(serviceFinder).init());

        // init events (submit address, submit postcode, )
        this.bindEvents();
    },

    /**
     * Binds events from the 'events' list for the BRC to code.
     */
    bindEvents: function () {
        document.body.addEventListener('click', (event) => {
            if (event.target.classList.contains('js-show-address-search')) {
                event.preventDefault();
                this.showAddressSearch();
            } else if (event.target.classList.contains('js-show-postcode-search')) {
                event.preventDefault();
                this.showPostcodeSearch();
            } else if (event.target.classList.contains('js-add-another')) {
                this.resetForms();
            } else if (event.target.classList.contains('js-clear-results')) {
                this.selectedProperties = [];
                this.resetForms();
            } else if (event.target.classList.contains('js-show-results')) {
                if (!this.validateStep('property')) {
                    event.preventDefault();
                    this.showErrorSummary();
                }
            } else if (event.target.classList.contains('js-remove-property')) {
                event.preventDefault();
                this.removePropertyFromResults(event.target.getAttribute('data-property-index'));
            }
        });

        document.body.addEventListener('change', (event) => {
            if (event.target === this.propertySelectWrapper) {
                event.preventDefault();
                businessRatesCalculator.setCurrentProperty();
            }
        });

        document.body.addEventListener('submit', (event) => {
            if (event.target === document.querySelector('#postcode-form')) {
                event.preventDefault();
                this.findByPostcode();
            }
            if (event.target === document.querySelector('#address-form')) {
                event.preventDefault();
                this.findByAddress();
            }
        });
    },

    calculateResults: function () {
        // SBBS eligibility depends on ALL properties' rateable values
        let totalRateable = 0;

        for (let i = 0, il = this.selectedProperties.length; i < il; i++) {
            totalRateable += parseInt(this.selectedProperties[i][businessRatesCalculator.rateableValueType]);
        }

        for (let j = 0, jl = this.selectedProperties.length; j < jl; j++) {

            const property = this.selectedProperties[j];
            let sbbsReliefFraction,
                reliefFraction = 0;

            // reset appliedRelief
            delete property.appliedRelief;

            property.reliefs = [];

            property.baseLiability = this.getBaseLiability(property[businessRatesCalculator.rateableValueType]);
            property.netLiability = property.baseLiability;

            sbbsReliefFraction = this.getSBBSFraction(totalRateable, property);

            // only add SBBS relief if it's being used
            if (sbbsReliefFraction > 0) {
                property.reliefs.push({
                    name: 'Small Business Bonus Scheme',
                    fraction: sbbsReliefFraction,
                    amount: property.netLiability * sbbsReliefFraction
                });
            }

            // only the largest relief will be applied
            for (let k = 0, kl = property.reliefs.length; k < kl; k++) {
                if (property.reliefs[k].fraction > reliefFraction) {
                    reliefFraction = property.reliefs[k].fraction;
                    property.appliedRelief = property.reliefs[k];
                }
            }

            if(property.appliedRelief) {
                property.netLiability = Math.max(property.netLiability - property.appliedRelief.amount.toFixed(2), 0);
            }
        }
    },

    cleanOnSearch: function () {
        // remove errors
        this.errorSummary.querySelector('.form-errors').innerHTML = '';

        // remove property list
        this.propertySelectPlayback.innerHTML = '';
        this.propertySelectForm.innerHTML = '';
    },

    findByAddress: function () {
        const address = document.querySelector('#address');
        const town = document.querySelector('#town');

        this.cleanOnSearch();

        if (this.validateStep('property')) {
            this.getProperties((address.value + ' ' + town.value).trim());
            this.searchType = 'street-town';

            const fields = [];
            if (address.value !== '') {
                fields.push(address.value);
            }
            if (town.value !== '') {
                fields.push(town.value);
            }
            this.searchValue = fields.join(', ');
        } else {
            document.title = this.form.getStepTitle(this.form.getCurrentStep(), true);
            this.showErrorSummary();
        }
    },

    findByPostcode: function () {
        const postcodeField = document.querySelector('#postcode');
        const postcodeValue = postcodeField.value.toUpperCase().replace(/\s+/g, '');

        this.cleanOnSearch();

        if (this.validateStep('property')) {
            this.getProperties(postcodeValue);
            this.searchType = 'postcode';
            this.searchValue = postcodeValue;
        } else {
            document.title = this.form.getStepTitle(this.form.getCurrentStep(), true);
            this.showErrorSummary();
        }
    },

    /**
     * A large business supplement is added to properties with a rateable value over the supplement threshold
     * @param {number} rateableValue of a property
     * @return {number} the property's base liability
     */
    getBaseLiability: function (rateableValue) {
        let poundage = this.ratesCalculatorData.poundage;

        if (rateableValue > this.ratesCalculatorData.large_business_supplement_threshold) {
            poundage = poundage + this.ratesCalculatorData.large_business_supplement;
        } else if (this.ratesCalculatorData.intermediate_business_supplement_threshold && rateableValue > this.ratesCalculatorData.intermediate_business_supplement_threshold) {
            poundage = poundage + this.ratesCalculatorData.intermediate_business_supplement;
        }

        return rateableValue * poundage;
    },

    getProperties: function (searchTerm) {
        // disable buttons
        const postcodeButton = document.getElementById('find-by-postcode');
        const addressButton = document.getElementById('find-by-address');
        postcodeButton.setAttribute('disabled', true);
        addressButton.setAttribute('disabled', true);

        // do search
        PromiseRequest(`${businessRatesCalculator.apiUrl}${searchTerm}`)
            .then(
                (data) => {
                    const result = JSON.parse(data.response);

                    if (result.properties && result.properties.length) {
                        delete this.propertySelectWrapper.dataset.error;
                        businessRatesCalculator.searchResults = result.properties;
                        businessRatesCalculator.showSearchResults();
                    } else if (result.resultType === 'too-many-results') {
                        this.propertySelectWrapper.dataset.error = 'too-many-results';
                        this.propertySelectForm.innerHTML = `<h3>Too many results</h3>
                        <p>Sorry, the information you entered returned too many properties. Please enter more of the address or postcode. Use the <a href="http://www.royalmail.com/find-a-postcode">Royal Mail website</a> to find the postcode.</p>`;
                    } else {
                        this.propertySelectWrapper.dataset.error = 'no-results-found';
                        this.propertySelectForm.innerHTML = `<h3>No results found</h3>
                        <p>Please check that the address or postcode you're searching for is valid and in Scotland. The calculator uses address data from the Scottish Assessors Association (SAA).</p>
                        <p>If you have entered a valid business location and your property can't be found, <a id="unlisted" href="https://www.saa.gov.uk/contact-us/feedback/">please report this to the Scottish Assessors Association</a>.</p>`;
                        }
                },
                (error) => {
                    this.propertySelectWrapper.dataset.error = 'no-results-found';
                    this.propertySelectForm.innerHTML = `<h3>No results found</h3>
                    <p>Please check that the address or postcode you're searching for is valid and in Scotland. The calculator uses address data from the Scottish Assessors Association (SAA).</p>
                    <p>If you have entered a valid business location and your property can't be found, <a id="unlisted" href="https://www.saa.gov.uk/contact-us/feedback/">please report this to the Scottish Assessors Association</a>.</p>`;
                }
        )
            .finally(() => {
                // enable buttons
                postcodeButton.removeAttribute('disabled');
                addressButton.removeAttribute('disabled');

                const playbackData = {};
                if (this.searchType === 'postcode') {
                    playbackData.type = 'Postcode';
                    playbackData.value = this.searchValue;
                    playbackData.buttonClass = 'js-show-postcode-search';
                } else {
                    playbackData.type = 'Street and town';
                    playbackData.value = this.searchValue;
                    playbackData.buttonClass = 'js-show-address-search';
                }

                this.propertySelectPlayback.innerHTML = `<dl class="ds_prefilled-value-list" aria-label="Your current answers">
                    <dt class="ds_prefilled-value-list__key">${playbackData.type}</dt>
                    <dd class="ds_prefilled-value-list__value">
                        <div class="brc-capitalise">${playbackData.value}</div>
                        <button class="ds_link  ds_prefilled-value-list__value-actions  ${playbackData.buttonClass}">Change <span class="visually-hidden">${playbackData.type}<</span></button>
                    </dd>
                </dl>`;

                this.addressSearchForm.classList.add('fully-hidden');
                this.postcodeSearchForm.classList.add('fully-hidden');
                this.propertySelectWrapper.classList.remove('fully-hidden');
                temporaryFocus(this.propertySelectWrapper);
            });
    },

    /**
     * Calculate the fraction of rateable-value relief which derives from the Small Business Bonus Scheme.
     * @param {number} totalRateable The total rateable value of all buildings added by the user so far.
     * @param {object} property The building currently being considered.
     * @return {number} The percentage relief to be applied.
     */
    getSBBSFraction: function (totalRateable, property) {
        let sbbsReliefFraction;

        const sbbs_combined_threshold = 35000;
        const sbbs_mid_threshold = 12000;
        const sbbs_high_threshold = 15000;
        const sbbs_high_cutoff = 20000;

        function sbbs_mid_function (rv) {
            return (1 - (0.75 * (1 - (sbbs_high_threshold - rv) / 3000)));
        }

        function sbbs_high_function (rv) {
            return 0.25 * ((sbbs_high_cutoff - rv)/5000);
        }

        if (this.selectedProperties.length > 1) {
            // multiple properties
            if (totalRateable > sbbs_combined_threshold) {
                sbbsReliefFraction = 0;
            } else if (totalRateable > sbbs_mid_threshold) {
                if (property.rv > sbbs_high_threshold) {
                    sbbsReliefFraction = sbbs_high_function(property.rv);
                } else {
                    sbbsReliefFraction = 0.25;
                }
            } else {
                sbbsReliefFraction = 1;
            }
        } else {
            // single property
            if (property.rv > sbbs_high_cutoff) {
                sbbsReliefFraction = 0;
            } else if (property.rv > sbbs_high_threshold) {
                sbbsReliefFraction = sbbs_high_function(property.rv);
            } else if (property.rv > sbbs_mid_threshold) {
                sbbsReliefFraction = sbbs_mid_function(property.rv);
            } else {
                sbbsReliefFraction = 1;
            }
        }

        return sbbsReliefFraction;
    },

    removePropertyFromResults: function (propertyIndex) {
        // remove property from selectedProperties array
        this.selectedProperties.splice(propertyIndex, 1);

        // recalculate & show results
        businessRatesCalculator.calculateResults();
        businessRatesCalculator.showResults();
    },

    /**
     * Reset all forms within this tool.
     */
    resetForms: function () {
        delete this.currentProperty;
        delete this.searchResults;

        [].slice.call(document.querySelectorAll('[data-step="property"] input[type="text"]')).forEach(input => {
            input.value = '';
        });

        [].slice.call(document.querySelectorAll('[data-step="property"] input[type="radio"]')).forEach(radio => {
            radio.removeAttribute('checked');
        });

        [].slice.call(document.querySelectorAll('[data-step="property"] select')).forEach(select => {
            select.querySelector('option:checked').removeAttribute('selected');
        });

        this.cleanOnSearch();
        this.showPostcodeSearch();
    },

    /**
     * Sets the object representing a building in the current calculation to equal the search-result
     * referenced by the currently-selected OPTION tag in the DOM's SELECT list of buildings or to
     * the the single returned property if the returned property set has only one entry.
     */
    setCurrentProperty: function () {
        let selectedProperty;

        // add selected property
        if (this.searchResults.length === 1) {
            this.currentProperty = this.searchResults[0];
        } else {
            selectedProperty = document.querySelector('#property-list option:checked');
            this.currentProperty = this.searchResults[selectedProperty.value];
        }
    },

    showAddressSearch: function () {
        this.addressSearchForm.classList.remove('fully-hidden');
        this.postcodeSearchForm.classList.add('fully-hidden');
        this.propertySelectWrapper.classList.add('fully-hidden');
        temporaryFocus(this.addressSearchForm);
    },

    showErrorSummary: function () {
        this.errorSummary.classList.remove('fully-hidden');
        temporaryFocus(this.errorSummary);
    },

    showPostcodeSearch: function () {
        this.addressSearchForm.classList.add('fully-hidden');
        this.postcodeSearchForm.classList.remove('fully-hidden');
        this.propertySelectWrapper.classList.add('fully-hidden');
        temporaryFocus(this.postcodeSearchForm);
    },

    showResults: function () {
        const resultsContainer = document.querySelector('#property-results');
        resultsContainer.innerHTML = propertyResultsTemplate.render({ properties: this.selectedProperties });
    },

    showSearchResults: function () {
        const resultsData = {
            searchResults: this.searchResults,
            showAsSelect: true
        };

        if (resultsData.searchResults.length === 1) {
            // show a single property if there is only one result
            resultsData.showAsSelect = false;
        }

        resultsData.searchResults.forEach(result => {
            if (result.occupier.length) {
                result.occupierName = result.occupier[0].name.split('\n')[0];
            }
        });

        resultsData.iconsFile = bloomreachWebfile('/assets/images/icons/icons.stack.svg');
        this.propertySelectForm.innerHTML = propertySelectTemplate.render(resultsData);

        // preselect the first item if we have one property result
        if (this.searchResults.length === 1) {
            // select the first property
            document.querySelector('#property-list input[type=radio]').checked = true;
        }

        businessRatesCalculator.setCurrentProperty();
    },

    updateOrAddProperty: function () {
        let match = false;

        for (let i = 0, il = this.selectedProperties.length; i < il; i++) {
            if (this.selectedProperties[i].address === this.currentProperty.address && this.selectedProperties[i].occupierName === this.currentProperty.occupierName) {
                match = true;
                this.selectedProperties[i] = this.currentProperty;
                break;
            }
        }

        if (!match) {
            // store the completed current property on root scope's selectedProperties
            this.selectedProperties.push(this.currentProperty);
        }
    },

    validateStep: function (step) {
        /*
         * look for data-validation attributes in current step & PERFORM VALIDATION
         * do not allow progress if invalid
         */

       const stepContainer = document.querySelector(`[data-step="${step}"]`);
       const itemsThatNeedToBeValidated = [].slice.call(stepContainer.querySelectorAll('[data-validation]')).filter(item => item.offsetParent);

       itemsThatNeedToBeValidated.forEach(item => {
           const validations = item.getAttribute('data-validation').split(' ');
           const validationChecks = [];
           for (let i = 0, il = validations.length; i < il; i++) {
               validationChecks.push(commonForms[validations[i]]);
            }
            commonForms.validateInput(item, validationChecks);
        });

        const invalidFields = [].slice.call(stepContainer.querySelectorAll('[aria-invalid="true"]')).filter(item => item.offsetParent);

        return invalidFields.length === 0;
    }
};

window.format = businessRatesCalculator;
window.format.init();

export default businessRatesCalculator;
