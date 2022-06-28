'use strict';

import PromiseRequest from '../../../../node_modules/@scottish-government/pattern-library/src/base/tools/promise-request/promise-request';
import temporaryFocus from '../../../../node_modules/@scottish-government/pattern-library/src/base/tools/temporary-focus/temporary-focus';

class AsyncSearch {
    constructor(asyncSearch) {
    }

    init() {
        this.resultsContainer = document.getElementById('search-results');

        this.bindEvents();
    }

    bindEvents() {
        this.resultsContainer.addEventListener('click', event => {
            if (event.target.classList.contains('ds_pagination__link')) {
                event.preventDefault();

                const targetHref = event.target.href;

                var targetUrlParams = new URLSearchParams(targetHref);
                var currentUrlParams = new URLSearchParams(window.location.search);

                let page = 1;

                if (targetUrlParams.get('page')) {
                    page = targetUrlParams.get('page');
                }

                currentUrlParams.set('page', page);

                const pageUrl = `${window.location.pathname}?${currentUrlParams.toString()}`;
                const resultsUrl = pageUrl.replace('/funnelback?', '/funnelbackresults?');

                this.loadResults(resultsUrl)
                    .then(value => {
                        window.history.pushState({}, '', pageUrl);
                        this.populateResults(value.responseText);
                    })
                    .catch(error => {
                        console.log('Failed to fetch additional results. Navigating normally. ', error);
                        window.location = targetHref;
                    });
            }
        });

        window.addEventListener('popstate', () => {
            const resultsUrl = window.location.href.replace('/funnelback?', '/funnelbackresults?');
            this.loadResults(resultsUrl)
                .then(value => {
                    this.populateResults(value.responseText);
                })
                .catch(error => {
                    console.log('Failed to fetch additional results.', error);
                });
        });
    }

    loadResults(resultsUrl) {
        const promiseRequest = PromiseRequest(resultsUrl);
        return promiseRequest;
    }

    populateResults(html) {
        document.getElementById('search-results').innerHTML = html;

        const rect = this.resultsContainer.getBoundingClientRect();
        window.setTimeout(() => {
            window.scrollTo(window.scrollX, window.scrollY + rect.top);
        }, 0);

        temporaryFocus(this.resultsContainer);
    }
}

export default AsyncSearch;
