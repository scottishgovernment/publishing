/* global describe beforeEach it expect spyOn $ */

'use strict';

import gup from '../../../src/main/scripts/tools/gup';

describe('Gup', function() {
    it('can gupify text content', function() {
        var $window = {
            location: {
                href: "http://test?name=mark&age=45"
            }
        }
        var result = gup('name', $window);
        expect(result).toBe('mark');
    });
    it('can gupify numeric content', function() {
        var $window = {
            location: {
                href: "http://test?name=mark&age=45"
            }
        }
        var result = gup('age', $window);
        expect(result).toBe('45');
    });
    it('can gupify missing parameters', function() {
        var $window = {
            location: {
                href: "http://test?name=mark&age=45"
            }
        }
        var result = gup('address', $window);
        expect(result).toBeNull();
    });
});
