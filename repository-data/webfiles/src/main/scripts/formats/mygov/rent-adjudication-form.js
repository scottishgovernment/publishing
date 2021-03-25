// RENT ADJUDICATION FORM

/* global require grecaptcha expireRecaptcha */

'use strict';

const formObject = {
    propertyAddress: null,
    tenants: { 'tenant-1': {} },
    tenantAgent: {
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
    },
    'propertyType': null,
    'rooms': [{
        name: 'Living rooms',
        quantity: 1
    },{
        name: 'Bedrooms',
        quantity: 1
    },{
        name: 'Kitchens',
        quantity: 1
    },{
        name: 'Bathrooms',
        quantity: 1
    },{
        name: 'WC',
        quantity: 0
    }],
    'sharedAreas': null,
    'included': null,
    'heating': null,
    'doubleGlazing': null,
    'servicesDetails': null,
    'servicesCostDetails': null,
    'furnished': null,
    'noInventory': null,
    'improvementsTenant': null,
    'improvementsLandlord': null,
    'damage': null,
    'currentRentAmount': null,
    'currentRentFrequency': null,
    'newRentAmount': null,
    'newRentFrequency': null,

    landlords: { 'landlord-1': {} },
    'hasLettingAgent': null,
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
    },
    'notAvailableForInspection': null
};

const formMapping = {
    'propertyAddress': '#rpz-property-address',
    'hasTenantAgent': '[name="tenant-agent-query"]',
    'tenantAgent.name': '#tenant-agent-name',
    'tenantAgent.email': '#tenant-agent-email',
    'tenantAgent.telephone': '#tenant-agent-phone',
    'tenantAgent.address.building': '#tenant-agent-address-building',
    'tenantAgent.address.street': '#tenant-agent-address-street',
    'tenantAgent.address.town': '#tenant-agent-address-town',
    'tenantAgent.address.region': '#tenant-agent-address-region',
    'tenantAgent.address.postcode': '#tenant-agent-postcode',
    'hasLettingAgent': '[name="letting-agent-query"]',
    'lettingAgent.name': '#letting-agent-name',
    'lettingAgent.email': '#letting-agent-email',
    'lettingAgent.telephone': '#letting-agent-phone',
    'lettingAgent.address.building': '#letting-agent-address-building',
    'lettingAgent.address.street': '#letting-agent-address-street',
    'lettingAgent.address.town': '#letting-agent-address-town',
    'lettingAgent.address.region': '#letting-agent-address-region',
    'lettingAgent.address.postcode': '#letting-agent-postcode',

    // property
    'propertyType': '[name="property-building"]',
    'buildingOther': '#building-other',
    // rooms
    'sharedAreas': '#shared-areas-details',
    'sharedAreasQuery': '[name="shared-areas-query"]',
    'included': '#included-details',
    'includedQuery': '[name="included-query"]',
    'heating': '[name="heating-query"]',
    'doubleGlazing': '[name="glazing-query"]',
    'servicesQuery': '[name="services-query"]',
    'servicesDetails': '#services-details',
    'servicesCostDetails': '#services-cost-details',
    'furnished': '[name="furnished-query"]',
    'noInventory': '#has-inventory',
    'tenantImprovementsQuery': '[name="tenant-improvements-query"]',
    'landlordImprovementsQuery': '[name="landlord-improvements-query"]',
    'improvementsTenant': '#tenant-improvements-details',
    'improvementsLandlord': '#landlord-improvements-details',
    'damage': '#damages-details',
    'damagesQuery': '[name="damages-query"]',

    // rent
    'currentRentAmount': '#current-rent-amount',
    'currentRentFrequency': '#current-payment-frequency',
    'newRentAmount': '#new-rent-amount',
    'newRentFrequency': '#new-payment-frequency',

    'notAvailableForInspection': '#property-inspection-dates'
};

