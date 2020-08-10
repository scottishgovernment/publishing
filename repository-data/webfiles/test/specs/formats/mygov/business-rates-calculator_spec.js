/* global jasmine, loadFixtures, spyOn, describe, fdescribe, xdescribe, beforeEach, afterEach, it, xit, fit, expect, $ */

'use strict';

jasmine.getFixtures().fixturesPath = 'base/test/fixtures';

import brc from '../../../../src/main/scripts/formats/mygov/business-rates-calculator';
import Handlebars from '../../../../src/main/scripts/vendor/hbs/handlebars';

describe('business-rates-calculator', function () {

    beforeEach(function () {
        window.ga = jasmine.createSpy('ga');
        loadFixtures('business-rates-calculator.html');
        brc.init();
        $('body').addClass('business-rates-calculator');

        // need a non-https api URL to test against
        brc.apiUrl = '/address/?search=';

        // pull this out to a fixture?
        this.properties = [
            {'rv': '2050', 'address': '(8)\nROSEBERY CRESCENT LANE\nEDINBURGH\nEH12 5JR', 'occupier': [
                {'name': 'THOMAS JOHNSTONE\nCARTSIDE AVENUE\nINCHINNON\nRENFREW\nPA4 9RU'}
            ]},
            {'rv': '8500', 'address': 'GLENORA GUEST HOUSE,14\nROSEBERY CRESCENT\nEDINBURGH\nEH12 5JY', 'occupier': [
                {'name': 'MS WENDY PHILLIPS'}
            ]},
            {'rv': '8200', 'address': 'MY EDINBURGH LIFE,13\nROSEBERY CRESCENT\nEDINBURGH\nEH12 5JY', 'occupier': [
                {'name': 'MR P CANNELL\nMIDDLERIG PROPERTIES\n12 MERCHISTON GARDENS\nEDINBURGH\nEH10 5DD'}
            ]},
            {'rv': '6400', 'address': '1A ROSEBERY CRESCENT LANE\nEDINBURGH\nEH12 5JR', 'occupier': [
                {'name': 'BERNISDALE HOMES'}
            ]},
            {'rv': '1200', 'address': '(05)\n3 ROSEBERY CRESCENT LANE\nEDINBURGH\nEH12 5JR', 'occupier': [
                {'name': 'CLARE CAMPBELL\n21A DOUGLAS CRESCENT\nEDINBURGH\nEH12 5BA'}
            ]},
            {'rv': '22100', 'address': '5 ROSEBERY CRESCENT\nEDINBURGH\nEH12 5JP', 'occupier': [
                {'name': 'LEVY & MCNALLY PER PALACE RESIDENTIAL LETS\n11 BELLEVUE CRESCENT\nEDINBURGH\nEH3 6ND'}
            ]},
            {'rv': '650', 'address': '6/1 ROSEBERY CRESCENT LANE\nEDINBURGH\nEH12 5JR', 'occupier': [
                {'name': 'J.F. WILLIAMS\nPO BOX 3\nELLON\nABERDEEN\nAB41 9EA'}
            ]},
            {'rv': '6600', 'address': '15 ROSEBERY CRESCENT\nEDINBURGH\nEH12 5JY', 'occupier': [
                {'name': 'TARA ROKPA EDINBURGH\n15 ROSEBERY CRESCENT\nEDINBURGH'}
            ]}
        ];
    });

    describe('the wizard', function () {
        beforeEach(function () {
            brc.currentProperty = {
                rv: 100000
            };
        });

        it('should go to the front page on restart()', function () {
            brc.wizard.restart();

            expect(brc.wizard.currentStep).toEqual('introduction');
            expect($('#introduction').hasClass('fully-hidden')).toBeFalsy();
        });

        it('should go to the next page on nextStep()', function () {
            brc.wizard.restart();
            brc.wizard.nextStep();

            expect(brc.wizard.currentStep).toEqual('property-address');
            expect($('#introduction').hasClass('fully-hidden')).toBeTruthy();
            expect($('#property-address').hasClass('fully-hidden')).toBeFalsy();
        });

        it('should go to the previous page on previousStep()', function () {
            brc.wizard.restart();
            brc.wizard.nextStep();
            brc.wizard.previousStep();

            expect(brc.wizard.currentStep).toEqual('introduction');
            expect($('#introduction').hasClass('fully-hidden')).toBeFalsy();
            expect($('#property-address').hasClass('fully-hidden')).toBeTruthy();
        });

        it('should throw an error if there is no next step to go to', function () {
            brc.wizard.currentStep = $('.wizard-step:last').attr('id');

            expect(function () {
                brc.wizard.nextStep();
            }).toThrow();
        });

        it('should throw an error if there is no previous step to go to', function () {
            brc.wizard.currentStep = $('.wizard-step:first').attr('id');

            expect(function () {
                brc.wizard.previousStep();
            }).toThrow();
        });

        xit('should scroll to the top of the page on step change', function () {
            brc.wizard.goToStep('property-address');

            expect(window.scrollY).toEqual(0);
        });

        // this spec is not finding the fixture
        xit('should navigate to a specific step on hash change', function () {
            spyOn(brc.wizard, 'goToStep');

            window.location.hash = 'property-results';

            expect(brc.wizard.goToStep).toHaveBeenCalledWith('property-results');
        });

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

                it('should give an error if the postcode does not validate', function () {
                    this.postcodeEl.val('not a postcode');

                    spyOn(brc, 'formError');
                    brc.findByPostCode();

                    expect(brc.formError).toHaveBeenCalledWith(this.postcodeEl.closest('.form-group'), 'Please try searching again using a full business postcode.');
                });

                it ('should give an error if the postcode is not Scottish', function () {
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
                expect($('#property-selection').find('label:first-of-type').text()).toEqual('TEST ADDRESS');
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
                expect($('#property-list').find('option').length).toEqual(8);
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

        describe('the large business supplement', function () {
            it('should add the large business supplement to the poundage where appropriate', function () {
                expect(brc.getBaseLiability(brc.ratesCalculatorData.large_business_supplement_threshold + 1)).toEqual((brc.ratesCalculatorData.large_business_supplement_threshold + 1) * (brc.ratesCalculatorData.poundage + brc.ratesCalculatorData.large_business_supplement));
                expect(brc.getBaseLiability(brc.ratesCalculatorData.intermediate_business_supplement_threshold + 1)).toEqual((brc.ratesCalculatorData.intermediate_business_supplement_threshold + 1) * (brc.ratesCalculatorData.poundage + brc.ratesCalculatorData.intermediate_business_supplement));
                expect(brc.getBaseLiability(brc.ratesCalculatorData.intermediate_business_supplement_threshold - 1)).toEqual((brc.ratesCalculatorData.intermediate_business_supplement_threshold - 1) * brc.ratesCalculatorData.poundage);
            });
        });

        describe('calculating the Small Business Bonus Scheme relief fraction', function () {
            it('should return 100% SBBS relief when individual property rateable value is up to ' + brc.ratesCalculatorData.sbbs_100_rv_threshold, function () {
                expect(brc.getSBBSFraction(brc.ratesCalculatorData.sbbs_100_rv_threshold, {rv: brc.ratesCalculatorData.sbbs_100_rv_threshold})).toEqual(brc.ratesCalculatorData.sbbs_100_percentage_relief);
            });

            it('should return 25% SBBS relief when individual property rateable value is greater than ' + brc.ratesCalculatorData.sbbs_100_rv_threshold +  ' and up to ' + brc.ratesCalculatorData.sbbs_25_rv_threshold, function () {
                expect(brc.getSBBSFraction(brc.ratesCalculatorData.sbbs_25_rv_threshold, {rv: brc.ratesCalculatorData.sbbs_25_rv_threshold})).toEqual(brc.ratesCalculatorData.sbbs_25_percentage_relief);
            });

            it('should return 0 SBBS relief when individual property rateable value is over ' + brc.ratesCalculatorData.sbbs_25_rv_threshold, function(){
                expect(brc.getSBBSFraction(brc.ratesCalculatorData.sbbs_25_rv_threshold + 1, {rv: brc.ratesCalculatorData.sbbs_25_rv_threshold + 1})).toEqual(0);
            });

            it('should return the correct SBBS relief when total rateable is up to combined property threshold of ' + brc.ratesCalculatorData.sbbs_combined_threshold, function () {
                expect(brc.getSBBSFraction(brc.ratesCalculatorData.sbbs_combined_threshold, {rv: brc.ratesCalculatorData.sbbs_100_rv_threshold})).toEqual(brc.ratesCalculatorData.sbbs_100_percentage_relief);
                expect(brc.getSBBSFraction(brc.ratesCalculatorData.sbbs_combined_threshold, {rv: brc.ratesCalculatorData.sbbs_25_rv_threshold})).toEqual(brc.ratesCalculatorData.sbbs_25_percentage_relief);
            });

            it('should return 0 SBBS relief when total rateable is over combined property threshold of ' + brc.ratesCalculatorData.sbbs_combined_threshold, function () {
                expect(brc.getSBBSFraction(brc.ratesCalculatorData.sbbs_combined_threshold + 1, {rv: brc.ratesCalculatorData.sbbs_100_rv_threshold})).toEqual(0);
                expect(brc.getSBBSFraction(brc.ratesCalculatorData.sbbs_combined_threshold + 1, {rv: brc.ratesCalculatorData.sbbs_25_rv_threshold})).toEqual(0);
            });
        });

        describe('the results themselves', function () {

            it('should calculate results correctly: case 1', function () {
                // provide a dummy for selectedProperties
                brc.selectedProperties = [
                    {
                        'rv': '11000',
                        'address': '(8)\nROSEBERY CRESCENT LANE\nEDINBURGH\nEH12 5JR',
                        'occupier': [
                            {'name': 'THOMAS JOHNSTONE\nCARTSIDE AVENUE\nINCHINNON\nRENFREW\nPA4 9RU'}
                        ],
                        'newBuild': false,
                        'vacant': 'moreThanOneYear'
                    }
                ];

                brc.calculateResults();

                expect(brc.selectedProperties[0].netLiability).toEqual(0);
            });

            it('should calculate results correctly: case 2', function () {
                // provide a dummy for selectedProperties
                brc.selectedProperties = [
                    {
                        'rv': '11000',
                        'address': '(8)\nROSEBERY CRESCENT LANE\nEDINBURGH\nEH12 5JR',
                        'occupier': [
                            {'name': 'THOMAS JOHNSTONE\nCARTSIDE AVENUE\nINCHINNON\nRENFREW\nPA4 9RU'}
                        ],
                        'newBuild': true,
                        'vacant': 'moreThanOneYear'
                    }
                ];

                brc.calculateResults();

                expect(brc.selectedProperties[0].netLiability).toEqual(0);
            });

            it('should calculate results correctly: case 3', function () {
                // provide a dummy for selectedProperties
                brc.selectedProperties = [
                    {
                        'rv': '17000',
                        'address': '(8)\nROSEBERY CRESCENT LANE\nEDINBURGH\nEH12 5JR',
                        'occupier': [
                            {'name': 'THOMAS JOHNSTONE\nCARTSIDE AVENUE\nINCHINNON\nRENFREW\nPA4 9RU'}
                        ],
                        'newBuild': false,
                        'vacant': 'no'
                    }
                ];

                brc.calculateResults();

                if (brc.ratesCalculatorData.universalRelief) {
                    expect(brc.selectedProperties[0].netLiability).toEqual(6214.04);
                } else {
                    expect(brc.selectedProperties[0].netLiability).toEqual(6247.5);
                }
            });

            it('should calculate results correctly: case 4 SBBS and Empty Property', function () {
                // provide a dummy for selectedProperties
                brc.selectedProperties = [
                    {
                        'rv': '9000',
                        'address': '(8)\nROSEBERY CRESCENT LANE\nEDINBURGH\nEH12 5JR',
                        'occupier': [
                            {'name': 'THOMAS JOHNSTONE\nCARTSIDE AVENUE\nINCHINNON\nRENFREW\nPA4 9RU'}
                        ],
                        'newBuild': false,
                        'vacant': 'no'
                    }
                ];

                brc.calculateResults();

                // expect SBBS to be present and a reduced fraction
                var appliedRelief = brc.selectedProperties[0].appliedRelief;

                expect(appliedRelief.name.toLowerCase()).toEqual('small business bonus scheme');
            });
        });

        it('should clear data on click on the "clear and start over" button', function () {
            // provide a dummy for selectedProperties
            brc.selectedProperties = [
                {
                    'rv': '17000',
                    'address': '(8)\nROSEBERY CRESCENT LANE\nEDINBURGH\nEH12 5JR',
                    'occupier': [
                        {'name': 'THOMAS JOHNSTONE\nCARTSIDE AVENUE\nINCHINNON\nRENFREW\nPA4 9RU'}
                    ],
                    'newBuild': false,
                    'vacant': 'no'
                }
            ];

            spyOn(brc, 'resetForms');

            brc.clearData();

            expect(brc.selectedProperties.length).toEqual(0);
            expect(brc.resetForms).toHaveBeenCalled();
        });

        describe('removing properties', function(){

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


    });

    describe('miscellany: the calculator', function () {

        it('should have a mechanism for reporting form errors', function () {
            var errorEl = $('#postcode').closest('.form-group');
            var message = 'sample error message';
            brc.formError(errorEl, message);

            expect($('.error-text').length).toEqual(1);
        });

        it('should have a mechanism for resetting/restarting', function () {
            spyOn(brc.wizard, 'goToStep');

            brc.resetForms();

            expect($('#postcode').val()).toEqual('');
            expect($('#property-list').length).toEqual(0);
            expect($('#address-form').css('display')).toEqual('none');
            expect(brc.currentProperty).not.toBeDefined();
            expect(brc.searchResults).not.toBeDefined();

            expect(brc.wizard.goToStep).toHaveBeenCalledWith('property-address');
        });

        it('should have an event binding capability', function(){
            // dummy up an event to bind
            brc.events = {
                'click a.reset': 'resetForms'
            };

            brc.bindEvents();

            spyOn(brc, 'resetForms');

            $('a.reset').trigger('click');

            expect(brc.resetForms).toHaveBeenCalled();
        });
    });

    describe('Miscellany: DOM fiddling', function(){
        it('should open the disclaimer or "more info" blocks on click of a specified link', function(){
            this.triggerLink = $('#test-more-link.more-info');

            this.target = $(this.triggerLink.attr('href'));

            this.target.css('display','none');

            this.triggerLink.trigger('click');

            expect(this.target.css('display')).not.toEqual('none');
        });

        it('should wrap the disclaimer in a containing element', function(){
            // we've already run init() so remove disclaimer and start over
            $('#disclaimer').remove();
            $('<div id="disclaimer">Disclaimer</div>').appendTo('body');

            expect($('aside#disclaimer').length).toEqual(0);

            brc.treatDisclaimer();

            expect($('aside#disclaimer').length).toEqual(1);
        });

        it('should not wrap a disclaimer if one is not present (??, but covering the else case)', function(){
            $('#disclaimer').remove();

            brc.treatDisclaimer();

            expect($('aside#disclaimer').length).toEqual(0);
        });
    });

    describe('Handlebars helpers', function () {

        var helpers = Handlebars.helpers;

        describe('propertyAddress', function () {

            it('should be defined', function () {
                expect(helpers.propertyAddress).toBeDefined();
            });

            it('should prepend an occupier to the address if one is present', function () {
                var propertyData = {
                    rv: 'XXXXXXXXXXXXXXXXXX',
                    address: '5 ROSEBERY CRESCENT\nEDINBURGH\nEH12 5JP',
                    occupier: [
                        {
                            name: 'LEVY & MCNALLY PER PALACE RESIDENTIAL LETS\n11 BELLEVUE CRESCENT\nEDINBURGH\nEH3 6ND'
                        }
                    ]
                };

                expect(helpers.propertyAddress(propertyData)).toEqual('LEVY & MCNALLY PER PALACE RESIDENTIAL LETS, 5 ROSEBERY CRESCENT\nEDINBURGH\nEH12 5JP');
            });

            it('should return just the address of the property if an occupier is not present', function () {
                var propertyData = {
                    'rv': '20800',
                    'address': '5 ROSEBERY CRESCENT\nEDINBURGH\nEH12 5JP',
                    'occupier': []
                };

                expect(helpers.propertyAddress(propertyData)).toEqual('5 ROSEBERY CRESCENT\nEDINBURGH\nEH12 5JP');
            });
        });

        describe('propertyAddressMultiLine', function () {

            it('should be defined', function () {
                expect(helpers.propertyAddressMultiLine).toBeDefined();
            });

            it('should prepend an occupier to the address if one is present', function () {
                var propertyData = {
                    rv: 'XXXXXXXXXXXXXXXXXX',
                    address: '5 ROSEBERY CRESCENT\nEDINBURGH\nEH12 5JP',
                    occupier: [
                        {
                            name: 'LEVY & MCNALLY PER PALACE RESIDENTIAL LETS\n11 BELLEVUE CRESCENT\nEDINBURGH\nEH3 6ND'
                        }
                    ]
                };

                expect(helpers.propertyAddressMultiLine(propertyData)).toEqual('LEVY & MCNALLY PER PALACE RESIDENTIAL LETS<br>5 ROSEBERY CRESCENT<br>EDINBURGH<br>EH12 5JP');
            });

            it('should return just the address of the property if an occupier is not present', function () {
                var propertyData = {
                    'rv': '20800',
                    'address': '5 ROSEBERY CRESCENT\nEDINBURGH\nEH12 5JP',
                    'occupier': []
                };

                expect(helpers.propertyAddressMultiLine(propertyData)).toEqual('5 ROSEBERY CRESCENT<br>EDINBURGH<br>EH12 5JP');
            });
        });

        describe('formatCurrency', function () {
            it('should be defined', function () {
                expect(helpers.formatCurrency).toBeDefined();
            });

            it('should return nicely-formatted currency results when supplied with a number', function () {
                var testNumbers = [
                    [0, '£0.00'],
                    [1.2, '£1.20'],
                    [1.23, '£1.23'],
                    [12.3, '£12.30'],
                    [12.34, '£12.34'],
                    [123.4, '£123.40'],
                    [123.45, '£123.45'],
                    [1234.5, '£1,234.50'],
                    [1234.56, '£1,234.56'],
                    [1234567.89, '£1,234,567.89'],
                    [12345.67890, '£12,345.68']
                ];

                for (var i = 0, il = testNumbers.length; i < il; i++) {
                    expect(helpers.formatCurrency(testNumbers[i][0])).toEqual(testNumbers[i][1]);
                }
            });
        });

        describe('formatDiscount', function () {
            it('should be defined', function () {
                expect(helpers.formatDiscount).toBeDefined();
            });

            it('should return a formatted discount', function () {
                expect(helpers.formatDiscount('0.3')).toEqual('30% discount');
            });
        });

        describe('propertyResultTitle', function () {
            it('should be defined', function () {
                expect(helpers.propertyResultTitle).toBeDefined();
            });

            it('should return a "Property X" title and remove button if there are multiple properties', function () {
                expect(helpers.propertyResultTitle(0, {properties: this.properties})).toEqual('<h3 class="brc-result__title">Property 8</h3><a href="#" class="remove-property  ds_button  ds_button--cancel  ds_button--small  ds_button--has-icon" data-property-index="0">Remove property<svg class="ds_icon" aria-hidden="true" role="img"><use xlink:href="/assets/images/icons/icons.stack.svg#close-21"></use></svg></a>');
            });

            it('should return just the title if there is just one property', function () {
                expect(helpers.propertyResultTitle(0, {properties: [this.properties[0]]})).toEqual('<h3 class="brc-result__title">Property 1</h3>');
            });
        });
    });
});

describe('business rates calculator: new values', function () {
    beforeEach(function () {
        const dateToUse = new Date(2020, 9, 1);

        window.ga = jasmine.createSpy('ga');
        loadFixtures('business-rates-calculator.html');
        brc.init(dateToUse);
        $('body').addClass('business-rates-calculator');

        // need a non-https api URL to test against
        brc.apiUrl = '/address/?search=';

        // from provided worked examples
        this.properties = [
            {
                'rv': '10000', 'address': '(8)\nROSEBERY CRESCENT LANE\nEDINBURGH\nEH12 5JR', 'occupier': [
                    { 'name': 'THOMAS JOHNSTONE\nCARTSIDE AVENUE\nINCHINNON\nRENFREW\nPA4 9RU' }
                ],
                expectedLiability: 0,
                expectedLiabilityWithUniversalRelief: 0
            },
            {
                'rv': '17000', 'address': '(8)\nROSEBERY CRESCENT LANE\nEDINBURGH\nEH12 5JR', 'occupier': [
                    { 'name': 'THOMAS JOHNSTONE\nCARTSIDE AVENUE\nINCHINNON\nRENFREW\nPA4 9RU' }
                ],
                expectedLiability: 6349.5,
                expectedLiabilityWithUniversalRelief: 6214.04
            },
            {
                'rv': '34000', 'address': '(8)\nROSEBERY CRESCENT LANE\nEDINBURGH\nEH12 5JR', 'occupier': [
                    { 'name': 'THOMAS JOHNSTONE\nCARTSIDE AVENUE\nINCHINNON\nRENFREW\nPA4 9RU' }
                ],
                expectedLiability: 16932,
                expectedLiabilityWithUniversalRelief: 16661.09
            },
            {
                'rv': '55000', 'address': '(8)\nROSEBERY CRESCENT LANE\nEDINBURGH\nEH12 5JR', 'occupier': [
                    { 'name': 'THOMAS JOHNSTONE\nCARTSIDE AVENUE\nINCHINNON\nRENFREW\nPA4 9RU' }
                ],
                expectedLiability: 28105,
                expectedLiabilityWithUniversalRelief: 27655.32
            },
            {
                'rv': '100000', 'address': '(8)\nROSEBERY CRESCENT LANE\nEDINBURGH\nEH12 5JR', 'occupier': [
                    { 'name': 'THOMAS JOHNSTONE\nCARTSIDE AVENUE\nINCHINNON\nRENFREW\nPA4 9RU' }
                ],
                expectedLiability: 52400,
                expectedLiabilityWithUniversalRelief: 51561.6
            }
        ];
    });

    it('should correctly calculate net liabilities with the updated values', function () {
        this.properties.forEach(function (property) {
            // provide a dummy for selectedProperties
            brc.selectedProperties = [
                property
            ];
            brc.calculateResults();

            if (brc.ratesCalculatorData.universalRelief) {
                expect(brc.selectedProperties[0].netLiability).toEqual(property.expectedLiabilityWithUniversalRelief);
            } else {
                expect(brc.selectedProperties[0].netLiability).toEqual(property.expectedLiability);
            }
        });
    });
});
