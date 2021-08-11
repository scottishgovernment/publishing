/* global document, window */

// SMART ANSWER

import feedback from '../components/feedback';
import SmartAnswer from '../components/smart-answer';

const smartanswer = {
    init: () => {
        const smartAnswerContainers = [].slice.call(document.querySelectorAll('[data-module="smartanswer"]'));
        smartAnswerContainers.forEach(smartAnswerContainer => new SmartAnswer(smartAnswerContainer).init());

        feedback.init();
    }
};

window.format = smartanswer;
window.format.init();

export default smartanswer;
