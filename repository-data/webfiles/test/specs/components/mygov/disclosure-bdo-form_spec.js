/* global jasmine, describe, expect, it, loadFixtures, spyOn, grecaptcha */

'use strict';

jasmine.getFixtures().fixturesPath = 'base/test/fixtures';
jasmine.DEFAULT_TIMEOUT_INTERVAL = 6000;

import disclosureBdo from '../../../../src/main/scripts/components/mygov/disclosure-bdo-form';
import $ from '../../../../src/main/scripts/vendor/jquery/dist/jquery.min';

describe('disclosureBdo form', function () {
    beforeEach(function() {
        // first load your fixtures
        window.body = '';
        loadFixtures('disclosure-bdo-form.html');
    });

    xit ('should flag an incomplete CAPTCHA as invalid', function () {
        var $disclosureBdoForm = $('#disclosure-bdo-form');

        spyOn(grecaptcha, 'getResponse').and.callFake(function () {
            return '';
        });

        $disclosureBdoForm.trigger('submit');

        expect ($('#recaptcha').closest('.form-group').hasClass('js-has-error')).toBeTruthy();
    });

    xit ('should remove any extant CAPTCHA error notification if the CAPTCHA is valid', function () {
        var $disclosureBdoForm = $('#disclosure-bdo-form');

        spyOn(grecaptcha, 'getResponse').and.callFake(function () {
            return '123';
        });

        $disclosureBdoForm.trigger('submit');

        expect ($('#recaptcha').closest('.form-group').hasClass('js-has-error')).toBeFalsy();
    });
});
