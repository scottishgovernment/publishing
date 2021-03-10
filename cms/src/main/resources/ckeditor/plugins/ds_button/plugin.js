CKEDITOR.plugins.add( 'ds_button', {
    init: function( editor ) {
        editor.addCommand( 'ds_button', new CKEDITOR.dialogCommand( 'dsButtonDialog' ) );
        editor.ui.addButton( 'ds_button', {
            label: 'Button',
            command: 'ds_button',
            icon: this.path + 'images/ds_button.png',
            toolbar: 'insert,1'
        });
        editor.on( 'doubleclick', function( evt ) {
            var element = evt.data.element;
            if ( element.hasClass('ds_button') ) {
                evt.data.dialog = 'dsButtonDialog';
            }
        });

        if (document.getElementById('buttonStyles') === null) {
            let link = document.createElement('link');
            link.id = 'buttonStyles';
            link.rel = 'stylesheet';
            link.type = 'text/css';
            link.href = this.path + 'ds_button.css';
            document.body.appendChild(link);
        }

        CKEDITOR.dialog.add( 'dsButtonDialog', this.path + 'dialogs/ds_button.js' );
    }
});
