/* global describe beforeEach it expect spyOn $ */

'use strict';

jasmine.getFixtures().fixturesPath = 'base/test/fixtures';

import orghub from '../../../src/main/scripts/formats/organisation';
import feedback from '../../../src/main/scripts/components/feedback';

describe("Corp org hub format", function() {
    it ('should init feedback on init', function () {
        spyOn(feedback, 'init');

        orghub.init();

        expect(feedback.init).toHaveBeenCalled();
    });
});
