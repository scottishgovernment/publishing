/* global describe beforeEach it expect spyOn $ */

'use strict';

jasmine.getFixtures().fixturesPath = 'base/test/fixtures';
jasmine.DEFAULT_TIMEOUT_INTERVAL = 6000;

const multiPageFormSpecSections = [
    {
        group: {
            slug: 'part-1',
            title: 'The details',
        },
        slug: 'property',
        title: 'Property',
        pages: [
            {
                slug: 'property-details',
                title: 'Property details'
            },
            {
                slug: 'property-furnishings',
                title: 'Furnishings'
            },
            {
                slug: 'property-hmo',
                title: 'HMO'
            }
        ]
    },
    {
        group: {
            slug: 'part-2',
            title: 'The tenancy'
        },
        slug: 'tenancy',
        title: 'Tenancy',
        pages: [
            {
                slug: 'tenancy-start',
                title: 'Tenancy start date'
            },
            {
                slug: 'tenancy-payment',
                title: 'Payment details'
            }
        ]
    }
];

const multiPageFormSpecFormObject = {
    hasLettingAgent: 'no',
    tenantAgent: {
        address: {
            building: null
        }
    },
    currentRentFrequency: null,
    reasons: [],
    section10bFailure: false
}

const multiPageFormSpecFormMapping = {
    // radio
    'hasLettingAgent': '[name="letting-agent-query"]',
    // checkbox
    'section10bFailure': '#missing-new-terms',
    // checkbox group
    'reasons': '[data-group="reasons"]',
    // text
    'tenantAgent.address.building': '#tenant-agent-address-building',
    // select
    'currentRentFrequency': '#current-payment-frequency',
}

import MultiPageForm from '../../../src/main/scripts/components/multi-page-form';
import $ from 'jquery';
window.$ = $;

