// SUSPICIOUS ACTIVITY FORM

/* global grecaptcha */

'use strict';

import commonForms from './common.forms';
import _ from 'tinydash';
import 'textchange';
import $ from 'jquery';

let submitted = false;
let success = false;
const $suspiciousActivityDetails = $('#suspicious-activity-details');
const $additionalInformation = $('#additional-information');
const $location = $('#location');
const $dateTime = $('#date-time');
const $vesselInformation = $('#vessel-information');
const $telephoneNumber = $('#telephone-number');
const $recaptcha = $('#recaptcha');
const $clientError = $('.client-error');
const $formErrors = $('.form-errors');

/**
 * Checks if recaptcha is completed. Appends or Removes an error message.
 *
 * @param {object} $field - the form field to be checked
 * @returns {boolean} whether the field value is valid
 */
const recaptchaCompleted = function($field) {
    const valid = grecaptcha.getResponse() !== '';
    const className = '.' + $field.attr('id') + '-errors';

    if (!valid) {
        if($(className + ' .required').length === 0) {
            $(className).append('<li class="error required">reCAPTCHA is required</li>');
        }
    } else {
        $(className + ' .required').remove();
    }

    return valid;
};

/**
 * Checks if $field is completed. Appends or Removes an error message.
 *
 * @param {object} $field - the form field to be checked
 * @returns {boolean} whether the field value is valid
 */
const requiredField = function($field) {
    const trimmedValue = $.trim($field.val());
    const className = '.' + $field.attr('id') +'-errors';
    const label = $('label[for="' + $field.attr('id') + '"]');

    const valid = trimmedValue !== '';

    if (!valid) {
        if($(className + ' .required').length === 0) {
            $(className).append('<li class="error required">' + label.text() + ' is' +
                ' required</li>');
        }
    } else {
        $(className + ' .required').remove();
    }

    return valid;
};

/**
 * Appends an error container for $field to the DOM
 *
 * @param {object} $field - the form field to log errors against
 */
const appendErrorContainer = function($field) {
    const className = $field.attr('id') + '-errors';
    if($('.' + className).length === 0) {
        $formErrors.append('<ul class="' + className + '"></ul>');
    }
};

/**
 * Removes the error container for $field from the DOM
 *
 * @param {object} $field - the form field errors were logged against
 */
const removeErrorContainer = function($field) {
    const className = $field.attr('id') + '-errors';
    if($('.' + className).length !== 0) {
        $('.' + className).remove();
    }
};

/**
 * Validates $field
 *
 * @param {object} $field - the form field to validate
 * @param {boolean} highlightField - should a visual indicator be applied to the UI
 * @param {arguments} - [2 ..] list of validation functions to test $field against
 * @returns {boolean} whether the field value is valid
 */
const validateField = function($field, highlightField) {

    appendErrorContainer($field);

    const validationChecks = [];

    for(let i = 2; i < arguments.length; i++) {
        validationChecks.push(arguments[i]($field));
    }

    const valid = validationChecks.every(function(x) { return x; } );

    const $label = $('label[for="' + $field.attr('id') + '"]');

    if (highlightField) {
        if (!valid) {
            $field.css('border-color', '#d32205');
            $label.css('color', '#d32205');
        } else {
            $field.css('border-color', '#487f33');
            $label.css('color', '#333');
        }
    }

    if (valid) {
        removeErrorContainer($field);
    }

    return valid;
};

/**
 * Returns form data as object
 *
 * @returns {object} form data
 */
const getFormData = function() {
    return  {
        suspiciousActivityDetails: $suspiciousActivityDetails.val(),
        additionalInformation: $additionalInformation.val() || '',
        location: $location.val() || '',
        dateTime: $dateTime.val() || '',
        vesselInformation: $vesselInformation.val() || '',
        telephoneNumber: $telephoneNumber.val() || '',
        recaptcha: grecaptcha.getResponse()
    };
};

/**
 * Checks all form field to determine if form is valid
 * Hightlights UI if highlightField
 *
 * @returns {boolean} is the form valid
 */
const formIsValid = function (highlightField) {
    $formErrors.html('');

    const suspiciousActivityDetails = validateField($suspiciousActivityDetails, highlightField, requiredField);
    const location = validateField($location, highlightField, requiredField);
    const dateTime = validateField($dateTime, highlightField, requiredField);
    const vesselInformation = validateField($vesselInformation, highlightField);
    const telephoneNumber = validateField($telephoneNumber, highlightField);
    const additionalInformation = validateField($additionalInformation, highlightField);
    const recaptcha = validateField($recaptcha, highlightField, recaptchaCompleted);

    return suspiciousActivityDetails && location && dateTime && vesselInformation && telephoneNumber && additionalInformation && recaptcha; //NOSONAR
};

/**
 * Shows or hides main error container in the DOM
 */
const showOrHideErrorBox = function () {
    if (submitted) {
        if (!formIsValid(false)) {
            $clientError.removeClass('fully-hidden');
        } else {
            $clientError.addClass('fully-hidden');
        }
    }
};

const suspiciousActivityForm = {};

/**
 * Module entry point
 */
suspiciousActivityForm.init = function () {
    $('form#suspicious-activity-form').on('submit', function(e) {
        submitted = true;
        e.preventDefault();

        if(!formIsValid(true)) {
            $clientError.removeClass('fully-hidden');
            window.location = '#feedback-box';
            return;
        }

        $clientError.addClass('fully-hidden');

        const postData = getFormData();

        $.ajax({
            type: 'POST',
            url: '/service/form/report-activity/',
            contentType:'application/json',
            data: JSON.stringify(postData)
        })
            .done(function() {
                success = true;
                $('.server-success').removeClass('fully-hidden');
                $('.server-error').addClass('fully-hidden');
                $('.main-content').hide();
                window.location = '#feedback-box';
            })
            .fail(function() {
                $('.server-error').removeClass('fully-hidden');
                $('.server-success').addClass('fully-hidden');
                window.location = '#feedback-box';
            });

    });
};

/**
 * Exposes recaptcha validation function to global window object to allow reCAPTCHA to find it
 */
window.checkRecaptcha = function() {
    // recaptcha can still timeout after the form has been successfully submitted. Don't
    // show the error msg if this happens
    if (success) {
        return;
    }

    validateField($recaptcha, recaptchaCompleted);
    showOrHideErrorBox();
};

window.format = suspiciousActivityForm;
window.format.init();

export default suspiciousActivityForm;
