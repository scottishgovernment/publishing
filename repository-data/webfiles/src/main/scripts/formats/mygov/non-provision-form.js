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
import bloomreachWebfile from '../../tools/bloomreach-webfile';

import DSDatePicker from '../../../../../node_modules/@scottish-government/pattern-library/src/components/date-picker/date-picker';

const formTemplate = require('../../templates/mygov/non-provision-form');
const summaryTemplate = require('../../templates/mygov/non-provision-summary');
const housingFormPageNavTemplate = require('../../templates/housing-form-pagenav');
const sectionNavTemplate = require('../../templates/visited-only-section-nav');
const subNavTemplate = require('../../templates/visited-only-subsection-nav');
const noticeTemplate = require('../../templates/mygov/non-provision-notice-end');

/*
    * Calculate the end date of the notice period the landlord has to respond.
    */

const calcNoticeEnd = function(){
    // Standard notice period is 28 days
    let noticePeriod = 28;
    const dateNoticeGiven = formObject.noticeDate.split('/').reverse().join('-');

    // If terms have changed in the 28 days before the notice is given, add the remainder of that period
    if (formObject.section10bFailure && formObject.hasChangedTerms === 'true'){
        const lastChangedDate = formObject.changedTermsDate.split('/').reverse().join('-');

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

                const html = summaryTemplate.render(nonProvisionForm.form.settings.formObject);
                document.querySelector('#summary-container').innerHTML = html;

                commonHousing.validateSummary();
                commonHousing.summaryAccordion(document.getElementById('summary-container'));
                window.DS.tracking.init(summaryContainer);
            },
            updateNoticeEndDate: function () {
                calcNoticeEnd();
                const html = noticeTemplate.render({noticeEndDate: formObject.intendedReferralDate});
                document.querySelector('#notice-end-container').innerHTML = html;
            }
        },
        sectionTemplate: sectionNavTemplate,
        subsectionTemplate: subNavTemplate,
        pageNavTemplate: housingFormPageNavTemplate,
        pageNavFunction: pageNavFunction
    }),

    init: function () {
        // append form template
        const formTemplateContainer = document.querySelector('#form-container');
        if (!formTemplateContainer) {
            return false;
        }
        const overviewContent = formTemplateContainer.innerHTML;
        formTemplateContainer.innerHTML = formTemplate.render({
            tenants: true,
            iconsFile: bloomreachWebfile('/assets/images/icons/icons.stack.svg'),
            webfilesPath: bloomreachWebfile()
        });
        formTemplateContainer.querySelector('#overview').innerHTML = overviewContent;

        if (window.DS && window.DS.components) {
            const accordionElements = [].slice.call(formTemplateContainer.querySelectorAll('[data-module="ds-accordion"]'));
            accordionElements.forEach(accordionElement => new window.DS.components.Accordion(accordionElement).init());
        }

        commonForms.appendCaptchaScript();

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

    setupAddTenantNames: function () {
        const addTenantButton = document.querySelector('.js-add-tenant');
        if (addTenantButton) {
            addTenantButton.addEventListener('click', event => {
                event.preventDefault();

                const tenantNames = nonProvisionForm.form.settings.formObject.tenantNames;
                const currentNumberOfTenants = commonForms.objectKeys(tenantNames).length;
                const newNumber = currentNumberOfTenants + 1;

                const tenantNamesContainer = document.querySelector('.js-tenant-names-container');
                const newQuestion = document.createElement('div');

                newQuestion.classList.add('ds_question');
                newQuestion.innerHTML = `<label class="ds_label" for="tenant-${newNumber}-name">Tenant ${newNumber}: Full name</label>
                <input type="text" id="tenant-${newNumber}-name" class="ds_input">`;

                tenantNamesContainer.appendChild(newQuestion);

                nonProvisionForm.form.mapField(`tenantNames.tenant${newNumber}`, `#tenant-${newNumber}-name`);
                tenantNames[`tenant${newNumber}`] = null;

                window.DS.tracking.init(newQuestion);
            });
        }
    },

    setupAddLandlords: function () {
        const sections = [
            {
                name: 'landlords',
                group: 'managing-the-property',
                nameInput: '.js-landlord-name',
                container: '.js-landlords-container',
                template: require('../../templates/mygov/ftt-landlord-html'),
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
        const newDate = new Date();
        const minChangedDate = new Date(newDate.setDate(newDate.getDate() - 27));

        const changedDatePicker = new DSDatePicker(document.getElementById('changed-terms-date-picker'), {minDate: minChangedDate, maxDate: new Date(), imagePath: bloomreachWebfile('/assets/images/icons/')});
        const noticeDatePicker = new DSDatePicker(document.getElementById('notice-date-picker'), {minDate: new Date(), imagePath: bloomreachWebfile('/assets/images/icons/')});

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

            commonForms.validateInput(this, validationChecks);
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

            formData[field] = value.split('/').reverse().join('-');
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
        if (formData.hasLettingAgent === 'yes') {
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

    pageNavData.startPage = currentStep.slug === 'overview';

    const stepIsLastInSection = currentStep.slug === $(`#${currentStep.section} section:last`).attr('id');

    if (stepIsLastInSection) {
        pageNavData.addLandlord = (currentStep.section === 'managing-the-property' && currentStep.slug !== 'letting-agent');
    }

    return pageNavData;
}

$('.multi-page-form').on('click', '.js-download-file', function (event) {
    event.preventDefault();

    const documentDownloadForm = $('#non-provision-document-download');
    documentDownloadForm.find('input[name="type"]').val($(this).closest('.document-info').attr('data-documenttype'));

    // make sure the notice period end date is up to date
    calcNoticeEnd();

    // make a copy of the form data to manipulate before posting
    const formData = JSON.parse(JSON.stringify(nonProvisionForm.form.settings.formObject));
    const data = nonProvisionForm.prepareFormDataForPost(formData);
    data.recaptcha = grecaptcha.getResponse();

    // analytics tracking
    const downloadType = documentDownloadForm.find('input[name=type]').val();

    commonForms.track({
        'event': 'formSubmitted',
        'formId': 'non-provision-form',
        'downloadType': downloadType
    });

    // Set hidden data field to have value of JSON data
    documentDownloadForm.find('input[name="data"]').val(encodeURIComponent(JSON.stringify(data)));
    documentDownloadForm.trigger('submit');
    expireRecaptcha();
});

window.format = nonProvisionForm;
window.format.init();

export default nonProvisionForm;
