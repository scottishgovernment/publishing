CKEDITOR.dialog.add('dsContactDetailsDialog', function (editor) {
    function isOptionalButValidEmailAddress() {
        alert(this.getValue())
        if (this.getValue() !== '') {
            // this is not a super strong regex, but checks for a correct format
            const expression = /[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,8}/,
                regex = new RegExp(expression),
                match = this.getValue().toUpperCase().match( regex ),
                isValid = !!( match );

            if (!isValid) {
                alert( 'Please enter a valid email address' );
            }

            return isValid;
        }
    }

    function isOptionalButValidUrl() {
        if (this.getValue() !== '') {

            const expression = /(https?:\/\/)?([\w\-])+\.{1}([a-zA-Z]{2,63})([\/\w-]*)*\/?\??([^#\n\r]*)?#?([^\n\r]*)/,
                regex = new RegExp(expression),
                match = this.getValue().match(regex),
                isValid = !!(match);

            if (!isValid) {
                alert('Please enter a valid URL');
            }

            return isValid;
        }
    }

    return {
        title: 'Contact details',
        minWidth: 500,
        minHeight: 200,
        contents: [
            {
                id: 'basic',
                label: 'Basic',
                elements: [
                    {
                        type: 'html',
                        html: 'Any fields left blank will not be shown'
                    },
                    {
                        type: 'text',
                        id: 'title',
                        label: 'Title',
                        setup: function (widget) {
                            this.setValue( widget.data.title );
                        },
                        commit: function (widget) {
                            widget.setData('title', this.getValue());
                            widget.setData('hasTitle', !!this.getValue());
                        }
                    },
                    {
                        type: 'textarea',
                        id: 'address',
                        label: 'Address',
                        setup: function (widget) {
                            this.setValue( widget.data.address );
                        },
                        commit: function (widget) {
                            widget.setData('address', this.getValue());
                            widget.setData('hasAddress', !!this.getValue());
                        }
                    },
                    {
                        type: 'text',
                        id: 'email',
                        label: 'Email',
                        validate: isOptionalButValidEmailAddress,
                        setup: function (widget) {
                            this.setValue( widget.data.email );
                        },
                        commit: function (widget) {
                            widget.setData('email', this.getValue());
                            widget.setData('hasEmail', !!this.getValue());
                        }
                    },


                ]
            },
            {
                id: 'additional',
                label: 'Phone',
                elements: [
                    {
                        type: 'html',
                        html: 'Any fields left blank will not be shown'
                    },
                    {
                        type: 'text',
                        id: 'phone-number',
                        label: 'Main phone number',
                        setup: function (widget) {
                            this.setValue( widget.data.phone );
                        },
                        commit: function (widget) {
                            widget.setData('phone', this.getValue());
                            widget.setData('hasPhone', !!this.getValue());
                        }
                    },
                    {
                        type: 'text',
                        id: 'phone-times',
                        label: 'Main phone hours of operation',
                        setup: function (widget) {
                            this.setValue( widget.data.phoneTimes );
                        },
                        commit: function (widget) {
                            widget.setData('phoneTimes', this.getValue());
                        }
                    },
                    {
                        type: 'text',
                        id: 'phone-2-title',
                        label: 'Phone 2 title',
                        setup: function (widget) {
                            this.setValue( widget.data.phone2title );
                        },
                        commit: function (widget) {
                            widget.setData('phone2title', this.getValue());
                            widget.setData('hasPhone2', !!this.getValue());
                        }
                    },
                    {
                        type: 'text',
                        id: 'phone-2-number',
                        label: 'Phone 2 number',
                        setup: function (widget) {
                            this.setValue( widget.data.phone2 );
                        },
                        commit: function (widget) {
                            widget.setData('phone2', this.getValue());
                            widget.setData('hasPhone2', !!this.getValue());
                        }
                    },
                    {
                        type: 'text',
                        id: 'phone-3-title',
                        label: 'Phone 3 title',
                        setup: function (widget) {
                            this.setValue( widget.data.phone3title );
                        },
                        commit: function (widget) {
                            widget.setData('phone3title', this.getValue());
                            widget.setData('hasPhone3', !!this.getValue());
                        }
                    },
                    {
                        type: 'text',
                        id: 'phone-3-number',
                        label: 'Phone 3 number',
                        setup: function (widget) {
                            this.setValue( widget.data.phone3 );
                        },
                        commit: function (widget) {
                            widget.setData('phone3', this.getValue());
                            widget.setData('hasPhone3', !!this.getValue());
                        }
                    }
                ]
            },
            {
                id: 'social',
                label: 'Social',
                elements: [
                    {
                        type: 'html',
                        html: 'Any fields left blank will not be shown'
                    },


                    {
                        type: 'hbox',
                        widths: ['33%', '67%'],
                        children: [
                            {
                                type: 'text',
                                id: 'social1Text',
                                label: 'Link text',
                                setup: function (widget) {
                                    this.setValue( widget.data.social1Text );
                                },
                                commit: function (widget) {
                                    widget.setData('social1Text', this.getValue());
                                }
                            },
                            {
                                type: 'text',
                                id: 'social1Url',
                                label: 'URL',
                                validate: isOptionalButValidUrl,
                                setup: function (widget) {
                                    this.setValue( widget.data.social1Url );
                                },
                                commit: function (widget) {
                                    widget.setData('social1Url', this.getValue());
                                    widget.setData('hasSocial1', !!this.getValue());
                                }
                            }
                        ]
                    },

                    {
                        type: 'hbox',
                        widths: ['33%', '67%'],
                        children: [
                            {
                                type: 'text',
                                id: 'social2Text',
                                label: 'Link text',
                                setup: function (widget) {
                                    this.setValue( widget.data.social2Text );
                                },
                                commit: function (widget) {
                                    widget.setData('social2Text', this.getValue());
                                }
                            },
                            {
                                type: 'text',
                                id: 'social2Url',
                                label: 'URL',
                                validate: isOptionalButValidUrl,
                                setup: function (widget) {
                                    this.setValue( widget.data.social2Url );
                                },
                                commit: function (widget) {
                                    widget.setData('social2Url', this.getValue());
                                    widget.setData('hasSocial2', !!this.getValue());
                                }
                            }
                        ]
                    },

                    {
                        type: 'hbox',
                        widths: ['33%', '67%'],
                        children: [
                            {
                                type: 'text',
                                id: 'social3Text',
                                label: 'Link text',
                                setup: function (widget) {
                                    this.setValue( widget.data.social3Text );
                                },
                                commit: function (widget) {
                                    widget.setData('social3Text', this.getValue());
                                }
                            },
                            {
                                type: 'text',
                                id: 'social3Url',
                                label: 'URL',
                                validate: isOptionalButValidUrl,
                                setup: function (widget) {
                                    this.setValue( widget.data.social3Url );
                                },
                                commit: function (widget) {
                                    widget.setData('social3Url', this.getValue());
                                    widget.setData('hasSocial3', !!this.getValue());
                                }
                            }
                        ]
                    },

                    {
                        type: 'hbox',
                        widths: ['33%', '67%'],
                        children: [
                            {
                                type: 'text',
                                id: 'social4Text',
                                label: 'Link text',
                                setup: function (widget) {
                                    this.setValue( widget.data.social4Text );
                                },
                                commit: function (widget) {
                                    widget.setData('social4Text', this.getValue());
                                }
                            },
                            {
                                type: 'text',
                                id: 'social4Url',
                                label: 'URL',
                                validate: isOptionalButValidUrl,
                                setup: function (widget) {
                                    this.setValue( widget.data.social4Url );
                                },
                                commit: function (widget) {
                                    widget.setData('social4Url', this.getValue());
                                    widget.setData('hasSocial4', !!this.getValue());
                                }
                            }
                        ]
                    },

                    {
                        type: 'hbox',
                        widths: ['33%', '67%'],
                        children: [
                            {
                                type: 'text',
                                id: 'social5Text',
                                label: 'Link text',
                                setup: function (widget) {
                                    this.setValue( widget.data.social5Text );
                                },
                                commit: function (widget) {
                                    widget.setData('social5Text', this.getValue());
                                }
                            },
                            {
                                type: 'text',
                                id: 'social5Url',
                                label: 'URL',
                                validate: isOptionalButValidUrl,
                                setup: function (widget) {
                                    this.setValue( widget.data.social5Url );
                                },
                                commit: function (widget) {
                                    widget.setData('social5Url', this.getValue());
                                    widget.setData('hasSocial5', !!this.getValue());
                                }
                            }
                        ]
                    }
                ]
            }
        ]
    };
});
