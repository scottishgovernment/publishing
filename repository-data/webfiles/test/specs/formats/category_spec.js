/* global describe beforeEach it expect spyOn $ */

'use strict';

jasmine.getFixtures().fixturesPath = 'base/test/fixtures';

import category from '../../../src/main/scripts/formats/category';
import feedback from '../../../src/main/scripts/components/feedback';

describe("category format", function() {
    it ('should init feedback on init', function () {
        spyOn(feedback, 'init');

        category.init();

        expect(feedback.init).toHaveBeenCalled();
    });
});
