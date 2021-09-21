(function () {
    "use strict";

    var skipToolbarButtonUpdate = false,
        PREVENT_DBLCLICK_DELAY = 300;

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
            skipToolbarButtonUpdate = true;
            editor.getCommand('pickFragment').setState(CKEDITOR.TRISTATE_DISABLED);
            setTimeout(function () {
                skipToolbarButtonUpdate = false;
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

            exec: function (editor) {
                // var selectedLink = getSelectedFragmentOrNull(editor.getSelection());
                // openInternalLinkPickerDialog(selectedLink);

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

                element.appendText(parameters.f_uuid);
                element.setAttribute('contenteditable', 'false');
                element.setAttribute('href', 'http://');
                element.addClass('fragment');
                element.data('uuid', parameters.f_uuid);

                delete editor.selectedFragment;

                // var selectedLink = getSelectedFragmentOrNull(editor.getSelection());

                // if (selectedLink !== null) {

                //     setElementAttributes(selectedLink, LINK_ATTRIBUTES_PARAMETER_MAP, parameters);

                //     // ensure compatibility with the 'link' plugin, which creates an additional attribute
                //     // 'data-cke-saved-href' for each link that overrides the actual href value. We don't need
                //     // this attribute, so remove it.
                //     selectedLink.removeAttribute('data-cke-saved-href');
                // } else {
                //     createLinkFromSelection(editor.getSelection(), parameters);
                //     selectedLink = getSelectedFragmentOrNull(editor.getSelection());
                // }

                // if (selectedLink !== null) {
                //     if (selectedLink.hasAttribute('data-uuid')) {

                //         // Set the href attribute to 'http://' so the CKEditor link picker will still recognize the link
                //         // as a link (and, for example, enable the 'remove link' button). The CMS will recognize the empty
                //         // 'http://' href and still interpret the link as an internal link.
                //         selectedLink.setAttribute('href', 'http://');
                //     } else {
                //         // the link has been removed in the picker dialog
                //         selectedLink.remove(true);

                //     }
                // }

            }
        });

        editor.on('doubleclick', function (event) {
            var clickedFragment = event.data.element;
            console.log(clickedFragment)
            if (isFragmentElement(clickedFragment)) {
                console.log(222)
                // disableRegisteredCKEditorDialog(event);
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
