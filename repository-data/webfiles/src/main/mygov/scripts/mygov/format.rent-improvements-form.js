// RENT IMPROVEMENTS FORM

/* global require grecaptcha expireRecaptcha */

'use strict';

const formObject = {
    address: null,
    landlords: { 'landlord-1': {} },
    tenants: { 'tenant-1': {} },
    landlordsAgent: null,
    hasLettingAgent: null,
    improvements: '',
    rentIncreaseAmount: '',
    rentIncreaseFrequency: '',
    includesReceipts: null,
    includesBeforeAndAfterPictures: null
};

const formMapping = {
    'address': '#property-address-property-address',
    'hasLettingAgent': '[name="letting-agent-query"]',
    'landlordsAgent.name': '#letting-agent-name',
    'landlordsAgent.email': '#letting-agent-email',
    'landlordsAgent.telephone': '#letting-agent-phone',
    'landlordsAgent.address.building': '#letting-agent-address-building',
    'landlordsAgent.address.street': '#letting-agent-address-street',
    'landlordsAgent.address.town': '#letting-agent-address-town',
    'landlordsAgent.address.region': '#letting-agent-address-region',
    'landlordsAgent.address.postcode': '#letting-agent-postcode',
    'improvements': '#improvements-details',
    'rentIncreaseAmount': '#rent-increase-amount',
    'rentIncreaseFrequency': '#rent-payment-frequency',
    'includesReceipts': '#documents-receipts',
    'includesBeforeAndAfterPictures': '#documents-photos'
};

import feedback from './feedback';
import MultiPageForm from './component.multi-page-form';
import PostcodeLookup from './component.postcode-lookup';
import commonForms from './common.forms';
import formSections from './housing-forms/rent-improvements-sections';
import commonHousing from './common.housing';
import $ from 'jquery';

const rentImprovementsSummaryTemplate = require('../templates/rent-improvements-summary.hbs');
const housingFormPageNavTemplate = require('../templates/housing-form-pagenav.hbs');
const sectionNavTemplate = require('../templates/visited-only-section-nav.hbs');
const subNavTemplate = require('../templates/visited-only-subsection-nav.hbs');
const rentImprovementsDownloadTemplate = require('../templates/rent-improvements-download.hbs');

$('form').each(function() {
    this.reset();
});

