> # Business-profile-manager

Sample profile updater service does validate Business profile data before updating data to DB.
This validation has to be done by all Quick book products i.e
* Timesheet
* Accounts
* Payments
* Payroll

### How it works

The application uses Spring boot application.

### Database

It uses mySQL in memory database (for now), can be changed easily in the `application.properties` for any other database.

### Getting started

You need Java 11 installed.

    ./gradlew clean build

    java -jar target/business-profile-manager-0.0.1-SNAPSHOT.jar

### Run test

    ./gradlew test

### Data base

* mySQL DB - jdbc:mysql://localhost:3306/profile_db
* DB schema can be found in /resources/data.sql

To re-configure DB change configuration in application.properties file

### Services

This validation is done by using `resources/validationData.json` file which has validation configuration for each of the products.
validation configuration includes regular expression for each of the fields categorized by products.

* These services are to validate business profile data for every QB product individually

    * POST /validate/accounting
    * POST /validate/payroll
    * POST /validate/payments
    * POST /validate/timesheet

  Request:
 ```
      {
        "companyName": "abc",
        "legalName": "abc",
        "businessAddress": {
            "line1": "main1",
            "line2": "a",
            "city": "blr",
            "state": "KAR",
            "zip": "560066",
            "country": "IN"
        },
        
        "legalAddress": {
            "line1": "main1",
            "line2": "a",
            "city": "blr",
            "state": "KAR",
            "zip": "560066",
            "country": "IN"
        },
        "taxID": "ANYPL296",
        "email": "lavap@sabre.com",
        "website": "www.intuit.com"
      }
  ```
Response:
  ```
  {
      "status": "SUCCESS",
      "message": "Data is valid. validation done by accounting product"
  }
  ```

* This service is to validate data against multiple Quick books product
    * POST /validate

  Example: at a shot we are validate profile data against payroll and accounting product.

Request:
```
{
   "businessProfile":{
	"companyName": "abc",
	"legalName": "abc",
	"businessAddress": {
		"line1": "main1",
		"line2": "a",
		"city": "blr",
		"state": "",
		"zip": "560066",
		"country": "IN"
	},
	
	"legalAddress": {
		"line1": "main1",
		"line2": "a",
		"city": "blr",
		"state": "KAR",
		"zip": "560066",
		"country": "IN"
	},
	"taxID": "ANYPL2911R",
	"email": "lavap@s.com",
	"website": "www.intuit.com"
   },
   "products": ["accounting", "payroll"]
}
```

Response:
```
{
    "status": "SUCCESS",
    "validationResponse": {
        "accounting": {
            "status": "SUCCESS",
            "message": "Data is valid. validation done by accounting product"
        },
        "payroll": {
            "status": "SUCCESS",
            "message": "Data is valid. validation done by payroll product"
        }
    }
}
```

* This service is to validate data against already subscribed products and also products to which subscription is being requested.
  If validation is accepted by all products than data is updated to DB.

    * POST /subscribe

  Request:
```
  {
     "businessProfile":{
  	"companyName": "abc",
  	"legalName": "abc",
  	"businessAddress": {
  		"line1": "main1",
  		"line2": "a",
  		"city": "blr",
  		"state": "KAR",
  		"zip": "560066",
  		"country": "IN"
  	},
  	
  	"legalAddress": {
  		"line1": "main1",
  		"line2": "a",
  		"city": "blr",
  		"state": "KAR",
  		"zip": "560066",
  		"country": "IN"
  	},
  	"taxID": "ANYPL290R",
  	"email": "lavap@sabre.com",
  	"website": "www.intuit.com"
     },
     "products": ["accounting", "payroll"]
  }
```

Response:

```
{
    "status": "SUCCESS",
    "message": "Business profile is updated and also subscription list",
    "batchValidationResponse": {
        "status": "SUCCESS",
        "validationResponse": {
            "accounting": {
                "status": "SUCCESS",
                "message": "Data is valid. validation done by accounting product"
            },
            "payroll": {
                "status": "SUCCESS",
                "message": "Data is valid. validation done by payroll product"
            }
        }
    },
    "texID": "ANYPL2966R"
}
```

