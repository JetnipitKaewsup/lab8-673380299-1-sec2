# Product Management System

ระบบจัดการข้อมูลสินค้า พัฒนาด้วย **Spring Boot** โดยใช้สถาปัตยกรรมแบบ Layered Architecture แบ่งหน้าที่ของระบบออกเป็น Entity, Repository, Service และ Controller เพื่อให้โค้ดมีโครงสร้างชัดเจนและง่ายต่อการดูแล

ระบบรองรับการจัดการข้อมูลสินค้าแบบ CRUD รวมถึงข้อมูลรายละเอียดสินค้าและรีวิวสินค้า และมีการใช้ **Strategy Pattern** สำหรับคำนวณราคาหลังส่วนลด เช่น ส่วนลดสมาชิกและส่วนลดเทศกาล

---

## รายงานอภิปราย

[Lab 7.pdf]

---

## Technologies

* Java
* Spring Boot
* Spring Data JPA
* PostgreSQL
* Thymeleaf
* Maven

---

## Features

* แสดงรายการสินค้าทั้งหมด
* แสดงข้อมูลสินค้าตาม ID
* เพิ่มข้อมูลสินค้า
* แก้ไขข้อมูลสินค้า
* ลบข้อมูลสินค้า
* จัดการรายละเอียดสินค้า
* จัดการรีวิวสินค้า
* คำนวณราคาสินค้าหลังส่วนลด
* รองรับส่วนลดหลายประเภทด้วย Strategy Pattern
* เชื่อมต่อข้อมูลกับ PostgreSQL
* ใช้ JPA สำหรับ Mapping ความสัมพันธ์ระหว่าง Entity

---

## Project Structure

```text
src/
└── main/
    ├── java/com/example/demo/
    │   ├── model/
    │   │   ├── Product.java
    │   │   ├── ProductDetail.java
    │   │   └── Review.java
    │   │
    │   ├── repository/
    │   │   ├── ProductRepository.java
    │   │   ├── ProductDetailRepository.java
    │   │   └── ReviewRepository.java
    │   │
    │   ├── service/
    │   │   └── ProductService.java
    │   │
    │   ├── controller/
    │   │   └── ProductController.java
    │   │
    │   └── strategy/
    │       ├── DiscountStrategy.java
    │       ├── DiscountContext.java
    │       ├── NoDiscountStrategy.java
    │       ├── MemberDiscountStrategy.java
    │       └── SeasonalSaleStrategy.java
    │
    └── resources/
        ├── templates/
        │   └── products/
        │       └── ...
        │
        └── application.properties
```

---

## Architecture

ระบบแบ่งออกเป็น Layer หลักดังนี้

```text
Browser
   │
   ▼
Controller
   │
   ▼
Service
   │
   ▼
Repository
   │
   ▼
PostgreSQL
```

โดย Entity จะทำหน้าที่แทนข้อมูลและกำหนดความสัมพันธ์ระหว่างข้อมูลในฐานข้อมูล

---

## Entity

ระบบประกอบด้วย Entity หลัก 3 คลาส ได้แก่

```text
Product
   │
   ├── 1 : 1 ── ProductDetail
   │
   └── 1 : N ── Review
```

### Product

ไฟล์ `Product.java`

ทำหน้าที่เป็น Entity หลักสำหรับแทนข้อมูลสินค้า และ Mapping กับตาราง `products` ใน PostgreSQL

ข้อมูลหลักของสินค้า ได้แก่

- ID
- ชื่อสินค้า
- หมวดหมู่
- แบรนด์
- จำนวนสินค้าในคลัง
- ราคา
- ประเภทส่วนลด
- รายละเอียดสินค้า
- รีวิวสินค้า

ตัวอย่างข้อมูล:

```text
Product
├── id
├── name
├── category
├── brand
├── stock
├── price
├── discountType
├── detail
└── reviews
```

นอกจากนี้ `Product` ยังมีเมธอด

```text
getDiscountedPrice()
```

