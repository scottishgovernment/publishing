(() => {
    CKEDITOR.dialog.add('dsLAFinderDialog', function (editor) {
        return {
            title: 'LA Finder',
            minWidth: 500,
            minHeight: 200,
            contents: [
                {
                    id: 'basic',
                    label: 'Basic',
                    elements: [
                        {
                            type: 'text',
                            id: 'title',
                            label: 'Title',
                            validate: CKEDITOR.dialog.validate.notEmpty('Title cannot be empty.'),
                            setup: function (widget) {
                                this.setValue(widget.data.title);
                            },
                            commit: function (widget) {
                                widget.setData('title', this.getValue());
                            }
                        },
                        {
                            type: 'text',
                            id: 'placeholder',
                            label: 'First option (placeholder)',
                            setup: function (widget) {
                                this.setValue(widget.data.placeholder);
                            },
                            commit: function (widget) {
                                widget.setData('placeholder', this.getValue());
                            }
                        },
                        {
                            type: 'html',
                            html: `<div>
                                <h2 id="links-table-title">Links</h2>
                                <table id="links-table">
                                    <thead><tr><th>Title</th><th>URL</th><th></th></tr></thead>
                                    <tbody></tbody>
                                </table>
                                <a class="js-add-link  cke_dialog_ui_button">Add link</a>
                                </div>`,
                            setup: function (widget) {
                                if (widget.data.links) {
                                    //renderList(widget.data.links);
                                    window.laFinderDialog.list.items = widget.data.links;
                                    if (window.laFinderDialog.list.items.length === 0) {
                                        window.laFinderDialog.list.addItem();
                                    }
                                    window.laFinderDialog.list.render();
                                }
                            },

                            commit: function (widget) {
                                widget.setData('replaceLinks', true);
                                widget.setData('links', window.laFinderDialog.list.items);
                            }
                        }
                    ]
                }
            ],

            onShow: function () {
                var selection = editor.getSelection();
                var element = selection.getStartElement();

                this.element = element;
                this.setupContent(this.element);

                window.laFinderDialog.list = new List(document.querySelector('#links-table'));
            },

            onOk: function () {
                window.laFinderDialog.list.sanitiseItems();
            },

            onLoad: function () {

                window.laFinderDialog = this;

                const addLink = document.querySelector('.js-add-link');

                if (!addLink.classList.contains('js-event-added')) {
                    addLink.classList.add('js-event-added');
                    document.querySelector('.js-add-link').addEventListener('click', (event) => {
                        window.laFinderDialog.list.addItem();
                        window.laFinderDialog.list.render();
                    });
                }
            }
        };
    });

    function List(element) {
        this.element = element;
        this.items = [];

        this.addItem = function (item = {}) {
            this.items.push(item);
        };

        this.removeItem = function (index) {
            this.items.splice(index, 1);
            this.render();
        };

        this.moveItem = function (index, shift) {
            function array_move(arr, old_index, new_index) {
                if (new_index >= arr.length) {
                    var k = new_index - arr.length + 1;
                    while (k--) {
                        arr.push(undefined);
                    }
                }
                arr.splice(new_index, 0, arr.splice(old_index, 1)[0]);
            }

            array_move(this.items, index, index + shift);
            this.render();
        };

        this.updateItem = function (data, index) {
            this.items[index] = data;
        };

        this.removeEmptyItems = function () {
            const indicesToRemove = [];

            this.items.forEach((item, index) => {
                if (!item.title && !item.url) {
                    indicesToRemove.push(index);
                }
            });

            indicesToRemove.reverse();

            for (let i = 0; i < indicesToRemove.length; i++) {
                this.items.splice(indicesToRemove[i], 1);
            }

            this.render();
        };

        this.sanitiseItems = function () {
            // crude, but remove invalid items with no warning!
            for (let i = 0; i < this.items.length; i++) {
                let item = this.items[i];

                if (!item.title || item.title.trim() === '' || !item.url || item.url.trim() === '') {
                    item = {};
                }
            }

            this.removeEmptyItems();
        };

        this.render = function () {
            this.element.querySelector('tbody').innerHTML = '';

            this.items.forEach((item, index) => {
                const itemElement = document.createElement('tr');
                itemElement.dataset.index = index;

                itemElement.innerHTML = itemTemplate(item, index, this.items.length);
                this.element.querySelector('tbody').appendChild(itemElement);

                itemElement.addEventListener('click', event => {
                    if (event.target.classList.contains('js-remove')) {
                        event.preventDefault();

                        const yesplz = confirm('Remove this item?');
                        if (yesplz) {
                            this.removeItem(index);
                        } else {
                            return false;
                        }
                    }

                    if (event.target.classList.contains('js-move-up')) {
                        event.preventDefault();
                        this.moveItem(index, -1);
                    }

                    if (event.target.classList.contains('js-move-down')) {
                        event.preventDefault();
                        this.moveItem(index, 1);
                    }
                });

                const titleInput = itemElement.querySelector('input[data-name="title"]');
                const urlInput = itemElement.querySelector('input[data-name="url"]');
                titleInput.addEventListener('blur', () => {
                    this.updateItem({ title: titleInput.value, url: urlInput.value}, index);
                });

                urlInput.addEventListener('blur', () => {
                    this.updateItem({ title: titleInput.value, url: urlInput.value}, index);
                });

                // form elements needs to be focusable
                // clear custom items from focus list first
                this.cleanFocusList();
                window.setTimeout(() => {
                    window.laFinderDialog.addFocusable(CKEDITOR.dom.element.get(itemElement.querySelector('input[data-name="title"]')), window.laFinderDialog._.focusList.length - 2);
                    window.laFinderDialog.addFocusable(CKEDITOR.dom.element.get(itemElement.querySelector('input[data-name="url"]')), window.laFinderDialog._.focusList.length - 2);
                    window.laFinderDialog.addFocusable(CKEDITOR.dom.element.get(itemElement.querySelector('a[data-name="move-up"]')), window.laFinderDialog._.focusList.length - 2);
                    window.laFinderDialog.addFocusable(CKEDITOR.dom.element.get(itemElement.querySelector('a[data-name="move-down"]')), window.laFinderDialog._.focusList.length - 2);
                    window.laFinderDialog.addFocusable(CKEDITOR.dom.element.get(itemElement.querySelector('a[data-name="remove"]')), window.laFinderDialog._.focusList.length - 2);
                }, 0);
            });
        };

        this.cleanFocusList = function () {
            const indicesToRemove = [];

            window.laFinderDialog._.focusList.forEach((item, index) => {
                if (item.element && (item.element.$.dataset.name)) {
                    indicesToRemove.push(index);
                }
            });

            indicesToRemove.reverse();

            for (let i = 0; i < indicesToRemove.length; i++) {
                window.laFinderDialog._.focusList.splice(indicesToRemove[i], 1);
            }
        }
    }

    function itemTemplate(item, index, total) {
        return `
            <td>
                <label class="visually-hidden  cke_dialog_ui_labeled_label" for="title-${index}">Text</label>
                <input placeholder="Title" data-name="title" value="${item.title ? item.title : ''}" class="cke_dialog_ui_input_text" id="title-${index}"/>
            </td>
            <td>
                <label class="visually-hidden  cke_dialog_ui_labeled_label" for="url-${index}">URL</label>
                <input placeholder="URL" data-name="url" value="${item.url ? item.url : ''}" class="cke_dialog_ui_input_text" id="url-${index}"/>
            </td>
            <td>
                <a href="#" title="Move up" data-name="move-up" ${index === 0 ? 'disabled="true"' : ''} class="cke_dialog_ui_button  js-move-up"><span class="visually-hidden">Up</span></a>
                <a href="#" title="Move down" data-name="move-down" ${index === total-1 ? 'disabled="true"' : ''} class="cke_dialog_ui_button  js-move-down"><span class="visually-hidden">Down</span></a>
                <a href="#" title="Remove" data-name="remove" ${total === 1 ? 'disabled="true"' : ''} class="cke_dialog_ui_button  js-remove"><span class="visually-hidden">Remove</span></a>
            </td>`;
    }
}) ();
