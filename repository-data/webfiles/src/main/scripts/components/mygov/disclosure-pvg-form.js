/* global grecaptcha */

'use strict';

import $ from 'jquery';
import _ from '../../vendor/lodash/dist/tinydash.es6';
import commonForms from '../../tools/forms';

let submitted = false,
    success = false,
    isValid = false;
const $applicationToJoinPvgScheme = $('#disclosure-pvg-application-to-join-pvg-scheme'),
    $existingPvgSchemeMember = $('#disclosure-pvg-existing-pvg-scheme-member'),
    $employerRegistrationApplication = $('#disclosure-pvg-employer-registration-application'),
    $addDisclosureCounterSignatories = $('#disclosure-pvg-add-disclosure-counter-signatories'),
    $removeDisclosureCounterSignatories = $('#disclosure-pvg-remove-disclosure-counter-signatories'),
    $responsibleEmployerEnrolmentApplication = $('#disclosure-pvg-responsible-employer-enrolment-application'),
    $subAccountApplication = $('#disclosure-pvg-sub-account-application'),
    $addressLine1 = $('#disclosure-pvg-address-line-1'),
    $addressLine2 = $('#disclosure-pvg-address-line-2'),
    $town = $('#disclosure-pvg-town'),
    $postcode = $('#disclosure-pvg-postcode'),
    $country = $('#disclosure-pvg-country'),
    $telephone = $('#disclosure-pvg-telephone'),
    $email = $('#disclosure-pvg-email'),
    $companyName = $('#disclosure-pvg-company-name'),
    $name = $('#disclosure-pvg-name'),
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
        applicationToJoinPvgScheme: $applicationToJoinPvgScheme.val(),
        existingPvgSchemeMember: $existingPvgSchemeMember.val(),
        employerRegistrationApplication: $employerRegistrationApplication.val(),
        addDisclosureCounterSignatories: $addDisclosureCounterSignatories.val(),
        removeDisclosureCounterSignatories: $removeDisclosureCounterSignatories.val(),
        responsibleEmployerEnrolmentApplication: $responsibleEmployerEnrolmentApplication.val(),
        subAccountApplication: $subAccountApplication.val(),
        name: $name.val(),
        companyName: $companyName.val(),
        addressLine1: $addressLine1.val(),
        addressLine2: $addressLine2.val(),
        town: $town.val(),
        postcode: $postcode.val().trim(),
        country: $country.val().trim(),
        telephone: $telephone.val(),
        email: $email.val(),
        recaptcha: grecaptcha.getResponse()
    };
};

const atLeastOneDocumentProvided = function(highlightField, errorFields) {
    const $applicationToJoinPvgScheme = $('#disclosure-pvg-application-to-join-pvg-scheme'),
        $existingPvgSchemeMember = $('#disclosure-pvg-existing-pvg-scheme-member'),
        $employerRegistrationApplication = $('#disclosure-pvg-employer-registration-application'),
        $addDisclosureCounterSignatories = $('#disclosure-pvg-add-disclosure-counter-signatories'),
        $removeDisclosureCounterSignatories = $('#disclosure-pvg-remove-disclosure-counter-signatories'),
        $responsibleEmployerEnrolmentApplication = $('#disclosure-pvg-responsible-employer-enrolment-application'),
        $subAccountApplication = $('#disclosure-pvg-sub-account-application');
    const documents = commonForms.atLeastOneRequired([$applicationToJoinPvgScheme[0],
        $existingPvgSchemeMember[0], $employerRegistrationApplication[0], $addDisclosureCounterSignatories[0],
        $removeDisclosureCounterSignatories[0],
        $responsibleEmployerEnrolmentApplication[0], $subAccountApplication[0]
    ], '#disclosure-pvg-documents', highlightField);
    commonForms.updateErrors(errorFields, documents, 'disclosure-pvg-documents');
    return documents;
};

