/* global describe beforeEach it expect spyOn $ */

'use strict';

jasmine.getFixtures().fixturesPath = 'base/test/fixtures';

import siteItem from '../../../src/main/scripts/formats/site-item';

describe("siteItem format", function() {
    it ('should have an init function', function () {
        expect(siteItem.init).toBeDefined();
    });
});
