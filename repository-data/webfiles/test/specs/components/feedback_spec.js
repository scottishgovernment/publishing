/* global describe, beforeEach, it, expect, spyOn, loadFixtures, fdescribe, fit, xdescribe, xit, $ */

'use strict';

jasmine.getFixtures().fixturesPath = 'base/test/fixtures';

import feedback from '../../../src/main/scripts/components/feedback';

feedback.init();

describe("Feedback object", function() {

    beforeEach(function() {
        loadFixtures('feedback.html');
    });

    it("should be defined", function() {
        expect(feedback).toBeDefined();
    });

    it("should be defined", function() {
        expect(feedback.init).toBeDefined();
    });

    xit("rejects invalid type", function() {
        spyOn(window, 'alert');
        feedback.init();
        spyOn($, 'ajax').and.returnValue({
            then: function(cb, fail) {
                fail({
                    status: 201
                });
            }
        });
        //Click No
        $('#feedbackForm').trigger('submit');
        expect(window.alert).toHaveBeenCalledWith('Please complete all the fields');
    });

    it("accepts 'no' when valid", function() {
        feedback.init();

        //Select No
        document.querySelector('#needsmetno').checked = true;

        //Set the values
        $("#comments-no").val('testing');
        document.querySelector('#reason-no option:nth-child(2)').selected = true;

        const event = new Event('submit');
        document.querySelector('#feedbackForm').dispatchEvent(event);

        expect(document.querySelectorAll('#feedbackErrorSummary .ds_error-summary__list li').length).toEqual(0);
    });

    it("rejects 'no' when not valid", function() {
        feedback.init();

        //Select No
        document.querySelector('#needsmetno').checked = true;

        const event = new Event('submit');
        document.querySelector('#feedbackForm').dispatchEvent(event);

        expect(document.querySelectorAll('#feedbackErrorSummary .ds_error-summary__list li').length).toEqual(2);
    });

    it("accepts 'yes, but' when valid", function() {
        feedback.init();

        //Select No
        document.querySelector('#needsmetyesbut').checked = true;

        //Set the values
        $("#comments-yesbut").val('testing');
        document.querySelector('#reason-yesbut option:nth-child(2)').selected = true;

        const event = new Event('submit');
        document.querySelector('#feedbackForm').dispatchEvent(event);

        expect(document.querySelectorAll('#feedbackErrorSummary .ds_error-summary__list li').length).toEqual(0);
    });

    it("rejects 'yes, but' when not valid", function() {
        feedback.init();

        //Select YesBut
        document.querySelector('#needsmetyesbut').checked = true;

        const event = new Event('submit');
        document.querySelector('#feedbackForm').dispatchEvent(event);

        expect(document.querySelectorAll('#feedbackErrorSummary .ds_error-summary__list li').length).toEqual(1);
    });

    it("can trigger a 'yes, but' feedback request", function() {
        spyOn($, 'ajax').and.returnValue({
            then: function(cb) {
                cb();
            }
        });
        feedback.init();
        //Select Yes
        document.querySelector('#needsmetyes').checked = true;

        const event = new Event('submit');
        document.querySelector('#feedbackForm').dispatchEvent(event);

        expect(document.querySelectorAll('#feedbackErrorSummary .ds_error-summary__list li').length).toEqual(0);
    });

    xit("handles bad responses from the server", function() {
        spyOn($, 'ajax').and.returnValue({
            then: function(cb, fail) {
                fail({
                    status: 400
                });
            }
        });
        feedback.init();

        //Select Yes
        document.querySelector('#needsmetyes').checked = true;

        const event = new Event('submit');
        document.querySelector('#feedbackForm').dispatchEvent(event);

        expect($('#feedbackForm .submit').hasClass('form-error')).toBeTruthy();
    });

    xit("handles good responses from the server", function() {
        spyOn($, 'ajax').and.returnValue({
            then: function(cb, fail) {
                fail({
                    status: 201
                });
            }
        });
        feedback.init();
        //Click Yes
        $('input:radio:eq(0)').trigger('click');
        $('#feedbackForm').trigger('submit');
        expect($('#feedbackForm').find('.form-error').length).toEqual(0);
    });

});
