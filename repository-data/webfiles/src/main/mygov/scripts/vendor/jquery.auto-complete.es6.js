/*
	jQuery autoComplete v1.0.7
    Copyright (c) 2014 Simon Steinberger / Pixabay
    GitHub: https://github.com/Pixabay/jQuery-autoComplete
    License: http://www.opensource.org/licenses/mit-license.php

    Modified 13/03/2019 by Jonathan Sutcliffe to work as an ES6 module
*/

/* global $ */

'use strict';

export default function(options) {
    var defaults = {
        source: 0,
        minChars: 3,
        delay: 150,
        cache: 1,
        menuClass: '',
        after: false,
        inputId: 'search-box',
        statusId: 'autocomplete-status',
        renderItem: function (item, search){
            // escape special characters
            search = search.replace(/[-\/\\^$*+?.()|[\]{}]/g, '\\$&');
            var re = new RegExp('(' + search.split(' ').join('|') + ')', 'gi');
            return `<li role="option" class="autocomplete-suggestion" data-val="${item}">${item.replace(re, '<b>$1</b>')}</li>`;
        },
        onSelect: function(e, term, item){}
    };

    var o = $.extend({}, defaults, options);

    // public methods
    if (typeof options == 'string') {
        this.each(function(){
            var that = $(this);
            if (options == 'destroy') {
                $(window).off('resize.autocomplete', that.updateSuggestionsList);
                that.off('blur.autocomplete focus.autocomplete keydown.autocomplete keyup.autocomplete');
                if (that.data('autocomplete'))
                    that.attr('autocomplete', that.data('autocomplete'));
                else
                    that.removeAttr('autocomplete');
                $(that.data('sc')).remove();
                that.removeData('sc').removeData('autocomplete');
            }
        });
        return this;
    }

    return this.each(function(){
        var that = $(this);
        // sc = 'suggestions container'
        that.suggestionsList = $(`<ul aria-label="Suggestions" role="listbox" id="autocomplete-results" class="autocomplete-suggestions ${o.menuClass}"></ul>`);

        that.data('sc', that.suggestionsList).data('autocomplete', that.attr('autocomplete'));
        that.attr('autocomplete', 'off');
        that.cache = {};
        that.last_val = '';

        that.updateSuggestionsList = function(resize, next){
            that.suggestionsList.css({
                top: that.outerHeight(),
                left: 0,
                width: that.outerWidth()
            });
            if (!resize) {
                that.suggestionsList.show();
                if (!that.suggestionsList.maxHeight) that.suggestionsList.maxHeight = parseInt(that.suggestionsList.css('max-height'));
                if (!that.suggestionsList.suggestionHeight) that.suggestionsList.suggestionHeight = $('.autocomplete-suggestion', that.suggestionsList).first().outerHeight();
                if (that.suggestionsList.suggestionHeight)
                    if (!next) that.suggestionsList.scrollTop(0);
                    else {
                        var scrTop = that.suggestionsList.scrollTop(), selTop = next.offset().top - that.suggestionsList.offset().top;
                        if (selTop + that.suggestionsList.suggestionHeight - that.suggestionsList.maxHeight > 0)
                            that.suggestionsList.scrollTop(selTop + that.suggestionsList.suggestionHeight + scrTop - that.suggestionsList.maxHeight);
                        else if (selTop < 0)
                            that.suggestionsList.scrollTop(selTop + scrTop);
                    }
            }
        };

        that.updateStatusBox = function (suggestionsCount = 0) {
            const statusBox = $(`#${o.statusId}`);
            switch(suggestionsCount) {
            case 0:
                statusBox.text('');
                break;
            case 1:
                statusBox.text('1 suggestion');
                break;
            default:
                statusBox.text(`${suggestionsCount} suggestions`);
                break;
            }
        };

        that.updateInputBox = function (suggestionsCount = 0) {
            const inputBox = $(`#${o.inputId}`);
            inputBox.attr('aria-expanded', suggestionsCount > 0);
        };

        if (o.after) {
            that.suggestionsList.insertAfter(o.after);
        } else {
            return;
        }

        $(window).on('resize.autocomplete', that.updateSuggestionsList);

        that.suggestionsList.on('mouseleave', '.autocomplete-suggestion', function (){
            $('.autocomplete-suggestion.selected').removeClass('selected');
        });

        that.suggestionsList.on('mouseenter', '.autocomplete-suggestion', function (){
            $('.autocomplete-suggestion.selected').removeClass('selected');
            $(this).addClass('selected');
        });

        that.on('blur.autocomplete', function(){
            let over_sb;
            try { over_sb = $('.autocomplete-suggestions:hover').length; } catch(e){ over_sb = 0; } // IE7 fix :hover
            if (!over_sb) {
                that.last_val = that.val();
                that.suggestionsList.hide();
                setTimeout(function(){ that.suggestionsList.hide(); }, 350); // hide suggestions on fast input
            } else if (!that.is(':focus')) setTimeout(function(){ that.focus(); }, 20);
        });

        if (!o.minChars) that.on('focus.autocomplete', function(){ that.last_val = '\n'; that.trigger('keyup.autocomplete'); });

        function suggest(data){
            var val = that.val();
            that.cache[val] = data;
            if (data.length && val.length >= o.minChars) {
                var s = '';
                for (var i=0;i<data.length;i++) s += o.renderItem(data[i], val);
                that.suggestionsList.html(s);
                that.updateSuggestionsList(0);
            }
            else {
                that.suggestionsList.hide();
            }

            that.updateStatusBox(data.length);
            that.updateInputBox(data.length);
        }

        that.on('keydown.autocomplete', function(e){
            const inputElement = document.querySelector(`#${o.inputId}`);
            let next, sel = $('.autocomplete-suggestion.selected', that.suggestionsList);
            // clear selected
            $('.autocomplete-suggestion').removeClass('selected').attr('aria-selected', false);

            // left, up, right, down (37, 38, 39, 40)
            // navigate through listbox
            if ((e.which === 37 || e.which === 38 || e.which === 39 || e.which === 40) && that.suggestionsList.html()) {
                if (!sel.length) {
                    next = (e.which === 40 || e.which === 39) ? $('.autocomplete-suggestion', that.suggestionsList).first() : $('.autocomplete-suggestion', that.suggestionsList).last();
                    that.val(next[0].querySelector('a').dataset.val);

                    next.addClass('selected')
                        .attr('aria-selected', true);

                    inputElement.setAttribute('aria-activedescendant', next.attr('id'));
                } else {
                    next = (e.which === 40 || e.which === 39) ? sel.next('.autocomplete-suggestion') : sel.prev('.autocomplete-suggestion');
                    if (next.length) {
                        sel.removeClass('selected');
                        that.val(next[0].querySelector('a').dataset.val);

                        next.addClass('selected')
                            .attr('aria-selected', true);

                        inputElement.setAttribute('aria-activedescendant', next.attr('id'));
                    }
                    else {
                        sel.removeClass('selected')
                            .attr('aria-selected', false);

                        that.val(that.last_val); next = 0;
                        inputElement.setAttribute('aria-activedescendant', '');
                    }
                }
                that.updateSuggestionsList(0, next);
                return false;
            }

            // esc
            // close listbox, remove activedescendant
            else if (e.which == 27) {
                that.val(that.last_val).suggestionsList.hide();
                inputElement.setAttribute('aria-activedescendant', '');
                inputElement.setAttribute('aria-expanded', false);
            }

            // enter
            // recommendation: set current search value to selected option, close listbox, remove activedescendant
            // actual behaviour: navigate to selected option
            else if (e.which === 13) {
                if (sel.length && that.suggestionsList.is(':visible')) {
                    sel[0].querySelector('a').click();
                }
            }

            // tab
            // close listbox, reset state, move focus to next focusable element
            // if an item is selected, set the text of the input box
            else if (e.which == 9) {
                if (sel.length && that.suggestionsList.is(':visible')) {
                    inputElement.value = sel[0].innerText.trim();
                    inputElement.setAttribute('aria-activedescendant', '');
                    inputElement.setAttribute('aria-expanded', false);

                    const submitButton = inputElement.parentNode.querySelector('[type=submit]');

                    window.setTimeout(function () {
                        submitButton.focus();
                    }, 0);
                }
            }
        });

        that.on('keyup.autocomplete', function(e){
            if ([13, 27, 35, 36, 37, 38, 39, 40].indexOf(e.which)) {
                var val = that.val();
                if (val.length >= o.minChars) {
                    if (val != that.last_val) {
                        that.last_val = val;
                        clearTimeout(that.timer);
                        if (o.cache) {
                            if (val in that.cache) { suggest(that.cache[val]); return; }
                            // no requests if previous suggestions were empty
                            for (var i=1; i<val.length-o.minChars; i++) {
                                var part = val.slice(0, val.length-i);
                                if (part in that.cache && !that.cache[part].length) { suggest([]); return; }
                            }
                        }
                        that.timer = setTimeout(function(){ o.source(val, suggest); }, o.delay);
                    }
                } else {
                    that.last_val = val;
                    that.suggestionsList.hide();
                    that.updateStatusBox();
                    that.updateInputBox();
                }
            }
        });
    });
}
