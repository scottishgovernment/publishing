/* global grecaptcha */

'use strict';

import commonForms from '../../tools/forms';
import _ from '../../vendor/lodash/dist/tinydash.es6';
import $ from 'jquery';

let submitted = false,
    success = false,
    isValid = false;
const $numberOfForms = $('#disclosure-bdo-number-of-forms'),
    $addressLine1 = $('#disclosure-bdo-address-line-1'),
    $addressLine2 = $('#disclosure-bdo-address-line-2'),
    $town = $('#disclosure-bdo-town'),
    $postcode = $('#disclosure-bdo-postcode'),
    $country = $('#disclosure-bdo-country'),
    $telephone = $('#disclosure-bdo-telephone'),
    $email = $('#disclosure-bdo-email'),
    $name = $('#disclosure-bdo-name'),
    $recaptcha = $('#recaptcha'),
    $clientError = $('.client-error'),
    $formErrors = $('.form-errors');

/**
 * Returns form data as object
 *
 * @returns {object} form data
 */
const getFormData = function() {
    return {
        numberOfForms: $numberOfForms.val(),
        name: $name.val(),
        addressLine1: $addressLine1.val(),
        addressLine2: $addressLine2.val(),
        town: $town.val(),
        postcode: $postcode.val().trim(),
        country: $country.val().trim(),
        telephone: $telephone.val(),
        email: $email.val() === '' ? null : $email.val(),
        recaptcha: grecaptcha.getResponse()
    };
};

/**
 * Checks all form field to determine if form is valid
 * Highlights UI if highlightField
 *
 * @returns {boolean} is the form valid
 */
const formIsValid = function(highlightField, errorFields) {

    const $addressLine1 = $('#disclosure-bdo-address-line-1'),
        $town = $('#disclosure-bdo-town'),
        $postcode = $('#disclosure-bdo-postcode'),
        $name = $('#disclosure-bdo-name'),
        $email = $('#disclosure-bdo-email'),
        $telephone = $('#disclosure-bdo-telephone'),
        $recaptcha = $('#recaptcha'),
        $numberOfForms = $('#disclosure-bdo-number-of-forms');
    $formErrors.html('');

    const numberOfForms = commonForms.validateField($numberOfForms, highlightField, commonForms.requiredField, commonForms.numericOnly,
        _.partialRight(commonForms.maxCharacters, $numberOfForms.data('maxlength')));

    commonForms.updateErrors(errorFields, numberOfForms, 'disclosure-bdo-number-of-forms');

    const name = commonForms.validateField($name, highlightField, commonForms.requiredField,
        _.partialRight(commonForms.maxCharacters, $name.data('maxlength')));
    commonForms.updateErrors(errorFields, name, 'disclosure-bdo-name');

    const telephone = commonForms.validateField($telephone, highlightField,
        _.partialRight(commonForms.maxCharacters, $telephone.data('maxlength')));
    commonForms.updateErrors(errorFields, telephone, 'disclosure-bdo-telephone');

    const addressLine1 = commonForms.validateField($addressLine1, highlightField, commonForms.requiredField,
        _.partialRight(commonForms.maxCharacters, $addressLine1.data('maxlength')));
    commonForms.updateErrors(errorFields, addressLine1, 'disclosure-bdo-address-line-1');

    const town = commonForms.validateField($town, highlightField, commonForms.requiredField,
        _.partialRight(commonForms.maxCharacters, $town.data('maxlength')));
    commonForms.updateErrors(errorFields, town, 'disclosure-bdo-town');

    const postcode = commonForms.validateField($postcode, highlightField, commonForms.requiredField);
    commonForms.updateErrors(errorFields, postcode, 'disclosure-bdo-postcode');

    const country = commonForms.validateField($country, highlightField,
        _.partialRight(commonForms.maxCharacters, $country.data('maxlength')));
    commonForms.updateErrors(errorFields, country, 'disclosure-bdo-country');

    const email = commonForms.validateField($email, highlightField, commonForms.requiredField, commonForms.regexMatch,
        _.partialRight(commonForms.maxCharacters, $email.data('maxlength')));
    commonForms.updateErrors(errorFields, email, 'disclosure-bdo-email');

    const recaptcha = commonForms.validateField($recaptcha, highlightField, commonForms.recaptchaCompleted);
    commonForms.updateErrors(errorFields, recaptcha, 'recaptcha');

    return numberOfForms && telephone && addressLine1 && town && postcode && country && name && email && recaptcha; //NOSONAR

};

const disclosureBdoForm = {};

/**
 * Module entry point
 */
disclosureBdoForm.init = function() {
    const $body = $('body');

    $body.on('submit', '#disclosure-bdo-form', function(e) {
        submitted = true;
        e.preventDefault();

        const errorFields = [];
        if (!formIsValid(true, errorFields)) {
            $clientError.removeClass('fully-hidden');
            window.enquiry = '#feedback-box';
            window.scrollTo(0, $('#feedback-box').offset().top - 10);
            commonForms.track({
                'event': 'formSubmitted',
                'formId': 'disclosure-bdo-form',
                'state': 'error',
                'errorFields': errorFields
            });
            return;
        } else {
            isValid = true;
        }

        $clientError.addClass('fully-hidden');

        const postData = getFormData();

        $.ajax({
            type: 'POST',
            url: '/service/form/bdo/',
            contentType: 'application/json',
            data: JSON.stringify(postData),
            done: function() {
                commonForms.track({
                    'event': 'formSubmitted',
                    'formId': 'disclosure-bdo-form',
                    'state': 'success',
                    'outwardCode': commonForms.outwardCode(postData.postcode),
                    'formRequests': [postData.numberOfForms]
                });
                success = true;

                $('.server-success').removeClass('fully-hidden');
                $('.server-error').addClass('fully-hidden');
                $('.main-content').hide();
                window.enquiry = '#feedback-box';
                window.scrollTo(0, $('#feedback-box').offset().top - 10);
            },
            fail: function() {
                commonForms.track({
                    'event': 'formSubmitted',
                    'formId': 'disclosure-bdo-form',
                    'state': 'error'
                });

                $('.server-error').removeClass('fully-hidden');
                $('.server-success').addClass('fully-hidden');
                window.enquiry = '#feedback-box';
                grecaptcha.reset();
                window.scrollTo(0, $('#feedback-box').offset().top - 10);
            }
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

    commonForms.validateField($recaptcha, commonForms.recaptchaCompleted);
    commonForms.showOrHideErrorBox(submitted, $clientError, isValid);
};

disclosureBdoForm.init();

export default disclosureBdoForm;
