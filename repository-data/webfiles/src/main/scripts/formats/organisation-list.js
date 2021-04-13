// ORG LIST
/**
 * Contains functions for the Organisation List Page
 */

'use strict';

const orgListPage = {
    init: function () {
        document.querySelectorAll('.sector__link').forEach(function(link) {
            link.addEventListener('focus', function () {
                link.parentNode.parentNode.classList.add('sector--has-focus');
            });

            link.addEventListener('blur', function () {
                link.parentNode.parentNode.classList.remove('sector--has-focus');
            });
        });
    }
};

window.format = orgListPage;
window.format.init();

export default orgListPage;
