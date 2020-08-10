// NON-PROVISION FORM

/* global require grecaptcha expireRecaptcha */

'use strict';

const formObject = {
    'address': {
        'building': null,
        'street': null,
        'region': null,
        'town': null,
        'postcode': null
    },
    'tenantNames': { 'tenant1': null },
    'hasAgent': null,
    'tenantsAgent': {
        'name': null,
        'address': {
            'building': null,
            'street': null,
            'region': null,
            'town': null,
            'postcode': null
        }
    },
    'hasLettingAgent': null,
    'landlordsAgent': {
        'name': null,
        'address': {
            'building': null,
            'street': null,
            'region': null,
            'town': null,
            'postcode': null
        }
    },
    'landlords': {
        'landlord-1': {
            'name': null,
            'address': {
                'building': null,
                'street': null,
                'region': null,
                'town': null,
                'postcode': null
            }
        }
    },
    'communicationMethod': null,
    'noticeDate': null,
    'intendedReferralDate': null,

    'section10aFailure': false,
    'section10bFailure': false,
    'section10aDetails': '',
    'section10bDetails': '',
    'section11Failure': false,
    'section11Details': '',
    'section16Failure': false,
    'hasChangedTerms': '',
    'changedTermsDate': null
};

const formSections = [
    {
        group: {
            slug: 'part-0',
            title: 'Part zero',
            hideFromSectionNav: true
        },
        hideFromSectionNav: true,
        slug: 'introduction',
        title: 'Introduction',
        pages: [
            {
                slug: 'introduction',
                title: 'Introduction',
                hideSubsectionNav: true,
                hideSectionNav: true,
                noFormBox: true
            }
        ]
    },
    {
        group: {
            slug: 'form-details',
            title: 'Details',
            iconLeft: true
        },
        slug: 'property',
        title: 'Property',
        pages: [
            {
                slug: 'property-address',
                title: 'Property address',
                hideSubsectionNav: true
            }
        ]
    },
    {
        group: {
            slug: 'form-details',
            title: 'Details',
            iconLeft: true
        },
        slug: 'tenants',
        title: 'Tenants',
        pages: [
            {
                slug: 'tenant-names',
                title: 'Tenant names'
            },
            {
                slug: 'agent-details',
                title: 'Agent'
            }
        ]
    },
    {
        group: {
            slug: 'form-details',
            title: 'Details',
            iconLeft: true
        },
        slug: 'managing-the-property',
        title: 'Managing the property',
        pages: [
            {
                slug: 'letting-agent',
                title: 'Letting agent'
            }
        ]
    },
    {
        group: {
            slug: 'form-details',
            title: 'Details',
            iconLeft: true
        },
        slug: 'whats-missing',
        title: 'What\'s missing',
        pages: [
            {
                slug: 'whats-missing-details',
                title: 'What\'s missing'
            },
            {
                slug: 'changed-terms',
                title: 'Changed or new terms',
                disabled: true
            },
            {
                slug: 'payment-order',
                title: 'Payment order'
            }
        ]
    },
    {
        group: {
            slug: 'form-details',
            title: 'Details',
            iconLeft: true
        },
        slug: 'giving-this-notice',
        title: 'Giving this notice',
        pages: [
            {
                slug: 'communication',
                title: 'Giving this notice'
            },
            {
                slug: 'notice-date',
                title: 'Notice date'
            },
            {
                slug: 'notice-end',
                title: 'Notice period end date',
                triggerEvent: 'updateNoticeEndDate'
            }
        ]
    },
    {
        group: {
            slug: 'form-details',
            title: 'Details',
            iconLeft: true
        },
        slug: 'summary',
        title: 'Summary',
        pages: [
            {
                slug: 'summary',
                title: 'Summary',
                hideSubsectionNav: true,
                noFormBox: true,
                triggerEvent: 'updateSummary'
            }
        ]
    },
    {
        group: {
            slug: 'download',
            title: 'Download',
            hideFromGroupNav: true
        },
        slug: 'download',
        title: 'Download',
        pages: [
            {
                slug: 'notice-download',
                title: 'Download',
                hideSubsectionNav: true,
                noFormBox: true
            }
        ]
    }
];

