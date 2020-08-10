// ORG HUB

'use strict';

import feedback from '../components/feedback';

const orghub = {};

orghub.init = function () {
    let orgLogoElement = document.querySelector('.org-logo');
    if (orgLogoElement) {
        orgLogoElement.addEventListener('error', orghub.replaceMissingLogo);
    }
    feedback.init();
};

orghub.replaceMissingLogo = function() {
    document.querySelector('.org-logo').setAttribute('src', '/assets/images/no-logo.png');
};

window.format = orghub;
window.format.init();

export default orghub;