สำหรับคำนวณราคาหลังส่วนลด โดยเรียกใช้ Strategy ที่เหมาะสมตาม `discountType`

สามารถนำไปใช้ใน Thymeleaf ได้ เช่น

```text
${product.getDiscountedPrice()}
```

---

### ProductDetail

ไฟล์ `ProductDetail.java`

ทำหน้าที่เก็บรายละเอียดเพิ่มเติมของสินค้า โดยแยกออกจากข้อมูลหลักของ Product

ข้อมูลประกอบด้วย

- ID
- รายละเอียดสินค้า
- การรับประกัน
- น้ำหนัก
- ขนาด
- ประเทศที่ผลิต

ความสัมพันธ์กับ `Product` เป็นแบบ

```text
Product 1 ─── 1 ProductDetail
```

โดย `Product` เป็นฝั่งที่เก็บ Foreign Key ผ่าน

```text
detail_id
```

---

### Review

ไฟล์ `Review.java`

ทำหน้าที่เก็บข้อมูลรีวิวของสินค้า

ข้อมูลประกอบด้วย

- ID
- ผู้รีวิว
- คะแนน
- ความคิดเห็น
- วันที่รีวิว
- สินค้าที่ถูกรีวิว

ความสัมพันธ์กับ `Product` เป็นแบบ

```text
Product 1 ─── N Review
```

โดย `Review` เป็นฝั่ง Many และเป็นเจ้าของความสัมพันธ์ เนื่องจากมี Foreign Key

```text
product_id
```

---

## Repository

ระบบแยก Repository ตาม Entity เพื่อให้แต่ละ Repository รับผิดชอบการเข้าถึงข้อมูลของ Entity ที่เกี่ยวข้อง

### ProductRepository

ไฟล์ `ProductRepository.java`

ทำหน้าที่จัดการข้อมูลของ `Product`

```java
public interface ProductRepository
        extends JpaRepository<Product, Long> {
}
```

---

### ProductDetailRepository

ไฟล์ `ProductDetailRepository.java`

ทำหน้าที่จัดการข้อมูลของ `ProductDetail`

```java
public interface ProductDetailRepository
        extends JpaRepository<ProductDetail, Long> {
}
```

---

### ReviewRepository

ไฟล์ `ReviewRepository.java`

ทำหน้าที่จัดการข้อมูลของ `Review`

```java
public interface ReviewRepository
        extends JpaRepository<Review, Long> {
}
```

การแยก Repository ช่วยให้แต่ละส่วนมีหน้าที่ชัดเจน และไม่ให้ Repository หนึ่งต้องรับผิดชอบข้อมูลของหลาย Entity

---

## Service

ไฟล์ `ProductService.java`

ทำหน้าที่จัดการ Business Logic และเป็นตัวกลางระหว่าง Controller กับ Repository

คำสั่งหลักของระบบ ได้แก่

```text
getAllProducts()
getProductById(Long id)
addProduct(Product product)
updateProduct(Long id, Product product)
deleteProduct(Long id)
```

`ProductService` ใช้ **Constructor Injection** เพื่อรับ Dependency จาก Spring

```java
public ProductService(
        ProductRepository productRepository,
        ProductDetailRepository productDetailRepository) {

    this.productRepository = productRepository;
    this.productDetailRepository = productDetailRepository;
}
```

ในการแก้ไขสินค้า ระบบจะจัดการข้อมูลของ

```text
Product
   │
   └── ProductDetail
```

โดยไม่จำเป็นต้องเรียก `ReviewRepository` ในกรณีที่ไม่ได้แก้ไขข้อมูล Review

---

## Controller

ไฟล์ `ProductController.java`

ทำหน้าที่รับ HTTP Request จาก Browser และเรียกใช้ `ProductService` สำหรับดำเนินการต่าง ๆ

Controller ใช้

```java
@Controller
@RequestMapping("/products")
```

เพื่อกำหนดให้ Controller นี้จัดการ Request ที่เกี่ยวข้องกับ Product

Controller ใช้ **Constructor Injection**

