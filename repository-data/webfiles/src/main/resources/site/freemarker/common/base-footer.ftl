<#include "include/imports.ftl">

<footer class="ds_site-footer">
    <div class="ds_wrapper">
        <div class="ds_site-footer__content">
            <ul class="ds_site-footer__site-items">
                <#list pageable.items as item>
                    <li class="ds_site-items__item">
                        <@hst.link var="link" hippobean=item />
                        <a data-footer="footer-link-${item?index + 1}" href="${link}">${item.title}</a>
                    </li>
                </#list>
            </ul>

            <div class="ds_site-footer__copyright">
                <a data-footer="footer-copyright" class="ds_site-footer__copyright-logo" href="http://www.nationalarchives.gov.uk/doc/open-government-licence/version/3/">
                    <img src="<@hst.webfile path="/assets/images/logos/ogl.svg"/>" alt="Open Government License" />
                </a>
                <p>All content is available under the <a data-footer="copyright" href="http://www.nationalarchives.gov.uk/doc/open-government-licence/version/3/">Open Government Licence v3.0</a>, except for graphic assets and where otherwise stated</p>
                <p>&copy; Crown Copyright</p>
            </div>

            <div class="ds_site-footer__org">
                <a data-footer="footer-logo" class="ds_site-footer__org-link" title="The Scottish Government" href="http://www.gov.scot/">
                    <img class="ds_site-footer__org-logo" src="<@hst.webfile path="/assets/images/logos/scottish-government--min--reversed.svg"/>" alt="gov.scot" />
                </a>
            </div>
        </div>
    </div>
</footer>
