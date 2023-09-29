!function(){"use strict";var n=window.location,r=window.document,o=r.currentScript,p=o.getAttribute("data-api")||"https://plausible.io/api/event";function l(t,e){t&&console.warn("Ignoring Event: "+t),e&&e.callback&&e.callback()}function t(t,e){if(/^localhost$|^127(\.[0-9]+){0,2}\.[0-9]+$|^\[::1?\]$/.test(n.hostname)||"file:"===n.protocol)return l("localhost",e);if(window._phantom||window.__nightmare||window.navigator.webdriver||window.Cypress)return l(null,e);try{if("true"===window.localStorage.plausible_ignore)return l("localStorage flag",e)}catch(t){}var a={},i=(a.n=t,a.u=n.href,a.d=o.getAttribute("data-domain"),a.r=r.referrer||null,e&&e.meta&&(a.m=JSON.stringify(e.meta)),e&&e.props&&(a.p=e.props),new XMLHttpRequest);i.open("POST",p,!0),i.setRequestHeader("Content-Type","text/plain"),i.send(JSON.stringify(a)),i.onreadystatechange=function(){4===i.readyState&&e&&e.callback&&e.callback()}}var e=window.plausible&&window.plausible.q||[];window.plausible=t;for(var a,i=0;i<e.length;i++)t.apply(this,e[i]);function s(){a!==n.pathname&&(a=n.pathname,t("pageview"))}var c,u=window.history;u.pushState&&(c=u.pushState,u.pushState=function(){c.apply(this,arguments),s()},window.addEventListener("popstate",s)),"prerender"===r.visibilityState?r.addEventListener("visibilitychange",function(){a||"visible"!==r.visibilityState||s()}):s();var d=1;function f(t){var e,a,i,n,r,o,p;function l(){n||(n=!0,window.location=i.href)}"auxclick"===t.type&&t.button!==d||(e=function(t){for(;t&&(void 0===t.tagName||!(e=t)||!e.tagName||"a"!==e.tagName.toLowerCase()||!t.href);)t=t.parentNode;var e;return t}(t.target),a=e&&e.href&&e.href.split("?")[0],(o=a)&&(p=o.split(".").pop(),g.some(function(t){return t===p}))&&(n=!(o={name:"File Download",props:{url:a}}),!function(t,e){if(!t.defaultPrevented)return e=!e.target||e.target.match(/^_(self|parent|top)$/i),t=!(t.ctrlKey||t.metaKey||t.shiftKey)&&"click"===t.type,e&&t}(a=t,i=e)?(r={props:o.props},plausible(o.name,r)):(r={props:o.props,callback:l},plausible(o.name,r),setTimeout(l,5e3),a.preventDefault())))}r.addEventListener("click",f),r.addEventListener("auxclick",f);var u=["pdf","xlsx","docx","txt","rtf","csv","exe","key","pps","ppt","pptx","7z","pkg","rar","gz","zip","avi","mov","mp4","mpeg","wmv","midi","mp3","wav","wma"],w=o.getAttribute("file-types"),v=o.getAttribute("add-file-types"),g=w&&w.split(",")||v&&v.split(",").concat(u)||u}();
