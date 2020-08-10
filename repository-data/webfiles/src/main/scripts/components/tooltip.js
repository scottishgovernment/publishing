// TOOLTIP

'use strict';

import $ from 'jquery';

const tooltip = {
    /**
     * Converts title attributes of elements of the relevant class ('mgs-tooltip') into something more styled than
     * a standard "title" attribute
     */
    init: function () {
        const global = this;

        $('.mgs-tooltip').each(function () {

            const $that = $(this);

            /**
             * Creating the tooltip
             */
            const tooltipText = $(this).attr('title');

            const tooltipContainer = $('<div class="mgs-tooltip__container"></div>');
            const tooltipArrow = $('<div class="mgs-tooltip__arrow"></div>');
            const tooltipContent = $('<div class="mgs-tooltip__content"></div>');
            const tooltipClose = $('<span class="mgs-tooltip__close glyphicon glyphicon-remove"></span>');

            tooltipContent
                .text(tooltipText)
                .appendTo(tooltipContainer);

            tooltipArrow.appendTo(tooltipContainer);

            tooltipClose.appendTo(tooltipContent);

            tooltipContainer.appendTo($(this));

            $(window).on('resize', function () {
                /**
                 * Repositioning the tooltip
                 */
                global.placeTooltip($that);
            });

            tooltipClose.on('click', function (event) {
                event.stopPropagation();
                $that.removeClass('active');
            });

        }).on('click', function (event) {
            // don't bubble up as that will fire the tooltip close behaviour and the tooltip will not show
            event.stopPropagation();

            // remove any active tooltips
            $('.mgs-tooltip').removeClass('active');

            const $that = $(this);
            /**
             * Positioning the tooltip
             */
            global.placeTooltip($that);

            // show the tooltip
            $that.addClass('active');

            // create close event on click anywhere else on the page
            $(document).on('click', function() {
                if($that.is(':visible')) {
                    $that.removeClass('active');
                }

                // remove close event
                $(document).off('click');
            });
        });
    },

    placeTooltip: function (tooltip) {
        const tooltipContainer = tooltip.find('.mgs-tooltip__container');
        const tooltipContent = tooltip.find('.mgs-tooltip__content');

        const parent = tooltip.parent();
        let nudgeAmount = 0;

        // allow the box to stick out a little bit past the content area on either side
        const overflowAmount = 8;

        // case 1: box is too far to the left
        if (tooltipContainer.offset().left < parent.offset().left) {
            nudgeAmount = parent.offset().left - tooltipContainer.offset().left;

            nudgeAmount = nudgeAmount - overflowAmount;
        }

        // case 2: box is too far to the right
        else if (tooltipContainer.offset().left + tooltipContainer.width() - parent.offset().left > parent.width()) {
            nudgeAmount = tooltipContainer.offset().left + tooltipContainer.width() - parent.offset().left - parent.width();
            nudgeAmount = nudgeAmount * -1 + overflowAmount;
        }

        // move box into position
        tooltipContent.css({
            left: nudgeAmount
        });
    }
};

tooltip.init();

export default tooltip;
