# UI + API Automation Framework

A comprehensive automation framework built from scratch with Selenium, Cucumber, TestNG, and RestAssured for both UI and API testing.

## 🚀 Features

### Core Framework Components
- **Selenium WebDriver** (without PageFactory) with singleton pattern
- **Cucumber + TestNG** integration for BDD testing
- **Browser/OS enums** with headless toggle support
- **Properties + Environment variable** support
- **Test data management** (Excel, JSON, POJO, Database)
- **ExtentReports + Excel reporting** with comprehensive logging
- **Log4j logging** singleton pattern
- **RestAssured** for API automation

### Key Features
- ✅ **Newbie-friendly**: Only configuration changes needed in properties file
- ✅ **Singleton WebDriver** with proper resource management
- ✅ **Multi-browser support** (Chrome, Firefox, Edge, Safari)
- ✅ **Headless mode** toggle
- ✅ **Parallel execution** support
- ✅ **Comprehensive reporting** with screenshots
- ✅ **Data-driven testing** with multiple data sources
- ✅ **Environment-specific** configuration
- ✅ **Retry mechanism** for flaky tests
- ✅ **Database integration** for test data

## 📁 Project Structure

```
├── src/
│   ├── main/
│   │   ├── java/com/automation/
│   │   │   ├── core/                    # Core framework components
│   │   │   │   ├── ConfigManager.java   # Configuration management
│   │   │   │   ├── WebDriverManager.java # WebDriver singleton
│   │   │   │   ├── LoggerManager.java   # Logging singleton
│   │   │   │   └── enums/              # Browser and OS enums
│   │   │   ├── data/                   # Test data management
│   │   │   │   └── TestDataManager.java
│   │   │   ├── reporting/              # Reporting components
│   │   │   │   └── ExtentReportManager.java
│   │   │   ├── ui/                     # UI automation
│   │   │   │   ├── BasePage.java       # Base page class
│   │   │   │   └── pages/              # Page objects
│   │   │   └── api/                    # API automation
│   │   │       ├── BaseAPI.java        # Base API class
│   │   │       └── UserAPI.java        # API implementations
│   │   └── resources/
│   │       ├── config.properties       # Main configuration
│   │       └── log4j2.xml             # Logging configuration
│   └── test/
│       ├── java/com/automation/
│       │   ├── runners/                # Test runners
│       │   │   ├── UITestRunner.java
│       │   │   ├── APITestRunner.java
│       │   │   └── IntegrationTestRunner.java
│       │   ├── stepdefinitions/        # Cucumber step definitions
│       │   │   ├── ui/
│       │   │   └── api/
│       │   └── listeners/              # TestNG listeners
│       └── resources/
│           ├── features/               # Cucumber feature files
│           │   ├── ui/
│           │   └── api/
│           └── testdata/               # Test data files
│               ├── TestData.xlsx
│               └── TestData.json
├── pom.xml                            # Maven configuration
├── testng.xml                         # TestNG configuration
└── README.md                          # This file
```

## 🛠️ Setup Instructions

### Prerequisites
- Java 11 or higher
- Maven 3.6 or higher
- Git

### Installation

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd ui-api-automation-framework
   ```

2. **Install dependencies**
   ```bash
   mvn clean install
   ```

3. **Configure the framework**
   - Edit `src/main/resources/config.properties` for your environment
   - Update URLs, browser settings, and other configurations

4. **Run tests**
   ```bash
   # Run all tests
   mvn test
   
   # Run specific test suite
   mvn test -Dsuite=UITestRunner
   mvn test -Dsuite=APITestRunner
   ```

## ⚙️ Configuration

### Main Configuration (`config.properties`)

```properties
# Browser Configuration
browser=chrome
headless=false
implicit.wait=10
explicit.wait=20

# Environment Configuration
environment=qa
base.url=https://demoqa.com
api.base.url=https://reqres.in/api

# Test Data Configuration
test.data.path=src/test/resources/testdata
excel.file.name=TestData.xlsx
json.file.name=TestData.json

# Reporting Configuration
extent.report.path=target/extent-reports
extent.report.title=UI + API Automation Framework

# Logging Configuration
log.level=INFO
log.file.path=target/logs
log.file.name=automation.log
```

### Environment-Specific Configuration

Create environment-specific files like `config-qa.properties`, `config-staging.properties`:

```properties
# config-qa.properties
base.url=https://qa-demoqa.com
api.base.url=https://qa-api.example.com
environment=qa

# config-staging.properties
base.url=https://staging-demoqa.com
api.base.url=https://staging-api.example.com
environment=staging
```

## 🧪 Running Tests

### Command Line Options

```bash
# Run with specific browser
mvn test -Dbrowser=firefox

# Run in headless mode
mvn test -Dheadless=true

# Run with specific environment
mvn test -Denvironment=staging

