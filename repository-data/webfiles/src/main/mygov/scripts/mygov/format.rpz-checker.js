// RPZ CHECKER

'use strict';

import PostcodeLookup from './component.postcode-lookup';
import feedback from './feedback';

const rpzChecker = {

    init: function(){
        feedback.init();
        const rpzComplete = function(){};
        new PostcodeLookup({  // NOSONAR
            rpz: true,
            lookupId: 'rpz-checker-postcode-lookup',
            rpzComplete: rpzComplete,
            displayNotRPZ: true
        });
    }

};

window.format = rpzChecker;
window.format.init();

export default rpzChecker;
