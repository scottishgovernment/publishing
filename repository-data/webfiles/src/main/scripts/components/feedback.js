//feedback.js
/*
 Contains functionality for on page feedback
 */

'use strict';

import commonFormsOld from '../tools/forms';
import temporaryFocus from '@scottish-government/design-system/src/base/tools/temporary-focus/temporary-focus';

const errorSummaryTemplate = require('../templates/error-summary');
const feedbackTemplate = require('../templates/feedback');

// todo: replace with DS PromiseRequest if it gets updated to support POST
const PromiseRequest = function (url, method, data, headers) {
    const request = new XMLHttpRequest();

    return new Promise((resolve, reject) => {
        request.onreadystatechange = () => {
            if (request.readyState !== 4) {
                return;
            }

            if (request.status >= 200 && request.status < 300) {
                resolve(request);
            } else {
                reject({
                    status: request.status,
                    statusText: request.statusText
                });
            }
        };

        request.open(method, url, true);

        if (typeof headers === 'object') {
            for (const header of Object.entries(headers)) {
                request.setRequestHeader(header[0], header[1])
            }
        }

        request.send(data);
    });
};

// todo: this is a temporary override of commonForms using new error handling code that is part of the MTA redesign
// todo: this can be removed when that revamped error code is released
const commonForms = {
    requiredDropdown: function (field) {
        const value = field.value;
        const fieldName = commonFormsOld.getLabelText(field);
        const message = 'Select one of the options';

        const valid = value !== null && value !== '';

        commonForms.toggleFormErrors(field, valid, fieldName, message);
        commonForms.toggleCurrentErrors(field, valid, 'required', message);

        return valid;
    },

    requiredField: function (field, customMessage) {
        const trimmedValue = field.value.trim();
        const fieldName = commonFormsOld.getLabelText(field);

        const valid = trimmedValue !== '';

        let message = 'This field is required';
        if (field.dataset.message || customMessage) {
            message = '';
            if (fieldName) {
                message += `<strong>${fieldName}</strong> <br>`;
            }
            message += `${field.dataset.message || customMessage}`;
        }

        commonForms.toggleFormErrors(field, valid, fieldName, message);
        commonForms.toggleCurrentErrors(field, valid, 'invalid-required', message);

        return valid;
    },

    requiredRadio: function (container) {
        const radioButtons = [].slice.call(container.querySelectorAll('input[type="radio"]'));

        const title = commonFormsOld.getTitleFromLegend(container.querySelector('legend'));
        const message = 'Select one of the options';

        let valid = false;

        radioButtons.forEach(radioButton => {
            if (radioButton.checked) {
                valid = true;
            }
        });

        commonForms.toggleFormErrors(container, valid, title, message);
        commonForms.toggleCurrentErrors(container, valid, 'required', message);

        return valid;
    },

    toggleCurrentErrors: commonFormsOld.toggleCurrentErrors,
    validateInput: commonFormsOld.validateInput,

    renderErrorSummary: function (errors) {
        const errorSumaryContainerElement = document.querySelector('.js-error-summary-container');
        errorSumaryContainerElement.innerHTML = errorSummaryTemplate.render({ errors: errors });

        if (errors.length > 0) {
            temporaryFocus(errorSumaryContainerElement.querySelector('.ds_error-summary'));
        }
    },

    toggleFormErrors: function (field, valid, fieldName = '', message = '') {
        this.errors = this.errors || [];
        this.errors = this.errors.filter(item => !(item.fragmentId === field.id && item.message === message));
        fieldName = fieldName || this.getLabelText(field);
        if (!valid) {
            this.errors.push({ fragmentId: field.id, fieldName: fieldName, message: message });
        }
    },

    validateStep: function (container) {
        this.errors = [];

        const itemsThatNeedToBeValidated = [].slice.call(container.querySelectorAll('[data-validation]')).filter(item => item.offsetParent);
        itemsThatNeedToBeValidated.forEach(item => {
            const validations = item.getAttribute('data-validation').split(' ');
            const validationChecks = [];
            for (let i = 0, il = validations.length; i < il; i++) {
                if (commonForms[validations[i]]) {
                    validationChecks.push(commonForms[validations[i]]);
                }
            }

            commonForms.validateInput(item, validationChecks);
        });

        const invalidFields = [].slice.call(container.querySelectorAll('[aria-invalid="true"],[data-invalid="true"]')).filter(item => item.offsetParent && item.dataset.validation);

        // elear errors on no longer validated fields
        const noLongerValidatedFields = [].slice.call(container.querySelectorAll('[aria-invalid="true"],[data-invalid="true"]')).filter(item => typeof item.dataset.validation === 'undefined');
        noLongerValidatedFields.forEach(item => {
            item.errors = {};
            commonForms.validateInput(item, []);
            commonForms.toggleCurrentErrors(item, true, '', '')
        });

        this.renderErrorSummary(this.errors);

        return invalidFields.length === 0;
    }
}

