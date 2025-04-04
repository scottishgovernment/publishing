// ARTICLE

'use strict';

import '../../../../../node_modules/prismjs/prism';

const article = {
    init: function() {
        this.resizeIframes();
    },

    resizeIframes: () => {
        function resizeIframe(obj) {
            obj.style.height = obj.contentWindow.document.body.scrollHeight + 'px';
        }

        const iframes = [].slice.call(document.querySelectorAll('iframe.example__iframe'));

        iframes.forEach(iframe => {
            iframe.addEventListener('load', () => resizeIframe(iframe));
        });
    }
};

window.format = article;
window.format.init();

export default article;
