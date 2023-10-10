// POSTCODE LOOKUP

/* global require */

'use strict';

import PromiseRequest from '../../../../node_modules/@scottish-government/design-system/src/base/tools/promise-request/promise-request';
import temporaryFocus from '../../../../node_modules/@scottish-government/design-system/src/base/tools/temporary-focus/temporary-focus';

const postcodeResultsTemplate = require('../templates/postcode-results');

class PostcodeLookup {
    constructor(element, options = {}) {
        if (!element) {
            return;
        }

        // this.name is used in other modules
        this.name = 'PostcodeLookup';

        this.element = element;
        this.options = options;

        this.lookupElement = element.querySelector('.ds_address__lookup');
        this.resultsElement = element.querySelector('.ds_address__results');
        this.manualElement = element.querySelector('.ds_address__manual');

        this.resultsSelectElement = this.resultsElement.querySelector('.js-results-select');
        this.resultsInfoText = element.querySelector('.js-postcode-info-text');

        this.postcodeInput = this.lookupElement.querySelector('.js-postcode-input');

        this.buildingInput = this.manualElement.querySelector('.js-manual-building');
        this.streetInput = this.manualElement.querySelector('.js-manual-street');
        this.townInput = this.manualElement.querySelector('.js-manual-town');
        this.regionInput = this.manualElement.querySelector('.js-manual-region');
        this.manualPostcodeInput = this.manualElement.querySelector('.js-manual-postcode');

        this.endpointUrl = '/service/housing/postcode/address-lookup';

        this.PromiseRequest = PromiseRequest;

        // auto-init
        this.init();
    }

    init() {
        this.element.addEventListener('click', event => {
            if (event.target.classList.contains('js-postcode-search')) {
                event.preventDefault();
                this.submitPostcodeSearch();
            }

            if (event.target.classList.contains('js-show-manual-entry')) {
                event.preventDefault();
                this.showSection(this.manualElement);
            }

            if (event.target.classList.contains('js-show-postcode-lookup')) {
                event.preventDefault();
                delete this.selectedAddress;
                this.resultsSelectElement.html = '';
                this.showSection(this.lookupElement);
            }
        });

        this.postcodeInput.addEventListener('keydown', event => {
            if (event.key === 'Enter') {
                event.preventDefault();
                this.submitPostcodeSearch();
            }
        });

        this.resultsSelectElement.addEventListener('change', event => {
            const index = parseInt(event.target.value);

            // display RPZ status
            if (this.options.rpz) {
                this.displayRpzStatus(index, this.resultsObject);
            }

            if (index > -1) {
                this.selectedAddress = this.resultsObject[index];

                // populate address fields
                if (this.selectedAddress.org && this.selectedAddress.building) {
                    this.selectedAddress.building = `${this.selectedAddress.org}, ${this.selectedAddress.building}`;
                } else if (this.selectedAddress.org) {
                    this.selectedAddress.building = this.selectedAddress.org;
                }

                this.buildingInput.value = this.selectedAddress.building;
                this.streetInput.value = this.selectedAddress.street;
                this.townInput.value = this.selectedAddress.town;
                this.manualPostcodeInput.value = this.selectedAddress.postcode;

                // trigger change events on those fields
                const changeEvent = new Event('change');
                this.buildingInput.dispatchEvent(changeEvent);
                this.streetInput.dispatchEvent(changeEvent);
                this.townInput.dispatchEvent(changeEvent);
                this.manualPostcodeInput.dispatchEvent(changeEvent);
            }
        });
    }

