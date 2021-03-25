/* global describe beforeEach it expect spyOn $ */

'use strict';

jasmine.getFixtures().fixturesPath = 'base/test/fixtures';

import rentAdjudicationForm from '../../../../src/main/scripts/formats/mygov/rent-adjudication-form';

describe("Rent adjudication form", function() {

    describe ('formEvents', function () {
        it ('should have an "update summary" function', function () {
            rentAdjudicationForm.init();
            var updateSummary = rentAdjudicationForm.form.settings.formEvents.updateSummary;
            expect (typeof updateSummary).toEqual('function');
        });

        it ('should have an "insert property address" function', function () {
            rentAdjudicationForm.init();
            var insertPropertyAddress = rentAdjudicationForm.form.settings.formEvents.insertPropertyAddress;
            expect (typeof insertPropertyAddress).toEqual('function');
        });

        it ('should have a "check inventory" function', function () {
            rentAdjudicationForm.init();
            var checkInventory = rentAdjudicationForm.form.settings.formEvents.checkInventory;
            expect (typeof checkInventory).toEqual('function');
        });
    });

    describe ('prepareFormDataForPost', function () {
        let formData = {}

        it ('should add the value for "other" property type when relevant', function () {

        });

        it ('should format tenants and landlords into arrays', function () {
            formData.tenants = {
                'tenant-1': {foo: 'bar'},
                'tenant-2': {foo2: 'bar2'}
            }

            formData.landlords = {
                'landlord-1': {foo: 'bar'},
                'landlord-2': {foo2: 'bar2'}
            }

            let formDataForPost = rentAdjudicationForm.prepareFormDataForPost(formData);

            expect(formDataForPost.tenants).toEqual([{"foo":"bar"},{"foo2":"bar2"}]);
            expect(formDataForPost.landlords).toEqual([{"foo":"bar"},{"foo2":"bar2"}]);
        });

        it ('should not include letting agent details if there is no letting agent', function () {
            formData.lettingAgent = {
                name: 'bob',
                address: {
                    postcode: 'AA1 1AA'
                }
            }

            let formDataForPost = rentAdjudicationForm.prepareFormDataForPost(formData);
            expect(formDataForPost.lettingAgent).toEqual({
                name: 'bob',
                address: {
                    postcode: 'AA1 1AA'
                }
            });

            formData.hasLettingAgent = 'no';

            formDataForPost = rentAdjudicationForm.prepareFormDataForPost(formData);

            expect(formDataForPost.lettingAgent).toEqual({});
        });

        it ('should not include tenant agent details if there is no tenant agent', function () {
            formData.tenantAgent = {
                name: 'bob',
                address: {
                    postcode: 'AA1 1AA'
                }
            }

            let formDataForPost = rentAdjudicationForm.prepareFormDataForPost(formData);
            expect(formDataForPost.tenantAgent).toEqual({
                name: 'bob',
                address: {
                    postcode: 'AA1 1AA'
                }
            });

            formData.hasTenantAgent = 'no';

            formDataForPost = rentAdjudicationForm.prepareFormDataForPost(formData);

            expect(formDataForPost.tenantAgent).toEqual({});
        });

        it ('should not include various property details if their query is set to "no"', function () {
            formData = {
                sharedAreas: 'angel',
                included: 'bee sting',
                servicesDetails: 'carrot',
                servicesCostDetails: 'devil\'s food',
                furnished: 'eccles',
                improvementsTenant: 'fruit',
                improvementsLandlord: 'genoa',
                damage: 'hummingbird',

                sharedAreasQuery: 'yes',
                includedQuery: 'yes',
                servicesQuery: 'yes',
                tenantImprovementsQuery: 'yes',
                landlordImprovementsQuery: 'yes',
                damagesQuery: 'yes',

                tenants: [],
                landlords: []
            }

            let formDataForPost = rentAdjudicationForm.prepareFormDataForPost(formData);
            expect(formDataForPost.sharedAreas).toEqual('angel');
            expect(formDataForPost.included).toEqual('bee sting');
            expect(formDataForPost.servicesDetails).toEqual('carrot');
            expect(formDataForPost.servicesCostDetails).toEqual('devil\'s food');
            expect(formDataForPost.furnished).toEqual('eccles');
            expect(formDataForPost.improvementsTenant).toEqual('fruit');
            expect(formDataForPost.improvementsLandlord).toEqual('genoa');
            expect(formDataForPost.damage).toEqual('hummingbird');

            formData.sharedAreasQuery = 'no';
            formData.includedQuery = 'no';
            formData.servicesQuery = 'no';
            formData.furnished = 'no';
            formData.tenantImprovementsQuery = 'no';
            formData.landlordImprovementsQuery = 'no';
            formData.damagesQuery = 'no';

            formDataForPost = rentAdjudicationForm.prepareFormDataForPost(formData);
            expect(formDataForPost.sharedAreas).toEqual(null);
            expect(formDataForPost.included).toEqual(null);
            expect(formDataForPost.servicesDetails).toEqual(null);
            expect(formDataForPost.servicesCostDetails).toEqual(null);
            expect(formDataForPost.furnished).toEqual(null);
            expect(formDataForPost.improvementsTenant).toEqual(null);
            expect(formDataForPost.improvementsLandlord).toEqual(null);
            expect(formDataForPost.damage).toEqual(null);
        });
    });
});
