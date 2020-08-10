/* global describe beforeEach it expect spyOn $ */

'use strict';

jasmine.getFixtures().fixturesPath = 'base/test/fixtures';

import PostcodeLookup from '../../../../src/main/scripts/components/postcode-lookup';
import rpzChecker from '../../../../src/main/scripts/formats/mygov/rpz-checker';
import feedback from '../../../../src/main/scripts/components/feedback';

describe("rpzChecker format", function() {
    it ('should init feedback on init', function () {
        spyOn(feedback, 'init');

        rpzChecker.init();

        expect(feedback.init).toHaveBeenCalled();
    });
});
