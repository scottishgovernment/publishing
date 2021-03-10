CKEDITOR.dialog.add('dsButtonDialog', function (editor) {
    return {
        title: 'Button',
        minWidth: 400,
        minHeight: 200,
        contents: [
            {
                id: 'tab-basic',
                elements: [
                    {
                        type: 'text',
                        id: 'button-text',
                        label: 'Button text',
                        'default': 'Button',
                        validate: CKEDITOR.dialog.validate.notEmpty("Button text field cannot be empty."),
                        setup: function (element, preview) {
                            this.preview_button = preview;
                            this.setValue(element.getText());
                        },
                        commit: function (element) {
                            element.setText(this.getValue());
                        },
                        onChange: function () {
                            this.preview_button.setText(this.getValue());
                        }
                    },
                    {
                        type: 'text',
                        id: 'button-url',
                        label: 'URL',
                        setup: function (element) {
                            this.setValue(element.getAttribute("href"));
                        },
                        commit: function (element) {
                            element.setAttribute("href", this.getValue());
                            element.removeAttribute('data-cke-saved-href');
                        }
                    },
                    {
                        type: 'select',
                        id: 'button-target',
                        label: 'Target',
                        items: [ [ '_self' ], [ '_blank' ] ],
                        'default': '_self',
                        setup: function (element) {
                            this.setValue(element.getAttribute("target"));
                        },
                        commit: function(element) {
                            element.setAttribute("target", this.getValue());
                        }
                    },
                    {
                        type: 'select',
                        id: 'button-type',
                        label: 'Type',
                        items: [ [ 'Primary' ], [ 'Secondary' ], [ 'Cancel' ] ],
                        'default': 'Primary',
                        commit: function (element) {
                            element.$.classList.remove('ds_button--secondary', 'ds_button--cancel');
                            if (this.getValue() === 'Secondary') {
                                element.$.classList.add('ds_button--secondary');
                            }
                            if (this.getValue() === 'Cancel') {
                                element.$.classList.add('ds_button--cancel');
                            }
                        }
                    },
                    {
                        type: 'select',
                        id: 'button-width',
                        label: 'Width',
                        items: [ [ 'Flexible' ], [ 'Fixed' ], [ 'Max' ] ],
                        'default': 'Flexible',
                        commit: function (element) {
                            element.$.classList.remove('ds_button--fixed', 'ds_button--max');
                            if (this.getValue() === 'Fixed') {
                                element.$.classList.add('ds_button--fixed');
                            }
                            if (this.getValue() === 'Max') {
                                element.$.classList.add('ds_button--max');
                            }
                        }
                    }
                ]
            }
        ],

        onShow: function () {
            var selection = editor.getSelection();
            var element = selection.getStartElement();

            if (!element || !element.hasClass('ds_button')) {
                element = editor.document.createElement('a');
                element.setAttribute('class', 'ds_button');
                element.setText('Button');
                this.insertMode = true;
            } else
                this.insertMode = false;

            this.element = element;
        },

        onOk: function () {
            var ds_btn = this.element;
            this.commitContent(ds_btn);

            if (this.insertMode)
                editor.insertElement(ds_btn);
        }
    };
});
