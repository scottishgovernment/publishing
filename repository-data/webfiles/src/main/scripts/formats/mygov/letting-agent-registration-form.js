/* global $, require, document, window */

'use strict';

import MultiPageForm from '../../components/multi-page-form';
import feedback from '../../components/feedback';
import bloomreachWebfile from '../../tools/bloomreach-webfile';
import commonForms from '../../tools/forms';

const formTemplate = require('../../templates/mygov/letting-agent-registration-form');

const formSections = [
    {
        group: {
            slug: 'part-0',
            title: 'Part zero'
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
            slug: 'part-0',
            title: 'Part zero'
        },
        slug: 'sign-up',
        title: 'Sign up',
        pages: [
            {
                slug: 'form',
                title: 'Sign up',
                hideSectionNav: true,
                hideSubsectionNav: true
            },
            {
                slug: 'success',
                title: 'Success',
                hideSectionNav: true,
                hideSubsectionNav: true,
                noFormBox: true
            }
        ]
    }
];

const lettingAgentRegistrationForm = {

    form: new MultiPageForm({
        formSections: formSections,
        formMapping: {},
        formObject: {},
        noSectionNav: true
    }),

    endpointUrl: '/service/form/letting/',

    init: function () {
        // append form template
        const formTemplateContainer = document.querySelector('#form-container');
        if (!formTemplateContainer) {
            return false;
        }
        const overviewContent = formTemplateContainer.innerHTML;
        formTemplateContainer.innerHTML = formTemplate.render({
            iconsFile: bloomreachWebfile('/assets/images/icons/icons.stack.svg')
        });
        formTemplateContainer.querySelector('#overview').innerHTML = overviewContent + formTemplateContainer.querySelector('#overview').innerHTML;

        commonForms.appendCaptchaScript();
        commonForms.setupRecaptcha();

        this.form.init();
        feedback.init();
        lettingAgentRegistrationForm.form.validateStep = lettingAgentRegistrationForm.validateStep;

        // init events (submit address, submit postcode, )
        this.bindEvents();
    },

    /**
     * Binds events from the 'events' list for the BRC to code.
     */
    bindEvents: function () {
        document.body.addEventListener('click', (event) => {
            if (event.target.classList.contains('js-submit-form')) {
                event.preventDefault();
                this.submitForm();
            }
        });
    },

    /**
     * Returns form data as object
     *
     * @returns {object} form data
     */
    getFormData: function () {
        return {
            name: document.querySelector('#letting-agent-registration-name').value,
            companyName: document.querySelector('#letting-agent-registration-company-name').value,
            addressLine1: document.querySelector('#letting-agent-registration-address-line-1').value,
            addressLine2: document.querySelector('#letting-agent-registration-address-line-2').value,
            town: document.querySelector('#letting-agent-registration-town').value,
            postcode: document.querySelector('#letting-agent-registration-postcode').value,
            country: document.querySelector('#letting-agent-registration-country').value,
            email: document.querySelector('#letting-agent-registration-email').value,
            consent: document.querySelector('#letting-agent-registration-consent').checked,
            recaptcha: grecaptcha.getResponse()
        };
    },

    validateStep: function () {
        return commonForms.validateStep(lettingAgentRegistrationForm.form.currentStep);
    },

    submitForm: function () {
        const errorSummary = document.querySelector('.ds_error-summary');
        errorSummary.querySelector('.form-errors').innerHTML = '';

        // validate
        if (this.validateStep()) {
            errorSummary.classList.add('fully-hidden');
            errorSummary.setAttribute('aria-hidden', 'true');

            const postData = this.getFormData();

            // send
            $.ajax({
                type: 'POST',
                url: '/service/form/letting/',
                contentType: 'application/json',
                data: JSON.stringify(postData),
                done: function () {
                    commonForms.track({
                        'event': 'formSubmitted',
                        'formId': 'letting-agent-registration-form',
                        'state': 'success',
                        'outwardCode': commonForms.outwardCode(postData.postcode),
                        'formRequests': []
                    });

                    expireRecaptcha();
                    lettingAgentRegistrationForm.form.goToStep(lettingAgentRegistrationForm.form.getStep('slug', 'success'));
                }
            });
        } else {
            if (errorSummary) {
                errorSummary.classList.remove('fully-hidden');
                errorSummary.setAttribute('aria-hidden', 'false');
                errorSummary.scrollIntoView();
                window.DS.tracking.init(errorSummary);
            }
        }
    }
};

window.format = lettingAgentRegistrationForm;
window.format.init();

export default lettingAgentRegistrationForm;
