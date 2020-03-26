CKEDITOR.plugins.add('inset-text', {
    requires: 'widget',
    icons: 'inset-text',
    init: function (editor) {
        editor.widgets.add('inset-text', {
            button: 'Add inset text',

            template: '<div class="ds_inset-text">' +
                '<div class="ds_inset-text__text">' +
                    '' +
                '</div>' +
            '</div>',

            editables: {
                content: {
                    selector: '.ds_inset-text__text',
                    pathName: 'inset'
                },
            },

            allowedContent:
            'div(!ds_inset-text__text)',

            upcast: function( element ) {
                return element.name == 'div' && element.hasClass( 'ds_inset-text' );
            }
        });
    }
});