    displayRpzStatus(index, results) {
        // if 'The address is not listed' chosen
        if (index < 0) {
            // self.addressDisplay.val('').trigger('change').addClass('fully-hidden');
            this.resultsInfoText.classList.remove('fully-hidden');
            this.resultsInfoText.innerHTML = 'If you can’t find your address, you should check whether it\'s listed' +
                ' with <a href="https://www.royalmail.com/find-a-postcode" target="_blank">' +
                'Royal Mail (opens in a new window)</a>. If your address is listed with' +
                ' Royal Mail, but we still can’t find it, you should send your address to us:' +
                ' <a href="mailto:PostcodeQuery@gov.scot">PostcodeQuery@gov.scot</a>.';
            // self.manualAddress.find('.building, .street, .town, .region').val('');
            return;
        }

        const uprn = results[index].uprn;
        const dateString = new Date().toJSON().slice(0, 10);

        this.PromiseRequest(`/service/housing/rpz?uprn=${uprn}&date=${dateString}`)
            .then(data => JSON.parse(data.responseText))
            .then(data => {
                if (data.inRentPressureZone) {
                    this.resultsInfoText.classList.remove('fully-hidden');
                    this.resultsInfoText.innerHTML = 'This address is in a Rent Pressure Zone.';
                } else {
                    if (this.options.displayNotRPZ) {
                        this.resultsInfoText.classList.remove('fully-hidden');
                        this.resultsInfoText.innerHTML = '<strong>This address is not in a Rent Pressure Zone.</strong>' +
                            '<br>This means there\'s no limit on how much rent can be increased at this property.';
                    }
                }

                if (typeof this.options.rpzComplete === 'function') {
                    this.options.rpzComplete(data);
                }
            })
            .catch(error => {
                this.resultsInfoText.classList.remove('fully-hidden');
                this.resultsInfoText.innerHTML = 'Sorry, we were unable to check if this address is in a Rent Pressure Zone.';
            });
    }

    fetchPostcodeResults(postcode) {
        return this.PromiseRequest(`${this.endpointUrl}?postcode=${postcode}`)
            .then(data => JSON.parse(data.responseText))
            .catch(result => {
                this.removeLookupError();
                this.showLookupError(
                    'Unable to fetch results',
                    'Sorry, we can not fetch results for this postcode. Please try again later.'
                );
            });
    }

    formatPostcode(postcode) {
        return postcode.toUpperCase();
    }

    getAddressAsObject() {
        return {
            building: this.selectedAddress.building,
            street: this.selectedAddress.street,
            town: this.selectedAddress.town,
            region: this.selectedAddress.region,
            postcode: this.selectedAddress.postcode
        };
    }

    getAddressAsString() {
        return Object.values(this.getAddressAsObject()).filter(value => value && value.trim() != '').join('\n');
    }

    removeLookupError() {
        let titleElement = this.lookupElement.querySelector('.js-error-title');
        let messageElement = this.lookupElement.querySelector('.js-error-message');

        const questionElement = this.lookupElement.querySelector('.ds_question');
        const inputElement = this.lookupElement.querySelector('.js-postcode-input');

        if (titleElement) {
            titleElement.parentNode.removeChild(titleElement);
        }

        if (messageElement) {
            messageElement.parentNode.removeChild(messageElement);
        }

        questionElement.classList.remove('ds_question--error');
        inputElement.classList.remove('ds_input--error');
    }

    removeErrors() {
        const errorQuestions = [].slice.call(this.element.querySelectorAll('.ds_question--error'));

        errorQuestions.forEach(question => {
            const errorInputs = [].slice.call(question.querySelectorAll('.ds_input--error'));
            errorInputs.forEach(input => {
                input.classList.remove('ds_input--error');
                input.removeAttribute('aria-invalid');
            });

            question.classList.remove('ds_question--error');
            const messages = [].slice.call(question.querySelectorAll('.ds_question__error-message, .js-error-message'));
            messages.forEach(message => {
                message.parentNode.removeChild(message);
            });
        });
    }

