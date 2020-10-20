// COMMON HOUSING

'use strict';

import commonForms from './forms';
import $ from 'jquery';
import { Accordion } from '../../../../node_modules/@scottish-government/pattern-library/src/all';

const commonHousing = {
    camelify: function (string) {
        return string.replace(/(?:^\w|[A-Z]|\b\w)/g, function (letter, index) {
            return index === 0 ? letter.toLowerCase() : letter.toUpperCase();
        }).replace(/\s+|[-]/g, '');
    },

    summaryAccordion: function (scope) {
        const accordionElements = [].slice.call(scope.querySelectorAll('[data-module="ds-accordion"]'));
        accordionElements.forEach(accordionElement => new Accordion(accordionElement).init());
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

    validateSummary: function () {
        // do validations

        const allValidationFields = document.querySelectorAll('[data-validation]:not(.no-validate)');
        const fieldsToValidate = [].slice.call(allValidationFields).filter(field => {
            if (field.closest('.ds_reveal-content')) {
                return [].slice.call(field.closest('.ds_reveal-content').parentNode.children).filter(item => item.nodeName === 'INPUT' && item.checked).length;
            } else {
                return true;
            }
        });

        fieldsToValidate.forEach(field => {
            // perform validation
            const validations = field.getAttribute('data-validation').split(' ');
            const validationChecks = [];
            for (let i = 0, il = validations.length; i < il; i++) {
                if (commonForms[validations[i]]) {
                    validationChecks.push(commonForms[validations[i]]);
                }
            }

            commonForms.validateInput($(field), validationChecks);
        });
        const invalidFields = $('[aria-invalid="true"]:not(.no-validate)');

        const stopProgressToDownload = function(){
            $('.client-error h2').focus();
            return false;
        };

        if (invalidFields.length > 0) {
            $('.client-error').removeClass('fully-hidden').attr('aria-hidden', 'false');
            $('.client-error h2').focus();
            $('#button-next').on('click', stopProgressToDownload);

            // Copy error message(s) into the corresponding table row
            const createErrorItems = function () {
                const errorItem = document.createElement('li');

                $(this).find('.form--required').remove();

                errorItem.innerText = $.trim($(this).text());
                return errorItem;
            };

            for (let i = 0; i < invalidFields.length; i++) {
                const tableRow = $('[data-summary-field*="' + invalidFields[i].id + '"');
                const errorList = $('.' + invalidFields[i].id + '-errors li').clone();
                const errorListItems = errorList.map(createErrorItems);

                let groupErrorList;

                if (tableRow.find('.summary-errors').length > 0) {
                    groupErrorList = tableRow.find('.summary-errors');
                } else {
                    groupErrorList = $('<ul class="summary-errors"></ul>');
                    tableRow.find('td:first').append(groupErrorList);
                    tableRow.addClass('ds_input--error');
                }

                $(groupErrorList).append(errorListItems);
            }
        } else {
            $('#button-next').off('click', stopProgressToDownload);
        }
    },

    // Address manual fill section show/hide
    setManualLinkSections: function () {
        $('body').on('click', '.js-address-manual-link', function (event) {
            event.preventDefault();

            const manualAddress = $(this).closest('.js-postcode-lookup').find('.address-manual');
            const buildingOrStreet = $(this).parent().parent().find('.town, .input-wrapper');
            const postcodeDropdown = $(this).parent().parent().find('.postcode-results select');

            manualAddress.toggleClass('fully-hidden');

            // set which fields should not be validated on summary page
            if (manualAddress.hasClass('fully-hidden')) {
                buildingOrStreet.addClass('no-validate');
                postcodeDropdown.removeClass('no-validate');
            } else {
                buildingOrStreet.removeClass('no-validate');
                postcodeDropdown.addClass('no-validate');
            }

            // remove validation errors from lookup when opening manual entry
            const nearestLookup = $(this).parent().parent().find('.postcode-search');
            nearestLookup.removeClass('ds_input--error');
            nearestLookup.siblings('.current-errors').html('');
        });
    },

    setupRepeatingSections: function (sections, form) {

        function getSection(name, sections) {
            for (let j = 0, jl = sections.length; j < jl; j++) {
                if (sections[j].name === name) {
                    return sections[j];
                }
            }
        }

        const appendRepeatingSection = function (sectionNumber, sectionId, section) {
            const sectionTemplate = section.template;

            const templateData = {
                index: sectionNumber,
                guarantor: section.guarantor,
                agent: sectionNumber === 3,
                injectedContent: section.injectedContent,
                addressrequired: section.addressrequired || false,
                requiredName: section.requiredName || false,
                slug: section.slug,
                stepTitle: section.stepTitle
            };

            if (section.suppressedFields) {
                section.suppressedFields.forEach(field => templateData['hide_' + field] = true);
            }

            const sectionHtml = sectionTemplate.render(templateData);

            $(section.container).append(sectionHtml);

            // update formObject
            let sectionName = commonHousing.camelify(section.name);
            if (form.settings.formObject[sectionName]) {
                form.settings.formObject[sectionName][sectionId] = {};
            }

            // bind section DOM to formObject
            const fieldMappings = section.fieldMappings(sectionNumber);

            for (const key in fieldMappings) {
                if (!fieldMappings.hasOwnProperty(key)) { continue; }

                form.mapField(key, fieldMappings[key]);
            }

            // add section to navigation
            const sectionTitle = section.group || section.name;

            const newStep = {
                section: sectionTitle,
                slug: sectionId,
                title: section.stepTitle
            };

            // initialise any postcode lookups in the section
            // now with annoying setInterval
            if (section.initPostcodeLookups) {
                let interval;
                interval = window.setInterval(function () {
                    if ($(section.container).is(':visible')) {
                        section.initPostcodeLookups(sectionNumber);
                        window.clearInterval(interval);
                    }
                }, 100);
            }

            form.addStep(sectionTitle, newStep);
        };

        /*
            *  Event listener to be attached to a button for adding an additional section.
            *  @param {object} sections
            *  @param {object} form
            */
        const addSectionListener = function(section) {
            const newSectionNumber = $('[data-group="' + section.name + '"]').length + 1;
            const newSectionId = section.slug + '-' + newSectionNumber;

            appendRepeatingSection(newSectionNumber, newSectionId, section);

            // redirect to new page
            window.location.href = '#!/' + section.name + '/' + newSectionId + '/';
        };

        const removeRepeatingSection = function (sectionNumber, section) {

            // remove section from DOM
            $('section[data-step="' + section.slug + '-' + sectionNumber + '"]').remove();

            // remove section from formObject
            const sectionId = section.slug + '-' + sectionNumber;
            delete form.settings.formObject[commonHousing.camelify(section.name)][sectionId];

            // remove step from navigation
            form.removeStep(section.slug + '-' + sectionNumber);

            // remove any validation messages
            $('#feedback-box .form-errors').html('');
            $('#feedback-box .client-error').addClass('fully-hidden');
        };

        /*
            *  Event listener to be attached to a button for removing an additional section.
            *  @param {object} section
            */
        const removeSectionListener = function(section) {
            const currentId = $('[data-group="' + section.name + '"]').filter(':not(.fully-hidden)').attr('id');
            const currentNumber = currentId.split('-').pop();
            const firstSectionId = $(section.container).find('section')[0].id;
            let redirectSection;

            // decide which section to redirect to on remove:
            // if in first section go to next, if not go to previous
            if (currentId === firstSectionId){
                const nextSectionId = $('#' + currentId).next('section')[0].id;
                redirectSection = section.slug + '-' + nextSectionId.split('-').pop();
            } else {
                const prevSectionId = $('#' + currentId).prev('section')[0].id;
                redirectSection = section.slug + '-' + prevSectionId.split('-').pop();
            }

            // add success notification
            let inputName = $(this).parent().find(section.nameInput).val();
            inputName = inputName ? ' (' + inputName + ')' : '';

            $('#' + redirectSection).prepend(
                '<div class="ds_inset-text  js-note-success"><div class="ds_inset-text__text">'
                    + section.stepTitle + inputName + ' removed successfully</div></div>');

            // remove success notification on click
            $('.js-note-success').on('click', function(){
                $(this).hide('slow', function(){ $(this).remove(); });
            });

            // redirect to previous or next page
            window.location.href = '#!/' + section.name + '/' + redirectSection + '/';

            removeRepeatingSection(currentNumber, section);

            setTimeout(function(){
                // add one time event listener to remove success message on navigating away
                $('#page-nav').one('change', function(){
                    $('.js-note-success').remove();
                });
            }, 1000);
        };

        /*
            *  Event listener to dynamically update a span of text with user input from an input field.
            */
        const dynamicTitleListener = function(el) {
            const inputValue = el.val();
            const section = el.closest('section')[0];

            $(section).find('.js-dynamic-title').text(inputValue);
        };

        /*
            *  Event listener to rename a step in the sub navigation on exiting a title field
            *  @param {jQ object} el
            *  @param {string} defaultTitle - default to be used if the field is empty
            */
        const renameStepListener = function(el, defaultTitle) {
            const inputValue = el.val() || defaultTitle;
            const currentStepSlug = form.getCurrentStep().slug;

            form.renameStep(currentStepSlug, inputValue);
        };

        // when a new page loads, decide if an 'add ...' button should be displayed.
        $('#page-nav').on('change', function(){
            const currentStep = form.currentStep;
            const totalInSection = $('#' + currentStep.section + ' section').length;
            const removeButton = null;

            // if more than one section, add 'remove' button to first one, if not hide it
            if (totalInSection > 1 && removeButton){
                $(removeButton).removeClass('fully-hidden');
            } else {
                $(removeButton).addClass('fully-hidden');
            }
        });

        $('#page-nav').on('click', '.js-add-repeating-section', function (event) {
            const section = getSection(event.target.getAttribute('data-section'), sections);
            addSectionListener(section);
        });

        $('body').on('click', '.js-remove-repeating-section', function (event) {
            const section = getSection($(event.target).closest('section').attr('data-group'), sections);
            removeSectionListener(section);
        });

        $('body').on('keyup', '.js-dynamic-title-input', function () {
            dynamicTitleListener($(this));
        });

        $('body').on('blur', '.js-dynamic-title-input', function (event) {
            const section = getSection($(event.target).closest('section').attr('data-group'), sections);
            renameStepListener($(this), section.stepTitle);
        });

        for (let i = 0, il = sections.length; i < il; i++) {
            const section = sections[i];

            // create the first section page
            appendRepeatingSection(1, section.slug + '-' + '1', section);
        }
    }
};

export default commonHousing;
