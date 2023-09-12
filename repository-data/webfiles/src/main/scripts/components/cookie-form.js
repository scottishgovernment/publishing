'use strict';

import storage from '../../../../node_modules/@scottish-government/pattern-library/src/base/tools/storage/storage';

const cookieForm = {

    init: function () {
        // on init, set the relevant permissions
        const cookiePermissionsString = storage.get({
            type: 'cookie',
            name: 'cookiePermissions'
        });

        // on init show the form
        const cookieFormBox = document.getElementById('cookie-form-box');
        const className = 'fully-hidden';
        if (cookieFormBox.classList) {
            cookieFormBox.classList.remove(className);
        } else {
            cookieFormBox.className = cookieFormBox.className.replace(new RegExp('(^|\\b)' + className.split(' ').join('|') + '(\\b|$)', 'gi'), ' ');
        }

        const inputGroups = document.querySelectorAll('#cookie-preferences .ds_field-group');
        let cookiePermissions = {};

        if (storage.isJsonString(cookiePermissionsString)) {
            cookiePermissions = JSON.parse(cookiePermissionsString);
        } else {
            cookiePermissions = {};
        }

        for(let i = 0, il = inputGroups.length; i < il; i++) {
            const inputGroup = inputGroups[i];

            const groupName = inputGroup.querySelector('input[type="radio"]').name;

            if (cookiePermissions[groupName.replace('cookie-', '')]) {
                inputGroup.querySelector('input[id$="-yes"]').setAttribute('checked', true);
            } else {
                inputGroup.querySelector('input[id$="-no"]').setAttribute('checked', true);
            }
        }

        document.getElementById('cookie-preferences').addEventListener('submit', function (event) {
            event.preventDefault();

            const inputs = document.querySelectorAll('input[name^="cookie"][value="true"]');

            for (let j = 0, jl = inputs.length; j < jl; j++) {
                const thisInput = inputs[j];

                cookiePermissions[thisInput.name.replace('cookie-', '')] = thisInput.checked;
            }

            storage.set({
                type: 'cookie',
                category: 'necessary',
                value: JSON.stringify(cookiePermissions),
                name: 'cookiePermissions',
                expiry: 365
            });

            storage.set({
                type: 'cookie',
                category: 'preferences',
                name: 'cookie-notification-acknowledged',
                value: 'yes',
                expires: 365
            });

            document.querySelector('#cookie-success-message').classList.remove('fully-hidden');
            document.querySelector('#cookie-success-message').style.top = '32px';

            // hide cookie notice
            const cookieNotice = document.querySelector('#cookie-notice');
            cookieNotice.classList.add('notification--confirmed');
            cookieNotice.parentNode.querySelector('.js-initial-cookie-content').classList.add('fully-hidden');
            cookieNotice.parentNode.querySelector('.js-confirm-cookie-content').classList.remove('fully-hidden');

            cookieForm.removeDisallowedCookies();

        });
    },

    removeDisallowedCookies: function() {
        // remove cookies
        // this is ugly
        const cookiesAsObject = document.cookie.split('; ').reduce((prev, current) => {
            const [name, ...value] = current.split('=');
            prev[name] = value.join('=');
            return prev;
        }, {});

        if (!storage.hasPermission(storage.categories.statistics)) {
            const statisticsCookiePrefixes = ['_ga', '_gid', '_ga_'];
            for (const cookie in cookiesAsObject) {
                if (statisticsCookiePrefixes.includes(cookie.substring(0,4))) {
                    storage.cookie.remove(cookie);
                }
            }
        }

        if (!storage.hasPermission(storage.categories.preferences)) {
            const preferencesCookiePrefixes = ['banner-'];
            for (const cookie in cookiesAsObject) {
                if (preferencesCookiePrefixes.includes(cookie.substring(0,8))) {
                    storage.cookie.remove(cookie);
                }
            }
        }
    }
};

export default cookieForm;
