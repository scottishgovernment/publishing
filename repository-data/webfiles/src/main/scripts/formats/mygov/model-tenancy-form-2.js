import AddAnother from '../../components/add-another';
import bloomreachWebfile from '../../tools/bloomreach-webfile';
import commonForms from '../../tools/forms';
import formMapping from '../../components/mygov/housing-forms/model-tenancy-mapping';
import MultiPageForm from "../../components/multi-page-form-2";
import PostcodeLookup from '../../components/postcode-lookup';
import PromiseRequest from '@scottish-government/design-system/src/base/tools/promise-request/promise-request';
import temporaryFocus from '@scottish-government/design-system/src/base/tools/temporary-focus/temporary-focus';

// todo: inline/embedded styles for the form layout with subgrid
// todo: document with comments
// todo: field mapping for payment method 'other'
// todo: field mapping for rent frequency detail

const additionalTermsDeleteTemplate = require('../../templates/mygov/housing-form-subprocesses/additional-terms-delete');
const additionalTermsFormTemplate = require('../../templates/mygov/housing-form-subprocesses/additional-terms-form');
const additionalTermsSummaryTemplate = require('../../templates/mygov/housing-form-subprocesses/additional-terms-summary');
const editableTermTemplate = require('../../templates/mygov/housing-form-subprocesses/editable-term');
const formTemplate = require('../../templates/mygov/model-tenancy-form-2');
const landlordsDeleteTemplate = require('../../templates/mygov/housing-form-subprocesses/landlords-delete');
const landlordsFormTemplate = require('../../templates/mygov/housing-form-subprocesses/landlords-form');
const landlordsSummaryTemplate = require('../../templates/mygov/housing-form-subprocesses/landlords-summary');
const lettingAgentServicesDeleteTemplate = require('../../templates/mygov/housing-form-subprocesses/letting-agent-services-delete');
const lettingAgentServicesFormTemplate = require('../../templates/mygov/housing-form-subprocesses/letting-agent-services-form');
const lettingAgentServicesSummaryTemplate = require('../../templates/mygov/housing-form-subprocesses/letting-agent-services-summary');
const mandatoryTemplate = require('../../templates/mygov/model-tenancy-mandatory-2024');
const rentFrequencyDetailsTemplate = require('../../templates/mygov/model-tenancy-rent-frequency-detail');
const servicesDeleteTemplate = require('../../templates/mygov/housing-form-subprocesses/services-delete');
const servicesFormTemplate = require('../../templates/mygov/housing-form-subprocesses/services-form');
const servicesSummaryTemplate = require('../../templates/mygov/housing-form-subprocesses/services-summary');
const summaryTemplate = require('../../templates/mygov/model-tenancy-form-2-summary');
const tenantsDeleteTemplate = require('../../templates/mygov/housing-form-subprocesses/tenants-delete');
const tenantsFormTemplate = require('../../templates/mygov/housing-form-subprocesses/tenants-form');
const tenantsSummaryTemplate = require('../../templates/mygov/housing-form-subprocesses/tenants-summary');
const whatsIncludedDeleteTemplate = require('../../templates/mygov/housing-form-subprocesses/whats-included-delete');
const whatsIncludedFormTemplate = require('../../templates/mygov/housing-form-subprocesses/whats-included-form');
const whatsIncludedSummaryTemplate = require('../../templates/mygov/housing-form-subprocesses/whats-included-summary');

