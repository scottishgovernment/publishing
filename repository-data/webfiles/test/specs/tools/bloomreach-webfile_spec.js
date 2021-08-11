/* global describe beforeEach it expect spyOn $ */

'use strict';

jasmine.getFixtures().fixturesPath = 'base/test/fixtures';

import bloomreachWebfile from '../../../src/main/scripts/tools/bloomreach-webfile';

describe('bloomreach webfile', function () {
    const container = document.createElement('div');
    container.id = 'container';
    document.body.appendChild(container);

    afterEach(() => {
        container.innerHTML = '';
    });

    it('should add a webfiles URL part to a provided path', () => {
        const input = document.createElement('input');
        input.id = 'br-webfile-path';
        input.value = '/a/webfiles/path/';
        document.getElementById('container').appendChild(input);

        expect(bloomreachWebfile('my-file.svg')).toEqual('/a/webfiles/path/my-file.svg');
    });

    it('should not modify the path if no webfiles path is found', () => {
        expect(bloomreachWebfile('my-file.svg')).toEqual('my-file.svg');
    });
});
