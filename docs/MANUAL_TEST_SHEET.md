# Manual test sheet (end-to-end)
1. Register BUYER (201, envelope ok) 2. Login (session, timeout 30m) 3. Logout 4. Login again
5. Browse /products 6. Search `q=mouse` 7. Filter `category=Electronics` 8. Detail `/product?id=1`
9. Add cart 10. Update qty 11. Remove 12. Add again 13. Checkout confirm 14. Mock payment ok
15. Order created 16. Stock reduced 17. Buyer history shows order
18. Seller login 19. Create listing 20. Edit 21. Delete own 22. Incoming order visible
23. Status PENDING→CONFIRMED→SHIPPED→DELIVERED; buyer cancel path
24. Review delivered product (201) 25. Appears on detail
26. Admin login 27. Users list 28. Orders list 29. Remove listing
30. Buyer→/seller/* 403 31. Buyer→/admin/* 403 32. Bad id 404 33. Qty 0 → 400
34. Exceed stock → 400 35. Rating 9 → 400; unpurchased → 400 36. `' OR '1'='1` harmless
37. `<script>` stored, escaped via c:out 38. Chat mock reply 39. 11th msg rate-limited
40. Repeat question cached 41. Gemini down → degraded response 42. `/api/v1/health` UP/UP
