// COMMON FORMS

/* global grecaptcha */

'use strict';

import $ from 'jquery';

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
        let fragments = string.split('/');
        return new Date(`${fragments[1]}/${fragments[0]}/${fragments[2]}`);
    },

    /**
     * Takes a date object and returns a date string in the format we use in forms (dd/MM/yyyy)
     * @param {date}
     * @returns {string}
     */
    dateToString: function (date) {
        return `${commonForms.leadingZeroes(date.getDate(), 2)}/${commonForms.leadingZeroes(date.getMonth() + 1, 2)}/${date.getFullYear()}`;
    },

    setupRecaptcha: function () {
        let recaptchaSuccess = false;

        /* Exposes recaptcha validation function to global window object to allow reCAPTCHA to find it
            */
        window.checkRecaptcha = function () {
            // check reCAPTCHA
            recaptchaSuccess = commonForms.validateField($('#recaptcha'), true, commonForms.recaptchaCompleted);

            if (!recaptchaSuccess) {
                return false;
            } else {
                $('.js-document-container').find('.js-download-file').removeAttr('disabled');
            }
        };

        window.expireRecaptcha = function () {
            $('.js-document-container').find('.js-download-file').attr('disabled', true);
            recaptchaSuccess = false;
            grecaptcha.reset();
        };
    },

    /**
     * Checks if recaptcha is completed. Appends or Removes an error message.
     *
     * @param {object} $field - the form field to be checked
     * @returns {boolean} whether the field value is valid
     */
    recaptchaCompleted: function ($field) {
        const valid = grecaptcha.getResponse() !== '';
        const className = `.${$field.attr('id')}-errors`;

        if (!valid) {
            if ($(`${className} .recaptcha`).length === 0) {
                $(className).append('<li class="error recaptcha"><strong>reCAPTCHA</strong> <br>This field is required</li>');
            }
        } else {
            $(`${className} .recaptcha`).remove();
        }

        return valid;
    },

    /**
     * Appends an error container for $field to the DOM
     *
     * @param {object} $field - the form field to log errors against
     */
    appendErrorContainer: function ($field) {
        const $formErrors = $('.form-errors');

        const className = $field.attr('id') + '-errors';
        if ($(`.${className}`).length === 0) {
            $formErrors.append(`<ul class="ds_error-summary__list  ${className}"></ul>`);
        }
    },

    /**
     * Removes the error container for $field from the DOM
     *
     * @param {object} $field - the form field errors were logged against
     */
    removeErrorContainer: function ($field) {
        const className = $field.attr('id') + '-errors';
        if ($(`${className}`).length !== 0) {
            $(`${className}`).remove();
        }
    },

    /**
     * Checks if $field is completed. Appends or Removes an error message.
     *
     * @param {object} $field - the form field to be checked
     * @returns {boolean} whether the field value is valid
     */
    requiredField: function ($field, customMessage) {
        const trimmedValue = $.trim($field.val());
        const fieldName = commonForms.getLabelText($field);

        const valid = trimmedValue !== '';

        let message = 'This field is required';
        if (customMessage) {
            message = '';
            if (fieldName) {
                message += `<strong>${fieldName}</strong> <br>`;
            }
            message += `${customMessage}`;
        }

        commonForms.toggleFormErrors($field, valid, 'invalid-required', fieldName, message);
        commonForms.toggleCurrentErrors($field, valid, 'invalid-required', message);

        return valid;
    },

    regexMatch: function ($field) {
        const trimmedValue = $.trim($field.val()),
            regex = new RegExp($field.attr('pattern')),
            message = $field.data('errormessage'),
            fieldName = commonForms.getLabelText($field);

        const valid = trimmedValue === '' || trimmedValue.match(regex);

        commonForms.toggleFormErrors($field, valid, 'invalid-regex', fieldName, message);
        commonForms.toggleCurrentErrors($field, valid, 'invalid-regex', message);

        return valid;
    },

    /**
     * Checks whether if $field is less than or equal to maxLength.
     * Appends or Removes an error message.
     * Updates character count
     *
     * @param {object} $field - the form field to be checked
     * @param {int} maxLength - the permitted character length of $field
     * @returns {boolean} whether the field value is valid
     */
    maxCharacters: function ($field, maxLength) {

        const valid = $field.val().length <= maxLength;
        const className = `.${$field.attr('id')}-errors`;
        const fieldName = commonForms.getLabelText($field);

        if (!valid) {
            if ($(className + ' .too-many').length === 0) {
                $(className).append(`<li class="error too-many">${fieldName} has too many characters</li>`);
            }
        } else {
            $(`${className} .too-many`).remove();
        }

        $field.siblings('.js-count').html((maxLength - $field.val().length));

        return valid;
    },

    numericOnly: function ($field, min, max) {
        const trimmedValue = $.trim($field.val());
        min = min || 1;
        max = max || 9999;
        const valid = trimmedValue === '' || ($field.val() >= min && $field.val() <= max);
        const className = `.${$field.attr('id')}-errors`;
        const fieldName = commonForms.getLabelText($field);
        if (!valid) {
            if ($(className + ' .required').length === 0) {
                $(className).append(`<li class="error required"><strong>${fieldName}</strong> please specify a numeric value between ${min} and ${max}</li>`);
            }
        } else {
            $(`${className} .too-many`).remove();
        }

        return valid;
    },

    atLeastOneRequired: function (fields, container, highlightField) {
        const $container = $(container);
        this.appendErrorContainer($container);
        let provided = false;
        for (let i = 0; i < fields.length; i++) {
            provided = provided || fields[i].val();
        }
        if (highlightField) {
            if (!provided) {
                $container.closest('.form-group')
                    .addClass('js-has-error')
                    .removeClass('js-has-success');
            } else {
                $container.closest('.form-group')
                    .removeClass('js-has-error')
                    .addClass('js-has-success');
            }
        }

        if (provided) {
            this.removeErrorContainer($container);
        } else {
            this.requiredField($container, 'Please specify at least one document');
        }

        return provided;
    },

    // New form validations

    validPostcode: function ($field) {
        let message = 'Please enter a valid postcode, for example EH6 6QQ';
        let trimmedValue = $.trim($field.val());

        let postcodeRegExp = new RegExp('^[A-Z]{1,2}[0-9R][0-9A-Z]?[0-9][ABD-HJLNP-UW-Z]{2}$');
        let postcodeValue = trimmedValue.toUpperCase().replace(/\s+/g, '');

        let valid = trimmedValue === '' || postcodeValue.match(postcodeRegExp) !== null;

        commonForms.toggleFormErrors($field, valid, 'invalid-postcode', 'Postcode', message);
        commonForms.toggleCurrentErrors($field, valid, 'invalid-postcode', message);

        return valid;
    },

    validEmail: function ($field) {
        const message = 'Please enter a valid email address, for example jane.smith@gov.scot';
        const trimmedValue = $.trim($field.val());

        const regex = /^[^@ ]+@[^@ ]+\.[^@ ]+$/;

        const valid = trimmedValue === '' || trimmedValue.match(regex) !== null;

        commonForms.toggleFormErrors($field, valid, 'invalid-email', 'Email address', message);
        commonForms.toggleCurrentErrors($field, valid, 'invalid-email', message);

        return valid;
    },

    requiredRadio: function ($field) {
        const radioButtons = $field.find('input:radio');

        const title = $field.find('legend').html();
        const message = 'Please select one of the options';

        let valid = false;

        for (let i = 0; i < radioButtons.length; i++) {
            if ($(radioButtons[i]).is(':checked')) {
                valid = true;
            }
        }

        commonForms.toggleFormErrors($field, valid, 'required', title, message);
        commonForms.toggleCurrentErrors($field, valid, 'required', message);

        return valid;
    },

    requiredDropdown: function ($field) {
        const value = $field.val();
        const fieldName = commonForms.getLabelText($field);
        const message = 'Please select one of the options';

        const valid = value !== null && value !== '';

        commonForms.toggleFormErrors($field, valid, 'required', fieldName, message);
        commonForms.toggleCurrentErrors($field, valid, 'required', message);

        return valid;
    },

    validPhone: function ($field) {
        const trimmedValue = $.trim($field.val()).replace(/\s+/g, '');
        const message = 'Use only numbers 0-9 and the "+" character. Phone numbers should be less than 20 characters long.';

        // A regular expression matching only up to 20 numbers and possibly a '+' character at the beginning
        const regex = new RegExp('^\\+?[0-9]{0,20}$');

        const valid = trimmedValue.match(regex) !== null;

        commonForms.toggleFormErrors($field, valid, 'invalid-phone', 'Phone number', message);
        commonForms.toggleCurrentErrors($field, valid, 'invalid-phone', message);

        return valid;
    },

    isValidDate: function (day, month, year) {
        // solution by Matti Virkkunen: http://stackoverflow.com/a/4881951
        const isLeapYear = year % 4 === 0 && year % 100 !== 0 || year % 400 === 0;
        const daysInMonth = [31, isLeapYear ? 29 : 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31][month - 1];

        const valid = day <= daysInMonth;

        return valid;
    },

    dateRegex: function ($field) {
        const trimmedValue = $.trim($field.val());
        const fieldName = $(`label[for="${$field.attr('id')}"]`).text();
        const message = 'Please enter the date as DD/MM/YYYY';

        // A regular expression only allowing dd/mm/yyyy format
        const regex = new RegExp('^\\d\\d\\/\\d\\d\\/\\d\\d\\d\\d$');

        // check date is a valid date
        const day = parseInt(trimmedValue.slice(0, 2));
        const month = parseInt(trimmedValue.slice(3, 5));
        const year = parseInt(trimmedValue.slice(6, 10));

        const valid = trimmedValue === '' || (commonForms.isValidDate(day, month, year) && trimmedValue.match(regex) !== null);

        commonForms.toggleFormErrors($field, valid, 'invalid-date-format', fieldName, message);
        commonForms.toggleCurrentErrors($field, valid, 'invalid-date-format', message);

        return valid;
    },

    futureDate: function ($field) {
        const trimmedValue = $.trim($field.val());
        const fieldName = commonForms.getLabelText($field);
        const message = 'This date must be in the future';
        let valid = false;

        if (commonForms.dateRegex($field) && trimmedValue.length) {
            const day = parseInt(trimmedValue.slice(0, 2));
            const month = parseInt(trimmedValue.slice(3, 5));
            const year = parseInt(trimmedValue.slice(6, 10));

            const date = new Date(`${month}/${day}/${year}`);

            date.setHours(23);
            date.setMinutes(59);
            date.setSeconds(59);

            valid = date >= new Date();

            commonForms.toggleFormErrors($field, valid, 'invalid-future-date', fieldName, message);
            commonForms.toggleCurrentErrors($field, valid, 'invalid-future-date', message);
        }

        return valid;
    },

    afterDate: function ($field) {
        const minDate = commonForms.stringToDate($field.attr('data-mindate'));
        const trimmedValue = $.trim($field.val());
        const fieldName = commonForms.getLabelText($field);
        const message = `This date must be after ${commonForms.leadingZeroes(minDate.getDate(), 2)}/${commonForms.leadingZeroes((minDate.getMonth() + 1), 2)}/${minDate.getFullYear()}`;
        let valid = false;

        if (commonForms.dateRegex($field) && trimmedValue.length) {
            const day = parseInt(trimmedValue.slice(0, 2));
            const month = parseInt(trimmedValue.slice(3, 5));
            const year = parseInt(trimmedValue.slice(6, 10));

            const date = new Date(`${month}/${day}/${year}`);

            date.setHours(23);
            date.setMinutes(59);
            date.setSeconds(59);

            valid = date >= minDate;

            commonForms.toggleFormErrors($field, valid, 'invalid-after-date', fieldName, message);
            commonForms.toggleCurrentErrors($field, valid, 'invalid-after-date', message);
        }

        return valid;
    },

    pastDate: function ($field) {
        const trimmedValue = $.trim($field.val());
        const fieldName = commonForms.getLabelText($field);
        const message = 'This date must be in the past';
        let valid = false;

        if (commonForms.dateRegex($field) && trimmedValue.length) {
            const day = parseInt(trimmedValue.slice(0, 2));
            const month = parseInt(trimmedValue.slice(3, 5));
            const year = parseInt(trimmedValue.slice(6, 10));

            const date = new Date(`${month}/${day}/${year}`);

            valid = date < new Date();

            commonForms.toggleFormErrors($field, valid, 'invalid-past-date', fieldName, message);
            commonForms.toggleCurrentErrors($field, valid, 'invalid-past-date', message);
        }

        return valid;
    },

    maxValue: function ($field) {
        let maxValue = $field.attr('data-maxvalue');
        let valid;
        let fieldName = commonForms.getLabelText($field);
        let message;

        if (!maxValue) {
            valid = true;
        } else {
            if ($field.attr('data-message')) {
                message = $field.attr('data-message').replace('{value}', maxValue);
            } else {
                message = `This must not be greater than ${maxValue}`;
            }

            valid = Number($field.val()) <= Number(maxValue);
        }
        commonForms.toggleFormErrors($field, valid, 'invalid-max-value', fieldName, message);
        commonForms.toggleCurrentErrors($field, valid, 'invalid-max-value', message);

        return valid;
    },

    validCurrency: function ($field) {
        const trimmedValue = $.trim($field.val());
        const fieldName = commonForms.getLabelText($field);
        const message = 'Please enter a currency amount with no more than two decimal places, e.g. 150.00';

        const numberRegex = new RegExp('^£?[0-9]+([.][0-9]{2}p?)?$');
        let valid = false;

        if (trimmedValue === '' || trimmedValue.match(numberRegex)) {
            valid = true;
        }

        // remove currency symbols
        $field.val($field.val().replace(/[£p]/ig, ''));

        commonForms.toggleFormErrors($field, valid, 'invalid-currency', fieldName, message);
        commonForms.toggleCurrentErrors($field, valid, 'invalid-currency', message);

        return valid;
    },

    /**
     *  Requires an address to have either building or street address.
     */
    requiredBuildingOrStreet: function ($field) {
        const message = 'Please enter an address, including building or street';
        const building = $.trim($field.find('.building').val());
        const street = $.trim($field.find('.street').val());

        let valid = false;
        const buildingOrStreet = building || street;

        if (buildingOrStreet) {
            valid = true;
        }

        commonForms.toggleFormErrors($field, valid, 'invalid-required-address', 'Address', message);
        commonForms.toggleCurrentErrors($field.find('.building'), valid, 'invalid-required-address', message);
        commonForms.toggleCurrentErrors($field.find('.street'), valid, 'invalid-required-address', message);

        return valid;
    },

    /**
     *  For a required postcode lookup, invalid unless address fields are showing
     *  (either will have been filled when address chosen, or will trigger their own validations if empty)
     */
    requiredPostcodeLookup: function ($field) {
        const manualAddress = $field.closest('.js-postcode-lookup').find('.address-manual');
        const addressDropdown = $field.closest('.js-postcode-lookup').find('.postcode-results');

        let message = 'Please enter a postcode and click "Find address"';

        if (manualAddress.length > 0) {
            message += ', or enter an address manually';
        }

        const valid = (manualAddress.length > 0 && !manualAddress.hasClass('fully-hidden')) ||
            (addressDropdown.length > 0 && !addressDropdown.hasClass('fully-hidden'));

        commonForms.toggleFormErrors($field, valid, 'invalid-required-postcode', 'Postcode lookup', message);
        commonForms.toggleCurrentErrors($field, valid, 'invalid-required-postcode', message);

        return valid;
    },

    /**
     *  For a required RPZ postcode lookup - does not allow progress if address chosen is in a rent pressure zone.
     */
    requiredNonRPZAddress: function ($field) {
        const message = 'You cannot use this form if the address chosen' +
            ' is in a Rent Pressure Zone.';

        const fieldInRpz = $field.hasClass('js-in-rpz');

        const valid = !fieldInRpz;

        commonForms.toggleFormErrors($field, valid, 'invalid-required-non-rpz-address', 'Postcode lookup', message);
        commonForms.toggleCurrentErrors($field, valid, 'invalid-required-non-rpz-address', message);

        return valid;
    },

    /**
     *  For a required RPZ postcode lookup - only allows progress if address chosen is in a rent pressure zone.
     */
    requiredRPZAddress: function ($field) {
        const message = 'You can only use this form if the address chosen' +
            ' is in a Rent Pressure Zone.';

        const fieldInRpz = $field.hasClass('js-in-rpz');

        const valid = fieldInRpz;

        commonForms.toggleFormErrors($field, valid, 'invalid-required-rpz-address', 'Postcode lookup', message);
        commonForms.toggleCurrentErrors($field, valid, 'invalid-required-rpz-address', message);

        return valid;
    },

    /**
     *  For a required RPZ postcode lookup - does not allow progress if 'address not listed' is chosen.
     */
    noAddressNotListed: function ($field) {
        const message = 'To complete this form you must choose an address';
        const addressNotListed = $field.val() === '-1';

        const valid = !addressNotListed;

        commonForms.toggleFormErrors($field, valid, 'invalid-required-listed-address', 'Postcode lookup', message);
        commonForms.toggleCurrentErrors($field, valid, 'invalid-required-listed-address', message);

        return valid;
    },

    /**
     *  Requires a set of checkboxes to have at least one checked.
     *  todo: this is v similar to requiredRadio -- consolidate?
     */
    atLeastOneCheckbox: function ($field) {
        const checkboxes = $field.find('input:checkbox');

        const title = $field.find('legend').html();
        const message = $field.attr('data-message') || 'Please select one of the options';

        let valid = false;

        for (let i = 0; i < checkboxes.length; i++) {
            if ($(checkboxes[i]).is(':checked')) {
                valid = true;
            }
        }

        commonForms.toggleFormErrors($field, valid, 'invalid-minimum-one-checkbox', title, message);
        commonForms.toggleCurrentErrors($field, valid, 'invalid-minimum-one-checkbox', message);

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

        // obtain field ID
        let fieldId;

        if (field[0].nodeName !== 'INPUT' && field[0].nodeName !== 'SELECT' && field[0].nodeName !== 'TEXTAREA') {
            // assume fieldset
            if (field.find('input[type="radio"]').length) {
                fieldId = field.find('input[type="radio"]')[0].name;
            } else if (field.find('input[type="checkbox"]').length) {
                fieldId = field.find('input[type="checkbox"]')[0].id;
            } else {
                fieldId = field[0].id;
            }
        } else {
            fieldId = field[0].id;
        }

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
            field.attr('aria-invalid', true);

            let errorContainer;

            // add an error container
            if (field.attr('type') !== 'hidden') {
                field[0].setAttribute('aria-describedby', `${fieldId}-errors`);
                errorContainer = $(`<div class="ds_question__message" id="${fieldId}-errors" data-field="${fieldId}"></div>`);
                if (field.find('legend').length) {
                    errorContainer.insertAfter(field.closest('.ds_question').find('legend')[0]);
                } else {
                    errorContainer.insertAfter(field.closest('.ds_question').find('label')[0]);
                }
            }

            Object.entries(field.errors).forEach(error => {
                errorContainer.append(`<p class="${error[0]}">${error[1]}</p>`);
            });
        } else {
            question.classList.remove('ds_question--error');
            field.removeAttr('aria-invalid');
        }
    },

    /**
     * Adds or removes error messages to main error container (at top of page)
     */
    toggleFormErrors: function (field, valid, errorClass, fieldName, message) {
        const className = `.${field.attr('id')}-errors`;
        const errorName = className + ' .' + errorClass;
        if (!valid) {
            if ($(errorName).length === 0) {
                $(className).append(`<p class="error ${errorClass}"><a class="form-nav" href="#${field.attr('id')}">${$.trim(fieldName)}: <span class="underline">${$.trim(message)}</span></a></p>`);
            } else {
                $(errorName).removeClass('error-grey');
            }
        } else {
            if ($('.client-error').hasClass('fully-hidden')) {
                $(errorName).remove();
            } else {
                $(errorName).addClass('error-grey');
            }
        }
    },

    /**
     * Validates $field (newer version)
     *
     * @param {object} $field - the form field to validate
     * @param {boolean} highlightField - should a visual indicator be applied to the UI
     * @param {validationChecks} - array of validation functions to test $field against
     * @returns {boolean} whether the field value is valid
     */
    validateInput: function ($field, validationChecks, highlightField = true) {
        commonForms.appendErrorContainer($field);
        let valid = true;
        let question;

        if ($field.hasClass('ds_question')) {
            question = $field;
        } else {
            question = $field.closest('.ds_question');
        }

        for (let i = 0; i < validationChecks.length; i++) {
            if (validationChecks[i]($field) === false) {
                valid = false;
            }
        }

        if (highlightField) {
            if (!valid) {
                $field.addClass('ds_input--error');
            } else {
                $field.removeClass('ds_input--error');
            }
        }

        return valid;
    },

    /**
     * Validates $field
     *
     * @param {object} $field - the form field to validate
     * @param {boolean} highlightField - should a visual indicator be applied to the UI
     * @param {arguments} - [2 ..] list of validation functions to test $field against
     * @returns {boolean} whether the field value is valid
     */
    validateField: function ($field, highlightField) {
        commonForms.appendErrorContainer($field);

        const validationChecks = [];

        for (let i = 2; i < arguments.length; i++) {
            validationChecks.push(arguments[i]($field));
        }

        const valid = validationChecks.every(function (x) {
            return x;
        });

        if (highlightField) {
            if (!valid) {
                $field.closest('.form-group')
                    .addClass('js-has-error')
                    .removeClass('js-has-success');
            } else {
                $field.closest('.form-group')
                    .removeClass('js-has-error')
                    .addClass('js-has-success');
            }
        }

        if (valid) {
            commonForms.removeErrorContainer($field);
        }

        return valid;
    },

    updateErrors: function (errorFields, value, field) {
        if (!value && errorFields) {
            errorFields.push(field);
        }
    },

    /**
     * Shows or hides main error container in the DOM
     */
    showOrHideErrorBox: function (submitted, $clientError, isValid) {
        if (submitted) {
            if (!isValid) {
                $clientError.removeClass('fully-hidden');
            } else {
                $clientError.addClass('fully-hidden');
            }
        }
    },

    // Legacy function - used in Brexit form & letting agent registration form
    requiredCheckbox: function ($field) {
        const valid = $field.prop('checked'),
            className = `.${$field.attr('id')}-errors`,
            message = $field.data('errormessage');

        if (!valid) {
            if ($(className + ' .requiredCheckbox').length === 0) {
                $(className).append(`<li class="error requiredCheckbox">${message}</li>`);
            }
        } else {
            $(`${className} .requiredCheckbox`).remove();
        }

        return valid;
    },

    getLabelText: function ($field) {
        if ($field.attr('aria-labeledby')) {
            return $(`#${$field.attr('aria-labeledby')}`).html();
        } else if ($field[0].nodeName === 'FIELDSET') {
            return null;
        } else {
            return $(`label[for="${$field.attr('id')}"]`).html();
        }
    },

    track: function(details) {
        window.dataLayer = window.dataLayer || [];
        window.dataLayer.push(details);
    },

    outwardCode: function(value) {
        const trimmed = value.replace(/\s+/g, '');
        return trimmed.substring(0, trimmed.length - 3);
    },

    pageNavFunction: function (startSlug, currentStep) {
        // which extra buttons do we show?
        const pageNavData = {};

        pageNavData.startPage = currentStep.slug === startSlug;

        // does the current step have a data-group attribute and is it the last of its kind?
        const groupName = $(`section[data-step="${currentStep.slug}"]`).attr('data-group');
        const stepIsLastInGroup = $(`[data-group="${groupName}"]:last`).is(`section[data-step="${currentStep.slug}"]`);

        if (stepIsLastInGroup) {
            if (currentStep.section === 'landlords') {
                pageNavData.addLandlord = true;
            } else if (currentStep.section === 'tenants') {
                pageNavData.addTenant = true;
            }
        }

        return pageNavData;
    },

    validateStep: function (step) {
        /* look for data-validation attributes in current step & PERFORM VALIDATION
         * do not allow progress if invalid
         */
        const stepContainer = $(`section[data-step="${step.slug}"]`);
        const fieldsThatNeedToBeValidated = stepContainer.find('[data-validation]:visible');

        fieldsThatNeedToBeValidated.each(function (index, element) {
            const validations = element.getAttribute('data-validation').split(' ');
            const validationChecks = [];
            for (let i = 0, il = validations.length; i < il; i++) {
                if (commonForms[validations[i]]) {
                    validationChecks.push(commonForms[validations[i]]);
                }
            }

            commonForms.validateInput($(this), validationChecks);
        });

        const invalidFields = stepContainer.find('[aria-invalid="true"]:visible');

        return invalidFields.length === 0;
    },

    objectValues: function (object, emptyValue = []) {
        return object ? Object.values(object) : emptyValue;
    },

    objectKeys: function (object) {
        return Object.keys(object || {});
    }
};

export default commonForms;
