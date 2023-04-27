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
import bloomreachWebfile from '../../tools/bloomreach-webfile';

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

                const summaryContainer = document.querySelector('#summary-container');
                const html = summaryTemplate.render(summaryObject);
                summaryContainer.innerHTML = html;

                commonHousing.validateSummary();
                commonHousing.summaryAccordion(summaryContainer);
                window.DS.tracking.init(summaryContainer);
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

        const formTemplateData = {
            iconsFile: bloomreachWebfile('/assets/images/icons/icons.stack.svg')
        };

        if (formType === 'tenant') {
            formTemplateData.tenants = true;
        } else {
            formTemplateData.subtenants = true;
        }

        formTemplateData.webfilesPath = bloomreachWebfile();
        formTemplateData.assetsPath = document.getElementById('site-root-path').value.replace('mygov', '') + 'assets';

        this.recaptchaSitekey = document.getElementById('recaptchaSitekey').value;
        this.recaptchaEnabled = document.getElementById('recaptchaEnabled').value === 'true';
        formTemplateData.recaptchaEnabled = this.recaptchaEnabled;
        formTemplateData.recaptchaSitekey = this.recaptchaSitekey;

        formTemplateContainer.innerHTML = formTemplate.render(formTemplateData);
        formTemplateContainer.querySelector('#overview').innerHTML = overviewContent;

        commonForms.appendCaptchaScript();

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
        if (this.recaptchaEnabled) {
            commonForms.setupRecaptcha();
        }
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
        const tenancyStartDatePicker = new DSDatePicker(document.getElementById('tenancy-start-date-picker'), { maxDate: new Date(), imagePath: bloomreachWebfile('/assets/images/icons/') });
        const tenancyStartWithHelpDatePicker = new DSDatePicker(document.getElementById('tenancy-start-date-with-help-picker'), {maxDate: new Date(), imagePath: bloomreachWebfile('/assets/images/icons/')});
        const noticeDatePicker = new DSDatePicker(document.getElementById('notice-date-picker'), {minDate: new Date(), imagePath: bloomreachWebfile('/assets/images/icons/')});
        const tribunalDatePicker = new DSDatePicker(document.getElementById('notice-period-end-date-picker'), {minDate: new Date(), imagePath: bloomreachWebfile('/assets/images/icons/')});
        const tribunalWithHelpDatePicker = new DSDatePicker(document.getElementById('notice-period-end-date-with-help-picker'), {minDate: new Date(), imagePath: bloomreachWebfile('/assets/images/icons/')});

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
                if (!commonForms.validateInput(startInput, startInputValidation, false)) {
                    valid = false;
                }

                const noticeInput = document.querySelector('#notice-date');
                const noticeInputValidation = noticeInput.dataset.validation.split(' ').map(check => commonForms[check]);
                if (!commonForms.validateInput(noticeInput, noticeInputValidation, false)) {
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
                suppressedFields: ['email', 'registrationNumber'],
                fieldMappings: function(number) {
                    const fieldMappings = {};

                    fieldMappings['landlords[\'landlord-' + number + '\'].name'] = '#landlord-' + number + '-name';
                    fieldMappings['landlords[\'landlord-' + number + '\'].address'] = new PostcodeLookup(document.getElementById('landlord-' + number + '-postcode-lookup'));
                    fieldMappings['landlords[\'landlord-' + number + '\'].address.building'] = '#landlord-' + number + '-address-building';
                    fieldMappings['landlords[\'landlord-' + number + '\'].address.street'] = '#landlord-' + number + '-address-street';
                    fieldMappings['landlords[\'landlord-' + number + '\'].address.town'] = '#landlord-' + number + '-address-town';
                    fieldMappings['landlords[\'landlord-' + number + '\'].address.region'] = '#landlord-' + number + '-address-region';
                    fieldMappings['landlords[\'landlord-' + number + '\'].address.postcode'] = '#landlord-' + number + '-postcode';
                    fieldMappings['landlords[\'landlord-' + number + '\'].telephone'] = '#landlord-' + number + '-phone';

                    return fieldMappings;
                }
            }
        ];

        commonHousing.setupRepeatingSections(sections, this.form);
    },

    setupAddTenantNames: function () {
        const addTenantButtons = [].slice.call(document.querySelectorAll('.js-add-tenant'));
        addTenantButtons.forEach(addTenantButton => {
            addTenantButton.addEventListener('click', event => {
                event.preventDefault();

                const tenantNames = noticeToLeaveForm.form.settings.formObject.tenantNames;
                const currentNumberOfTenants = commonForms.objectKeys(tenantNames).length;
                const newNumber = currentNumberOfTenants + 1;

                const tenantNamesContainer = document.querySelector('.js-tenant-names-container');
                const newQuestion = document.createElement('div');

                newQuestion.classList.add('ds_question');
                newQuestion.innerHTML = `<label class="ds_label" for="tenant-${newNumber}-name">Tenant ${newNumber}: Full name</label>
                <input type="text" id="tenant-${newNumber}-name" class="ds_input">`;

                tenantNamesContainer.appendChild(newQuestion);

                noticeToLeaveForm.form.mapField(`tenantNames.tenant${newNumber}`, `#tenant-${newNumber}-name`);
                tenantNames[`tenant${newNumber}`] = null;

                window.DS.tracking.init(newQuestion);
            });
        });

        const addSubTenantButtons = [].slice.call(document.querySelectorAll('.js-add-subtenant'));
        addSubTenantButtons.forEach(addSubTenantButton => {
            addSubTenantButton.addEventListener('click', event => {
                event.preventDefault();

                const subtenantNames = noticeToLeaveForm.form.settings.formObject.subtenantNames;
                const currentNumberOfSubtenants = commonForms.objectKeys(subtenantNames).length;
                const newNumber = currentNumberOfSubtenants + 1;

                const subtenantNamesContainer = document.querySelector('.js-subtenant-names-container');
                const newQuestion = document.createElement('div');

                newQuestion.classList.add('ds_question');
                newQuestion.innerHTML = `<label class="ds_label" for="subtenant-${newNumber}-name">Subtenant ${newNumber}: Full name</label>
                <input type="text" id="subtenant-${newNumber}-name" class="ds_input">`;

                subtenantNamesContainer.appendChild(newQuestion);

                noticeToLeaveForm.form.mapField(`subtenantNames.subtenant${newNumber}`, `#subtenant-${newNumber}-name`);
                subtenantNames[`subtenant${newNumber}`] = null;

                window.DS.tracking.init(newQuestion);
            });
        });
    },

    setupValidations: function () {
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

    setupPostcodeLookups: function() {
        new PostcodeLookup(document.getElementById('property-postcode-lookup'), { required: true });
        new PostcodeLookup(document.getElementById('letting-agent-postcode-lookup'));
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

        // always advance the end of notice by one day (it is the following day)
        earliestTribunalDate.setDate(earliestTribunalDate.getDate() + 1);

        if (endOfNoticeData.type === 'months') {
            this.addMonths(earliestTribunalDate, endOfNoticeData.number);
        } else {
            earliestTribunalDate.setDate(earliestTribunalDate.getDate() + endOfNoticeData.number);
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
        let evictionGroundsWithPeriods = {
            LANDLORD_TO_SELL: '84d',
            LANDLORD_TO_SELL_HARDSHIP: '84d',
            LENDER_TO_SELL: '84d',
            LANDLORD_TO_REFURBISH: '84d',
            LANDLORD_TO_LIVE: '84d',
            LANDLORD_TO_LIVE_HARDSHIP: '84d',
            LANDLORD_FAMILY_MEMBER_TO_LIVE: '84d',
            LANDLORD_NON_RESIDENTIAL_PURPOSE: '84d',
            RELIGIOUS: '84d',
            LANDLORD_REGISTRATION_REVOKED: '84d',
            LANDLORD_HMO_REFUSED: '84d',
            LANDLORD_OVERCROWDING_NOTICE: '84d',
            YOU_CEASE_TO_BE_EMPLOYEE: '84d',
            YOU_NO_LONGER_NEED_SUPPORTED_ACC: '84d',
            YOU_NO_LONGER_OCCUPY: '28d',
            YOU_BREACHED_TERMS: '28d',
            YOU_RENT_ARREARS: '28d',
            YOU_RENT_ARREARS_SUBSTANTIAL: '28d',
            YOU_CRIMINAL_CONVICTION: '28d',
            YOU_ANTISOCIAL_BEHAVIOUR: '28d',
            YOU_ASSOCIATED_CONVICTION_OR_ANTISOCIAL: '28d'
        };

        const evictionGroundsUsed = noticeToLeaveForm.form.settings.formObject.reasons;

        if (noticeMethod !== 'hand') {
            noticeDate.setDate(noticeDate.getDate() + 2);
        }

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

        // find longest eviction grounds
        let longestEvictionPeriod = 0;
        let longestGround;
        evictionGroundsUsed.forEach(ground => {
            const definitions = {
                '6m': 168,
                '3m': 84,
                '84d': 84,
                '28d': 28
            };

            if (definitions[evictionGroundsWithPeriods[ground.id]] > longestEvictionPeriod) {
                longestEvictionPeriod = definitions[evictionGroundsWithPeriods[ground.id]];
                longestGround = ground;
            }
        });

        const period = evictionGroundsWithPeriods[longestGround.id];

        if (noticeDate < sixMonthsFromStartDate) {
            // if within six months of start date
            retData.number = 28;
        } else if (period.match('m')) {
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

$('.multi-page-form').on('click', '.js-download-file', function (event) {
    event.preventDefault();

    const documentDownloadForm = $('#document-download');
    documentDownloadForm.find('input[name="type"]').val($(this).closest('.js-document-container').attr('data-documenttype'));

    // make a copy of the form data to manipulate before posting
    const formData = JSON.parse(JSON.stringify(noticeToLeaveForm.form.settings.formObject));
    const data = noticeToLeaveForm.prepareFormDataForPost(formData);
    if (noticeToLeaveForm.recaptchaEnabled) {
        data.recaptcha = grecaptcha.getResponse();
    }

    // Set hidden data field to have value of JSON data
    documentDownloadForm.find('input[name="data"]').val(encodeURIComponent(JSON.stringify(data)));
    documentDownloadForm.trigger('submit');
    if (noticeToLeaveForm.recaptchaEnabled) {
        expireRecaptcha();
    }
});

export default noticeToLeaveForm;
