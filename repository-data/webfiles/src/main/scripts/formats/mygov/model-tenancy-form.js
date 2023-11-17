// MODEL TENANCY FORM

/* global require grecaptcha expireRecaptcha */

'use strict';
const formObject = {'propertyType':null, 'propertyInRpz': false, 'buildingOther':null,'furnishingType':'','propertyAddress':null,'hmoProperty':'','hmo24ContactNumber':null,'hmoRegistrationExpiryDate':null,'hmoRenewalApplicationSubmitted':false,'tenancyStartDate':null,'rentAmount':null,'rentPaymentFrequency':null,'rentPaymentScheduleObject':{},'rentPaymentSchedule': null,'rentPayableInAdvance':null,'rentPaymentMethod':null,'firstPaymentAmount':null,'firstPaymentDate':null,'firstPaymentPeriodEnd':null,'servicesIncludedInRent':[],includedAreasOrFacilities:[],sharedFacilities:[],excludedAreasFacilities:[],'communicationsAgreement':'','depositAmount':null,'tenancyDepositSchemeAdministrator':null,'services':null,'facilities':[],'landlords':{'landlord-1':{}},'lettingAgent':{'name':null,'address':{'building':null,'street':null,'town':null,'postcode':null},'telephone':null,'registrationNumber':null},'agent':{'name':null,'address':{'building':null,'street':null,'town':null,'postcode':null},'telephone':null},'factor':{'name':null,'address':{'building':null,'street':null,'town':null,'postcode':null},'telephone':null,'registrationNumber':null},'tenants':{'tenant-1':{}},'optionalTerms':{'contentsAndConditions':null,'localAuthorityTaxesAndCharges':null,'utilities':null,'commonParts':null,'alterations':null,'privateGarden':null,'roof':null,'binsAndRecycling':null,'storage':null,'dangerousSubstances':null,'pets':null,'smoking':null,'liquidPetroleumGas':null},'additionalTerms':{'additional-term-1':{}}};

import $ from 'jquery';
import feedback from '../../components/feedback';
import EditableTable from '../../components/editable-table';
import MultiPageForm from '../../components/multi-page-form';
import PostcodeLookup from '../../components/postcode-lookup';
import formSections from '../../components/mygov/housing-forms/model-tenancy-sections';
import formMapping from '../../components/mygov/housing-forms/model-tenancy-mapping';
import commonForms from '../../tools/forms';
import commonHousing from '../../tools/housing';
import DSDatePicker from '../../../../../node_modules/@scottish-government/design-system/src/components/date-picker/date-picker';
import bloomreachWebfile from '../../tools/bloomreach-webfile';

const formTemplate = require('../../templates/mygov/model-tenancy-form');
const housingFormPageNavTemplate = require('../../templates/housing-form-pagenav');
const mandatoryTemplate = require('../../templates/mygov/model-tenancy-mandatory');
const paymentTemplate = require('../../templates/mygov/model-tenancy-payment-dates');
const summaryOneTemplate = require('../../templates/mygov/model-tenancy-summary-1');
const summaryTwoExcludedTemplate = require('../../templates/mygov/model-tenancy-summary-2-excluded');
const summaryTwoTemplate = require('../../templates/mygov/model-tenancy-summary-2');

const confirmDefaultModal = `<div class="modal">
    <div class="modal__overlay"></div>
    <div class="modal__dialog">

        <a href="#" id="js-modal-close" class="modal__close" ds_button  ds_button--icon-only  ds_button--small  ds_button--cancel">
            <span class="visually-hidden">Close</span>
            <svg class="ds_icon" aria-hidden="true" role="img"><use href="/assets/images/icons/icons.stack.svg#close"></use></svg>
        </a>

        <h2 class="modal__title">Reset to recommended text?</h2>
        <p class="modal__body">Note: You will lose all of your custom text and will revert to the recommended term provided.</p>
        <button class="ds_no-margin  ds_button  ds_button--small" id="js-modal-continue">Continue</button>
        <button class="ds_no-margin  ds_button--cancel  ds_button  ds_button--small" id="js-modal-cancel">Cancel</button>
    </div>
</div>`;