const feedbackForm = {
    init: function () {
        this.feedbackContainer = document.getElementById('feedback');

        if (!this.feedbackContainer) {
            // no feedback form on this page, do nothing
            return;
        }

        this.form = this.feedbackContainer.querySelector('form');

        this.addTracking();
        this.attachEventHandlers();
        this.setInitialData();
        this.form.innerHTML = feedbackTemplate.render({ step: 'type' });
        this.feedbackContainer.classList.add('js-initialised');
    },

    addTracking: function () {
        this.feedbackContainer.addEventListener('change', event => {
            if (event.target.classList.contains('ds_radio__input')) {
                const type = this.form.querySelector('[name="feedback-type"]:checked').value;
                this.updateDataLayer(type, 'feedbackRadio');
            }

            if (event.target.classList.contains('ds_select')) {
                this.updateDataLayer(this.data.type, 'feedbackSelect');
            }
        });
    },

    attachEventHandlers: function () {
        this.feedbackContainer.addEventListener('click', event => {
            if (event.target.classList.contains('js-continue')) {
                if (commonForms.validateStep(this.form)) {
                    this.data.type = this.form.querySelector('[name="feedback-type"]:checked').value;

                    this.form.innerHTML = feedbackTemplate.render({
                        step: 'details',
                        data: this.data
                    });

                    temporaryFocus(this.form);
                }
            }

            if (event.target.classList.contains('js-cancel')) {
                this.resetForm();
                temporaryFocus(this.form);
            }

            if (event.target.classList.contains('js-submit')) {
                event.preventDefault();
                if (commonForms.validateStep(this.form)) {
                    this.data.reason = this.getFeedbackReason();
                    this.data.freetext = this.getFeedbackFreeText();
                    this.submitFeedback();
                }
            }
        });
    },

    getFeedbackReason: function () {
        let reason = '';

        if (this.data.type !== 'yes') {
            reason = this.form.querySelector('#reason').value;
        }

        return reason;
    },

    getFeedbackFreeText: function () {
        const freeText = this.form.querySelector('#feedback-comments').value;
        return freeText.substring(0, 250);
    },

    setInitialData: function () {
        this.data = {
            category: document.querySelector('#page-category').value,
            contentItem: document.getElementById('documentUuid').value,
            errors: [],
            hippoContentItem: window.location.pathname,
            slug: document.location.pathname
        }

        const urlParams = new URLSearchParams(window.location.search);
        if (urlParams.has('q')) {
            this.data.searchTerm = urlParams.get('q');
        }
        if (urlParams.has('cat')) {
            this.data.searchCat = urlParams.get('cat');
        }
        if (urlParams.has('page')) {
            this.data.searchPage = urlParams.get('page');
        }
    },

    resetForm: function () {
        delete this.data.reason;
        delete this.data.type;
        this.form.innerHTML = feedbackTemplate.render({ step: 'type' });
        commonForms.renderErrorSummary([]);
    },

    showSuccessMessage: function () {
        const errorSummaryElement = this.feedbackContainer.querySelector('.js-error-summary-container');
        const thanksElement = this.feedbackContainer.querySelector('.js-confirmation-message');

        // remove form and error summary
        this.form.parentNode.removeChild(this.form);
        errorSummaryElement.parentNode.removeChild(errorSummaryElement);

        // show success message
        thanksElement.classList.remove('fully-hidden');
    },

    submitFeedback: function () {
        // update datalayer
        this.updateDataLayer(this.data.type, 'feedbackSubmit');

        // submit feedback
        PromiseRequest('/service/feedback', 'POST', JSON.stringify(this.data), { 'Content-Type': 'application/json; charset=utf-8' })
            .then(() => {
                this.showSuccessMessage();
            })
            .catch(err => {
                let message = 'Sorry, we have a problem at our side. Please try again later.';

                if (err.status === 429) {
                    message = 'Sorry, too many requests have been submitted. Please try again later.';
                }

                commonForms.renderErrorSummary([{
                    message: message
                }]);
            });
    },

    updateDataLayer: function (type, event) {
        // update datalayer
        window.dataLayer = window.dataLayer || [];
        window.dataLayer.push({
            'type': type,
            'reason': this.data.reason,
            'event': event
        });
    }
};

export default feedbackForm;
