/* global describe beforeEach it expect spyOn $ */

'use strict';

jasmine.getFixtures().fixturesPath = 'base/test/fixtures';

import status from '../../../src/main/scripts/formats/status';

describe("status format", function() {
    it ('should have an init function', function () {
        expect(status.init).toBeDefined();
    });
});
