// OVERSEAS TRAVEL DECLARATION FORM

/* global require grecaptcha expireRecaptcha */

'use strict';

const formObject = {
    name: null,
    dob: null,
    nationality: null,
    address: {
        building: null,
        street: null,
        town: null,
        region: null,
        postcode: null
    },
    passportNumber: null,
    destinationCountry: null,
    reason: null,
    otherReason: null,
    signature: null,
    signedDate: null
};

const formMapping = {
    'name': '#name',
    'dob': '#dob',
    'nationality': '#nationality',
    'address.building': '#home-address-building',
    'address.street': '#home-address-street',
    'address.town': '#home-address-town',
    'address.region': '#home-address-region',
    'address.postcode': '#home-postcode',
    'passportNumber': '#passportNumber',
    'destinationCountry': '#destinationCountry',
    'reason': '[name="travel-reason"]',
    'otherReason': '#otherReason',
    'signature': '#signature',
    'signedDate': '#signedDate'
};

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
        slug: 'personal',
        title: 'Personal details',
        pages: [
            {
                slug: 'personal',
                title: 'Personal details'
            }
        ]
    },
    {
        group: {
            slug: 'part-0',
            title: 'Part zero'
        },
        slug: 'travel',
        title: 'Travel details',
        pages: [
            {
                slug: 'travel',
                title: 'Travel details'
            }
        ]
    },
    {
        group: {
            slug: 'part-0',
            title: 'Part zero'
        },
        slug: 'declaration',
        title: 'Declaration',
        pages: [
            {
                slug: 'declaration',
                title: 'Declaration'
            }
        ]
    },
    {
        group: {
            slug: 'part-0',
            title: 'Part zero'
        },
        slug: 'download',
        title: 'Download',
        pages: [
            {
                slug: 'download',
                title: 'Download',
                hideSubsectionNav: true,
                noFormBox: true
            }
        ]
    }
];

import $ from 'jquery';
import feedback from '../../components/feedback';
import MultiPageForm from '../../components/multi-page-form';
import PostcodeLookup from '../../components/postcode-lookup';
import commonForms from '../../tools/forms';
import commonHousing from '../../tools/housing';
import DSDatePicker from '../../../../../node_modules/@scottish-government/pattern-library/src/components/date-picker/date-picker';
import bloomreachWebfile from '../../tools/bloomreach-webfile';

const formTemplate = require('../../templates/mygov/overseas-travel-declaration-form');
const housingFormPageNavTemplate = require('../../templates/housing-form-pagenav');
const sectionNavTemplate = require('../../templates/visited-only-section-nav');
const subNavTemplate = require('../../templates/visited-only-subsection-nav');

$('form').each(function() {
    this.reset();
});