const formMapping = {
    'address.building': '#property-address-building',
    'address.street': '#property-address-street',
    'address.town': '#property-address-town',
    'address.region': '#property-address-region',
    'address.postcode': '#property-postcode',
    'tenantNames.tenant1': '#tenant-1-name',
    'hasAgent': '[name="tenants-agent-query"]',
    'tenantsAgent.name': '#tenants-agent-name',
    'tenantsAgent.address.building': '#tenants-agent-address-building',
    'tenantsAgent.address.street': '#tenants-agent-address-street',
    'tenantsAgent.address.town': '#tenants-agent-address-town',
    'tenantsAgent.address.region': '#tenants-agent-address-region',
    'tenantsAgent.address.postcode': '#tenants-agent-postcode',
    'hasLettingAgent': '[name="letting-agent-query"]',
    'landlordsAgent.name': '#letting-agent-name',
    'landlordsAgent.address.building': '#letting-agent-address-building',
    'landlordsAgent.address.street': '#letting-agent-address-street',
    'landlordsAgent.address.town': '#letting-agent-address-town',
    'landlordsAgent.address.region': '#letting-agent-address-region',
    'landlordsAgent.address.postcode': '#letting-agent-postcode',
    'landlords[\'landlord-1\'].name': '#landlord-1-name',
    'landlords[\'landlord-1\'].address.building': '#landlord-1-address-building',
    'landlords[\'landlord-1\'].address.street': '#landlord-1-address-street',
    'landlords[\'landlord-1\'].address.town': '#landlord-1-address-town',
    'landlords[\'landlord-1\'].address.region': '#landlord-1-address-region',
    'landlords[\'landlord-1\'].address.postcode': '#landlord-1-postcode',
    'section10aFailure': '#missing-tenancy',
    'section10bFailure': '#missing-new-terms',
    'section10aDetails': '#missing-tenancy-details',
    'section10bDetails': '#missing-new-terms-details',
    'section11Failure': '#missing-notes',
    'section11Details': '#missing-notes-details',
    'section16Failure': '[name="payment-order-query"]',
    'hasChangedTerms': '[name="changed-terms-query"]',
    'changedTermsDate': '#changed-terms-date-picker',
    'communicationMethod': '[name="communication-query"]',
    'noticeDate': '#notice-date-picker'
};

import $ from 'jquery';
import feedback from '../../components/feedback';
import MultiPageForm from '../../components/multi-page-form';
import PostcodeLookup from '../../components/postcode-lookup';
import commonForms from '../../tools/forms';
import commonHousing from '../../tools/housing';
import moment from '../../vendor/moment';
import DSDatePicker from '../../../../../node_modules/@scottish-government/pattern-library/src/components/date-picker/date-picker';

const nonProvisionSummaryTemplate = require('../../templates/non-provision-summary.hbs');
const housingFormPageNavTemplate = require('../../templates/housing-form-pagenav.hbs');
const nonProvisionSectionNavTemplate = require('../../templates/visited-only-section-nav.hbs');
const nonProvisionSubNavTemplate = require('../../templates/visited-only-subsection-nav.hbs');
const noticeTemplate = require('../../templates/non-provision-notice-end.hbs');

/*
    * Calculate the end date of the notice period the landlord has to respond.
    */

const calcNoticeEnd = function(){
    // Standard notice period is 28 days
    let noticePeriod = 28;
    const dateNoticeGiven = moment(formObject.noticeDate, 'DD/MM/YYYY');

    // If terms have changed in the 28 days before the notice is given, add the remainder of that period
    if (formObject.section10bFailure && formObject.hasChangedTerms === 'true'){
        const lastChangedDate = moment(formObject.changedTermsDate, 'DD/MM/YYYY');

        const daysBetween = dateNoticeGiven.diff(lastChangedDate, 'days');

        if (daysBetween <= 28){
            const daysOfPeriodRemaining = 28 - daysBetween;
            noticePeriod = noticePeriod + daysOfPeriodRemaining;
        }
    }

    // If using post or email, 2 days more are given
    if (formObject.communicationMethod === 'recorded-delivery' || formObject.communicationMethod === 'email') {
        noticePeriod = noticePeriod + 2;
    }

    // Add the notice period to the date notice is given
    if (formObject.noticeDate){
        formObject.intendedReferralDate = dateNoticeGiven.add(noticePeriod, 'days').format('DD/MM/YYYY');
    }
};

