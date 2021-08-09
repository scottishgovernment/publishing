/* global describe beforeEach it expect spyOn $ */

'use strict';

jasmine.getFixtures().fixturesPath = 'base/test/fixtures';

import guide from '../../../src/main/scripts/formats/guide';
import feedback from '../../../src/main/scripts/components/feedback';

describe("guide format", function() {
    it ('should init feedback on init', function () {
        spyOn(feedback, 'init');

        guide.init();

        expect(feedback.init).toHaveBeenCalled();
    });
});
