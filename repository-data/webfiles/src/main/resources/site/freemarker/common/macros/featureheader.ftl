<#ftl output_format="HTML">

<@hst.webfile var="featureheadericonspath" path="/assets/images/icons/icons.stack.svg"/>

<#-- Renders document.featureheader as a Feature header block, using the document's
     own title, summary, hero image and last updated date. Caller must check
     document.featureheader?? first. -->
<#macro renderFeatureHeader document wrap>
    <#assign featureheader = document.featureheader />
    <#assign constrain = true />
    <#if wrap?? && wrap && featureheader.fullWidth?? && featureheader.fullWidth>
        <#assign constrain = false />
    </#if>
    <#if (!wrap?? || wrap) && constrain>
    <div class="ds_wrapper">
    </#if>
        <header class="ds_feature-header<#if featureheader.backgroundColor?has_content><#switch featureheader.backgroundColor?lower_case><#case 'secondary'>  ds_feature-header--background-secondary<#break><#case 'tertiary'>  ds_feature-header--background-tertiary<#break><#case 'theme'>  ds_feature-header--background-secondary<#break><#case 'theme reversed'>  ds_feature-header--background<#break></#switch></#if><#if featureheader.wideText?? && featureheader.wideText>  ds_feature-header--wide</#if><#if !constrain>  ds_feature-header--fullwidth</#if><#if !(featureheader.verticalAlign?has_content) || featureheader.verticalAlign == 'top'>  ds_feature-header--top</#if>">
            <#if !constrain>
            <div class="ds_wrapper">
            </#if>
            <div class="ds_feature-header__primary">
                <h1 class="ds_feature-header__title"><#if document.title??>${document.title}</#if></h1>

                <#if document.lastUpdatedDate??>
                    <dl class="ds_page-header__metadata  ds_metadata">
                        <div class="ds_metadata__item">
                            <dt class="ds_metadata__key">Last updated</dt>
                            <dd class="ds_metadata__value"><@fmt.formatDate value=document.lastUpdatedDate.time type="both" pattern="d MMM yyyy"/></dd>
                        </div>
                    </dl>
                </#if>

                <#if document.summary?has_content>
                    <p>${document.summary}</p>
                </#if>

                <#if featureheader.cta?has_content>
                    <#if featureheader.link?has_content>
                        <a href="<@hst.link hippobean=featureheader.link/>" class="ds_button ds_button--has-icon">${featureheader.cta}<svg class="ds_icon" aria-hidden="true" role="img">
                            <use href="${featureheadericonspath}#chevron_right"></use>
                        </svg></a>
                    <#elseif featureheader.externalLink?has_content>
                        <a href="${featureheader.externalLink}" class="ds_button ds_button--has-icon">${featureheader.cta}<svg class="ds_icon" aria-hidden="true" role="img">
                            <use href="${featureheadericonspath}#chevron_right"></use>
                        </svg></a>
                    </#if>
                </#if>
            </div>

            <#if document.heroImage??>
                <div class="ds_feature-header__secondary<#if featureheader.imageNoMargin?? && featureheader.imageNoMargin>  ds_feature-header__secondary--no-padding</#if><#if featureheader.imageCover?? && featureheader.imageCover>  ds_feature-header__secondary--cover</#if><#if featureheader.imageAlignMobile?has_content>  ds_feature-header__secondary--${featureheader.imageAlignMobile}-mobile</#if>">
                    <#if document.heroImage.xlargesixcolumns??>
                        <img class="ds_feature-header__image" alt="" src="<@hst.link hippobean=document.heroImage.xlargesixcolumns />"
                            <#if document.heroImage.xlargesixcolumns.width gt 0>width="${document.heroImage.xlargesixcolumns.width?c}"</#if>
                            <#if document.heroImage.xlargesixcolumns.height gt 0>height="${document.heroImage.xlargesixcolumns.height?c}"</#if>
                            loading="lazy"
                            srcset="
                                <@hst.link hippobean=document.heroImage.smallcolumns/> 360w,
                                <@hst.link hippobean=document.heroImage.smallcolumnsdoubled/> 720w,
                                <@hst.link hippobean=document.heroImage.mediumsixcolumns/> 352w,
                                <@hst.link hippobean=document.heroImage.mediumsixcolumnsdoubled/> 704w,
                                <@hst.link hippobean=document.heroImage.largesixcolumns/> 448w,
                                <@hst.link hippobean=document.heroImage.largesixcolumnsdoubled/> 896w,
                                <@hst.link hippobean=document.heroImage.xlargesixcolumns/> 544w,
                                <@hst.link hippobean=document.heroImage.xlargesixcolumnsdoubled/> 1088w"
                            sizes="(min-width:1200px) 544px, (min-width:992px) 448px, (min-width: 768px) 352px, 100vw"
                            >
                    <#else>
                        <img class="ds_feature-header__image" src="<@hst.link hippobean=document.heroImage />" alt=""
                            <#if document.heroImage.original.width gt 0>width="${document.heroImage.original.width?c}"</#if>
                            <#if document.heroImage.original.height gt 0>height="${document.heroImage.original.height?c}"</#if>
                            loading="lazy"
                            >
                    </#if>
                </div>
            </#if>
            <#if !constrain>
            </div>
            </#if>
        </header>
    <#if (!wrap?? || wrap) && constrain>
    </div>
    </#if>
</#macro>
