<div class="ds_wrapper">
    <h1>Latest news, show images: ${showImages?c}</h1>
    <#list news as newsItem>
        <li>
            <a href="<@hst.link hippobean=newsItem/>">${newsItem.title}</a>
        </li>
    </#list>
</div>