const providedDocumentsAreValid = function(highlightField, errorFields) {
    const $applicationToJoinPvgScheme = $('#disclosure-pvg-application-to-join-pvg-scheme'),
        $existingPvgSchemeMember = $('#disclosure-pvg-existing-pvg-scheme-member'),
        $employerRegistrationApplication = $('#disclosure-pvg-employer-registration-application'),
        $addDisclosureCounterSignatories = $('#disclosure-pvg-add-disclosure-counter-signatories'),
        $removeDisclosureCounterSignatories = $('#disclosure-pvg-remove-disclosure-counter-signatories'),
        $responsibleEmployerEnrolmentApplication = $('#disclosure-pvg-responsible-employer-enrolment-application'),
        $subAccountApplication = $('#disclosure-pvg-sub-account-application');
    const applicationToJoinPvgScheme = commonForms.validateField($applicationToJoinPvgScheme[0], highlightField, commonForms.numericOnly,
        _.partialRight(commonForms.maxCharacters, $applicationToJoinPvgScheme.data('maxlength')));
    commonForms.updateErrors(errorFields, applicationToJoinPvgScheme, 'disclosure-pvg-application-to-join-pvg-scheme');

    const existingPvgSchemeMember = commonForms.validateField($existingPvgSchemeMember[0], highlightField, commonForms.numericOnly,
        _.partialRight(commonForms.maxCharacters, $existingPvgSchemeMember.data('maxlength')));
    commonForms.updateErrors(errorFields, existingPvgSchemeMember, 'disclosure-pvg-existing-pvg-scheme-member');

    const employerRegistrationApplication = commonForms.validateField($employerRegistrationApplication[0], highlightField, commonForms.numericOnly,
        _.partialRight(commonForms.maxCharacters, $employerRegistrationApplication.data('maxlength')));
    commonForms.updateErrors(errorFields, employerRegistrationApplication, 'disclosure-pvg-employer-registration-application');

    const addDisclosureCounterSignatories = commonForms.validateField($addDisclosureCounterSignatories[0], highlightField, commonForms.numericOnly,
        _.partialRight(commonForms.maxCharacters, $addDisclosureCounterSignatories.data('maxlength')));
    commonForms.updateErrors(errorFields, addDisclosureCounterSignatories, 'disclosure-pvg-add-disclosure-counter-signatories');

    const removeDisclosureCounterSignatories = commonForms.validateField($removeDisclosureCounterSignatories[0], highlightField, commonForms.numericOnly,
        _.partialRight(commonForms.maxCharacters, $removeDisclosureCounterSignatories.data('maxlength')));
    commonForms.updateErrors(errorFields, removeDisclosureCounterSignatories, 'disclosure-pvg-remove-disclosure-counter-signatories');

    const responsibleEmployerEnrolmentApplication = commonForms.validateField($responsibleEmployerEnrolmentApplication[0], highlightField, commonForms.numericOnly,
        _.partialRight(commonForms.maxCharacters, $responsibleEmployerEnrolmentApplication.data('maxlength')));
    commonForms.updateErrors(errorFields, responsibleEmployerEnrolmentApplication, 'disclosure-pvg-responsible-employer-enrolment-application');

    const subAccountApplication = commonForms.validateField($subAccountApplication[0], highlightField, commonForms.numericOnly,
        _.partialRight(commonForms.maxCharacters, $subAccountApplication.data('maxlength')));
    commonForms.updateErrors(errorFields, subAccountApplication, 'disclosure-pvg-sub-account-application');
    // all fields should be valid
    return applicationToJoinPvgScheme && existingPvgSchemeMember && employerRegistrationApplication && addDisclosureCounterSignatories && removeDisclosureCounterSignatories && responsibleEmployerEnrolmentApplication && subAccountApplication; //NOSONAR
};

/**
 * Checks all form field to determine if form is valid
 * Highlights UI if highlightField
 *
 * @returns {boolean} is the form valid
 */
