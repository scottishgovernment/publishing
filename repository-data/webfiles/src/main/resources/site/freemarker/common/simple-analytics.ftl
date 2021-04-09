<#if simpleAnalyticsEnabled = true>
<script>
// this polyfill is required until Simple Analytics fix this issue in their code
(function () {
  if ( typeof window.CustomEvent === "function" ) return false; //If not IE

  function CustomEvent ( event, params ) {
    params = params || { bubbles: false, cancelable: false, detail: undefined };
    var evt = document.createEvent( 'CustomEvent' );
    evt.initCustomEvent( event, params.bubbles, params.cancelable, params.detail );
    return evt;
   }

  CustomEvent.prototype = window.Event.prototype;
  window.Event = CustomEvent;
  window.CustomEvent = CustomEvent;
})();
</script>

<script async defer src="https://sa.mygov.scot/app.js"></script>

<script type="module" src="/assets/scripts/simple-analytics.js"></script>
<script nomodule src="/assets/scripts/simple-analytics.es5.js"></script>

<noscript><img src="https://sa.mygov.scot/image.gif" alt=""></noscript>
</#if>
