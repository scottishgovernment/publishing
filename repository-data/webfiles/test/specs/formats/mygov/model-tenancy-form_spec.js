/* global describe beforeEach it expect spyOn $ */

'use strict';

jasmine.getFixtures().fixturesPath = 'base/test/fixtures';

import modelTenancyForm from '../../../../src/main/scripts/formats/mygov/model-tenancy-form';

describe("Model tenancy form", function () {

    beforeEach(function() {
        // load your fixtures
        loadFixtures('housing-form.html');
    });

    it("should be defined", function() {
        expect(modelTenancyForm).toBeDefined();
    });

    it("should init", function() {
        modelTenancyForm.init();
    });
});
