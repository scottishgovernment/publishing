// RENT INCREASE FORM

/* global require grecaptcha expireRecaptcha */

'use strict';

const formObject = {
    tenancyStartDate: null,
    lastIncreaseDate: null,
    currentRentAmount: null,
    currentRentFrequency: null,
    newRentAmount: null,
    newRentFrequency: null,
    givingThisNotice: null,
    improvementsQuery: null,
    improvementsIncrease: null,
    dateOfIncrease: null,
    tenantNames: { 'tenant1': null },
    address: null,
    landlords: { 'landlord-1': {} },
    lettingAgent: {
        name: null,
        email: null,
        telephone: null,
        address: {
            building: null,
            street: null,
            town: null,
            region: null,
            postcode: null
        }
    }
};

const formMapping = {
    'tenancyStartDate': '#tenancy-start-date',
    'firstTimeIncrease': '[name="first-time-query"]',
    'lastIncreaseDate': '#last-increase-date',
    'currentRentAmount': '#current-rent-amount',
    'currentRentFrequency': '#current-payment-frequency',
    'newRentAmount': '#new-rent-amount',
    'newRentFrequency': '#new-payment-frequency',
    'givingThisNotice': '[name="giving-notice-query"]',
    'address': '#property-address-property-address',
    'address.building': '#property-address-address-building',
    'address.street': '#property-address-address-street',
    'address.town': '#property-address-address-town',
    'address.region': '#property-address-address-region',
    'address.postcode': '#property-address-postcode',
    'improvementsIncrease': '#improvements-increase',
    'improvementsQuery': '[name="improvements-query"]',
    'dateOfIncrease': '#rent-increase-date',
    'hasLettingAgent': '[name="letting-agent-query"]',
    'lettingAgent.name': '#letting-agent-name',
    'lettingAgent.email': '#letting-agent-email',
    'lettingAgent.telephone': '#letting-agent-phone',
    'lettingAgent.address.building': '#letting-agent-address-building',
    'lettingAgent.address.street': '#letting-agent-address-street',
    'lettingAgent.address.town': '#letting-agent-address-town',
    'lettingAgent.address.region': '#letting-agent-address-region',
    'lettingAgent.address.postcode': '#letting-agent-postcode',
    'tenantNames.tenant1': '#tenant-1-name'
};

import feedback from '../../components/feedback';
import MultiPageForm from '../../components/multi-page-form';
import PostcodeLookup from '../../components/postcode-lookup';
import commonForms from '../../tools/forms';
import commonHousing from '../../tools/housing';
import formSections from '../../components/mygov/housing-forms/rent-increase-sections';
import DSDatePicker from '../../../../../node_modules/@scottish-government/design-system/src/components/date-picker/date-picker';
import bloomreachWebfile from '../../tools/bloomreach-webfile';

const formTemplate = require('../../templates/mygov/rent-increase-form');
const summaryTemplate = require('../../templates/mygov/rent-increase-summary');
const housingFormPageNavTemplate = require('../../templates/housing-form-pagenav');
const dateOfIncreaseTemplate = require('../../templates/mygov/rent-increase-date-of-increase');
const sectionNavTemplate = require('../../templates/visited-only-section-nav');
const subNavTemplate = require('../../templates/visited-only-subsection-nav');

[].slice.call(document.querySelectorAll('form')).forEach((form) => form.reset());

let NOW = new Date();
NOW.setHours(0,0,0,0);

const costOfLivingLegislationEndDate = new Date(2024, 3, 1);

// date override from querystring
const qsParams = new URLSearchParams(window.location.search);
if (qsParams.get('date')) {
    const date = new Date(`${qsParams.get('date').substring(0,4)}-${qsParams.get('date').substring(4,6)}-${qsParams.get('date').substring(6,8)}`);
    if (!isNaN(date.getTime())) {
        NOW = date;
    }
}

const adjustDate = function (date, adjustment, capMonth) {
    if (!adjustment) {
        return date;
    }

    const newDate = new Date(date.getTime());

    if (adjustment.years) {
        newDate.setFullYear(date.getFullYear() + adjustment.years);
    }

    if (adjustment.months) {
        newDate.setMonth(date.getMonth() + adjustment.months);

        // capMonth: true will prevent the date being set beyond the end of a month
        // (e.g. 31 Jan -> 28 Feb instead of 31 Jan -> 3 Mar)
        if (capMonth && newDate.getMonth() > (12 + date.getMonth() + adjustment.months) % 12) {
            newDate.setDate(0);
        }
    }

    if (adjustment.days) {
        newDate.setDate(newDate.getDate() + adjustment.days);
    }

    return newDate;
};

