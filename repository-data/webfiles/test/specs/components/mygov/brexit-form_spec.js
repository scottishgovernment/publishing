/* global jasmine, describe, expect, it, loadFixtures, spyOn, grecaptcha beforeEach */

'use strict';

jasmine.getFixtures().fixturesPath = 'base/test/fixtures';
jasmine.DEFAULT_TIMEOUT_INTERVAL = 6000;

import brexitForm from '../../../../src/main/scripts/components/mygov/brexit-form';
import $ from '../../../../src/main/scripts/vendor/jquery/dist/jquery.min';

xdescribe('brexit form', function () {
    beforeEach(function() {
        // first load your fixtures
        window.body = '';
        loadFixtures('brexit-form.html');
    });

    it ('should provide a live count of remaining characters on the company name field', function () {
        var $company = $('#brexit-company-name');

        $company.val('foo');
        $company.trigger('blur');

        expect ($company.siblings('.js-count').text()).toEqual('297');

        $company.val('foobar');
        $company.trigger('textchange');

        expect ($company.siblings('.js-count').text()).toEqual('294');
    });

    it ('should provide a live count of remaining characters on the your name field', function () {
        var $name = $('#brexit-name');

        $name.val('foo');
        $name.trigger('blur');

        expect ($name.siblings('.js-count').text()).toEqual('197');

        $name.val('foobar');
        $name.trigger('textchange');

        expect ($name.siblings('.js-count').text()).toEqual('194');
    });

    it ('should provide a live count of remaining characters on the email address field', function () {
        var $email = $('#brexit-email');

        $email.val('foo');
        $email.trigger('blur');

        expect ($email.siblings('.js-count').text()).toEqual('147');

        $email.val('foobar');
        $email.trigger('textchange');

        expect ($email.siblings('.js-count').text()).toEqual('144');
    });

    it ('should provide a live count of remaining characters on the telephone field', function () {
        var $telephone = $('#brexit-telephone');

        $telephone.val('foo');
        $telephone.trigger('blur');

        expect ($telephone.siblings('.js-count').text()).toEqual('17');

        $telephone.val('foobar');
        $telephone.trigger('textchange');

        expect ($telephone.siblings('.js-count').text()).toEqual('14');
    });

    it ('should provide a live count of remaining characters on the enquiry field', function () {
        var $enquiry = $('#brexit-enquiry');

        $enquiry.val('foo');
        $enquiry.trigger('blur');

        expect ($enquiry.siblings('.js-count').text()).toEqual('1997');

        $enquiry.val('foobar');
        $enquiry.trigger('textchange');

        expect ($enquiry.siblings('.js-count').text()).toEqual('1994');
    });

    it ('should flag empty fields as invalid', function () {
        var $companyName = $('#brexit-company-name');
        var $enquiry = $('#brexit-enquiry');

        $companyName.val('');
        $companyName.trigger('blur');

        expect ($companyName.closest('.form-group').hasClass('js-has-error')).toBeTruthy();
    });

    it ('should flag too-long fields as invalid', function () {
        var $companyName = $('#brexit-company-name');
        var $enquiry = $('#brexit-enquiry');

        $companyName.val('0123456789012345678901234567890123456789012345678' +
            '90123456789012345678901234567890123456789012345678901234567890' +
            '12345678901234567890123456789012345678901234567890123456789012' +
            '34567890123456789012345678901234567890123456789012345678901234' +
            '56789012345678901234567890123456789012345678901234567890123456' +
            '78901234567890123456789012345678901234567890123456789012345678' +
            '90123456789012345678901234567890123456789');
        $companyName.trigger('blur');

        expect ($companyName.closest('.form-group').hasClass('js-has-error')).toBeTruthy();
    });

    xit ('should flag an incomplete CAPTCHA as invalid', function () {
        var $brexitForm = $('#brexit-form');

        spyOn(grecaptcha, 'getResponse').and.callFake(function () {
            return '';
        });

        $brexitForm.trigger('submit');

        expect ($('#recaptcha').closest('.form-group').hasClass('js-has-error')).toBeTruthy();
    });

    xit ('should remove any extant CAPTCHA error notification if hte CAPTCHA is valid', function () {
        var $brexitForm = $('#brexit-form');

        spyOn(grecaptcha, 'getResponse').and.callFake(function () {
            return '123';
        });

        $brexitForm.trigger('submit');

        expect ($('#recaptcha').closest('.form-group').hasClass('js-has-error')).toBeFalsy();
    });

    xit ('should submit the form if all fields are valid', function () {
        var $companyName = $('#brexit-company-name');
        var $enquiry = $('#brexit-enquiry');
        var $brexitForm = $('#brexit-form');

        spyOn(grecaptcha, 'getResponse').and.callFake(function () {
            return '123';
        });

        $companyName.val('My company');
        $enquiry.val('My question');

        spyOn($, 'ajax').and.callFake(function (options) {
            options.success();
        });

        $brexitForm.trigger('submit');

        expect($.ajax).toHaveBeenCalled();
    });
});
