
<section id="feedback" class="mg_feedback">
    <div class="ds_error-summary  fully-hidden" id="feedbackErrorSummary" aria-labelledby="error-summary-title" role="alert">
        <h2 class="ds_error-summary__title" id="error-summary-title">There is a problem</h2>
        <div class="ds_error-summary__content">

        </div>
    </div>

    <div id="feedbackThanks" class="mg_feedback__success  ds_reversed  fully-hidden">
        <p><strong>Thanks for your feedback</strong></p>
    </div>

    <form id="feedbackForm">
        <input id="page-category" type="hidden" value="${layoutName}" >


        <fieldset id="feedbacktype">
            <legend>Was this helpful?</legend>

            <div class="ds_field-group">
                <div class="ds_radio  ds_radio--small">
                    <input type="radio" class="ds_radio__input" id="needsmetyes" name="feedbacktype" data-gtm="fdbk-yes" value="yes" data-form="radio-feedbacktype-yes">
                    <label class="ds_radio__label" for="needsmetyes">Yes</label>

                    <div class="ds_reveal-content">
                        <div class="ds_question">
                            <label class="ds_label" for="comments-yes">Your comments</label><br>
                            <textarea rows="5" maxlength="250" class="ds_input" id="comments-yes" data-form="textarea-comments-yes"></textarea>

                            <p><strong>Note:</strong> Your feedback will help us make improvements on this site. Please do not provide any <a class="external" href="https://ico.org.uk/for-organisations/guide-to-data-protection/guide-to-the-general-data-protection-regulation-gdpr/key-definitions/what-is-personal-data/">personal information</a></p>
                        </div>

                        <button type="submit" class="ds_button  ds_no-margin" data-gtm="fdbk-send">Send feedback</button>
                    </div>
                </div>

                <div class="ds_radio  ds_radio--small">
                    <input type="radio" class="ds_radio__input" id="needsmetno" name="feedbacktype" data-gtm="fdbk-no" value="no" data-form="radio-feedbacktype-no">
                    <label class="ds_radio__label" for="needsmetno">No</label>

                    <div class="ds_reveal-content">
                        <div class="ds_question">
                            <label class="ds_label" for="reason-no">Choose a reason for your feedback</label>
                            <div class="ds_select-wrapper">
                                <select id="reason-no" class="ds_select">
                                    <option selected="" disabled="" value="">Please select a reason</option>
                                    <option>It wasn't detailed enough</option>
                                    <option>It's hard to understand</option>
                                    <option>It's incorrect</option>
                                    <option>It needs updating</option>
                                    <option>I'm not sure what I need to do next</option>
                                    <option>There's a broken link</option>
                                    <option>Other</option>
                                </select>
                                <span class="ds_select-arrow" aria-hidden="true"></span>
                            </div>
                        </div>

                        <div class="ds_question">
                            <label class="ds_label" for="comments-no">Your comments</label><br>
                            <textarea rows="5" maxlength="250" class="ds_input" id="comments-no" data-form="textarea-comments-no"></textarea>

                            <p><strong>Note:</strong> Your feedback will help us make improvements on this site. Please do not provide any <a class="external" href="https://ico.org.uk/for-organisations/guide-to-data-protection/guide-to-the-general-data-protection-regulation-gdpr/key-definitions/what-is-personal-data/">personal information</a></p>
                        </div>

                        <button type="submit" class="ds_button  ds_no-margin" data-gtm="fdbk-send">Send feedback</button>
                    </div>
                </div>

                <div class="ds_radio  ds_radio--small">
                    <input type="radio" class="ds_radio__input" id="needsmetyesbut" name="feedbacktype" data-gtm="fdbk-yesbut" value="yesbut" data-form="radio-feedbacktype-yesbut">
                    <label class="ds_radio__label" for="needsmetyesbut">Yes, but</label>

                    <div class="ds_reveal-content">
                        <div class="ds_question">
                            <label class="ds_label" for="reason-no">Choose a reason for your feedback</label>
                            <div class="ds_select-wrapper">
                                <select id="reason-yesbut" class="ds_select">
                                    <option selected="" disabled="" value="">Please select a reason</option>
                                    <option>It's hard to understand</option>
                                    <option>It needs updating</option>
                                    <option>I'm not sure what I need to do next</option>
                                    <option>There's a broken link</option>
                                    <option>There's a spelling mistake</option>
                                    <option>Other</option>
                                </select>
                                <span class="ds_select-arrow" aria-hidden="true"></span>
                            </div>
                        </div>

                        <div class="ds_question">
                            <label class="ds_label" for="comments-yesbut">Your comments</label><br>
                            <textarea rows="5" maxlength="250" class="ds_input" id="comments-yesbut" data-form="textarea-comments-yesbut"></textarea>

                            <p><strong>Note:</strong> Your feedback will help us make improvements on this site. Please do not provide any <a class="external" href="https://ico.org.uk/for-organisations/guide-to-data-protection/guide-to-the-general-data-protection-regulation-gdpr/key-definitions/what-is-personal-data/">personal information</a></p>
                        </div>

                        <button type="submit" class="ds_button  ds_no-margin" data-gtm="fdbk-send">Send feedback</button>
                    </div>
                </div>
            </div>
        </fieldset>
    </form>
</section>
