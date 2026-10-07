// Wishlist hearts (client-side only, localStorage). No backend changes.
(function () {
  var KEY = 'jsmart-wishlist';
  function load() {
    try {
      return JSON.parse(localStorage.getItem(KEY) || '[]');
    } catch (e) {
      return [];
    }
  }
  function save(list) {
    try {
      localStorage.setItem(KEY, JSON.stringify(list));
    } catch (e) {
      // ignore
    }
  }
  function paint() {
    var list = load();
    document.querySelectorAll('[data-wish]').forEach(function (btn) {
      btn.classList.toggle('active', list.indexOf(btn.getAttribute('data-wish')) !== -1);
    });
  }
  document.addEventListener('click', function (e) {
    var btn = e.target.closest ? e.target.closest('[data-wish]') : null;
    if (!btn) {
      return;
    }
    e.preventDefault();
    var id = btn.getAttribute('data-wish');
    var list = load();
    var i = list.indexOf(id);
    if (i === -1) {
      list.push(id);
    } else {
      list.splice(i, 1);
    }
    save(list);
    paint();
  });
  document.addEventListener('DOMContentLoaded', paint);
})();
