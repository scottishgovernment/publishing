/* global describe beforeEach it expect spyOn $ */

'use strict';

jasmine.getFixtures().fixturesPath = 'base/test/fixtures';

import rpzChecker from '../../../../src/main/scripts/formats/mygov/rent-pressure-zone-checker';
import feedback from '../../../../src/main/scripts/components/feedback';

describe("rpzChecker format", function() {
    it ('should init feedback on init', function () {
        spyOn(feedback, 'init');

        rpzChecker.init();

        expect(feedback.init).toHaveBeenCalled();
    });
});
