/* global describe beforeEach it expect spyOn $ */

'use strict';

jasmine.getFixtures().fixturesPath = 'base/test/fixtures';

import noticeToLeaveForm from '../../../../src/main/scripts/formats/mygov/notice-to-leave-form';

describe("noticeToLeaveForm format", function() {
    it ('should have an init function', function () {
        expect(noticeToLeaveForm.init).toBeDefined();
    });
});
