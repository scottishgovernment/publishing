/* global describe beforeEach it expect spyOn $ */

'use strict';

jasmine.getFixtures().fixturesPath = 'base/test/fixtures';

import global from '../../src/main/scripts/global';
import storage from '../../src/main/scripts/tools/storage';

global.storage = storage;

describe('global:', function() {
    it('should set up the cookie notice on init()', function() {
        spyOn(global, 'initCookieNotice');

        global.init();

        expect(global.initCookieNotice).toHaveBeenCalled();
    });

    describe('initCookieNotice()', function() {
        beforeEach(function () {
            loadFixtures('cookie-notice.html');
        });

        it('should check whether the cookie notice has been acknowledged', function() {
            spyOn(global.storage, 'get');

            global.initCookieNotice();

            expect(global.storage.get).toHaveBeenCalledWith({ type: storage.types.cookie, name: 'cookie-notification-acknowledged' });
        });

        it('should keep the cookie notice hidden if the cookie acknowledgement cookie has been set', function() {
            document.cookie = 'cookie-notification-acknowledged=yes';

            global.initCookieNotice();

            expect($('#cookie-notice').hasClass('fully-hidden')).toBeTruthy();
        });
    });
});
