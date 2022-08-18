// BUSINESS RATES CALCULATOR

'use strict';

import MultiPageForm from '../../components/multi-page-form';
import feedback from '../../components/feedback';
import bloomreachWebfile from '../../tools/bloomreach-webfile';

const PolyPromise = require('../../vendor/promise-polyfill').default;
const formTemplate = require('../../templates/mygov/business-rates-calculator');
const propertySelectTemplate = require('../../templates/mygov/business-rates-calculator-propertyselect');
const propertyResultsTemplate = require('../../templates/mygov/business-rates-calculator-results');
const postcodeRegExp = new RegExp('^([A-PR-UWYZ0-9][A-HK-Y0-9][AEHMNPRTVXY0-9]?[ABEHMNPRVWXY0-9]? {0,2}[0-9][ABD-HJLN-UW-Z]{2}|GIR ?0AA)$');
const scottishPostcodeRegExp = new RegExp('^(AB|DD|DG|EH|FK|G|HS|IV|KA|KW|KY|ML|PA|PH|TD|ZE)[0-9]{1,2} {0,2}[0-9][ABD-HJLN-UW-Z]{2}$');

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
                hideSubsectionNav: true
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
                } else {
                    window.location.hash = "!/property";
                }
            }
        },
        noSectionNav: true
    }),

    apiUrl: '/address/?search=',

    init: function (today = new Date()) {
        // append form template
        const formTemplateContainer = document.querySelector('#form-container');
        if (!formTemplateContainer) {
            return false;
        }
        const overviewContent = formTemplateContainer.innerHTML;
        formTemplateContainer.innerHTML = formTemplate.render({
            iconsFile: bloomreachWebfile('/assets/images/icons/icons.stack.svg'),
            webfilesPath: bloomreachWebfile()
        });
        formTemplateContainer.querySelector('#overview').innerHTML = overviewContent + formTemplateContainer.querySelector('#overview').innerHTML;

        this.selectedProperties = [];

        this.form.validateStep = this.validateStep;
        this.form.init();
        feedback.init();


        // taken from provided spreadsheet
        this.ratesCalculatorData = {};

        this.ratesCalculatorData.sbbs_100_rv_threshold = 15000;
        this.ratesCalculatorData.sbbs_100_percentage_relief = 1;
        this.ratesCalculatorData.sbbs_25_rv_threshold = 18000;
        this.ratesCalculatorData.sbbs_25_percentage_relief = 0.25;
        this.ratesCalculatorData.sbbs_combined_threshold = 35000;
        this.ratesCalculatorData.financial_year = '2021-2022';
        this.ratesCalculatorData.poundage = 0.490;
        this.ratesCalculatorData.intermediate_business_supplement_threshold = 51000;
        this.ratesCalculatorData.intermediate_business_supplement = 0.013;
        this.ratesCalculatorData.large_business_supplement_threshold = 95000;
        this.ratesCalculatorData.large_business_supplement = 0.026;

        const newFiscalYeardate = new Date(2022, 3, 1);
        if (today > newFiscalYeardate) {
            this.ratesCalculatorData.financial_year = '2022-2023';
            this.ratesCalculatorData.poundage = 0.498;
        }

        // adjust this to use different value types, e.dg. current rateable value or proposed rateable value
        this.rateableValueType = 'rv';

        // init accordions
        const accordionElements = [].slice.call(document.querySelectorAll('[data-module="ds-accordion"]'));
        accordionElements.forEach(accordionElement => new DS.components.Accordion(accordionElement).init());

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
                this.showAddress();
            } else if (event.target.classList.contains('js-add-another')) {
                this.resetForms();
            } else if (event.target.classList.contains('js-clear-results')) {
                this.selectedProperties = [];
                this.resetForms();
            } else if (event.target.classList.contains('js-remove-property')) {
                event.preventDefault();
                this.removePropertyFromResults(event.target.getAttribute('data-property-index'));
            }
        });

        document.body.addEventListener('change', (event) => {
            if (event.target === document.querySelector('#property-select')) {
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

    findByPostcode: function () {
        const postcode = document.querySelector('#postcode');
        const postcodeValue = postcode.value.toUpperCase().replace(/\s+/g, '');

        this.cleanOnSearch();

        // check whether it's a valid postcode
        if (postcodeValue.match(postcodeRegExp)) {
            // now check whether it's a Scottish postcode
            if (postcodeValue.match(scottishPostcodeRegExp)) {
                this.getProperties(postcodeValue);
            } else {
                this.addFieldError('postcode', 'You have entered a postcode associated with an address outside Scotland. This site can only be used to calculate Business Rates for Scottish companies. <a href="https://www.gov.uk/calculate-your-business-rates">Please visit gov.uk for more information</a>.');
                postcode.focus();
            }
        } else {
            this.addFieldError('postcode', 'Please try searching again using a full business postcode.');
            postcode.focus();
        }
    },

    findByAddress: function () {
        const address = document.querySelector('#address');
        const town = document.querySelector('#town');

        this.cleanOnSearch();

        if (town.value === '' && address.value === '') {
            this.addFieldGroupError('street-and-town', 'Please enter a street or town.');
            address.focus();
        } else {
            this.getProperties((address.value + ' ' + town.value).trim());
        }
    },

    showAddress: function () {
        document.querySelector('#address-form').classList.remove('fully-hidden');
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

        // hide address inputs
        document.querySelector('#address-form').classList.add('fully-hidden');
    },

    cleanOnSearch: function () {
        // remove errors
        this.removeFieldError('postcode');
        this.removeFieldGroupError('street-and-town');

        // remove property list
        document.getElementById('property-select-wrapper').innerHTML = '';
    },

    getProperties: function (searchTerm) {
        // disable buttons
        const postcodeButton = document.getElementById('find-by-postcode');
        const addressButton = document.getElementById('find-by-address');
        postcodeButton.setAttribute('disabled', true);
        addressButton.setAttribute('disabled', true);

        // do search
        this.promiseRequest(`${businessRatesCalculator.apiUrl}${searchTerm}`)
            .then(
                (data) => {
                    const result = JSON.parse(data.response);

                    if (result.properties && result.properties.length) {
                        businessRatesCalculator.searchResults = result.properties;
                        businessRatesCalculator.showSearchResults();
                    } else if (result.resultType === 'too-many-results') {
                        document.querySelector('#property-select-wrapper').innerHTML = `<h3>Too many results</h3>
                        <p>Sorry, the information you entered returned too many properties. Please enter more of the address or postcode. Use the <a href="http://www.royalmail.com/find-a-postcode">Royal Mail website</a> to find the postcode.</p>`;
                    } else {
                        document.querySelector('#property-select-wrapper').innerHTML = `<h3>No results found</h3>
                        <p>Please check that the address or postcode you're searching for is valid and in Scotland. The calculator uses address data from the Scottish Assessors Association (SAA).</p>
                        <p>If you have entered a valid business location and your property can't be found, <a id="unlisted" href="https://www.saa.gov.uk/contact-us/feedback/">please report this to the Scottish Assessors Association</a>.</p>`;
                        }
                    // enable buttons
                    postcodeButton.removeAttribute('disabled');
                    addressButton.removeAttribute('disabled');
                },
                (error) => {
                    document.querySelector('#property-select-wrapper').innerHTML = `<h3>No results found</h3>
                    <p>Please check that the address or postcode you're searching for is valid and in Scotland. The calculator uses address data from the Scottish Assessors Association (SAA).</p>
                    <p>If you have entered a valid business location and your property can't be found, <a id="unlisted" href="https://www.saa.gov.uk/contact-us/feedback/">please report this to the Scottish Assessors Association</a>.</p>`;

                    // enable buttons
                    postcodeButton.removeAttribute('disabled');
                    addressButton.removeAttribute('disabled');
                }
            );
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
        document.querySelector('#property-select-wrapper').innerHTML = propertySelectTemplate.render(resultsData);

        // preselect the first item if we have one property result
        if (this.searchResults.length === 1) {
            // select the first property
            document.querySelector('#property-list input[type=radio]').checked = true;
        }

        businessRatesCalculator.setCurrentProperty();
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

    updateOrAddProperty: function () {
        let match = false;

        for (let i = 0, il = this.selectedProperties.length; i < il; i++) {
            if (this.selectedProperties[i].address === this.currentProperty.address) {
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

    /**
     * Calculate the fraction of rateable-value relief which derives from the Small Business Bonus Scheme.
     * @param {number} totalRateable The total rateable value of all buildings added by the user so far.
     * @param {object} property The building currently being considered.
     * @return {number} The percentage relief to be applied.
     */
    getSBBSFraction: function (totalRateable, property) {
        let sbbsReliefFraction;

        if (totalRateable > this.ratesCalculatorData.sbbs_combined_threshold) {
            sbbsReliefFraction = 0;
        } else if (property.rv <= this.ratesCalculatorData.sbbs_100_rv_threshold) {
            sbbsReliefFraction = this.ratesCalculatorData.sbbs_100_percentage_relief;
        } else if (property.rv <= this.ratesCalculatorData.sbbs_25_rv_threshold) {
            sbbsReliefFraction = this.ratesCalculatorData.sbbs_25_percentage_relief;
        } else {
            sbbsReliefFraction = 0;
        }

        return sbbsReliefFraction;
    },

    showResults: function () {
        const resultsContainer = document.querySelector('#property-results');
        resultsContainer.innerHTML = propertyResultsTemplate.render({ properties: this.selectedProperties });
    },

    addFieldGroupError: function (fieldGroupId, message) {
        const fieldGroup = document.getElementById(fieldGroupId);

        fieldGroup.classList.add('ds_question--error');

        const messageEl = document.createElement('p');
        messageEl.classList.add('ds_question__message');
        messageEl.setAttribute('data-form', 'error-more-detail');
        messageEl.innerHTML = message;

        fieldGroup.insertBefore(messageEl, fieldGroup.firstChild);

        const fields = [].slice.call(fieldGroup.querySelectorAll('input, textarea, select'));

        fields.forEach(field => {
            field.classList.add('ds_input--error');
            field.setAttribute('aria-invalid', true);
        });
    },

    removeFieldGroupError: function (fieldGroupId) {
        const fieldGroup = document.getElementById(fieldGroupId);

        fieldGroup.classList.remove('ds_question--error');

        const fields = [].slice.call(fieldGroup.querySelectorAll('input, textarea, select'));

        fields.forEach(field => {
            field.classList.remove('ds_input--error');
            field.removeAttribute('aria-invalid');
        });

        const messageEl = fieldGroup.querySelector('.ds_question__message');
        if (messageEl) {
            messageEl.parentNode.removeChild(messageEl);
        }
    },

    addFieldError: function (fieldId, message) {
        const field = document.getElementById(fieldId);

        this.removeFieldError(fieldId);

        field.closest('.ds_question').classList.add('ds_question--error');
        field.classList.add('ds_input--error');
        field.setAttribute('aria-invalid', true);

        const messageEl = document.createElement('p');
        messageEl.classList.add('ds_question__message');
        messageEl.setAttribute('data-form', 'error-more-detail');
        messageEl.innerHTML = message;

        if (field.parentNode.classList.contains('ds_input__wrapper')) {
            field.parentNode.insertAdjacentElement('beforebegin', messageEl);
        } else {
            field.insertAdjacentElement('beforebegin', messageEl);
        }
    },

    removeFieldError: function (fieldId) {
        const field = document.getElementById(fieldId);

        const questionEl = field.closest('.ds_question');
        questionEl.classList.remove('ds_question--error');
        field.classList.remove('ds_input--error');
        field.removeAttribute('aria-invalid');

        const messageEl = questionEl.querySelector('.ds_question__message');
        if (messageEl) {
            messageEl.parentNode.removeChild(messageEl);
        }
    },

    promiseRequest: (url, method = 'GET') => {
        const request = new XMLHttpRequest();

        return new PolyPromise((resolve, reject) => {
            request.onreadystatechange = () => {
                if (request.readyState !== 4) {
                    return;
                }

                if (request.status >= 200 && request.status < 300) {
                    resolve(request);
                } else {
                    reject({
                        status: request.status,
                        statusText: request.statusText
                    });
                }
            };

            request.open(method, url, true);
            request.send();
        });
    },

    removePropertyFromResults: function (propertyIndex) {
        // remove property from selectedProperties array
        this.selectedProperties.splice(propertyIndex, 1);

        // recalculate & show results
        businessRatesCalculator.calculateResults();
        businessRatesCalculator.showResults();
    },

    errorMessageMarkup: function (fieldId, message) {
        const messageEl = document.createElement('p');
        messageEl.classList.add('error', 'invalid-field', 'ds_error-summary__list');
        messageEl.innerHTML = `<a class="ds_error-summary__link" href="#${fieldId}">${message}</a>`;
        return messageEl;
    }
};

window.format = businessRatesCalculator;
window.format.init();

export default businessRatesCalculator;