const formIsValid = function(highlightField, errorFields) {

    const $addressLine1 = $('#disclosure-pvg-address-line-1'),
        $town = $('#disclosure-pvg-town'),
        $postcode = $('#disclosure-pvg-postcode'),
        $name = $('#disclosure-pvg-name'),
        $email = $('#disclosure-pvg-email'),
        $telephone = $('#disclosure-pvg-telephone'),
        $recaptcha = $('#recaptcha');
    $formErrors.html('');

    const documents = atLeastOneDocumentProvided(highlightField, errorFields);

    const documentsValid = providedDocumentsAreValid(highlightField, errorFields);

    const name = commonForms.validateField($name[0], highlightField, commonForms.requiredField,
        _.partialRight(commonForms.maxCharacters, $name.data('maxlength')));
    commonForms.updateErrors(errorFields, name, 'disclosure-pvg-name');

    const email = commonForms.validateField($email[0], highlightField, commonForms.requiredField, commonForms.regexMatch,
        _.partialRight(commonForms.maxCharacters, $email.data('maxlength')));
    commonForms.updateErrors(errorFields, email, 'disclosure-pvg-email');

    const telephone = commonForms.validateField($telephone[0], highlightField,
        _.partialRight(commonForms.maxCharacters, $telephone.data('maxlength')));
    commonForms.updateErrors(errorFields, telephone, 'disclosure-pvg-telephone');

    const addressLine1 = commonForms.validateField($addressLine1[0], highlightField, commonForms.requiredField,
        _.partialRight(commonForms.maxCharacters, $addressLine1.data('maxlength')));
    commonForms.updateErrors(errorFields, addressLine1, 'disclosure-pvg-address-line-1');

    const town = commonForms.validateField($town[0], highlightField, commonForms.requiredField,
        _.partialRight(commonForms.maxCharacters, $town.data('maxlength')));
    commonForms.updateErrors(errorFields, town, 'disclosure-pvg-town');

    const postcode = commonForms.validateField($postcode[0], highlightField, commonForms.requiredField);
    commonForms.updateErrors(errorFields, postcode, 'disclosure-pvg-postcode');

    const country = commonForms.validateField($country[0], highlightField,
        _.partialRight(commonForms.maxCharacters, $country.data('maxlength')));
    commonForms.updateErrors(errorFields, country, 'disclosure-pvg-country');

    const recaptcha = commonForms.validateField($recaptcha[0], highlightField, commonForms.recaptchaCompleted);
    commonForms.updateErrors(errorFields, recaptcha, 'recaptcha');

    return telephone && addressLine1 && town && postcode && country && name && email && recaptcha && documents && documentsValid; //NOSONAR

};

const disclosurePvgForm = {};

/**
 * Module entry point
 */
disclosurePvgForm.init = function() {
    const $body = $('body');

    $body.on('submit', '#disclosure-pvg-form', function(e) {
        submitted = true;
        e.preventDefault();

        const errorFields = [];
        if (!formIsValid(true, errorFields)) {
            $clientError.removeClass('fully-hidden');
            window.enquiry = '#feedback-box';
            window.scrollTo(0, $('#feedback-box').offset().top - 10);
            commonForms.track({
                'event': 'formSubmitted',
                'formId': 'disclosure-pvg-form',
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
            url: '/service/form/pvg/',
            contentType: 'application/json',
            data: JSON.stringify(postData),
            done: function() {
                commonForms.track({
                    'event': 'formSubmitted',
                    'formId': 'disclosure-pvg-form',
                    'state': 'success',
                    'outwardCode': commonForms.outwardCode(postData.postcode),
                    'formRequests': [postData.applicationToJoinPvgScheme,
                        postData.existingPvgSchemeMember,
                        postData.employerRegistrationApplication,
                        postData.addDisclosureCounterSignatories,
                        postData.removeDisclosureCounterSignatories,
                        postData.responsibleEmployerEnrolmentApplication,
                        postData.subAccountApplication
                    ]
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
                    'formId': 'disclosure-pvg-form',
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

disclosurePvgForm.init();

export default disclosurePvgForm;
