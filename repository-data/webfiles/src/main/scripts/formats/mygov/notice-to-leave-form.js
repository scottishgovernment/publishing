
import noticeToLeaveCommon from './notice-to-leave-common';

const noticeToLeaveForm = noticeToLeaveCommon;

window.format = noticeToLeaveForm;

const formBoxEl = document.querySelector('.multi-page-form');

let formType;
if (formBoxEl.getAttribute('name') === 'notice-to-leave-form') {
    formType = 'tenant';
} else {
    formType = 'subtenant';
}
window.format.init(formType);

export default noticeToLeaveForm;
