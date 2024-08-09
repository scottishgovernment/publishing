<#ftl output_format="HTML">
<#include "include/imports.ftl">
<#include "macros/content-blocks.ftl">
<@hst.webfile var="iconspath" path="/assets/images/icons/icons.stack.svg"/>

<#if document??>
<#assign variables = hstRequestContext.getAttribute("variables")/>
<@hst.messagesReplace escapeMessageXml=false bundle=variables variablePrefix="[[" variableSuffix="]]">

<div class="cms-editable">
    <@hst.manageContent hippobean=document />

    <@hst.include ref="breadcrumbs"/>

    <div class="ds_wrapper">
        <main id="main-content" class="ds_layout  mg_layout--org-hub">
            <div class="ds_layout__header">
                <header class="ds_page-header">
                    <h1 class="ds_page-header__title">${document.title}</h1>
                    <dl class="ds_page-header__metadata  ds_metadata">
                        <#if document.website?has_content>
                            <div class="ds_metadata__item">
                                <dt class="ds_metadata__key">Web</dt>
                                <dd class="ds_metadata__value"><a href="${document.website}">${document.website?replace("https://", "")?replace("http://", "")}</a></dd>
                            </div>
                        </#if>
                    </dl>
                </header>
            </div>

            <#if document.logo??>
                <div class="ds_layout__partner  mg_partner-logo">
                    <#if document.logo.xlargefourcolumns??>
                        <img alt="" src="<@hst.link hippobean=document.logo.xlargefourcolumns />"
                            loading="lazy"
                            srcset="
                            <@hst.link hippobean=document.logo.smallcolumns/> 448w,
                            <@hst.link hippobean=document.logo.smallcolumnsdoubled/> 896w,
                            <@hst.link hippobean=document.logo.mediumfourcolumns/> 224w,
                            <@hst.link hippobean=document.logo.mediumfourcolumnsdoubled/> 448w,
                            <@hst.link hippobean=document.logo.largefourcolumns/> 288w,
                            <@hst.link hippobean=document.logo.largefourcolumnsdoubled/> 576w,
                            <@hst.link hippobean=document.logo.xlargefourcolumns/> 352w,
                            <@hst.link hippobean=document.logo.xlargefourcolumnsdoubled/> 704w"
                            sizes="(min-width:1200px) 352px, (min-width:992px) 288px, (min-width: 768px) 224px, 448px"
                            >
                    <#else>
                        <img loading="lazy" alt="" src="<@hst.link hippobean=document.logo/>" />
                    </#if>
                </div>
            </#if>

            <div class="ds_layout__content">
                <section class="page-section">
                    <@hst.html var="htmlcontent" hippohtml=document.content/>
                    <#if htmlcontent?has_content>
                        <div class="ds_leader">
                            <@hst.html hippohtml=document.content/>
                        </div>
                    <#else>
                        <p>${document.summary}</p>
                    </#if>

                    <#if document.noticesContentBlocks??>
                        <@renderContentBlocks document.noticesContentBlocks />
                    </#if>
                </section>
            </div>

            <!--Only show services if any exist-->
            <#if document.featuredservices?size gt 0>
                <!--noindex-->
                <div class="ds_layout__services">
                    <section id="featured-services" class="page-section  org-services">
                        <h2>${document.featuredservicestitle}</h2>

                        <ul class="ds_category-list  ds_cols  ds_cols--2">
                            <#list document.featuredservices as child>
                                <@hst.link var="link" hippobean=child.link/>
                                <li class="ds_category-item">
                                    <h2 class="ds_category-item__title">
                                        <a href="${link}" class="ds_category-item__link">${child.title}</a>
                                    </h2>

                                    <div class="ds_category-item__summary  ds_stack">
                                        ${child.description?no_esc}
                                    </div>
                                </li>
                            </#list>
                        </ul>
                    </section>
                </div>
                <!--endnoindex-->
            <#elseif services?has_content>
                <!--noindex-->
                <div class="ds_layout__services">
                    <section id="other-services" class="page-section  org-services">
                        <h2>Services</h2>
                        <ul class="ds_category-list  ds_cols  ds_cols--2">
                            <#list services as child>
                                <@hst.link var="link" hippobean=child/>

                                <li class="ds_category-item">
                                    <h2 class="ds_category-item__title">
                                        <a href="${link}" class="ds_category-item__link">${child.title}</a>
                                    </h2>

                                    <div class="ds_category-item__summary">
                                        ${child.summary}
                                    </div>
                                </li>
                            </#list>
                        </ul>
                    </section>
                </div>
                <!--endnoindex-->
            </#if>

            <!--Only show featured items if any exist-->
            <#if document.featureditem?size gt 0>
                <!--noindex-->
                <div class="ds_layout__featured">
                    <section id="how-we-work" class="page-section">
                        <h2>${document.featureditemstitle}</h2>
                        <div class="ds_cols  ds_cols--4">
                            <#list document.featureditem as child>
                                <div class="ds_card  ds_card--transparent">
                                    <div class="ds_card__media">
                                        <div class="ds_aspect-box">
                                            <#if child.image.xlargethreecolumns??>
                                                <img class="ds_aspect-box__inner" alt="" src="<@hst.link hippobean=child.image.xlargethreecolumns />"
                                                    width="${child.image.xlargethreecolumns.width?c}"
                                                    height="${child.image.xlargethreecolumns.height?c}"
                                                    loading="lazy"
                                                    srcset="
                                                    <@hst.link hippobean=child.image.smallcolumns/> 448w,
                                                    <@hst.link hippobean=child.image.smallcolumnsdoubled/> 896w,
                                                    <@hst.link hippobean=child.image.mediumsixcolumns/> 352w,
                                                    <@hst.link hippobean=child.image.mediumsixcolumnsdoubled/> 704w,
                                                    <@hst.link hippobean=child.image.largethreecolumns/> 208w,
                                                    <@hst.link hippobean=child.image.largethreecolumnsdoubled/> 416w,
                                                    <@hst.link hippobean=child.image.xlargethreecolumns/> 256w,
                                                    <@hst.link hippobean=child.image.xlargethreecolumnsdoubled/> 512w"
                                                    sizes="(min-width:1200px) 256px, (min-width:992px) 208px, (min-width: 768px) 352px, 448px"
                                                    >
                                            <#else>
                                                <img loading="lazy" src="<@hst.link hippobean=child.image/>" alt="" class="ds_aspect-box__inner" />
                                            </#if>
                                        </div>
                                    </div>
                                    <@hst.link var="link" hippobean=child.link/>
                                    <div class="ds_card__content  ds_category-item">
                                        <h2 class="ds_category-item__title"><a class="ds_card__cover-link" href="${link}">${child.link.title}</a></h2>
                                        <div class="ds_category-item__summary">
                                            ${child.summary?no_esc}
                                        </div>
                                    </div>
                                </div>
                            </#list>
                        </div>
                    </section>
                </div>
                <!--endnoindex-->
            </#if>

            <#if document.featuredrole?has_content || document.organisationstructureContentBlocks??>
            <div class="ds_layout__organisation">
                <section id="organisation" class="page-section">
                    <h2>${document.organisationtitle}</h2>

                    <#if document.featuredrole?has_content>
                        <!-- featured role -->
                        <div class="mg_org-person">
                            <div class="mg_org-person__image">
                                <#if document.featuredroleimage?has_content>
                                    <img alt="${document.featuredrolename}" class="person__image"
                                    src="<@hst.link hippobean=document.featuredroleimage.xlargethreecolumns/>"
                                    loading="lazy"
                                    srcset="<@hst.link hippobean=document.featuredroleimage.smallcolumnsdoubled/> 208w,
                                        <@hst.link hippobean=document.featuredroleimage.smallcolumnsdoubled/> 416w,
                                        <@hst.link hippobean=document.featuredroleimage.mediumfourcolumns/> 224w,
                                        <@hst.link hippobean=document.featuredroleimage.mediumfourcolumnsdoubled/> 448w,
                                        <@hst.link hippobean=document.featuredroleimage.largethreecolumns/> 208w,
                                        <@hst.link hippobean=document.featuredroleimage.largethreecolumnsdoubled/> 416w,
                                        <@hst.link hippobean=document.featuredroleimage.xlargethreecolumns/> 256w,
                                        <@hst.link hippobean=document.featuredroleimage.xlargethreecolumnsdoubled/> 512w"
                                    sizes="(min-width:1200px) 256px, (min-width:992px) 208px, (min-width:768px) 224px, 208px">
                                <#else>
                                    <img loading="lazy" class="person__image" src="<@hst.link path='/assets/images/people/placeholder.png'/>" alt="${document.featuredrolename}">
                                </#if>
                            </div>

                            <header class="mg_org-person__heading">
                                <h3 class="mg_org-person__name">${document.featuredrolename}</h3>

                                <p class="mg_org-person__roles">
                                    ${document.featuredrole}
                                </p>
                            </header>

                            <div class="mg_org-person__summary  large--seven-twelfths">
                                ${document.featuredroledescription?no_esc}
                            </div>
                        </div>
                        <!-- /end featured role -->
                    </#if>

                    <#if document.organisationstructureContentBlocks??>
                        <@renderContentBlocks document.organisationstructureContentBlocks />
                    </#if>
                </section>
            </div>
            </#if>

            <#if document.phone?has_content ||
                document.fax?has_content ||
                document.email?has_content ||
                document.address?has_content
            >
            <div class="ds_layout__contact">
                <div class="ds_contact-details">
                    <h2 class="ds_contact-details__title">Contact</h2>

                    <dl class="ds_contact-details__list">

                        <#if document.phone?has_content>
                            <div class="ds_contact-details__item">
                                <dt>Phone</dt>
                                <dd>
                                    ${document.phone?no_esc}
                                    <a href="https://www.gov.uk/call-charges">Find out about call charges</a>
                                </dd>
                            </div>
                        </#if>
                        <#if document.fax?has_content>
                            <div class="ds_contact-details__item">
                                <dt>Fax</dt>
                                <dd>
                                    ${document.fax}
                                </dd>
                            </div>
                        </#if>
                        <#if document.email?has_content>
                            <div class="ds_contact-details__item">
                                <dt>Email</dt>
                                <dd><a href="mailto:${document.email}">${document.email}</a></dd>
                            </div>
                        </#if>
                        <#if document.address?has_content>
                            <div class="ds_contact-details__item">
                                <dt>Address</dt>
                                <dd translate="no">
                                    <address>
                                        ${document.address?no_esc}
                                    </address>
                                </dd>
                            </div>
                        </#if>
                    </dl>
                </div>
            </div>
            </#if>

            <#if document.facebook?has_content ||
                document.twitter?has_content ||
                document.flickr?has_content ||
                document.youtube?has_content ||
                document.linkedin?has_content ||
                document.blog?has_content
            >
            <div class="ds_layout__connect">
                <div class="ds_contact-details">
                    <h2 class="ds_contact-details__title">Connect</h2>

                    <dl class="ds_contact-details__list">
                        <div class="ds_contact-details__item  ds_contact-details__social">
                            <dt class="visually-hidden">Social media</dt>
                            <#if document.facebook?has_content>
                                <dd class="ds_contact-details__social-item">
                                    <a class="ds_contact-details__social-link" href="${document.facebook}">
                                        <svg class="ds_contact-details__social-icon  ds_icon" aria-hidden="true" role="img"><use href="${iconspath}#facebook"></use></svg>
                                        Facebook
                                    </a>
                                </dd>
                            </#if>
                            <#if document.twitter?has_content>
                                <dd class="ds_contact-details__social-item">
                                    <a class="ds_contact-details__social-link" href="https://x.com/${document.twitter}">
                                        <svg class="ds_contact-details__social-icon  ds_icon" aria-hidden="true" role="img"><use href="${iconspath}#x"></use></svg>
                                        ${document.twitter}
                                    </a>
                                </dd>
                            </#if>
                            <#if document.flickr?has_content>
                                <dd class="ds_contact-details__social-item">
                                    <a class="ds_contact-details__social-link" href="${document.flickr}">
                                        <svg class="ds_contact-details__social-icon  ds_icon" aria-hidden="true" role="img"><use href="${iconspath}#flickr"></use></svg>
                                        Flickr
                                    </a>
                                </dd>
                            </#if>
                            <#if document.youtube?has_content>
                                <dd class="ds_contact-details__social-item">
                                    <a class="ds_contact-details__social-link" href="${document.youtube}">
                                        <svg class="ds_contact-details__social-icon  ds_icon" aria-hidden="true" role="img"><use href="${iconspath}#youtube"></use></svg>
                                        YouTube
                                    </a>
                                </dd>
                            </#if>
                            <#if document.linkedin?has_content>
                                <dd class="ds_contact-details__social-item">
                                    <a class="ds_contact-details__social-link" href="${document.linkedin}">
                                        <svg class="ds_contact-details__social-icon  ds_icon" aria-hidden="true" role="img"><use href="${iconspath}#linkedin"></use></svg>
                                        LinkedIn
                                    </a>
                                </dd>
                            </#if>
                            <#if document.blog?has_content>
                                <dd class="ds_contact-details__social-item">
                                    <a class="ds_contact-details__social-link" href="${document.blog}">
                                        <svg class="ds_contact-details__social-icon  ds_icon" aria-hidden="true" role="img"><use href="${iconspath}#blog"></use></svg>
                                        Blog
                                    </a>
                                </dd>
                            </#if>
                        </div>
                    </dl>
                </div>
            </div>
            </#if>

            <#include 'feedback-wrapper.ftl'>
        </main>
    </div>
</div>
</@hst.messagesReplace>

<@hst.headContribution category="meta">
    <meta name="dc.format" content="Organisation"/>
</@hst.headContribution>

</#if>

<#assign scriptName="organisation">
<#include 'scripts.ftl'/>
