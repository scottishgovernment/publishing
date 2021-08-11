/* global describe beforeEach it expect spyOn $ */

'use strict';

jasmine.getFixtures().fixturesPath = 'base/test/fixtures';

import home from '../../../src/main/scripts/formats/home';
import feedback from '../../../src/main/scripts/components/feedback';

describe("homepage format", function() {
    it ('should init feedback on init', function () {
        spyOn(feedback, 'init');

        home.init();

        expect(feedback.init).toHaveBeenCalled();
    });
});
