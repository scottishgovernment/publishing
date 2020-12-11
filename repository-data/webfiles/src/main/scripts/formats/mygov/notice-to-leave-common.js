// NOTICE TO LEAVE FORM

/* global require grecaptcha expireRecaptcha */

'use strict';

const formObject = {
    tenantNames: {},
    address: {
        building: null,
        street: null,
        region: null,
        town: null,
        postcode: null
    },
    entryDate: null,
    landlords: {},
    landlordsAgent: {
        name: null,
        email: null,
        telephone: null,
        address: {
            building: null,
            street: null,
            town: null,
            region: null,
            postcode: null
        },
        registrationNumber: null
    },
    reasons: [],
    reasonDetails: null,
    supportingEvidence: null,
    earliestTribunalDate: null,
    hasLandlordsAgent: null,
    hasEndNoticeHelp: null
};

const formMapping = {
    'address.building': '#property-address-building',
    'address.street': '#property-address-street',
    'address.town': '#property-address-town',
    'address.region': '#property-address-region',
    'address.postcode': '#property-postcode',
    'landlordsAgent.name': '#letting-agent-name',
    'landlordsAgent.email': '#letting-agent-email',
    'landlordsAgent.telephone': '#letting-agent-phone',
    'landlordsAgent.address.building': '#letting-agent-address-building',
    'landlordsAgent.address.street': '#letting-agent-address-street',
    'landlordsAgent.address.town': '#letting-agent-address-town',
    'landlordsAgent.address.region': '#letting-agent-address-region',
    'landlordsAgent.address.postcode': '#letting-agent-postcode',
    'reasons': '[data-group="reasons"]',
    'reasonDetails': '#eviction-details',
    'supportingEvidence': '#supporting-evidence',
    'entryDate': '#tenancy-start-date',
    'entryDateWithHelp': '#tenancy-start-date-with-help',
    'noticePeriodEndDate': '#notice-period-end-date',
    'noticePeriodEndDateWithHelp': '#notice-period-end-date-with-help',

    'hasLandlordsAgent': '[name="letting-agent-query"]',
    'hasEndNoticeHelp': '[name="end-notice-help-query"]',
    'tenantNames.tenant1': '#tenant-1-name',
    'subtenantNames.subtenant1': '#subtenant-1-name'
};

import $ from 'jquery';
import feedback from '../../components/feedback';
import MultiPageForm from '../../components/multi-page-form';
import PostcodeLookup from '../../components/postcode-lookup';
import commonForms from '../../tools/forms';
import commonHousing from '../../tools/housing';
import formSections from '../../components/mygov/housing-forms/notice-to-leave-sections';
import DSDatePicker from '../../../../../node_modules/@scottish-government/pattern-library/src/components/date-picker/date-picker';

const formTemplate = require('../../templates/mygov/notice-to-leave-form');
const summaryTemplate = require('../../templates/mygov/notice-to-leave-summary');
const housingFormPageNavTemplate = require('../../templates/housing-form-pagenav');
const sectionNavTemplate = require('../../templates/visited-only-section-nav');
const subNavTemplate = require('../../templates/visited-only-subsection-nav');

[].slice.call(document.querySelectorAll('form')).forEach((form) => form.reset());