const modelTenancyForm = {
    form: new MultiPageForm({
        formEvents: {
            /**
             * Enable or disable the HMO pages of the form based on the user's response to the
             * 'property is a HMO' question
             */
            enableOrDisableHMOPages: () => {
                const isHMOProperty = modelTenancyForm.form.formDataObject.hmoProperty === 'true';

                const hmoDetailsPage = modelTenancyForm.form.getPage('slug', 'hmo-details');
                hmoDetailsPage.disabled = !isHMOProperty;
            },
            /**
             * Enable or disable the lettign agent pages of the form based on the user's response to
             * the 'a letting agent looks after thei property' question
             */
            enableOrDisableLettingAgentPages: () => {
                const hasLettingAgent = modelTenancyForm.form.formDataObject.hasLettingAgent === 'letting-agent-yes'

                const lettingAgentDetailsPage = modelTenancyForm.form.getPage('slug', 'letting-agent-details');
                const lettingAgentServicesPage = modelTenancyForm.form.getPage('slug', 'letting-agent-services');

                lettingAgentDetailsPage.disabled = !hasLettingAgent;
                lettingAgentServicesPage.disabled = !hasLettingAgent;
            },
            /**
             * Render a client-side template for mandatory terms populated with relevant data
             * entered into the form so far
             */
            renderMandatoryTerms: function () {
                const mandatoryTermsContainer = document.querySelector('#mandatory-terms-container');
                const summaryObject = JSON.parse(JSON.stringify(modelTenancyForm.form.formDataObject));

                summaryObject.hasTenants = Object.values(summaryObject.tenants).length;
                summaryObject.hasLandlords = Object.values(summaryObject.landlords).length;

                mandatoryTermsContainer.innerHTML = mandatoryTemplate.render(summaryObject);

                // todo: services provided by letting agent etc

                // commonHousing.summaryAccordion(mandatoryTermsContainer);
                // window.DS.tracking.init(mandatoryTermsContainer);

                // modelTenancyForm.renderEditableTerm();
            },
            /**
             * Call the form's renderEditableTerm function
             */
            renderEditableTerm: () => {
                modelTenancyForm.renderEditableTerm();
            },
            /**
             * Render a client-side template for the form summary populated with data entered into
             * the form
             * Set an event listener to handle clicks on action buttons in the summary list
             */
            renderSummary: () => {
                const summaryContainer = document.querySelector('#summary');
                summaryContainer.innerHTML = summaryTemplate.render(modelTenancyForm.form.formDataObject);

                const mandatoryTermsContainer = summaryContainer.querySelector('#mandatory-terms-container-summary');
                const summaryObject = JSON.parse(JSON.stringify(modelTenancyForm.form.formDataObject));
                summaryObject.hasTenants = Object.values(summaryObject.tenants).length;
                summaryObject.hasLandlords = Object.values(summaryObject.landlords).length;
                summaryObject.mandatory2 = true;
                mandatoryTermsContainer.innerHTML = mandatoryTemplate.render(summaryObject);

                summaryContainer.addEventListener('click', event => {
                    if (event.target.classList.contains('js-change')) {
                        const pageToNavigateTo = event.target.dataset.slug;
                        modelTenancyForm.form.goToPage(modelTenancyForm.form.getPage('slug', pageToNavigateTo));
                    }
                });
            }
        },
        formMapping: formMapping,
        modifiers: [{
            pattern: new RegExp(/\.postcode$/),
            transformFunction: function () {
                return arguments[0].toUpperCase();
            }
        }],
        sections: [
            {
                slug: 'property',
                title: 'About the property',
                pages: [
                    {
                        slug: 'property-address',
                        title: 'What\'s the property address',
                    },
                    {
                        headerType: 'legend',
                        slug: 'property-type',
                        title: 'What type of property is it?'
                    },
                    {
                        slug: 'property-included',
                        title: 'What\'s included in the tenancy agreement?',
                        titleShort: 'What\'s included?',
                        triggerEventOnEntry: 'renderWhatsIncludedAddAnother'
                    },
                    {
                        headerType: 'legend',
                        slug: 'property-furnished',
                        title: 'Is the property furnished?'
                    },
                    {
                        slug: 'property-hmo',
                        title: 'Is the property a House in Multiple Occupation (HMO)?',
                        titleShort: 'Is the property a HMO?',
                        triggerEventOnExit: 'enableOrDisableHMOPages'
                    },
                    {
                        disabled: true,
                        slug: 'hmo-details',
                        title: 'HMO details',
                    }
                ]
            },
            {
                slug: 'tenancy',
                title: 'About the tenancy',
                pages: [
                    {
                        slug: 'tenancy-start',
                        title: 'When does the tenancy start?'
                    },
                    {
                        slug: 'tenancy-payment-method',
                        title: 'How will the tenant pay rent?'
                    },
                    {
                        slug: 'tenancy-payment-frequency',
                        title: 'How often will the tenant pay rent?'
                    },
                    {
                        slug: 'tenancy-payment-frequency-details',
                        title: 'Rent frequency details',
                        triggerEventOnEntry: 'renderRentFrequencyDetails'
                    },
                    {
                        slug: 'tenancy-payment-amount',
                        title: 'How much will each of these rent payments be?'
                    },
                    {
                        slug: 'tenancy-payment-type',
                        title: 'Will they pay in advance or in arrears?'
                    },
                    {
                        slug: 'tenancy-services',
                        title: 'What services are included in the rent?',
                        triggerEventOnEntry: 'renderServicesAddAnother'
                    },
                    {
                        slug: 'tenancy-first-payment',
                        title: 'First rent payment'
                    },
                    {
                        slug: 'tenancy-deposit',
                        title: 'Deposit'
                    },
                    {
                        slug: 'tenancy-communication-agreement',
                        title: 'How will you and the tenant(s) communicate?',
                    }
                ]
            },
            {
                slug: 'managing',
                title: 'Managing the property',
                pages: [
                    {
                        slug: 'letting-agent',
                        title: 'Is a letting agent managing the property?',
                        triggerEventOnExit: 'enableOrDisableLettingAgentPages'
                    },
                    {
                        disabled: true,
                        slug: 'letting-agent-details',
                        title: 'Letting agent details',
                    },
                    {
                        disabled: true,
                        slug: 'letting-agent-services',
                        title: 'Letting agent services',
                        triggerEventOnEntry: 'renderLettingAgentServicesAddAnother'
                    },
                    {
                        slug: 'landlords',
                        title: 'Landlord details',
                        triggerEventOnEntry: 'renderLandlordsAddAnother'
                    }
                ]
            },
            {
                slug: 'tenants',
                title: 'About the tenants',
                pages: [
                    {
                        slug: 'tenant-details',
                        title: 'Tenant details',
                        triggerEventOnEntry: 'renderTenantsAddAnother'
                    }
                ]
            },
            {
                slug: 'mandatory-terms',
                title: 'Review mandatory terms',
                pages: [
                    {
                        slug: 'must-include-terms',
                        title: 'Must-include terms',
                        triggerEventOnEntry: 'renderMandatoryTerms'
                    },
                    {
                        slug: 'notification-about-other-tenants',
                        title: 'Notification about other tenants',
                        triggerEventOnEntry: 'renderEditableTerm'
                    },
                    {
                        slug: 'ending-the-tenancy',
                        title: 'Ending the tenancy',
                        triggerEventOnEntry: 'renderEditableTerm'
                    }
                ]
            },
            {
                slug: 'optional-terms',
                title: 'Add optional terms',
                pages: [
                    {
                        slug: 'extra-terms',
                        title: 'Extra terms'
                    },
                    {
                        slug: 'contents-and-condition',
                        title: 'Contents and condition',
                        triggerEventOnEntry: 'renderEditableTerm'
                    },
                    {
                        slug: 'local-authority-taxes-charges',
                        title: 'Local authority taxes/charges',
                        triggerEventOnEntry: 'renderEditableTerm'
                    },
                    {
                        slug: 'utilities',
                        title: 'Utilities',
                        triggerEventOnEntry: 'renderEditableTerm'
                    },
                    {
                        slug: 'alterations',
                        title: 'Alterations',
                        triggerEventOnEntry: 'renderEditableTerm'
                    },
                    {
                        slug: 'common-parts',
                        title: 'Common parts',
                        triggerEventOnEntry: 'renderEditableTerm'
                    },
                    {
                        slug: 'private-garden',
                        title: 'Private garden',
                        triggerEventOnEntry: 'renderEditableTerm'
                    },
                    {
                        slug: 'roof',
                        title: 'Roof',
                        triggerEventOnEntry: 'renderEditableTerm'
                    },
                    {
                        slug: 'bins-and-recycling',
                        title: 'Bins and recycling',
                        triggerEventOnEntry: 'renderEditableTerm'
                    },
                    {
                        slug: 'storage',
                        title: 'Storage',
                        triggerEventOnEntry: 'renderEditableTerm'
                    },
                    {
                        slug: 'dangerous-substances',
                        title: 'Dangerous substances',
                        triggerEventOnEntry: 'renderEditableTerm'
                    },
                    {
                        slug: 'pets',
                        title: 'Pets',
                        triggerEventOnEntry: 'renderEditableTerm'
                    },
                    {
                        slug: 'smoking',
                        title: 'Smoking',
                        triggerEventOnEntry: 'renderEditableTerm'
                    },
                    {
                        slug: 'additional-terms',
                        title: 'Additional terms',
                        triggerEventOnEntry: 'renderAdditionalTermsAddAnother'
                    }
                ]
            },
            {
                slug: 'review',
                title: 'Review',
                pages: [
                    {
                        showInTracker: false,
                        slug: 'review',
                        title: 'Review your tenancy agreement',
                        triggerEventOnEntry: 'renderSummary'
                    }
                ]
            },
            {
                slug: 'download',
                title: 'Download',
                pages: [
                    {
                        showInTracker: false,
                        slug: 'download',
                        title: 'Your tenancy agreement is ready'
                    }
                ]
            },
            {
                slug: 'confirmation',
                title: 'Confirmation',
                pages: [{
                    showInTracker: false,
                    slug: 'confirmation'
                }]
            },
        ]
    }),

    downloadCount: { word: 0, pdf: 0 },

    init: function () {
        this.recaptchaSitekey = document.getElementById('recaptchaSitekey').value;
        this.recaptchaEnabled = document.getElementById('recaptchaEnabled').value === 'true';

        commonForms.appendCaptchaScript();

        // // todo: temp until can get w/ xhr/promise
        // this.form.formDataObject = template;

        this.getDefaultText()
            // todo: this is then()
            .then(data => {
                this.form.formDataObject = JSON.parse(data.responseText);








                this.form.formDataObject2 = {
                    "recaptcha": "",
                    "tenants": [
                        {
                            "address": {
                                "building": "",
                                "postcode": "BN4 4NA",
                                "street": "1 BANANA ROAD",
                                "town": "BANANAS"
                            },
                            "email": "",
                            "name": "Banana Peterson",
                            "telephone": "0123 456 7890",
                            "guarantor": {
                                "name": "Banana Mackay",
                                "address": {
                                    "building": "",
                                    "postcode": "BN4 4NA",
                                    "street": "2 BANANA ROAD",
                                    "town": "BANANAS"
                                }
                            }
                        }
                    ],
                    "guarantors": [],
                    "lettingAgent": {
                        "name": "Banana Smith",
                        "telephone": "0123 456 7890",
                        "address": {
                            "building": "",
                            "postcode": "BN4 4NA",
                            "street": "3 BANANA ROAD",
                            "town": "BANANAS"
                        }
                    },
                    "hasLettingAgent": "letting-agent-yes",
                    "landlords": [
                        {
                            "address": {
                                "building": "",
                                "postcode": "BN4 4NA",
                                "street": "4 BANANA ROAD",
                                "town": "BANANAS"
                            },
                            "email": "",
                            "name": "Banana Rogers",
                            "registrationNumber": "",
                            "telephone": "0123 456 7890"
                        }
                    ],
                    "communicationsAgreement": "HARDCOPY",
                    "propertyAddress": {
                        "building": "",
                        "postcode": "BN4 4NA",
                        "street": "5 BANANA ROAD",
                        "town": "BANANAS"
                    },
                    "propertyType": "COTTAGE",
                    "furnishingType": "PARTLY_FURNISHED",
                    "inRentPressureZone": null,
                    "hmoProperty": "true",
                    "hmo24ContactNumber": "0123 456 7890",
                    "hmoRenewalApplicationSubmitted": true,
                    "hmoRegistrationExpiryDate": "22/10/2024",
                    "tenancyStartDate": "22/10/2024",
                    "rentAmount": "",
                    "rentPaymentFrequency": "EVERY_FOUR_WEEKS",
                    "rentPayableInAdvance": "true",
                    "firstPaymentDate": "21/10/2024",
                    "firstPaymentAmount": "123",
                    "firstPaymentPeriodEnd": "21/10/2024",
                    "rentPaymentDayOrDate": "",
                    "rentPaymentSchedule": "",
                    "rentPaymentMethod": "Bank transfer",
                    "servicesIncludedInRent": [
                        {
                            "name": "Window cleaning",
                            "value": "5"
                        }
                    ],
                    "servicesProvidedByLettingAgent": [],
                    "servicesLettingAgentIsFirstContactFor": [],
                    "includedAreasOrFacilities": [],
                    "excludedAreasFacilities": [],
                    "sharedFacilities": [],
                    "depositAmount": "132",
                    "tenancyDepositSchemeAdministrator": "Safedeposits Scotland",
                    "optionalTerms": {
                        "contentsAndConditions": "The Tenant agrees that the signed Inventory and Record of Condition, [attached as Schedule 1 to this Agreement/ which will be supplied to the Tenant no later than the start date of the tenancy] is a full and accurate record of the contents and condition of the Let Property at the start date of the tenancy.  The Tenant has a period of 7 days from the start date of the tenancy (set out above in the 'start date of the tenancy' section) to ensure that the Inventory and Record of Condition is correct and either 1) to tell the Landlord of any discrepancies in writing, after which the Inventory and Record of Condition will be amended as appropriate or 2) to take no action and, after the 7-day period has expired, the Tenant shall be deemed to be fully satisfied with the terms.\n\nThe Tenant agrees to replace or repair (or, at the option of the Landlord, to pay the reasonable cost of repairing or replacing) any of the contents which are destroyed, damaged, removed or lost during the tenancy, fair wear and tear excepted, where this was caused wilfully or negligently by the Tenant, anyone living with the Tenant or an invited visitor to the Let Property (see clause above on 'Reasonable care').  Items to be replaced by the Tenant will be replaced by items of equivalent value and quality. ",
                        "alterations": "The Tenant agrees not to make any alteration to the Let Property, its fixtures or fittings, nor to carry out any internal or external decoration without the prior written consent of the Landlord. ",
                        "commonParts": "In the case of a flatted Let Property, or any other Let Property having common parts the Tenant agrees, in conjunction with the other proprietors / occupiers, to sweep and clean the common stairway and to co-operate with other proprietors/properties in keeping the garden, back green or other communal areas clean and tidy. ",
                        "privateGarden": "The Tenant will maintain the garden in a reasonable manner. ",
                        "roof": "The Tenant is not permitted to access the roof without the Landlord's written consent, except in the case of an emergency. ",
                        "binsAndRecycling": "The Tenant agrees to dispose of or recycle all rubbish in an appropriate manner and at the appropriate time. Rubbish must not be placed anywhere in the common stair at any time.  The Tenant must take reasonable care to ensure that the rubbish is properly bagged or recycled in the appropriate container.  If rubbish is normally collected from the street, on the day of collection it should be put out by the time specified by the local authority.  Rubbish and recycling containers should be returned to their normal storage places as soon as possible after it has been collected.  The Tenant must comply with any local arrangements for the disposal of large items. ",
                        "storage": "Nothing belonging to the Tenant or anyone living with the Tenant or a visitor may be left or stored in the common stair if it causes a fire or safety hazard, or nuisance or annoyance to neighbours. ",
                        "dangerousSubstances": "The Tenant agrees to the normal and safe storage of any petroleum and/or gas, including liquid petroleum gas, for garden appliances (mowers etc.), barbecues or other commonly used household goods or appliances.  The Tenant must not store, keep or bring into the Let Property or any store, shed or garage any other flammable liquids, explosives or explosive gases which might reasonably be considered to be a fire hazard or otherwise dangerous to the Let Property or its occupants or the neighbours or the neighbour's property. ",
                        "pets": "The Tenant will not keep any animals or pets in the Let Property without the prior written consent of the Landlord. Any pet (where permitted) will be kept under supervision and control to ensure that it does not cause deterioration in the condition of the Let Property or common areas, nuisance either to neighbours or in the locality of the Let Property. ",
                        "smoking": "The Tenant agrees not to smoke, or to permit visitors to smoke tobacco or any other substance, in the Let Property, without the prior written consent of the Landlord.\n\nThe Tenant will not smoke in stairwells or any other common parts. "
                    },
                    "mustIncludeTerms": {},
                    "additionalTerms": [],
                    "excludedTerms": [],
                    "rentPaymentScheduleObject": {},
                    "facilities": [
                        {
                            "name": "Garden",
                            "type": "Included"
                        },
                        {
                            "name": "Garage",
                            "type": "Excluded"
                        },
                        {
                            "name": "Pool",
                            "type": "Shared"
                        },
                        {
                            "name": "Laundry room",
                            "type": "Included"
                        }
                    ],
                    "propertyType_text": "Cottage",
                    "furnishingType_text": "Part Furnished",
                    "hmoProperty_text": "Yes",
                    "rentPaymentFrequency_text": "Four weekly",
                    "rentPayableInAdvance_text": "Advance",
                    "rentPaymentMethod_text": "Bank transfer",
                    "tenancyDepositSchemeAdministrator_text": "Safedeposits Scotland",
                    "communicationsAgreement_text": "By paper — in post or in person",
                    "hasLettingAgent_text": "Yes",
                    "servicesLookedAfterByLettingAgent": [
                        {
                            "name": "Cleaner",
                            "value": "Yes"
                        },
                        {
                            "name": "Daily high five from the concierge",
                            "value": "No"
                        }
                    ]
                }

















                this.form.formDataObject.rentPaymentScheduleObject = {};

                this.form.formDataObject.facilities = this.form.formDataObject.facilities || [];
                this.form.initialDataObject = JSON.parse(JSON.stringify(this.form.formDataObject));

                this.defaultTerms = {
                    ...JSON.parse(JSON.stringify(this.form.initialDataObject.optionalTerms)),
                    ...JSON.parse(JSON.stringify(this.form.initialDataObject.mustIncludeTerms))
                };

                this.form.init();
            });

        this.attachEventHandlers();
        this.renderForm();
        this.setupAddAnothers();
        this.setupDatePickers();
        this.setupPostcodeLookups();
        // this.setupValidation();

        if (this.recaptchaEnabled) {
            commonForms.setupRecaptcha();
        }
    },

    attachEventHandlers: function () {
        document.querySelector('#form-content').addEventListener('click', event => {
            if (event.target.classList.contains('js-download-file')) {
                event.preventDefault();

                const downloadForm = document.querySelector('#mta-document-download');
                const downloadType = event.target.dataset.documenttype
                downloadForm.querySelector('input[name="type"]').value = downloadType;

                const formData = JSON.parse(JSON.stringify(this.form.formDataObject));
                const data = this.prepareFormDataForPost(formData);

                // tracking
                this.downloadCount[downloadType] = this.downloadCount[downloadType] + 1;

                const includedTerms = commonForms.objectKeys(data.optionalTerms);
                const excludedTerms = data.excludedTerms || [];
                const editedTerms = data.editedTerms || []; // todo: what is this for?
                delete data.editedTerms;

                commonForms.track({
                    'event': 'formSubmitted',
                    'formId': 'model-tenancy-form',
                    'downloadType': downloadType,
                    'downloadCount': this.downloadCount,
                    'tenantNumber': data.tenants.length,
                    'landlordNumber': data.landlords.length,
                    'additionalTermNumber': data.additionalTerms.length,
                    'includedTerms': includedTerms,
                    'excludedTerms': excludedTerms,
                    'editedTerms': editedTerms
                });

                downloadForm.querySelector('input[name="data"]').value = encodeURIComponent(JSON.stringify(data));

                this.form.pauseUnloadEvent = true;
                downloadForm.submit();

                if (this.recaptchaEnabled) {
                    expireRecaptcha();
                }
            }
        })
    },

    getDefaultText: function () {
        return PromiseRequest('/service/housing/model-tenancy/template');
    },

    /**
     * Process the form data into the format that the backend expects
     * @param {object} formData
     * @returns {object}
     */
    prepareFormDataForPost(formData) {
        // 1. Property address as string not object
        formData.propertyAddress = this.form.formMapping.propertyAddress.getAddressAsString();

        // 2. build guarantors
        const tenants = formData.tenants;

        tenants.forEach(tenant => {
            if (tenant.guarantor) {
                let guarantorMatch = false;
                tenant.guarantor.address = tenant.guarantor.address || {};

                for (let i = 0, il = formData.guarantors.length; i < il; i++) {
                    const guarantor = guarantors[i];

                    if (guarantor.name === tenant.guarantor.name &&
                        guarantor.address.street === tenant.guarantor.address.street
                    ) {
                        guarantor.tenantNames.push(tenant.name);
                        guarantorMatch = true;
                    }
                }

                if (!guarantorMatch) {
                    formData.guarantors.push({
                        name: tenant.guarantor.name,
                        address: {
                            building: tenant.guarantor.address.building,
                            street: tenant.guarantor.address.street,
                            town: tenant.guarantor.address.town,
                            region: tenant.guarantor.address.region,
                            postcode: tenant.guarantor.address.postcode
                        },
                        tenantNames: [tenant.name]
                    });
                }

                delete tenant.guarantor;
            }
        });

        // 3. format dates to YYYY-MM-DD
        const dateFields = ['tenancyStartDate', 'firstPaymentDate', 'firstPaymentPeriodEnd', 'hmoRegistrationExpiryDate'];
        for (const field of dateFields) {
            let value = formData[field];
            if (value === null || value.split(' ').join('') === '') {
                continue;
            }

            value = value.trim();

            let dateRegex = new RegExp(/\d{1,2}\/\d{1,2}\/\d{4}/);

            if (value.match(dateRegex)) {
                let dateAsArray = value.replace(/-/g, '/').split('/');

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
        };

        // 4. Add value from 'other' option in property type field if selected
        const selectedPropertyTypeRadio = document.querySelector('[name="property-building"]:checked');
        const selectedPropertyTypeLabel = document.querySelector(`label[for="${selectedPropertyTypeRadio.id}"]`);
        if (selectedPropertyTypeRadio.value.toUpperCase() === 'OTHER') {
            formData.propertyType = document.querySelector('#building-other').value;
        } else {
            formData.propertyType = selectedPropertyTypeLabel.innerText;
        }

        // 5. Shred the facilities into expected places
        formData.includedAreasOrFacilities = formData.includedAreasOrFacilities || [];
        formData.excludedAreasFacilities = formData.excludedAreasFacilities || [];
        formData.sharedFacilities = formData.sharedFacilities || [];
        for (const facility of formData.facilities) {
            if (facility.type.toUpperCase() === 'INCLUDED') {
                formData.includedAreasOrFacilities.push(facility.name);
            } else if (facility.type.toUpperCase() === 'EXCLUDED') {
                formData.excludedAreasFacilities.push(facility.name);
            } else if (facility.type.toUpperCase() === 'SHARED') {
                formData.sharedFacilities.push(facility.name);
            }
        }

        // 6. Format rent payment schedule into a sentence string
        const frequency = formData.rentPaymentFrequency;
        const schedule = formData.rentPaymentScheduleObject;

        let scheduleString = '';
        const day = schedule.day || '';
        const week = schedule.week || '';
        const date = schedule.date || '';

        if (commonForms.objectKeys(schedule).length > 0) {
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
        // 8. Remove HMO info if the user has said the property is not HMO

        // 9. Add recaptcha data
        if (this.recaptchaEnabled) {
            formData.recaptcha = grecaptcha.getResponse();
        }
        return formData;
    },

    /**
     * Render an "additional terms" add another widget, map its data to the form data object on save
     */
    renderAdditionalTermsAddAnother() {
        const data = modelTenancyForm.form.formDataObject.additionalTerms || [];

        const additionalTermsAddAnother = new AddAnother({
            addString: 'You have added the term <strong>{{title}}</strong> to the tenancy agreement.',
            changeString: 'You have changed the term <strong>{{title}}</strong> on the tenancy agreement.',
            deleteString: 'You have deleted the term <strong>{{title}}</strong> from the tenancy agreement.',
            nameString: 'Additional term',

            buttonText: 'Add a term',
            container: document.getElementById('additional-terms-add-another'),
            data: data,
            deleteTemplate: additionalTermsDeleteTemplate,
            formTemplate: additionalTermsFormTemplate,
            formTemplateRenderArguments: {
                requiredName: true
            },
            summaryTemplate: additionalTermsSummaryTemplate,

            onSave: formContainer => {
                modelTenancyForm.form.formDataObject.additionalTerms = additionalTermsAddAnother.getData();

                return {
                    title: formContainer.querySelector('#additional-term-title').value,
                    content: formContainer.querySelector('#additional-term-content').value,
                }
            }
        });

        additionalTermsAddAnother.init();
    },

    /**
     * Render an editable term
     * Set up the editable term's event listeners
     * Map the term to the form data object
     */
    renderEditableTerm() {
        const editableTermContainer = document.querySelector('.form-page:not(.fully-hidden) .js-editable');

        const templateData = {
            checkboxId: editableTermContainer.dataset.query,
            label: 'Placeholder label for this term',
            slug: editableTermContainer.dataset.term
        };

        if (editableTermContainer.dataset.type === 'mandatory') {
            templateData.value = this.form.formDataObject.mustIncludeTerms[editableTermContainer.dataset.term];
        } else {
            templateData.value = this.form.formDataObject.optionalTerms[editableTermContainer.dataset.term];
        }

        editableTermContainer.innerHTML = editableTermTemplate.render(templateData);

        const resetButtons = editableTermContainer.querySelector('.mg_editable-term__reset');

        editableTermContainer.querySelector('.mg_editable-term').addEventListener('click', event => {
            if (event.target.classList.contains('js-editable-term-reset-link')) {
                // show reset buttons, tempfocus reset buttons
                resetButtons.classList.remove('fully-hidden');
                temporaryFocus(resetButtons);
            }
            if (event.target.classList.contains('js-editable-term-reset-confirm')) {
                // reset term, hide reset buttons, tempfocus editable
                if (editableTermContainer.dataset.type === 'mandatory') {
                    this.form.formDataObject.mustIncludeTerms[editableTermContainer.dataset.term] = this.defaultTerms[editableTermContainer.dataset.term];
                } else {
                    this.form.formDataObject.optionalTerms[editableTermContainer.dataset.term] = this.defaultTerms[editableTermContainer.dataset.term];
                }
                this.form.formEvents.renderEditableTerm();
                temporaryFocus(editableTermContainer);
            }
            if (event.target.classList.contains('js-editable-term-reset-cancel')) {
                // hide reset buttons
                resetButtons.classList.add('fully-hidden');
            }

            if (event.target.classList.contains('js-include-term')) {
                const termSlug = event.target.dataset.slug;
                const type = editableTermContainer.dataset.type === 'mandatory' ? 'mustIncludeTerms' : 'optionalTerms';

                if (event.target.checked) {
                    // include the term
                    this.form.formDataObject[type][termSlug] = this.form.formDataObject.excludedTerms[termSlug];
                    delete this.form.formDataObject.excludedTerms[termSlug];
                } else {
                    // exclude the term
                    this.form.formDataObject.excludedTerms[termSlug] = this.form.formDataObject[type][termSlug];
                    delete this.form.formDataObject[type][termSlug];
                }
            }
        });
    },

    /**
     * Render the form into the form content container
     */
    renderForm() {
        const formTemplateContainer = document.querySelector('#form-content');
        if (!formTemplateContainer) {
            return false;
        }

        formTemplateContainer.innerHTML = formTemplate.render({
            tenants: true,
            iconsFile: bloomreachWebfile('/assets/images/icons/icons.stack.svg'),
            webfilesPath: bloomreachWebfile(),
            assetsPath: document.getElementById('site-root-path').value.replace('mygov', '') + 'assets',
            recaptchaEnabled: this.recaptchaEnabled,
            recaptchaSitekey: this.recaptchaSitekey,
            ...this.form.formDataObject
        });
    },

    /**
     * Render a "landlords" add another widget, map its data to the form data object on save
     */
    renderLandlordsAddAnother() {
        const data = modelTenancyForm.form.formDataObject.landlords || [];

        const landlordsAddAnother = new AddAnother({
            addString: 'You have added the landlord <strong>{{name}}</strong> to the tenancy agreement.',
            changeString: 'You have changed the landlord <strong>{{name}}</strong> on the tenancy agreement.',
            deleteString: 'You have deleted the landlord <strong>{{name}}</strong> from the tenancy agreement.',
            nameString: 'Landlord',

            buttonText: 'Add a landlord',
            container: document.getElementById('landlords-add-another'),
            data: data,
            deleteTemplate: landlordsDeleteTemplate,
            formTemplate: landlordsFormTemplate,
            formTemplateRenderArguments: {
                requiredName: true
            },
            summaryTemplate: landlordsSummaryTemplate,

            onRenderForm: formContainer => {
                landlordsAddAnother.landlordPostcodeLookup = new PostcodeLookup(formContainer.querySelector('#landlord-postcode-lookup'));
            },

            onSave: formContainer => {
                modelTenancyForm.form.formDataObject.landlords = landlordsAddAnother.getData();

                return {
                    address: landlordsAddAnother.landlordPostcodeLookup.getAddressAsObject(),
                    email: formContainer.querySelector('#landlord-email').value,
                    name: formContainer.querySelector('#landlord-name').value,
                    registrationNumber: formContainer.querySelector('#landlord-registration').value,
                    telephone: formContainer.querySelector('#landlord-phone').value
                }
            }
        });

        landlordsAddAnother.init();
    },

    /**
     * Render a "lettign agent services" add another widget, map its data to the form data object on save
     */
    renderLettingAgentServicesAddAnother() {
        const data = modelTenancyForm.form.formDataObject.servicesLookedAfterByLettingAgent || [];
        const lettingAgentServicesAddAnother = new AddAnother({
            addString: 'You have added the service <strong>{{name}}</strong> to the tenancy agreement.',
            changeString: 'You have changed the service <strong>{{name}}</strong> on the tenancy agreement.',
            deleteString: 'You have deleted the service <strong>{{name}}</strong> from the tenancy agreement.',
            nameString: 'Service',

            buttonText: 'Add a service',
            container: document.getElementById('letting-agent-services-add-another'),
            data: data,
            deleteTemplate: lettingAgentServicesDeleteTemplate,
            formTemplate: lettingAgentServicesFormTemplate,
            summaryTemplate: lettingAgentServicesSummaryTemplate,

            onSave: formContainer => {
                modelTenancyForm.form.formDataObject.servicesLookedAfterByLettingAgent = lettingAgentServicesAddAnother.getData();

                return {
                    name: formContainer.querySelector('#letting-agent-service-name').value,
                    value: formContainer.querySelector('[name="letting-agent-first-contact"]:checked').value
                }
            }
        });

        lettingAgentServicesAddAnother.init();
    },

    /**
     * Render the relevant rent frequency details for fields for the selected rent frequency
     * Map the rent frequency fields to the form object
     */
    renderRentFrequencyDetails() {
        const rentFrequencyContainer = document.querySelector('#tenancy-payment-frequency-details-question-container');

        rentFrequencyContainer.innerHTML = rentFrequencyDetailsTemplate.render({
            frequency: modelTenancyForm.form.formDataObject.rentPaymentFrequency,
            title: `Payment frequency: ${modelTenancyForm.form.formDataObject.rentPaymentFrequency_text}`
        });

        // Reset the form's data on what the schedule is
        modelTenancyForm.form.formDataObject.rentPaymentScheduleObject = {};

        // Map new fields
        const fieldMappings = {
            'rentPaymentScheduleObject.day': '#tenancy-payment-frequency-day',
            'rentPaymentScheduleObject.week': '#tenancy-payment-frequency-week',
            'rentPaymentScheduleObject.date': '#tenancy-payment-frequency-date'
        };

        for (const key in fieldMappings) {
            if (!fieldMappings.hasOwnProperty(key)) { continue; }
            modelTenancyForm.form.mapField(key, fieldMappings[key]);
        }
    },

    /**
     * Render a "services" add another widget, map its data to the form data object on save
     */
    renderServicesAddAnother() {
        const data = modelTenancyForm.form.formDataObject.servicesIncludedInRent;

        const servicesAddAnother = new AddAnother({
            addString: 'You have added the service <strong>{{name}}</strong> to the tenancy agreement.',
            changeString: 'You have changed the service <strong>{{name}}</strong> on the tenancy agreement.',
            deleteString: 'You have deleted the service <strong>{{name}}</strong> from the tenancy agreement.',
            nameString: 'Service',

            buttonText: 'Add a service',
            container: document.getElementById('services-add-another'),
            data: data,
            deleteTemplate: servicesDeleteTemplate,
            formTemplate: servicesFormTemplate,
            summaryTemplate: servicesSummaryTemplate,

            onSave: formContainer => {
                modelTenancyForm.form.formDataObject.servicesIncludedInRent = servicesAddAnother.getData();

                return {
                    name: formContainer.querySelector('#service-name').value,
                    value: formContainer.querySelector('#service-amount').value
                }
            }
        });

        servicesAddAnother.init();
    },

    /**
     * Render a "tenants" add another widget, map its data to the form data object on save
     */
    renderTenantsAddAnother() {
        const data = modelTenancyForm.form.formDataObject.tenants || [];

        const tenantsAddAnother = new AddAnother({
            addString: 'You have added the tenant <strong>{{name}}</strong> to the tenancy agreement.',
            changeString: 'You have changed the tenant <strong>{{name}}</strong> on the tenancy agreement.',
            deleteString: 'You have deleted the tenant <strong>{{name}}</strong> from the tenancy agreement.',
            nameString: 'Tenant',

            buttonText: 'Add a tenant',
            container: document.getElementById('tenants-add-another'),
            data: data,
            deleteTemplate: tenantsDeleteTemplate,
            formTemplate: tenantsFormTemplate,
            summaryTemplate: tenantsSummaryTemplate,

            onRenderForm: formContainer => {
                tenantsAddAnother.tenantPostcodeLookup = new PostcodeLookup(formContainer.querySelector('#tenant-postcode-lookup'));
                tenantsAddAnother.guarantorPostcodeLookup = new PostcodeLookup(formContainer.querySelector('#guarantor-postcode-lookup'));
            },

            onSave: formContainer => {
                modelTenancyForm.form.formDataObject.tenants = tenantsAddAnother.getData();

                const data = {
                    address: tenantsAddAnother.tenantPostcodeLookup.getAddressAsObject(),
                    email: formContainer.querySelector('#tenant-email').value,
                    name: formContainer.querySelector('#tenant-name').value,
                    telephone: formContainer.querySelector('#tenant-phone').value
                }

                if (!!formContainer.querySelector('#guarantor-name').value.length || Object.keys(tenantsAddAnother.guarantorPostcodeLookup.getAddressAsObject()).length > 0) {
                    data.guarantor = {
                        name: formContainer.querySelector('#guarantor-name').value,
                        address: tenantsAddAnother.guarantorPostcodeLookup.getAddressAsObject()
                    }
                }

                return data;
            }
        });

        tenantsAddAnother.init();
    },

    /**
     * Render a "what's included" add another widget, map its data to the form data object on save
     */
    renderWhatsIncludedAddAnother() {
        const data = modelTenancyForm.form.formDataObject.facilities;

        const whatsIncludedAddAnother = new AddAnother({
            addString: 'You have added the area/facility <strong>{{name}}</strong> to the tenancy agreement.',
            changeString: 'You have changed the area/facility <strong>{{name}}</strong> on the tenancy agreement.',
            deleteString: 'You have deleted the area/facility <strong>{{name}}</strong> from the tenancy agreement.',
            nameString: 'Area or facility',

            buttonText: 'Add an area or facility',
            container: document.getElementById('whats-included-add-another'),
            data: data,
            deleteTemplate: whatsIncludedDeleteTemplate,
            formTemplate: whatsIncludedFormTemplate,
            summaryTemplate: whatsIncludedSummaryTemplate,

            onSave: formContainer => {
                modelTenancyForm.form.formDataObject.facilities = whatsIncludedAddAnother.getData();

                return {
                    name: formContainer.querySelector('#facility-name').value,
                    type: formContainer.querySelector('[name="facility-type"]:checked').value
                }
            }
        });

        whatsIncludedAddAnother.init();
    },

    /**
     * Add form events for each of the "add another" widgets, to be called on page transitions
     */
    setupAddAnothers() {
        this.form.formEvents.renderAdditionalTermsAddAnother = this.renderAdditionalTermsAddAnother;
        this.form.formEvents.renderLandlordsAddAnother = this.renderLandlordsAddAnother;
        this.form.formEvents.renderLettingAgentServicesAddAnother = this.renderLettingAgentServicesAddAnother;
        this.form.formEvents.renderLettingAgentOtherServicesAddAnother = this.renderLettingAgentOtherServicesAddAnother;
        this.form.formEvents.renderRentFrequencyDetails = this.renderRentFrequencyDetails;
        this.form.formEvents.renderServicesAddAnother = this.renderServicesAddAnother;
        this.form.formEvents.renderTenantsAddAnother = this.renderTenantsAddAnother;
        this.form.formEvents.renderWhatsIncludedAddAnother = this.renderWhatsIncludedAddAnother;
    },

    /**
     * Instantiate and initialise DS date picker components
     */
    setupDatePickers() {
        const startDatePicker = new window.DS.components.DSDatePicker(document.getElementById('tenancy-start-date-picker'), {minDate: new Date(), imagePath: bloomreachWebfile('/assets/images/icons/')});
        const hmoDatePicker = new window.DS.components.DSDatePicker(document.getElementById('hmo-expiry-date-picker'), {minDate: new Date(), imagePath: bloomreachWebfile('/assets/images/icons/')});
        const firstPaymentDatePicker = new window.DS.components.DSDatePicker(document.getElementById('first-payment-date-picker'), {minDate: new Date(), imagePath: bloomreachWebfile('/assets/images/icons/')});
        const firstPaymentEndDatePicker = new window.DS.components.DSDatePicker(document.getElementById('first-payment-end-date-picker'), {minDate: new Date(), imagePath: bloomreachWebfile('/assets/images/icons/')});

        startDatePicker.init();
        hmoDatePicker.init();
        firstPaymentDatePicker.init();
        firstPaymentEndDatePicker.init();
    },

    setupPostcodeLookups: function () {
        const rpzComplete = function(rpzData){
            this.form.formDataObject.inRentPressureZone = rpzData.inRentPressureZone;
        };

        this.form.formMapping.propertyAddress = new PostcodeLookup(
            document.getElementById('property-postcode-lookup'),
            {
                notScottishMessage: 'The postcode you\'ve entered is not a Scottish postcode.' +
                ' You can only create a Scottish Government Model Tenancy Agreement for' +
                ' homes in Scotland. <a href="https://www.gov.uk/tenancy-agreements-a-guide-for' +
                '-landlords" target="_blank">Find out more about UK tenancy agreements' +
                ' (opens in a new window)</a>.',
                readOnly: false,
                rpz: true, // NOSONAR
                rpzComplete: rpzComplete
            }
        );
        this.form.formMapping['lettingAgent.address'] = new PostcodeLookup(document.getElementById('letting-agent-postcode-lookup'));
    },

    /**
     * Set up custom validators
     */
    setupValidation() {
        commonForms.atLeastOneTenant = function () {
            const tenantRepeater = document.querySelector('#tenants-add-another');
            tenantRepeater.dataset.invalid = true;
            return false;
        }
    }
}

window.format = modelTenancyForm;
window.format.init();

export default modelTenancyForm;
