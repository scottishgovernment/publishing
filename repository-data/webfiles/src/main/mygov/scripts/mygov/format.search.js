// SEARCH
/*
 Contains functionality for the search page
 Fetches search results
 Puts results on the results page
 */

/* global require */

'use strict';

import gup from './gup';
import Paginator from './paginator';
import $ from 'jquery';

let searchResultsItemsTemplate = require('../templates/searchresultsitems.hbs');
const didYouMeanTemplate = require('../templates/didyoumean.hbs');

const search = {

    //Globals
    settings: {
        name: 'search',
        pageSize: 10,
        pagePadding: 3,
        searchUrl: '/service/search/site?q=',
        resultsContainer: '#search-results',
        resultsList: '#search-results-list',
        paginationContainerId: 'pagination',
        notAvailable: 'Search is not available right now, please try again later'
    },

    // Run when module first loads
    init: function (gups) {
        let keywords;
        if ( gups ){
            keywords = window.location.pathname.split('/').join(' ').split('-').join(' ');
        }

        this.initPaginator();
        this.doInitialSearch(keywords);
    },

    initPaginator: function () {
        const that = this;

        // create paginator
        that.paginator = new Paginator(document.getElementById(that.settings.paginationContainerId), that.settings.pagePadding, that);
    },

    /**
     * Updates the "showing n results" text
     * @param {number} count
     *
     * @returns {boolean} success
     */
    updateResultCount: function (count) {
        const countElement = $('.js-search-results-count');

        if (countElement.length > 0 && typeof count === 'number') {
            countElement.html(`<b>${count}</b> ${(count === 1 ? 'result' : 'results')}`);
            return true;
        } else {
            return false;
        }
    },

    doSearch: function (params, append) {

        // add ajax loader
        const elementsToHideWhileLoading = $('#result-count, #pagination, #search-results-list');
        const ajaxLoader = $('<div id="ajax-loader">Loading results</div>');

        if (!append) {
            elementsToHideWhileLoading.addClass('fully-hidden');
        }

        ajaxLoader.insertAfter('#search-results-list');

        const that = this;

        const url = this.settings.searchUrl + params.q + '&from=' + params.from + '&size=' + params.size;

        $.ajax(url)
            .done(function (result) {

                that.paginator.setParams(params.from, params.size, result.hits.total.value);
                that.paginator.renderPages();

                document.title = `Search results for "${params.q}" (page ${gup('page') || 1}) - mygov.scot`;

                // display any "did you mean" suggestions
                if (result.hasOwnProperty('suggest')) {
                    const didYouMeanItems = that.getDidYouMean(result.suggest);

                    if (didYouMeanItems.length > 0) {
                        const html = didYouMeanTemplate({ items: didYouMeanItems });

                        $('#search-results').before(html);
                    }
                }

                that.numberHits(result, params);

                //Put the results on screen
                that.populateResults(result, false, append);

                // update the list's start point
                $(that.settings.resultsContainer).find('ol#seach-results-list')
                    .attr('start', params.from + 1)
                    .attr('data-search-term', params.q);

                that.updateResultCount(result.hits.total.value);
                //Set the text box text
                if ( $('body').hasClass('status')) {
                    $('#site-search').val(window.location.pathname.replace(/\//g, ' '));
                } else {
                    // provide an empty string if no 'q' to gup
                    const guppedQ = gup('q') || '';
                    $('#site-search').val(decodeURIComponent(guppedQ.replace(/\+/g, '%20')));
                }
            })
            .fail(function () {
                //Search not available
                $(that.settings.resultsContainer).html(that.settings.notAvailable);
            })
            .always(function () {
                // remove "loading" spinner
                 ajaxLoader.remove();
                 elementsToHideWhileLoading.removeClass('fully-hidden');
            });
    },

    doInitialSearch: function (keywords) {

        //Get initial search term
        this.searchParams = {
            q: this.getQueryString('q') || keywords || '',
            from: this.getQueryString('from') || 0,
            size: this.getQueryString('size') || this.settings.pageSize
        };

        // parse a page querystring param
        if (this.getQueryString('page') !== '') {
            this.searchParams.from = parseInt(this.getQueryString('page') - 1, 10) * this.searchParams.size;
        }

        search.doSearch(this.searchParams);
    },

    getQueryString: function (param, $window, $gup) {
        const currentGup = $gup || gup;
        return currentGup(param, $window) || '';
    },

    /*
    Takes some data, mixes it with a template and returns the results
    */
    templatise: function (data, template) {
        //Maybe a template wasn't passed in
        searchResultsItemsTemplate = template || searchResultsItemsTemplate;

        //Get the template and the data together
        return searchResultsItemsTemplate(data);
    },

    numberHits : function (data, params) {
        const hits = data.hits.hits;

        for (let i = 0, il = hits.length; i < il; i++) {
            hits[i].globalIndex = params.from + i;
        }
    },

    populateResults: function (data, $searchResultsItemsTemplate, append) {
        let html;

        if (append) {
            html = this.templatise(data, searchResultsItemsTemplate);

            $(this.settings.resultsList).append(html);
        } else {
            searchResultsItemsTemplate = $searchResultsItemsTemplate || searchResultsItemsTemplate;
            html = this.templatise(data, searchResultsItemsTemplate);
            $(this.settings.resultsList).html(html);
        }
    },

    /**
     *
     * @param {object} suggest
     */
    getDidYouMean: function(suggest) {
        // remove existing suggestions
        $('#suggestions').remove();

        // find the suggestion whose HIGHLIGHTED section is fully wrapped in EM tags
        const suggestedOptions = suggest.didyoumean[0].options;

        const suggestionsArray = [];

        for (let i = 0, il = suggestedOptions.length; i < il; i++) {
            suggestionsArray.push(suggestedOptions[i]);
        }

        return suggestionsArray;
    }
};

window.search = search;

window.format = search;
window.format.init(true);

export default search;