const nonProvisionForm = {
    form: new MultiPageForm({
        formId: 'non-provision-form',
        formSections: formSections,
        formMapping: formMapping,
        formObject: formObject,
        modifiers: [{
            pattern: new RegExp(/\.postcode$/),
            transformFunction: function () {
                return arguments[0].toUpperCase();
            }
        }],
        formEvents: {
            updateSummary: function () {
                calcNoticeEnd();
nonProvisionForm.form.settings.formObject = {"address":{"building":"CLINTZ COTTAGE HARTSIDE","street":"","region":"","town":"OXTON, LAUDER","postcode":"TD2 6PU"},"tenantNames":{"tenant1":"ten 1 name","tenant2":"ten 2 name"},"hasAgent":"true","tenantsAgent":{"name":"Jonathan Sutcliffe","address":{"building":"CLINTZ COTTAGE HARTSIDE","street":"","region":"","town":"OXTON, LAUDER","postcode":"TD2 6PU"}},"hasLettingAgent":"true","landlordsAgent":{"name":"Jonathan Sutcliffe","address":{"building":"NETHER HARTSIDE","street":"","region":"","town":"OXTON, LAUDER","postcode":"TD2 6PU"}},"landlords":{"landlord-1":{}},"communicationMethod":null,"noticeDate":"16/06/2020","intendedReferralDate":"28/07/2020","section10aFailure":false,"section10bFailure":true,"section10aDetails":"","section10bDetails":"234","section11Failure":false,"section11Details":"","section16Failure":"true","hasChangedTerms":"true","changedTermsDate":"02/06/2020","hasAgent_text":"Yes","hasLettingAgent_text":"Yes","hasChangedTerms_text":"Yes","section16Failure_text":"Yes"}
                const html = nonProvisionSummaryTemplate(nonProvisionForm.form.settings.formObject);
                document.querySelector('#summary-container').innerHTML = html;

                commonHousing.validateSummary();
                commonHousing.summaryAccordion(document.getElementById('summary-container'));
            },
            updateNoticeEndDate: function () {
                calcNoticeEnd();
                const html = noticeTemplate({noticeEndDate: formObject.intendedReferralDate});
                document.querySelector('#notice-end-container').innerHTML = html;
            }
        },
        sectionTemplate: nonProvisionSectionNavTemplate,
        subsectionTemplate: nonProvisionSubNavTemplate,
        pageNavTemplate: housingFormPageNavTemplate,
        pageNavFunction: pageNavFunction
    }),

    init: function () {
        feedback.init();
        commonHousing.setManualLinkSections();
        this.setupAddTenantNames();
        this.setupAddLandlords();
        this.setupDatePickers();
        this.setupValidations();
        this.setupPostcodeLookups();
        this.setupConditionalSections();
        commonForms.setupRecaptcha();

        nonProvisionForm.form.validateStep = nonProvisionForm.validateStep;
        nonProvisionForm.form.init();
    },

    setupConditionalSections: function(){
        // disable landlords section if letting agent is selected
        $('#letting-agent-query').on('change', function(){
            const landlords = commonForms.objectKeys(nonProvisionForm.form.settings.formObject.landlords);

            if ($('#letting-agent-yes').prop('checked')){
                for (let i = 0; i < landlords.length; i++) {
                    nonProvisionForm.form.disableStep(landlords[i]);
                }
                // if letting agent, disable validations on landlords fields
                $('[data-step="managing-the-property"] section:not([data-step="letting-agent"]) [data-validation]').addClass('no-validate');

                nonProvisionForm.form.updateFormNav();
            } else {
                for (let i = 0; i < landlords.length; i++) {
                    nonProvisionForm.form.enableStep(landlords[i]);
                }

                // if no letting agent, enable validations on landlords fields
                $('[data-step="managing-the-property"] section:not([data-step="letting-agent"]) .postcode-search').removeClass('no-validate');
                $('[data-step="managing-the-property"] section:not([data-step="letting-agent"]) [id$=-name]').removeClass('no-validate');

                nonProvisionForm.form.updateFormNav();
            }
        });

        // enable changed or new terms section if changed terms are missing
        $('#whats-missing-query [type="checkbox"]').on('change', function(){
            if ($('#missing-new-terms').prop('checked')){
                nonProvisionForm.form.enableStep('changed-terms');
                // enable validations on changed terms fields
                $('#changed-terms-query').removeClass('no-validate');

                nonProvisionForm.form.updateFormNav();
            } else {
                nonProvisionForm.form.disableStep('changed-terms');

                // disable validations on changed terms fields
                $('#changed-terms-query').addClass('no-validate');

                nonProvisionForm.form.updateFormNav();
            }
        });
    },

    setupAddTenantNames: function(){
        $('.js-add-tenant').on('click', function (event) {
            event.preventDefault();

            const tenantNames = nonProvisionForm.form.settings.formObject.tenantNames;
            const currentNumberOfTenants = commonForms.objectKeys(tenantNames).length;
            const newNumber = currentNumberOfTenants + 1;

            $('.js-tenant-names-container').append(
                `<div class="ds_question"><label class="ds_label" for="tenant-${newNumber}-name">Tenant ${newNumber}: Full name</label>
                <input type="text" id="tenant-${newNumber}-name" class="ds_input" data-form="textinput-tenant-${newNumber}-name"></div>`);

            nonProvisionForm.form.mapField(`tenantNames.tenant${newNumber}`, `#tenant-${newNumber}-name`);
            tenantNames[`tenant${newNumber}`] = null;
        });
    },

    setupAddLandlords: function () {
        const sections = [
            {
                name: 'landlords',
                group: 'managing-the-property',
                nameInput: '.js-landlord-name',
                container: '.js-landlords-container',
                template: 'landlordFTTHtml',
                slug: 'landlord',
                stepTitle: 'Landlord',
                fieldMappings: function(number) {
                    const fieldMappings = {};
                    fieldMappings['landlords[\'landlord-' + number + '\'].name'] = `#landlord-${number}-name`;
                    fieldMappings['landlords[\'landlord-' + number + '\'].address.building'] = `#landlord-${number}-address-building`;
                    fieldMappings['landlords[\'landlord-' + number + '\'].address.street'] = `#landlord-${number}-address-street`;
                    fieldMappings['landlords[\'landlord-' + number + '\'].address.town'] = `#landlord-${number}-address-town`;
                    fieldMappings['landlords[\'landlord-' + number + '\'].address.region'] = `#landlord-${number}-address-region`;
                    fieldMappings['landlords[\'landlord-' + number + '\'].address.postcode'] = `#landlord-${number}-postcode`;
                    return fieldMappings;
                },
                initPostcodeLookups: function(number) {
                    new PostcodeLookup({ rpz: false, lookupId: `landlord-${number}-postcode-lookup` }); // NOSONAR
                }
            }
        ];

        commonHousing.setupRepeatingSections(sections, this.form);

        // when a new page loads, decide if an 'add ...' button should be displayed.
        $('#page-nav').on('change', function(){
            // if the summary page has been visited AND we're on a page before the summary page, show the "back to summary" button
            const summaryStep = nonProvisionForm.form.getStep('slug', 'summary');
            const formSectionsFlattened = nonProvisionForm.form.flattenedSections(true);

            const thisStepIndex = formSectionsFlattened.findIndex(function (element) {
                return element.slug === nonProvisionForm.form.currentStep.slug;
            });
            const summaryStepIndex = formSectionsFlattened.findIndex(function (element) {
                return element.slug === summaryStep.slug;
            });

            const goToSummaryButton = $('#go-to-summary');

            if (summaryStep.visited && thisStepIndex < summaryStepIndex) {
                goToSummaryButton.removeClass('fully-hidden');
            } else {
                goToSummaryButton.addClass('fully-hidden');
            }
        });
    },


    setupDatePickers: function () {
        const newDate = moment();
        const minChangedDate = newDate.subtract(27, 'days').toDate();

        const changedDatePicker = new DSDatePicker(document.getElementById('changed-terms-date-picker'), {minDate: minChangedDate, maxDate: new Date()});
        const noticeDatePicker = new DSDatePicker(document.getElementById('notice-date-picker'), {minDate: new Date()});

        changedDatePicker.init();
        noticeDatePicker.init();
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
        $('body').on('click', '.client-error a', function(){
            const currentStep = nonProvisionForm.form.currentStep.slug;
            let targetElement = $(this.hash);

            // if on summary page, link to section in table
            if (currentStep === 'summary'){
                const elementId = this.hash.slice(1);
                targetElement = $(`[data-summary-field*=${elementId}`);
            }

            const elementTop = targetElement.offset().top;
            $('html, body').animate({ scrollTop: elementTop - 60 }, 500);
        });
    },

    setupPostcodeLookups: function() {
        new PostcodeLookup({ rpz: false, lookupId: 'property-postcode-lookup' }); // NOSONAR
        new PostcodeLookup({ rpz: false, lookupId: 'tenants-agent-postcode-lookup' }); // NOSONAR
        new PostcodeLookup({ rpz: false, lookupId: 'letting-agent-postcode-lookup' }); // NOSONAR
    },

    validateStep: function () {
        /* look for data-validation attributes in current step & PERFORM VALIDATION
            * do not allow progress if invalid
            */
        const currentStep = nonProvisionForm.form.currentStep;
        const currentStepContainer = $(`section[data-step="${currentStep.slug}"]`);
        const fieldsThatNeedToBeValidated = currentStepContainer.find('[data-validation]:visible');

        fieldsThatNeedToBeValidated.each(function (index, element) {
            /* add 'dirty' class to elements that may not have been interacted
            *  with yet so they will be revalidated on keypress
            */
            $(element).addClass('js-dirty');

            // perform validation
            const validations = element.getAttribute('data-validation').split(' ');
            const validationChecks = [];
            for (let i = 0, il = validations.length; i < il; i++) {
                if (commonForms[validations[i]]) {
                    validationChecks.push(commonForms[validations[i]]);
                }
            }

            commonForms.validateInput($(this), validationChecks);
        });

        const invalidFields = currentStepContainer.find('[aria-invalid="true"]:visible');

        return invalidFields.length === 0;
    },

    prepareFormDataForPost: function (formData) {

        // 1. Format dates to YYYY-MM-DD
        const dateFields = ['noticeDate', 'intendedReferralDate'];

        for (let i = 0; i < dateFields.length; i++) {
            const field = dateFields[i];
            const value = formData[field];
            if (value === null || value.split(' ').join('') === ''){
                continue;
            }

            const momentDate = moment(value, 'DD/MM/YYYY');
            formData[field] = momentDate.format('YYYY-MM-DD');
        }

        // 2. Remove details from 'missing' if box not checked
        if (!formData.section10aFailure){
            formData.section10aDetails = '';
        }

        if (!formData.section10bFailure){
            formData.section10bDetails = '';
        }

        if (!formData.section11Failure){
            formData.section11Details = '';
        }

        if (formData.section10aFailure || formData.section10bFailure) {
            formData.section10Failure = true;
            formData.section10Details = `${formData.section10aDetails}\n\n${formData.section10bDetails}`;
        }

        // 3. Format landlords and tenant names
        formData.landlords = commonForms.objectValues(formData.landlords);
        formData.tenantNames = commonForms.objectValues(formData.tenantNames).filter(value => !!value);

        // 4. Remove landlord or letting agent data
        if (formData.hasLettingAgent === 'true') {
            formData.landlords = [];
        } else {
            formData.landlordsAgent = {};
        }

        // 5. Remove tenant's agent data if no agent
        if (formData.hasAgent === 'false'){
            formData.tenantsAgent = {};
        }

        return formData;
    }
};

