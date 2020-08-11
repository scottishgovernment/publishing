'use strict';

import storage from '../tools/storage';

const cookieModule = {
    cookieNotice: document.querySelector('#cookie-notice'),
    cookieConfirm: document.querySelector('#cookie-confirm'),

    init: function () {
        if (!this.cookieNotice || !this.cookieConfirm) {
            return;
        }

        const that = this;

        this.setInitialCookiePermissions();
        this.initCookieForm();

        const cookieAcceptButton = this.cookieNotice.querySelector('.js-accept-cookies');

        // check whether we need to display the cookie notice
        if (!storage.get({type: storage.types.cookie, name: 'cookie-notification-acknowledged'})) {
            this.cookieNotice.classList.remove('fully-hidden');
        }

        // bind a click handler to the close button
        cookieAcceptButton.addEventListener('click', function (event) {
            event.preventDefault();

            const cookiePermissions = JSON.parse(JSON.stringify(storage.categories));
            for (const key in cookiePermissions) {
                if (!cookiePermissions.hasOwnProperty(key)) {continue;}

                cookiePermissions[key] = true;
            }

            storage.setCookie(
                storage.categories.necessary,
                'cookiePermissions',
                JSON.stringify(cookiePermissions).replace(/\"/g,'\"'),
                365
            );

            storage.setCookie(
                storage.categories.necessary,
                'cookie-notification-acknowledged',
                'yes',
                365
            );

            // show success message
            that.cookieNotice.classList.add('fully-hidden');
            that.cookieConfirm.classList.remove('fully-hidden');

            // scroll to success message
            window.scrollTo(window.scrollX, 0);
        });
    },

    setInitialCookiePermissions: function () {
        const permissionsString = storage.getCookie('cookiePermissions') || '';

        if (!storage.isJsonString(permissionsString)) {
            const permissions = {};
            permissions.statistics = true;
            permissions.preferences = true;

            storage.setCookie(storage.categories.necessary,
                'cookiePermissions',
                JSON.stringify(permissions).replace(/\"/g,'\"')
            );
        }
    },

    initCookieForm: function () {
        const that = this;

        // on init, set the relevant permissions
        const cookiePermissionsString = storage.get({
            type: 'cookie',
            name: 'cookiePermissions'
        });

        // on init show the form
        const cookieFormBox = document.getElementById('cookie-form-box');
        if (cookieFormBox) {
            const className = 'fully-hidden';
            if (cookieFormBox.classList) {
                cookieFormBox.classList.remove(className);
            } else {
                cookieFormBox.className = cookieFormBox.className.replace(new RegExp('(^|\\b)' + className.split(' ').join('|') + '(\\b|$)', 'gi'), ' ');
            }

            const inputGroups = document.querySelectorAll('#cookie-preferences .input-wrapper');
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
                    value: JSON.stringify(cookiePermissions).replace(/\"/g,'\"'),
                    name: 'cookiePermissions',
                    expiry: 365
                });

                storage.set({
                    type: 'cookie',
                    category: 'necessary',
                    name: 'cookie-notification-acknowledged',
                    value: 'yes',
                    expires: 365
                });

                // hide cookie notice
                that.cookieNotice.classList.add('fully-hidden');
                that.cookieConfirm.classList.remove('fully-hidden');

                // scroll to success message
                window.scrollTo(window.scrollX, 0);
            });
        }
    }
};

cookieModule.init();

export default cookieModule;
