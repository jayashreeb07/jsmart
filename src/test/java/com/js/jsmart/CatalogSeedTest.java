package com.js.jsmart;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Guards the 50-product marketplace catalog in seed_products.sql:
 * exact count, category distribution, images, prices and stock.
 */
class CatalogSeedTest {

  private List<String[]> rows() throws Exception {
    java.io.InputStream in = getClass().getClassLoader().getResourceAsStream("seed_products.sql");
    assertNotNull(in, "seed_products.sql must exist on classpath");
    String sql = new String(in.readAllBytes(), StandardCharsets.UTF_8);
    Pattern row = Pattern.compile(
        "\\((\\d+),\\s*(\\d+),\\s*'((?:[^']|'')*)',\\s*'((?:[^']|'')*)',\\s*([\\d.]+),\\s*(\\d+),"
        + "\\s*'((?:[^']|'')*)',\\s*'((?:[^']|'')*)'\\)");
    Matcher m = row.matcher(sql);
    List<String[]> out = new ArrayList<>();
    while (m.find()) {
      out.add(new String[]{m.group(1), m.group(2), m.group(3), m.group(4),
          m.group(5), m.group(6), m.group(7), m.group(8)});
    }
    return out;
  }

  @Test
  void exactlyFiftyProducts() throws Exception {
    assertEquals(50, rows().size(), "Catalog must contain exactly 50 products");
  }

  @Test
  void categoryDistribution() throws Exception {
    Map<String, Integer> expected = new LinkedHashMap<>();
    expected.put("Mobiles & Electronics", 8);
    expected.put("Fashion", 8);
    expected.put("Footwear", 6);
    expected.put("Home & Kitchen", 7);
    expected.put("Beauty & Personal Care", 5);
    expected.put("Bags, Watches & Accessories", 5);
    expected.put("Books & Stationery", 4);
    expected.put("Toys & Kids", 3);
    expected.put("Sports & Fitness", 4);
    Map<String, Integer> actual = new LinkedHashMap<>();
    for (String[] r : rows()) {
      actual.merge(r[6], 1, Integer::sum);
    }
    assertEquals(expected, actual, "Category distribution must match the marketplace plan");
  }

  @Test
  void everyProductHasImagePriceAndStock() throws Exception {
    List<String[]> all = rows();
    assertEquals(50, all.size());
    java.util.Set<String> ids = new java.util.HashSet<>();
    java.util.Set<String> names = new java.util.HashSet<>();
    for (String[] r : all) {
      assertTrue(ids.add(r[0]), "Duplicate product id " + r[0]);
      assertTrue(names.add(r[2]), "Duplicate product name " + r[2]);
      assertFalse(r[2].isBlank(), "Product " + r[0] + " needs a name");
      assertFalse(r[3].isBlank(), "Product " + r[0] + " needs a description");
      assertTrue(new BigDecimal(r[4]).compareTo(BigDecimal.ZERO) > 0, "Price must be positive " + r[0]);
      assertTrue(Integer.parseInt(r[5]) >= 0, "Stock must be >= 0 " + r[0]);
      assertTrue(r[7].startsWith("http"), "Product " + r[0] + " needs an image URL");
      assertTrue(r[7].contains("unsplash.com/"), "Image should be a stable product photo " + r[0]);
    }
  }
}
