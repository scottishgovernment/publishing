<#ftl output_format="HTML">
<#include "include/imports.ftl">
<@hst.webfile var="iconspath" path="/assets/images/icons/icons.stack.svg"/>

<#if document??>
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
                    <img alt="" src="<@hst.link hippobean=document.logo/>" />
                </div>
            </#if>

            <div class="ds_layout__content">
                <section class="page-section">
                    <#if document.content?has_content>
                        <div class="ds_leader">
                            <@hst.html hippohtml=document.content/>
                        </div>
                    <#else>
                        <p>${document.summary}</p>
                    </#if>

                    <@hst.html hippohtml=document.notices/>
                </section>
            </div>

            <!--Only show services if any exist-->
            <#if document.featuredservices?size gt 0>
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
                                        ${child.description}
                                    </div>
                                </li>
                            </#list>
                        </ul>
                    </section>
                </div>
            <#elseif services?has_content>
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
                                        <@hst.html hippohtml=child.summary/>
                                    </div>
                                </li>
                            </#list>
                        </ul>
                    </section>
                </div>
            </#if>

            <!--Only show featured items if any exist-->
            <#if document.featureditem?size gt 0>
                <div class="ds_layout__featured">
                <section id="how-we-work" class="page-section">
                    <h2>${document.featureditemstitle}</h2>

                    <div class="ds_cols  ds_cols--4">
                        <#list document.featureditem as child>
                            <div class="ds_card  ds_card--transparent">
                                <div class="ds_card__media">
                                    <img src="<@hst.link hippobean=child.image/>" alt="" class="ds_card__image" />
                                </div>
                                <@hst.link var="link" hippobean=child.link/>
                                <div class="ds_card__content  ds_category-item">
                                    <h2 class="ds_category-item__title"><a class="ds_card__cover-link" href="${link}">${child.link.title}</a></h2>
                                    <div class="ds_category-item__summary">
                                        ${child.summary}
                                    </div>
                                </div>

                            </div>
                        </#list>
                    </div>
                    </section>
                </div>
            </#if>

            <@hst.html var="organisationstructure" hippohtml=document.organisationstructure/>
            <#if document.featuredrole?has_content || organisationstructure?has_content>
            <div class="ds_layout__organisation">
                <section id="organisation" class="page-section">
                    <h2>${document.organisationtitle}</h2>

                    <#if document.featuredrole??>
                        <!-- featured role -->
                        <div class="mg_org-person">
                            <div class="mg_org-person__image">
                                <#if document.featuredroleimage??>
                                    <img alt="${document.featuredrolename}" class="person__image"
                                    src="<@hst.link hippobean=document.featuredroleimage.columnimagefour/>"
                                    srcset="<@hst.link hippobean=document.featuredroleimage.columnimagefour/> 208w,
                                        <@hst.link hippobean=document.featuredroleimage.columnimagefourdbl/> 416w,
                                        <@hst.link hippobean=document.featuredroleimage.columnimagefourmedium/> 224w,
                                        <@hst.link hippobean=document.featuredroleimage.columnimagefourmediumdbl/> 448w,
                                        <@hst.link hippobean=document.featuredroleimage.columnimagethreelarge/> 208w,
                                        <@hst.link hippobean=document.featuredroleimage.columnimagethreelargedbl/> 416w,
                                        <@hst.link hippobean=document.featuredroleimage.columnimagethreexl/> 256w,
                                        <@hst.link hippobean=document.featuredroleimage.columnimagethreexldbl/> 512w"
                                    sizes="(min-width:1200px) 256px, (min-width:992px) 208px, (min-width:768px) 224px, 208px">
                                <#else>
                                    <img class="person__image" src="<@hst.link path='/assets/images/people/placeholder.png'/>" alt="${person.title}">
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

                        <#if organisationstructure?has_content>
                            <@hst.html hippohtml=document.organisationstructure/>
                        </#if>
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
                                        <svg class="ds_contact-details__social-icon  ds_icon" aria-hidden="true" role="img"><use xlink:href="${iconspath}#facebook"></use></svg>
                                        Facebook
                                    </a>
                                </dd>
                            </#if>
                            <#if document.twitter?has_content>
                                <dd class="ds_contact-details__social-item">
                                    <a class="ds_contact-details__social-link" href="http://twitter.com/${document.twitter}">
                                        <svg class="ds_contact-details__social-icon  ds_icon" aria-hidden="true" role="img"><use xlink:href="${iconspath}#twitter"></use></svg>
                                        ${document.twitter}
                                    </a>
                                </dd>
                            </#if>
                            <#if document.flickr?has_content>
                                <dd class="ds_contact-details__social-item">
                                    <a class="ds_contact-details__social-link" href="${document.flickr}">
                                        <svg class="ds_contact-details__social-icon  ds_icon" aria-hidden="true" role="img"><use xlink:href="${iconspath}#flickr"></use></svg>
                                        Flickr
                                    </a>
                                </dd>
                            </#if>
                            <#if document.youtube?has_content>
                                <dd class="ds_contact-details__social-item">
                                    <a class="ds_contact-details__social-link" href="${document.youtube}">
                                        <svg class="ds_contact-details__social-icon  ds_icon" aria-hidden="true" role="img"><use xlink:href="${iconspath}#youtube"></use></svg>
                                        YouTube
                                    </a>
                                </dd>
                            </#if>
                            <#if document.blog?has_content>
                                <dd class="ds_contact-details__social-item">
                                    <a class="ds_contact-details__social-link" href="${document.blog}">
                                        <svg class="ds_contact-details__social-icon  ds_icon" aria-hidden="true" role="img"><use xlink:href="${iconspath}#blog"></use></svg>
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
</#if>

<@hst.headContribution category="meta">
    <#if document??>
    <meta name="description" content="${document.metaDescription}"/>
    </#if>
</@hst.headContribution>

<#assign scriptName="organisation">
<#include 'scripts.ftl'/>
