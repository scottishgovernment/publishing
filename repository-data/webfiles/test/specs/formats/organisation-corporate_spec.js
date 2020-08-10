/* global describe beforeEach it expect spyOn $ */

'use strict';

jasmine.getFixtures().fixturesPath = 'base/test/fixtures';

import corporateOrgHub from '../../../src/main/scripts/formats/organisation-corporate';
import feedback from '../../../src/main/scripts/components/feedback';

describe("Corp org hub format", function() {
    it ('should init feedback on init', function () {
        spyOn(feedback, 'init');

        corporateOrgHub.init();

        expect(feedback.init).toHaveBeenCalled();
    });
});
