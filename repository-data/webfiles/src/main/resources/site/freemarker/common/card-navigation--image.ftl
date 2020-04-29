<#include "include/imports.ftl">

<div class="ds_wrapper">

    <ol class="ds_category-list  ds_category-list--grid  ds_category-list--narrow">
        <#list children as child>
            <@hst.link var="link" hippobean=child/>
            <li class="ds_card  ds_card--no-padding">
                <div class="ds_card__media">
                    <#if child.cardImage??>
                        <img alt="${document.title}" class="ds_card__image"
                            src="<@hst.link hippobean=child.cardImage.cardTwoThreeFiveS/>"
                            srcset="<@hst.link hippobean=child.cardImage.cardTwoThreeFiveS/> 290w,
                                <@hst.link hippobean=child.cardImage.cardTwoThreeFiveSDbl/> 580w,
                                <@hst.link hippobean=child.cardImage.cardTwoThreeFiveM/> 355w,
                                <@hst.link hippobean=child.cardImage.cardTwoThreeFiveMDbl/> 710w,
                                <@hst.link hippobean=child.cardImage.cardTwoThreeFiveL/> 306w,
                                <@hst.link hippobean=child.cardImage.cardTwoThreeFiveLDbl/> 612w,
                                <@hst.link hippobean=child.cardImage.cardTwoThreeFiveXl/> 373w,
                                <@hst.link hippobean=child.cardImage.cardTwoThreeFiveXlDbl/> 746w"
                            sizes="(min-width:1200px) 373px, (min-width:920px) 306px, (min-width:768px) 355px, 290w"/>
                    </#if>
                </div>
                <div class="ds_card__content  ds_category-item">
                    <h2 class="ds_category-item__title">
                        <a data-navigation="category-item-${child?index + 1}" class="ds_card__cover-link" href="${link}">${child.title}</a>
                    </h2>

                    <div class="ds_category-item__summary">
                        ${child.summary}
                    </div>
                </div>
            </li>
        </#list>
    </ol>

</div>