const rentImprovementsForm = {
    form: new MultiPageForm({
        formSections: formSections,
        formMapping: formMapping,
        formObject: formObject,
        formEvents: {
            updateSummary: function () {
                const html = rentImprovementsSummaryTemplate(rentImprovementsForm.form.settings.formObject);
                document.querySelector('#summary-container').innerHTML = html;

                commonHousing.validateSummary();
                commonHousing.summaryAccordion(document.getElementById('summary-container'));
            },
            insertPropertyAddress: function() {
                const propertySpan = $('.js-declaration-address');
                const addressToInsert = rentImprovementsForm.form.settings.formObject.address || '_______';

                propertySpan.text(addressToInsert);
            },
            checkDocuments: function() {
                const formObject = rentImprovementsForm.form.settings.formObject;
                const noDocuments = !formObject.includesReceipts && !formObject.includesBeforeAndAfterPictures;

                const html = rentImprovementsDownloadTemplate({form: rentImprovementsForm.form.settings.formObject,
                    noDocuments: noDocuments});
                document.querySelector('.js-download-details-container').innerHTML = html;
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
        pageNavFunction: function () {return commonForms.pageNavFunction('overview', rentImprovementsForm.form.currentStep);},
        pageNavTemplate: housingFormPageNavTemplate
    }),

    init: function () {
        feedback.init();
        rentImprovementsForm.form.validateStep = rentImprovementsForm.validateStep;
        rentImprovementsForm.form.init();
        commonHousing.setManualLinkSections();
        this.setupRepeatingSections();
        this.setupValidations();
        this.setupPostcodeLookups();
        this.setupBackToSummary();
        commonForms.setupRecaptcha();
    },

    setupBackToSummary: function () {
        rentImprovementsForm.form.setupBackToSummary();
    },

    setupRepeatingSections: function () {
        const sections = [
            {
                name: 'tenants',
                nameInput: '.js-tenant-name',
                container: '.js-tenants-container',
                template: 'tenantHtml',
                addressrequired: true,
                requiredName: true,
                slug: 'tenant',
                stepTitle: 'Tenant',
                suppressedFields: ['address'],
                fieldMappings: function (number) {
                    const fieldMappings = {};

                    fieldMappings['tenants[\'tenant-' + number + '\'].name'] = `#tenant-${number}-name`;
                    fieldMappings['tenants[\'tenant-' + number + '\'].email'] = `#tenant-${number}-email`;
                    fieldMappings['tenants[\'tenant-' + number + '\'].telephone'] = `#tenant-${number}-phone`;

                    return fieldMappings;
                }
            }, {
                name: 'landlords',
                nameInput: '.js-landlord-name',
                container: '.js-landlords-container',
                template: 'landlordHtml',
                addressrequired: true,
                requiredName: true,
                slug: 'landlord',
                stepTitle: 'Landlord',
                suppressedFields: ['registrationNumber'],
                fieldMappings: function(number) {
                    const fieldMappings = {};

                    fieldMappings['landlords[\'landlord-' + number + '\'].name'] = `#landlord-${number}-name`;
                    fieldMappings['landlords[\'landlord-' + number + '\'].email'] = `#landlord-${number}-email`;
                    fieldMappings['landlords[\'landlord-' + number + '\'].telephone'] = `#landlord-${number}-phone`;
                    fieldMappings['landlords[\'landlord-' + number + '\'].registrationNumber'] = `#landlord-${number}-registration`;
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
    },

    setupValidations: function () {
        // On click of error playback link, scroll to above field so label is visible
        $('body').on('click', '.client-error a', function(){
            const currentStep = rentImprovementsForm.form.currentStep.slug;
            let targetElement = $(this.hash);

            // if on summary page, link to section in table
            if (currentStep === 'summary'){
                const elementId = this.hash.slice(1);
                targetElement = $(`[data-summary-field*=${elementId}`);
            }

            const elementTop = targetElement.offset().top;
            $('html, body').animate({ scrollTop: elementTop - 60 }, 500);
        });

        return;
    },

    setupPostcodeLookups: function() {
        const addressInfoNoteHtml = '<h4>Your home is in a Rent Pressure Zone (RPZ).</h4>';

        new PostcodeLookup({ readOnly: true, rpz: true, lookupId: 'property-address-postcode-lookup', infoNoteHtml: addressInfoNoteHtml }); // NOSONAR
        new PostcodeLookup({ rpz: false, lookupId: 'landlord-1-postcode-lookup' }); // NOSONAR
        new PostcodeLookup({ rpz: false, lookupId: 'letting-agent-postcode-lookup' }); // NOSONAR
    },

    validateStep: function () {
        return commonHousing.validateStep(rentImprovementsForm.form.currentStep);
    },

    prepareFormDataForPost: function (formData) {
        // there are some things we need to address before we post the data
        const formDataForPost = JSON.parse(JSON.stringify(formData));

        // 1. Format tenants and landlords into arrays
        formDataForPost.tenants = commonForms.objectValues(formData.tenants);
        formDataForPost.landlords = commonForms.objectValues(formData.landlords);

        // 2. Remove letting/tenant agent details if no letting/tenant agent
        if (formData.hasLettingAgent === 'no'){
            formDataForPost.landlordsAgent = {};
        }

        // 3. Delete unneeded fields
        delete formDataForPost.hasLettingAgent;
        delete formDataForPost.hasLettingAgent_text;
        delete formDataForPost.rentIncreaseFrequency_text;

        return formDataForPost;
    }
};

$('.js-download-file').on('click', function (event) {
    event.preventDefault();
    const documentDownloadForm = $('#r-imp-document-download');
    documentDownloadForm.find('input[name="type"]').val($(this).closest('.document-info').attr('data-documenttype'));
    documentDownloadForm.submit();
});

$('#r-imp-document-download').on('submit', function(){
    // make a copy of the form data to manipulate before posting
    const formData = JSON.parse(JSON.stringify(rentImprovementsForm.form.settings.formObject));
    const data = rentImprovementsForm.prepareFormDataForPost(formData);
    data.recaptcha = grecaptcha.getResponse();

    // analytics tracking
    const downloadType = $(this).find('input[name=type]').val();

    commonForms.track({
        'event': 'formSubmitted',
        'formId': 'rent-increase-for-improvement-form',
        'downloadType': downloadType,
        'tenantNumber': data.tenants.length,
        'landlordNumber': data.landlords.length
    });

    // Set hidden data field to have value of JSON data
    $(this).find('input[name="data"]').val(encodeURIComponent(JSON.stringify(data)));

    expireRecaptcha();
});

window.format = rentImprovementsForm;
window.format.init();

export default rentImprovementsForm;
