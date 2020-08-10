// HOUSING TEMPLATES

'use strict';

const HousingTemplates = {

    subsectionNav: '{{#unless hideSubsectionNav}}' +
        '<nav aria-label="subsections in this section" class="ds_side-navigation" data-module="ds-side-navigation">' +

            '<input type="checkbox" class="fully-hidden  js-toggle-side-navigation" id="show-side-navigation" aria-controls="side-navigation-root">' +
            '<label class="ds_side-navigation__expand  ds_link" for="show-side-navigation"><span class="visually-hidden">Show all</span> Pages in this section <span class="ds_side-navigation__expand-indicator"></span></label>' +

            '<ol class="ds_side-navigation__list  js-contents">' +
                '{{#pages}}' +
                    '{{#unless hidden}}' +
                        '<li class="ds_side-navigation__item">' +
                            '{{#if excluded}}' +
                                '{{#if current}}' +
                                '<span class="ds_side-navigation__link"  style="color: red !important;">' +
                                    '{{title}}' +
                                '</span>' +
                                '{{else}}' +
                                '<a href="#!/{{section}}/{{slug}}/" class="ds_side-navigation__link"  style="color: red !important;">' +
                                    '{{title}}' +
                                '</a>' +
                                '{{/if}}' +
                            '{{else}}' +
                                '{{#if current}}' +
                                '<span class="ds_side-navigation__link">{{title}}</span>' +
                                '{{else}}' +
                                '<a href="#!/{{section}}/{{slug}}/" class="ds_side-navigation__link">' +
                                    '{{title}}' +
                                '</a>' +
                                '{{/if}}' +
                            '{{/if}}' +
                        '</li>' +
                    '{{/unless}}' +
                '{{/pages}}' +
            '</ol>' +
        '</nav>' +
    '{{/unless}}',

    confirmDefaultModal: function(){
        return `<div class="modal">
                <div class="modal__overlay"></div>
                <div class="modal__dialog">

                    <a href="#" id="js-modal-close" class="modal__close" ds_button  ds_button--icon-only  ds_button--small  ds_button--cancel">
                        <span class="visually-hidden">Close</span>
                        <svg class="ds_icon" aria-hidden="true" role="img"><use xlink:href="/assets/images/icons/icons.stack.svg#close-21"></use></svg>
                    </a>

                    <h2 class="modal__title">Reset to recommended text?</h2>
                    <p class="modal__body">Note: You will lose all of your custom text and will revert to the recommended term provided.</p>
                    <button class="ds_no-margin  ds_button  ds_button--small" id="js-modal-continue">Continue</button>
                    <button class="ds_no-margin  ds_button--cancel  ds_button  ds_button--small" id="js-modal-cancel">Cancel</button>
                </div>
            </div>`;
    },

    tenantHtml: `<section data-group="{{slug}}s" data-step="{{slug}}-{{index}}" id="{{slug}}-{{index}}" class="form-step  fully-hidden">
            <h2>{{stepTitle}} details: <span class="js-dynamic-title"></span></h2>
            <a class="js-remove-repeating-section  repeating-container__remove">Remove this {{slug}}</a>

            <div class="ds_question">
                <label class="ds_label" for="{{slug}}-{{index}}-name">{{#if requiredName}}<span class="form--required"><span>star</span></span>{{/if}} Full name</label>
                <input {{#if requiredName}}data-validation="requiredField" aria-required="true"{{/if}} type="text" id="{{slug}}-{{index}}-name" class="js-dynamic-title-input  ds_input" data-form="textinput-{{slug}}-{{index}}-name">
            </div>

            {{#unless hide_email}}
            <div class="ds_question">
                <label class="ds_label" for="{{slug}}-{{index}}-email">Email</label>
                <input data-validation="validEmail" type="text" id="{{slug}}-{{index}}-email" class="ds_input" data-form="textinput-{{slug}}-{{index}}-email">
            </div>
            {{/unless}}

            {{#unless hide_phone}}
            <div class="ds_question">
                <label class="ds_label" for="{{slug}}-{{index}}-phone">Phone number</label>
                <input data-validation="validPhone" type="text" id="{{slug}}-{{index}}-phone" class="ds_input  ds_input--fixed-20" data-form="textinput-{{slug}}-{{index}}-phone">
            </div>
            {{/unless}}

            {{#unless hide_address}}
            <div id="{{slug}}-{{index}}-postcode-lookup" class="js-postcode-lookup">
                <div class="form-group">

                    <h3 class="beta">{{stepTitle}} address</h3>
                    <div class="ds_question">
                        <label class="ds_label" for="{{slug}}-{{index}}-postcode-search">Postcode</label>

                        <div class="ds_input__wrapper">
                            <input class="ds_input  ds_input--fixed-10  postcode-search" {{#if addressrequired}}data-validation="requiredPostcodeLookup"{{/if}} type="text" id="{{slug}}-{{index}}-postcode-search" data-form="textinput-{{slug}}-{{index}}-postcode-search">
                            <button class="js-find-address-button  ds_button">Find address</button>
                        </div>
                    </div>

                    <div class="ds_question  postcode-results  fully-hidden">
                        <label for="{{slug}}-{{index}}-postcode-results" class="ds_label">Select an address</label>

                        <div class="ds_select-wrapper">
                            <select class="ds_select" id="{{slug}}-{{index}}-postcode-results" {{#if addressrequired}}data-validation="requiredDropdown"{{/if}}></select>
                            <span class="ds_select-arrow" aria-hidden="true"></span>
                        </div>
                    </div>

                    <div class="postcode-info-note  ds_inset-text  fully-hidden"></div>

                    <textarea class="address-display  ds_input  fully-hidden" id="rpz-property-address" disabled="true" title="Property address" data-form="textarea-rpz-property-address"></textarea>

                    <p>
                        <a href="#" class="js-address-manual-link">Enter the address manually</a>
                    </p>

                    <div class="address-manual  fully-hidden">
                        {{#if addressrequired}}<div class="ds_question" data-validation="requiredBuildingOrStreet" id="tenant-{{index}}-building-street">{{/if}}
                            <label class="ds_label" for="{{slug}}-{{index}}-address-building">Building</label>
                            <input type="text" id="{{slug}}-{{index}}-address-building" class="ds_input  building" data-form="textinput-{{slug}}-{{index}}-address-building">

                            <label class="ds_label" for="{{slug}}-{{index}}-address-street">Street address</label>
                            <input type="text" id="{{slug}}-{{index}}-address-street" class="ds_input  street" data-form="textinput-{{slug}}-{{index}}-address-street">
                        {{#if addressrequired}}</div>{{/if}}

                        <div class="ds_question">
                            <label class="ds_label" for="{{slug}}-{{index}}-address-town">Town or city</label>
                            <input type="text" id="{{slug}}-{{index}}-address-town" class="ds_input town" {{#if addressrequired}}data-validation="requiredField"{{/if}} data-form="textinput-{{slug}}-{{index}}-address-town">
                        </div>

                        <div class="ds_question">
                            <label class="ds_label" for="{{slug}}-{{index}}-address-region">Region or province</label>
                            <input type="text" id="{{slug}}-{{index}}-address-region" class="ds_input region" data-form="textinput-{{slug}}-{{index}}-address-region">
                        </div>

                        <div class="ds_question">
                            <label class="ds_label" for="{{slug}}-{{index}}-postcode">Postcode</label>
                            <input type="text" id="{{slug}}-{{index}}-postcode" class="ds_input uppercase input--small postcode" data-form="textinput-{{slug}}-{{index}}-postcode">
                        </div>
                    </div>
                </div>
            </div>
            {{/unless}}

            {{#if guarantor}}

                <div class="ds_question">
                    <h3 class="beta">Guarantor</h3>
                    <fieldset id="guarantor-{{index}}-query">
                        <legend>
                        Does this tenant have a guarantor?
                        </legend>
                        <div class="ds_field-group">
                            <div class="ds_radio">
                                <input id="guarantor-{{index}}-yes" name="guarantor-{{index}}-query" value="guarantorYes" class="ds_radio__input" type="radio" data-form="radio-guarantor-{{index}}-query-guarantorYes">\n
                                <label for="guarantor-{{index}}-yes" class="ds_radio__label">Yes</label>

                                <div class="ds_reveal-content">
                                    <div class="ds_question">
                                        <label class="ds_label" for="guarantor-{{index}}-name">Guarantor full name</label>
                                        <input type="text" id="guarantor-{{index}}-name" class="ds_input" data-form="textinput-guarantor-{{index}}-name">
                                    </div>

                                    <div id="guarantor-{{index}}-postcode-lookup" class="js-postcode-lookup">
                                        <div class="form-group">
                                            <div class="ds_question">
                                                <h3 class="beta">Guarantor address</h3>
                                                <label class="ds_label" for="guarantor-{{index}}-postcode-search">Postcode (UK only)</label>
                                                <div class="ds_input__wrapper">
                                                    <input type="text" id="guarantor-{{index}}-postcode-search" class="ds_input  ds_input--fixed-10  postcode-search" data-form="textinput-guarantor-{{index}}-postcode-search">

                                                    <button class="js-find-address-button  ds_button">Find address</button>
                                                </div>

                                                <p>
                                                    <a href="#" class="js-address-manual-link">Enter the address manually</a>
                                                </p>
                                            </div>

                                            <div class="ds_question  postcode-results  fully-hidden">
                                                <label class="ds_label" for="guarantor-{{index}}-postcode-results">Please select your address</label>
                                                <div class="ds_select-wrapper">
                                                    <select class="ds_select" id="guarantor-{{index}}-postcode-results"><option disabled="" selected="">Select an address</option>
                                                    </select>
                                                    <span class="ds_select-arrow" aria-hidden="true"></span>
                                                </div>
                                            </div>

                                            <div class="postcode-info-note  ds_inset-text  fully-hidden"></div>

                                            <div class="address-manual  fully-hidden">
                                                <div class="ds_question">
                                                    <label class="ds_label" for="guarantor-{{index}}-building">Building</label>
                                                    <input type="text" id="guarantor-{{index}}-address-building" class="ds_input  building" data-form="textinput-guarantor-{{index}}-address-building">

                                                    <label class="ds_label" for="guarantor-{{index}}-street">Street address</label>
                                                    <input type="text" id="guarantor-{{index}}-address-street" class="ds_input  street" data-form="textinput-guarantor-{{index}}-address-street">
                                                </div>

                                                <div class="ds_question">
                                                    <label class="ds_label" for="guarantor-{{index}}-address-town">Town or city</label>
                                                    <input type="text" id="guarantor-{{index}}-address-town" class="ds_input town" data-form="textinput-guarantor-{{index}}-address-town">
                                                </div>

                                                <div class="ds_question">
                                                    <label class="ds_label" for="guarantor-{{index}}-region">Region or province</label>
                                                    <input type="text" id="guarantor-{{index}}-address-region" class="ds_input region" data-form="textinput-guarantor-{{index}}-address-region">
                                                </div>

                                                <div class="ds_question">
                                                    <label class="ds_label" for="guarantor-{{index}}-postcode">Postcode</label>
                                                    <input type="text" id="guarantor-{{index}}-postcode" class="ds_input ds_input--fixed-10 postcode" data-form="textinput-guarantor-{{index}}-postcode">
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>

                            <div class="ds_radio">
                                <input id="guarantor-{{index}}-no" name="guarantor-{{index}}-query" value="guarantorNo" class="ds_radio__input" type="radio" data-form="radio-guarantor-{{index}}-query-guarantorNo">\n
                                <label for="guarantor-{{index}}-no" class="ds_radio__label">No</label>
                            </div>
                        </div>
                    </fieldset>
                </div>
            {{/if}}
          </section>`,

    landlordHtml: `<section data-group="landlords" data-step="landlord-{{index}}" id="landlord-{{index}}" class="fully-hidden  form-step">
            <h2>Landlord: <span class="js-dynamic-title"></span></h2>
            <a class="js-remove-repeating-section  repeating-container__remove">Remove this landlord</a>

            {{#if injectedContent}}<p>{{injectedContent}}</p>{{/if}}

            <div class="form-group">
                <div class="ds_question">
                    <label class="ds_label" for="landlord-{{index}}-name">Full name {{#if requiredName}}(required){{/if}}</label>
                    <input {{#if requiredName}}data-validation="requiredField" aria-required="true"{{/if}} type="text" id="landlord-{{index}}-name" class="js-dynamic-title-input  ds_input" data-form="textinput-landlord-{{index}}-name">
                </div>

                {{#unless hide_registrationNumber}}
                <div class="ds_question">
                    <label class="ds_label" for="landlord-{{index}}-registration">Landlord registration number</label>
                    <div class="ds_hint-text">
                        <p><a href="https://www.mygov.scot/renting-your-property-out/registration/" target="_blank">Find out more if you don't have a landlord registration number.</a></p>
                        <p>If you have applied for Landlord Registration and are waiting to receive your registration number leave this field blank.</p>
                        <p>Landlord registration numbers use the format 123456/123/12345.</p>
                    </div>
                    <input data-validation="regexMatch" data-errormessage="Landlord registration number is not in a recognised format (e.g. 123456/123/12345)" pattern="^\\d{5,7}[-/ ]\\d{3}[-/ ]\\d{5}$" type="text" id="landlord-{{index}}-registration" class="ds_input" data-form="textinput-landlord-{{index}}-registration">
                </div>
                {{/unless}}

                {{#unless hide_email}}
                <div class="ds_question">
                <label class="ds_label" for="landlord-{{index}}-email">Email</label>
                <input data-validation="validEmail" type="text" id="landlord-{{index}}-email" class="ds_input" data-form="textinput-landlord={{index}}-email">
                </div>
                {{/unless}}


                {{#unless hide_phone}}
                <div class="ds_question">
                <label class="ds_label" for="landlord-{{index}}-phone">Phone number</label>
                <input data-validation="validPhone" type="text" id="landlord-{{index}}-phone" class="ds_input  ds_input--fixed-20" data-form="textinput-landlord={{index}}-phone">
                </div>
                {{/unless}}
            </div>

            <div id="landlord-{{index}}-postcode-lookup" class="js-postcode-lookup">
                <div class="form-group">
                    <div class="ds_question">
                        <h3 class="beta">Landlord address</h3>
                        <label class="ds_label" for="landlord-{{index}}-postcode-search">Postcode (UK only)</label>
                        <div class="ds_input__wrapper">
                            <input {{#if addressrequired}}data-validation="requiredPostcodeLookup"{{/if}} type="text" id="landlord-{{index}}-postcode-search" class="ds_input  ds_input--fixed-10  postcode-search" tada-form="landlord-{{index}}-postcode-search">\n
                            <button class="js-find-address-button  ds_button">Find address</button>
                        </div>
                        <p>
                            <a href="#" class="js-address-manual-link">Enter the address manually</a>
                        </p>
                    </div>

                    <div class="ds_question  postcode-results  fully-hidden">
                        <label class="ds_label" for="landlord-{{index}}-postcode-results">Please select your address</label>
                        <div class="ds_select-wrapper">
                            <select class="ds_select" id="landlord-{{index}}-postcode-results" {{#if addressrequired}}data-validation="requiredDropdown"{{/if}}>
                            </select>
                            <span class="ds_select-arrow" aria-hidden="true"></span>
                        </div>
                    </div>

                    <div class="postcode-info-note  ds_inset-text  fully-hidden"></div>

                    <div class="address-manual  fully-hidden">
                        <div class="ds_question" {{#if addressrequired}}data-validation="requiredBuildingOrStreet" id="landlord-{{index}}-building-street"{{/if}}>
                            <label class="ds_label" for="landlord-{{index}}-building">Building</label>
                            <input type="text" id="landlord-{{index}}-address-building" class="ds_input  building" data-form="textinput-landlord-{{index}}-address-building">

                            <label class="ds_label" for="landlord-{{index}}-street">Street address</label>
                            <input type="text" id="landlord-{{index}}-address-street" class="ds_input  street" data-form="textinput-landlord-{{index}}-address-street">
                        </div>

                        <div class="ds_question">
                        <label class="ds_label" for="landlord-{{index}}-address-town">Town or city</label>
                        <input type="text" id="landlord-{{index}}-address-town" class="ds_input town"  {{#if addressrequired}}data-validation="requiredField"{{/if}} data-form="textinput-landlord-{{index}}-address-town">
                        </div>

                        <div class="ds_question">
                        <label class="ds_label" for="landlord-{{index}}-region">Region or province</label>
                        <input type="text" id="landlord-{{index}}-address-region" class="ds_input region" data-form="textinput-landlord-{{index}}-address-region">
                        </div>

                        <div class="ds_question">
                        <label class="ds_label" for="landlord-{{index}}-postcode">Postcode</label>
                        <input type="text" id="landlord-{{index}}-postcode" class="ds_input ds_input--fixed-10 postcode" data-form="textinput-landlord-{{index}}-postcode">
                        </div>
                    </div>
                </div>
          </section>`,

    landlordHtmlWithTelephone: `<section data-group="landlords" data-step="landlord-{{index}}" id="landlord-{{index}}" class="fully-hidden  form-step">
            <h2>Landlord: <span class="js-dynamic-title"></span></h2>
            <a class="js-remove-repeating-section  repeating-container__remove">Remove this landlord</a>

            {{#if injectedContent}}<p>{{injectedContent}}</p>{{/if}}

          <div class="form-group">
                <div class="ds_question">
                    <label class="ds_label" for="landlord-{{index}}-name">Full name {{#if requiredName}}(required){{/if}}</label>
                    <input {{#if requiredName}}data-validation="requiredField" aria-required="true"{{/if}} type="text" id="landlord-{{index}}-name" class="js-dynamic-title-input  ds_input" data-form="textinput-landlord-{{index}}-name">
                </div>

                {{#unless hide_registrationNumber}}
                <div class="ds_question">
                    <label class="ds_label" for="landlord-{{index}}-registration">Landlord registration number</label>
                    <p class="small"><a href="https://www.mygov.scot/renting-your-property-out/registration/" target="_blank">Find out more if you don't have a landlord registration number.</a></p>
                    <p class="small">If you have applied for Landlord Registration and are waiting to receive your registration number leave this field blank.</p>
                    <p class="small">Landlord registration numbers use the format 123456/123/12345.</p>
                    <input data-validation="regexMatch" data-errormessage="Landlord registration number is not in a recognised format (e.g. 123456/123/12345)" pattern="^\\d{5,7}[-/ ]\\d{3}[-/ ]\\d{5}$" type="text" id="landlord-{{index}}-registration" class="ds_input" data-form="textinput-landlord-{{index}}-registration">
                </div>
                {{/unless}}

                {{#unless hide_email}}
                <div class="ds_question">
                <label class="ds_label" for="landlord-{{index}}-email">Email</label>
                <input data-validation="validEmail" type="text" id="landlord-{{index}}-email" class="ds_input" data-form="textinput-landlord-{{index}}-email">
                </div>
                {{/unless}}


                {{#unless hide_phone}}
                <div class="ds_question">
                <label class="ds_label" for="landlord-{{index}}-phone">Phone number</label>
                <input data-validation="validPhone" type="text" id="landlord-{{index}}-phone" class="ds_input  ds_input--fixed-20" data-form="textinput-landlord-{{index}}-phone">
                </div>
                {{/unless}}
            </div>

            <div id="landlord-{{index}}-postcode-lookup" class="js-postcode-lookup">
                <div class="form-group">
                    <div class="ds_question">
                        <h3 class="beta">Landlord address</h3>
                        <label class="ds_label" for="landlord-{{index}}-postcode-search">Postcode (UK only)</label>
                        <div class="ds_input__wrapper">
                            <input {{#if addressrequired}}data-validation="requiredPostcodeLookup"{{/if}} type="text" id="landlord-{{index}}-postcode-search" class="ds_input  ds_input--fixed-10  postcode-search" data-form="textinput-landlord-{{index}}-postcode-search">\n
                            <button class="js-find-address-button  ds_button">Find address</button>
                        </div>
                    </div>

                    <div class="ds_question  postcode-results  fully-hidden">
                        <label class="ds_label" for="landlord-{{index}}-postcode-results">Please select your address</label>
                        <div class="ds_select-wrapper">
                            <select class="ds_select" id="landlord-{{index}}-postcode-results" {{#if addressrequired}}data-validation="requiredDropdown"{{/if}}>
                            </select>
                            <span class="ds_select-arrow" aria-hidden="true"></span>
                        </div>
                    </div>

                    <div class="postcode-info-note  ds_inset-text  fully-hidden"></div>
                    <p>
                        <a href="#" class="js-address-manual-link">Enter the address manually</a>
                    </p>

                    <div class="address-manual  fully-hidden">
                        <div class="ds_question" {{#if addressrequired}}data-validation="requiredBuildingOrStreet" id="landlord-{{index}}-building-street"{{/if}}>
                            <label class="ds_label" for="landlord-{{index}}-building">Building</label>
                            <input type="text" id="landlord-{{index}}-address-building" class="ds_input  building" data-form="textinput-landlord-{{index}}-address-building">

                            <label class="ds_label" for="landlord-{{index}}-street">Street address</label>
                            <input type="text" id="landlord-{{index}}-address-street" class="ds_input  street" data-form="textinput-landlord-{{index}}-address-street">
                        </div>

                        <div class="ds_question">
                            <label class="ds_label" for="landlord-{{index}}-address-town">Town or city</label>
                            <input type="text" id="landlord-{{index}}-address-town" class="ds_input town"  {{#if addressrequired}}data-validation="requiredField"{{/if}} data-form="textinput-landlord-{{index}}-address-town">
                        </div>

                        <div class="ds_question">
                            <label class="ds_label" for="landlord-{{index}}-region">Region or province</label>
                            <input type="text" id="landlord-{{index}}-address-region" class="ds_input region" data-form="textinput-landlord-{{index}}-address-region">
                        </div>

                        <div class="ds_question">
                            <label class="ds_label" for="landlord-{{index}}-postcode">Postcode</label>
                            <input type="text" id="landlord-{{index}}-postcode" class="ds_input  ds_input--fixed-10" data-form="textinput-landlord-{{index}}-postcode">
                        </div>
                    </div>

                    <div class="ds_question">
                        <label class="ds_label" for="landlord-{{index}}-phone">Phone number</label>
                        <input data-validation="validPhone" type="text" id="landlord-{{index}}-phone" class="ds_input  ds_input--fixed-20" data-form="textinput-landlord-{{index}}-phone">
                    </div>
                </div>
          </section>`,

    additionalTermHtml: `<section data-group="additional-terms" data-step="additional-term-{{index}}" id="additional-term-{{index}}" class="fully-hidden form-step">
            <h2>Additional terms</h2>
            <a class="js-remove-repeating-section  repeating-container__remove">Remove this term</a>

            <p>Enter any additional terms you would like to have in your tenancy agreement.</p>

            <div class="ds_question">
                <label class="ds_label" for="additional-term-{{index}}-title">Additional term title</label>
                <input type="text" id="additional-term-{{index}}-title" class="js-dynamic-title-input  ds_input" data-form="textinput-additional-term-{{index}}-title">
            </div>

            <div class="ds_question">
                <label class="ds_label" for="additional-term-{{index}}-content">Additional term text</label>
                <textarea id="additional-term-{{index}}-content" class="ds_input" rows="10" data-form="textarea-additional-term-{{index}}-content"></textarea>
            </div>
            </section>`,

    landlordFTTHtml: `<section data-group="landlords" data-step="landlord-{{index}}" id="landlord-{{index}}" class="fully-hidden form-step">
    <h2>Landlord: <span class="js-dynamic-title"></span></h2>
    <a class="js-remove-repeating-section  repeating-container__remove">Remove this landlord</a>

    <div class="ds_question">
        <label class="ds_label" for="landlord-{{index}}-name">Full name</label>
        <input type="text" id="landlord-{{index}}-name" class="js-dynamic-title-input ds_input" data-validation="requiredField">
    </div>

    <div id="landlord-{{index}}-postcode-lookup" >
        <div class="ds_question  js-postcode-lookup">
            <label class="ds_label" for="landlord-{{index}}-postcode-search">Postcode (UK only)</label>

            <div class="ds_input__wrapper">
                <input type="text" id="landlord-{{index}}-postcode-search" class="ds_input  ds_input--fixed-10  postcode-search" data-validation="requiredPostcodeLookup">
                <button class="js-find-address-button  ds_button">Find address</button>
            </div>

            <p>
                <a href="#landlord-{{index}}-manual-address" class="js-address-manual-link">Enter the address manually</a>
            </p>
        </div>

        <div class="postcode-results fully-hidden">
            <label class="ds_label" for="landlord-{{index}}-postcode-results">Please select your address</label>

            <div class="ds_select-wrapper">
                <select class="ds_select" id="landlord-{{index}}-postcode-results" data-validation="requiredDropdown"></select>
                </select>
                <span class="ds_select-arrow" aria-hidden="true"></span>
            </div>
        </div>

        <div class="postcode-info-note ds_inset-text fully-hidden"></div>

        <div id="landlord-{{index}}-manual-address" class="address-manual  fully-hidden">
            <div class="ds_question  no-validate" data-validation="requiredBuildingOrStreet" id="landlord-{{index}}-building-street">
                <label class="ds_label" for="landlord-{{index}}-address-building">Building</label>
                <input type="text" id="landlord-{{index}}-address-building" class="ds_input  building" data-form="textinput-{{for}}-address-building">

                <label class="ds_label" for="landlord-{{index}}-address-street">Street address</label>
                <input type="text" id="landlord-{{index}}-address-street" class="ds_input  street" data-form="textinput-{{for}}-address-street">
            </div>

            <div class="ds_question">
                <label class="ds_label" for="landlord-{{index}}-address-town">Town or city</label>
                <input type="text" id="landlord-{{index}}-address-town" class="ds_input  town {{#if required}}no-validate" data-validation="requiredField{{/if}}" data-form="textinput-{{for}}-address-town">
            </div>

            <div class="ds_question">
                <label class="ds_label" for="landlord-{{index}}-address-region">Region or province</label>
                <input type="text" id="landlord-{{index}}-address-region" class="ds_input  region" data-form="textinput-{for}}-address-region">
            </div>

            <div class="ds_question">
                <label class="ds_label" for="landlord-{{index}}-postcode">Postcode</label>
                <input type="text" id="landlord-{{index}}-postcode" class="ds_input  uppercase  ds_input--fixed-10  postcode" data-form="textinput-{{for}}-postcode">
            </div>
        </div>

        <div class="ds_inset-text">
            <div class="ds_inset-text__text">
                If you don't know your landlord's address, you might be able to find it on the <a href="https://www.landlordregistrationscotland.gov.uk/" target="_blank">Scottish Landlord Register (opens in a new window)</a>
            </div>
        </div>
    </div>
</section>`

};

export default HousingTemplates;
