/* global describe beforeEach it expect spyOn $ */

'use strict';

jasmine.getFixtures().fixturesPath = 'base/test/fixtures';
jasmine.DEFAULT_TIMEOUT_INTERVAL = 6000;

import accordion from '../../../src/main/scripts/formats/accordion.js';

describe("Accordion page", function() {
    it("should be defined", function() {
        expect(accordion).toBeDefined();
    });

    it("init should be defined", function() {
        expect(accordion.init).toBeDefined();
    });
});
