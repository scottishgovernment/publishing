/* global jasmine, describe, expect, it, loadFixtures, spyOn, grecaptcha */

'use strict';

jasmine.getFixtures().fixturesPath = 'base/test/fixtures';
jasmine.DEFAULT_TIMEOUT_INTERVAL = 6000;

import disclosurePvg from '../../../../src/main/scripts/components/mygov/disclosure-pvg-form';
import $ from '../../../../src/main/scripts/vendor/jquery/dist/jquery.min';

describe('disclosurePvg form', function () {
    beforeEach(function() {
        // first load your fixtures
        window.body = '';
        loadFixtures('disclosure-pvg-form.html');
    });

    xit ('should flag an incomplete CAPTCHA as invalid', function () {
        var $disclosurePvgForm = $('#disclosure-pvg-form');

        spyOn(grecaptcha, 'getResponse').and.callFake(function () {
            return '';
        });

        $disclosurePvgForm.trigger('submit');

        expect ($('#recaptcha').closest('.form-group').hasClass('js-has-error')).toBeTruthy();
    });

    xit ('should remove any extant CAPTCHA error notification if the CAPTCHA is valid', function () {
        var $disclosurePvgForm = $('#disclosure-pvg-form');

        spyOn(grecaptcha, 'getResponse').and.callFake(function () {
            return '123';
        });

        $disclosurePvgForm.trigger('submit');

        expect ($('#recaptcha').closest('.form-group').hasClass('js-has-error')).toBeFalsy();
    });
});
