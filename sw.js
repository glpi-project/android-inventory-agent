---
---

self.addEventListener('install', function(e) {
  e.waitUntil(
    caches.open('airhorner').then(function(cache) {
      return cache.addAll([
        '{{ site.baseurl }}/',
        '{{ site.baseurl }}/?homescreen=1',
        '{{ "/index.html" | relative_url }}',
        '{{ "/index.html" | relative_url }}?homescreen=1',
        '{{ "/css/flyve-mdm.min.css" | relative_url }}',
        '{{ "/css/main.css" | relative_url }}',
        '{{ "css/syntax.css" | relative_url }}',
        '{{ "images/lenovo.jpg" | relative_url }}',
        '{{ "images/xperia.jpg" | relative_url }}',
        '{{ "images/blackBerry.jpg" | relative_url }}',
        '{{ "images/typo.png" | relative_url }}',
        '{{ "images/logo.png" | relative_url }}',
        '{{ "js/app.js" | relative_url }}',
        '{{ "js/jquery.min.js" | relative_url }}',
        '{{ "js/bootstrap.min.js" | relative_url }}',
        '{{ "images/logo.png" | relative_url }}',
        '{{ "manifest.json" | relative_url }}',
        '{{ "fonts/glyphs/winjs-symbols.ttf" | relative_url }}',
        '{{ "images/logo.png" | relative_url }}',
        '{{ "fonts/selawk.ttf" | relative_url }}',
      ])
    })
  )
})

self.addEventListener('fetch', function(event) {
  console.log(event.request.url)
  event.respondWith(
    caches.match(event.request).then(function(response) {
      return response || fetch(event.request)
    })
  )
})