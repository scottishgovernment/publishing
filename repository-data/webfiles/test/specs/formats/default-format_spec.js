/* global describe beforeEach it expect spyOn $ */

'use strict';

jasmine.getFixtures().fixturesPath = 'base/test/fixtures';

import defaultFormat from '../../../src/main/scripts/formats/default-format';

describe("default format", function() {
    it ('should have an init function', function () {
        expect(defaultFormat.init).toBeDefined();
    });
});
