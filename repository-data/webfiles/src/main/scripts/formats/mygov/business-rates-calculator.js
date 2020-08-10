// BUSINESS RATES CALCULATOR
/*
Contains functionality for the business rates calculator page
*/

'use strict';

import feedback from '../../components/feedback';
import Handlebars from '../../vendor/hbs/handlebars';
import $ from 'jquery';

// templates used by the calculator
const templates = {
    // displays a property selection form for use when a search returns multiple properties
    propertySelect: '<form id="property-list">' +
        '<div id="property-selection" class="result-count-{{searchResults.length}}" >' +

        '{{#if showAsSelect}}' +
        '<label class="ds_label" for="property-select">Select the property</label>' +
        '<div class="ds_select-wrapper">' +
        '<select id="property-select" class="ds_select">' +
        '{{#searchResults}}' +
        '<option value="{{@index}}">' +
        '{{propertyAddress .}}' +
        '</option>' +
        '{{/searchResults}}' +
        '</select>' +
        '<span class="ds_select-arrow" aria-hidden="true"></span>' +
        '</div>' +

        '{{else}}' +
        '{{#searchResults}}' +
        '<div class="ds_radio">' +
            '<input class="ds_radio__input" id="propertyList{{ @index }}" name="property-list" type="radio" data-form="radio-property-list">' +
            '<label class="ds_radio__label" for="propertyList{{ @index }}">{{{propertyAddressMultiLine .}}}</label>' +
        '</div>' +
        '{{/searchResults}}' +
        '{{/if}}' +

        '<p><a href="https://www.saa.gov.uk/contact-us/feedback/" class="small">Is your property listed? Let the Assessor know.</a></p>' +
        '</div>' +
        '<button class="ds_button">Next</button>' +
        '</form>',

    // displays final result of the calculator
    resultsTemplate: '{{#properties}}' +
        '<div class="mg_leg_result">' +
        '<div class="indent">' +
        '{{{propertyResultTitle @index ..}}}' +

        '<div class="address">' +
        '<h4>Address</h4>' +
        '<p>{{{propertyAddress .}}}</p>' +
        '</div>' +

        '<div class="block right">' +
        '<h4>Rateable value</h4>' +
        '<p>{{formatCurrency rv}}</p>' +

        '<h4>Liability before relief</h4>' +
        '<p>{{formatCurrency baseLiability}}</p>' +
        '</div>' +

        '<div class="relief right block">' +
        '<h3>Relief applied</h3>' +

        '{{#appliedRelief}}' +

        '<h4>{{name}}</h4>' +
        '<p class="discount">{{formatDiscount fraction}}</p>' +
        '<p>{{formatCurrency amount}}</p>' +

        '{{/appliedRelief}}' +

        '{{#universalRelief}}' +

        '<h4>{{name}}</h4>' +
        '<p class="discount">{{formatDiscount fraction}}</p>' +
        '<p>{{formatCurrency amount}}</p>' +

        '{{/universalRelief}}' +

        '</div>' +

        '<div class="liability block right">' +
        '<h4>Net liability</h4>' +
        '<p>The amount you might have to pay:</p>' +

        '<p class="large heavy">{{formatCurrency netLiability}}</p>' +
        '</div>' +

        '<p><a href="#introduction" class="ds_button  ds_button--cancel">Clear results and start again</a></p>' +

        '<div class="ds_inset-text"><div class="ds_inset-text__text">' +
        'If you have more than one business property, it might affect your eligibility for the Small Business Bonus Scheme. You can <a href="#property-address" class="reset">add additional properties</a>.' +
        '</div></div>' +

        '<h4 class="gamma">Extra reliefs available</h4>' +

        '<p>You might be able to get extra reliefs. You can:</p>' +

        '<ul>' +
            '<li>find more information on <a href="https://www.mygov.scot/business-rates-relief/" target="_blank">business rates reliefs</a></li>' +
            '<li>contact your <a href="https://www.mygov.scot/find-your-local-council/" target="_blank">local council</a> for advice</p>' +
        '</ul>' +

        '</div>' +

        '<h2 class="emphasis">What to do next?</h2>' +

        '<div class="indent">' +

        '<p>The exact amount you\'ll pay can only be provided by your local council when it sends you your rates bill.</p>' +
        '<p>A rates bill will be issued when you notify the council of your move to the new premises.</p>' +
        '<a href="{{localAuthority.links.tax}}{{#unless localAuthority.links.tax}}{{localAuthority.links.homepage}}{{/unless}}" class="ds_button">' +
        'Contact {{localAuthority.name}} council' +
        '</a>' +

        '</div>' +
        '</div>' +
        '{{/properties}}',

    // displays an error message with a title
    largeError: '<div class="form-error error-block" id="{{inputId}}">' +
        '<h3 class="error-text">{{title}}</h3>' +
        '{{{content}}}' +
        '</div>'
};