# Run with parallel execution
mvn test -Dparallel.execution=true

# Run specific test tags
mvn test -Dcucumber.filter.tags="@smoke"
mvn test -Dcucumber.filter.tags="@api and @users"
```

### TestNG XML Configuration

```xml
<!-- Run specific test suites -->
<test name="UI Tests" parallel="classes" thread-count="2">
    <classes>
        <class name="com.automation.runners.UITestRunner"/>
    </classes>
</test>
```

## 📊 Reports

### ExtentReports
- **Location**: `target/extent-reports/`
- **Features**: Interactive HTML reports with screenshots, logs, and test details
- **Timestamps**: Each run creates a new report with timestamp

### Cucumber Reports
- **HTML**: `target/cucumber-reports/ui-cucumber-report.html`
- **JSON**: `target/cucumber-reports/ui-cucumber-report.json`
- **JUnit**: `target/cucumber-reports/ui-cucumber-report.xml`

### Logs
- **Location**: `target/logs/automation.log`
- **Features**: Rolling file logs with different log levels

## 📝 Writing Tests

### UI Tests with Cucumber

1. **Create Feature File** (`src/test/resources/features/ui/login.feature`):
```gherkin
Feature: User Login
  Scenario: Successful login
    Given I am on the login page
    When I enter valid username "testuser"
    And I enter valid password "password123"
    And I click the login button
    Then I should be logged in successfully
```

2. **Create Step Definitions**:
```java
@When("I enter valid username {string}")
public void iEnterValidUsername(String username) {
    loginPage.enterUsername(username);
}
```

3. **Create Page Object**:
```java
public class LoginPage extends BasePage {
    private final By usernameField = By.id("username");
    
    public void enterUsername(String username) {
        type(usernameField, username);
    }
}
```

### API Tests with Cucumber

1. **Create Feature File** (`src/test/resources/features/api/users.feature`):
```gherkin
Feature: User API
  Scenario: Get all users
    Given the API base URL is configured
    When I send a GET request to "/users"
    Then the response status code should be 200
```

2. **Create API Class**:
```java
public class UserAPI extends BaseAPI {
    public Response getAllUsers(String endpoint) {
        return get(endpoint);
    }
}
```

## 📊 Test Data Management

### Excel Data
```java
// Read data from Excel
List<Map<String, String>> loginData = testDataManager.readExcelData("LoginData");
```

### JSON Data
```java
// Read data from JSON
Map<String, Object> testData = testDataManager.readJsonAsMap("TestData.json");
```

### Database Data
```java
// Execute database queries
List<Map<String, Object>> results = testDataManager.executeQuery("SELECT * FROM users");
```

## 🔧 Framework Components

### Core Managers
- **ConfigManager**: Singleton for configuration management
- **WebDriverManager**: Singleton for WebDriver management
- **LoggerManager**: Singleton for logging
- **TestDataManager**: Test data handling
- **ExtentReportManager**: Reporting management

### Base Classes
- **BasePage**: Common UI automation methods
- **BaseAPI**: Common API automation methods

### Enums
- **Browser**: Supported browsers (CHROME, FIREFOX, EDGE, SAFARI)
- **OS**: Supported operating systems (WINDOWS, MAC, LINUX)

## 🚀 Best Practices

### For Newbies
1. **Only modify `config.properties`** for configuration changes
2. **Use existing page objects** as templates for new pages
3. **Follow naming conventions** for locators and methods
4. **Use data-driven testing** with Excel/JSON files
5. **Add proper logging** for debugging

### For Advanced Users
1. **Extend BasePage/BaseAPI** for custom functionality
2. **Create custom listeners** for specific requirements
3. **Implement custom reporting** if needed
4. **Add database integration** for complex test data
5. **Create reusable utilities** for common operations

## 🐛 Troubleshooting

### Common Issues

1. **WebDriver not found**
   - Ensure WebDriverManager dependency is included
   - Check browser version compatibility

2. **Tests not running**
   - Verify TestNG configuration in `testng.xml`
   - Check Cucumber glue path in test runners

3. **Reports not generating**
   - Ensure ExtentReports dependency is included
   - Check report path permissions

4. **Configuration not loading**
   - Verify properties file location
   - Check environment variable overrides

### Debug Mode
```bash
# Enable debug logging
mvn test -Dlog.level=DEBUG

# Run with verbose output
mvn test -X
```

## 📈 Performance Optimization

1. **Parallel Execution**: Enable in `testng.xml`
2. **Headless Mode**: Use for CI/CD pipelines
3. **Browser Reuse**: Framework handles WebDriver lifecycle
4. **Resource Cleanup**: Automatic cleanup in listeners

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Follow coding standards
4. Add tests for new functionality
5. Submit a pull request

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🆘 Support

For support and questions:
- Create an issue in the repository
- Check the documentation
- Review existing examples

---

**Happy Testing! 🎉**
