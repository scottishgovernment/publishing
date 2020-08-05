/* global $ */
// CATEGORIES

'use strict';

import stickyBackToTop from './sticky-back-to-top';
import feedback from './feedback';

const autoscaleHeaderAt = 105,
    autoscalePollingFrequency = 50,
    autoscaleThreshold = 786,
    siteHeader = $('.category-header-wrapper');
let autoscaleStatus = 'up';

const categories = {
    init: function () {
        this.initStickyTitles();
        this.initTitleCards();
        this.initStickyHeader();

        this.headerAutoscaling();

        feedback.init();

        stickyBackToTop.init();
    },

    /** Now Use Timer to check scroll position for sticky header
     *  as scroll events are unreliable
     *  Uses the touch events to start and stop the monitoring
     */
    headerAutoscaling: function() {
        const t = this;
        let interval;

        const init = function() {
            const windowWidth = $(window).width();

            // Reset autoscaling
            clearInterval(interval);
            interval = null;

            // Only run the autoscaling on small screens.
            if (windowWidth < autoscaleThreshold) {

                // Polling often is the best practice for catching
                // scrolling events due to the inconsistencies in how
                // browsers handle the scrolling event.
                interval = setInterval(function() {
                    const position = $(window).scrollTop();
                    // Perform scaling
                    if (position > autoscaleHeaderAt) {
                        t.headerScaleDown();
                    }
                    else {
                        t.headerScaleUp();
                    }
                }, autoscalePollingFrequency);

            }
            else {
                // Ensure that header is scaled up if window is wide.
                t.headerScaleUp();
            }

        };

        $(window).resize(function() {
            init();
        });

        $(window).on('touchend', function() {
            clearInterval(interval);
            interval = null;
        });

        $(window).on('touchstart', function() {
            init();
        });
    },

    headerScaleDown: function() {
        if (autoscaleStatus === 'down') {
            return;
        }
        siteHeader.addClass('category-header-wrapper--sticky');
        autoscaleStatus = 'down';
    },

    headerScaleUp: function() {
        if (autoscaleStatus === 'up') {
            return;
        }
        siteHeader.removeClass('category-header-wrapper--sticky');
        autoscaleStatus = 'up';
    },


    /**
     * Sets up behaviour on the header cards:
     * 1. Click/hover behaviour on ancestor cards
     * 2. Touch/drag behaviour on ancestor cards
     */
    initTitleCards: function () {

        let startX = 0;
        let moveX = 0;

        const childCard = $('.js-child-card');
        const parentCard = $('.js-parent-card');
        const ancestorCards = $('.js-ancestor-card');
        const swipeCards = $('.js-swipe-card').not(':first-child');

        let cardToReveal = [];
        let cardsToMove = [];
        let moveThreshold = 100;

        // store initial widths of each card -- we'll want them for swipe animation
        $('.category-header__card').each(function () {
            $(this).attr('data-initialwidth', $(this).outerWidth());
            moveThreshold = $(this).outerWidth() * 0.333333;
        });

        // recalculate initial widths on window resize
        $(window).on('resize', function () {
            $('.category-header__card').each(function () {
                $(this).attr('data-initialwidth', $(this).outerWidth());
                moveThreshold = $(this).outerWidth() * 0.333333;
            });
        });

        /**
         * parent page card animation on mouseover and on click
         */
        ancestorCards.on('mouseenter mouseleave', function () {
            childCard.toggleClass('category-header__card--dink');

            // dink cards that follow
            $(this).nextAll('.js-ancestor-card').toggleClass('category-header__card--dink');

        }).on('click', function (event) {
            event.preventDefault();

            const that = this;

            const closest = $(this).closest('.category-header');

            const cssObj = {
                width: closest.width() + parseInt($(this).css('padding-right'), 10),
                left: '100%',
                marginLeft: 0
            };

            childCard.css(cssObj);
            $(this).nextAll('.js-ancestor-card').css(cssObj);

            // delay to match the CSS animation
            setTimeout(function () {

                window.location.href = $(that).attr('href');
            }, 200);
        });

        /**
         * Start tracking a touch drag
         */
        swipeCards.on('touchstart', function (e) {
            startX = e.originalEvent.changedTouches[0].pageX;

            swipeCards.addClass('js-card--no-animation');
        });

        /**
         * Track a touch drag, and animate the child card to follow
         */
        swipeCards.on('touchmove', function (e) {
            const nowX = e.originalEvent.changedTouches[0].pageX;
            const nowXRelative = nowX - startX;

            moveX = Math.max(nowXRelative, 0);
            moveX = Math.min(moveX, $('.category-header').width() - parseInt($(this).css('margin-left'), 10));

            cardsToMove = $(this).add($(this).nextAll()).add(childCard);

            if ($(this).is('.js-ancestor-card')) {
                cardToReveal = $(this).prev('.js-ancestor-card');
            } else {
                cardToReveal = parentCard;
            }

            cardsToMove.each(function () {
                $(this).css({
                    left: moveX,
                    width: $(this).attr('data-initialwidth')
                });
            });

            if(moveX > moveThreshold) {
                cardToReveal.addClass('js-ancestor-card--active');
            } else {
                cardToReveal.removeClass('js-ancestor-card--active');
            }
        });

        /**
         * If the user has dragged further than the drag threshold, navigate to parent page
         * Else return the child card to its original position
         */
        swipeCards.on('touchend', function () {
            swipeCards.removeClass('js-card--no-animation');

            if (moveX > moveThreshold) {
                cardToReveal.click();
            } else {
                if(cardsToMove.length > 0) {
                    cardsToMove.css({
                        left: 0,
                        width: ''
                    });
                }

                if (cardToReveal.length > 0) {
                    cardToReveal.removeClass('js-ancestor-card-link--active');
                }
            }

            cardsToMove = cardToReveal = [];
        });
    },

    initStickyHeader: function () {
        const that = this;

        const categoryHeader = $('.js-category-header-sticky');

        // create ghost to take up the layout space when position switches to fixed
        const categoryHeaderGhost = $('<div class="js-category-header-ghost"></div>');
        categoryHeaderGhost.insertBefore(categoryHeader);

        // set initial state in case we start partway down the page
        that.bannerHeight = categoryHeader.outerHeight();
        that.stickCategoryHeader(categoryHeader, categoryHeaderGhost);

        $(window).on('resize', function () {
            that.bannerHeight = categoryHeader.outerHeight();
            that.stickCategoryHeader(categoryHeader, categoryHeaderGhost);
        });

        $(window).on('scroll', function () {
            that.stickCategoryHeader(categoryHeader, categoryHeaderGhost);
        });
    },

    stickCategoryHeader: function (categoryHeader, categoryHeaderGhost) {

        const windowScrollTop = $(window).scrollTop();

        // need to offset this to accommodate other sticky items
        const offsetModifier = $('#page-top').offset().top;
        const categoryHeaderInitialOffset = categoryHeader.parent().offset().top;

        if(windowScrollTop > categoryHeaderInitialOffset - offsetModifier + (this.bannerHeight - 63)) {
            categoryHeader
                .addClass('category-header-wrapper--sticky')
                .css({
                    top: offsetModifier
                })
                .find('.large--eight-twelfths').css({
                    width: '100%'
                });

            categoryHeaderGhost.css({
                display: 'block'
            });
        } else {
            categoryHeader
                .removeClass('category-header-wrapper--sticky')
                .css({
                    top: 'auto'
                })
                .find('.col-md-8').css({
                    width: ''
                });

            categoryHeaderGhost.css({
                display: 'none'
            });
        }
    },

    /**
     * Sets up behaviour for the jumpoff-with-subcategories page subcategory titles
     */
    initStickyTitles: function () {
        const that = this;

        $('.js-category-title-sticky').each(function () {

            const thisStickyTitle = $(this).find('.category-item__header--group-header');
            const thisStickySection = $(this);

            // create ghost to take up the layout space when position switches to fixed
            const categoryHeaderGhost = $('<div class="js-category-header-ghost"></div>');
            categoryHeaderGhost.insertBefore(thisStickyTitle);

            // set initial state in case we start partway down the page
            that.stickSectionTitle(thisStickyTitle, thisStickySection, categoryHeaderGhost);

            $(window).on('resize', function () {
                that.stickSectionTitle(thisStickyTitle, thisStickySection, categoryHeaderGhost);
            });

            $(window).on('scroll', function () {
                that.stickSectionTitle(thisStickyTitle, thisStickySection, categoryHeaderGhost);
            });
        });
    },

    /**
     * Handler for sticky functionality used by initStickyTitles()
     *
     * @param thisStickyTitle
     * @param thisStickySection
     * @param thisStickyTitleGhost
     */
    stickSectionTitle: function (thisStickyTitle, thisStickySection, thisStickyTitleGhost) {
        const windowScrollTop = $(window).scrollTop();
        const thisStickySectionInitialOffset = thisStickySection.offset().top;

        thisStickyTitle.width(thisStickyTitle.parent().width());

        // need to offset this to accommodate other sticky items
        let topOffsetModifier = $('.js-category-header-sticky').height();
        if ($('#staging-notice').length > 0) {
            topOffsetModifier += $('#staging-notice').height();
        }

        // we will need another offset modifier to align the sticky header to the last subcategory title
        // and this modifier will be different on single-column and two-column views
        let bottomOffsetModifier;
        let stickyTitleTopPadding;

        // this width check is weak
        if ($(window).innerWidth() < 768) {
            bottomOffsetModifier = thisStickyTitle.outerHeight(true) * -1 + 28;
            stickyTitleTopPadding = 0;
        } else {
            bottomOffsetModifier = 0;
            stickyTitleTopPadding = 28;
        }

        // calculate a top offset
        let topOffset = 0;

        // height of any preceding sticky content
        topOffset += $('.js-category-header-sticky').offset().top - $(window).scrollTop();

        // height of category header
        topOffset += parseInt($('.js-category-header-sticky').css('height'), 10);

        // it would be nicest to stop the stickiness where the last header in the section appears
        const lastHeader = thisStickySection.find('article:last-child .category-item__title').last();

        const maxBottomOffset = lastHeader.offset().top + bottomOffsetModifier;

        if (windowScrollTop + topOffsetModifier + stickyTitleTopPadding > thisStickySection.offset().top && windowScrollTop + topOffsetModifier + stickyTitleTopPadding < maxBottomOffset) {
            // we are scrolling in a section

            // add sticky header styling (small screens)
            thisStickyTitle.addClass('category-item__header--group-header--sticky');
            thisStickyTitle.find('.title-text').addClass('title-text--sticky');

            // make ghost take up layout space
            thisStickyTitleGhost.css({
                display: 'block',
                height: thisStickyTitle.outerHeight(true)
            });

            // stick parent title at top of content area
            thisStickyTitle.css({
                position: 'fixed',
                top: topOffset + stickyTitleTopPadding,
                zIndex: '1'
            });

        } else if(windowScrollTop + topOffsetModifier + stickyTitleTopPadding < maxBottomOffset) {
            // we are before a section

            // hide ghost
            thisStickyTitleGhost.css({
                display: 'none',
                height: 0
            });

            // remove sticky header styling (small screens)
            thisStickyTitle.removeClass('category-item__header--group-header--sticky');
            thisStickyTitle.find('.title-text').removeClass('title-text--sticky');

            // unstick parent title
            thisStickyTitle.css({
                position: 'static',
                top: 0
            });
        } else {
            // we are at the end of a section

            // retain ghost position
            thisStickyTitleGhost.css({
                display: 'block',
                height: thisStickyTitle.outerHeight(true)
            });

            // stick parent title next to last child title
            thisStickyTitle.css({
                position: 'absolute',
                top: maxBottomOffset - thisStickySectionInitialOffset + 'px'
            });
        }
    }
};

window.format = categories;
window.format.init();

export default categories;