function pageNavFunction () {
    // which extra buttons do we show?
    const pageNavData = {};
    const currentStep = nonProvisionForm.form.currentStep;

    pageNavData.startPage = currentStep.slug === 'introduction';

    const stepIsLastInSection = currentStep.slug === $(`#${currentStep.section} section:last`).attr('id');

    if (stepIsLastInSection) {
        pageNavData.addLandlord = (currentStep.section === 'managing-the-property' && currentStep.slug !== 'letting-agent');
    }

    return pageNavData;
}

$('.js-download-file').on('click', function (event) {
    event.preventDefault();

    const documentDownloadForm = $('#non-provision-document-download');
    documentDownloadForm.find('input[name="type"]').val($(this).closest('.document-info').attr('data-documenttype'));
    documentDownloadForm.submit();
});

$('#non-provision-document-download').on('submit', function() {
    // make sure the notice period end date is up to date
    calcNoticeEnd();

    // make a copy of the form data to manipulate before posting
    const formData = JSON.parse(JSON.stringify(nonProvisionForm.form.settings.formObject));
    const data = nonProvisionForm.prepareFormDataForPost(formData);
    data.recaptcha = grecaptcha.getResponse();

    // analytics tracking
    const downloadType = $(this).find('input[name=type]').val();

    commonForms.track({
        'event': 'formSubmitted',
        'formId': 'non-provision-form',
        'downloadType': downloadType
    });

    // Set hidden data field to have value of JSON data
    $(this).find('input[name="data"]').val(encodeURIComponent(JSON.stringify(data)));
    expireRecaptcha();
});

window.format = nonProvisionForm;
window.format.init();

export default nonProvisionForm;
