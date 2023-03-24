/* global jasmine, loadFixtures, spyOn, describe, fdescribe, xdescribe, beforeEach, afterEach, it, xit, fit, expect, $ */

'use strict';

jasmine.getFixtures().fixturesPath = 'base/test/fixtures';

import brc from '../../../../src/main/scripts/formats/mygov/business-rates-calculator';
const testObj = {};

fdescribe('business-rates-calculator', function () {

    beforeEach(function () {
        const dateToUse = new Date(2021, 9, 1);

        window.ga = jasmine.createSpy('ga');
        loadFixtures('business-rates-calculator.html');
        brc.init(dateToUse);

        $('body').addClass('business-rates-calculator');

        // need a non-https api URL to test against
        brc.apiUrl = '/address/?search=';

        testObj.properties = [
            {
                rv: 10000,
                address: 'Example 1',
                occupier: [
                    {
                        name: 'Example 1'
                    }
                ]
            },
            {
                rv: 17000,
                address: 'Example 2',
                occupier: [
                    {
                        name: 'Example 2'
                    }
                ]
            },
            {
                rv: 34000,
                address: 'Example 3',
                occupier: [
                    {
                        name: 'Example 3'
                    }
                ]
            },
            {
                rv: 55000,
                address: 'Example 4',
                occupier: [
                    {
                        name: 'Example 4'
                    }
                ]
            },
            {
                rv: 100000,
                address: 'Example 5',
                occupier: [
                    {
                        name: 'Example 5'
                    }
                ]
            }
        ];
    });

    //Finding business properties by their addresses
    describe('step one: property search', function () {
        describe('find by postcode', () => {
            beforeEach(() => {
                testObj.postcodeInput = document.getElementById('postcode');
            });

            it('should display an error message when the postcode field is empty', function () {
                brc.findByPostcode();

                expect(testObj.postcodeInput.classList.contains('ds_input--error')).toBeTrue();
            });

            it('should give an error if the postcode does not validate', function () {
                testObj.postcodeInput.value = 'not a postcode';

                spyOn(brc, 'addFieldError').and.callThrough();
                brc.findByPostcode();

                expect(document.querySelector('.ds_question__error-message')).toBeDefined();

                expect(brc.addFieldError).toHaveBeenCalled();
            });

            it ('should give an error if the postcode is not Scottish', function () {
                testObj.postcodeInput.value = 'SW1A2EY';

                spyOn(brc, 'addFieldError').and.callThrough();
                brc.findByPostcode();

                expect(document.querySelector('.ds_question__error-message')).toBeDefined();

                expect(brc.addFieldError).toHaveBeenCalled();
            });

            it('should get a property list if the postcode validates', function () {
                testObj.postcodeInput.value = 'EH12 5JP';

                spyOn(brc, 'getProperties');
                brc.findByPostcode();

                expect(brc.getProperties).toHaveBeenCalledWith(testObj.postcodeInput.value.replace(' ', ''));
            });
        });

        describe('search results for a single result', () => {
            beforeEach(() => {
                brc.searchResults = [
                    {'rv': '20800', 'address': 'TEST ADDRESS', 'occupier': []}
                ];

                brc.showSearchResults();
            });

            it('should show the property', function () {
                expect(document.querySelector('#property-selection label').innerText).toEqual(', TEST ADDRESS');
            });

            it('should set brc.currentProperty correctly for this single result', function () {
                brc.setCurrentProperty();

                expect(brc.currentProperty).toEqual(brc.searchResults[0]);
            });
        });

        describe('search results for multiple results', () => {
            beforeEach(() => {
                // stub in a property set
                brc.searchResults = testObj.properties;

                brc.showSearchResults();
            });

            it('should show a list of properties', () => {
                // there are eight properties in the sample set
                expect(document.querySelectorAll('#property-list option').length).toEqual(5);
            });

            it('should set brc.currentProperty on submit of the results list', () => {
                let n = 3;

                document.querySelector('#property-list option:nth-child(' + n + ')').selected = 'selected';

                brc.setCurrentProperty();

                expect(brc.currentProperty).toEqual(testObj.properties[n - 1]);
            });
        });

    });

    describe('step two: calculation results', () => {

    });

    describe('miscellany: the calculator', () => {

    });
});
