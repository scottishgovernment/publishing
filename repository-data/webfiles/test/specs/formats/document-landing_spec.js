/* global describe beforeEach it expect spyOn $ */

'use strict';

jasmine.getFixtures().fixturesPath = 'base/test/fixtures';

import documentLanding from '../../../src/main/scripts/formats/document-landing';

describe("document landing format", function() {
    it ('should have an init function', function () {
        expect(documentLanding.init).toBeDefined();
    });
});
