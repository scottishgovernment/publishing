/* global describe beforeEach it expect spyOn $ */

'use strict';

jasmine.getFixtures().fixturesPath = 'base/test/fixtures';

import index from '../../../src/main/scripts/formats/home';

describe("index format", function() {
    it ('should have an init function', function () {
        expect(index.init).toBeDefined();
    });
});
