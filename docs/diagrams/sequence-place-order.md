# D3 Sequence — place order (mirrors CheckoutServlet → OrderService → DAO)
```mermaid
sequenceDiagram
  participant B as Browser
  participant C as CheckoutServlet
  participant S as OrderService
  participant D as JdbcOrderDAO
  participant DB as H2 (HikariCP)
  B->>C: POST /checkout (confirm mock payment)
  C->>S: checkout(buyerId, cart, mockPaymentOk)
  S->>S: validate cart/stock/payment
  S->>D: createWithItems(con, order)
  D->>DB: BEGIN; INSERT orders; INSERT order_items; UPDATE stock; DELETE cart
  alt stock insufficient
    DB-->>D: 0 rows updated
    D-->>S: SQLException
    S->>DB: ROLLBACK
    S-->>C: 400 CHECKOUT_FAILED
  else ok
    D->>DB: COMMIT
    D-->>S: orderId
    S-->>C: orderId
  end
  C-->>B: redirect /orders?success=id (or 201 JSON)
```
