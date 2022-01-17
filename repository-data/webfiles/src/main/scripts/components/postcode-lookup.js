// POSTCODE LOOKUP

/* global require */

'use strict';

import $ from 'jquery';
import commonForms from '../tools/forms';

const postcodeResultsTemplate = require('../templates/postcode-results');
const addressTemplate = require('../templates/address');

const PostcodeLookup = function(settings){
    this.settings = settings || {};
    this.init = init;
    this.displayStaticAddress = displayStaticAddress;
    this.displayEditableAddress = displayEditableAddress;
    this.displayRpzStatus = displayRpzStatus;
    this.findAddresses = findAddresses;
    this.clearAddresses = clearAddresses;

    // self-init
    this.init();
};

function init() {
    const self = this;

    if (!this.settings.lookupId) {
        return;
    }

    this.rpzComplete = this.settings.rpzComplete;
    this.settings.lookupId = '#' + this.settings.lookupId;
    this.isRpzLookup = this.settings.rpz;
    this.readOnly = this.settings.readOnly;

    this.addressDisplay = $(self.settings.lookupId + ' .address-display');
    this.postcodeResults = $(self.settings.lookupId + ' .postcode-results');
    this.postcodeSearch = $(self.settings.lookupId + ' .postcode-search');
    this.infoNote = $(self.settings.lookupId + ' .postcode-info-note');
    this.manualAddress = $(this.settings.lookupId + ' .address-manual');


    const select = $(self.settings.lookupId + ' select')[0];

    window.storedAddresses = window.storedAddresses || {};

    const storedAddressForThisPostcodeLookup = window.storedAddresses[self.settings.dataFrom];

    if (self.settings.dataFrom && storedAddressForThisPostcodeLookup) {
        self.postcodeResults.find('select').addClass('no-validate');

        if (storedAddressForThisPostcodeLookup.postcode) {
            self.postcodeSearch.val(storedAddressForThisPostcodeLookup.postcode);
        }

        if (self.readOnly) {
            self.displayStaticAddress(0, [storedAddressForThisPostcodeLookup]);
        } else {
            self.displayEditableAddress(0, [storedAddressForThisPostcodeLookup]);
        }
    }

    // binding events
    // 1. submit search
    function submitSearch() {
        // Remove any previous error messages
        self.infoNote.html('').addClass('fully-hidden');

        const input = $(self.settings.lookupId + ' .postcode-search').val();
        const select = $(self.settings.lookupId + ' select')[0];

        if (!input) {
            $(self.settings.lookupId + ' .postcode-search')[0].focus();
            return;
        }

        self.clearAddresses();
        self.findAddresses(input, select);
    }

    $('body').on('click', self.settings.lookupId + ' .js-find-address-button', function (event) {
        event.preventDefault();
        submitSearch();
    });

    $(self.settings.lookupId + ' .postcode-search').on('keydown', function(event){
        // submit when return key pressed while in search box
        if (event.keyCode === 13){
            event.preventDefault();
            submitSearch();
        }
    });

    // 2. choose and display an address
    $(select).on('change', function(){
        const index = parseInt($(this).val());

        self.infoNote.addClass('fully-hidden');

        if (self.settings.storeResultAs) {
            window.storedAddresses[self.settings.storeResultAs] = self.addressesAtPostcode[index];
        }

        if (self.readOnly) {
            self.displayStaticAddress(index, self.addressesAtPostcode);
        } else {
            self.displayEditableAddress(index, self.addressesAtPostcode);
        }

        if (self.isRpzLookup) {
            self.displayRpzStatus(index, self.addressesAtPostcode);
        }
    });
}

function displayStaticAddress (index, results) {
    if (index > -1) {
        const result = results[index];
        const address = addressTemplate.render(result);

        this.addressDisplay.removeClass('fully-hidden no-validate').val(address).trigger('change');
        this.addressDisplay.height(this.addressDisplay[0].scrollHeight);
    }
}

function displayEditableAddress(index, results) {
    const manualAddressLink = $(this.settings.lookupId + ' .js-address-manual-link');
    const postcode = results[0].postcode;

    if (index > -1) {
        const org = results[index].org;
        let building = results[index].building;
        const street = results[index].street;
        const locality = results[index].locality;
        const region = results[index].region;
        let town = results[index].town;

        if (locality) {
            town = `${locality}, ${town}`;
        }
        if (org && building) {
            building = `${org}, ${building}`;
        } else if (org) {
            building = org;
        }

        this.manualAddress.find('.building').val(building).trigger('change');
        this.manualAddress.find('.street').val(street).trigger('change');
        this.manualAddress.find('.town').val(town).trigger('change');
        this.manualAddress.find('.region').val(region).trigger('change');
        this.manualAddress.find('.postcode').val(postcode).trigger('change');
    } else {
        this.manualAddress.find('.building, .street, .town, .region, .postcode').val('');
    }

    this.manualAddress.removeClass('fully-hidden');
    manualAddressLink.addClass('fully-hidden');
}

