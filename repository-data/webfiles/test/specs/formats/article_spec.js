/* global describe beforeEach it expect spyOn $ */

'use strict';

jasmine.getFixtures().fixturesPath = 'base/test/fixtures';

import article from '../../../src/main/scripts/formats/article';
import feedback from '../../../src/main/scripts/components/feedback';

describe("Article format", function() {
    it ('should init feedback on init', function () {
        spyOn(feedback, 'init');

        article.init();

        expect(feedback.init).toHaveBeenCalled();
    });
});
