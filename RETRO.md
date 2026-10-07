# Retro
## Sprint 1 (MVP: auth, products, browse)
- Worked: layered skeleton compiled early; PreparedStatement everywhere from day one
- Didn't: JSP wiring took longer than services
- Change: build UI shell before deep service logic next time
## Sprint 2 (cart, checkout, orders, reviews, admin)
- Worked: single-transaction checkout with rollback tests
- Didn't: status-transition rules needed 2 iterations
- Change: encode workflow as explicit transition table (done in OrderService)
## Sprint 3 (chatbot, hardening, docs, CI)
- Worked: mock-first chatbot kept build offline-safe
- Didn't: MDC cleanup initially missed in filter
- Change: always try/finally MDC.clear (done)
