// CORPORATE ORG HUB

'use strict';

import feedback from './feedback';
import $ from 'jquery';

const corporateOrgHub = {};

corporateOrgHub.init = function () {
    // init toggle
    $('.js-toggle-services').on('click', function () {
        const pageHeight = $('body').height(),
            bottomOffset = pageHeight - window.scrollY,
            orgServices = $('.org-services'),
            buttonText = $(this).find('.expand__text');

        if (orgServices.hasClass('org-services--show-all')) {
            $('.org-services').toggleClass('org-services--show-all');
            buttonText.text('Show all');
            window.scrollTo(window.scrollX, $('body').height() - bottomOffset);
        } else {
            $('.org-services').toggleClass('org-services--show-all');
            buttonText.text('Show fewer');
        }

        $(this).toggleClass('expand--open');
    });

    $('.org-logo').on('error', corporateOrgHub.replaceMissingLogo);

    feedback.init();
};

corporateOrgHub.replaceMissingLogo = function() {
    $('.org-logo').attr('src', '/assets/images/no-logo.png');
};

window.format = corporateOrgHub;
window.format.init();

export default corporateOrgHub;
