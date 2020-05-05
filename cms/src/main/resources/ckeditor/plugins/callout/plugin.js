// using a closure so we don't pollute global scope
const calloutPlugin = function () {
    CKEDITOR.plugins.add('callout', {
        requires: 'widget',
        icons: 'callout',
        init: function (editor) {
            editor.widgets.add('callout', {
                button: 'Add callout',
                template: `<aside class="ds_callout" role="complementary">
                        <header>
                            <div class="ds_callout__label  ds_content-label">Label</div>
                            <h2 class="ds_callout__title">Title</h2>
                        </header>
                        <div class="ds_callout__content">Content</div>
                    </aside>`,
                editables: {
                    label: {
                        selector: '.ds_callout__label',
                        pathName: 'callout label',
                        allowedContent: 'u'
                    },
                    title: {
                        selector: '.ds_callout__title',
                        pathName: 'callout title'
                    },
                    content: {
                        selector: '.ds_callout__content',
                        pathName: 'callout content',
                        $1: {
                            // Use the ability to specify elements as an object.
                            elements: CKEDITOR.dtd,
                            attributes: true,
                            styles: false,
                            classes: true
                        }
                    }
                },

                allowedContent: {
                    $1: {
                        // Use the ability to specify elements as an object.
                        elements: CKEDITOR.dtd,
                        attributes: true,
                        styles: false,
                        classes: true
                    }
                },

                upcast: function( element ) {
                    return element.name == 'aside' && element.hasClass( 'ds_callout' );
                }
            });
        }
    });
}();