window.adjustDate = adjustDate;

const resetHours = function (date) {
    date.setHours(0, 0, 0, 0);
    return date;
};

const rentIncreaseForm = {
    rpzData: {},

    form: new MultiPageForm({
        formSections: formSections,
        formMapping: formMapping,
        formObject: formObject,
        formEvents: {
            updateSummary: function () {
                const summaryContainer = document.querySelector('#summary-container');
                const summaryObject = JSON.parse(JSON.stringify(rentIncreaseForm.form.settings.formObject));
                summaryObject.hasLandlords = Object.values(summaryObject.landlords)[0].name;
                const html = summaryTemplate.render(summaryObject);

                summaryContainer.innerHTML = html;
                commonHousing.summaryAccordion(summaryContainer);
                window.DS.tracking.init(summaryContainer);
            },
            updateDateOfIncrease: function () {
                rentIncreaseForm.setupDateOfIncrease();
            },
            setupIncreaseAmountPage: function () {
                rentIncreaseForm.setupIncreaseAmountPage();
            }
        },
        modifiers: [{
            pattern: new RegExp(/\.postcode$/),
            transformFunction: function () {
                return arguments[0].toUpperCase();
            }
        }],
        pageNavFunction: function () {return commonForms.pageNavFunction('overview', rentIncreaseForm.form.currentStep);},
        pageNavTemplate: housingFormPageNavTemplate,
        sectionTemplate: sectionNavTemplate,
        subsectionTemplate: subNavTemplate
    }),

    init: function () {
        feedback.init();
        rentIncreaseForm.form.validateStep = rentIncreaseForm.validateStep;

        // append form template
        const formTemplateContainer = document.querySelector('#form-container');
        if (!formTemplateContainer) {
            return false;
        }
        const overviewContent = formTemplateContainer.innerHTML;
        this.recaptchaSitekey = document.getElementById('recaptchaSitekey').value;
        this.recaptchaEnabled = document.getElementById('recaptchaEnabled').value === 'true';
        formTemplateContainer.innerHTML = formTemplate.render({
            iconsFile: bloomreachWebfile('/assets/images/icons/icons.stack.svg'),
            webfilesPath: bloomreachWebfile(),
            assetsPath: document.getElementById('site-root-path').value.replace('mygov', '') + 'assets',
            recaptchaEnabled: this.recaptchaEnabled,
            recaptchaSitekey: this.recaptchaSitekey
        });
        formTemplateContainer.querySelector('#overview').innerHTML = overviewContent;

        commonForms.appendCaptchaScript();

        rentIncreaseForm.form.init();
        commonHousing.setManualLinkSections();
        this.setupRepeatingSections();
        this.setupAddTenantNames();
        this.setupValidations();
        this.setupDatePickers();
        this.setupPostcodeLookups();
        this.setupCustomFieldEvents();
        this.setupBackToSummary();
        if (this.recaptchaEnabled) {
            commonForms.setupRecaptcha();
        }
        this.setupProgressResets();

        if (NOW < costOfLivingLegislationEndDate) {
            this.transformMarkupCostOfLiving();
            this.useCostOfLivingLegislation();
        }
    },

    transformMarkupCostOfLiving: function () {
        // transform page content
        const newRentScreen = document.querySelector('[data-step="new-rent"]');
        const newRentFrequencyQuestion = newRentScreen.querySelector('#new-rent-frequency-question');
        newRentFrequencyQuestion.parentNode.removeChild(newRentFrequencyQuestion);

        const newRentAmountInput = newRentScreen.querySelector('#new-rent-amount');
        const frequencyReadOnly = document.createElement('span');
        frequencyReadOnly.id = 'frequency-read-only';
        newRentAmountInput.parentNode.append(frequencyReadOnly);
        newRentAmountInput.style.margin = '0 8px 0 0';
        newRentAmountInput.style.display = 'inline-block';

        const newRentWarningText = document.createElement('div');
        newRentWarningText.classList.add('ds_warning-text');
        newRentWarningText.innerHTML = `
            <strong class="ds_warning-text__icon" aria-hidden="true"></strong>
            <strong class="visually-hidden">Warning</strong>
            <div class="ds_warning-text__text">
                <h3>Temporary rent cap</h3>
                <p>There is a temporary cap on rent increases during tenancies. The cap is set at 3%.</p>
                <p>The process is different if you want to increase the rent by up to 6% because there has been an <a href="https://www.mygov.scot/rent-cap-landlords/when-you-can-increase-the-rent">increase in certain costs</a>.</p>
                <p>You can find out more by contacting <a href="https://www.gov.scot/publications/about-rent-service-scotland/">Rent Service Scotland</a>.</p>
            </div>
        `;

        newRentScreen.append(newRentWarningText);
    },

    useCostOfLivingLegislation: function () {
        // new validation method
        commonForms.validateRentIncreaseAmount = function (field, percentage = 3) {
            let newValue = field.value;
            let valid = true;
            let fieldName = commonForms.getLabelText(field);
            let message;
            let oldAmount = formObject.currentRentAmount;

            const maxPermittedValue = oldAmount * (1 + (percentage * 0.01));

            if (newValue > maxPermittedValue) {
                valid = false;
                if (field.dataset.message) {
                    message = field.dataset.message;
                } else {
                    message = `You cannot increase the rent by more than ${percentage}%`;
                }
            }

            commonForms.toggleFormErrors(field, valid, 'invalid--rent-increase', fieldName, message);
            commonForms.toggleCurrentErrors(field, valid, 'invalid-rent-increase', message);

            return valid;
        };
    },

    setupBackToSummary: function () {
        rentIncreaseForm.form.setupBackToSummary();
    },

    setupRepeatingSections: function () {
        const sections = [
            {
                name: 'landlords',
                nameInput: '.js-landlord-name',
                container: '.js-landlords-container',
                template: require('../../templates/landlord-html'),
                addressrequired: true,
                slug: 'landlord',
                stepTitle: 'Landlord',
                requiredFields: ['requiredName', 'requiredAddress'],
                suppressedFields: ['registrationNumber', 'email', 'phone'],
                fieldMappings: function (number) {
                    const fieldMappings = {};

                    fieldMappings['landlords[\'landlord-' + number + '\'].name'] = `#landlord-${number}-name`;
                    fieldMappings['landlords[\'landlord-' + number + '\'].email'] = `#landlord-${number}-email`;
                    fieldMappings['landlords[\'landlord-' + number + '\'].telephone'] = `#landlord-${number}-phone`;
                    fieldMappings['landlords[\'landlord-' + number + '\'].registrationNumber'] = `#landlord-${number}-registration`;
                    fieldMappings[`landlords[landlord-${number}].address`] = new PostcodeLookup(document.getElementById(`landlord-${number}-postcode-lookup`));
                    fieldMappings['landlords[\'landlord-' + number + '\'].address.building'] = `#landlord-${number}-address-building`;
                    fieldMappings['landlords[\'landlord-' + number + '\'].address.street'] = `#landlord-${number}-address-street`;
                    fieldMappings['landlords[\'landlord-' + number + '\'].address.town'] = `#landlord-${number}-address-town`;
                    fieldMappings['landlords[\'landlord-' + number + '\'].address.region'] = `#landlord-${number}-address-region`;
                    fieldMappings['landlords[\'landlord-' + number + '\'].address.postcode'] = `#landlord-${number}-postcode`;

                    return fieldMappings;
                }
            }
        ];

        commonHousing.setupRepeatingSections(sections, this.form);
    },

    setupAddTenantNames: function () {
        const addTenantButton = document.querySelector('.js-add-tenant');
        if (addTenantButton) {
            addTenantButton.addEventListener('click', event => {
                event.preventDefault();

                const tenantNames = rentIncreaseForm.form.settings.formObject.tenantNames;
                const currentNumberOfTenants = commonForms.objectKeys(tenantNames).length;
                const newNumber = currentNumberOfTenants + 1;

                const tenantNamesContainer = document.querySelector('.js-tenant-names-container');
                const newQuestion = document.createElement('div');

                newQuestion.classList.add('ds_question');
                newQuestion.innerHTML = `<label class="ds_label" for="tenant-${newNumber}-name">Tenant ${newNumber}: Full name</label>
                <input type="text" id="tenant-${newNumber}-name" class="ds_input">`;

                tenantNamesContainer.appendChild(newQuestion);

                rentIncreaseForm.form.mapField(`tenantNames.tenant${newNumber}`, `#tenant-${newNumber}-name`);
                tenantNames[`tenant${newNumber}`] = null;
                window.DS.tracking.init(newQuestion);
            });
        }
    },

    setupDatePickers: function () {
        const tenancyStartDatePicker = new DSDatePicker(document.getElementById('tenancy-start-date-picker'), {maxDate: NOW, imagePath: bloomreachWebfile('/assets/images/icons/')});
        const lastIncreaseDatePicker = new DSDatePicker(document.getElementById('last-increase-date-picker'), {maxDate: NOW, imagePath: bloomreachWebfile('/assets/images/icons/')});

        tenancyStartDatePicker.init();
        lastIncreaseDatePicker.init();
    },

    setupValidations: function () {
        // the "other" radio option needs to enable/disable validation on its associated text input
        $('[name="property-building"]').on('change', function () {
            if ($('[name="property-building"]:checked').val() === 'OTHER') {
                $('#building-other').attr('data-validation', 'requiredField')
                    .parent().removeClass('fully-hidden');
            } else {
                $('#building-other').removeAttr('data-validation aria-invalid')
                    .removeClass('ds_input--error')
                    .next('.current-errors')
                    .remove();
                $('#building-other').parent().addClass('fully-hidden');
            }
        });

        // On click of error playback link, scroll to above field so label is visible
        $('body').on('click', '.client-error a', function (event) {
            const targetElement = document.querySelector(this.hash);
            const question = targetElement.closest('.ds_question');

            const elementToScrollTo = question || targetElement;
            if (elementToScrollTo) {
                elementToScrollTo.scrollIntoView();
                event.preventDefault();
            }
        });
    },

    rpzComplete: function (rpzData) {
        rentIncreaseForm.rpzData = rpzData;

        const isRPZ = rpzData.inRentPressureZone;
        rentIncreaseForm.form.settings.formObject.inRentPressureZone = isRPZ;

        // find the improvements page
        const formSections = rentIncreaseForm.form.settings.formSections;

        const increaseSection = formSections.find(section => section.slug === 'increase');
        const improvementsPage = increaseSection.pages.find(page => page.slug === 'improvements');

        // if we're in a RPZ show the step. otherwise, hide it.
        improvementsPage.hidden = !isRPZ;
        improvementsPage.disabled = !isRPZ;

        const reset = {
            propertiesToClear: [
                'form.settings.formObject.newRentAmount',
                'form.settings.formObject.improvementsIncrease'
            ],
            elementsToClear: ['#new-rent-amount', '#improvements-increase']
        };
        if (isRPZ) {
            reset.targetStep = 'improvements';
        } else {
            reset.targetStep = 'new-rent';
        }
        rentIncreaseForm.resetProgress(reset);

        rentIncreaseForm.form.updatePageNav();
    },

    setupPostcodeLookups: function () {
        const addressInfoNoteHtml = '<h4>Your property is in a Rent Pressure Zone (RPZ).</h4>' +
            '<p>You\'ll need to give us some details about your property so that we can check' +
            ' your increase is allowed within your RPZ.</p>';

        rentIncreaseForm.form.settings.formMapping['address'] = new PostcodeLookup(document.getElementById('property-address-postcode-lookup'), { readOnly: true, rpz: true, infoNoteHtml: addressInfoNoteHtml, rpzComplete: this.rpzComplete, storeResultAs: 'propertyAddressResult'});
        rentIncreaseForm.form.settings.formMapping['lettingAgent.address'] = new PostcodeLookup(document.getElementById('letting-agent-postcode-lookup'), { rpz: false, });
    },

    setupCustomFieldEvents: function () {
        // payment frequency -- prepopulate initial "current" frequency onto "new" frequency
        const currentFrequency = $('#current-payment-frequency');
        const newFrequency = $('#new-payment-frequency');

        $('#page-nav').on('click', '#button-next', function () {
            if (rentIncreaseForm.form.currentStep.slug === 'current-rent' &&
                !newFrequency.is('.js-dirty')
            ) {
                newFrequency.val(currentFrequency.val());
                newFrequency.trigger('change');
            }
        });

        newFrequency.on('change', function () {
            if (newFrequency.hasClass('js-dirty') && formObject.inRentPressureZone) {
                rentIncreaseForm.setMaximumRentIncrease();

                // and revalidate the input
                const newRentField = $('#new-rent-amount');

                const validations = newRentField.attr('data-validation').split(' ');
                const validationChecks = [];
                for (let i = 0, il = validations.length; i < il; i++) {
                    if (commonForms[validations[i]]) {
                        validationChecks.push(commonForms[validations[i]]);
                    }
                }

                // remove any extant maxvalue error
                $('[data-field="new-rent-amount"]').find('.invalid-max-value').remove();
                commonForms.validateInput(newRentField[0], validationChecks);
            }

            newFrequency.addClass('js-dirty');
        });

        const firstTimeQueryRadios = $('[name="first-time-query"]');

        firstTimeQueryRadios.on('change', function () {
            if (event.currentTarget.value === 'yes') {
                formObject.lastIncreaseDate = null;
            } else {
                formObject.lastIncreaseDate = $('#last-increase-date').val().length > 0 ? $('#last-increase-date').val() : null;
            }
        });
    },

    setupProgressResets: function () {
        const resets = [
            // start date
            {
                inputSelectors: [
                    '#tenancy-start-date',
                    '#last-increase-date',
                    '[name="first-time-query"]'
                ],
                targetStep: 'date-of-increase',
                propertiesToClear: [
                    'form.settings.formObject.dateOfIncrease',
                    'notificationDate'
                ],
                elementsToClear: ['#rent-increase-date']
            },

            // current rent
            {
                inputSelectors: ['#current-rent-amount', '#current-rent-frequency'],
                targetStep: 'new-rent',
                propertiesToClear: [],
                elementsToClear: []
            },

            // giving notice
            {
                inputSelectors: ['[name="giving-notice-query"]'],
                targetStep: 'date-of-increase',
                propertiesToClear: [],
                elementsToClear: [],
                eventToTrigger: rentIncreaseForm.setupDateOfIncrease
            }
        ];

        resets.forEach(function (reset) {
            $(reset.inputSelectors.join(',')).on('change', function () {
                rentIncreaseForm.resetProgress(reset);
            });
        });
    },

    resetProgress: function (reset) {
        $(reset.elementsToClear.join(',')).val('').trigger('change');

        if (reset.eventToTrigger) {
            reset.eventToTrigger();
        }

        rentIncreaseForm.form.resetProgressToStep(reset.targetStep);
    },

    setupDateOfIncrease: function () {
        const lastIncreaseDateAsString = rentIncreaseForm.form.settings.formObject.lastIncreaseDate || '01/01/2000';

        // set earliest date for next increase as 12 months from the last date of increase
        let earliestDateForNextIncrease = adjustDate(resetHours(commonForms.stringToDate(lastIncreaseDateAsString)), {months: 12}, true);

        // earliest date of increase has to be at least three months (+ optional two days) away
        // the notice period ends the day before the increase
        const dateAdjustment = {months: 3, days: 1};
        if (rentIncreaseForm.form.settings.formObject.givingThisNotice !== 'hand') {
            dateAdjustment.days = dateAdjustment.days + 2;
        }
        earliestDateForNextIncrease = new Date(Math.max(adjustDate(NOW, dateAdjustment, true).getTime(), earliestDateForNextIncrease.getTime()));

        // template needs the following info:
        const templateData = {};
        templateData.canIncreaseRentNow = NOW >= resetHours(adjustDate(commonForms.stringToDate(lastIncreaseDateAsString), {months: 12}, true));
        templateData.isByHand = rentIncreaseForm.form.settings.formObject.givingThisNotice === 'hand';
        templateData.deliveryMethod = rentIncreaseForm.form.settings.formObject.givingThisNotice === 'post' ? 'recorded delivery post' : rentIncreaseForm.form.settings.formObject.givingThisNotice;
        templateData.earliestDateForNextIncrease = commonForms.dateToString(earliestDateForNextIncrease);
        templateData.afterDate = commonForms.dateToString(adjustDate(earliestDateForNextIncrease, {days: -1}));
        templateData.initialValue = rentIncreaseForm.form.settings.formObject.dateOfIncrease;

        const html = dateOfIncreaseTemplate.render(templateData);
        document.querySelector('#date-of-increase-content-container').innerHTML = html;

        const rentIncreaseDatePicker = new DSDatePicker(document.getElementById('rent-increase-date-picker'), {minDate: earliestDateForNextIncrease, imagePath: bloomreachWebfile('/assets/images/icons/')});
        rentIncreaseDatePicker.init();

        // validate this date field
        const rentIncreaseDate = document.getElementById('rent-increase-date');
        if (rentIncreaseDate.value) {
            let validations = rentIncreaseDate.getAttribute('data-validation').split(' ');
            const validationChecks = validations.map(validation => commonForms[validation]);
            commonForms.validateInput(rentIncreaseDate, validationChecks);
        }

        if (rentIncreaseForm.notificationDate && rentIncreaseForm.notificationDate !== '') {
            rentIncreaseForm.addNotificationDate();
        }

        // bind change event
        rentIncreaseForm.form.mapField('dateOfIncrease', '#rent-increase-date');

        document.getElementById('rent-increase-date').addEventListener('change', function () {
            if (this.value.trim() === '' || !this.value.trim().match(/\d\d\/\d\d\/\d\d\d\d/)) {
                document.getElementById('date-increase-notification-alert').innerHTML = '';
                return;
            }

            let validations = rentIncreaseDate.getAttribute('data-validation').split(' ');
            const validationChecks = validations.map(validation => commonForms[validation]);
            if (commonForms.validateInput(rentIncreaseDate, validationChecks)) {
                window.setTimeout(function () {
                    rentIncreaseForm.addNotificationDate();
                }, 0);
            }
        });
    },

    addNotificationDate: function () {
        let notificationDate = commonForms.stringToDate(rentIncreaseForm.form.settings.formObject.dateOfIncrease);

        notificationDate = adjustDate(notificationDate, {months: -3}, true);
        //bring forward two days if we're not giving this notice by hand
        if (rentIncreaseForm.form.settings.formObject.givingThisNotice !== 'hand') {
            notificationDate.setDate(notificationDate.getDate() - 2);
        }
        notificationDate.setDate(notificationDate.getDate() - 1);

        rentIncreaseForm.notificationDate = notificationDate;

        if (rentIncreaseForm.form.settings.formObject.notificationSendDate) {
            rentIncreaseForm.notificationSendDate = commonForms.dateToString(new Date (Math.min(commonForms.stringToDate(rentIncreaseForm.form.settings.formObject.notificationSendDate), rentIncreaseForm.notificationDate)));
            if (commonForms.stringToDate(rentIncreaseForm.notificationSendDate) < NOW) {
                rentIncreaseForm.notificationSendDate = commonForms.dateToString(NOW);
            }
        } else {
            rentIncreaseForm.notificationSendDate = '';
        }

        rentIncreaseForm.form.settings.formObject.notificationSendDate = rentIncreaseForm.notificationSendDate;

        const templateData = {};
        templateData.today = commonForms.dateToString(NOW);
        templateData.notificationDate = commonForms.dateToString(rentIncreaseForm.notificationDate);

        if (rentIncreaseForm.form.settings.formObject.givingThisNotice === 'hand') {
            templateData.notificationDateText = `This means you must give your tenant this notice no later than ${templateData.notificationDate}. That  gives them 3 months' notice of the day you intend to increase their rent.`;
        } else {
            templateData.notificationDateText = `This means that you must send your tenant this notice no later than ${templateData.notificationDate}. That  gives them 3 months' notice of the date the rent will increase, plus 2 days to get this notice.`;
        }

        let html = `
        <div class="ds_inset-text">
            <div class="ds_inset-text__text">${templateData.notificationDateText}</div>
        </div>
        <div class="date-entry  ds_question">
            <label class="ds_label" for="rent-increase-send-date">When will you send this notice?</label>
            <div class="ds_hint-text">
                <p>Use DD/MM/YYYY format.</p>
            </div>
            <div id="rent-increase-send-date-picker" data-module="ds-datepicker" class="ds_datepicker">
                <div class="ds_input__wrapper">
                    <input data-mindate="${templateData.today}" data-maxDate="${templateData.notificationDate}" class="ds_input  ds_input--fixed-10  js-end-date-input" data-validation="dateRegex beforeDate afterDate requiredField" type="text" id="rent-increase-send-date" placeholder="e.g. ${templateData.today}" value="${rentIncreaseForm.notificationSendDate}">
                </div>
            </div>`;

        if (!isNaN(rentIncreaseForm.notificationDate.getTime())){
            document.getElementById('date-increase-notification-alert').innerHTML = html;
            const rentIncreaseSendDatePicker = new DSDatePicker(document.getElementById('rent-increase-send-date-picker'), {minDate: NOW, maxDate: rentIncreaseForm.notificationDate, imagePath: bloomreachWebfile('/assets/images/icons/')});
            rentIncreaseSendDatePicker.init();

            // bind change event
            rentIncreaseForm.form.mapField('notificationSendDate', '#rent-increase-send-date');
        } else {
            // clear the info note if date is not valid
            document.getElementById('date-increase-notification-alert').innerHTML = '';
        }
    },

    validateStep: function () {
        return commonForms.validateStep(rentIncreaseForm.form.currentStep);
    },

    prepareFormDataForPost: function (formData) {
        // there are some things we need to address before we post the data
        // Format tenants and landlords into arrays
        const formDataForPost = {};
        formDataForPost.tenantNames = commonForms.objectValues(formData.tenantNames);
        formDataForPost.landlords = commonForms.objectValues(formData.landlords);
        formDataForPost.inRentPressureZone = formData.inRentPressureZone;
        formDataForPost.landlordsAgent = formData.lettingAgent;
        formDataForPost.oldRentAmount = formData.currentRentAmount;
        formDataForPost.oldRentPeriod = formData.currentRentFrequency;
        formDataForPost.newRentAmount = formData.newRentAmount;
        formDataForPost.newRentPeriod = formData.newRentFrequency;
        formDataForPost.rentIncreaseDate = formData.dateOfIncrease;

        const notificationDate = rentIncreaseForm.notificationDate.getFullYear()
                                + '-' + commonForms.leadingZeroes(rentIncreaseForm.notificationDate.getMonth() + 1, 2)
                                + '-' + commonForms.leadingZeroes(rentIncreaseForm.notificationDate.getDate(), 2);

        let notificationSendDate = commonForms.stringToDate(rentIncreaseForm.form.settings.formObject.notificationSendDate);
        notificationSendDate = notificationSendDate.getFullYear()
                                + '-' + commonForms.leadingZeroes(notificationSendDate.getMonth() + 1, 2)
                                + '-' + commonForms.leadingZeroes(notificationSendDate.getDate(), 2);

        formDataForPost.notificationDate = notificationDate;
        formDataForPost.notificationSendDate = notificationSendDate;
        formDataForPost.lastRentIncreaseDate = formObject.lastIncreaseDate;
        formDataForPost.capFromDate = rentIncreaseForm.rpzData.dateFrom;
        formDataForPost.capToDate = rentIncreaseForm.rpzData.dateTo;
        if (rentIncreaseForm.rpzData.inRentPressureZone) {
            formDataForPost.calculation = {
                x: rentIncreaseForm.rpzData.maxIncrease,
                y: formData.improvementsIncrease,
                cpi: rentIncreaseForm.cpiDelta.toString()
            };
        }

        let propertyAddress = rentIncreaseForm.form.settings.formMapping.address.getAddressAsObject();
        let org = propertyAddress.org;
        let building = propertyAddress.building;
        let locality = propertyAddress.locality;
        let town = propertyAddress.town;

        if (locality) {
            town = `${locality}, ${town}`;
        }
        if (org && building) {
            building = `${org}, ${building}`;
        } else if (org) {
            building = org;
        }

        formDataForPost.address = {
            building: building,
            street: propertyAddress.street,
            town: town,
            postcode: propertyAddress.postcode
        };

        // Format dates to YYYY-MM-DD
        const dateFields = ['lastRentIncreaseDate', 'rentIncreaseDate'];

        for (let j = 0; j < dateFields.length; j++) {
            const field = dateFields[j];
            const value = formDataForPost[field];
            if (value === null || value === '' || value.split(' ').join('') === ''){
                continue;
            }

            formDataForPost[field] = commonForms.dateStringToMachineDate(value);
        }

        return commonForms.trimObjectValues(formDataForPost);
    },

    setupIncreaseAmountPage: function () {
        if (NOW < costOfLivingLegislationEndDate) {
            const frequencyReadOnly = document.getElementById('frequency-read-only');
            frequencyReadOnly.innerText = document.querySelector(`#current-payment-frequency option[value="${formObject.currentRentFrequency}"]`).innerText;
        } else {
            const lastIncreaseDate = formObject.lastIncreaseDate || formObject.tenancyStartDate;
            const isRPZ = formObject.inRentPressureZone;
            rentIncreaseForm.storedLastIncreaseDate = lastIncreaseDate;

            const increaseSection = formSections.find(section => section.slug === 'increase');
            const newRentPage = increaseSection.pages.find(page => page.slug === 'new-rent');
            if ($('#new-rent-amount').attr('data-maxvalue')) {
                $('#new-rent-amount').removeAttr('data-maxvalue');
                commonForms.validateStep(newRentPage);
            }

            if (lastIncreaseDate && isRPZ) {
                // reformat lastIncreaseDate and NOW into expected format
                const fromDate = [lastIncreaseDate.split('/')[2], lastIncreaseDate.split('/')[1], lastIncreaseDate.split('/')[0]].join('-');
                const toDate = NOW.getFullYear() + '-' + commonForms.leadingZeroes(NOW.getMonth() + 1, 2) + '-' + commonForms.leadingZeroes(NOW.getDate(), 2);
                $.get('/service/housing/cpi/cpi-delta',
                    {
                        from_date: fromDate,
                        to_date: toDate
                    }).done(function (data) {
                        rentIncreaseForm.cpiDelta = isNaN(data) ? 0 : data;

                        rentIncreaseForm.setMaximumRentIncrease();
                    });
            }
        }
    },

    getMaximumRentAmountNormalizedPerAnnum: function () {
        let currentRentNormalisedPerAnnum;
        let improvementsIncreaseNormalisedPerAnnum;
        let currentRentAmount = Number(rentIncreaseForm.form.settings.formObject.currentRentAmount);
        let currentRentFrequency = rentIncreaseForm.form.settings.formObject.currentRentFrequency;
        let improvementsIncrease = Number(rentIncreaseForm.form.settings.formObject.improvementsIncrease);
        let ministerAmount = rentIncreaseForm.rpzData.maxIncrease;

        if (currentRentFrequency === 'WEEKLY') {
            currentRentNormalisedPerAnnum          = currentRentAmount/7 * 365;
            improvementsIncreaseNormalisedPerAnnum = improvementsIncrease/7 * 365;
        } else if (currentRentFrequency === 'FORTNIGHTLY') {
            currentRentNormalisedPerAnnum          = currentRentAmount/14 * 365;
            improvementsIncreaseNormalisedPerAnnum = improvementsIncrease/14 * 365;
        } else if (currentRentFrequency === 'EVERY_FOUR_WEEKS') {
            currentRentNormalisedPerAnnum          = currentRentAmount/28 * 365;
            improvementsIncreaseNormalisedPerAnnum = improvementsIncrease/28 * 365;
        } else if (currentRentFrequency === 'CALENDAR_MONTH') {
            currentRentNormalisedPerAnnum          = currentRentAmount * 12;
            improvementsIncreaseNormalisedPerAnnum = improvementsIncrease * 12;
        } else if (currentRentFrequency === 'QUARTERLY') {
            currentRentNormalisedPerAnnum          = currentRentAmount * 4;
            improvementsIncreaseNormalisedPerAnnum = improvementsIncrease * 4;
        } else if (currentRentFrequency === 'EVERY_SIX_MONTHS') {
            currentRentNormalisedPerAnnum          = currentRentAmount * 2;
            improvementsIncreaseNormalisedPerAnnum = improvementsIncrease * 2;
        } else {
            // no period provided
            return null;
        }

        const maximumRentNormalizedPerAnnum = currentRentNormalisedPerAnnum * (1 + 0.01 * (rentIncreaseForm.cpiDelta + 1 + ministerAmount)) + improvementsIncreaseNormalisedPerAnnum;

        return maximumRentNormalizedPerAnnum.toFixed(2);
    },

    setMaximumRentIncrease: function () {
        const newRentFrequency = rentIncreaseForm.form.settings.formObject.newRentFrequency;
        const maximumRentNormalizedPerAnnum = Number(rentIncreaseForm.getMaximumRentAmountNormalizedPerAnnum());
        const maxRentAmount = rentIncreaseForm.getMaximumRentAmountForPeriod(newRentFrequency, maximumRentNormalizedPerAnnum);
        document.querySelector('#new-rent-amount').setAttribute('data-maxvalue', maxRentAmount);
    },

    getMaximumRentAmountForPeriod: function (period, maximumRentNormalizedPerAnnum) {
        let maximumRentPerPeriod;

        if (period === 'WEEKLY') {
            maximumRentPerPeriod = maximumRentNormalizedPerAnnum / 365 * 7;
        } else if (period === 'FORTNIGHTLY') {
            maximumRentPerPeriod = maximumRentNormalizedPerAnnum / 365 * 14;
        } else if (period === 'EVERY_FOUR_WEEKS') {
            maximumRentPerPeriod = maximumRentNormalizedPerAnnum / 365 * 28;
        } else if (period === 'CALENDAR_MONTH') {
            maximumRentPerPeriod = maximumRentNormalizedPerAnnum / 12;
        } else if (period === 'QUARTERLY') {
            maximumRentPerPeriod = maximumRentNormalizedPerAnnum / 4;
        } else if (period === 'EVERY_SIX_MONTHS') {
            maximumRentPerPeriod = maximumRentNormalizedPerAnnum / 2;
        } else {
            // no period provided
            return null;
        }

        return maximumRentPerPeriod.toFixed(2);
    }
};

$('.multi-page-form').on('click', '.js-download-file', function (event) {
    event.preventDefault();

    const documentDownloadForm = $('#document-download');
    documentDownloadForm.find('input[name="type"]').val($(this).closest('.js-document-container').attr('data-documenttype'));

    // make a copy of the form data to manipulate before posting
    const formData = JSON.parse(JSON.stringify(rentIncreaseForm.form.settings.formObject));
    const data = rentIncreaseForm.prepareFormDataForPost(formData);
    if (rentIncreaseForm.recaptchaEnabled) {
        data.recaptcha = grecaptcha.getResponse();
    }

    // Set hidden data field to have value of JSON data
    documentDownloadForm.find('input[name="data"]').val(encodeURIComponent(JSON.stringify(data)));
    documentDownloadForm.trigger('submit');
    if (rentIncreaseForm.recaptchaEnabled) {
        expireRecaptcha();
    }
});

window.format = rentIncreaseForm;
window.format.init();

export default rentIncreaseForm;