import $ from 'jquery';
import feedback from '../../components/feedback';
import EditableTable from '../../components/editable-table';
import MultiPageForm from '../../components/multi-page-form';
import PostcodeLookup from '../../components/postcode-lookup';
import commonForms from '../../tools/forms';
import commonHousing from '../../tools/housing';
import formSections from '../../components/mygov/housing-forms/rent-adjudication-sections';

const formTemplate = require('../../templates/mygov/rent-adjudication-form');
const summaryTemplate = require('../../templates/mygov/rent-adjudication-summary');
const housingFormPageNavTemplate = require('../../templates/housing-form-pagenav');
const sectionNavTemplate = require('../../templates/visited-only-section-nav');
const subNavTemplate = require('../../templates/visited-only-subsection-nav');

$('form').each(function() {
    this.reset();
});

const rentAdjudicationForm = {
    form: new MultiPageForm({
        formSections: formSections,
        formMapping: formMapping,
        formObject: formObject,
        formEvents: {
            updateSummary: function () {
                const summaryObject = JSON.parse(JSON.stringify(rentAdjudicationForm.form.settings.formObject));

                summaryObject.hasTenants = Object.values(summaryObject.tenants)[0].name;
                summaryObject.hasLandlords = Object.values(summaryObject.landlords)[0].name;

                const html = summaryTemplate.render(summaryObject);
                document.querySelector('#summary-container').innerHTML = html;

                commonHousing.validateSummary();
                commonHousing.summaryAccordion(document.getElementById('summary-container'));
            },
            insertPropertyAddress: function() {
                const propertySpan = $('.js-declaration-address');
                const addressToInsert = rentAdjudicationForm.form.settings.formObject.propertyAddress || '_______';

                propertySpan.text(addressToInsert);
            },
            checkInventory: function() {
                const elementToHide = $('.js-hide-if-no-inventory');
                if (rentAdjudicationForm.form.settings.formObject.noInventory && rentAdjudicationForm.form.settings.formObject.furnished === 'yes') {
                    elementToHide.hide();
                } else {
                    elementToHide.show();
                }
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
        pageNavFunction: function () {return commonForms.pageNavFunction('overview', rentAdjudicationForm.form.currentStep);},
        pageNavTemplate: housingFormPageNavTemplate
    }),

    init: function () {
        // append form template
        const formTemplateContainer = document.querySelector('#form-container');
        if (!formTemplateContainer) {
            return false;
        }
        const overviewContent = formTemplateContainer.innerHTML;
        formTemplateContainer.innerHTML = formTemplate.render({tenants: true});
        formTemplateContainer.querySelector('#overview').innerHTML = formTemplateContainer.querySelector('#overview').innerHTML + overviewContent;

        feedback.init();
        rentAdjudicationForm.form.validateStep = rentAdjudicationForm.validateStep;
        rentAdjudicationForm.form.init();
        commonHousing.setManualLinkSections();
        this.setupRepeatingSections();
        this.setupValidations();
        this.setupPostcodeLookups();
        this.setupCustomFieldEvents();
        this.setupBackToSummary();
        commonForms.setupRecaptcha();

        new EditableTable({ // NOSONAR
            tableContainer: document.querySelector('#rooms-table'),
            formObject: rentAdjudicationForm.form.settings.formObject,
            dataPath: 'rooms',
            fields: [
                {
                    title: 'Room name',
                    slug: 'name',
                    validation: 'requiredField'
                },
                {
                    title: 'Quantity',
                    slug: 'quantity',
                    validation: 'requiredField',
                    options: [
                        {displayName: 0, value: 0},
                        {displayName: 1, value: 1},
                        {displayName: 2, value: 2},
                        {displayName: 3, value: 3},
                        {displayName: 4, value: 4},
                        {displayName: 5, value: 5},
                        {displayName: 6, value: 6},
                        {displayName: 7, value: 7}
                    ]
                }
            ],
            addText: 'Add a room'
        });
    },

    setupBackToSummary: function () {
        rentAdjudicationForm.form.setupBackToSummary();
    },

    setupRepeatingSections: function () {
        const sections = [
            {
                name: 'tenants',
                nameInput: '.js-tenant-name',
                container: '.js-tenants-container',
                template: require('../../templates/tenant-html'),
                addressrequired: true,
                requiredName: true,
                slug: 'tenant',
                stepTitle: 'Tenant',
                fieldMappings: function (number) {
                    const fieldMappings = {};

                    fieldMappings['tenants[\'tenant-' + number + '\'].name'] = `#tenant-${number}-name`;
                    fieldMappings['tenants[\'tenant-' + number + '\'].email'] = `#tenant-${number}-email`;
                    fieldMappings['tenants[\'tenant-' + number + '\'].telephone'] = `#tenant-${number}-phone`;
                    fieldMappings['tenants[\'tenant-' + number + '\'].address.building'] = `#tenant-${number}-address-building`;
                    fieldMappings['tenants[\'tenant-' + number + '\'].address.street'] = `#tenant-${number}-address-street`;
                    fieldMappings['tenants[\'tenant-' + number + '\'].address.town'] = `#tenant-${number}-address-town`;
                    fieldMappings['tenants[\'tenant-' + number + '\'].address.region'] = `#tenant-${number}-address-region`;
                    fieldMappings['tenants[\'tenant-' + number + '\'].address.postcode'] = `#tenant-${number}-postcode`;

                    return fieldMappings;
                },
                initPostcodeLookups: function(number) {
                    new PostcodeLookup({ rpz: false, lookupId: `tenant-${number}-postcode-lookup`, dataFrom: 'propertyAddressResult' }); // NOSONAR
                    new PostcodeLookup({ rpz: false, lookupId: `guarantor-${number}-postcode-lookup` }); // NOSONAR
                }
            }, {
                name: 'landlords',
                nameInput: '.js-landlord-name',
                container: '.js-landlords-container',
                template: require('../../templates/landlord-html'),
                addressrequired: true,
                requiredName: true,
                slug: 'landlord',
                stepTitle: 'Landlord',
                injectedContent: 'RSS need to write to your Landlord, so please provide their full contact details.',
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
            const currentStep = rentAdjudicationForm.form.currentStep.slug;
            let targetElement = $(this.hash);

            // if on summary page, link to section in table
            if (currentStep === 'summary'){
                const elementId = this.hash.slice(1);
                targetElement = $(`[data-summary-field*=${elementId}]`);
            }

            const elementTop = targetElement.offset().top;
            $('html, body').animate({ scrollTop: elementTop - 60 }, 500);
        });

        return;
    },

    setupPostcodeLookups: function() {
        const addressInfoNoteHtml = '<h4>Your home is in a Rent Pressure Zone (RPZ).</h4>' +
            '<p>You can\'t apply to Rent Service Scotland about your rent being too high. ' +
            'This is because the amount your rent can go up is already capped to stop it being too high.</p>' +
            '<p>If you live in an RPZ and still think your rent is too high, you can ' +
            '<a href="https://www.mygov.scot/rent-pressure-zone-checker/" target="_blank">' +
            'find out what the RPZ limit for your home is</a>.</p>';

        new PostcodeLookup({ readOnly: true, rpz: true, lookupId: 'rpz-postcode-lookup', infoNoteHtml: addressInfoNoteHtml, storeResultAs: 'propertyAddressResult' }); // NOSONAR
        new PostcodeLookup({ rpz: false, lookupId: 'tenant-1-postcode-lookup' }); // NOSONAR
        new PostcodeLookup({ rpz: false, lookupId: 'tenant-agent-postcode-lookup' }); // NOSONAR
        new PostcodeLookup({ rpz: false, lookupId: 'landlord-1-postcode-lookup' }); // NOSONAR
        new PostcodeLookup({ rpz: false, lookupId: 'letting-agent-postcode-lookup' }); // NOSONAR
    },

    setupCustomFieldEvents: function () {
        // payment frequency -- prepopulate initial "current" frequency onto "new" frequency
        const currentFrequency = $('#current-payment-frequency');
        const newFrequency = $('#new-payment-frequency');

        $('#page-nav').on('click', '#button-next', function () {
            if (rentAdjudicationForm.form.currentStep.slug === 'current-rent' &&
                !newFrequency.is('.js-dirty')
            ) {
                newFrequency.val(currentFrequency.val());
                newFrequency.trigger('change');
            }
        });

        newFrequency.on('change', function () {
            newFrequency.addClass('js-dirty');
        });
    },

    validateStep: function () {
        return commonForms.validateStep(rentAdjudicationForm.form.currentStep);
    },

    prepareFormDataForPost: function (formData) {
        // there are some things we need to address before we post the data
        const formDataForPost = JSON.parse(JSON.stringify(formData));

        // 1. Add value from 'other' option in property type field if selected
        if ($('#building-other-query').is(':checked')) {
            formDataForPost.propertyType = $('#building-other').val();
        } else {
            formDataForPost.propertyType = $('[name="property-building"]:checked + label').text();
        }

        // 2. Format tenants and landlords into arrays
        formDataForPost.tenants = commonForms.objectValues(formData.tenants);
        formDataForPost.landlords = commonForms.objectValues(formData.landlords);

        // 3. Remove letting/tenant agent details if no letting/tenant agent
        if (formData.hasLettingAgent === 'no'){
            formDataForPost.lettingAgent = {};
        }

        if (formData.hasTenantAgent === 'no'){
            formDataForPost.tenantAgent = {};
        }

        // 4. Remove details about the home when 'no' chosen for that question
        if (formData.sharedAreasQuery === 'no'){
            formDataForPost.sharedAreas = null;
        }

        if (formData.includedQuery === 'no'){
            formDataForPost.included = null;
        }

        if (formData.servicesQuery === 'no'){
            formDataForPost.servicesDetails = null;
            formDataForPost.servicesCostDetails = null;
        }

        if (formData.furnished === 'no'){
            formDataForPost.furnished = null;
            formDataForPost.noInventory = null;
        }

        if (formData.tenantImprovementsQuery === 'no'){
            formDataForPost.improvementsTenant = null;
        }

        if (formData.landlordImprovementsQuery === 'no'){
            formDataForPost.improvementsLandlord = null;
        }

        if (formData.damagesQuery === 'no'){
            formDataForPost.damage = null;
        }

        return formDataForPost;
    }
};

$('.multi-page-form').on('click', '.js-download-file', function (event) {
    event.preventDefault();

    const documentDownloadForm = $('#ra-document-download');
    documentDownloadForm.find('input[name="type"]').val($(this).closest('.document-info').attr('data-documenttype'));

    // make a copy of the form data to manipulate before posting
    const formData = JSON.parse(JSON.stringify(rentAdjudicationForm.form.settings.formObject));
    const data = rentAdjudicationForm.prepareFormDataForPost(formData);
    data.recaptcha = grecaptcha.getResponse();

    // analytics tracking
    const downloadType = documentDownloadForm.find('input[name=type]').val();

    commonForms.track({
        'event': 'formSubmitted',
        'formId': 'rent-adjudication-form',
        'downloadType': downloadType,
        'tenantNumber': data.tenants.length,
        'landlordNumber': data.landlords.length
    });

    // Set hidden data field to have value of JSON data
    documentDownloadForm.find('input[name="data"]').val(encodeURIComponent(JSON.stringify(data)));
    documentDownloadForm.trigger('submit');
    expireRecaptcha();
});

window.format = rentAdjudicationForm;
window.format.init();

export default rentAdjudicationForm;
