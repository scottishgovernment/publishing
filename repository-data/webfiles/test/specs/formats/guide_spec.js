/* global describe beforeEach it expect spyOn $ */

'use strict';

jasmine.getFixtures().fixturesPath = 'base/test/fixtures';

import guide from '../../../src/main/scripts/formats/guide';
import feedback from '../../../src/main/scripts/components/feedback';

describe('Guide page', function() {
    it('should be defined', function() {
        expect(guide).toBeDefined();
    });

    it('init should be defined', function() {
        expect(guide.init).toBeDefined();
    });
});