$('form').each(function() {
    this.reset();
});

const modelTenancyForm = {
    form: new MultiPageForm({
        formId: 'model-tenancy-form',
        formSections: formSections,
        formMapping: formMapping,
        formObject: formObject,
        formEvents: {
            updateSummary1: function () {
                const summaryContainer = document.querySelector('#summary-container-1');
                const html = summaryOneTemplate.render(modelTenancyForm.form.settings.formObject);
                summaryContainer.innerHTML = html;
                commonHousing.summaryAccordion(summaryContainer);
                window.DS.tracking.init(summaryContainer);
            },
            updateSummary2: function () {
                const summaryContainer2 = document.querySelector('#summary-container-2');
                const summaryContainer2Excluded = document.querySelector('#summary-container-2-excluded');

                const summaryObject = JSON.parse(JSON.stringify(modelTenancyForm.form.settings.formObject));

                summaryObject.hasTenants = Object.values(summaryObject.tenants)[0].name;
                summaryObject.hasLandlords = Object.values(summaryObject.landlords)[0].name;
                summaryObject.mandatory2 = true;
                const mandatoryHtml = mandatoryTemplate.render(summaryObject);

                const extraTermsHtml = summaryTwoTemplate.render(summaryObject);
                const excludedHtml = summaryTwoExcludedTemplate.render(summaryObject);
                summaryContainer2.innerHTML = mandatoryHtml + extraTermsHtml;
                summaryContainer2Excluded.innerHTML = excludedHtml;
                commonHousing.summaryAccordion(summaryContainer2);
                commonHousing.summaryAccordion(summaryContainer2Excluded);

                window.DS.tracking.init(summaryContainer2);
                window.DS.tracking.init(summaryContainer2Excluded);
            },
            updateMandatoryTerms: function () {
                const mandatoryTermsContainer = document.querySelector('#mandatory-terms-container');
                const summaryObject = JSON.parse(JSON.stringify(modelTenancyForm.form.settings.formObject));

                summaryObject.hasTenants = Object.values(summaryObject.tenants)[0].name;
                summaryObject.hasLandlords = Object.values(summaryObject.landlords)[0].name;
                const html = mandatoryTemplate.render(summaryObject);

                mandatoryTermsContainer.innerHTML = html + '</div>';
                commonHousing.summaryAccordion(mandatoryTermsContainer);
                window.DS.tracking.init(mandatoryTermsContainer);
            }
        },
        modifiers: [{
            pattern: new RegExp(/\.postcode$/),
            transformFunction: function () {
                return arguments[0].toUpperCase();
            }
        }],
        pageNavFunction: pageNavFunction,
        pageNavTemplate: housingFormPageNavTemplate
    }),

    downloadCount: { word: 0, pdf: 0 },

    init: function () {
        // append form template
        const formTemplateContainer = document.querySelector('#form-container');
        if (!formTemplateContainer) {
            return false;
        }
        const overviewContent = formTemplateContainer.innerHTML;

        this.recaptchaSitekey = document.getElementById('recaptchaSitekey').value;
        this.recaptchaEnabled = document.getElementById('recaptchaEnabled').value === 'true';
        formTemplateContainer.innerHTML = formTemplate.render({
            tenants: true,
            iconsFile: bloomreachWebfile('/assets/images/icons/icons.stack.svg'),
            webfilesPath: bloomreachWebfile(),
            assetsPath: document.getElementById('site-root-path').value.replace('mygov', '') + 'assets',
            recaptchaEnabled: this.recaptchaEnabled,
            recaptchaSitekey: this.recaptchaSitekey
        });
        formTemplateContainer.querySelector('#overview').innerHTML = overviewContent;

        commonForms.appendCaptchaScript();

        feedback.init();
        this.getDefaultText();
        commonHousing.setManualLinkSections();
        this.setupExtraTerms();
        this.setupRepeatingSections();
        this.setupDatePickers();
        this.setupValidations();
        this.setupPostcodeLookups();
        this.setupPaymentDatePickers();
        if (this.recaptchaEnabled) {
            commonForms.setupRecaptcha();
        }
        modelTenancyForm.form.validateStep = modelTenancyForm.validateStep;

        new EditableTable({ // NOSONAR
            tableContainer: document.querySelector('#facilities-table'),
            dataPath: 'facilities',
            fields: [{
                title: 'Facility name',
                slug: 'name',
                validation: 'requiredField'
            }, {
                title: 'Type',
                slug: 'type',
                validation: 'requiredField',
                options: [
                    {displayName: '', value: ''},
                    {displayName: 'Included', value: 'INCLUDED'},
                    {displayName: 'Excluded', value: 'EXCLUDED'},
                    {displayName: 'Shared', value: 'SHARED'}
                ]
            }],
            addText: 'Add area or facility'
        });

        new EditableTable({ // NOSONAR
            tableContainer: document.querySelector('#services-table'),
            dataPath: 'servicesIncludedInRent',
            fields: [{
                title: 'Service name',
                slug: 'name',
                validation: 'requiredField'
            }, {
                title: 'Amount (per rent payment)',
                slug: 'value',
                format: 'currency',
                validation: 'validCurrency requiredField'
            }],
            addText: 'Add a service'
        });

        new EditableTable({ // NOSONAR
            tableContainer: document.querySelector('#letting-agent-services-table'),
            dataPath: 'servicesProvidedByLettingAgent',
            fields: [{
                title: 'Service name',
                slug: 'name',
                validation: 'requiredField'
            }, {
                title: 'Is the letting agent the first point of contact for this service?',
                slug: 'lettingAgentIsFirstContact',
                type: 'radio',
                options: [
                    {displayName: 'Yes', value: 'YES'},
                    {displayName: 'No', value: 'NO'}
                ],
                validation: 'requiredRadio'
            }],
            addText: 'Add a service'
        });

        new EditableTable({ // NOSONAR
            tableContainer: document.querySelector('#letting-agent-other-services-table'),
            dataPath: 'servicesLettingAgentIsFirstContactFor',
            fields: [{
                title: 'Service name',
                slug: 'name',
                validation: 'requiredField'
            }],
            addText: 'Add a service'
        });
    },

    getDefaultText: function(section, termSection){
        $.ajax('/service/housing/model-tenancy/template')
            .done(function(data){
                if (section && termSection) {
                    modelTenancyForm.form.settings.formObject[termSection][section] = data[termSection][section];
                    $(`[data-term-title=${section}]`).val(data[termSection][section]).trigger('change');
                } else {
                    modelTenancyForm.form.settings.formObject = data;
                    modelTenancyForm.form.settings.formObject.rentPaymentScheduleObject = {};
                }
            })
            .always(function(){
                if (!section){
                    modelTenancyForm.form.init();
                }
                modelTenancyForm.form.settings.formObject.tenants = { 'tenant-1': { name: '', email: '', telephone: '', address: {} } };
                modelTenancyForm.form.settings.formObject.landlords = { 'landlord-1': { name: '', email: '', telephone: '', address: {}, registrationNumber: '' } };
                modelTenancyForm.form.settings.formObject.additionalTerms = { 'additional-term-1': { title: '', content: '' } };
                modelTenancyForm.form.settings.formObject.excludedTerms = {};
                modelTenancyForm.form.settings.formObject.facilities = [];
                // Terms sections need to not be fully hidden initally to accurately resize them for content length
                $('#extra-terms, #extra-terms section').addClass('hidden--hard');
            });
    },



    setupExtraTerms: function () {
        const toggleOptionalTerm = function (fieldName, termSection, exclude) {
            modelTenancyForm.form.settings.formObject.excludedTerms = modelTenancyForm.form.settings.formObject.excludedTerms || {};

            if (exclude === true) {
                modelTenancyForm.form.settings.formObject.excludedTerms[fieldName] = modelTenancyForm.form.settings.formObject[termSection][fieldName];
                delete modelTenancyForm.form.settings.formObject[termSection][fieldName];
            } else {
                modelTenancyForm.form.settings.formObject[termSection][fieldName] = modelTenancyForm.form.settings.formObject.excludedTerms[fieldName];
                delete modelTenancyForm.form.settings.formObject.excludedTerms[fieldName];
            }
        };

        $('body').on('click', '.js-edit-term', function(event){
            event.preventDefault();
            $(this).parent().siblings('.js-term-content').attr('disabled', false).removeClass('remove')[0].focus();
            $(this).siblings('.js-reset-term, .js-save-term').removeClass('fully-hidden');
            $(this).addClass('fully-hidden');
        });

        $('body').on('change', '.js-include-term', function(){
            const currentStep = modelTenancyForm.form.currentStep;
            const currentSection = `[data-step="${currentStep.slug}"]`;
            const fieldName = $(currentSection).find('.js-term-content').attr('data-term-title');
            let termSection = 'optionalTerms';
            if (currentStep.section === 'must-include-terms'){
                termSection = 'mustIncludeTerms';
            }

            if (this.checked === true){
                currentStep.excluded = false;
                $(currentSection).find('.js-term-content').attr('disabled', true).removeClass('remove');
                $(currentSection).find('.js-edit-term').removeClass('fully-hidden');
                modelTenancyForm.form.updateFormNav();
                toggleOptionalTerm(fieldName, termSection, false);
            } else {
                currentStep.excluded = true;
                $(currentSection).find('.js-term-content').attr('disabled', true).addClass('remove');
                $(currentSection).find('.js-reset-term, .js-edit-term').addClass('fully-hidden');
                modelTenancyForm.form.updateFormNav();
                toggleOptionalTerm(fieldName, termSection, true);
            }
            $(currentSection).find('.js-save-term').addClass('fully-hidden');
        });

        $('body').on('click', '.js-reset-term', function(event){
            event.preventDefault();
            // open modal to confirm revert to default text
            $(this).parent().siblings('textarea').after(confirmDefaultModal);
            $('.modal')[0].focus();

            $('#js-modal-cancel, #js-modal-close, .modal__overlay').on('click', function(){
                $('.modal').remove();
            });

            const resetButton = this;

            $('#js-modal-continue').on('click', function(){
                const currentTextArea = $(resetButton).parent().siblings('textarea')[0];
                const sectionTitle = currentTextArea.getAttribute('data-term-title');

                // find out if we're in the optionalTerms or the mustIncludeTerms
                const currentStep = modelTenancyForm.form.currentStep;
                let termSection = 'optionalTerms';
                if (currentStep.section === 'must-include-terms'){
                    termSection = 'mustIncludeTerms';
                }
                modelTenancyForm.getDefaultText(sectionTitle, termSection);

                $(currentTextArea).attr('disabled', true).removeClass('remove');
                $(resetButton).siblings('.js-save-term, .js-edit-term').addClass('fully-hidden');
                $(resetButton).addClass('fully-hidden');
                $('.modal').remove();
            });
        });

        $('body').on('click', '.js-save-term', function(event){
            event.preventDefault();
            $(this).parent().siblings('textarea').attr('disabled', true);
            $(this).siblings('.js-edit-term').removeClass('fully-hidden');
            $(this).addClass('fully-hidden');
        });

        // make textareas resize to fit content for terms
        $('#extra-terms textarea, #must-include-terms textarea').each(function(){
            // set initial height
            const contentHeight = $(this)[0].scrollHeight;
            $(this).height(contentHeight);

            $(this).on('paste input change', function () {
                const borderTop = parseFloat($(this).css('borderTopWidth'));
                const borderBottom = parseFloat($(this).css('borderBottomWidth'));

                if ($(this).outerHeight() > this.scrollHeight) {
                    $(this).height('84px');
                }
                while ($(this).outerHeight() < this.scrollHeight + borderTop + borderBottom) {
                    $(this).height($(this).height() + 1);
                }
            });

            $(this).on('keypress', function() {
                modelTenancyForm.form.settings.formObject.editedTerms = modelTenancyForm.form.settings.formObject.editedTerms || [];
                const termTitle = $(this).attr('data-term-title');

                if ($.inArray(termTitle, modelTenancyForm.form.settings.formObject.editedTerms) < 0) {
                    modelTenancyForm.form.settings.formObject.editedTerms.push(termTitle);
                }
            });
        });
    },

    setupRepeatingSections: function () {
        const sections = [
            {
                name: 'tenants',
                nameInput: '.js-tenant-name',
                container: '.js-tenants-container',
                template: require('../../templates/tenant-html'),
                slug: 'tenant',
                stepTitle: 'Tenant',
                guarantor: true,
                fieldMappings: function(number) {
                    const fieldMappings = {};
                    fieldMappings['tenants[\'tenant-' + number + '\'].name'] = `#tenant-${number}-name`;
                    fieldMappings['tenants[\'tenant-' + number + '\'].email'] = `#tenant-${number}-email`;
                    fieldMappings['tenants[\'tenant-' + number + '\'].telephone'] = `#tenant-${number}-phone`;
                    fieldMappings[`tenants[tenant-${number}].address`] = new PostcodeLookup(document.getElementById(`tenant-${number}-postcode-lookup`));
                    fieldMappings['tenants[\'tenant-' + number + '\'].address.building'] = `#tenant-${number}-address-building`;
                    fieldMappings['tenants[\'tenant-' + number + '\'].address.street'] = `#tenant-${number}-address-street`;
                    fieldMappings['tenants[\'tenant-' + number + '\'].address.town'] = `#tenant-${number}-address-town`;
                    fieldMappings['tenants[\'tenant-' + number + '\'].address.region'] = `#tenant-${number}-address-region`;
                    fieldMappings['tenants[\'tenant-' + number + '\'].address.postcode'] = `#tenant-${number}-postcode`;
                    fieldMappings['tenants[\'tenant-' + number + '\'].guarantor.name'] = `#guarantor-${number}-name`;
                    fieldMappings['tenants[\'tenant-' + number + '\'].hasGuarantor'] = `[name="guarantor-${number}-query"]`;
                    fieldMappings[`tenants[tenant-${number}].guarantor.address`] = new PostcodeLookup(document.getElementById(`guarantor-${number}-postcode-lookup`));
                    fieldMappings['tenants[\'tenant-' + number + '\'].guarantor.address.building'] = `#guarantor-${number}-address-building`;
                    fieldMappings['tenants[\'tenant-' + number + '\'].guarantor.address.street'] = `#guarantor-${number}-address-street`;
                    fieldMappings['tenants[\'tenant-' + number + '\'].guarantor.address.town'] = `#guarantor-${number}-address-town`;
                    fieldMappings['tenants[\'tenant-' + number + '\'].guarantor.address.region'] = `#guarantor-${number}-address-region`;
                    fieldMappings['tenants[\'tenant-' + number + '\'].guarantor.address.postcode'] = `#guarantor-${number}-postcode`;
                    return fieldMappings;
                }
            },
            {
                name: 'landlords',
                group: 'managing-the-property',
                nameInput: '.js-landlord-name',
                container: '.js-landlords-container',
                template: require('../../templates/landlord-html'),
                slug: 'landlord',
                stepTitle: 'Landlord',
                fieldMappings: function(number) {
                    const fieldMappings = {};
                    fieldMappings['landlords[\'landlord-' + number + '\'].name'] = `#landlord-${number}-name`;
                    fieldMappings['landlords[\'landlord-' + number + '\'].email'] = `#landlord-${number}-email`;
                    fieldMappings['landlords[\'landlord-' + number + '\'].telephone'] = `#landlord-${number}-phone`;
                    fieldMappings[`landlords[landlord-${number}].address`] = new PostcodeLookup(document.getElementById(`landlord-${number}-postcode-lookup`));
                    fieldMappings['landlords[\'landlord-' + number + '\'].address.building'] = `#landlord-${number}-address-building`;
                    fieldMappings['landlords[\'landlord-' + number + '\'].address.street'] = `#landlord-${number}-address-street`;
                    fieldMappings['landlords[\'landlord-' + number + '\'].address.town'] = `#landlord-${number}-address-town`;
                    fieldMappings['landlords[\'landlord-' + number + '\'].address.region'] = `#landlord-${number}-address-region`;
                    fieldMappings['landlords[\'landlord-' + number + '\'].address.postcode'] = `#landlord-${number}-postcode`;
                    fieldMappings['landlords[\'landlord-' + number + '\'].registrationNumber'] = `#landlord-${number}-registration`;
                    return fieldMappings;
                }
            },
            {
                name: 'additional-terms',
                container: '.js-terms-container',
                slug: 'additional-term',
                template: require('../../templates/mygov/mta-additional-term-html'),
                stepTitle: 'Additional terms',
                fieldMappings: function(number) {
                    const fieldMappings = {};
                    fieldMappings['additionalTerms[\'additional-term-' + number + '\'].title'] = `#additional-term-${number}-title`;
                    fieldMappings['additionalTerms[\'additional-term-' + number + '\'].content'] = `#additional-term-${number}-content`;
                    return fieldMappings;
                }
            }
        ];

        commonHousing.setupRepeatingSections(sections, this.form);

        // additional page change event:
        // if the summary page has been visited AND we're on a page before the summary page, show the "back to summary" button
        $('#page-nav').on('change', function(){
            const summaryStep = modelTenancyForm.form.getStep('slug', 'part-1-summary');
            const formSectionsFlattened = modelTenancyForm.form.flattenedSections(true);

            const thisStepIndex = formSectionsFlattened.findIndex(function (element) {
                return element.slug === modelTenancyForm.form.currentStep.slug;
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
        const startDatePicker = new DSDatePicker(document.getElementById('tenancy-start-date-picker'), {minDate: new Date(), imagePath: bloomreachWebfile('/assets/images/icons/')});
        const hmoDatePicker = new DSDatePicker(document.getElementById('hmo-expiry-date-picker'), {minDate: new Date(), imagePath: bloomreachWebfile('/assets/images/icons/')});
        const firstPaymentDatePicker = new DSDatePicker(document.getElementById('first-payment-date-picker'), {minDate: new Date(), imagePath: bloomreachWebfile('/assets/images/icons/')});
        const firstPaymentEndDatePicker = new DSDatePicker(document.getElementById('first-payment-end-date-picker'), {minDate: new Date(), imagePath: bloomreachWebfile('/assets/images/icons/')});

        startDatePicker.init();
        hmoDatePicker.init();
        firstPaymentDatePicker.init();
        firstPaymentEndDatePicker.init();
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
        const rpzComplete = function(rpzData){
            modelTenancyForm.form.settings.formObject.inRentPressureZone = rpzData.inRentPressureZone;
        };

        const notScottishMessage = 'The postcode you\'ve entered is not a Scottish postcode.' +
        ' You can only create a Scottish Government Model Tenancy Agreement for' +
        ' homes in Scotland. <a href="https://www.gov.uk/tenancy-agreements-a-guide-for' +
        '-landlords" target="_blank">Find out more about UK tenancy agreements' +
        ' (opens in a new window)</a>.';

        modelTenancyForm.form.settings.formMapping.propertyAddress = new PostcodeLookup(
            document.getElementById('property-postcode-lookup'),
            {
                rpz: true, // NOSONAR
                rpzComplete: rpzComplete,
                notScottishMessage: notScottishMessage,
                readOnly: true
            }
        );
        modelTenancyForm.form.settings.formMapping['lettingAgent.address'] = new PostcodeLookup(document.getElementById('letting-agent-postcode-lookup'));
    },

    setupPaymentDatePickers: function() {
        const that = this;
        // On choosing a payment frequency, display more options for when during each period rent is due
        $('#tenancy-payment-frequency').on('change', function(){
            if (!$(this).val()) {
                return;
            }

            const paymentData  = {};
            paymentData[$(this).val()] = true;

            const paymentHtml = paymentTemplate.render(paymentData);
            $('#tenancy-payment-dates').html(paymentHtml);

            // Reset the form's data on what the schedule is
            that.form.settings.formObject.rentPaymentScheduleObject = {};

            // Map new fields to formObject
            const fieldMappings = {
                'rentPaymentScheduleObject.day': '#tenancy-payment-frequency-day',
                'rentPaymentScheduleObject.week': '#tenancy-payment-frequency-week',
                'rentPaymentScheduleObject.date': '#tenancy-payment-frequency-date'
            };

            for (const key in fieldMappings) {
                if (!fieldMappings.hasOwnProperty(key)) { continue; }
                that.form.mapField(key, fieldMappings[key]);
            }
        });
    },

    validateStep: function () {
        return commonForms.validateStep(modelTenancyForm.form.currentStep);
    },

    prepareFormDataForPost: function (formData) {
        // 1. build up the guarantors
        const tenants = formData.tenants,
            guarantors = [];

        for (const key in tenants) {
            const tenant = tenants[key];

            if (tenant.hasGuarantor === 'guarantor-yes') {
                let guarantorMatch = false;
                tenant.guarantor.address = tenant.guarantor.address || {};

                for (let i = 0, il = guarantors.length; i < il; i++) {
                    const guarantor = guarantors[i];

                    if (guarantor.name === tenant.guarantor.name &&
                        guarantor.address.street === tenant.guarantor.address.street
                    ) {
                        guarantor.tenantNames.push(tenant.name);
                        guarantorMatch = true;
                    }
                }

                if (!guarantorMatch) {
                    guarantors.push({
                        name: tenant.guarantor.name,
                        address: {
                            building: tenant.guarantor.address.building,
                            street: tenant.guarantor.address.street,
                            town: tenant.guarantor.address.town,
                            region: tenant.guarantor.address.region,
                            postcode: tenant.guarantor.address.postcode
                        },
                        tenantNames: [ tenant.name ]
                    });
                }
            }
            delete tenant.guarantor;
            delete tenant.hasGuarantor;
            delete tenant.hasGuarantor_text;
        }

        if (guarantors.length > 0) {
            formData.guarantors = guarantors;
        }

        // 2. Format dates to YYYY-MM-DD
        const dateFields = ['tenancyStartDate', 'firstPaymentDate', 'firstPaymentPeriodEnd', 'hmoRegistrationExpiryDate'];

        for (const field of dateFields) {
            let value = formData[field];
            if (value === null || value.split(' ').join('') === ''){
                continue;
            }

            value = value.trim();

            let dateRegex = new RegExp(/\d{1,2}\/\d{1,2}\/\d{4}/);

            if (value.match(dateRegex)) {
                let dateAsArray = value.trim().replace(/-/g, '/').split('/');

                // check date is a valid date
                const day = dateAsArray[0];
                const month = dateAsArray[1];
                const year = dateAsArray[2];

                if (!isNaN(Date.parse(`${month}/${day}/${year}`))) {
                    formData[field] = `${year}-${commonForms.leadingZeroes(month, 2)}-${commonForms.leadingZeroes(day, 2)}`;
                } else {
                    formData[field] = null;
                }
            } else {
                formData[field] = null;
            }
        }

        // 3. Add value from 'other' option in property type field if selected
        if ($('#building-other-query').is(':checked')) {
            formData.propertyType = $('#building-other').val();
        } else {
            formData.propertyType = $('[name="property-building"]:checked + label').text();
        }

        // 4. Shred the facilities into expected places
        formData.includedAreasOrFacilities = formData.includedAreasOrFacilities || [];
        formData.excludedAreasFacilities = formData.excludedAreasFacilities || [];
        formData.sharedFacilities = formData.sharedFacilities || [];
        for (let j = 0, jl = formData.facilities.length; j < jl; j++) {
            const facility = formData.facilities[j];

            if (facility.type === 'INCLUDED') {
                formData.includedAreasOrFacilities.push(facility.name);
            } else if (facility.type === 'EXCLUDED') {
                formData.excludedAreasFacilities.push(facility.name);
            } else if (facility.type === 'SHARED') {
                formData.sharedFacilities.push(facility.name);
            }
        }

        // 5. Format tenants, landlords, additional and excluded terms into arrays
        formData.tenants = commonForms.objectValues(formData.tenants);
        formData.landlords = commonForms.objectValues(formData.landlords);
        formData.additionalTerms = commonForms.objectValues(formData.additionalTerms);
        formData.excludedTerms = commonForms.objectKeys(formData.excludedTerms);

        // 6. Format rent payment schedule into a sentence string
        const frequency = formData.rentPaymentFrequency;
        const schedule = formData.rentPaymentScheduleObject;

        let scheduleString = '';
        const day = schedule.day || '';
        const week = schedule.week || '';
        const date = schedule.date || '';

        if (commonForms.objectKeys(schedule).length > 0){
            switch (frequency) {
            case 'WEEKLY':
                scheduleString = day;
                break;
            case 'FORTNIGHTLY':
            case 'EVERY_FOUR_WEEKS':
                scheduleString = `{$day} of the ${week.toLowerCase()}`;
                break;
            case 'CALENDAR_MONTH':
                scheduleString = `the ${date} of the month`;
                break;
            case 'QUARTERLY':
            case 'EVERY_SIX_MONTHS':
                scheduleString = `the ${date}`;
                break;
            default:
                break;
            }
        }

        formData.rentPaymentSchedule = scheduleString;

        // 7. Remove letting agent details if user has said there is none
        if (formData.hasLettingAgent === 'no'){
            formData.lettingAgent = null;
        }

        // 8. Remove HMO info if the user has said the property is not HMO
        if (formData.hmoProperty === 'false') {
            formData.hmo24ContactNumber = null;
            formData.hmoRegistrationExpiryDate = null;
            formData.hmoRenewalApplicationSubmitted = false;
        }
        return commonForms.trimObjectValues(formData);
    }
};

function pageNavFunction () {
    // which extra buttons do we show?
    const pageNavData = {};
    const currentStep = modelTenancyForm.form.currentStep;

    pageNavData.backToDetails = currentStep.slug === 'part-2-summary';
    pageNavData.startPage = currentStep.slug === 'summary';

    const stepIsLastInSection = currentStep.slug === $(`[data-step="${currentStep.section}"] section:last`).attr('data-step');

    if (stepIsLastInSection) {
        pageNavData.addLandlord = (currentStep.section === 'managing-the-property' && currentStep.slug !== 'letting-agent');
        pageNavData.addTenant = currentStep.section === 'tenants';
        pageNavData.addTerm = currentStep.section === 'additional-terms';
    }

    return pageNavData;
}

$('.multi-page-form').on('click', '.js-download-file', function (event) {
    event.preventDefault();

    const documentDownloadForm = $('#mta-document-download');
    documentDownloadForm.find('input[name="type"]').val($(this).closest('.js-document-container').attr('data-documenttype'));

    const formData = JSON.parse(JSON.stringify(modelTenancyForm.form.settings.formObject));
    const data = modelTenancyForm.prepareFormDataForPost(formData);

    if (modelTenancyForm.recaptchaEnabled) {
        data.recaptcha = grecaptcha.getResponse();
    }

    // analytics tracking
    const downloadType = documentDownloadForm.find('input[name=type]').val();
    modelTenancyForm.downloadCount[downloadType.toLowerCase()]++;

    const includedTerms = commonForms.objectKeys(data.optionalTerms);
    const excludedTerms = data.excludedTerms || [];
    const editedTerms = data.editedTerms || [];
    delete data.editedTerms;

    commonForms.track({
        'event': 'formSubmitted',
        'formId': 'model-tenancy-form',
        'downloadType': downloadType,
        'downloadCount': modelTenancyForm.downloadCount,
        'tenantNumber': data.tenants.length,
        'landlordNumber': data.landlords.length,
        'additionalTermNumber': data.additionalTerms.length,
        'includedTerms': includedTerms,
        'excludedTerms': excludedTerms,
        'editedTerms': editedTerms
    });

    // Set hidden data field to have value of JSON data
    documentDownloadForm.find('input[name="data"]').val(encodeURIComponent(JSON.stringify(data)));
    documentDownloadForm.trigger('submit');
    if (modelTenancyForm.recaptchaEnabled) {
        expireRecaptcha();
    }
});

window.format = modelTenancyForm;
window.format.init();

export default modelTenancyForm;