const overseasTravelForm = {
    form: new MultiPageForm({
        formSections: formSections,
        formMapping: formMapping,
        formObject: formObject,
        formEvents: {
        },
        modifiers: [{
            pattern: new RegExp(/\.postcode$/),
            transformFunction: function () {
                return arguments[0].toUpperCase();
            }
        }],
        sectionTemplate: sectionNavTemplate,
        subsectionTemplate: subNavTemplate,
        pageNavFunction: function () {return commonForms.pageNavFunction('overview', overseasTravelForm.form.currentStep);},
        pageNavTemplate: housingFormPageNavTemplate
    }),

    init: function () {
        // append form template
        const formTemplateContainer = document.querySelector('#form-container');
        if (!formTemplateContainer) {
            return false;
        }
        const overviewContent = formTemplateContainer.innerHTML;
        this.recaptchaSitekey = document.getElementById('recaptchaSitekey').value;
        this.recaptchaEnabled = document.getElementById('recaptchaEnabled').value === 'true';
        formTemplateContainer.innerHTML = formTemplate.render({
            webfilesPath: bloomreachWebfile(),
            assetsPath: document.getElementById('site-root-path').value.replace('mygov', '') + 'assets',
            recaptchaEnabled: this.recaptchaEnabled,
            recaptchaSitekey: this.recaptchaSitekey
        });
        formTemplateContainer.querySelector('#overview').innerHTML = formTemplateContainer.querySelector('#overview').innerHTML + overviewContent;

        commonForms.appendCaptchaScript();

        feedback.init();
        overseasTravelForm.form.validateStep = overseasTravelForm.validateStep;
        overseasTravelForm.form.init();
        commonHousing.setManualLinkSections();
        this.setupValidations();
        this.setupPostcodeLookups();
        this.setupDatePickers();
        if (this.recaptchaEnabled) {
            commonForms.setupRecaptcha();
        }
    },

    setupDatePickers: function () {
        const dobPicker = new DSDatePicker(document.getElementById('dob-picker'), {maxDate: new Date()});
        const signedDatePicker = new DSDatePicker(document.getElementById('signedDate-picker'), {maxDate: new Date()});

        dobPicker.init();
        signedDatePicker.init();

        // prefill sifgnedDate with today's date
        signedDatePicker.inputElement.value = commonForms.dateToString(new Date());
        $(signedDatePicker.inputElement).trigger('change');
    },

    setupPostcodeLookups: function () {
        new PostcodeLookup({ rpz: false, lookupId: 'home-postcode-lookup', readOnly: false }); // NOSONAR
    },

    setupValidations: function () {
        // On click of error playback link, scroll to above field so label is visible
        $('body').on('click', '.client-error a', function (event) {
            const targetElement = document.querySelector(this.hash);
            const question = targetElement.closest('.ds_question');

            const elementToScrollTo = question || targetElement;
            if (elementToScrollTo) {
                elementToScrollTo.scrollIntoView();
                event.preventDefault();
            }
        });
    },

    validateStep: function () {
        return commonHousing.validateStep(overseasTravelForm.form.currentStep);
    },

    prepareFormDataForPost: function (formData) {
        // there are some things we need to address before we post the data
        const formDataForPost = JSON.parse(JSON.stringify(formData));

        // 1. remove unneeded fields
        if (formDataForPost.reason !== 'OTHER') {
            formDataForPost.otherReason = '';
        }

        delete formDataForPost.reason_text;

        // 2. Format dates to YYYY-MM-DD
        const dateFields = ['dob', 'signedDate'];

        for (let j = 0; j < dateFields.length; j++) {
            const field = dateFields[j];
            const value = formData[field];
            if (value === null || value.split(' ').join('') === ''){
                continue;
            }

            const dateParts = value.split('/');
            formDataForPost[field] = `${dateParts[2]}-${dateParts[1]}-${dateParts[0]}`;
        }

        return formDataForPost;
    }
};

$('.multi-page-form').on('click', '.js-download-file', function (event) {
    event.preventDefault();

    const documentDownloadForm = $('#travel-declaration-download');
    documentDownloadForm.find('input[name="type"]').val($(this).closest('.js-download-container').attr('data-documenttype'));

    // make a copy of the form data to manipulate before posting
    const formData = JSON.parse(JSON.stringify(overseasTravelForm.form.settings.formObject));
    const data = overseasTravelForm.prepareFormDataForPost(formData);
    if (this.recaptchaEnabled) {
        data.recaptcha = grecaptcha.getResponse();
    }

    // analytics tracking
    const downloadType = $(this).find('input[name=type]').val();

    commonForms.track({
        'event': 'formSubmitted',
        'formId': 'travel-declaration-download',
        'downloadType': downloadType
    });

    // Set hidden data field to have value of JSON data
    documentDownloadForm.find('input[name="data"]').val(encodeURIComponent(JSON.stringify(data)));
    documentDownloadForm.trigger('submit');
    if (this.recaptchaEnabled) {
        expireRecaptcha();
    }
});

window.format = overseasTravelForm;
window.format.init();

export default overseasTravelForm;