    showResults(results, postcode) {
        const scottishResults = results.filter(result => result.country === 'Scotland');

        // cases to cover:
        if (results.length < 1) {
            // 1. no results
            this.removeLookupError();
            this.showLookupError(
                'No results found for this postcode.',
                'If you can\'t find your address, you should check whether it\'s listed' +
                ' with <a href="https://www.royalmail.com/find-a-postcode" target="_blank">' +
                'Royal Mail (opens in a new window)</a>. If your address is listed with' +
                ' Royal Mail, but we still can\'t find it, you should send your address to us:' +
                ' <a href="mailto:PostcodeQuery@gov.scot">PostcodeQuery@gov.scot</a>.'
            );
        } else if (this.options.rpz && scottishResults.length < 1) {
            // 2. no scottish results
            this.removeLookupError();
            this.showLookupError(
                'No Scottish results found for this postcode.',
                this.options.notScottishMessage || 'The postcode you\'ve entered ' +
                'is not a Scottish postcode. Rent Pressure Zones only apply in Scotland.'
            );
        } else {
            // 3. has results
            const options = postcodeResultsTemplate.render({ results: results });
            this.resultsSelectElement.closest('.ds_question').classList.remove('fully-hidden');
            this.resultsSelectElement.innerHTML = options;

            this.resultsElement.querySelector('.js-postcode-value').innerText = this.formatPostcode(postcode);

            this.showSection(this.resultsElement);
        }
    }

    showSection(targetSection) {
        this.removeErrors();

        [this.lookupElement, this.resultsElement, this.manualElement].forEach(thisSection => {
            if (thisSection === targetSection) {
                thisSection.classList.remove('fully-hidden');
            } else {
                thisSection.classList.add('fully-hidden');
            }

            temporaryFocus(targetSection);
        });
    }

    showLookupError(title, message) {
        const questionElement = this.lookupElement.querySelector('.ds_question');
        const inputElement = this.lookupElement.querySelector('.js-postcode-input');

        let titleElement = this.lookupElement.querySelector('.js-error-title');
        let messageElement = this.lookupElement.querySelector('.js-error-message');

        titleElement = document.createElement('p');
        titleElement.classList.add('ds_question__error-message');
        titleElement.classList.add('js-error-title');
        inputElement.insertAdjacentElement('beforebegin', titleElement);

        messageElement = document.createElement('p');
        messageElement.classList.add('ds_hint-text');
        messageElement.classList.add('js-error-message');
        titleElement.insertAdjacentElement('afterend', messageElement);

        titleElement.innerHTML = title;
        messageElement.innerHTML = message;

        questionElement.classList.add('ds_question--error');
        inputElement.classList.add('ds_input--error');
    }

    submitPostcodeSearch() {

        const postcodeValue = this.postcodeInput.value;

        if (!postcodeValue) {
            this.postcodeInput.focus();
            return;
        }

        if (this.validatePostcode(postcodeValue)) {
            this.fetchPostcodeResults(postcodeValue)
                .then(data => {
                    this.postcodeInput.value = this.formatPostcode(postcodeValue);
                    if (data && data.results) {
                        this.resultsObject = data.results;
                        this.showResults(data.results, postcodeValue);
                    }
                });
        } else {
            this.removeErrors();

            this.postcodeInput.classList.add('ds_input--error');
            this.postcodeInput.setAttribute('aria-invalid', true);
            this.postcodeInput.closest('.ds_question').classList.add('ds_question--error');

            // insert message
            const messageElement = document.createElement('p');
            messageElement.innerText = "Enter a valid postcode, for example 'EH6 6QQ'";
            messageElement.classList.add('ds_question__error-message');
            this.postcodeInput.insertAdjacentElement('beforebegin', messageElement);
        }
    }

    validatePostcode(postcode) {
        let trimmedValue = postcode;

        let postcodeRegExp = new RegExp('^[A-Z]{1,2}[0-9R][0-9A-Z]?[0-9][ABD-HJLNP-UW-Z]{2}$');
        let postcodeValue = trimmedValue.toUpperCase().replace(/\s+/g, '');

        return trimmedValue === '' || postcodeValue.match(postcodeRegExp) !== null;
    }
}

export default PostcodeLookup;
