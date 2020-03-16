CKEDITOR.plugins.add('accordion', {
    requires: 'widget',
    icons: 'accordion',
    init: function (editor) {
        editor.widgets.add('accordion', {
            button: 'Add accordion',

            template:
`<div class="ds_accordion-item">
    <input type="checkbox" class="visually-hidden  ds_accordion-item__control" id="panel-1" aria-labelledby="panel-1-heading" />
    <div class="ds_accordion-item__header">
        <h3 id="panel-1-heading" class="ds_accordion-item__title">
            Title
        </h3>
        <span class="ds_accordion-item__indicator"></span>
        <label class="ds_accordion-item__label" for="panel-1"><span class="visually-hidden">Show this section</span></label>
    </div>
    <div class="ds_accordion-item__body">
        Body
    </div>
</div>`,

            editables: {
                title: {
                    selector: '.ds_accordion-item__title',
                    pathName: 'accordionTitle',
                    allowedContent: 'strong em abbr'
                },
                content: {
                    selector: '.ds_accordion-item__body',
                    pathName: 'accordionBody',
                    allowedContent: 'p br ul ol li strong em'
                }
            },

            allowedContent:
                'div(!ds_accordion-item);div(!ds_accordion-item__header);h3(!ds_accordion-item__title);div(!ds_accordion-item__indicator);div(!ds_accordion-item__label);div(!ds_accordion-item__body);input(!ds_accordion-item__control)',

            requiredContent: 'div(ds_accordion-item)',

            upcast: function( element ) {
                return element.name == 'div' && element.hasClass( 'ds_accordion-item' );
            }
        });
    },

    generateIdString: function () {
        return parseInt(Math.random() * 1000000, 10);
    },

    onLoad: function () {
        if (document.getElementById('accordionStyles') === null) {
            let link = document.createElement('link');
            link.id = 'accordionStyles';
            link.rel = 'stylesheet';
            link.type = 'text/css';
            link.href = this.path + 'accordion.css';
            document.body.appendChild(link);
        }
    }
});