```java
public ProductController(ProductService productService) {
    this.productService = productService;
}
```

ตัวอย่างการทำงานของ Controller ได้แก่

```text
GET  /products
GET  /products/add
GET  /products/edit/{id}
POST /products/save
POST /products/update/{id}
POST /products/delete/{id}
```

---

## Strategy Pattern

ระบบใช้ **Strategy Pattern** สำหรับแยกวิธีการคำนวณส่วนลดแต่ละประเภทออกจากกัน

```text
DiscountStrategy
       │
       ├── NoDiscountStrategy
       ├── MemberDiscountStrategy
       └── SeasonalSaleStrategy
```

การแยกแต่ละ Algorithm ออกจากกันทำให้ Product ไม่จำเป็นต้องเขียนเงื่อนไขการคำนวณส่วนลดทุกประเภทไว้ภายในคลาสเดียว

---

### DiscountStrategy

ไฟล์ `DiscountStrategy.java`

เป็น Interface ที่กำหนดรูปแบบมาตรฐานสำหรับการคำนวณส่วนลด

```java
public interface DiscountStrategy {

    double calculate(double price);

}
```

ทุก Strategy จะต้อง Implement เมธอด `calculate()`

---

### NoDiscountStrategy

ไฟล์ `NoDiscountStrategy.java`

ใช้เมื่อสินค้าไม่มีส่วนลด

```text
ราคาสุทธิ = ราคาปกติ
```

ตัวอย่าง

```text
ราคา 1,000 บาท
→ ราคาสุทธิ 1,000 บาท
```

---

### MemberDiscountStrategy

ไฟล์ `MemberDiscountStrategy.java`

ใช้สำหรับส่วนลดสมาชิก

```text
ราคาสุทธิ = ราคา × 0.90
```

ตัวอย่าง

```text
ราคา 1,000 บาท
→ ส่วนลด 10%
→ ราคาสุทธิ 900 บาท
```

---

### SeasonalSaleStrategy

ไฟล์ `SeasonalSaleStrategy.java`

ใช้สำหรับส่วนลดในช่วงเทศกาล

```text
ราคาสุทธิ = ราคา × 0.80
```

ตัวอย่าง

```text
ราคา 1,000 บาท
→ ส่วนลด 20%
→ ราคาสุทธิ 800 บาท
```

---

### DiscountContext

ไฟล์ `DiscountContext.java`

ทำหน้าที่เลือก Strategy ที่เหมาะสมตามประเภทส่วนลดของสินค้า

แนวคิดการทำงาน:

```text
Product
   │
   │ discountType
   ▼
DiscountContext
   │
   ├── MEMBER
   │      ↓
   │ MemberDiscountStrategy
   │
   ├── SEASONAL
   │      ↓
   │ SeasonalSaleStrategy
   │
   └── NONE
          ↓
       NoDiscountStrategy
```

จากนั้น Strategy ที่ถูกเลือกจะทำหน้าที่คำนวณราคาสุทธิ

```text
Product
   ↓
getDiscountedPrice()
   ↓
DiscountContext
   ↓
DiscountStrategy
   ↓
calculate(price)
   ↓
ราคาหลังส่วนลด
```

การออกแบบด้วย Strategy Pattern ช่วยลดการเขียนเงื่อนไขการคำนวณส่วนลดไว้ใน `Product` และช่วยให้สามารถเพิ่ม Algorithm การคำนวณส่วนลดใหม่ได้ง่ายขึ้น

---

## Database

ระบบใช้ **PostgreSQL** เป็นฐานข้อมูล โดยมีตารางหลัก 3 ตาราง ได้แก่

```text
products
products_detail
reviews
```

ความสัมพันธ์ของตารางสามารถสรุปได้ดังนี้

```text
products
   │
   │ detail_id
   ▼
products_detail

products
   │
   │ product_id
   ▼
reviews
```

### products

เก็บข้อมูลหลักของสินค้า เช่น

```text
products
├── id
├── name
├── category
├── brand
├── stock
├── price
├── discount_type
└── detail_id
```

