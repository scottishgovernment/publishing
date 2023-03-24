/* global jasmine, loadFixtures, spyOn, describe, fdescribe, xdescribe, beforeEach, afterEach, it, xit, fit, expect, $ */

'use strict';

jasmine.getFixtures().fixturesPath = 'base/test/fixtures';

import brc from '../../../../src/main/scripts/formats/mygov/business-rates-calculator';

// todo: much of this spec relates to the pre-multipageform version
xdescribe('business-rates-calculator', function () {

    beforeEach(function () {
        const dateToUse = new Date(2021, 9, 1);

        window.ga = jasmine.createSpy('ga');
        loadFixtures('business-rates-calculator.html');
        brc.init(dateToUse);

        $('body').addClass('business-rates-calculator');

        // need a non-https api URL to test against
        brc.apiUrl = '/address/?search=';

        this.properties = [
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
        ]
    });

    //Finding business properties by their addresses
    describe('step one: property address', function () {
        it('should show the address fields on showAddress()', function () {
            brc.showAddress();

            expect($('#address-form').css('display')).toEqual('block');
        });

        describe('findByAddress()', function () {
            it('should remove any extant address lists from the page', function () {
                brc.findByAddress();

                expect($('#property-selection').length).toEqual(0);
            });

            it ('should not show an error message if only the town field is left blank', function () {
                var townField = $('#town');
                var addressField = $('#address');

                townField.val('');
                addressField.val('foo');
                brc.findByAddress();
                expect($('.error-text').length).toEqual(0);
            });

            it ('should not show an error message if only the address field is left blank', function () {
                var townField = $('#town');
                var addressField = $('#address');

                townField.val('foo');
                addressField.val('');
                brc.findByAddress();
                expect($('.error-text').length).toEqual(0);
            });

            it ('should show one error messages if both fields are left blank', function () {
                var townField = $('#town');
                var addressField = $('#address');

                townField.val('');
                addressField.val('');
                brc.findByAddress();
                expect($('.error-text').length).toEqual(1);
            });

            it ('should remove any current error messages from the page before adding new ones', function () {
                var townField = $('#town');
                var addressField = $('#address');

                //First, create some errors.
                townField.val('');
                addressField.val('');
                brc.findByAddress();
                expect($('.error-text').length).toEqual(1);

                // now complete one field
                townField.val('foo');
                brc.findByAddress();
                expect($('.error-text').length).toEqual(0);

                // now complete both fields
                addressField.val('foo');
                townField.val('bar');
                brc.findByAddress();
                expect($('.error-text').length).toEqual(0);
            });

            describe('requesting external data', function () {
                var baseTimeOut;
                beforeEach(function () {
                    baseTimeOut = jasmine.DEFAULT_TIMEOUT_INTERVAL;
                    jasmine.DEFAULT_TIMEOUT_INTERVAL = 30000;
                });

                afterEach(function () {
                    jasmine.DEFAULT_TIMEOUT_INTERVAL = baseTimeOut;
                });

                it('should hide the next step button while fetching data', function () {
                    var step2Button = $('#property-list input[type=submit]');

                    brc.getProperties();

                    expect(step2Button.length).toEqual(0);
                });

                it('should display an error message if it fails to retrieve data from the remote service', function (done) {
                    $('#town').val('Nonexistent Street');
                    $('#address').val('Nowhere');

                    brc.getProperties();

                    setTimeout(function () {
                        expect($('.form-error').length).toBeGreaterThan(0);
                        done();
                    }, 9000);
                });

                xit('should disable additional form submissions until the API call completes', function (done) {
                    var submitButtons = $('#find-by-postcode').add('#find-by-address');

                    brc.getProperties();

                    submitButtons.each(function () {
                        expect($(this).prop('disabled')).toBeTruthy();
                    });

                    setTimeout(function () {
                        submitButtons.each(function () {
                            expect($(this).prop('disabled')).not.toBeTruthy();
                        });
                        done();
                    }, 6000);
                });
            });

            describe('submitting an address', function () {
                beforeEach(function () {
                    this.addressEl = $('#address');
                    this.townEl = $('#town');
                });

                it('should display errors if the address does not validate', function () {
                    this.addressEl.val('');
                    this.townEl.val('');

                    brc.findByAddress();

                    expect($('.error-text').length).toEqual(1);
                });

                it('should get a property list if the address validates', function () {
                    this.addressEl.val('rosebery crescent');
                    this.townEl.val('edinburgh');

                    spyOn(brc, 'getProperties');
                    brc.findByAddress();

                    expect(brc.getProperties).toHaveBeenCalledWith(this.addressEl.val() + ' ' + this.townEl.val());
                });
            });
        });

        //Finding business properties by their postcodes
        describe('findByPostCode()', function () {
            it('should remove any extant address lists from the page', function () {
                brc.findByPostCode();

                expect($('#property-selection').length).toEqual(0);
            });

            it('should display an error message when the postcode field is empty', function () {
                brc.findByPostCode();

                expect($('.error-text').length).toEqual(1);
            });

            it('should remove any extant error messages from the page when used successfully', function () {
                $('#postcode').val('EH12 5JP');
                brc.findByPostCode();

                expect($('.error-text').length).toEqual(0);
            });

            describe('submitting a postcode', function () {
                beforeEach(function () {
                    this.postcodeEl = $('#postcode');
                });

                xit('should give an error if the postcode does not validate', function () {
                    this.postcodeEl.val('not a postcode');

                    spyOn(brc, 'formError');
                    brc.findByPostCode();

                    expect(brc.formError).toHaveBeenCalledWith(this.postcodeEl.closest('.form-group'), 'Please try searching again using a full business postcode.');
                });

                xit ('should give an error if the postcode is not Scottish', function () {
                    this.postcodeEl.val('OL84LU');

                    spyOn(brc, 'formError');
                    brc.findByPostCode();

                    expect(brc.formError).toHaveBeenCalledWith(this.postcodeEl.closest('.form-group'), 'You have entered a postcode associated with an address outside Scotland. This site can only be used to calculate Business Rates for Scottish companies. <a href="https://www.gov.uk/calculate-your-business-rates">Please visit gov.uk for more information</a>.');
                });

                it('should get a property list if the postcode validates', function () {
                    this.postcodeEl.val('EH12 5JP');

                    spyOn(brc, 'getProperties');
                    brc.findByPostCode();

                    expect(brc.getProperties).toHaveBeenCalledWith(this.postcodeEl.val().replace(' ', ''));
                });
            });
        });

        describe('search results for a single result', function () {
            beforeEach(function () {
                brc.searchResults = [
                    {'rv': '20800', 'address': 'TEST ADDRESS', 'occupier': []}
                ];

                brc.showSearchResults();
            });

            it('should show the property', function () {
                expect($('#property-selection').find('label:first-of-type').text()).toEqual(', TEST ADDRESS');
            });

            it('should set brc.currentProperty correctly for this single result', function () {
                brc.setCurrentProperty();

                expect(brc.currentProperty).toEqual(brc.searchResults[0]);
            });
        });

        describe('search results for multiple results', function () {
            beforeEach(function () {
                // stub in a property set
                brc.searchResults = this.properties;

                brc.showSearchResults();
            });

            it('should show a list of properties', function () {
                // there are eight properties in the sample set
                expect($('#property-list').find('option').length).toEqual(5);
            });

            it('should set brc.currentProperty on submit of the results list', function () {
                var n = 3;

                $('#property-list').find('option:nth-child(' + n + ')').prop('selected', 'selected');

                brc.setCurrentProperty();

                expect(brc.currentProperty).toEqual(this.properties[n - 1]);
            });
        });

        describe('all property searches', function () {
            it('should reset sections of the DOM to a known default state on a property search', function () {
                // force population of a few DOM elements we would expect to be addressed
                $('<form id="property-list"></form>').appendTo('body');
                $('<div class="error-block"></div>').appendTo('body');
                brc.formError($('#postcode'), 'example error');

                brc.cleanOnSearch();

                expect($('#property-list').length).toEqual(0);
                expect($('.error-block').length).toEqual(0);
                expect($('.error-text').length).toEqual(0);
            });
        });

        describe('clicking on the next step link', function(){
            beforeEach(function(){
                $('<form id="property-list"><div id="property-selection" class="block box-element result-count-2"><label for="property-select">Select the property</label><select id="property-select"><option value="0">SCOTTISH GOVERNMENT, (A) VICTORIA QUAY EDINBURGH EH6 6QQ</option><option value="1">BANK OF SCOTLAND PER HBOS GROUP PROPERTY (RATING), (A1) VICTORIA QUAY EDINBURGH EH6 6QQ</option></select><p><a href="http://saa.gov.uk/contact.html" class="small">Is your property listed? Let us know.</a></p></div><input class="primary" type="submit" value="Step 2"></form>').appendTo('body');

                this.nextStepButton = $('#property-list input[type=submit]');
            });

            afterEach(function () {
                $('#property-list').remove();
            });

            xit('should save the property', function(){
                spyOn(brc, 'setCurrentProperty');

                this.nextStepButton.trigger('click');

                expect(brc.setCurrentProperty).toHaveBeenCalled();
            });

            xit('should progress the tool to the next step', function(){
                spyOn(brc.wizard, 'goToStep');

                this.nextStepButton.trigger('click');

                expect(brc.wizard.goToStep).toHaveBeenCalledWith('results');
            });

            it('should add properties to a collection of selectedProperties', function () {
                brc.selectedProperties = [
                    {
                        'rv': '11000',
                        'address': 'TEST ADDRESS ONE',
                        'occupier': [
                            {'name': 'THOMAS JOHNSTONE\nCARTSIDE AVENUE\nINCHINNON\nRENFREW\nPA4 9RU'}
                        ],
                        'newBuild': 'false',
                        'vacant': 'moreThanOneYear'
                    }
                ];

                brc.currentProperty = {
                    'rv': '11000',
                    'address': 'TEST ADDRESS TWO',
                    'occupier': [
                        {'name': 'THOMAS JOHNSTONE\nCARTSIDE AVENUE\nINCHINNON\nRENFREW\nPA4 9RU'}
                    ]
                };

                brc.updateOrAddProperty();

                expect(brc.selectedProperties.length).toEqual(2);
            });
        });
    });

    describe('step 2: calculation results', function () {
        beforeEach(function () {
            loadFixtures('business-rates-calculator.html');
            brc.init();
            $('body').addClass('business-rates-calculator');
        });

        // the large business supplement
        it('should add the large business supplement to the poundage where appropriate', function () {
            expect(brc.getBaseLiability(brc.ratesCalculatorData.large_business_supplement_threshold + 1)).toEqual((brc.ratesCalculatorData.large_business_supplement_threshold + 1) * (brc.ratesCalculatorData.poundage + brc.ratesCalculatorData.large_business_supplement));
            expect(brc.getBaseLiability(brc.ratesCalculatorData.intermediate_business_supplement_threshold + 1)).toEqual((brc.ratesCalculatorData.intermediate_business_supplement_threshold + 1) * (brc.ratesCalculatorData.poundage + brc.ratesCalculatorData.intermediate_business_supplement));
            expect(brc.getBaseLiability(brc.ratesCalculatorData.intermediate_business_supplement_threshold - 1)).toEqual((brc.ratesCalculatorData.intermediate_business_supplement_threshold - 1) * brc.ratesCalculatorData.poundage);
        });

        // calculating the Small Business Bonus Scheme relief fraction
        it('should return 100% SBBS relief when individual property rateable value is up to [sbbs_100_rv_threshold]', function () {
            expect(brc.getSBBSFraction(brc.ratesCalculatorData.sbbs_100_rv_threshold, {rv: brc.ratesCalculatorData.sbbs_100_rv_threshold})).toEqual(brc.ratesCalculatorData.sbbs_100_percentage_relief);
        });

        it('should return 25% SBBS relief when individual property rateable value is greater than [sbbs_100_rv_threshold] and up to [sbbs_25_rv_threshold]', function () {
            expect(brc.getSBBSFraction(brc.ratesCalculatorData.sbbs_25_rv_threshold, {rv: brc.ratesCalculatorData.sbbs_25_rv_threshold})).toEqual(brc.ratesCalculatorData.sbbs_25_percentage_relief);
        });

        it('should return 0 SBBS relief when individual property rateable value is over [sbbs_25_rv_threshold]', function(){
            expect(brc.getSBBSFraction(brc.ratesCalculatorData.sbbs_25_rv_threshold + 1, {rv: brc.ratesCalculatorData.sbbs_25_rv_threshold + 1})).toEqual(0);
        });

        it('should return the correct SBBS relief when total rateable is up to combined property threshold of [sbbs_combined_threshold]', function () {
            expect(brc.getSBBSFraction(brc.ratesCalculatorData.sbbs_combined_threshold, {rv: brc.ratesCalculatorData.sbbs_100_rv_threshold})).toEqual(brc.ratesCalculatorData.sbbs_100_percentage_relief);
            expect(brc.getSBBSFraction(brc.ratesCalculatorData.sbbs_combined_threshold, {rv: brc.ratesCalculatorData.sbbs_25_rv_threshold})).toEqual(brc.ratesCalculatorData.sbbs_25_percentage_relief);
        });

        it('should return 0 SBBS relief when total rateable is over combined property threshold of [sbbs_combined_threshold]', function () {
            expect(brc.getSBBSFraction(brc.ratesCalculatorData.sbbs_combined_threshold + 1, {rv: brc.ratesCalculatorData.sbbs_100_rv_threshold})).toEqual(0);
            expect(brc.getSBBSFraction(brc.ratesCalculatorData.sbbs_combined_threshold + 1, {rv: brc.ratesCalculatorData.sbbs_25_rv_threshold})).toEqual(0);
        });

        // the results themselves
        it('should calculate results correctly: example 1', function () {
            // provide a dummy for selectedProperties
            brc.selectedProperties = [];
            brc.selectedProperties.push(this.properties[0]);

            brc.calculateResults();

            expect(brc.selectedProperties[0].netLiability).toEqual(0);
        });

        it('should calculate results correctly: example 2', function () {
            // provide a dummy for selectedProperties
            brc.selectedProperties = [];
            brc.selectedProperties.push(this.properties[1]);

            brc.calculateResults();

            expect(brc.selectedProperties[0].netLiability).toEqual(6247.5);
        });

        it('should calculate results correctly: example 3', function () {
            // provide a dummy for selectedProperties
            brc.selectedProperties = [];
            brc.selectedProperties.push(this.properties[2]);

            brc.calculateResults();

            if (brc.ratesCalculatorData.universalRelief) {
                expect(brc.selectedProperties[0].netLiability).toEqual(16660);
            } else {
                expect(brc.selectedProperties[0].netLiability).toEqual(16660);
            }
        });

        it('should calculate results correctly: example 4', function () {
            // provide a dummy for selectedProperties
            brc.selectedProperties = [];
            brc.selectedProperties.push(this.properties[3]);

            brc.calculateResults();

            if (brc.ratesCalculatorData.universalRelief) {
                expect(brc.selectedProperties[0].netLiability).toEqual(27665);
            } else {
                expect(brc.selectedProperties[0].netLiability).toEqual(27665);
            }
        });

        it('should calculate results correctly: example 5', function () {
            // provide a dummy for selectedProperties
            brc.selectedProperties = [];
            brc.selectedProperties.push(this.properties[4]);

            brc.calculateResults();

            if (brc.ratesCalculatorData.universalRelief) {
                expect(brc.selectedProperties[0].netLiability).toEqual(51600);
            } else {
                expect(brc.selectedProperties[0].netLiability).toEqual(51600);
            }
        });

        // removing properties
        it('should have a mechanism for removing a property from the results', function () {
            brc.selectedProperties = [
                {
                    'rv': '17000',
                    'address': 'PROPERTY ONE',
                    'occupier': [
                        {'name': 'THOMAS JOHNSTONE\nCARTSIDE AVENUE\nINCHINNON\nRENFREW\nPA4 9RU'}
                    ],
                    'newBuild': false,
                    'vacant': 'no'
                },
                {
                    'rv': '17000',
                    'address': 'PROPERTY TWO',
                    'occupier': [
                        {'name': 'THOMAS JOHNSTONE\nCARTSIDE AVENUE\nINCHINNON\nRENFREW\nPA4 9RU'}
                    ],
                    'newBuild': false,
                    'vacant': 'no'
                }
            ];

            // remove first property
            brc.removePropertyFromResults(0);

            expect(brc.selectedProperties.length).toEqual(1);
            expect(brc.selectedProperties[0].address).toEqual('PROPERTY TWO');

            // restart
            brc.selectedProperties = [
                {
                    'rv': '17000',
                    'address': 'PROPERTY ONE',
                    'occupier': [
                        {'name': 'THOMAS JOHNSTONE\nCARTSIDE AVENUE\nINCHINNON\nRENFREW\nPA4 9RU'}
                    ],
                    'newBuild': false,
                    'vacant': 'no'
                },
                {
                    'rv': '17000',
                    'address': 'PROPERTY TWO',
                    'occupier': [
                        {'name': 'THOMAS JOHNSTONE\nCARTSIDE AVENUE\nINCHINNON\nRENFREW\nPA4 9RU'}
                    ],
                    'newBuild': false,
                    'vacant': 'no'
                }
            ];

            // remove the second property
            brc.removePropertyFromResults(1);

            expect(brc.selectedProperties.length).toEqual(1);
            expect(brc.selectedProperties[0].address).toEqual('PROPERTY ONE');

        });

        it('should update the results when removing a property from the results', function(){
            brc.selectedProperties = [
                {
                    'rv': '17000',
                    'address': 'PROPERTY ONE',
                    'occupier': [
                        {'name': 'THOMAS JOHNSTONE\nCARTSIDE AVENUE\nINCHINNON\nRENFREW\nPA4 9RU'}
                    ],
                    'newBuild': false,
                    'vacant': 'no'
                },
                {
                    'rv': '17000',
                    'address': 'PROPERTY TWO',
                    'occupier': [
                        {'name': 'THOMAS JOHNSTONE\nCARTSIDE AVENUE\nINCHINNON\nRENFREW\nPA4 9RU'}
                    ],
                    'newBuild': false,
                    'vacant': 'no'
                }
            ];

            spyOn(brc, 'calculateResults');

            brc.removePropertyFromResults(1);

            expect(brc.calculateResults).toHaveBeenCalled();
        });

        xit('should call removePropertyFromResults on click of "remove" buttons', function(){
            var removeButton = $($('.business-rates-calculator .remove-property')[0]);

            spyOn(brc, 'removePropertyFromResults');

            removeButton.trigger('click');

            expect(brc.removePropertyFromResults).toHaveBeenCalled();
        });


    });

    describe('miscellany: the calculator', function () {
        beforeEach(function () {
            loadFixtures('business-rates-calculator.html');
            brc.init();
            $('body').addClass('business-rates-calculator');
        });

        it('should have a mechanism for resetting/restarting', function () {
            brc.resetForms();

            expect($('#postcode').val()).toEqual('');
            expect(brc.currentProperty).not.toBeDefined();
            expect(brc.searchResults).not.toBeDefined();
        });
    });
});