// template helpers used by the calculator

// prepends the occupier name (if present) to the address
Handlebars.registerHelper('propertyAddress', function (data) {
    let address = '';
    if (data.occupier[0]) {
        address += data.occupier[0].name.split('\n')[0] + ', ';
    }
    address += data.address;

    return address;
});

// prepends the occupier name (if present) to the address and inserts line breaks
Handlebars.registerHelper('propertyAddressMultiLine', function (data) {
    let address = '';
    if (data.occupier[0]) {
        address += data.occupier[0].name.split('\n')[0] + '<br>';
    }
    address += data.address.replace(/\n/ig, '<br>');

    return address;
});

// prettily formats a number as a currency string with symbol, comma separation, and correct decimal places
Handlebars.registerHelper('formatCurrency', function (value) {
    const baseParts = Number(value).toFixed(2).split('.');
    const commaGroupedUnits = baseParts[0].split('').reverse().join('').match(/.{1,3}/g).join(',').split('').reverse().join('');
    return `£${commaGroupedUnits}.${baseParts[1]}`;
});

// outputs a string representing the discount fraction as a percentage
Handlebars.registerHelper('formatDiscount', function (value) {
    return `${value * 100}% discount`;
});

// conditionally provides a title for properties on the results page if there are multiple properties
Handlebars.registerHelper('propertyResultTitle', function (index, propertiesObj) {

    const totalProperties =  propertiesObj.properties.length;

    const title = `<h3 class="brc-result__title">Property ${totalProperties - index}</h3>`;

    if (totalProperties > 1) {
        const button = `<a href="#" class="remove-property  ds_button  ds_button--cancel  ds_button--small  ds_button--has-icon" data-property-index="${index}">Remove property<svg class="ds_icon" aria-hidden="true" role="img"><use xlink:href="/assets/images/icons/icons.stack.svg#close-21"></use></svg></a>`;

        return title + button;
    } else {
        return title;
    }


});

/*
    Matches the format of a postcode. Permits:
    full postcode e.g. AB12 3CD
    partial e.g. AB12 3, AB12
    */
const postcodeRegExp = new RegExp('^([A-PR-UWYZ0-9][A-HK-Y0-9][AEHMNPRTVXY0-9]?[ABEHMNPRVWXY0-9]? {0,2}[0-9][ABD-HJLN-UW-Z]{2}|GIR ?0AA)$');

const scottishPostcodeRegExp = new RegExp('^(AB|DD|DG|EH|FK|G|HS|IV|KA|KW|KY|ML|PA|PH|TD|ZE)[0-9]{1,2} {0,2}[0-9][ABD-HJLN-UW-Z]{2}$');

function toggleAttribute(el, attributeName) {
    if (el.attr(attributeName) === 'true') {
        el.attr(attributeName, 'false');
    } else {
        el.attr(attributeName, 'true');
    }
}

