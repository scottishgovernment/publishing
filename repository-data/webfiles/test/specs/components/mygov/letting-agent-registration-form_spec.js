/* global jasmine, describe, expect, it, loadFixtures, spyOn, grecaptcha */

'use strict';

jasmine.getFixtures().fixturesPath = 'base/test/fixtures';
jasmine.DEFAULT_TIMEOUT_INTERVAL = 6000;

import lettingAgentRegistrationForm from '../../../../src/main/scripts/components/mygov/letting-agent-registration-form';

describe('Letting agent registration form', function () {
    beforeEach(function() {
        // first load your fixtures
        window.body = '';
        loadFixtures('letting-agent-registration-form.html');
    });

    // For immediate validation and only works if grecaptcha global is working (ie not now)
    xit ('should flag empty fields as invalid', function () {
        var $lettingAgentRegistrationForm = $('#letting-agent-registration-form');
        var $companyName = $('#letting-agent-registration-company-name');

        $companyName.val('');
        // $companyName.trigger('blur'); // Required if validation is dyanmic
        $lettingAgentRegistrationForm.trigger('submit');

        expect ($companyName.closest('.form-group').hasClass('js-has-error')).toBeTruthy();
    });

    xit ('should flag an incomplete CAPTCHA as invalid', function () {
        var $lettingAgentRegistrationForm = $('#letting-agent-registration-form');

        spyOn(grecaptcha, 'getResponse').and.callFake(function () {
            return '';
        });

        $lettingAgentRegistrationForm.trigger('submit');

        expect ($('#recaptcha').closest('.form-group').hasClass('js-has-error')).toBeTruthy();
    });

    xit ('should remove any extant CAPTCHA error notification if the CAPTCHA is valid', function () {
        var $lettingAgentRegistrationForm = $('#letting-agent-registration-form');

        spyOn(grecaptcha, 'getResponse').and.callFake(function () {
            return '123';
        });

        $lettingAgentRegistrationForm.trigger('submit');

        expect ($('#recaptcha').closest('.form-group').hasClass('js-has-error')).toBeFalsy();
    });
});
