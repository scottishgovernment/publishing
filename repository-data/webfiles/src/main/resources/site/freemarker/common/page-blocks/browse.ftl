<#ftl output_format="HTML">
<div class="ds_pb  ds_pb--link-list <#if removebottompadding>  ds_!_padding-bottom--0</#if>">
    <div class="category-lower  ds_pre-footer-background">
        <div class="ds_wrapper">
            <div class="ds_layout  ds_layout--category-list">
                <div class="ds_layout__grid">
                    <div class="ds_category-list-container">
                        <#if navigationType == "image-card">
                            <#include '../card-navigation--image.ftl'/>
                        </#if>
                        <#if navigationType == "card">
                            <#include '../card-navigation.ftl'/>
                        </#if>
                        <#if navigationType == "grid">
                            <#include '../grid-navigation.ftl'/>
                        </#if>
                        <#if navigationType == "list">
                            <#include '../list-navigation.ftl'/>
                        </#if>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
