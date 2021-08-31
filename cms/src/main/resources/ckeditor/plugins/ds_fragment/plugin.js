/*
 * Copyright 2013-2017 Hippo B.V. (http://www.onehippo.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
(function () {
    "use strict";

    var skipToolbarButtonUpdate = false,
        PREVENT_DBLCLICK_DELAY = 300;

    function iterate(object, func) {
        var property;
        for (property in object) {
            if (object.hasOwnProperty(property)) {
                func(property, object[property]);
            }
        }
    }

    function isEmpty(value) {
        return value === undefined || value === null || value === "";
    }

    function setElementAttributes(element, attributeParameterMap, parameters) {
        console.log('setElementAttributes ', attributeParameterMap, parameters);

        iterate(attributeParameterMap, function (attribute, parameterName) {
            var parameterValue = parameters[parameterName];
            if (isEmpty(parameterValue)) {
                console.log('removeAttribute', attribute)
                element.removeAttribute(attribute);
            } else {
                console.log('setAttribute', attribute, parameterValue);
                element.setAttribute(attribute, parameterValue);
            }
        });
    }

    function getElementParameters(element, attributeParameterMap) {
        var parameters = {};
        if (element !== null) {
            iterate(attributeParameterMap, function (attribute, parameterName) {
                if (element.hasAttribute(attribute)) {
                    parameters[parameterName] = element.getAttribute(attribute);
                }
            });
        }
        return parameters;
    }

    function disableRegisteredCKEditorDialog(ckeditorEvent) {
        ckeditorEvent.data.dialog = null;
    }

    function initInternalLinkPicker(editor) {

        var LINK_ATTRIBUTES_PARAMETER_MAP = {
                'data-fragment-uuid': 'f_uuid',
                title: 'f_title',
                target: 'f_target'
            };

        function createLinkAttributePairs(parameters) {
            var pairs = {'class' : 'fragment'};
            iterate(LINK_ATTRIBUTES_PARAMETER_MAP, function (attribute, parameterName) {
                var parameterValue = parameters[parameterName];
                if (parameterValue !== undefined && parameterValue !== null && parameterValue !== "") {
                    pairs[attribute] = parameterValue;
                }
            });
            return pairs;

        }

        function isInternalLink(element) {
            return !element.isReadOnly() && element.is('a') && element.hasAttribute('data-uuid');
        }

        function isSelectionEmpty(selection) {
            if (selection === null || selection.getType() === CKEDITOR.SELECTION_NONE) {
                return true;
            }
            var ranges = selection.getRanges();
            return ranges.length === 0 || ranges[0].collapsed;
        }

        function getSelectedLinkOrNull(selection) {
            var startElement, linkNode;

            if (selection === null) {
                return null;
            }

            startElement = selection.getStartElement();

            if (startElement !== null) {
                linkNode = startElement.getAscendant('a', true);
                if (linkNode !== null && linkNode.is('a')) {
                    return linkNode;
                }
            }
            return null;
        }

        function openInternalLinkPickerDialog(selectedLink) {
            // disable button state for PREVENT_DBLCLICK_DELAY, to prevent double clicking:
            skipToolbarButtonUpdate = true;
            editor.getCommand('pickInternalLink').setState(CKEDITOR.TRISTATE_DISABLED);
            setTimeout(function () {
                skipToolbarButtonUpdate = false;
            }, PREVENT_DBLCLICK_DELAY);

            var linkPickerParameters = getElementParameters(selectedLink, LINK_ATTRIBUTES_PARAMETER_MAP);
            if (window.Wicket) {
                window.Wicket.Ajax.post({
                    u: editor.config.hippopicker.internalLink.callbackUrl,
                    ep: linkPickerParameters
                });
            } else {
                editor.fire('openLinkPicker', linkPickerParameters);
            }
        }

        function createLinkFromSelection(selection, linkParameters) {
            var range, linkAttributes, linkStyle;
console.log('createLinkFromSelection', selection, linkParameters);
            if (!isSelectionEmpty(selection)) {
                range = selection.getRanges()[0];

                console.log("range ", range)
                linkAttributes = createLinkAttributePairs(linkParameters);
console.log('linkAttributes', linkAttributes);
                linkStyle = new CKEDITOR.style({
                    element: 'div',
                    attributes: linkAttributes,
                    type: CKEDITOR.STYLE_BLOCK
                });
                linkStyle.applyToRange(range);
                range.select();
            } else {
                consle.log('selections is empty');
            }
        }

        function updateToolbarButtonState() {
            if (skipToolbarButtonUpdate) {
                return;
            }
            var state = CKEDITOR.TRISTATE_OFF,
                selection = editor.getSelection();

            if (isSelectionEmpty(selection) && getSelectedLinkOrNull(selection) === null) {
                state = CKEDITOR.TRISTATE_DISABLED;
            }
            editor.getCommand('pickFragment').setState(state);
        }

        editor.ui.addButton('PickFragment', {
            label: 'pick fragment',
            command: 'pickFragment',
            toolbar: 'links,5',
            icon: 'ds_fragment'
        });

        editor.addCommand('pickFragment', {
            startDisabled: true,

            exec: function (editor) {
                var selectedLink = getSelectedLinkOrNull(editor.getSelection());
                openInternalLinkPickerDialog(selectedLink);
            }
        });

        editor.addCommand('insertInternalLink', {
            exec: function (editor, parameters) {
                var selectedLink = getSelectedLinkOrNull(editor.getSelection());
console.log('exec ', selectedLink);
                if (selectedLink !== null) {
                    setElementAttributes(selectedLink, LINK_ATTRIBUTES_PARAMETER_MAP, parameters);

                    // ensure compatibility with the 'link' plugin, which creates an additional attribute
                    // 'data-cke-saved-href' for each link that overrides the actual href value. We don't need
                    // this attribute, so remove it.
                    //selectedLink.removeAttribute('data-cke-saved-href');
                } else {
                    createLinkFromSelection(editor.getSelection(), parameters);
                    selectedLink = getSelectedLinkOrNull(editor.getSelection());
                }

                if (selectedLink !== null) {
                    if (selectedLink.hasAttribute('data-uuid')) {
                        // Set the href attribute to 'http://' so the CKEditor link picker will still recognize the link
                        // as a link (and, for example, enable the 'remove link' button). The CMS will recognize the empty
                        // 'http://' href and still interpret the link as an internal link.
                        selectedLink.setAttribute('href', 'http://');
                    } else {
                        // the link has been removed in the picker dialog
                        selectedLink.remove(true);
                    }
                }
            }
        });

        editor.on('doubleclick', function (event) {
            var clickedLink = getSelectedLinkOrNull(editor.getSelection()) || event.data.element;

            if (isInternalLink(clickedLink)) {
                disableRegisteredCKEditorDialog(event);
                openInternalLinkPickerDialog(clickedLink);
            }
        }, null, null, 20);  // use a higher priority than 10 to overwrite the external link dialog

        // update the toolbar button state whenever the selection changes (copied from the 'clipboard' plugin)
        editor.on('contentDom', function () {
            var editable = editor.editable(),
                mouseupTimeout;

            editable.attachListener(CKEDITOR.env.ie ? editable : editor.document.getDocumentElement(), 'mouseup', function () {
                mouseupTimeout = setTimeout(updateToolbarButtonState, 0);
            });
            editor.on('destroy', function () {
                clearTimeout(mouseupTimeout);
            });
            editable.on('keyup', updateToolbarButtonState);
        });
    }

    function makeCompatibleWithMaximizePlugin(editor) {
        // The 'maximize' plugin breaks the styling of Hippo's modal Wicket dialogs because it removes all CSS classes
        // (including 'hippo-root') from the document body when the editor is maximized. Here we explicitly re-add
        // the 'hippo-root' CSS class when the editor is maximized so the image picker dialog still looks good.
        if (window.Wicket) {
            editor.on("afterCommandExec", function (event) {
                if (event.data.name === 'maximize') {
                    if (event.data.command.state === CKEDITOR.TRISTATE_ON) {
                        CKEDITOR.document.getBody().addClass('hippo-root');
                    }
                }
            });
        }
    }

    function makeCompatibleWithLinkPlugin() {
        // The 'link' plugin should only show the option 'New Window (_blank)' in the list of possible link targets
        CKEDITOR.on('dialogDefinition', function (event) {
            var dialogName = event.data.name,
                dialogDefinition = event.data.definition,
                editor = event.editor,
                targetTab, linkTargetType;

            if (dialogName === 'link') {
                targetTab = dialogDefinition.getContents('target');
                linkTargetType = targetTab.get('linkTargetType');

                linkTargetType.items = [
                    [editor.lang.common.notSet, 'notSet'],
                    [editor.lang.common.targetNew, '_blank']
                ];
            }
        });
    }

    CKEDITOR.plugins.add('ds_fragment', {

        icons: 'ds_fragment',

        init: function (editor) {


            initInternalLinkPicker(editor);

            makeCompatibleWithMaximizePlugin(editor);
            makeCompatibleWithLinkPlugin();
        }

    });
}());