const businessRatesCalculator = {

    // sets up the calculator
    init: function (today = new Date()) {

        feedback.init();

        const that = this;

        this.bindEvents();
        this.initWizard();
        this.treatDisclaimer();

        $('#address-form').hide();

        this.selectedProperties = [];

        // taken from provided spreadsheet
        this.ratesCalculatorData = {
            'sbbs_100_rv_threshold': 15000,
            'sbbs_100_percentage_relief': 1,
            'sbbs_25_rv_threshold': 18000,
            'sbbs_25_percentage_relief': 0.25,
            'poundage': 0.49,
            'large_business_supplement_threshold': 51000,
            'large_business_supplement': 0.026,
            'financial_year': '2019-2020',
            'sbbs_combined_threshold': 35000,
            'universalRelief': 0.016
        };

        const newFiscalYeardate = new Date(2020, 3, 1);
        if (today > newFiscalYeardate) {
            this.ratesCalculatorData.financial_year = '2020-2021';
            this.ratesCalculatorData.poundage = 0.498;
            this.ratesCalculatorData.intermediate_business_supplement_threshold = 51000;
            this.ratesCalculatorData.intermediate_business_supplement = 0.013;
            this.ratesCalculatorData.large_business_supplement_threshold = 95000;
            this.ratesCalculatorData.large_business_supplement = 0.026;
        }

        // adjust this to use different value types, e.dg. current rateable value or proposed rateable value
        this.rateableValueType = 'rv';

        this.apiUrl = '/address/?search=';

        $('.more-info, a[href="#disclaimer"]').on('click', function (e) {
            e.preventDefault();
            const target = $($(this).attr('href'));

            toggleAttribute(target, 'aria-expanded');
            target.slideToggle();
        });

        $('.business-rates-calculator').on('click', '.remove-property', function (e) {
            e.preventDefault();

            that.removePropertyFromResults(e.currentTarget.getAttribute('data-property-index'));
        });
    },

    // Binding after page initialisation is useful.  Format is ACTION SELECTOR: FUNCTION [DATA]
    events: {
        'submit form#postcode-form': 'findByPostCode',
        'submit form#address-form': 'findByAddress',
        'click #dont-know-postcode': 'showAddress',
        'click a.reset': 'resetForms',
        'click p.clear-results a': 'clearData'
    },

    /**
     * Initialise the code which maintains the state of the "wizard" - that is, the internal
     * state-controller of this tool.
     */
    initWizard: function () {
        const businessRatesCalculator = this,

            wizard = {
                // navigates to a specified step and initialises it
                goToStep: function (stepId) {

                    $('#' + stepId).removeClass('fully-hidden').attr('aria-hidden', false)
                        .siblings('.wizard-step')
                        .addClass('fully-hidden').attr('aria-hidden', true);
                    location.hash = stepId;
                    wizard.currentStep = stepId;
                    wizard.initStep(stepId);
                },

                nextStep: function () {
                    const nextStepId = $('#' + wizard.currentStep).next('.wizard-step').attr('id');
                    if (nextStepId) {
                        wizard.goToStep(nextStepId);
                    } else {
                        throw 'no step to navigate to';
                    }
                },

                // not certain we need previous but it's here for symmetry
                previousStep: function () {
                    const previousStepId = $('#' + wizard.currentStep).prev('.wizard-step').attr('id');
                    if (previousStepId) {
                        wizard.goToStep(previousStepId);
                    } else {
                        throw 'no step to navigate to';
                    }
                },

                // perform some setup actions when a step is displayed
                initStep: function () {
                    window.scrollTo(0, 0);
                },

                // resets the calculator and navigates to the start
                restart: function () {
                    businessRatesCalculator.resetForms();
                    wizard.goToStep('introduction');
                },

                currentStep: 'introduction'
            };

        // initialize on step 0
        wizard.goToStep('introduction');

        // bind to hashchange for step changes
        window.onhashchange = function () {
            const hash = location.hash;
            const locationElement = $(hash);
            if (locationElement.length > 0 && locationElement.hasClass('wizard-step')) {
                const step = hash.substring(1);
                wizard.goToStep(step);
            }
        };

        this.wizard = wizard;
    },

    /**
     * Finds the disclaimer and wraps it
     */
    treatDisclaimer: function () {
        // this match is weak -- relies on Markdown title parsing to set the ID
        const disclaimerHeader = $('#disclaimer');

        if (disclaimerHeader.length > 0) {
            disclaimerHeader.removeAttr('id');

            const headerLevel = parseInt(disclaimerHeader[0].nodeName.replace(/h/i));
            disclaimerHeader.add(disclaimerHeader.nextUntil('h' + (headerLevel - 1))).wrapAll('<aside aria-expanded="false" class="block" id="disclaimer"></aside>');

            window.setTimeout(function () {
                $('#disclaimer').hide();
            }, 0);
        }
    },

    /**
     * Reset sections of the DOM when a search is performed.
     */
    cleanOnSearch: function () {
        // remove property list
        $('#property-list').remove();

        // remove validation message
        $('.error-text').remove();
        $('.form-error').removeClass('form-error');

        $('.error-block').remove();
    },


    /**
     * Reset all forms within this tool.
     */
    resetForms: function () {
        delete this.currentProperty;
        delete this.searchResults;

        const toUpdate = $('#property-address');
        toUpdate.find('input[type=text]').val('');
        toUpdate.find('input[type=radio]').prop('checked', false);
        toUpdate.find('select').children('option:selected').prop('selected', false);

        this.cleanOnSearch();

        // hide address inputs
        $('#address-form').hide();

        // go to first step
        this.wizard.goToStep('property-address');
    },

    /**
     * Clear all data and reset all forms
     */
    clearData: function () {
        // clear the selected properties
        this.selectedProperties = [];

        // now reset the forms
        this.resetForms();
    },

    /**
     * Uses the postcode field of the DOM to perform a search against the remote data,
     * then calls the function to populate a SELECT tag with property details.
     */
    findByPostCode: function () {

        const postcode = $('#postcode');
        const postcodeValue = postcode.val().toUpperCase().replace(/\s+/g, '');

        this.cleanOnSearch();

        // check whether it's a valid postcode
        if (postcodeValue.match(postcodeRegExp)) {
            // now check whether it's a Scottish postcode
            if (postcodeValue.match(scottishPostcodeRegExp)) {
                this.getProperties(postcodeValue);
            } else {
                this.formError(postcode.closest('.form-group'), 'You have entered a postcode associated with an address outside Scotland. This site can only be used to calculate Business Rates for Scottish companies. <a href="https://www.gov.uk/calculate-your-business-rates">Please visit gov.uk for more information</a>.');
            }
        } else {
            this.formError(postcode.closest('.form-group'), 'Please try searching again using a full business postcode.');
        }
    },

    /**
     * Uses the street-address and postal-town fields in the DOM to trigger a search against the SAA data.
     * This will cause an update of the DOM with the search results (or applicable error message).
     */
    findByAddress: function () {
        const address = $('#address');
        const town = $('#town');

        this.cleanOnSearch();

        if (town.val() === '' && address.val() === '') {
            town.add(address).find('.error-text').remove();
            town.add(address).closest('.form-group').addClass('form-error');

            $('<p class="error-text"></p>').appendTo(town.closest('.form-group')).text('Please enter a street or town.').prepend('<h3>Error</h3> ');
        } else {
            this.getProperties((address.val() + ' ' + town.val()).trim());
        }
    },

    // show address fields
    showAddress: function () {
        $('#address-form').show();
    },

    /**
     * Gets a list of buildings from the remote API for the SAA's data, then either calls the function to show a
     * SELECT tag listing those buildings or - if nothing is found - displays an error message via the DOM.
     * @param {String} searchTerm The content of a query-string for the search-function of the SAA API.
     */
    getProperties: function (searchTerm) {
        const businessRatesCalculator = this,
            submitButtons = $('#find-by-postcode').add('#find-by-address'),
            errorTemplateSource = templates.largeError,
            errorTemplate = Handlebars.compile(errorTemplateSource);

        // disable submissions
        submitButtons.prop('disabled', 'disabled');

        // show loader
        const elementsToHideWhileLoading = $('#property-list');
        const ajaxLoader = $('<div id="ajax-loader">Loading results</div>');

        elementsToHideWhileLoading.addClass('fully-hidden').attr('aria-hidden', true);
        ajaxLoader.insertAfter('#address-form');

        const errorMessage = {};
        errorMessage.inputId = 'postcode';

        $.ajax({
            type: 'GET',
            url: businessRatesCalculator.apiUrl + searchTerm
        })
            .done(function (result) {
                if (result.properties && result.properties.length) {
                    businessRatesCalculator.searchResults = result.properties;
                    businessRatesCalculator.showSearchResults();
                } else {
                    /*
                        We have successfully communicated with the server, but have no results.  (Currently, this
                        is shown by a 404 error page.)
                        */
                    if (document.getElementById(errorMessage.inputId)) {
                        document.getElementById(errorMessage.inputId).setAttribute('aria-describedby', `${errorMessage.inputId}-errors`);
                    }
                    $(errorTemplate({
                        title: 'Please narrow your search',
                        content: '<p>Your search returned either no results, or too many to show.  ' +
                            'Supplying both the street address and the town might narrow down your search enough ' +
                            'that you get a result.  Also, check the spelling of each part of the address.</p>' +
                            '<p>If you have entered a valid business location and your property can\'t be found, ' +
                            '<a id=\"unlisted\" '+
                            'href=\"https://www.saa.gov.uk/contact-us/feedback/\">' +
                            'please report this to the Scottish Assessors Association</a>.</p>'
                    })).appendTo('#property-address .form-box');
                }
            })
            .error(function (error) {
            // Cannot use .catch() as it's a reserved word in IE8. Use escaped version instead
                if (document.getElementById(errorMessage.inputId)) {
                    document.getElementById(errorMessage.inputId).setAttribute('aria-describedby', `${errorMessage.inputId}-errors`);
                }

                if (error.status === 403) {
                    errorMessage.title = 'Too many results';
                    errorMessage.content = '<p>Sorry, the information you entered returned too many properties. Please enter more of the address or postcode. Use the <a href="http://www.royalmail.com/find-a-postcode">Royal Mail website</a> to find the postcode.</p>';
                } else {
                    errorMessage.title = 'No results found';
                    errorMessage.content = '<p>Please check that the address or postcode you\'re searching for is valid and in Scotland. ' +
                        'The calculator uses address data from the Scottish Assessors Association (SAA).</p>' +
                        '<p>If you have entered a valid business location and your property can\'t be found, ' +
                        '<a id=\"unlisted\" '+
                        'href=\"https://www.saa.gov.uk/contact-us/feedback/\">' +
                        'please report this to the SAA</a>.</p>';
                }

                // The connection "promise" has failed: display error text
                $(errorTemplate(errorMessage))
                    .appendTo('#property-address .form-box');
            })
            .always(function () {
                // (re-)enable submissions
                submitButtons.prop('disabled', '');

                // remove "loading" spinner
                ajaxLoader.remove();
                elementsToHideWhileLoading.removeClass('fully-hidden').attr('aria-hidden', false);
            });
    },

    // show a list of matching properties for the search
    showSearchResults: function () {
        const resultsData = {
            searchResults: this.searchResults,
            showAsSelect: true
        };

        const propertyTemplate = Handlebars.compile(templates.propertySelect);

        if (resultsData.searchResults.length === 1) {
            // show a single property if there is only one result
            resultsData.showAsSelect = false;
        }

        $(propertyTemplate(resultsData)).appendTo($('#property-address').find('.form-box'));

        // preselect the first item if we have one property result
        if(this.searchResults.length === 1) {
            // select the first property
            $('#property-list').find('input[type=radio]').prop('checked', true);
        }

        $('body').on('submit', '#property-list', function (event) {
            event.preventDefault();

            businessRatesCalculator.setCurrentProperty();

            // go to next step
            businessRatesCalculator.updateOrAddProperty();

            businessRatesCalculator.calculateResults();
            businessRatesCalculator.wizard.goToStep('results');
        });
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
            selectedProperty = $('#property-list').find('option:selected');
            this.currentProperty = this.searchResults[selectedProperty.val()];
        }
    },

    /**
     * Check whether currentProperty already exists in selectedProperties.
     * If it already exists, update the property data instead of adding a new item to the list.
     */
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
            this.selectedProperties.unshift(this.currentProperty);
        }
    },

    // calculate rate liabilities for all properties
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

            // on top of any other reliefs, add the coronavirus special
            property.universalRelief = {
                name: 'Universal 1.6% relief',
                fraction: 0.016,
                amount: property.baseLiability * this.ratesCalculatorData.universalRelief
            };

            property.netLiability = Math.max(property.netLiability - property.universalRelief.amount.toFixed(2), 0);
        }

        this.showResults();
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

    /**
     * Updates the DOM with the latest results of rate calculations.
     */
    showResults: function () {
        const resultsTemplateSource = templates.resultsTemplate,
            resultsTemplate = Handlebars.compile(resultsTemplateSource),
            resultContainer = $('#property-results');

        // remove old results
        resultContainer.find('.mg_leg_result').remove();

        // render new results
        $(resultsTemplate({
            properties: this.selectedProperties
        })).prependTo(resultContainer);
    },

    removePropertyFromResults: function(propertyIndex) {
        // remove property from selectedProperties array
        this.selectedProperties.splice(propertyIndex, 1);

        // recalculate & show results
        this.calculateResults();
    },

    /**
     * Binds events from the 'events' list for the BRC to code.
     */
    bindEvents: function () {
        const businessRatesCalculator = this;

        $.each(this.events, function (key, value) {
            const action = key.split(' ')[0],
                selector = key.split(' ')[1],
                businessRatesCalculatorFunction = value.split(' ')[0],
                data = value.split(' ')[1];

            $('body').on(action, selector, function (event) {
                event.preventDefault();
                businessRatesCalculator[businessRatesCalculatorFunction](data);
            });
        });
    },

    // validation messages
    formError: function (el, message) {
        el.find('.error-text').remove();
        el.addClass('form-error');

        const inputElement = el.find('input, textarea');

        const errorBlock = $(`<div class="error-block">
            <h3 class="error-text">Error</h3>
            <p>${message}</p>
            </div>`);

        errorBlock.appendTo(el);

        if (inputElement.length) {
            inputElement.attr('aria-describedby', `${inputElement[0].id}-errors`);
            errorBlock.find('p')[0].id = `${inputElement[0].id}-errors`;
        }
    }
};

window.format = businessRatesCalculator;
window.format.init();

export default businessRatesCalculator;
