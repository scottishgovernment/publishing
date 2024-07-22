<#ftl output_format="HTML">
<#include "include/imports.ftl">
<#include "macros/content-blocks.ftl">
<#list fragments as fragment>
<@renderContentBlocks fragment.contentBlocks />
</#list>