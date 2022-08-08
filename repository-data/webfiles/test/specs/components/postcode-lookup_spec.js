/* global document, describe, beforeEach, it, expect, spyOn, $ */

'use strict';

jasmine.getFixtures().fixturesPath = 'base/test/fixtures';

import PostcodeLookup from '../../../src/main/scripts/components/postcode-lookup';

const dummyResponse = { responseText: '{"results":[{"uprn":"116075015","building":"CLINTZ COTTAGE HARTSIDE","org":"","street":"","locality":"OXTON","town":"LAUDER","postcode":"TD2 6PU","country":"Scotland"},{"uprn":"116075016","building":"HARTSIDE FARM","org":"","street":"","locality":"OXTON","town":"LAUDER","postcode":"TD2 6PU","country":"Scotland"},{"uprn":"116056970","building":"NETHER HARTSIDE","org":"","street":"","locality":"OXTON","town":"LAUDER","postcode":"TD2 6PU","country":"Scotland"},{"uprn":"116056507","building":"OVER HARTSIDE","org":"","street":"","locality":"OXTON","town":"LAUDER","postcode":"TD2 6PU","country":"Scotland"},{"uprn":"116056521","building":"THE BEECHES HARTSIDE","org":"","street":"","locality":"OXTON","town":"LAUDER","postcode":"TD2 6PU","country":"Scotland"},{"uprn":"116056503","building":"THREEBURNFORD FARM","org":"","street":"","locality":"OXTON","town":"LAUDER","postcode":"TD2 6PU","country":"Scotland"}]}' };
const dummyResponseNoResults = { responseText: '{"results":[]}' };
const dummyResponseNoScottishResults = { responseText: '{"results":[{"uprn":"116075015","building":"CLINTZ COTTAGE HARTSIDE","org":"","street":"","locality":"OXTON","town":"LAUDER","postcode":"TD2 6PU","country":"England"}]}' };

function mockFetchPostcodeResults(response) {
    return function () {
        return Promise.resolve(response);
    };
};

