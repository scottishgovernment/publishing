CKEDITOR.plugins.add('ds_la-finder', {
    requires: 'widget',
    icons: 'ds_la-finder',

    init: function (editor) {
        CKEDITOR.dtd.$removeEmpty.span = false;
        CKEDITOR.dialog.add('dsLAFinderDialog', this.path + 'dialogs/ds_la-finder.js');

        editor.on('beforeCommandExec', function () {
            editor.contentSelectIdString = parseInt(Math.random() * 1000000, 10);
        });

        editor.widgets.add('ds_la-finder', {
            button: 'LA finder',
            pathName: 'finder',
            mask: true,
            dialog: 'dsLAFinderDialog',

            template: () =>
                `<div data-type="button" class="js-contentselect  form-box">
                <label for="content-select-${editor.contentSelectIdString}" class="ds_label">Select your council</label>
                <div class="ds_input--fluid-two-thirds ds_select-wrapper">
                <select for="content-select-${editor.contentSelectIdString}" class="ds_select">
                <option selected="selected" disabled="disabled" value="">Choose your council</option>
                </select>
                <span aria-hidden="true" class="ds_select-arrow">&nbsp;</span>
                </div>
            </div>`,

            allowedContent: {
                'select': {
                    classes: '!ds_select',
                    attributes: 'id'
                },
                'option': {
                    attributes: 'data-id, disabled, selected'
                },
                'span': {
                    attributes: '!aria-hidden',
                    classes: '!ds_select-arrow'
                },
                'a': {
                    attributes: '!href, id, aria-hidden',
                    classes: 'ds_button, ds_button--max, fully-hidden'
                },
                'div': {
                    attributes: 'data-type, data-module',
                    classes: 'js-contentselect, dd, finder-hero, form-box, ds_select-wrapper, ds_input--fluid-two-thirds'
                },
                'label': {
                    attributes: '!for',
                    classes: 'ds_label'
                },
                'dl': {
                    classes: ''
                }
            },

            parts: {
                wrapper: '.js-contentselect',
                title: '.ds_label',
                placeholder: 'option:first-child',
                select: 'select'
            },

            upcast: (element) => {
                return element.name == 'div' && element.hasClass('js-contentselect');
            },

            data: function () {
                if (this.parts.wrapper &&
                    this.parts.title &&
                    this.parts.placeholder &&
                    this.parts.select) {

                    this.parts.title.setText(this.data.title);
                    this.parts.placeholder.setText(this.data.placeholder);

                    if (this.data.replaceLinks) {
                        const oldLinks = this.parts.wrapper.find('a.ds_button').toArray();
                        oldLinks.forEach(oldLink => {
                            oldLink.$.parentNode.removeChild(oldLink.$);
                        });

                        const oldOptions = this.parts.select.find('option').toArray();
                        this.parts.select.setHtml('');
                        this.parts.select.append(oldOptions[0]);

                        if (this.data.links) {
                            this.data.links.forEach(link => {
                                const linkEl = new CKEDITOR.dom.element('a');
                                linkEl.addClass('ds_button');
                                linkEl.addClass('ds_button--max');
                                linkEl.addClass('fully-hidden');
                                linkEl.setAttribute('id', 'dd-' + slugify(link.title));

                                linkEl.setAttribute('href', link.url);
                                linkEl.setText(link.title);
                                this.parts.wrapper.append(linkEl);

                                const optionEl = new CKEDITOR.dom.element('option');
                                optionEl.setText(link.title);
                                optionEl.setAttribute('data-id', slugify(link.title));
                                this.parts.select.append(optionEl);
                            });
                        }

                        this.setData('replaceLinks', false);
                    }
                }
            },

            edit: function () {
                if (this.parts.wrapper &&
                    this.parts.title &&
                    this.parts.placeholder &&
                    this.parts.select) {

                    const linkElements = this.parts.wrapper.find('a.ds_button').toArray();
                    const links = [];
                    linkElements.forEach(link => {
                        links.push({ title: link.$.innerText || '', url: link.$.href || '' });
                    });
                    this.data.links = links;
                }
            },

            init: function () {
                editor.on('doubleclick', function (evt) {
                    var element = evt.data.element;
                    if (element.$.parentNode.classList.contains('cke_widget_ds_la-finder')) {
                        evt.data.dialog = 'dsLAFinderDialog';
                    }
                });

                // setup the data
                const data = {
                    title: !!this.parts.title ? this.parts.title.getText() : '',
                    placeholder: !!this.parts.placeholder ? this.parts.placeholder.getText() : ''
                };
                this.setData(data);
            }
        });
    }
});

function slugify(string = '') {

    string = String(string);

    return string
        // Make lower-case
        .toLowerCase()
        // Remove misc punctuation
        .replace(/['"’‘”“`]/g, '')
        // Replace non-word characters with dashes
        .replace(/[\W|_]+/g, '-')
        // Remove starting and trailing dashes
        .replace(/^-+|-+$/g, '');

}
