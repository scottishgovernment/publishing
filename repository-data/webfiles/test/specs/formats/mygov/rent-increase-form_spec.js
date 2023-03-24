/* global describe beforeEach it expect spyOn $ */

'use strict';

jasmine.getFixtures().fixturesPath = 'base/test/fixtures';

import rentIncreaseForm from '../../../../src/main/scripts/formats/mygov/rent-increase-form';

describe("Rent increase form", function() {

    let rpzData = { "validPostcode": true, "scottishPostcode": true, "inRentPressureZone": true, "rentPressureZoneTitle": "Stockbridge, Edinburgh", "maxIncrease": 1.5 };

    beforeEach(function () {
        window.body = '';
        loadFixtures('rent-increase-form.html');
    });

    it("should be defined", function() {
        expect(rentIncreaseForm).toBeDefined();
    });

    describe('maxRentNormalisedPerAnnum', function () {

        it ('should determine a maximum permitted rent amount per and normalise it for a year', function () {
            // set some initial values
            rentIncreaseForm.form.settings.formObject.currentRentAmount = 800;
            rentIncreaseForm.form.settings.formObject.improvementsIncrease = 10;
            rentIncreaseForm.cpiDelta = 0.5;
            rentIncreaseForm.rpzData = rpzData;
            let output;

            rentIncreaseForm.form.settings.formObject.currentRentFrequency = 'WEEKLY';
            output = rentIncreaseForm.getMaximumRentAmountNormalizedPerAnnum();
            expect(output).toEqual('43487.14');

            rentIncreaseForm.form.settings.formObject.currentRentFrequency = 'FORTNIGHTLY';
            output = rentIncreaseForm.getMaximumRentAmountNormalizedPerAnnum();
            expect(output).toEqual('21743.57');

            rentIncreaseForm.form.settings.formObject.currentRentFrequency = 'EVERY_FOUR_WEEKS';
            output = rentIncreaseForm.getMaximumRentAmountNormalizedPerAnnum();
            expect(output).toEqual('10871.79');

            rentIncreaseForm.form.settings.formObject.currentRentFrequency = 'CALENDAR_MONTH';
            output = rentIncreaseForm.getMaximumRentAmountNormalizedPerAnnum();
            expect(output).toEqual('10008.00');

            rentIncreaseForm.form.settings.formObject.currentRentFrequency = 'QUARTERLY';
            output = rentIncreaseForm.getMaximumRentAmountNormalizedPerAnnum();
            expect(output).toEqual('3336.00');

            rentIncreaseForm.form.settings.formObject.currentRentFrequency = 'EVERY_SIX_MONTHS';
            output = rentIncreaseForm.getMaximumRentAmountNormalizedPerAnnum();
            expect(output).toEqual('1668.00');
        });

        it ('should return null if no period specified', function () {
            rentIncreaseForm.form.settings.formObject.currentRentAmount = 800;
            rentIncreaseForm.form.settings.formObject.improvementsIncrease = 10;
            rentIncreaseForm.cpiDelta = 0.5;
            rentIncreaseForm.rpzData = rpzData;
            let output;

            rentIncreaseForm.form.settings.formObject.currentRentFrequency = 'SOME_NONSENSE';
            output = rentIncreaseForm.getMaximumRentAmountNormalizedPerAnnum();
            expect(output).toBeNull();
        });

        it ('should pass through if no CPI delta given', function () {
            rentIncreaseForm.form.settings.formObject.currentRentAmount = 800;
            rentIncreaseForm.form.settings.formObject.improvementsIncrease = 10;
            rentIncreaseForm.cpiDelta = null;
            rentIncreaseForm.rpzData = rpzData;
            let output;

            rentIncreaseForm.form.settings.formObject.currentRentFrequency = 'WEEKLY';
            output = rentIncreaseForm.getMaximumRentAmountNormalizedPerAnnum();
            expect(output).toEqual('43278.57');
        });
    });

    describe('maxRentIncreasePerPeriod', function () {
        it ('should determine a maximum permitted rent increase per rent payment period based on input values', function () {
            let period;
            let maximumRentNormalizedPerAnnum = 10000;
            let output;

            period = 'WEEKLY';
            output = rentIncreaseForm.getMaximumRentAmountForPeriod(period, maximumRentNormalizedPerAnnum);
            expect(output).toEqual('191.78');

            period = 'FORTNIGHTLY';
            output = rentIncreaseForm.getMaximumRentAmountForPeriod(period, maximumRentNormalizedPerAnnum);
            expect(output).toEqual('383.56');

            period = 'EVERY_FOUR_WEEKS';
            output = rentIncreaseForm.getMaximumRentAmountForPeriod(period, maximumRentNormalizedPerAnnum);
            expect(output).toEqual('767.12');

            period = 'CALENDAR_MONTH';
            output = rentIncreaseForm.getMaximumRentAmountForPeriod(period, maximumRentNormalizedPerAnnum);
            expect(output).toEqual('833.33');

            period = 'QUARTERLY';
            output = rentIncreaseForm.getMaximumRentAmountForPeriod(period, maximumRentNormalizedPerAnnum);
            expect(output).toEqual('2500.00');

            period = 'EVERY_SIX_MONTHS';
            output = rentIncreaseForm.getMaximumRentAmountForPeriod(period, maximumRentNormalizedPerAnnum);
            expect(output).toEqual('5000.00');
        });

        it ('should return null if no period specified', function () {
            let period = 'SOME_NONSENSE';
            let output = rentIncreaseForm.getMaximumRentAmountForPeriod(period, 10000);
            expect(output).toBeNull();
        });
    });

    describe('rpzComplete', function () {
        xit ('should disable/hide the improvements page if the property is not in a RPZ', function () {
            rentIncreaseForm.currentStep = {
                slug: 'new-rent',
                title: 'New rent',
                section: 'increase',
                sectionTitle: 'Increase'
            }

            rentIncreaseForm.rpzComplete(rpzData);
            rentIncreaseForm.form.goToStep({
                slug: 'new-rent',
                title: 'New rent',
                section: 'increase',
                sectionTitle: 'Increase'
            });

            let subsectionIndicatorItems = [].slice.call(document.querySelectorAll('#subsection-progess-indicator .page-group__item'));
            let improvementsItem = subsectionIndicatorItems.filter(
                item => item.innerText === 'Improvements'
            );

            expect (improvementsItem.length).toEqual(1);

            rentIncreaseForm.rpzComplete(false);
            rentIncreaseForm.form.goToStep({
                slug: 'new-rent',
                title: 'New rent',
                section: 'increase',
                sectionTitle: 'Increase'
            });

            subsectionIndicatorItems = [].slice.call(document.querySelectorAll('#subsection-progess-indicator .page-group__item'));
            improvementsItem = subsectionIndicatorItems.filter(
                item => item.innerText === 'Improvements'
            );

            expect (improvementsItem.length).toEqual(0);
        });
    });

    describe('setupIncreaseAmountPage', function () {
        it('should do nothing if the property is not in a RPZ', function () {
            rentIncreaseForm.setupIncreaseAmountPage();

            expect($('#new-rent-amount').attr('data-maxvalue')).toBeUndefined();
        });

        xit ('should set a maxvalue for new rent if the property is in a RPZ', function () {
            spyOn($, 'get').and.callFake(function(e) {
                return $.Deferred().resolve(0.1).promise();
            });

            rentIncreaseForm.form.settings.formObject.lastIncreaseDate = '01/01/2017';
            rentIncreaseForm.form.settings.formObject.inRentPressureZone = true;
            rentIncreaseForm.form.settings.formObject.newRentFrequency = 'WEEKLY';
            rentIncreaseForm.form.settings.formObject.currentRentAmount = 100;
            rentIncreaseForm.form.settings.formObject.currentRentFrequency = 'WEEKLY';
            rentIncreaseForm.rpzData = rpzData;
            rentIncreaseForm.setupIncreaseAmountPage();

            expect($('#new-rent-amount').attr('data-maxvalue')).not.toBeUndefined();
        });

        xit ('should use a CPI value of zero if data is unreadable', function () {
            spyOn($, 'get').and.callFake(function(e) {
                return $.Deferred().resolve('foo').promise();
            });

            rentIncreaseForm.form.settings.formObject.lastIncreaseDate = '01/01/2017';
            rentIncreaseForm.form.settings.formObject.inRentPressureZone = true;
            rentIncreaseForm.form.settings.formObject.newRentFrequency = 'WEEKLY';
            rentIncreaseForm.form.settings.formObject.currentRentAmount = 100;
            rentIncreaseForm.form.settings.formObject.currentRentFrequency = 'WEEKLY';
            rentIncreaseForm.rpzData = rpzData;
            rentIncreaseForm.setupIncreaseAmountPage();

            expect(rentIncreaseForm.cpiDelta).toEqual(0);
        });
    });

    describe ('prepareFormDataForPost', function () {
        let formData = {}

        xit ('should format tenants and landlords into arrays', function () {
            formData.tenantNames = {
                'tenant-1': {foo: 'bar'},
                'tenant-2': {foo2: 'bar2'}
            }

            formData.landlords = {
                'landlord-1': {foo: 'bar'},
                'landlord-2': {foo2: 'bar2'}
            }
            formData.dateOfIncrease = '02/01/2018';
            rentIncreaseForm.notificationDate = new Date('02/02/2018');
            window.storedAddresses.propertyAddressResult = {
                org: 'org',
                building: 'building',
                street: 'street',
                locality: 'locality',
                town: 'town',
                postcode: 'EH66QQ'
            }
            rentIncreaseForm.rpzData = {};

            let formDataForPost = rentIncreaseForm.prepareFormDataForPost(formData);

            expect(formDataForPost.tenantNames).toEqual([{"foo":"bar"},{"foo2":"bar2"}]);
            expect(formDataForPost.landlords).toEqual([{"foo":"bar"},{"foo2":"bar2"}]);
        });

        xit ('should format the data into the expected format', function () {
            let expectedData = {
                notificationDate: '2018-02-02',
                capFromDate: '01-02-2018',
                capToDate: '01-02-2020',
                address: {
                    building: 'org, building',
                    street: 'street',
                    town: 'locality, town',
                    postcode: 'EH66QQ'
                },

                inRentPressureZone: true,
                landlordsAgent: {name: 'bob'},
                oldRentAmount: 1000,
                oldRentPeriod: 'CALENDAR_MONTH',
                newRentAmount: 1100,
                newRentPeriod: 'CALENDAR_MONTH',
                rentIncreaseDate: '2018-01-02',
                landlords: [{
                    "foo": "bar"
                }, {
                    "foo2": "bar2"
                }],
                tenantNames: [{
                    "foo": "bar"
                }, {
                    "foo2": "bar2"
                }],
                lastRentIncreaseDate: '2012-02-01'
            }

            // actual values
            let formData = {
                inRentPressureZone: true,
                lettingAgent: {
                    name: 'bob'
                },
                currentRentAmount: 1000,
                currentRentFrequency: 'CALENDAR_MONTH',
                newRentAmount: 1100,
                newRentFrequency: 'CALENDAR_MONTH',
                dateOfIncrease: '02/01/2018',
                tenantNames: {
                    'tenant-1': {foo: 'bar'},
                    'tenant-2': {foo2: 'bar2'}
                },
                landlords: {
                    'landlord-1': {foo: 'bar'},
                    'landlord-2': {foo2: 'bar2'}
                }
            }

            rentIncreaseForm.form.settings.formObject.lastIncreaseDate = '01-02-2012';

            rentIncreaseForm.rpzData = {
                dateFrom: '01-02-2018',
                dateTo: '01-02-2020'
            }

            rentIncreaseForm.notificationDate = new Date('02/02/2018');

            window.storedAddresses.propertyAddressResult = {
                org: 'org',
                building: 'building',
                street: 'street',
                locality: 'locality',
                town: 'town',
                postcode: 'EH66QQ'
            }

            let formDataForPost = rentIncreaseForm.prepareFormDataForPost(formData);

            expect(formDataForPost).toEqual(expectedData);

        });
    });
});
