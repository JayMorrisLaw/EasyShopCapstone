# Capstone Project - Product Data Access Layer (MySqlProductDao)

## 📘 Project Overview

This project is part of a Capstone application that implements a **Data Access Layer (DAO)** for managing product information in a MySQL database. It uses **Java**, **JDBC**, and **Spring Framework**, and adheres to the **DAO Design Pattern** to decouple business logic from persistent storage logic.

The key component in this repository is the `MySqlProductDao` class, which handles the retrieval, creation, updating, and deletion of product records from the `products` table in a structured, reliable, and testable way.

## 🛠️ Technologies Used

* Java 17+
* MySQL
* Spring Framework (for dependency injection with `@Component`)
* JDBC (Java Database Connectivity)
* SQL
* Maven or Gradle (depending on setup)

---

## 📦 Key Features

The `MySqlProductDao` class implements the following methods from the `ProductDao` interface:

| Method Name             | Description                                                             |
| ----------------------- | ----------------------------------------------------------------------- |
| `search(...)`           | Returns products filtered by optional category, price range, and color. |
| `listByCategoryId(...)` | Returns all products from a specific category.                          |
| `getById(...)`          | Retrieves a single product by ID.                                       |
| `create(...)`           | Adds a new product and returns it with the generated ID.                |
| `update(...)`           | Updates an existing product’s details.                                  |
| `delete(...)`           | Deletes a product by ID.                                                |
| `getByCategoryId(...)`  | Delegates to `listByCategoryId()` (bug fix applied).                    |

---

## 🐞 Bug Fixes Explained

### 1. ✅ Fixed SQL Query Logic in `search(...)`

**Problem:**
The original SQL query only compared the product's price against a **single value** (max price), and redundantly used the same variable twice per condition. It did **not handle minimum price filtering** correctly.

```java
// ❌ Buggy (Commented Out)
String sql = "SELECT * FROM products " +
             "WHERE (category_id = ? OR ? = -1) " +
             "AND (price <= ? OR ? = -1) " +
             "AND (color = ? OR ? = '') ";
```

**Fix:**
Refactored the query to include both minimum and maximum price filters **with optional logic using sentinel values** (`-1` and `""`).

```java
// ✅ Fixed
String sql = "SELECT * FROM products " +
             "WHERE (? = -1 OR category_id = ?) " +
             "AND (? = -1 OR price >= ?) " +
             "AND (? = -1 OR price <= ?) " +
             "AND (? = '' OR color = ?)";
```

**Binding Fix:**
Updated the parameter bindings in the correct order for the updated SQL query:

```java
statement.setInt(1, categoryId);
statement.setInt(2, categoryId);
statement.setBigDecimal(3, minPrice);
statement.setBigDecimal(4, minPrice);
statement.setBigDecimal(5, maxPrice);
statement.setBigDecimal(6, maxPrice);
statement.setString(7, color);
statement.setString(8, color);
```

---

### 2. 🛠️ Fixed Return Bug in `getByCategoryId(...)`

**Problem:**
This method originally returned an empty list regardless of input.

```java
// ❌ Incorrect implementation
return List.of(); // Always returns an empty list
```

**Fix:**
Reused the already functional method `listByCategoryId(int categoryId)` for correct logic.

```java
// ✅ Corrected implementation
return listByCategoryId(categoryId);
```

---

## 🧪 Usage & Testing

* This DAO can be used within a Spring Boot application via dependency injection.
* Ensure your MySQL database contains a `products` table with appropriate columns.
* All methods can be tested using unit tests or integrated with a controller/service layer.

---

## 💃 Sample `products` Table Schema

```sql
CREATE TABLE products (
    product_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255),
    price DECIMAL(10,2),
    category_id INT,
    description TEXT,
    color VARCHAR(50),
    image_url TEXT,
    stock INT,
    featured BOOLEAN
);
```

---

## 🧠 Lessons & Skills Demonstrated

* Understanding and implementation of the **DAO pattern**.
* Usage of **prepared statements** to prevent SQL injection.
* Clean error handling using `try-with-resources` for DB operations.
* Implementation of **optional filters** in SQL using **sentinel values**.
* Spring component design using `@Component`.

---

## 📁 File Structure

```
src/
├── org.yearup.models/
│   └── Product.java
├── org.yearup.data/
│   └── ProductDao.java
├── org.yearup.data.mysql/
    └── MySqlProductDao.java
```

## 📜 License

This project is educational and may be used freely for learning and non-commercial purposes.

---

## 👨‍💼 Author

Created as part of a Java Capstone Project at \[Year Up / Custom Curriculum].

---

## 📞 Credits

Special thanks to Raymond for being a great teacher and guide throughout this project.


