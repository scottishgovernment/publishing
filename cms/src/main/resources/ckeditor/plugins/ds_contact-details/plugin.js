(() => {
    const templates = {
        address: new CKEDITOR.template(`<div class="ds_contact-details__item  ds_contact-details__item--address">
    <dt>Address</dt>
    <dd translate="no">
        <address>
        {address}
        </address>
    </dd>
</div>`),
        email: new CKEDITOR.template(`<div class="ds_contact-details__item  ds_contact-details__item--email">
    <dt>Email</dt>
    <dd><a href="mailto:{email}">{email}</a></dd>
</div>`),
        phone: new CKEDITOR.template(`<div class="ds_contact-details__item  ds_contact-details__item--phone">
    <dt>{title}</dt>
    <dd>
        {data}
        <a href="https://www.gov.uk/call-charges">Find out about call charges</a>
    </dd>
</div>`),
        social: new CKEDITOR.template(`<div class="ds_contact-details__item  ds_contact-details__social">
    <dt class="visually-hidden">Social media</dt>
</div`),
        socialItem: new CKEDITOR.template(`<dd class="ds_contact-details__social-item">
    <a class="ds_contact-details__social-link" href="{url}">
        <svg class="ds_contact-details__social-icon  ds_icon" aria-hidden="true" role="img"><use xlink:href="/assets/images/icons/icons.stack.svg#{icon}"></use></svg>
        {text}
    </a>
</dd>`),
        basic: new CKEDITOR.template(`<div class="ds_contact-details__item  ds_contact-details__item--{type}">
    <dt>{title}</dt>
    <dd>{data}</dd>
</div>`)
    };

    CKEDITOR.plugins.add('ds_contact-details', {
        requires: 'widget',
        icons: 'ds_contact-details',

        init: function (editor) {
            // alert('remember to add the new classes to the design system version and mygov version.');
            const contactDetails = widgetDefinition(editor);
            editor.widgets.add('ds_contact-details', contactDetails);

            CKEDITOR.dialog.add('dsContactDetailsDialog', this.path + 'dialogs/ds_contact-details.js');
        },

        onLoad: function () {
        }
    });

    function widgetDefinition(editor) {
        function deflate() {
            if ( this.deflated )
                return;

            // Remember whether widget was focused before destroyed.
            if ( editor.widgets.focused == this.widget )
                this.focused = true;

            editor.widgets.destroy( this.widget );

            // Mark widget was destroyed.
            this.deflated = true;
        }

        function inflate() {
            var editable = editor.editable(),
            doc = editor.document;

            // Create a new widget. This widget will be either captioned
            // non-captioned, block or inline according to what is the
            // new state of the widget.
            if ( this.deflated ) {
                this.widget = editor.widgets.initOn( this.element, 'ds_contact-details', this.widget.data );

                // Once widget was re-created, it may become an inline element without
                // block wrapper (i.e. when unaligned, end not captioned). Let's do some
                // sort of autoparagraphing here (https://dev.ckeditor.com/ticket/10853).
                if ( this.widget.inline && !( new CKEDITOR.dom.elementPath( this.widget.wrapper, editable ).block ) ) {
                    var block = doc.createElement( editor.activeEnterMode == CKEDITOR.ENTER_P ? 'p' : 'div' );
                    block.replace( this.widget.wrapper );
                    this.widget.wrapper.move( block );
                }

                // The focus must be transferred from the old one (destroyed)
                // to the new one (just created).
                if ( this.focused ) {
                    this.widget.focus();
                    delete this.focused;
                }

                delete this.deflated;
            }

            // If now widget was destroyed just update wrapper's alignment.
            // According to the new state.
            else {
                // setWrapperAlign( this.widget, alignClasses );
            }
        }

        return {
            allowedContent: 'strong(!ds_contact-details__icon); strong(!visually-hidden); div(!ds_contact-details__text)',

            parts: {
                title: '.ds_contact-details__title',
                address: '.ds_contact-details__item--address',
                email: '.ds_contact-details__item--email',
                phone: '.ds_contact-details__item--phone',
                phone2: '.ds_contact-details__item--phone2',
                phone3: '.ds_contact-details__item--phone3',
                social: '.ds_contact-details__social',
                social1: '.ds_contact-details__social-item:nth-of-type(1)',
                social2: '.ds_contact-details__social-item:nth-of-type(2)',
                social3: '.ds_contact-details__social-item:nth-of-type(3)',
                social4: '.ds_contact-details__social-item:nth-of-type(4)',
                social5: '.ds_contact-details__social-item:nth-of-type(5)',
                list: '.ds_contact-details__list',
            },
            dialog: 'dsContactDetailsDialog',
            template: `<div class="ds_contact-details">
                <h2 class="ds_contact-details__title"></h2>
                <dl class="ds_contact-details__list">
                </dl>
            </div>`,
            button: 'Contact details',
            pathName: 'contact',

            data: function () {
                this.data.hasSocial = (this.data.hasSocial1 ||
                    this.data.hasSocial2 ||
                    this.data.hasSocial3 ||
                    this.data.hasSocial4 ||
                    this.data.hasSocial5);

                this.shiftState({
                    widget: this,
                    element: this.element,
                    oldData: this.oldData,
                    newData: this.data,
                    deflate: deflate,
                    inflate: inflate
                });

                // Cache current data.
                this.oldData = CKEDITOR.tools.extend( {}, this.data );
            },

            init: function () {
                const helpers = CKEDITOR.plugins['ds_contact-details'];

                const data = {
                    hasTitle: !!this.parts.title,
                    hasAddress: !!this.parts.address,
                    hasEmail: !!this.parts.email,

                    hasPhone: !!this.parts.phone,

                    hasPhone2: !!this.parts.phone2,
                    hasPhone3: !!this.parts.phone3,

                    hasSocial1: !!this.parts.social1,
                    hasSocial2: !!this.parts.social2,
                    hasSocial3: !!this.parts.social3,
                    hasSocial4: !!this.parts.social4,
                    hasSocial5: !!this.parts.social5
                };

                // get initial dataset
                if (data.hasTitle) {
                    if (this.parts.title.getText() !== '') {
                        data.title = this.parts.title.getText();
                    } else {
                        data.title = 'Contact';
                    }
                }

                if (data.hasAddress) {
                    const addressEl = this.parts.address.find('address').$[0];
                    if (addressEl) {
                        data.address = addressEl.innerText;
                    }
                }
                if (data.hasEmail) {
                    const emailEl = this.parts.email.find('a').$[0];
                    if (emailEl) {
                        data.email = emailEl.innerText
                    }
                }

                if (data.hasPhone) {
                    const number = this.parts.phone.find('[data-part="number"]').$[0];
                }

                if (data.hasPhone2) {
                    const dt = this.parts.phone2.find('dt').$[0];
                    const dd = this.parts.phone2.find('dd').$[0];
                    if (dt) {
                        data.phone2title = dt.innerText;
                    }
                    if (dd) {
                        data.phone2 = dd.innerText;
                    }
                }
                if (data.hasPhone3) {
                    const dt = this.parts.phone3.find('dt').$[0];
                    const dd = this.parts.phone3.find('dd').$[0];
                    if (dt) {
                        data.phone3title = dt.innerText;
                    }
                    if (dd) {
                        data.phone3 = dd.innerText;
                    }
                }

                if (data.hasSocial1) {
                    const a = this.parts.social1.find('a.ds_contact-details__social-link').$[0];
                    if (a) {
                        data.social1Text = a.innerText;
                        data.social1Url = a.href;
                    }
                }
                if (data.hasSocial2) {
                    const a = this.parts.social2.find('a.ds_contact-details__social-link').$[0];
                    if (a) {
                        data.social2Text = a.innerText;
                        data.social2Url = a.href;
                    }
                }
                if (data.hasSocial3) {
                    const a = this.parts.social3.find('a.ds_contact-details__social-link').$[0];
                    if (a) {
                        data.social3Text = a.innerText;
                        data.social3Url = a.href;
                    }
                }
                if (data.hasSocial4) {
                    const a = this.parts.social4.find('a.ds_contact-details__social-link').$[0];
                    if (a) {
                        data.social4Text = a.innerText;
                        data.social4Url = a.href;
                    }
                }
                if (data.hasSocial5) {
                    const a = this.parts.social5.find('a.ds_contact-details__social-link').$[0];
                    if (a) {
                        data.social5Text = a.innerText;
                        data.social5Url = a.href;
                    }
                }

                this.setData(data);

                this.shiftState = helpers.stateShifter(this.editor);
            },

            upcast: function (element) {
                return element.name == 'div' && element.hasClass('ds_contact-details');
            }
        }
    }

    CKEDITOR.plugins['ds_contact-details'] = {
        stateShifter: function (editor) {
            // this determines the order of elements
            const shiftables = ['hasTitle', 'hasAddress', 'hasEmail', 'hasPhone', 'hasPhone2', 'hasPhone3', 'hasSocial'];

            const stateActions = {
                hasTitle: function (shift, oldValue, newValue) {
                    if (newValue && shift.newData.title !== undefined) {
                        shift.widget.parts.title.setHtml(shift.newData.title);
                    } else {
                        shift.widget.data.title = 'Contact'
                        shift.widget.parts.title.setHtml(shift.widget.data.title);
                    }
                },

                hasAddress: function (shift, oldValue, newValue) {
                    if (newValue) {
                        const address = CKEDITOR.dom.element.createFromHtml(templates.address.output({
                            address: shift.newData.address.replace(/\n/g, '<br>')
                        }));

                        shift.frag.append(address);
                    }
                },

                hasEmail: function (shift, oldValue, newValue) {
                    if (newValue) {
                        const email = CKEDITOR.dom.element.createFromHtml(templates.email.output({
                            email: shift.newData.email
                        }));

                        shift.frag.append(email);
                    }
                },

                hasPhone: function (shift, oldValue, newValue) {
                    if (newValue) {
                        const contentFrag = new CKEDITOR.dom.documentFragment();
                        if (shift.newData.phone) {
                            contentFrag.append(new CKEDITOR.dom.text(shift.newData.phone));
                        }
                        if (shift.newData.phoneTimes) {
                            contentFrag.append(new CKEDITOR.dom.element('br'));
                            contentFrag.append(new CKEDITOR.dom.text(shift.newData.phoneTimes));
                        }

                        const phone = CKEDITOR.dom.element.createFromHtml(templates.basic.output({
                            title: 'Phone',
                            data: contentFrag.getHtml(),
                            type: 'phone'
                        }));

                        shift.frag.append(phone);
                    }
                },

                hasPhone2: function (shift, oldValue, newValue) {
                    if (newValue) {
                        const phone = CKEDITOR.dom.element.createFromHtml(templates.basic.output({
                            title: shift.newData.phone2title,
                            data: shift.newData.phone2,
                            type: 'phone2'
                        }));

                        shift.frag.append(phone);
                    }
                },

                hasPhone3: function (shift, oldValue, newValue) {
                    if (newValue) {
                        const phone = CKEDITOR.dom.element.createFromHtml(templates.basic.output({
                            title: shift.newData.phone3title,
                            data: shift.newData.phone3,
                            type: 'phone3'
                        }));

                        shift.frag.append(phone);
                    }
                },

                hasSocial: function (shift, oldValue, newValue) {
                    if (newValue) {
                        const social = CKEDITOR.dom.element.createFromHtml(templates.social.output());

                        if (shift.newData.social1Url) {
                            const social1 = CKEDITOR.dom.element.createFromHtml(templates.socialItem.output({
                                text: shift.newData.social1Text || shift.newData.social1Url,
                                url: shift.newData.social1Url
                            }));
                            social.append(social1);
                        }

                        if (shift.newData.social2Url) {
                            const social2 = CKEDITOR.dom.element.createFromHtml(templates.socialItem.output({
                                text: shift.newData.social2Text || shift.newData.social1Url,
                                url: shift.newData.social2Url
                            }));
                            social.append(social2);
                        }

                        if (shift.newData.social3Url) {
                            const social3 = CKEDITOR.dom.element.createFromHtml(templates.socialItem.output({
                                text: shift.newData.social3Text || shift.newData.social1Url,
                                url: shift.newData.social3Url
                            }));
                            social.append(social3);
                        }

                        if (shift.newData.social4Url) {
                            const social4 = CKEDITOR.dom.element.createFromHtml(templates.socialItem.output({
                                text: shift.newData.social4Text || shift.newData.social1Url,
                                url: shift.newData.social4Url
                            }));
                            social.append(social4);
                        }

                        if (shift.newData.social5Url) {
                            const social5 = CKEDITOR.dom.element.createFromHtml(templates.socialItem.output({
                                text: shift.newData.social5Text || shift.newData.social5Url,
                                url: shift.newData.social5Url
                            }));
                            social.append(social5);
                        }

                        shift.frag.append(social);


                    }
                }
            };

            return function (shift) {
                var name, i;

                shift.changed = {};

                shift.frag = new CKEDITOR.dom.documentFragment();

                for ( i = 0; i < shiftables.length; i++ ) {
                    name = shiftables[ i ];

                    shift.changed[ name ] = shift.oldData ?
                        shift.oldData[ name ] !== shift.newData[ name ] : false;
                }

                // Iterate over possible state variables.
                for ( i = 0; i < shiftables.length; i++ ) {
                    name = shiftables[ i ];

                    stateActions[ name ]( shift,
                        shift.oldData ? shift.oldData[ name ] : null,
                        shift.newData[ name ] );
                }

                shift.widget.parts.list.setHtml(shift.frag.getHtml());

                shift.inflate();
            };
        }
    };
})();