const noticeToLeaveForm = {
    form: new MultiPageForm({
        formSections: formSections,
        formMapping: formMapping,
        formObject: formObject,
        formEvents: {
            updateSummary: function () {
                const summaryObject = JSON.parse(JSON.stringify(noticeToLeaveForm.form.settings.formObject));

                summaryObject.tenantNames = commonForms.objectValues(summaryObject.tenantNames).filter(item => item);
                summaryObject.subtenantNames = commonForms.objectValues(summaryObject.subtenantNames).filter(item => item);
                summaryObject.hasLandlords = Object.values(summaryObject.landlords)[0].name;

                const html = summaryTemplate.render(summaryObject);
                document.querySelector('#summary-container').innerHTML = html;

                commonHousing.validateSummary();
            }
        },
        modifiers: [{
            pattern: new RegExp(/\.postcode$/),
            transformFunction: function () {
                return arguments[0].toUpperCase();
            }
        }],
        sectionTemplate: sectionNavTemplate,
        subsectionTemplate: subNavTemplate,
        pageNavFunction: function () {return commonForms.pageNavFunction('overview', noticeToLeaveForm.form.currentStep);},
        pageNavTemplate: housingFormPageNavTemplate
    }),

    init: function (formType) {
        // append form template
        const formTemplateContainer = document.querySelector('#form-container');
        if (!formTemplateContainer) {
            return false;
        }
        const overviewContent = formTemplateContainer.innerHTML;

        formTemplateContainer.innerHTML = formTemplate.render({tenants: true});
        formTemplateContainer.querySelector('#overview').innerHTML = overviewContent;

        this.formType = formType;
        noticeToLeaveForm.form.settings.formObject.formType = this.formType;

        let index;

        if (this.formType !== 'subtenant') {
            index = formSections.findIndex(function (section) {
                return section.slug === 'subtenants';
            });
        } else if (this.formType !== 'tenant') {
            index = formSections.findIndex(function (section) {
                return section.slug === 'tenants';
            });
        }

        if (index) {
            formSections.splice(index, 1);
        }

        noticeToLeaveForm.form.validateStep = noticeToLeaveForm.validateStep;
        noticeToLeaveForm.form.init();
        commonHousing.setManualLinkSections();
        this.setupConditionalSections();
        this.setupRepeatingSections();
        this.setupAddTenantNames();
        this.setupDatePickers();
        this.setupValidations();
        this.setupPostcodeLookups();
        this.setupBackToSummary();
        this.setupEndDateCalculation();
        commonForms.setupRecaptcha();
        feedback.init();
    },

    setupConditionalSections: function () {
        // commonHousing.setupConditionalSections();

        // disable landlords section if letting agent is selected
        $('#letting-agent-query').on('change', function(){
            const landlords = commonForms.objectKeys(noticeToLeaveForm.form.settings.formObject.landlords);

            if ($('#letting-agent-yes').prop('checked')) {
                for (let i = 0; i < landlords.length; i++) {
                    noticeToLeaveForm.form.disableStep(landlords[i]);
                }
                // if letting agent, disable validations on landlords fields
                $('section[data-step="landlords"] [data-validation]').addClass('no-validate');

                noticeToLeaveForm.form.updateFormNav();
            } else {
                for (let i = 0; i < landlords.length; i++) {
                    noticeToLeaveForm.form.enableStep(landlords[i]);
                }

                // if no letting agent, enable validations on landlords fields
                $('section[data-step="landlords"] [data-validation]').removeClass('no-validate');

                noticeToLeaveForm.form.updateFormNav();
            }
        });
    },

    setupDatePickers: function () {
        const tenancyStartDatePicker = new DSDatePicker(document.getElementById('tenancy-start-date-picker'), { maxDate: new Date() });
        const tenancyStartWithHelpDatePicker = new DSDatePicker(document.getElementById('tenancy-start-date-with-help-picker'), {maxDate: new Date()});
        const noticeDatePicker = new DSDatePicker(document.getElementById('notice-date-picker'), {minDate: new Date()});
        const tribunalDatePicker = new DSDatePicker(document.getElementById('notice-period-end-date-picker'), {minDate: new Date()});
        const tribunalWithHelpDatePicker = new DSDatePicker(document.getElementById('notice-period-end-date-with-help-picker'), {minDate: new Date()});

        tenancyStartDatePicker.init();
        tenancyStartWithHelpDatePicker.init();
        noticeDatePicker.init();
        tribunalDatePicker.init();
        tribunalWithHelpDatePicker.init();
    },

    setupEndDateCalculation: function () {
        let jsNoticeDateInputs = $('.js-end-date-input');

        jsNoticeDateInputs.on('blur, change', function () {
            let startDate = $('#tenancy-start-date-with-help').val();
            let noticeDate = $('#notice-date').val();
            let noticeMethod = $('[name="giving-notice-query"]:checked');

            window.setTimeout(function () {
                let valid = true;

                const startInput = document.querySelector('#tenancy-start-date-with-help');
                const startInputValidation = startInput.dataset.validation.split(' ').map(check => commonForms[check]);
                if (!commonForms.validateInput($(startInput), startInputValidation, false)) {
                    valid = false;
                }

                const noticeInput = document.querySelector('#notice-date');
                const noticeInputValidation = noticeInput.dataset.validation.split(' ').map(check => commonForms[check]);
                if (!commonForms.validateInput($(noticeInput), noticeInputValidation, false)) {
                    valid = false;
                }

                if (startDate && noticeDate && noticeMethod.length && valid) {
                    noticeToLeaveForm.calcEndOfNoticeDate(commonForms.stringToDate(startDate), commonForms.stringToDate(noticeDate), noticeMethod.val());
                } else {
                    $('#end-of-notice-note').html('');
                    $('#end-of-notice-result').addClass('fully-hidden');
                }
            }, 10);
        });
    },

    setupBackToSummary: function () {
        noticeToLeaveForm.form.setupBackToSummary();
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
                requiredName: true,
                suppressedFields: ['email', 'phone', 'registrationNumber'],
                fieldMappings: function(number) {
                    const fieldMappings = {};

                    fieldMappings['landlords[\'landlord-' + number + '\'].name'] = '#landlord-' + number + '-name';
                    fieldMappings['landlords[\'landlord-' + number + '\'].address.building'] = '#landlord-' + number + '-address-building';
                    fieldMappings['landlords[\'landlord-' + number + '\'].address.street'] = '#landlord-' + number + '-address-street';
                    fieldMappings['landlords[\'landlord-' + number + '\'].address.town'] = '#landlord-' + number + '-address-town';
                    fieldMappings['landlords[\'landlord-' + number + '\'].address.region'] = '#landlord-' + number + '-address-region';
                    fieldMappings['landlords[\'landlord-' + number + '\'].address.postcode'] = '#landlord-' + number + '-postcode';
                    fieldMappings['landlords[\'landlord-' + number + '\'].telephone'] = '#landlord-' + number + '-phone';

                    return fieldMappings;
                },
                initPostcodeLookups: function(number) {
                    new PostcodeLookup({ rpz: false, lookupId: 'landlord-' + number + '-postcode-lookup' }); // NOSONAR
                }
            }
        ];

        commonHousing.setupRepeatingSections(sections, this.form);
    },

    setupAddTenantNames: function () {
        const addTenantButton = document.querySelector('.js-add-tenant');
        if (addTenantButton) {
            addTenantButton.addEventListener('click', (event) => {
                event.preventDefault();

                const tenantNamesContainer = document.querySelector('.js-tenant-names-container');

                const tenantNames = noticeToLeaveForm.form.settings.formObject.tenantNames;
                const currentNumberOfTenants = tenantNamesContainer.querySelectorAll('.ds_question').length;
                const newNumber = currentNumberOfTenants + 1;

                const newTenantField = document.createElement('div');
                newTenantField.classList.add('ds_question');
                newTenantField.innerHTML = `<label class="ds_label" for="tenant-${newNumber}-name">Tenant ${newNumber}: Full name</label>
            <input class="ds_input" type="text" id="tenant-${newNumber}-name" data-form="textinput-tenant-${newNumber}-name">`;

                tenantNamesContainer.appendChild(newTenantField);

                noticeToLeaveForm.form.mapField(`tenantNames.tenant${newNumber}`, `#tenant-${newNumber}-name`);
                tenantNames[`tenant${newNumber}`] = null;
            });
        }

        const addSubtenantButton = document.querySelector('.js-add-subtenant');
        if (addSubtenantButton) {
            addSubtenantButton.addEventListener('click', (event) => {
                event.preventDefault();

                const subtenantNamesContainer = document.querySelector('.js-subtenant-names-container');

                const subtenantNames = noticeToLeaveForm.form.settings.formObject.subtenantNames;
                const currentNumberOfSubtenants = subtenantNamesContainer.querySelectorAll('.ds_question').length;
                const newNumber = currentNumberOfSubtenants + 1;

                const newSubtenantField = document.createElement('div');
                newSubtenantField.classList.add('ds_question');
                newSubtenantField.innerHTML = `<label class="ds_label" for="subtenant-${newNumber}-name">Subtenant ${newNumber}: Full name</label>
            <input class="ds_input" type="text" id="subtenant-${newNumber}-name" data-form="textinput-subtenant-${newNumber}-name">`;

                subtenantNamesContainer.appendChild(newSubtenantField);

                noticeToLeaveForm.form.mapField(`subtenantNames.subtenant${newNumber}`, `#subtenant-${newNumber}-name`);
                subtenantNames[`subtenant${newNumber}`] = null;
            });
        }
    },

    setupValidations: function () {
        // On click of error playback link, scroll to above field so label is visible
        $('body').on('click', '.error a', function(){
            const currentStep = noticeToLeaveForm.form.currentStep.slug;
            let targetElement = $(this.hash);

            // if on summary page, link to section in table
            if (currentStep === 'summary'){
                const elementId = this.hash.slice(1);
                targetElement = $('[data-summary-field*=' + elementId + ']');
            }

            const elementTop = targetElement.offset().top;
            $('html, body').animate({ scrollTop: elementTop - 60 }, 200);
        });
    },

    setupPostcodeLookups: function() {
        new PostcodeLookup({ required: true, rpz: false, lookupId: 'property-postcode-lookup' }); // NOSONAR
        new PostcodeLookup({ rpz: false, lookupId: 'letting-agent-postcode-lookup' }); // NOSONAR
    },

    validateStep: function () {
        return commonForms.validateStep(noticeToLeaveForm.form.currentStep);
    },

    prepareFormDataForPost: function (formData) {
        // there are some things we need to address before we post the data

        // Format tenants and landlords into arrays
        const formDataForPost = {};

        let noticePeriodEndDate;

        if (formData.hasEndNoticeHelp === 'no') {
            formDataForPost.entryDate = formData.entryDate;
            noticePeriodEndDate = formData.noticePeriodEndDate;
        } else {
            formDataForPost.entryDate = formData.entryDateWithHelp;
            noticePeriodEndDate = formData.noticePeriodEndDateWithHelp;
        }

        let noticePeriodEndDateAsDate = commonForms.stringToDate(noticePeriodEndDate);
        let earliestTribunalDate = new Date(noticePeriodEndDateAsDate.getTime());
        earliestTribunalDate.setHours(0,0,0,0);
        earliestTribunalDate.setDate(earliestTribunalDate.getDate() + 1);
        formDataForPost.earliestTribunalDate = commonForms.dateToString(earliestTribunalDate);

        if (formData.hasLandlordsAgent === 'yes') {
            formDataForPost.landlordsAgent = formData.landlordsAgent;
        } else {
            formDataForPost.landlords = commonForms.objectValues(formData.landlords);
        }
        formDataForPost.address = formData.address;
        formDataForPost.supportingEvidence = formData.supportingEvidence;

        if (this.formType === 'tenant') {
            formDataForPost.tenantNames = commonForms.objectValues(formData.tenantNames);
        } else {
            formDataForPost.tenantNames = commonForms.objectValues(formData.subtenantNames);
        }

        formDataForPost.reasons = formData.reasons.map(reason => reason.id);
        formDataForPost.reasonDetails = formData.reasonDetails;

        // Format dates to YYYY-MM-DD
        const dateFields = ['earliestTribunalDate', 'entryDate'];

        for (let j = 0; j < dateFields.length; j++) {
            const field = dateFields[j];
            const value = formDataForPost[field];

            if (value === null || value === '' || value.split(' ').join('') === ''){
                continue;
            }

            formDataForPost[field] = value.split('/').reverse().join('-');
        }

        return formDataForPost;
    },

    calcEndOfNoticeDate: function (startDate, noticeDate, noticeMethod) {
        let endOfNoticeData = this.getEndOfNoticeData(startDate, noticeDate, noticeMethod);
        let tenantNoticeDate = noticeDate;

        let earliestTribunalDate = new Date(noticeDate.getTime());
        earliestTribunalDate.setHours(0, 0, 0, 0);
        if (endOfNoticeData.type === 'months') {
            this.addMonths(earliestTribunalDate, endOfNoticeData.number);
        } else {
            earliestTribunalDate.setDate(earliestTribunalDate.getDate() + endOfNoticeData.number);
        }

        // advance the end of notice by one day (it is the following day)
        earliestTribunalDate.setDate(earliestTribunalDate.getDate() + 1);

        // if email or post add 2 days
        if (endOfNoticeData.hasAddition) {
            earliestTribunalDate.setDate(earliestTribunalDate.getDate() + 2);
        }

        let endDateOfNoticePeriod = new Date(earliestTribunalDate.getTime());
        endDateOfNoticePeriod.setDate(endDateOfNoticePeriod.getDate() - 1);

        let type = noticeToLeaveForm.formType;

        $('#end-of-notice-note').html(`<div class="ds_inset-text__text">
            <p>Based on the details you've entered, you must give your ${type} <b class="summary__user-input">${endOfNoticeData.number} ${endOfNoticeData.type}'</b> notice${endOfNoticeData.hasAddition ? ' <span class="summary__user-input">plus 2 days</span>' : '' }. This means the first day after the notice period ends will
            be <b class="summary__user-input">${commonForms.dateToString(earliestTribunalDate)}</b>.</p>

            <p>If you do not give this notice to your ${type} on <b class="summary__user-input">${commonForms.dateToString(tenantNoticeDate)}</b>, it'll be wrong and
            you'll need to fill in a new one.</p>
            </div>
        `);

        $('#end-of-notice-result').removeClass('fully-hidden');

        $('#notice-period-end-date-with-help')
            .attr('data-mindate', commonForms.dateToString(endDateOfNoticePeriod))
            .val(commonForms.dateToString(endDateOfNoticePeriod))
            .trigger('change');
    },

    getEndOfNoticeData: function (startDate, noticeDate, noticeMethod) {
        // const evictionGroundsWithPeriods = {
        //     LANDLORD_TO_SELL: '84d',
        //     LENDER_TO_SELL: '84d',
        //     LANDLORD_TO_REFURBISH: '84d',
        //     LANDLORD_TO_LIVE: '84d',
        //     LANDLORD_FAMILY_MEMBER_TO_LIVE: '84d',
        //     LANDLORD_NON_RESIDENTIAL_PURPOSE: '84d',
        //     RELIGIOUS: '84d',
        //     LANDLORD_REGISTRATION_REVOKED: '84d',
        //     LANDLORD_HMO_REFUSED: '84d',
        //     LANDLORD_OVERCROWDING_NOTICE: '84d',
        //     YOU_CEASE_TO_BE_EMPLOYEE: '84d',
        //     YOU_NO_LONGER_NEED_SUPPORTED_ACC: '84d',
        //     YOU_NO_LONGER_OCCUPY: '28d',
        //     YOU_BREACHED_TERMS: '28d',
        //     YOU_RENT_ARREARS: '28d',
        //     YOU_CRIMINAL_CONVICTION: '28d',
        //     YOU_ANTISOCIAL_BEHAVIOUR: '28d',
        //     YOU_ASSOCIATED_CONVICTION_OR_ANTISOCIAL: '28d'
        // };

        const evictionGroundsWithPeriodsCOVID = {
            LANDLORD_TO_SELL: '6m',
            LENDER_TO_SELL: '6m',
            LANDLORD_TO_REFURBISH: '6m',
            LANDLORD_TO_LIVE: '3m',
            LANDLORD_FAMILY_MEMBER_TO_LIVE: '3m',
            LANDLORD_NON_RESIDENTIAL_PURPOSE: '6m',
            RELIGIOUS: '6m',
            LANDLORD_REGISTRATION_REVOKED: '3m',
            LANDLORD_HMO_REFUSED: '3m',
            LANDLORD_OVERCROWDING_NOTICE: '6m',
            YOU_CEASE_TO_BE_EMPLOYEE: '6m',
            YOU_NO_LONGER_NEED_SUPPORTED_ACC: '6m',
            YOU_NO_LONGER_OCCUPY: '28d',
            YOU_BREACHED_TERMS: '6m',
            YOU_RENT_ARREARS: '6m',
            YOU_CRIMINAL_CONVICTION: '3m',
            YOU_ANTISOCIAL_BEHAVIOUR: '3m',
            YOU_ASSOCIATED_CONVICTION_OR_ANTISOCIAL: '3m'
        };

        const evictionGroundsUsed = noticeToLeaveForm.form.settings.formObject.reasons;

        let today = new Date();
        today.setHours(0,0,0,0);

        let sixMonthsFromStartDate = startDate;
        sixMonthsFromStartDate.setHours(0,0,0,0);
        sixMonthsFromStartDate.setMonth(sixMonthsFromStartDate.getMonth() + 6);

        let endDate = startDate;
        endDate.setHours(0, 0, 0, 0);

        // object we'll return (default data)
        const retData = {
            type: 'days'
        };

        // if email or post add 2 days
        retData.hasAddition = noticeMethod !== 'hand';

        // find shortest eviction grounds
        let shortestEvictionPeriod = 9999;
        let shortestGround;
        evictionGroundsUsed.forEach(ground => {
            const definitions = {
                '6m': 168,
                '3m': 84,
                '28d': 28
            };

            if (definitions[evictionGroundsWithPeriodsCOVID[ground.id]] < shortestEvictionPeriod) {
                shortestEvictionPeriod = definitions[evictionGroundsWithPeriodsCOVID[ground.id]];
                shortestGround = ground;
            }

        });

        const period = evictionGroundsWithPeriodsCOVID[shortestGround.id];

        if (period.match('m')) {
            endDate.setMonth(endDate.getMonth() + parseInt(period, 10));
            retData.type = 'months';
            retData.number = parseInt(period, 10);
        } else if (period.match('d')) {
            endDate.setDate(endDate.getDate() + parseInt(period, 10));
            retData.number = parseInt(period, 10);
        } else {
            endDate.setDate(endDate.getDate() + period);
            retData.number = parseInt(period);
        }

        return retData;
    },

    addMonths: function (date, months) {
        const currentMonth = date.getMonth() + date.getFullYear() * 12;
        date.setMonth(date.getMonth() + months);
        const diff = date.getMonth() + date.getFullYear() * 12 - currentMonth;

        // If the difference is incorrect, set date to last day of prev month
        if (diff !== months) {
            date.setDate(0);
        }
        return date;
    }
};



$('.js-download-file').on('click', function (event) {
    event.preventDefault();
    const documentDownloadForm = $('#document-download');
    documentDownloadForm.find('input[name="type"]').val($(this).closest('.document-info').attr('data-documenttype'));
    documentDownloadForm.submit();
});

$('#document-download').on('submit', function(){
    // make a copy of the form data to manipulate before posting
    const formData = JSON.parse(JSON.stringify(noticeToLeaveForm.form.settings.formObject));
    const data = noticeToLeaveForm.prepareFormDataForPost(formData);
    data.recaptcha = grecaptcha.getResponse();

    // Set hidden data field to have value of JSON data
    $(this).find('input[name="data"]').val(encodeURIComponent(JSON.stringify(data)));

    expireRecaptcha();
});

export default noticeToLeaveForm;