function displayRpzStatus (index, results) {
    const self = this;

    // if 'My address is not listed' chosen
    if (index < 0) {
        self.addressDisplay.val('').trigger('change').addClass('fully-hidden');
        self.infoNote.removeClass('fully-hidden').html('If you can’t find your address, you should check whether it\'s listed' +
            ' with <a href="https://www.royalmail.com/find-a-postcode" target="_blank">' +
            'Royal Mail (opens in a new window)</a>. If your address is listed with' +
            ' Royal Mail, but we still can’t find it, you should send your address to us:' +
            ' <a href="mailto:PostcodeQuery@gov.scot">PostcodeQuery@gov.scot</a>.');
        self.manualAddress.find('.building, .street, .town, .region').val('');
        return;
    }

    const uprn = results[index].uprn;
    const dateString = new Date().toJSON().slice(0,10);

    $.ajax(`/service/housing/rpz?uprn=${uprn}&date=${dateString}`)
        .done(function(data){
            if (data.inRentPressureZone) {
                self.postcodeResults.find('select').addClass('js-in-rpz');

                self.infoNote.html(self.settings.infoNoteHtml ? self.settings.infoNoteHtml : 'This address is in a Rent Pressure Zone.');
                self.infoNote.removeClass('fully-hidden');
                if (typeof self.rpzComplete === 'function') {
                    self.rpzComplete(data);
                }
            } else {
                self.postcodeResults.find('select').removeClass('js-in-rpz');

                if(self.settings.displayNotRPZ){
                    self.infoNote.removeClass('fully-hidden').html(
                        '<strong>This address is not in a Rent Pressure Zone.</strong>' +
                        '<br>This means there\'s no limit on how much rent can be increased at this property.');
                }
                if (typeof self.rpzComplete === 'function') {
                    self.rpzComplete(data);
                }
            }
        })
        .fail(function(){
            self.infoNote.removeClass('fully-hidden').html(
                'Sorry, we were unable to check if this address is in a Rent Pressure Zone.');
        });
}

function clearAddresses () {
    // Clear any previously chosen address
    this.addressDisplay.val('').trigger('change').addClass('fully-hidden');
    this.infoNote.addClass('fully-hidden');
    this.postcodeResults.addClass('fully-hidden');

    $(this.settings.lookupId + ' .address-manual div input').val('');
    $($(this.settings.lookupId + ' select')[0]).html('');
}

function findAddresses (input, select) {
    const self = this;

    const field = document.querySelector(`${this.settings.lookupId} .postcode-search`);
    field.classList.remove('no-validate');

    const valid = commonForms.validateInput(field, [commonForms.validPostcode]);
    if (!valid) {
        return;
    }

    commonForms.toggleFormErrors(field, valid, 'invalid-required-postcode', 'Postcode lookup', '');
    commonForms.toggleCurrentErrors(field, valid, 'invalid-required-postcode', 'Please enter a postcode and click "Find address"');

    $.ajax(`/service/housing/postcode/address-lookup?postcode=${input}`)
        .done(function(data){
            // Clear any previously chosen address
            self.clearAddresses();

            if (data.results.length < 1){
                // If no initial results, display message
                self.postcodeResults.addClass('fully-hidden no-validate');
                self.infoNote.removeClass('fully-hidden').html('No results found for this postcode.' +
                '<br>If you can\'t find your address, you should check whether it\'s listed' +
                ' with <a href="https://www.royalmail.com/find-a-postcode" target="_blank">' +
                'Royal Mail (opens in a new window)</a>. If your address is listed with' +
                ' Royal Mail, but we still can\'t find it, you should send your address to us:' +
                ' <a href="mailto:PostcodeQuery@gov.scot">PostcodeQuery@gov.scot</a>.');
                self.manualAddress.find('.building, .street, .town, .region').val('');
                return;
            }

            // if RPZ checker lookup, remove any non-Scottish results
            if (self.isRpzLookup){
                data.results = data.results.filter(function(result){
                    return result.country === 'Scotland';
                });
            }

            if (data.results.length < 1){
                // If no results after non-Scottish filtered out, display message
                const defaultString = 'The postcode you\'ve entered ' +
                'is not a Scottish postcode. Rent Pressure Zones only apply in Scotland.';
                const htmlString = self.settings.notScottishMessage || defaultString;

                self.postcodeResults.addClass('fully-hidden no-validate');
                self.infoNote.removeClass('fully-hidden').html(htmlString);
            } else {
                // If there are results, put them into the dropdown menu
                const options = postcodeResultsTemplate.render(data);
                self.postcodeResults.removeClass('fully-hidden no-validate');
                $(select).html(options);

                self.addressesAtPostcode = data.results;

                field.classList.add('no-validate');
            }

            if (window.DS && window.DS.tracking) {
                window.DS.tracking.init(document.querySelector(self.settings.lookupId));
            }
        })
        .fail(function(response){
            self.clearAddresses();

            const responseJSON = response.responseJSON;

            if (responseJSON && responseJSON.issues.postcode) {
                self.postcodeResults.addClass('fully-hidden no-validate');
                self.addressDisplay.val('').trigger('change').height(0);
            } else {
                self.infoNote.removeClass('fully-hidden').html('Could not fetch address data, please try again');
            }
        });
}

export default PostcodeLookup;
