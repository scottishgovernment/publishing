'use strict';

import storage from '../storage';

class Notification {
    constructor (notification) {
        this.notification = notification;
        this.notificationClose = notification.querySelector('.js-close-notification');
    }

    // note: this is specific behaviour for the important banner. it needs to be generalised when rolled out to other banners.
    init() {
        if (this.notification.dataset.customcondition) {
            return;
        }

        let expiry = 365;

        if (this.notification.classList.contains('priority-banner') ||
            this.notification.id === 'staging-notice') {
            expiry = 1;
        }

        if (this.notificationClose) {
            if (!storage.getCookie(`banner-${this.notification.id}`)) {
                this.notification.classList.remove('fully-hidden-if-js');
            } else {
                this.notification.classList.add('fully-hidden-if-js');
            }

            this.notificationClose.addEventListener('click', () => {
                if (this.notification.parentNode) {
                    this.notification.parentNode.removeChild(this.notification);
                }

                storage.setCookie(
                    'preferences',
                    `banner-${this.notification.id}`,
                    true,
                    expiry
                );
            });
        }

        // set initialised
        this.notification.classList.add('js-initialised');
    }
}

export default Notification;
