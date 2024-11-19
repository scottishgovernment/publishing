// COMMON FORMS

/* global grecaptcha */

'use strict';

import temporaryFocus from "@scottish-government/design-system/src/base/tools/temporary-focus/temporary-focus";

const errorSummaryTemplate = require('../templates/error-summary');

const commonForms = {
    /**
     * prepends zeroes to a number, up to a set length
     * @param {number} value - number to prepend zeroes to
     * @param {number} length - desired length of number
     * @returns {string}
     */
    leadingZeroes: function (value, length) {
        let ret = value.toString();

        while (ret.length < length) {
            ret = '0' + ret.toString();
        }

        return ret;
    },

    /**
     * Takes a date string in the format we use in forms (dd/MM/yyyy) and returns a date object
     * @param {string}
     * @returns {date}
     */
    stringToDate: function (string) {
        let fragments = string.trim().split('/');
        if (this.isValidDate(fragments[0], fragments[1], fragments[2])) {
            return new Date(`${fragments[1]}/${fragments[0]}/${fragments[2]}`);
        } else {
            return '';
        }
    },

    /**
     * Takes a date object and returns a date string in the format we use in forms (dd/MM/yyyy)
     * @param {date}
     * @returns {string}
     */
    dateToString: function (date) {
        return `${commonForms.leadingZeroes(date.getDate(), 2)}/${commonForms.leadingZeroes(date.getMonth() + 1, 2)}/${date.getFullYear()}`;
    },

    /**
     * Convert dd/mm/yyyy to yyyy-mm-dd
     * @param {string} string
     * @returns {string}
     */
    dateStringToMachineDate: function (string) {
        return string.trim().split('/').reverse().join('-');
    },

    /**
     * simple string trim on object properties
     * does not iterate into nested object
     * @param {obj}
     * @returns {obj}
     */
    trimObjectValues: function (obj) {
        Object.keys(obj).forEach(k => {
            if (typeof obj[k] === 'string') {
                obj[k] = obj[k].trim();
            }
        });

        return obj;
    },

    appendCaptchaScript: function () {
        const captchaScript = document.createElement('script');
        captchaScript.src = "https://www.google.com/recaptcha/api.js";
        document.body.appendChild(captchaScript);
    },

    setupRecaptcha: function () {
        const downloadLinks = [].slice.call(document.querySelectorAll('.js-document-container .js-download-file'));
        let recaptchaSuccess = false;

        /* Exposes recaptcha validation function to global window object to allow reCAPTCHA to find it
            */
        window.checkRecaptcha = function () {
            // check reCAPTCHA
            recaptchaSuccess = commonForms.validateInput(document.getElementById('recaptcha'), [commonForms.recaptchaCompleted], false);
            if (!recaptchaSuccess) {
                return false;
            } else {
                downloadLinks.forEach(link => {
                    link.removeAttribute('disabled');
                });
            }
        };

        window.expireRecaptcha = function () {
            downloadLinks.forEach(link => {
                link.setAttribute('disabled', true);
            });

            recaptchaSuccess = false;
            grecaptcha.reset();
        };
    },

    /**
     * Checks if recaptcha is completed. Appends or Removes an error message.
     *
     * @param {object} field - the form field to be checked
     * @returns {boolean} whether the field value is valid
     */
    recaptchaCompleted: function (field, customMessage) {
        const valid = grecaptcha.getResponse() !== '';
        let message = 'You must complete the CAPTCHA';

        if (field.dataset.message || customMessage) {
            message = '';
            if (fieldName) {
                message += `<strong>${fieldName}</strong> <br>`;
            }
            message += `${field.dataset.message || customMessage}`;
        }

        commonForms.toggleFormErrors(field, valid, message, 'CAPTCHA');
        return valid;
    },

    /**
     * Checks if $field is completed. Appends or Removes an error message.
     *
     * @param {object} field - the form field to be checked
     * @returns {boolean} whether the field value is valid
     */
    requiredField: function (field, customMessage) {
        const trimmedValue = field.value.trim();
        const fieldName = commonForms.getLabelText(field);
        const valid = trimmedValue !== '';

        let message = 'This field is required';
        if (field.dataset.message || customMessage) {
            message = '';
            if (fieldName) {
                message += `<strong>${fieldName}</strong> <br>`;
            }
            message += `${field.dataset.message || customMessage}`;
        }

        commonForms.toggleFormErrors(field, valid, message);
        commonForms.toggleCurrentErrors(field, valid, 'invalid-required', message);

        return valid;
    },

    regexMatch: function (field) {
        const trimmedValue = field.value.trim();
        const regex = new RegExp(field.getAttribute('pattern'));
        const message = field.dataset.errormessage;
        const valid = trimmedValue === '' || trimmedValue.match(regex);

        commonForms.toggleFormErrors(field, valid, message);
        commonForms.toggleCurrentErrors(field, valid, 'invalid-regex', message);

        return valid;
    },

    /**
     * Checks whether if $field is less than or equal to maxLength.
     * Appends or Removes an error message.
     * Updates character count
     *
     * @param {object} field - the form field to be checked
     * @param {int} maxLength - the permitted character length of $field
     * @returns {boolean} whether the field value is valid
     */
    maxCharacters: function (field, maxLength) {
        const valid = field.value.length <= maxLength;
        const fieldName = commonForms.getLabelText(field);
        const message = `${fieldName} has too many characters`;

        commonForms.toggleFormErrors(field, valid, message);
        commonForms.toggleCurrentErrors(field, valid, 'too-many', message);

        const countEl = field.parentNode.querySelector('.js-count');
        if (countEl) {
            countEl.innerHTML = (maxLength - field.value.length);
        }

        return valid;
    },

    atLeastOneRequired: function (fields, container, highlightField) {
        let provided = false;
        for (const element of fields) {
            provided = provided || element.value;
        }
        if (highlightField) {
            const formGroup = container.closest('.form-group');

            if (!provided) {
                formGroup.classList.add('js-has-error');
                formGroup.classList.remove('js-has-success');
            } else {
                formGroup.classList.remove('js-has-error');
                formGroup.classList.add('js-has-success');
            }
        }

        if (!provided) {
            this.requiredField(container, 'Specify at least one document');
        }

        return provided;
    },

    // New form validations

    validPostcode: function (field) {
        const message = 'Enter a valid postcode, for example EH6 6QQ';
        const trimmedValue = field.value.trim();
        const postcodeRegExp = new RegExp('^[A-Z]{1,2}[0-9R][0-9A-Z]?[0-9][ABD-HJLNP-UW-Z]{2}$');
        const postcodeValue = trimmedValue.toUpperCase().replace(/\s+/g, '');
        const valid = trimmedValue === '' || postcodeValue.match(postcodeRegExp) !== null;

        commonForms.toggleFormErrors(field, valid, message);
        commonForms.toggleCurrentErrors(field, valid, 'invalid-postcode', message);

        return valid;
    },

    validScottishPostcode: function (field) {
        const message = 'Enter a valid postcode in Scotland, for example EH6 6QQ.';
        const trimmedValue = field.value.trim();
        const scottishPostcodeRegExp = new RegExp('^(AB|DD|DG|EH|FK|G|HS|IV|KA|KW|KY|ML|PA|PH|TD|ZE)[0-9]{1,2} {0,2}[0-9][ABD-HJLN-UW-Z]{2}$');
        const postcodeValue = trimmedValue.toUpperCase().replace(/\s+/g, '');
        const valid = trimmedValue === '' || postcodeValue.match(scottishPostcodeRegExp) !== null;

        commonForms.toggleFormErrors(field, valid, message);
        commonForms.toggleCurrentErrors(field, valid, 'invalid-postcode', message);

        return valid;
    },

    validEmail: function (field) {
        const message = 'Enter a valid email address, for example firstname.surname@example.com';
        const trimmedValue = field.value.trim();
        const regex = /^[^@ ]+@[^@ ]+\.[^@ ]+$/;
        const valid = trimmedValue === '' || trimmedValue.match(regex) !== null;

        commonForms.toggleFormErrors(field, valid, message);
        commonForms.toggleCurrentErrors(field, valid, 'invalid-email', message);

        return valid;
    },

    requiredRadio: function (container) {
        const radioButtons = [].slice.call(container.querySelectorAll('input[type="radio"]'));
        const message = 'Select one of the options';
        let valid = false;

        radioButtons.forEach(radioButton => {
            if (radioButton.checked) {
                valid = true;
            }
        });

        commonForms.toggleFormErrors(container, valid, message);
        commonForms.toggleCurrentErrors(container, valid, 'required', message);

        return valid;
    },

    requiredDropdown: function (field) {
        const value = field.value;
        const message = 'Select one of the options';
        const valid = value !== null && value !== '';

        commonForms.toggleFormErrors(field, valid, message);
        commonForms.toggleCurrentErrors(field, valid, 'required', message);

        return valid;
    },

    validPhone: function (field) {
        const trimmedValue = field.value.trim().replace(/\s+/g, '');
        const message = 'Use only numbers 0-9 and the "+" character. Phone numbers should be less than 20 characters long.';

        // A regular expression matching only up to 20 numbers and possibly a '+' character at the beginning
        const regex = new RegExp('^\\+?[0-9]{0,20}$');
        const valid = trimmedValue.match(regex) !== null;

        commonForms.toggleFormErrors(field, valid, message);
        commonForms.toggleCurrentErrors(field, valid, 'invalid-phone', message);

        return valid;
    },

    isValidDate: function (day, month, year) {
        // solution by Matti Virkkunen: http://stackoverflow.com/a/4881951
        const isLeapYear = year % 4 === 0 && year % 100 !== 0 || year % 400 === 0;
        const daysInMonth = [31, isLeapYear ? 29 : 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31][month - 1];

        return day <= daysInMonth;
    },

    dateRegex: function (field) {
        const trimmedValue = field.value.trim();
        const message = 'Enter the date as DD/MM/YYYY';

        // A regular expression only allowing dd/mm/yyyy format
        const regex = new RegExp('^\\d\\d\\/\\d\\d\\/\\d\\d\\d\\d$');

        // check date is a valid date
        const day = parseInt(trimmedValue.slice(0, 2));
        const month = parseInt(trimmedValue.slice(3, 5));
        const year = parseInt(trimmedValue.slice(6, 10));

        const valid = trimmedValue === '' || (commonForms.isValidDate(day, month, year) && trimmedValue.match(regex) !== null);

        commonForms.toggleFormErrors(field, valid, message);
        commonForms.toggleCurrentErrors(field, valid, 'invalid-date-format', message);

        return valid;
    },

    futureDate: function (field) {
        const trimmedValue = field.value.trim();
        const message = 'This date must be in the future';
        let valid = false;

        if (commonForms.dateRegex(field) && trimmedValue.length) {
            const day = parseInt(trimmedValue.slice(0, 2));
            const month = parseInt(trimmedValue.slice(3, 5));
            const year = parseInt(trimmedValue.slice(6, 10));

            const date = new Date(`${month}/${day}/${year}`);

            date.setHours(23);
            date.setMinutes(59);
            date.setSeconds(59);

            valid = date >= new Date();

            commonForms.toggleFormErrors(field, valid, message);
            commonForms.toggleCurrentErrors(field, valid, 'invalid-future-date', message);
        }

        return valid;
    },

    afterDate: function (field) {
        const minDate = commonForms.stringToDate(field.dataset.mindate);
        const trimmedValue = field.value.trim();
        const message = `This date must be after ${commonForms.leadingZeroes(minDate.getDate(), 2)}/${commonForms.leadingZeroes((minDate.getMonth() + 1), 2)}/${minDate.getFullYear()}`;
        let valid = false;

        if (commonForms.dateRegex(field) && trimmedValue.length) {
            const day = parseInt(trimmedValue.slice(0, 2));
            const month = parseInt(trimmedValue.slice(3, 5));
            const year = parseInt(trimmedValue.slice(6, 10));

            const date = new Date(`${month}/${day}/${year}`);

            date.setHours(23);
            date.setMinutes(59);
            date.setSeconds(59);

            valid = date >= minDate;

            commonForms.toggleFormErrors(field, valid, message);
            commonForms.toggleCurrentErrors(field, valid, 'invalid-after-date', message);
        }

        return valid;
    },

    pastDate: function (field) {
        const trimmedValue = field.value.trim();
        const message = 'This date must be in the past';
        let valid = false;

        if (commonForms.dateRegex(field) && trimmedValue.length) {
            const day = parseInt(trimmedValue.slice(0, 2));
            const month = parseInt(trimmedValue.slice(3, 5));
            const year = parseInt(trimmedValue.slice(6, 10));

            const date = new Date(`${month}/${day}/${year}`);

            valid = date < new Date();

            commonForms.toggleFormErrors(field, valid, message);
            commonForms.toggleCurrentErrors(field, valid, 'invalid-past-date', message);
        }

        return valid;
    },

    maxValue: function (field) {
        const maxValue = field.dataset.maxvalue;
        let valid;
        let message;

        if (!maxValue) {
            valid = true;
        } else {
            if (field.dataset.message) {
                message = field.dataset.message.replace('{value}', maxValue);
            } else {
                message = `This must not be greater than ${maxValue}`;
            }

            valid = Number(field.value) <= Number(maxValue);
        }
        commonForms.toggleFormErrors(field, valid, message);
        commonForms.toggleCurrentErrors(field, valid, 'invalid-max-value', message);

        return valid;
    },

    validCurrency: function (field) {
        const trimmedValue = field.value.trim();
        const message = 'Enter a currency amount with no more than two decimal places, e.g. 150.00';
        const numberRegex = new RegExp('^£?[0-9]+([.][0-9]{2}p?)?$');
        let valid = false;

        if (trimmedValue === '' || trimmedValue.match(numberRegex)) {
            valid = true;
        }

        // remove currency symbols
        field.value = field.value.replace(/[£p]/ig, '');

        commonForms.toggleFormErrors(field, valid, message);
        commonForms.toggleCurrentErrors(field, valid, 'invalid-currency', message);

        return valid;
    },

    /**
     *  Requires an address to have either building or street address.
     */
    requiredBuildingOrStreet: function (field) {
        const message = 'Enter an address, including building or street';
        const parentQuestion = field.closest('.js-building-street');
        const buildingEl = parentQuestion.querySelector('.js-manual-building');
        const streetEl = parentQuestion.querySelector('.js-manual-street');

        let valid = !!(buildingEl.value.trim() || streetEl.value.trim());

        commonForms.toggleFormErrors(buildingEl, valid, message);
        commonForms.toggleCurrentErrors(buildingEl, valid, 'invalid-required-address', message);

        return valid;
    },

    /**
     *  For a required postcode lookup, invalid unless address fields are showing
     *  (either will have been filled when address chosen, or will trigger their own validations if empty)
     */
    requiredPostcodeLookup(field) {
        /*
         if manual area visible, validate manual fields
         if results area visible, validate results
         if search area visible, validate search
        */
        const element = field.closest('.js-postcode-lookup');
        const lookupElement = element.querySelector('.ds_address__lookup');
        const resultsElement = element.querySelector('.ds_address__results');
        const manualElement = element.querySelector('.ds_address__manual');

        let valid = false;

        if (!lookupElement.classList.contains('fully-hidden')) {
            let message = "Enter a postcode and click 'Find address'";

            // todo: this should be a check for readonly mode
            if (true) {
                message += ', or enter an address manually';
            }

            const field = lookupElement.querySelector('.js-postcode-input');

            commonForms.toggleFormErrors(field, valid, message);
            commonForms.toggleCurrentErrors(field, valid, 'invalid-postcode-lookup', message);
        } else if (!resultsElement.classList.contains('fully-hidden')) {
            const value = resultsElement.querySelector('.js-results-select').value;

            if (!isNaN(+value) && +value > -1) {
                valid = true;
            }
        } else if (!manualElement.classList.contains('fully-hidden')) {

        }

        // const valid = false;
        return valid;
    },

    /**
     *  For a required RPZ postcode lookup - does not allow progress if address chosen is in a rent pressure zone.
     */
    requiredNonRPZAddress: function (field) {
        const message = 'You cannot use this form if the address chosen' +
            ' is in a Rent Pressure Zone.';
        const fieldInRpz = field.classList.contains('js-in-rpz');
        const valid = !fieldInRpz;

        commonForms.toggleFormErrors(field, valid, message);
        commonForms.toggleCurrentErrors(field, valid, 'invalid-required-non-rpz-address', message);

        return valid;
    },

    /**
     *  For a required RPZ postcode lookup - only allows progress if address chosen is in a rent pressure zone.
     */
    requiredRPZAddress: function (field) {
        const message = 'You can only use this form if the address chosen' +
            ' is in a Rent Pressure Zone.';
        const fieldInRpz = field.classList.contains('js-in-rpz');
        const valid = fieldInRpz;

        commonForms.toggleFormErrors(field, valid, message);
        commonForms.toggleCurrentErrors(field, valid, 'invalid-required-rpz-address', message);

        return valid;
    },

    /**
     *  For a required RPZ postcode lookup - does not allow progress if 'address not listed' is chosen.
     */
    noAddressNotListed: function (field) {
        const message = 'To complete this form you must choose an address';
        const addressNotListed = field.value === '-1';
        const valid = !addressNotListed;

        commonForms.toggleFormErrors(field, valid, message);
        commonForms.toggleCurrentErrors(field, valid, 'invalid-required-listed-address', message);

        return valid;
    },

    /**
     *  Requires a set of checkboxes to have at least one checked.
     *  todo: this is v similar to requiredRadio -- consolidate?
     */
    atLeastOneCheckbox: function (container) {
        const checkboxes = [].slice.call(container.querySelectorAll('input[type="checkbox"]'));
        const message = container.dataset.message || 'Select one or more options';

        let valid = false;

        checkboxes.forEach(checkbox => {
            if (checkbox.checked) {
                valid = true;
            }
        });

        commonForms.toggleFormErrors(container, valid, message);
        commonForms.toggleCurrentErrors(container, valid, 'invalid-minimum-one-checkbox', message);

        return valid;
    },

    /**
     *  Shows or hides individual field's current errors box (box below each field)
     */
    toggleCurrentErrors: function (field, valid, errorClass, message) {
        // add error to field's errors object
        field.errors = field.errors || {};
        if (!valid) {
            field.errors[errorClass] = message;
        } else {
            delete field.errors[errorClass];
        }

        let fieldId;
        let labelElement;

        if (field.nodeName !== 'INPUT' && field.nodeName !== 'SELECT' && field.nodeName !== 'TEXTAREA') {
            // assume fieldset
            if (field.querySelectorAll('input[type="radio"]').length) {
                fieldId = field.querySelectorAll('input[type="radio"]')[0].name;
            } else if (field.querySelectorAll('input[type="checkbox"]').length) {
                fieldId = field.querySelectorAll('input[type="checkbox"]')[0].id;
            } else {
                fieldId = field.id;
            }
            labelElement = field.querySelector('legend');
        } else {
            fieldId = field.id;
            labelElement = document.querySelector(`[for=${field.id}]`);
        }

        if (!fieldId) { return; }

        // update error display
        let question;
        if (document.getElementById(fieldId) && document.getElementById(fieldId).closest('.ds_question')) {
            question = document.getElementById(fieldId).closest('.ds_question');
        } else {
            // this essentially swallows errors when no ds_question element is found
            question = document.createElement('div');
        }

        // remove error container
        let errorContainer = document.querySelector(`#${fieldId}-errors`);
        if (errorContainer) {
            errorContainer.parentNode.removeChild(errorContainer);
        }

        if (Object.keys(field.errors).length) {
            question.classList.add('ds_question--error');
            field.setAttribute('aria-invalid', true);

            // add an error container
            if (field.getAttribute('type') !== 'hidden') {
                field.setAttribute('aria-describedby', `${fieldId}-errors`);
                errorContainer = document.createElement('div');
                errorContainer.classList.add('ds_question__error-message');
                errorContainer.id = `${fieldId}-errors`;
                errorContainer.dataset.field = fieldId;


                // todo: these lines are from a merge conflict
                if (field.nodeName === 'FIELDSET') {
                    field.querySelector('legend').insertAdjacentElement('afterend', errorContainer);
                } else {
                    document.querySelector(`[for=${field.id}]`).insertAdjacentElement('afterend', errorContainer);
                }
                labelElement.insertAdjacentElement('afterend', errorContainer);
                // todo: end merge conflict
            }

            for (let key in field.errors) {
                const errorEl = document.createElement('p');
                errorEl.classList.add(key);
                errorEl.innerHTML = `<span class="visually-hidden">Error:</span> ${field.errors[key]}`;
                errorContainer.appendChild(errorEl);
            }
        } else {
            question.classList.remove('ds_question--error');
            field.removeAttribute('aria-invalid');
        }

        if (errorContainer) {
            if (window.DS && window.DS.tracking) {
                window.DS.tracking.init(errorContainer);
            }
        }
    },

    /**
     * Adds or removes error messages to main error container (at top of page)
     */
    toggleFormErrors: function (field, valid, message, fieldName) {
        this.errors = this.errors || [];
        this.errors = this.errors.filter(item => !(item.fragmentId === field.id && item.message === message));
        fieldName = fieldName || this.getLabelText(field);
        if (!valid) {
            this.errors.push({ fragmentId: field.id, fieldName: fieldName, message: message });
        }
    },

    /**
     * Validates $field
     *
     * @param {object} $field - the form field to validate
     * @param {validationChecks} - array of validation functions to test $field against
     * @param {boolean} highlightField - should a visual indicator be applied to the UI
     * @returns {boolean} whether the field value is valid
     */
    validateInput: function (field, validationChecks, highlightField = true) {
        let valid = true;

        if (field.dataset.highlightonerror === 'false') {
            highlightField = false;
        }

        for (const element of validationChecks) {
            if (element && element(field) === false) {
                valid = false;
            }
        }

        if (highlightField) {
            if (!valid) {
                field.classList.add('ds_input--error');
            } else {
                field.classList.remove('ds_input--error');
            }
        }

        return valid;
    },

    requiredCheckbox: function (field) {
        const valid = field.checked;

        let fieldName = commonForms.getLabelText(field);
        if (field.dataset.label) {
            fieldName = field.dataset.label;
        }

        let message = 'This field is required';
        if (field.dataset.message) {
            message = '';
            if (fieldName) {
                message += `<strong>${fieldName}</strong> <br>`;
            }
            message += `${field.dataset.message}`;
        }

        commonForms.toggleFormErrors(field, valid, 'invalid-required', fieldName, message);
        commonForms.toggleCurrentErrors(field, valid, 'invalid-required', message);

        return valid;
    },

    getLabelText: function (field) {
        if (field.getAttribute('aria-labeledby')) {
            return document.getElementById(field.getAttribute('aria-labeledby')).innerHTML;
        } else if (field.nodeName === 'FIELDSET') {
            return field.querySelector('legend').innerText;
        } else {
            return document.querySelector(`label[for="${field.id}"]`).innerHTML;
        }
    },

    track: function(details) {
        window.dataLayer = window.dataLayer || [];
        window.dataLayer.push(details);
    },

    pageNavFunction: function (startSlug, currentStep) {
        // which extra buttons do we show?
        const pageNavData = {};

        pageNavData.startPage = currentStep.slug === startSlug;

        // does the current step have a data-group attribute and is it the last of its kind?
        const groupName = document.querySelector(`section[data-step="${currentStep.slug}"]`).dataset.group;
        if (groupName) {
            const stepIsLastInGroup = document.querySelector(`[data-group="${groupName}"]:last-child`) === document.querySelector(`section[data-step="${currentStep.slug}"]`);

            if (stepIsLastInGroup) {
                if (currentStep.section === 'landlords') {
                    pageNavData.addLandlord = true;
                } else if (currentStep.section === 'tenants') {
                    pageNavData.addTenant = true;
                }
            }
        }
        return pageNavData;
    },

    validateStep: function (container) {
        /* look for data-validation attributes in current step & PERFORM VALIDATION
         * do not allow progress if invalid
         */

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
    },

    objectValues: function (object, emptyValue = []) {
        return object ? Object.values(object) : emptyValue;
    },

    objectKeys: function (object) {
        return Object.keys(object || {});
    },

    renderErrorSummary(errors) {
        const errorSumaryContainerElement = document.querySelector('.js-error-summary-container');
        errorSumaryContainerElement.innerHTML = errorSummaryTemplate.render({ errors: errors });

        if (errors.length > 0) {
            temporaryFocus(errorSumaryContainerElement.querySelector('.ds_error-summary'));
        }
    }
};

export default commonForms;