xdescribe('Multi-page form', function() {

    it('should be defined', function() {
        expect(MultiPageForm).toBeDefined();
    });

    it('should init', function(){
        var testForm = new MultiPageForm({
            formSections: JSON.parse(JSON.stringify(multiPageFormSpecSections))
        });
        testForm.init();
    });

    it('should have initialised routing', function () {
        var numberOfRoutes = Object.keys(window.$.routes.list).length;
        var pageRoute = window.$.routes.find('page');

        expect(numberOfRoutes).toEqual(5);
        expect(pageRoute.route).toEqual('!/{section:slug}/{page:slug}/');
    });


    describe('Multi-page form getStep function', function(){

        var testForm;

        beforeEach(function(){
            testForm = new MultiPageForm({
                formSections: JSON.parse(JSON.stringify(multiPageFormSpecSections))
            });
            testForm.init();
        });


        it('should be able to search using the "current" property', function(){
            var returnedStep = testForm.getStep('current', true);

            expect(returnedStep.slug).toEqual('property-details');
        });

        it('should be able to search using the "slug" property', function(){
            var returnedStep = testForm.getStep('slug', 'property-hmo');

            expect(returnedStep.slug).toEqual('property-hmo');
        });

        it('should be able to search using nested properties', function(){
            var returnedStep = testForm.getStep('group.slug', 'part-2');

            expect(returnedStep.slug).toEqual('tenancy-start');
        });

    });

    describe('Multi-page form goToStep function', function(){

        var testForm;
        var testFormSections;

        beforeEach(function(){
            window.body = '';
            loadFixtures('multi-page-form.html');

            testForm = new MultiPageForm({
                formSections: JSON.parse(JSON.stringify(multiPageFormSpecSections))
            });
            testForm.init();
            testFormSections = testForm.settings.formSections;

            var targetStep = testForm.getRelativeStep(2);
            testForm.goToStep(targetStep);
        });

        it('should remove current label from original step', function(){
            expect(testFormSections[0].pages[0].current).toEqual(undefined);
        });

        it('should set new step as currentStep', function(done){
            var newCurrentStep = testForm.getCurrentStep();

            window.setTimeout(function () {
                expect(newCurrentStep.slug).toEqual('property-hmo');
                done();
            }, 10);
        });

        it('should set visited on new step to true', function(){
            var newCurrentStep = testForm.getCurrentStep();

            expect(newCurrentStep.visited).toEqual(true);
        });

        it('should hide original step from view', function(){
            var originalStep = $('[data-step="property-details"]');

            expect($(originalStep).hasClass('fully-hidden')).toBeTruthy();
        });

        it('should remove hidden class from new step', function(){
            var newStep = $('[data-step="property-hmo"]');

            expect($(newStep).hasClass('fully-hidden')).toBeFalsy();
        });
    });

    describe('Multi-page form navigation view changes', function(){

        var testForm;
        var testFormSections;

        beforeEach(function(){
            window.body = '';
            loadFixtures('multi-page-form.html');

            testForm = new MultiPageForm({
                formSections: JSON.parse(JSON.stringify(multiPageFormSpecSections))
            });
            testForm.init();
            testFormSections = testForm.settings.formSections;

            // change the current section
            delete testFormSections[0].pages[0].current;
            delete testFormSections[0].current;
            testFormSections[1].pages[0].current = true;
            testFormSections[1].current = true;
            testForm.currentStep = testFormSections[1].pages[0];
        });

        it('should update primary section nav', function(){
            testForm.updateFormNav();

            var currentPrimarySection = $('#section-progess-indicator').find('.section-nav__item--current')[0].text;

            expect(currentPrimarySection).toEqual('Tenancy');
        });

        it('should update subsection nav', function(){
            testForm.updateFormNav();
            var currentSubsection = $('#subsection-progess-indicator').find('.ds_side-navigation__link.ds_current')[0].innerText;

            expect(currentSubsection).toEqual('Tenancy start date');
        });

        it('should update page nav', function(){
            testForm.updatePageNav();

            expect($('#button-next').attr('href')).toEqual('#!/tenancy/tenancy-payment/');
        });

        it('should update heading label with current step details', function(){
            testForm.updatePageLabelWithCurrentStep();

            expect($('.form-step:not(.fully-hidden) > h2').attr('data-suffix')).toEqual('(1 of 2)');
        });

    });

    describe('Multi-page form navigation helpers', function(){

        var testForm;

        beforeEach(function(){
            testForm = new MultiPageForm({
                formSections: JSON.parse(JSON.stringify(multiPageFormSpecSections))
            });
            testForm.init();
        });

        it('should get current step as first step when first loaded', function(){
            var currentStep = testForm.getCurrentStep();

            expect(currentStep.slug).toEqual('property-details');
        });

        it('should return flattened sections', function(){
            var flattenedSections = testForm.flattenedSections(true);

            expect(flattenedSections.length).toEqual(5);
            expect(flattenedSections[0].section).toEqual('property');
        });

        it('should get next step', function(){
            var nextStep = testForm.getNextStep();

            expect(nextStep.slug).toEqual('property-furnishings');
        });

        it('should get previous step', function(){
            var nextStep = testForm.getNextStep();
            testForm.goToStep(nextStep);

            var prevStep = testForm.getPrevStep();

            expect(prevStep.slug).toEqual('property-details');
        });

        it('should get relative step with a positive number', function(){
            var relativeStep = testForm.getRelativeStep(2);

            expect(relativeStep.slug).toEqual('property-hmo');
        });

        it('should get relative step with a negative number', function(){
            var nextStep = testForm.getNextStep();
            testForm.goToStep(nextStep);

            var relativeStep = testForm.getRelativeStep(-1);

            expect(relativeStep.slug).toEqual('property-details');
        });

        it('should handle requests to get steps before first step', function(){
            var relativeStep = testForm.getRelativeStep(-1);

            expect(relativeStep).toBeFalsy();
        });

        it('should handle requests to get steps after last step', function(){
            var relativeStep = testForm.getRelativeStep(10);

            expect(relativeStep).toBeFalsy();
        });

    });

    describe('Multi-page form changes to steps', function(){

        var testForm;
        var testFormSections;

        beforeEach(function(){
            testForm = new MultiPageForm({
                formSections: JSON.parse(JSON.stringify(multiPageFormSpecSections))
            });
            testForm.init();
            testFormSections = testForm.settings.formSections;
        });

        it('should disable a specified step', function(){
            testForm.disableStep('property-hmo');

            expect(testFormSections[0].pages[2].disabled).toEqual(true);
        });

        it('should enable a specified step', function(){
            testForm.disableStep('property-hmo');
            testForm.enableStep('property-hmo');

            expect(testFormSections[0].pages[2].disabled).toEqual(false);
        });

        it('should add a step', function(){
            testForm.addStep('tenancy', { slug: 'tenancy-added', title: 'New Section'});

            expect(testFormSections[1].pages.length).toEqual(3);
            expect(testFormSections[1].pages[2].slug).toEqual('tenancy-added');
        });

        it('should remove a step by slug', function(){
            testForm.removeStep('tenancy-start');

            expect(testFormSections[1].pages.length).toEqual(1);
            expect(testFormSections[1].pages[0].slug).toEqual('tenancy-payment');
        });

        it('should rename a step', function(){
            testForm.renameStep('property-details', 'Details of the Property');

            expect(testFormSections[0].pages[0].title).toEqual('Details of the Property');
        });
    });

    describe('Multi-page form field mapping', function () {
        describe('field mapping', function () {
            let testForm;

            beforeEach(function () {
                testForm = new MultiPageForm({
                    formSections: multiPageFormSpecSections,
                    formMapping: multiPageFormSpecFormMapping,
                    formObject: multiPageFormSpecFormObject,
                    modifiers: []
                });

                window.body = '';
                loadFixtures('form-fields.html');
            });

            xit ('should apply preset values if present', function () {
                testForm.configureFieldMapping();

                expect(testForm.settings.formObject.hasLettingAgent).toEqual('no');
            })
        });

        describe('data binding to field types', function () {
            let testForm;

            beforeEach(function () {
                testForm = new MultiPageForm({
                    formSections: multiPageFormSpecSections,
                    formMapping: multiPageFormSpecFormMapping,
                    formObject: multiPageFormSpecFormObject,
                    modifiers: []
                });

                window.body = '';
                loadFixtures('form-fields.html');
            });

            it ('should bind radio button changes to the form object', function () {
                testForm.mapField('hasLettingAgent', '[name="letting-agent-query"]');

                let field = $('[name="letting-agent-query"]:first');
                field.prop('checked', true);
                field.trigger('change');

                expect(testForm.settings.formObject.hasLettingAgent).toEqual('yes');


            });

            it ('should bind checkbox group changes to the form object', function () {
                testForm.mapField('reasons', '[data-group="reasons"]');

                let fields = $('[data-group="reasons"]');
                $(fields[0]).prop('checked', true);
                $(fields[0]).trigger('change');

                $(fields[3]).prop('checked', true);
                $(fields[3]).trigger('change');

                expect(testForm.settings.formObject.reasons.length).toEqual(2);
                expect(testForm.settings.formObject.reasons[0]).toEqual({id:'LANDLORD_TO_SELL', name: 'You want to sell the property you\'re renting out (6 months)'});

            });

            it ('should bind checkbox changes to the form object', function () {
                testForm.mapField('section10bFailure', '#missing-new-terms');

                let field = $('#missing-new-terms');
                field.prop('checked', true);
                field.trigger('change');

                expect(testForm.settings.formObject.section10bFailure).toEqual(true);

            });

            it ('should bind text input changes to the form object', function () {
                testForm.mapField('tenantAgent.address.building', '#tenant-agent-address-building');

                let field = $('#tenant-agent-address-building');
                field.val('a building');
                field.trigger('change');

                expect(testForm.settings.formObject.tenantAgent.address.building).toEqual('a building');

            });

            it ('should bind select changes to the form object', function () {
                testForm.mapField('currentRentFrequency', '#current-payment-frequency');

                let field = $('#current-payment-frequency');
                $($(field).children()[3]).prop('selected', true);
                field.trigger('change');

                expect(testForm.settings.formObject.currentRentFrequency).toEqual('EVERY_FOUR_WEEKS');

            });
        });
    });
});
