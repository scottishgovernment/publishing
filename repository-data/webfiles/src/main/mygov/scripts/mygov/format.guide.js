// GUIDE
/*
 Contains functionality for the guide page
 Adds the select drop down
 Alters the guide content header to reflect current hash
 */

/* global pubsub */

'use strict';

import feedback from './feedback';
import stickyBackToTop from './sticky-back-to-top';
import pageGroup from '../shared/component.page-group';
import '../shared/component.sticky-document-info';
import $ from 'jquery';

let pgroup;

const guide = {
    init: function () {
        const that = this;

        //When select is changed, then change the page
        $('select.guide-sections-dropdown').bind('change', function() {
            const url = $(':selected', this).attr('value');
            that.doRedirect(url);
        });

        //Initialise the feedback handlers
        feedback.init();
        stickyBackToTop.init();
        pgroup = pageGroup.init();
        this.initStickyInfoInteractivity();

        if (window.location.hash) {
            window.setTimeout(function () {
                guide.goToAnchorLink(window.location.hash.substring(1));
            }, 1000);
        }

        $('a[href^=#]:not(.js-toggle-expand)').on('click', function (event) {
            event.preventDefault();
            window.location.hash = this.getAttribute('href');
            guide.goToAnchorLink(this.getAttribute('href').substring(1));
        });
    },

    doRedirect: function (url) {
        document.location.href = url;
    },

    goToAnchorLink: function (id) {
        if (document.getElementById(id)) {
            window.scrollTo(window.scrollX, document.getElementById(id).offsetTop - 84);
        }
    },

    /**
     * It would be nice to pull this out of the guide code, but it's not
     * clear what the general case would be yet
     */
    initStickyInfoInteractivity: function() {

        function scrollListTop() {

            const offset = $('.page-group').offset().top - parseInt($('.sticky-document-info').css('top'), 10) + 22; /** 21 is the MAGIC NUM!! */

            window.scrollTo(null, offset);
        }

        /**
         * Open or close the pageGroup list with buttons in sticy doc info
         */
        $('#js-mobile-toc-trigger-open').click(function(){
            pgroup.open();
            scrollListTop();
        });
        $('#js-mobile-toc-trigger-close').click(function(){
            pgroup.close();
            scrollListTop();
        });

        /* Hide close button to start with */
        $('#js-mobile-toc-trigger-close').hide();

        /**
         * Update which button to show based on page group state
         */
        pubsub.subscribe('page-group-change', function(event, state){
            if (state === 'open') {
                $('#js-mobile-toc-trigger-close').css('display', 'inline-block');
                $('#js-mobile-toc-trigger-open').hide();
            } else {
                $('#js-mobile-toc-trigger-open').css('display', 'inline-block');
                $('#js-mobile-toc-trigger-close').hide();
            }
        });

        $('.sticky-document-info__trigger').click(function(){
            const button = $(this),
                expandClass = 'sticky-document-info__trigger--expanded',
                panel = button.next(),
                openClass = 'sticky-document-info__panel--open',
                isOpen = panel.hasClass(openClass),
                isSticky = $('.sticky-document-info').hasClass('sticky-document-info--is-sticky');
            let buttonText;

            if (isSticky) {
                buttonText = 'All files';
                panel.find('.primary-doc').show()
                    .next().find('h3').show();
            } else {
                buttonText = 'Supporting files';
                panel.find('.primary-doc').hide()
                    .next().find('h3').hide();
            }
            button.text(buttonText);

            if ( !isOpen ) {
                // Add classes
                panel.addClass(openClass);
                button.addClass(expandClass);
            } else {
                // Remove classes
                panel.removeClass(openClass);
                button.removeClass(expandClass);
                // blur button
                button.blur();
            }
        });
    }
};

window.format = guide;
window.format.init();

export default guide;
