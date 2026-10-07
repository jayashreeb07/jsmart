// Cart remove via fetch + JSON API
document.addEventListener('click', async (e) => {
  const id = e.target && e.target.getAttribute && e.target.getAttribute('data-remove');
  if (!id) return;
  const ctx = window.location.pathname.split('/')[1] ? '/' + window.location.pathname.split('/')[1] : '';
  await fetch(ctx + '/api/v1/cart/' + id, { method: 'DELETE' });
  window.location.reload();
});
