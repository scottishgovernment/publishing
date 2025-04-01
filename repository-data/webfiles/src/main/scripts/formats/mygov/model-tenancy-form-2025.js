import AddAnother from '../../components/add-another';
import bloomreachWebfile from '../../tools/bloomreach-webfile';
import commonForms from '../../tools/forms';
import formMapping from '../../components/mygov/housing-forms/model-tenancy-mapping-2025';
import MultiPageForm from "../../components/multi-page-form-2025";
import PostcodeLookup from '../../components/postcode-lookup';
import PromiseRequest from '@scottish-government/design-system/src/base/tools/promise-request/promise-request';
import temporaryFocus from '@scottish-government/design-system/src/base/tools/temporary-focus/temporary-focus';

const additionalTermsDeleteTemplate = require('../../templates/mygov/housing-form-subprocesses/additional-terms-delete');
const additionalTermsFormTemplate = require('../../templates/mygov/housing-form-subprocesses/additional-terms-form');
const additionalTermsSummaryTemplate = require('../../templates/mygov/housing-form-subprocesses/additional-terms-summary');
const editableTermTemplate = require('../../templates/mygov/housing-form-subprocesses/editable-term');
const formTemplate = require('../../templates/mygov/model-tenancy-form-2025');
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
const summaryTemplate = require('../../templates/mygov/model-tenancy-form-2025-summary');
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
             * Enable or disable the letting agent pages of the form based on the user's response to
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

                // window.DS.tracking.init(mandatoryTermsContainer);
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
                const summaryObject = JSON.parse(JSON.stringify(modelTenancyForm.form.formDataObject));
                const summaryContainer = document.querySelector('#summary');

                summaryObject.hasTenants = Object.values(summaryObject.tenants).length;
                summaryObject.hasLandlords = Object.values(summaryObject.landlords).length;
                summaryObject.mandatory2 = true;

                summaryObject.facilitiesByType = {
                    excluded: modelTenancyForm.form.formDataObject.facilities.filter(item => item.type.toLowerCase() === 'excluded'),
                    included: modelTenancyForm.form.formDataObject.facilities.filter(item => item.type.toLowerCase() === 'included'),
                    shared: modelTenancyForm.form.formDataObject.facilities.filter(item => item.type.toLowerCase() === 'shared')
                };

                if (modelTenancyForm.form.formDataObject.servicesLookedAfterByLettingAgent.length > 0) {
                    summaryObject.servicesProvidedByLettingAgent = modelTenancyForm.form.formDataObject.servicesLookedAfterByLettingAgent.filter(item => item.value.toLowerCase() === 'yes');
                    summaryObject.servicesLettingAgentIsFirstContactFor = modelTenancyForm.form.formDataObject.servicesLookedAfterByLettingAgent.filter(item => item.value.toLowerCase() === 'no');
                }

                summaryContainer.innerHTML = summaryTemplate.render(summaryObject);

                const mandatoryTermsContainer = summaryContainer.querySelector('#mandatory-terms-container-summary');
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
                        title: 'What\'s the property address?',
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
                        noSummaryButton: true,
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
                        noSummaryButton: true,
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
                        slug: 'contents-and-conditions',
                        title: 'Contents and condition',
                        triggerEventOnEntry: 'renderEditableTerm'
                    },
                    {
                        slug: 'local-authority-taxes-and-charges',
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
                hideTracker: true,
                showInTracker: false,
                slug: 'review',
                title: 'Review',
                pages: [
                    {
                        nextText: 'Confirm and continue',
                        noSummaryButton: true,
                        showInTracker: false,
                        slug: 'review',
                        title: 'Review your tenancy agreement',
                        triggerEventOnEntry: 'renderSummary'
                    }
                ]
            },
            {
                hideTracker: true,
                showInTracker: false,
                slug: 'download',
                title: 'Download',
                pages: [
                    {
                        nextText: 'Yes, I have downloaded',
                        noSummaryButton: true,
                        showInTracker: false,
                        slug: 'download',
                        title: 'Your tenancy agreement is ready'
                    }
                ]
            },
            {
                hideTracker: true,
                showInTracker: false,
                slug: 'confirmation',
                title: 'Confirmation',
                pages: [{
                    noSummaryButton: true,
                    showInTracker: false,
                    slug: 'confirmation',
                    title: 'Thank you'
                }]
            },
        ]
    }),

    downloadCount: { word: 0, pdf: 0 },

    init: function () {
        this.recaptchaSitekey = document.getElementById('recaptchaSitekey').value;
        this.recaptchaEnabled = document.getElementById('recaptchaEnabled').value === 'true';

        commonForms.appendCaptchaScript();

        this.getDefaultText()
            .then(data => {
                this.form.formDataObject = JSON.parse(data.responseText);
            })
            .then(() => {
                // reformat terms
                function capitaliseFirstLetter(val) {
                    return String(val).charAt(0).toUpperCase() + String(val).slice(1);
                }

                this.form.formDataObject.optionalTermsAsArray = Object.entries(this.form.formDataObject.optionalTerms).map(([name, content]) => ({ name, content }));
                this.form.formDataObject.optionalTermsAsArray.forEach(item => {
                    item.slug = item.name.replace(/([A-Z])/g, '-$1').toLowerCase()
                    item.title = capitaliseFirstLetter(item.name.replace(/([A-Z])/g, ' $1').toLowerCase());
                    item.type = 'optional';
                });

                // reformat mandatory terms
                this.form.formDataObject.mustIncludeTermsAsArray = Object.entries(this.form.formDataObject.mustIncludeTerms).map(([name, content]) => ({ name, content }));
                this.form.formDataObject.mustIncludeTermsAsArray.forEach(item => {
                    item.slug = item.name.replace(/([A-Z])/g, '-$1').toLowerCase()
                    item.title = capitaliseFirstLetter(item.name.replace(/([A-Z])/g, ' $1').toLowerCase());
                    item.type = 'mandatory';
                });

                // end reformat terms

                this.form.formDataObject.facilities = [];
                this.form.formDataObject.servicesLookedAfterByLettingAgent = [];

                this.form.initialDataObject = JSON.parse(JSON.stringify(this.form.formDataObject));

                this.defaultTerms = {
                    ...JSON.parse(JSON.stringify(this.form.initialDataObject.optionalTerms)),
                    ...JSON.parse(JSON.stringify(this.form.initialDataObject.mustIncludeTerms))
                };

                this.renderForm();

                this.attachEventHandlers();
                this.setupAddAnothers();
                this.setupDatePickers();
                this.setupPostcodeLookups();
                this.setupValidation();

                if (this.recaptchaEnabled) {
                    commonForms.setupRecaptcha();
                }

                this.form.init();
            })
            .catch(err => {
                // todo: some sort of message for when the service fails
            });
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
                const editedTerms = data.editedTerms || [];
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
        });

        const paymentFrequencyContainer = document.getElementById('payment-frequency');
        paymentFrequencyContainer.addEventListener('change', event => {
            // Reset the form's data on what the schedule is
            modelTenancyForm.form.formDataObject.rentPaymentScheduleObject = {};
        });
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
        // Property address as string not object
        formData.propertyAddress = this.form.formMapping.propertyAddress.getAddressAsString();

        // build guarantors
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

        // format dates to YYYY-MM-DD
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

        // Add value from 'other' option in property type field if selected
        const selectedPropertyTypeRadio = document.querySelector('[name="property-building"]:checked');
        const selectedPropertyTypeLabel = document.querySelector(`label[for="${selectedPropertyTypeRadio.id}"]`);
        if (selectedPropertyTypeRadio.value.toUpperCase() === 'OTHER') {
            formData.propertyType = document.querySelector('#building-other').value;
        } else {
            formData.propertyType = selectedPropertyTypeLabel.innerText;
        }

        // Shred the facilities into expected places
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

        // Format rent payment schedule into a sentence string
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

        // return optional terms to expected format
        formData.optionalTerms = {};
        formData.optionalTermsAsArray.filter(item => !item.excluded).forEach(item => formData.optionalTerms[item.name] = item.content);
        formData.excludedTerms = formData.optionalTermsAsArray.filter(item => item.excluded).map(item => item.name);

        // letting agent services
        formData.servicesProvidedByLettingAgent = formData.servicesLookedAfterByLettingAgent
            .filter(item => item.value.toUpperCase() === 'YES')
            .map(item => { return { name: item.name, lettingAgentIsFirstContact: item.value.toUpperCase() } });

        formData.servicesLettingAgentIsFirstContactFor = formData.servicesLookedAfterByLettingAgent
            .filter(item => item.value.toUpperCase() === 'NO')
            .map(item => { return { name: item.name } });

        // Add recaptcha data
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
        const editableTermContainer = document.querySelector('.js-form-page:not(.fully-hidden) .js-editable');
        let currentTerm;
        let termArrayName;

        if (editableTermContainer.dataset.type === 'mandatory') {
            termArrayName = 'mustIncludeTermsAsArray';
            currentTerm = this.form.formDataObject.mustIncludeTermsAsArray.filter(item => item.slug === editableTermContainer.dataset.slug)[0]
        } else {
            termArrayName = 'optionalTermsAsArray';
            currentTerm = this.form.formDataObject.optionalTermsAsArray.filter(item => item.slug === editableTermContainer.dataset.slug)[0]
        }

        const templateData = {
            checkboxId: editableTermContainer.dataset.query,
            excluded: !!currentTerm.excluded,
            label: 'Term description',
            termName: currentTerm.name,
            slug: currentTerm.slug,
            termArray: termArrayName,
            value: currentTerm.content
        };

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

                if (event.target.closest('.js-editable').dataset.type === 'mandatory') {
                    this.form.formDataObject.mustIncludeTermsAsArray.filter(item => item.slug === termSlug)[0].excluded = !event.target.checked;
                } else {
                    this.form.formDataObject.optionalTermsAsArray.filter(item => item.slug === termSlug)[0].excluded = !event.target.checked;
                }
            }
        });

        const textarea = editableTermContainer.querySelector(`#editable-term-${currentTerm.slug}-value`);
        textarea.addEventListener('change', () => {
            this.form.formDataObject[textarea.dataset.type].filter(item => item.name === textarea.dataset.name)[0].content = textarea.value;
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
     * Render a "letting agent services" add another widget, map its data to the form data object on save
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
            title: `Payment frequency: ${modelTenancyForm.form.formDataObject.rentPaymentFrequency_text}`,
            day: modelTenancyForm.form.formDataObject.rentPaymentScheduleObject.day,
            week: modelTenancyForm.form.formDataObject.rentPaymentScheduleObject.week,
            date: modelTenancyForm.form.formDataObject.rentPaymentScheduleObject.date
        });

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

            const fieldName = 'Tenants';
            const valid = modelTenancyForm.form.formDataObject.tenants.length > 0;

            if (valid) {
                delete tenantRepeater.dataset.invalid;
            } else {
                tenantRepeater.dataset.invalid = true;
            }

            let message = 'You must enter at least one tenant';

            commonForms.toggleFormErrors(tenantRepeater, valid, message, fieldName);

            return valid;
        }

        commonForms.atLeastOneLandlord = function () {
            const landlordRepeater = document.querySelector('#landlords-add-another');

            const fieldName = 'Landlords';
            const valid = modelTenancyForm.form.formDataObject.landlords.length > 0;

            if (valid) {
                delete landlordRepeater.dataset.invalid;
            } else {
                landlordRepeater.dataset.invalid = true;
            }

            let message = 'You must enter at least one landlord';

            commonForms.toggleFormErrors(landlordRepeater, valid, message, fieldName);

            return valid;
        }

        // conditional fields
        const propertyBuildingContainer = document.getElementById('property-building');
        const propertyBuildingOtherElement = document.getElementById('building-other');
        propertyBuildingContainer.addEventListener('change', event => {
            if (event.target.value && event.target.value.toUpperCase() === 'OTHER') {
                propertyBuildingOtherElement.dataset.validation = 'requiredField';
                propertyBuildingOtherElement.setAttribute('aria-required', true);
            } else {
                delete propertyBuildingOtherElement.dataset.validation;
                propertyBuildingOtherElement.removeAttribute('aria-required');
            }
        });

        const paymentMethodContainer = document.getElementById('payment-method');
        const paymentMethodOtherElement = document.getElementById('tenancy-payment-method-other-text');
        paymentMethodContainer.addEventListener('change', event => {
            if (event.target.value && event.target.value.toUpperCase() === 'OTHER') {
                paymentMethodOtherElement.dataset.validation = 'requiredField';
                paymentMethodOtherElement.setAttribute('aria-required', true);
            } else {
                delete paymentMethodOtherElement.dataset.validation;
                paymentMethodOtherElement.removeAttribute('aria-required');
            }
        });
    }
}

window.format = modelTenancyForm;
window.format.init();

export default modelTenancyForm;
