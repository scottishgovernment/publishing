/* global grecaptcha */

'use strict';

import commonForms from '../../tools/forms';
import _ from '../../vendor/lodash/dist/tinydash.es6';
import $ from 'jquery';

let submitted = false,
    success = false,
    isValid = false;

const $companyName = $('#brexit-company-name'),
    $name = $('#brexit-name'),
    $email = $('#brexit-email'),
    $telephone = $('#brexit-telephone'),
    $sector = $('#brexit-sector'),
    $location = $('#brexit-location'),
    $enquiry = $('#brexit-enquiry'),
    $consent = $('#brexit-consent'),
    $recaptcha = $('#recaptcha'),
    $clientError = $('.client-error'),
    $formErrors = $('.form-errors');

/**
 * Returns form data as object
 *
 * @returns {object} form data
 */
const getFormData = function() {
    return  {
        companyName: $companyName.val(),
        name: $name.val(),
        email: $email.val(),
        telephone: $telephone.val(),
        location: $location.val(),
        sector: $sector.val() || '',
        enquiry: $enquiry.val() || '',
        confirmation: $consent.prop('checked'),

        recaptcha: grecaptcha.getResponse()
    };
};

/**
 * Checks all form field to determine if form is valid
 * Highlights UI if highlightField
 *
 * @returns {boolean} is the form valid
 */
const formIsValid = function (highlightField) {

    $formErrors.html('');
    const companyName = commonForms.validateField($companyName, highlightField, commonForms.requiredField,
        _.partialRight(commonForms.maxCharacters, $companyName.data('maxlength')));
    const name = commonForms.validateField($name, highlightField, commonForms.requiredField,
        _.partialRight(commonForms.maxCharacters, $name.data('maxlength')));
    const email = commonForms.validateField($email, highlightField, commonForms.requiredField, commonForms.regexMatch,
        _.partialRight(commonForms.maxCharacters,  $email.data('maxlength')));
    const telephone = commonForms.validateField($telephone, highlightField,
        _.partialRight(commonForms.maxCharacters,  $telephone.data('maxlength')));
    const location = commonForms.validateField($location, highlightField, commonForms.requiredField);
    const sector = commonForms.validateField($sector, highlightField, commonForms.requiredField);
    const enquiry = commonForms.validateField($enquiry, highlightField, commonForms.requiredField,
        _.partialRight(commonForms.maxCharacters,  $enquiry.data('maxlength')));
    const recaptcha = commonForms.validateField($recaptcha, highlightField, commonForms.recaptchaCompleted);

    const consent = commonForms.validateField($consent, highlightField, commonForms.requiredCheckbox);

    return consent && telephone && companyName && name && enquiry && sector && name && location && email && recaptcha; //NOSONAR
};

const brexitForm = {};

/**
 * Module entry point
 */
brexitForm.init = function () {
    const $body = $('body');

    $body.on('input blur keyup', '#brexit-company-name', function() {
        commonForms.validateField($(this), true, commonForms.requiredField, _.partialRight(commonForms.maxCharacters, $(this).data('maxlength')));
        commonForms.showOrHideErrorBox(submitted, $clientError, isValid);
    });

    $body.on('input blur keyup', '#brexit-name', function() {
        commonForms.validateField($(this), true, commonForms.requiredField, _.partialRight(commonForms.maxCharacters, $(this).data('maxlength')));
        commonForms.showOrHideErrorBox(submitted, $clientError, isValid);
    });

    $body.on('input blur keyup', '#brexit-email', function() {
        commonForms.validateField($(this), true, commonForms.requiredField, commonForms.regexMatch, _.partialRight(commonForms.maxCharacters, $(this).data('maxlength')));
        commonForms.showOrHideErrorBox(submitted, $clientError, isValid);
    });

    $body.on('input blur keyup', '#brexit-telephone', function() {
        commonForms.validateField($(this), false, commonForms.requiredField, _.partialRight(commonForms.maxCharacters, $(this).data('maxlength')));
        commonForms.showOrHideErrorBox(submitted, $clientError, isValid);
    });

    $body.on('input blur keyup', '#brexit-enquiry', function() {
        commonForms.validateField($(this), true, commonForms.requiredField, _.partialRight(commonForms.maxCharacters, $(this).data('maxlength')));
        commonForms.showOrHideErrorBox(submitted, $clientError, isValid);
    });

    $body.on('submit', '#brexit-form', function(e) {
        submitted = true;
        e.preventDefault();

        if(!formIsValid(true)) {
            $clientError.removeClass('fully-hidden');
            window.enquiry = '#feedback-box';
            window.scrollTo(0, $('#feedback-box').offset().top - 10);
            return;
        } else {
            isValid = true;
        }

        $clientError.addClass('fully-hidden');

        const postData = getFormData();

        window.dataLayer = window.dataLayer || [];
        window.dataLayer.push({
            'event' : 'formSubmitted',
            'formId': 'brexit-form'
        });


        $.ajax({
            type: 'POST',
            url: '/service/form/brexit/',
            contentType:'application/json',
            data: JSON.stringify(postData),
            success: function () {
                success = true;

                $('.server-success').removeClass('fully-hidden');
                $('.server-error').addClass('fully-hidden');
                $('.main-content').hide();
                window.enquiry = '#feedback-box';
                window.scrollTo(0, $('#feedback-box').offset().top - 10);
            },
            error: function ( ) {
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

brexitForm.init();

export default brexitForm;
