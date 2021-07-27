// SMART ANSWER

'use strict';

import feedback from '../components/feedback';

const smartanswer = {};

smartanswer.init = function() {
    feedback.init();

    document.querySelector('#answered-questions').style.display = 'none';
    document.querySelectorAll('#answered-questions li').forEach(function(answer) {
        answer.style.display = 'none';
    });

    var questions = document.querySelectorAll('.smartanswer-question');
    var i;
    for (i = 0; i < questions.length; i++) {
        if (i !== 0)  {
            questions[i].style.display = 'none';
        }
    }

    document.querySelectorAll('.smartanswer-answer').forEach(function(answer) {
        answer.style.display = 'none';
    });

    document.querySelectorAll('.smartanswer-nextbutton').forEach(function(button) {
        button.onclick = function(el) {

            const questionDiv = button.closest('div.smartanswer-question');
            const checkedButton = questionDiv.querySelector("input[type='radio']:checked");
            const nextSpan = checkedButton.parentElement.querySelector('.smartanswer-nextpage');
            const next = document.querySelector("#" + nextSpan.textContent);
            questionDiv.style.display = 'none';
            next.style.display = 'block';

            // display the answer in the section at the bottom.
            document.querySelector('#answered-questions').style.display = 'block';
            const answerLi = document.querySelector('[data-answer="' + questionDiv.id +'"]')
            answerLi.style.display='block';
            const answerSpan = document.querySelector('[data-answer-to-question="' + questionDiv.id +'"]');
            answerSpan.textContent = checkedButton.parentNode.getElementsByTagName('label')[0].textContent;
        };
    });


};

window.format = smartanswer;
window.format.init();

export default smartanswer;
