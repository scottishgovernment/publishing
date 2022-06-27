<#ftl output_format="HTML">
<#include "include/imports.ftl">

<footer class="ds_site-footer">
    <div class="ds_wrapper">
        <div class="ds_site-footer__content">
            <ul class="ds_site-footer__site-items">
                <#list children as item>
                    <li class="ds_site-items__item">
                        <@hst.link var="link" hippobean=item />
                        <a href="${link}">${item.title}</a>
                    </li>
                </#list>
            </ul>

            <div class="ds_site-footer__copyright">
                <a class="ds_site-footer__copyright-logo" href="https://www.nationalarchives.gov.uk/doc/open-government-licence/version/3/">
                    <img width="300" height="121" src="<@hst.webfile path="/assets/images/logos/ogl.svg"/>" alt="Open Government License" />
                </a>
                <p>All content is available under the <a href="https://www.nationalarchives.gov.uk/doc/open-government-licence/version/3/">Open Government Licence v3.0</a>, except for graphic assets and where otherwise stated</p>
                <p>&copy; Crown Copyright</p>
            </div>

            <div class="ds_site-footer__org">
                <a class="ds_site-footer__org-link" title="The Scottish Government" href="http://www.gov.scot/">
                    <img width="300" height="55" class="ds_site-footer__org-logo" src="<@hst.webfile path="/assets/images/logos/scottish-government--min.svg"/>" alt="gov.scot" />
                </a>
            </div>
        </div>
    </div>
</footer>
