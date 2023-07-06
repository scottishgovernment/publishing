CKEDITOR.plugins.add( 'codeSnippetLanguages', {
    init: function( editor ) {
        CKEDITOR.config.codeSnippet_languages = {
            html: 'HTML',
            javascript: 'JavaScript',
            css: 'CSS/SCSS',
            json: 'JSON',
            bash: 'Bash',
            markdown: 'Markdown',
            xml: 'XML',
            svg: 'SVG',
            typescript: 'TypeScript',
            tsconfig: 'TSconfig'
        };
    }
});