### products_detail

เก็บข้อมูลรายละเอียดเพิ่มเติมของสินค้า

```text
products_detail
├── id
├── description
├── warranty
├── weight
├── dimensions
└── manufactured_country
```

### reviews

เก็บข้อมูลรีวิวสินค้า

```text
reviews
├── id
├── reviewer
├── rating
├── comment
├── review_date
└── product_id
```

สามารถตรวจสอบข้อมูลใน PostgreSQL ผ่าน **pgAdmin 4** หรือใช้ SQL เช่น

```sql
SELECT * FROM products;

SELECT * FROM products_detail;

SELECT * FROM reviews;
```

---

## Application Flow

เมื่อผู้ใช้ส่ง Request จาก Browser ระบบจะทำงานตามลำดับ

```text
Browser
   ↓
ProductController
   ↓
ProductService
   ↓
ProductRepository
   ↓
JPA / Hibernate
   ↓
PostgreSQL
```

ตัวอย่างการแสดงรายการสินค้า

```text
Browser
   ↓
GET /products
   ↓
ProductController
   ↓
ProductService
   ↓
ProductRepository
   ↓
PostgreSQL
   ↓
Product List
   ↓
Thymeleaf
   ↓
Browser
```

---

### Add Product Flow

```text
Browser
   ↓
POST /products/save
   ↓
ProductController
   ↓
ProductService
   ↓
ProductRepository
   ↓
JPA / Hibernate
   ↓
PostgreSQL
```

หาก Product มี Review ระบบจะกำหนดความสัมพันธ์ระหว่าง Review กับ Product ก่อนบันทึกข้อมูล

```text
Product
   │
   └── Review
         │
         └── product = Product
```

---

### Update Product Flow

เมื่อผู้ใช้แก้ไขสินค้า

```text
Browser
   ↓
POST /products/update/{id}
   ↓
ProductController
   ↓
ProductService
   ├── ProductRepository
   └── ProductDetailRepository
          ↓
       JPA / Hibernate
          ↓
       PostgreSQL
```

การ Update จะปรับปรุงข้อมูลของ

```text
Product
ProductDetail
```

โดยไม่ได้เรียก `ReviewRepository` เนื่องจาก Use Case นี้ไม่ได้แก้ไขข้อมูล Review

---

### Delete Product Flow

```text
Browser
   ↓
POST /products/delete/{id}
   ↓
ProductController
   ↓
ProductService
   ↓
ProductRepository
   ↓
JPA / Hibernate
   ↓
PostgreSQL
```

---

## Discount Calculation Flow

เมื่อหน้า Thymeleaf ต้องการแสดงราคาหลังส่วนลด สามารถเรียก

```text
${product.getDiscountedPrice()}
```

ระบบจะทำงานตามลำดับ

```text
Thymeleaf
   ↓
Product.getDiscountedPrice()
   ↓
DiscountContext
   ↓
เลือก DiscountStrategy
   ↓
calculate(price)
   ↓
Discounted Price
   ↓
Thymeleaf
```

ตัวอย่าง:

```text
price = 1,000
discountType = MEMBER

        ↓

MemberDiscountStrategy

        ↓

1,000 × 0.90

        ↓

900 บาท
```

---

## Dependency Injection

ระบบใช้ **Dependency Injection** เพื่อให้ Spring เป็นผู้จัดการ Dependency ระหว่างคลาสต่าง ๆ โดยใช้ **Constructor Injection**

โครงสร้าง Dependency หลักคือ

```text
ProductController
        │
        │ ProductService
        ▼
ProductService
        │
        ├── ProductRepository
        │
        └── ProductDetailRepository
```

ตัวอย่างใน Controller

```java
public ProductController(ProductService productService) {
    this.productService = productService;
}
```

ตัวอย่างใน Service

```java
public ProductService(
        ProductRepository productRepository,
        ProductDetailRepository productDetailRepository) {

    this.productRepository = productRepository;
    this.productDetailRepository = productDetailRepository;
}
```

