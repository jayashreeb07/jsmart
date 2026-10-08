# JS Mart

JS Mart is an e-commerce marketplace built with Java Servlets, JSP and JDBC as a college capstone project. Buyers can browse products, use a cart, place orders and track them, while sellers can manage their products and orders.

## Features

* Buyer and seller registration/login
* Role-based access for buyer, seller and admin
* Product browsing and category filtering
* Product search and product details
* Shopping cart
* Checkout with mock payment flow
* Order history and order tracking
* Seller product management
* Seller order management
* Admin user/order management and moderation
* Product ratings and reviews
* Shopping assistant chatbot
* Success screens for important actions
* Security protections such as password hashing and session handling

## Tech Stack

* Java 17
* Servlets/JSP
* JDBC
* H2
* HikariCP
* Maven
* Tomcat 9
* JavaScript
* CSS
* Gson
* BCrypt
* JUnit 5
* Mockito

## How the Application Works

Buyer:
Browse → Search → View Product → Add to Cart → Checkout → View Orders → Track Order → Review

Seller:
Login → Manage Products → View Incoming Orders → Update Order Status

Admin:
Login → Manage Users/Orders → Moderate Products

## Project Structure

```
Browser
↓
Servlets / Controllers
↓
Services
↓
DAO
↓
HikariCP
↓
H2 Database
```

Main Java packages: `controller`, `service`, `dao`, `model`, `dto`, `filter`, `listener`, `util`, `exception`

## Running the Project

```bash
git clone <repository-url>
cd "JS Mart"
mvn -B clean verify
```

This builds the project and runs all tests. It generates `target/jsmart.war`, which can be deployed to Tomcat 9 by copying it into the Tomcat `webapps` folder and starting the server.

Once running, open:

http://localhost:8080/jsmart/

Health check:

http://localhost:8080/jsmart/api/v1/health

## Testing

The latest verification has:

* Maven build successful
* 26/26 tests passing
* WAR generated successfully

Run everything with:

```bash
mvn -B clean verify
```

## Chatbot

JS Mart includes a shopping/help chatbot with:

* Mock provider
* Optional Gemini provider
* Rate limiting
* Response caching
* Fallback response handling

The mock provider works out of the box with no setup. The Gemini provider is optional and needs its own API key configuration.

## Order Tracking

Buyers can follow their order progress through:

PENDING → CONFIRMED → SHIPPED → DELIVERED

An order can also be CANCELLED. Sellers and admins update the status, and the buyer tracking page always shows the current status from the database.

## Screenshots

Project screenshots can be added here.

## Deployment

Deployment documentation is available in `docs/DEPLOYMENT.md`, and the project includes a `Dockerfile` for container-based deployment.

## Known Limitations

* Payment is a mock flow.
* Gemini chatbot requires an API key if enabled.
* Some optional features were intentionally left out.

## Author

Jayashree B
