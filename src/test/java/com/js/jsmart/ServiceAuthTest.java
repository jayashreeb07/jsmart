package com.js.jsmart;

import com.js.jsmart.dao.OrderDAO;
import com.js.jsmart.dao.ProductDAO;
import com.js.jsmart.dao.ReviewDAO;
import com.js.jsmart.dto.ProductDTO;
import com.js.jsmart.exception.ForbiddenException;
import com.js.jsmart.model.Order;
import com.js.jsmart.model.OrderStatus;
import com.js.jsmart.model.Product;
import com.js.jsmart.service.OrderService;
import com.js.jsmart.service.ProductService;
import com.js.jsmart.service.ReviewService;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Service authorization + workflow tests.
 */
@ExtendWith(MockitoExtension.class)
class ServiceAuthTest {
  @Mock ProductDAO productDAO;
  @Mock OrderDAO orderDAO;
  @Mock ReviewDAO reviewDAO;

  @Test
  void sellerCannotEditOthersProduct() throws Exception {
    Product p = new Product();
    p.setId(1);
    p.setSellerId(99);
    when(productDAO.findById(1)).thenReturn(Optional.of(p));
    ProductService s = new ProductService(productDAO);
    ProductDTO dto = new ProductDTO();
    dto.setName("X");
    dto.setPrice(new BigDecimal("10.00"));
    dto.setStockQty(1);
    assertThrows(ForbiddenException.class, () -> s.update(2, 1, dto));
  }

  @Test
  void orderWorkflowForwardOnly() throws Exception {
    Order o = new Order();
    o.setId(5);
    o.setStatus(OrderStatus.PENDING);
    when(orderDAO.findById(5)).thenReturn(Optional.of(o));
    OrderService s = new OrderService(orderDAO, null);
    assertThrows(Exception.class, () -> s.updateStatus(5, "SHIPPED", "SELLER"));
    s.updateStatus(5, "CONFIRMED", "SELLER");
    verify(orderDAO).updateStatus(5, "CONFIRMED");
  }

  @Test
  void reviewRequiresDelivered() throws Exception {
    when(orderDAO.hasDeliveredPurchase(1, 2)).thenReturn(false);
    ReviewService s = new ReviewService(reviewDAO, orderDAO);
    assertThrows(Exception.class, () -> s.add(1, 2, 5, "great"));
  }

  @Test
  void reviewRatingRange() {
    ReviewService s = new ReviewService(reviewDAO, orderDAO);
    assertThrows(Exception.class, () -> s.add(1, 2, 9, "x"));
  }

  @Test
  void buyerCannotSkipWorkflow() throws Exception {
    Order o = new Order();
    o.setId(6);
    o.setStatus(OrderStatus.PENDING);
    when(orderDAO.findById(6)).thenReturn(Optional.of(o));
    OrderService s = new OrderService(orderDAO, null);
    assertThrows(Exception.class, () -> s.updateStatus(6, "SHIPPED", "BUYER"));
  }

  @Test
  void listSearchDelegates() throws Exception {
    when(productDAO.search(any(), any(), anyInt(), anyInt())).thenReturn(List.of());
    ProductService s = new ProductService(productDAO);
    assertTrue(s.browse(null, null, 10, 0).isEmpty());
  }
}
