/* global describe beforeEach it expect spyOn $ */

'use strict';

jasmine.getFixtures().fixturesPath = 'base/test/fixtures';

import signpost from '../../../src/main/scripts/formats/signpost';

describe("signpost format", function() {
    it ('should have an init function', function () {
        expect(signpost.init).toBeDefined();
    });
});
