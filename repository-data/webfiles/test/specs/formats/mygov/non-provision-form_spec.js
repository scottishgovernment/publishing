/* global describe beforeEach it expect spyOn $ */

'use strict';

jasmine.getFixtures().fixturesPath = 'base/test/fixtures';

import nonProvisionForm from '../../../../src/main/scripts/formats/mygov/non-provision-form';

describe("nonProvisionForm format", function() {
    it ('should have an init function', function () {
        expect(nonProvisionForm.init).toBeDefined();
    });
});
