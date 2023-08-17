# Business Profile Manager

Business Profile Manager is a comprehensive profile updater service that offers data validation before updating business
profile information in the database. The validation process is essential for all QuickBooks products, including
Timesheet, Accounts, Payments, and Payroll.

## Overview

This application utilizes the Spring Boot framework to facilitate its operations.

## Database

Currently, the system employs an in-memory MySQL database. However, you can effortlessly configure an alternate database
by adjusting settings in the `application.properties` file.

## Getting Started

Before proceeding, ensure you have Java 11 installed on your system.

To build the project and generate a JAR file, execute the following commands:

```bash
./gradlew clean build
```

To launch the application, use:

```bash
java -jar target/business-profile-manager-0.0.1-SNAPSHOT.jar
```

### Running Tests

For running tests, utilize the following command:

```bash
./gradlew test
```

### Database Configuration

By default, the application connects to a MySQL database with the following connection string:

```bash
jdbc:mysql://localhost:3306/profile_db
```

You can modify the database configuration by adjusting settings in the `application.properties` file.

## Available Services

The Business Profile Manager service provides the following essential endpoints for managing business profiles and
validations:

### Validate Business Profile Data

This service validates business profile data for each QuickBooks product.

* Endpoint: `POST /validate`

#### Request:

```json
{
  "business_profile": {
    "company_name": "test",
    "legal_name": "testLegal",
    "business_address": {
      "line1": "main1",
      "line2": "line2",
      "city": "blr",
      "state": "KAR",
      "zip": "560066",
      "country": "IN"
    },
    "legal_address": {
      "line1": "main1",
      "line2": "line2",
      "city": "blr",
      "state": "KAR",
      "zip": "560066",
      "country": "IN"
    },
    "tax_identifiers": [
      {
        "tax_identifier_type": "PAN",
        "tax_identifier_no": "CVZZ17P32P"
      }
    ],
    "email": "test@test.com",
    "website": "www.test.com"
  },
  "product": "accounting"
}
```

#### Response:

```json
{
  "product_id": "accounting",
  "status": "SUCCESSFUL",
  "validation_message": "Data is valid. Validation done by accounting product"
}
```

### Subscribe to Products

This service validates data against a business profile and subscribes to requested products if validation is successful.

* Endpoint: `POST /subscribe`

#### Request:

```json
{
  "profile": {
    "company_name": "test",
    "legal_name": "testLegal",
    "business_address": {
      "line1": "main1",
      "line2": "a",
      "city": "blr",
      "state": "KAR",
      "zip": "560066",
      "country": "IN"
    },
    "legal_address": {
      "line1": "main1",
      "line2": "a",
      "city": "blr",
      "state": "KAR",
      "zip": "560066",
      "country": "IN"
    },
    "tax_identifiers": [
      {
        "tax_identifier_type": "PAN",
        "tax_identifier_no": "CVZZ17P32P"
      },
      {
        "tax_identifier_type": "EAN",
        "tax_identifier_no": "ABC2376387"
      }
    ],
    "email": "test@test.com",
    "website": "www.test.com"
  },
  "products": [
    "TTracking",
    "Payment"
  ]
}
```

#### Response:

```json
{
  "profile_id": "8",
  "message": "Business profile is validated and subscribed successfully"
}
```

### Update Subscriptions

This service allows updating subscriptions for an already subscribed profile.

* Endpoint: `POST /profiles/{profile_id}/subscribe`

#### Request:

```json
{
  "products": [
    "QBO",
    "Payroll"
  ]
}
```

#### Response:

```json
{
  "profile_id": "6",
  "message": "Subscribed to the products: [QBO, Payroll]"
}
```

### Fetch Profile Details

This service retrieves details for an already subscribed profile.

* Endpoint: `GET /profiles/{profile_id}`

#### Response:

```json
{
  "profile": {
    "id": "2",
    "company_name": "test",
    "legal_name": "testLegal",
    "business_address": {
      "line1": "main1",
      "line2": "a",
      "city": "blr",
      "state": "KAR",
      "zip": "560066",
      "country": "IN"
    },
    "legal_address": {
      "line1": "main1",
      "line2": "a",
      "city": "blr",
      "state": "KAR",
      "zip": "560066",
      "country": "IN"
    },
    "tax_identifiers": [
      {
        "tax_identifier_type": "EAN",
        "tax_identifier_no": "ABC2376387"
      },
      {
        "tax_identifier_type": "PAN",
        "tax_identifier_no": "CVZZ17P32P"
      }
    ],
    "email": "test@test.com",
    "website": "www.test.com"
  },
  "subscribed_products": [
    "QBO"
  ]
}
```

### Update Profile Details

This service updates profile details for an already subscribed profile.

* Endpoint: `PUT /profiles/{profile_id}`

#### Request:

```json
{
  "company_name": "test2",
  "legal_name": "testLegal2",
  "business_address": {
    "line1": "main2",
    "line2": "a",
    "city": "blr",
    "state": "KAR",
    "zip": "560066",
    "country": "IN"
  },
  "legal_address": {
    "line1": "main2",
    "line2": "a",
    "city": "blr",
    "state": "KAR",
    "zip": "560066",
    "country": "IN"
  },
  "tax_identifiers": [
    {
      "tax_identifier_type": "PAN",
      "tax_identifier_no": "CVZZ17P32P"
    }
  ],
  "email": "test2@test.com",
  "website": "www.test.com",
  "subscription_products": [
    {
      "products": [
        "QBO",
        "PayRoll"
      ]
    }
  ]
}
```

#### Response:

```json
{
  "profile": {
    "id": "1",
    "companyName": "test2",
    "legalName": "testLegal2",
    "email": "test2@test.com",
    "website": "www.test.com"
  }
}
```

### Conclusion

The Business Profile Manager provides a powerful set of services to manage, validate, and update business profiles
seamlessly. It leverages modern technologies and best practices ensuring efficient and accurate operations for your
QuickBooks products.

