/* global describe beforeEach it expect spyOn $ */

'use strict';

jasmine.getFixtures().fixturesPath = 'base/test/fixtures';

import feedback from '../../../src/main/scripts/components/feedback';

feedback.init();

xdescribe("Feedback object", function() {

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
        // spyOn($, 'ajax').and.returnValue({
        //     then: function(cb, fail) {
        //         fail({
        //             status: 201
        //         });
        //     }
        // });
        feedback.init();
        //Click No
        $('input:radio:eq(1)').trigger('click');
        //Set the values
        $("#comments").val('testing');
        var sel = $("select.reason.no");
        $("option:eq(1)", sel).attr("selected", "true");

        $('#feedbackForm').trigger('submit');
        expect($('#feedbackForm').find('.form-error').length).toEqual(0);
    });

    it("rejects 'no' when not valid", function() {
        feedback.init();
        //Click No
        $('input:radio:eq(1)').trigger('click');
        $('#feedbackForm').trigger('submit');
        expect($('.form-error').length).toEqual(2);
    });

    it("accepts 'yes, but' when valid", function() {
        spyOn($, 'ajax').and.returnValue({
            then: function(cb, fail) {
                fail({
                    status: 201
                });
            }
        });
        feedback.init();
        //Click No
        $('input:radio:eq(2)').trigger('click');
        //Set the values
        $("#comments").val('testing');

        $('#feedbackForm').trigger('submit');
        expect($('#feedbackForm').find('.form-error').length).toEqual(0);
    });

    it("rejects 'yes, but' when not valid", function() {
        feedback.init();

        //Click YesBut
        $('input:radio:eq(2)').trigger('click');
        $('#feedbackForm').trigger('submit');

        expect($('.form-error').length).toEqual(1);
    });

    it("can trigger a 'yes, but' feedback request", function() {
        spyOn($, 'ajax').and.returnValue({
            then: function(cb) {
                cb();
            }
        });
        feedback.init();
        //Click Yes
        $('input:radio:eq(0)').trigger('click');
        $('#feedbackForm').trigger('submit');
        expect($('#feedbackForm').find('.form-error').length).toEqual(0);
    });

    it("handles bad responses from the server", function() {
        spyOn($, 'ajax').and.returnValue({
            then: function(cb, fail) {
                fail({
                    status: 400
                });
            }
        });
        feedback.init();
        //Click Yes
        $('input:radio:eq(0)').trigger('click');
        $('#feedbackForm').trigger('submit');
        expect($('#feedbackForm .submit').hasClass('form-error')).toBeTruthy();
    });

    it("handles good responses from the server", function() {
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
