// UKRAINE VOLUNTEER HOMES

/* global grecaptcha expireRecaptcha */

'use strict';

import bloomreachWebfile from '../../tools/bloomreach-webfile';
import commonForms from '../../tools/forms';
import commonHousing from '../../tools/housing';
import feedback from '../../components/feedback';
import MultiPageForm from '../../components/multi-page-form';

const formTemplate = require('../../templates/mygov/ukraine-volunteer-home-form');

const formSections = [
    {
        group: {
            slug: 'part-0',
            title: 'Part zero'
        },
        slug: 'your-details',
        title: 'Your details',
        pages: [
            {
                slug: 'your-details',
                title: 'Your detils',
                hideSectionNav: true,
                hideSubsectionNav: true,
                noFormBox: true
            }
        ]
    },
    {
        group: {
            slug: 'part-0',
            title: 'Part zero'
        },
        slug: 'thank-you',
        title: 'Thank you',
        pages: [
            {
                slug: 'thank-you',
                title: 'Thank you',
                hideSectionNav: true,
                hideSubsectionNav: true,
                noFormBox: true
            }
        ]
    }
];

const ukraineVolunteerHomeForm = {

    form: new MultiPageForm({
        formSections: formSections,
        formMapping: {
            'name': '#name',
            'email': '#email'
        },
        formObject: {},
        formEvents: {},
        modifiers: [],
        noSectionNav: true
    }),

    init: function () {
        // append form template
        const formTemplateContainer = document.querySelector('#form-container');
        if (!formTemplateContainer) {
            return false;
        }

        document.querySelector('.multi-page-form').style.padding = 0;

        const cmsContent = formTemplateContainer.innerHTML;
        const cmsAdditionalContent = document.querySelector('#cms-additional-content-source').innerHTML;
        this.recaptchaSitekey = document.getElementById('recaptchaSitekey').value;
        this.recaptchaEnabled = document.getElementById('recaptchaEnabled').value === 'true';
        formTemplateContainer.innerHTML = formTemplate.render({
            iconsFile: bloomreachWebfile('/assets/images/icons/icons.stack.svg'),
            webfilesPath: bloomreachWebfile(),
            recaptchaEnabled: this.recaptchaEnabled,
            recaptchaSitekey: this.recaptchaSitekey
        });
        formTemplateContainer.querySelector('#cms-content').innerHTML = cmsContent + formTemplateContainer.querySelector('#cms-content').innerHTML;
        formTemplateContainer.querySelector('#cms-additional-content').innerHTML = cmsAdditionalContent;

        this.endpointUrl = '/service/form/ukraine';
        this.form.validateStep = this.validateStep;
        this.form.init();
        feedback.init();
        commonForms.appendCaptchaScript();
        if (this.recaptchaEnabled) {
            commonForms.setupRecaptcha();
        }

        // init accordions
        // todo: do we have any accordions?
        const accordionElements = [].slice.call(document.querySelectorAll('[data-module="ds-accordion"]'));
        accordionElements.forEach(accordionElement => new DS.components.Accordion(accordionElement).init());

        // init events (submit form)
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

    submitForm: function () {
        if (this.form.validateStep()) {
            const formData = JSON.parse(JSON.stringify(ukraineVolunteerHomeForm.form.settings.formObject));
            if (this.recaptchaEnabled) {
                data.recaptcha = grecaptcha.getResponse();
            }

            const promiseRequest = commonForms.promiseRequest(this.endpointUrl, 'POST', JSON.stringify(formData), 'application/json');

            promiseRequest
                .then(() => {
                    this.form.goToStep(this.form.getNextStep());
                })
                .catch(() => {
                    // clunky
                    const errorSummary = document.querySelector('.ds_error-summary');
                    errorSummary.classList.remove('fully-hidden');
                    errorSummary.innerHTML = `
                    <h2 class="ds_error-summary__title" id="error-summary-title">There is a problem</h2>
                    <div class="form-errors">
                        <ul class="ds_error-summary__list send-errors">
                            <li class="error">
                                We are having technical difficulties at the moment. Please try again later.
                            </li>
                        </ul>
                    </div>
                    `;
                });

            if (this.recaptchaEnabled) {
                expireRecaptcha();
            }
        }
    },

    validateStep: function () {
        return commonHousing.validateStep(ukraineVolunteerHomeForm.form.currentStep);
    }
};

window.format = ukraineVolunteerHomeForm;
window.format.init();

export default ukraineVolunteerHomeForm;