describe("Postcode lookup", function() {

    beforeEach(function() {
        loadFixtures('postcode-lookup.html');
    });

    describe('basic events', () => {
        it('should show manual entry or postcode lookup on click of relevant links', () => {
            const myLookup = new PostcodeLookup(document.getElementById('property-postcode-lookup'));
            const manualLink = document.querySelector('.js-show-manual-entry');
            const lookupLink = document.querySelector('.js-show-postcode-lookup');
            const event = new Event('click', { bubbles: true });

            manualLink.dispatchEvent(event);
            expect(document.querySelector('.ds_address__lookup').classList.contains('fully-hidden')).toBeTrue();
            expect(document.querySelector('.ds_address__manual').classList.contains('fully-hidden')).toBeFalse();

            lookupLink.dispatchEvent(event);
            expect(document.querySelector('.ds_address__lookup').classList.contains('fully-hidden')).toBeFalse();
            expect(document.querySelector('.ds_address__manual').classList.contains('fully-hidden')).toBeTrue();
        });

        it('should submit a postcode search on click of the postcode search button', () => {
            const myLookup = new PostcodeLookup(document.getElementById('property-postcode-lookup'));
            const searchButton = document.querySelector('.js-postcode-search');

            spyOn(myLookup, 'submitPostcodeSearch');

            const event = new Event('click', { bubbles: true });

            searchButton.dispatchEvent(event);

            expect(myLookup.submitPostcodeSearch).toHaveBeenCalled();
        });
    });

    describe('postcode search', () => {
        it('postcode search should fail and focus on the search input when no postcode provided', () => {
            const myLookup = new PostcodeLookup(document.getElementById('property-postcode-lookup'));
            const searchButton = document.querySelector('.js-postcode-search');

            spyOn(myLookup, 'fetchPostcodeResults');

            const event = new Event('click', { bubbles: true });

            searchButton.dispatchEvent(event);

            expect(document.activeElement.id).toEqual('property-postcode-search');
            expect(myLookup.fetchPostcodeResults).not.toHaveBeenCalled();
        });

        it('postcode search should fail and display an error if the postcode is invalid', () => {
            const myLookup = new PostcodeLookup(document.getElementById('property-postcode-lookup'));
            const searchButton = document.querySelector('.js-postcode-search');

            const postcodeInput = document.querySelector('#property-postcode-search');
            const postcodeQuestion = postcodeInput.closest('.ds_question');

            postcodeInput.value = 'bananas';

            spyOn(myLookup, 'fetchPostcodeResults');

            const event = new Event('click', { bubbles: true });

            searchButton.dispatchEvent(event);

            expect(postcodeInput.classList.contains('ds_input--error')).toBeTrue();
            expect(postcodeQuestion.classList.contains('ds_question--error')).toBeTrue();
            expect(myLookup.fetchPostcodeResults).not.toHaveBeenCalled();
        });

        it('should show the address selection view if a successful search is carried out', () => {
            const myLookup = new PostcodeLookup(document.getElementById('property-postcode-lookup'));
            const searchButton = document.querySelector('.js-postcode-search');

            const postcodeInput = document.querySelector('#property-postcode-search');

            myLookup.PromiseRequest = mockFetchPostcodeResults(dummyResponse);

            postcodeInput.value = 'EH66QQ';

            const event = new Event('click', { bubbles: true });

            searchButton.dispatchEvent(event);

            const resultsSection = document.querySelector('.ds_address__results');

            window.setTimeout(() => {
                expect(resultsSection.classList.contains('fully-hidden')).toBeFalse();

                // six results plus two placeholders ('not listed', 'select address')
                expect(resultsSection.querySelector('select').children.length).toEqual(8);
            }, 0);
        });

        it('should show an error + message if there are no results returned', (done) => {
            const myLookup = new PostcodeLookup(document.getElementById('property-postcode-lookup'));
            const searchButton = document.querySelector('.js-postcode-search');

            const postcodeInput = document.querySelector('#property-postcode-search');

            myLookup.PromiseRequest = mockFetchPostcodeResults(dummyResponseNoResults);

            postcodeInput.value = 'EH66QQ';

            const event = new Event('click', { bubbles: true });

            spyOn(myLookup, 'showLookupError');

            searchButton.dispatchEvent(event);

            window.setTimeout(() => {
                expect(myLookup.showLookupError).toHaveBeenCalled();
                done();
            }, 0);
        });

        it('should show an error + message if there are no Scottish results returned for a RPZ search', (done) => {
            const myLookup = new PostcodeLookup(document.getElementById('property-postcode-lookup'), {rpz: true});
            const searchButton = document.querySelector('.js-postcode-search');

            const postcodeInput = document.querySelector('#property-postcode-search');

            myLookup.PromiseRequest = mockFetchPostcodeResults(dummyResponseNoScottishResults);

            postcodeInput.value = 'EH66QQ';

            const event = new Event('click', { bubbles: true });

            spyOn(myLookup, 'showLookupError').and.callThrough();

            searchButton.dispatchEvent(event);

            window.setTimeout(() => {
                expect(myLookup.showLookupError).toHaveBeenCalled();

                myLookup.removeLookupError();
                done();
            }, 0);
        });
    });

    describe('address selection', () => {
        it('should set values of the manual input elements for the selected address', () => {
            const myLookup = new PostcodeLookup(document.getElementById('property-postcode-lookup'));

            // mock up that view state
            const resultsSection = document.querySelector('.ds_address__results');
            const resultsSelect = resultsSection.querySelector('select');

            myLookup.resultsObject = JSON.parse(dummyResponse.responseText).results;

            const postcodeResultsTemplate = require('../../../src/main/scripts/templates/postcode-results');
            resultsSelect.innerHTML = postcodeResultsTemplate.render(JSON.parse(dummyResponse.responseText));

            resultsSelect.value = 2;
            resultsSelect.dispatchEvent(new Event('change'));

            expect(myLookup.buildingInput.value).toEqual('NETHER HARTSIDE');
            expect(myLookup.streetInput.value).toEqual('');
            expect(myLookup.townInput.value).toEqual('LAUDER');
            expect(myLookup.regionInput.value).toEqual('');
            expect(myLookup.postcodeInput.value).toEqual('TD2 6PU');
        });

        it('should not set the manual address input values if there isn\t a selected address', () => {
            const myLookup = new PostcodeLookup(document.getElementById('property-postcode-lookup'));

            // mock up that view state
            const resultsSection = document.querySelector('.ds_address__results');
            const resultsSelect = resultsSection.querySelector('select');

            myLookup.resultsObject = JSON.parse(dummyResponse.responseText).results;

            const postcodeResultsTemplate = require('../../../src/main/scripts/templates/postcode-results');
            resultsSelect.innerHTML = postcodeResultsTemplate.render(JSON.parse(dummyResponse.responseText));

            resultsSelect.value = -1;
            resultsSelect.dispatchEvent(new Event('change'));

            expect(myLookup.buildingInput.value).toEqual('');
            expect(myLookup.streetInput.value).toEqual('');
            expect(myLookup.townInput.value).toEqual('');
            expect(myLookup.regionInput.value).toEqual('');
            expect(myLookup.postcodeInput.value).toEqual('');
        });

        it('should not display the RPZ status of a property if this is a RPZ lookup', () => {
            const myLookup = new PostcodeLookup(document.getElementById('property-postcode-lookup'), {rpz: true});

            // mock up that view state
            const resultsSection = document.querySelector('.ds_address__results');
            const resultsSelect = resultsSection.querySelector('select');

            spyOn(myLookup, 'displayRpzStatus');

            myLookup.resultsObject = JSON.parse(dummyResponse.responseText).results;

            const postcodeResultsTemplate = require('../../../src/main/scripts/templates/postcode-results');
            resultsSelect.innerHTML = postcodeResultsTemplate.render(JSON.parse(dummyResponse.responseText));

            resultsSelect.value = -1;
            resultsSelect.dispatchEvent(new Event('change'));

            expect(myLookup.displayRpzStatus).toHaveBeenCalled();
        });
    });
});
