(function () {
    "use strict";

    var PREVENT_DBLCLICK_DELAY = 300;

    function initFragmentPicker(editor) {
        var LINK_ATTRIBUTES_PARAMETER_MAP = {
            'data-uuid': 'f_uuid',
            title: 'f_title',
            target: 'f_target',
            'class': 'ds_fragment'
        },
        //LANG = editor.lang.hippopicker,
        LINK_ALLOWED_CONTENT = 'div[!data-uuid,!href,title,target,class,contenteditable]',
        LINK_REQUIRED_CONTENT = 'div[data-uuid,href,class]';

        function openFragmentPickerDialog(selectedFragment) {
            // disable button state for PREVENT_DBLCLICK_DELAY, to prevent double clicking:
            editor.getCommand('pickFragment').setState(CKEDITOR.TRISTATE_DISABLED);
            setTimeout(function () {
                editor.getCommand('pickFragment').setState(CKEDITOR.TRISTATE_ENABLED);
            }, PREVENT_DBLCLICK_DELAY);

            var fragmentPickerParameters = getElementParameters(selectedFragment, LINK_ATTRIBUTES_PARAMETER_MAP);
            if (window.Wicket) {
                window.Wicket.Ajax.post({
                    u: editor.config.hippopicker.fragment.callbackUrl,
                    ep: fragmentPickerParameters
                });
            }
        }

        function isFragmentElement(element) {
            return !element.isReadOnly() && element.is('div')
                && element.hasAttribute('data-uuid')
                && element.hasClass('fragment');
        }

        function getElementParameters(element, attributeParameterMap) {
            var parameters = {};
            if (element !== null && typeof element !== 'undefined') {
                iterate(attributeParameterMap, function (attribute, parameterName) {
                    if (element.hasAttribute(attribute)) {
                        parameters[parameterName] = element.getAttribute(attribute);
                    }
                });
            }
            return parameters;
        }

        function iterate(object, func) {
            var property;
            for (property in object) {
                if (object.hasOwnProperty(property)) {
                    func(property, object[property]);
                }
            }
        }

        editor.addCommand('pickFragment', {
            allowedContent: LINK_ALLOWED_CONTENT,
            requiredContent: LINK_REQUIRED_CONTENT,

            startDisabled: false,

            exec: function () {
                openFragmentPickerDialog();
            }
        });

        editor.addCommand('insertFragment', {
            exec: function (editor, parameters) {
                var element;

                if (!editor.selectedFragment) {
                    element = new CKEDITOR.dom.element('div');
                    editor.insertElement(element);
                } else {
                    element = editor.selectedFragment;
                }

                element.setHtml('<div contenteditable="false">Fragment</div>');
                element.addClass('fragment');
                element.data('uuid', parameters.f_uuid);

                delete editor.selectedFragment;
            }
        });

        editor.on('doubleclick', function (event) {
            var clickedFragment = event.data.element;

            if (isFragmentElement(clickedFragment)) {
                openFragmentPickerDialog(clickedFragment);
                editor.selectedFragment = clickedFragment;
            }
        }, null, null, 20);

        editor.ui.addButton( 'ds_fragment', {
            label: 'Fragment',
            toolbar: 'insert, 1',
            icon: editor.plugins.ds_fragment.path + 'icons/ds_fragment.png',
            command: 'pickFragment',
            allowedContent: LINK_ALLOWED_CONTENT,
            requiredContent: LINK_REQUIRED_CONTENT
        });
    }

    CKEDITOR.plugins.add('ds_fragment', {
        init: function (editor) {
            initFragmentPicker(editor);
        }
    });
}());
