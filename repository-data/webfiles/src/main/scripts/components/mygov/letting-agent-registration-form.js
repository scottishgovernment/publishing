/* global grecaptcha */

'use strict';

import $ from 'jquery';
import _ from '../../vendor/lodash/dist/tinydash.es6';
import commonForms from '../../tools/forms';

let submitted = false,
    success = false,
    isValid = false;
const $name = $('#letting-agent-registration-name'),
    $companyName = $('#letting-agent-registration-company-name'),
    $addressLine1 = $('#letting-agent-registration-address-line-1'),
    $addressLine2 = $('#letting-agent-registration-address-line-2'),
    $town = $('#letting-agent-registration-town'),
    $postcode = $('#letting-agent-registration-postcode'),
    $country = $('#letting-agent-registration-country'),
    $email = $('#letting-agent-registration-email'),
    $consent = $('#letting-agent-registration-consent'),
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
        name: $name.val(),
        companyName: $companyName.val(),
        addressLine1: $addressLine1.val(),
        addressLine2: $addressLine2.val(),
        town: $town.val(),
        postcode: $postcode.val().trim(),
        country: $country.val().trim(),
        email: $email.val(),
        recaptcha: grecaptcha.getResponse(),
        confirmation: $consent.prop('checked')
    };
};

/**
 * Checks all form field to determine if form is valid
 * Highlights UI if highlightField
 *
 * @returns {boolean} is the form valid
 */
const formIsValid = function(highlightField, errorFields) {

    const $name = $('#letting-agent-registration-name'),
        $companyName = $('#letting-agent-registration-company-name'),
        $email = $('#letting-agent-registration-email'),
        $consent = $('#letting-agent-registration-consent'),
        $recaptcha = $('#recaptcha');
    $formErrors.html('');

    const name = commonForms.validateField($name[0], highlightField, commonForms.requiredField,
        _.partialRight(commonForms.maxCharacters, $name.data('maxlength')));
    commonForms.updateErrors(errorFields, name, 'letting-agent-registration-name');

    const companyName = commonForms.validateField($companyName[0], highlightField,
        commonForms.requiredField, _.partialRight(commonForms.maxCharacters,
        $companyName.data('maxlength')));
    commonForms.updateErrors(errorFields, companyName,
        'letting-agent-registration-company-name');

    const email = commonForms.validateField($email[0], highlightField,
        commonForms.requiredField, commonForms.regexMatch,
        _.partialRight(commonForms.maxCharacters, $email.data('maxlength')));
    commonForms.updateErrors(errorFields, email, 'letting-agent-registration-email');

    const consent = commonForms.validateField($consent[0], highlightField,
        commonForms.requiredCheckbox);

    const recaptcha = commonForms.validateField($recaptcha[0], highlightField,
        commonForms.recaptchaCompleted);
    commonForms.updateErrors(errorFields, recaptcha, 'recaptcha');

    return name && companyName && email && consent && recaptcha; //NOSONAR

};

const lettingAgentRegistrationForm = {};

/**
 * Module entry point
 */
lettingAgentRegistrationForm.init = function() {
    const $body = $('body');

    $body.on('submit', '#letting-agent-registration-form', function(e) {
        submitted = true;
        e.preventDefault();

        const errorFields = [];
        if (!formIsValid(true, errorFields)) {
            $clientError.removeClass('fully-hidden');
            window.enquiry = '#feedback-box';
            window.scrollTo(0, $('#feedback-box').offset().top - 10);
            commonForms.track({
                'event': 'formSubmitted',
                'formId': 'letting-agent-registration-form',
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
            url: '/service/form/letting/',
            contentType: 'application/json',
            data: JSON.stringify(postData),
            done: function() {
                commonForms.track({
                    'event': 'formSubmitted',
                    'formId': 'letting-agent-registration-form',
                    'state': 'success',
                    'outwardCode': commonForms.outwardCode(postData.postcode),
                    'formRequests': []
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
                    'formId': 'letting-agent-registration-form',
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

    commonForms.validateField($recaptcha[0], commonForms.recaptchaCompleted);
    commonForms.showOrHideErrorBox(submitted, $clientError[0], isValid);
};

lettingAgentRegistrationForm.init();

export default lettingAgentRegistrationForm;