การใช้ Constructor Injection ช่วยลดการสร้าง Object ด้วย `new` ภายในคลาส ทำให้ Dependency ของแต่ละคลาสเห็นได้ชัดเจน และช่วยให้ระบบมี **Low Coupling** และ **High Cohesion**

---

## Design Principles

โปรเจกต์นี้นำหลักการออกแบบซอฟต์แวร์มาประยุกต์ใช้ ได้แก่

- **Single Responsibility Principle (SRP)** — แต่ละ Layer และแต่ละคลาสมีหน้าที่ที่ชัดเจน เช่น Entity จัดการข้อมูล, Repository จัดการ Persistence, Service จัดการ Business Logic และ Controller จัดการ HTTP Request
- **Open/Closed Principle (OCP)** — Strategy Pattern ช่วยให้สามารถเพิ่มรูปแบบการคำนวณส่วนลดใหม่ได้โดยแยกเป็น Strategy ใหม่
- **Liskov Substitution Principle (LSP)** — Strategy แต่ละประเภทสามารถใช้แทน `DiscountStrategy` ได้ เนื่องจากมี Contract เดียวกัน
- **Interface Segregation Principle (ISP)** — `DiscountStrategy` มี Interface ขนาดเล็กและมีเฉพาะเมธอดที่เกี่ยวข้องกับการคำนวณส่วนลด
- **Dependency Inversion Principle (DIP)** — ใช้ Dependency Injection ผ่าน Constructor แทนการสร้าง Dependency โดยตรงภายในคลาส
- **High Cohesion** — แต่ละคลาสรวมความรับผิดชอบที่เกี่ยวข้องกัน
- **Low Coupling** — ลดการพึ่งพาโดยตรงระหว่างแต่ละ Layer
- **Strategy Pattern** — แยก Algorithm การคำนวณส่วนลดแต่ละประเภทออกจากกัน

---

## How to Run

### 1. Clone Project

```bash
git clone <repository-url>
cd <project-folder>
```

### 2. ตั้งค่า PostgreSQL

สร้าง Database ใน PostgreSQL และกำหนดข้อมูลการเชื่อมต่อใน

```text
src/main/resources/application.properties
```

ตัวอย่าง:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/<database-name>
spring.datasource.username=<username>
spring.datasource.password=<password>

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

### 3. Run Application

ใช้ Maven:

```bash
mvn spring-boot:run
```

หรือเปิดโปรเจกต์ผ่าน IDE แล้ว Run Spring Boot Application

### 4. เปิดระบบ

เปิด Browser และเข้า

```text
http://localhost:8080/products
```

---

## Summary

โปรเจกต์นี้เป็นระบบจัดการข้อมูลสินค้าที่พัฒนาด้วย **Spring Boot + Spring Data JPA + PostgreSQL + Thymeleaf** โดยแบ่งโครงสร้างระบบออกเป็น Controller, Service, Repository และ Entity เพื่อแยกความรับผิดชอบของแต่ละส่วนอย่างชัดเจน

ระบบประกอบด้วย Entity หลัก ได้แก่ `Product`, `ProductDetail` และ `Review` ซึ่งมีความสัมพันธ์แบบ One-to-One และ One-to-Many ตามลักษณะของข้อมูล นอกจากนี้ยังใช้ **Strategy Pattern** เพื่อจัดการการคำนวณส่วนลดหลายรูปแบบ เช่น ส่วนลดสมาชิกและส่วนลดเทศกาล ทำให้สามารถแยก Algorithm แต่ละประเภทออกจากกันและรองรับการเพิ่มรูปแบบส่วนลดใหม่ได้ง่ายขึ้น

ระบบยังใช้ **Constructor Injection** สำหรับจัดการ Dependency ระหว่าง Controller, Service และ Repository ซึ่งช่วยลด Coupling และทำให้แต่ละส่วนของระบบมีหน้าที่ชัดเจน สอดคล้องกับหลักการออกแบบซอฟต์แวร์ เช่น **SOLID, High Cohesion และ Low Coupling**
