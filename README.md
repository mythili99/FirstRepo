# Selenium Cucumber Test Automation Framework

A comprehensive, reusable test automation framework built with **Selenium WebDriver**, **Cucumber**, **TestNG**, and **RestAssured** for both UI and API testing.

## 🚀 Framework Features

### Core Components
- **Selenium WebDriver** without PageFactory for UI automation
- **Cucumber** with TestNG integration for BDD-style testing
- **RestAssured** for comprehensive API testing
- **Singleton WebDriver** with Browser/OS enum support
- **Headless browser** execution capability
- **Properties + Environment variable** configuration
- **Multi-format test data** management (Excel, JSON, POJO, Database)
- **Dual reporting** with ExtentReports (HTML) + Excel reports
- **Log4j singleton** logging framework
- **Thread-safe** parallel execution support

### Browser Support
- Chrome (with headless option)
- Firefox (with headless option)
- Edge (with headless option)
- Safari (macOS only)

### Test Data Sources
- **Excel files** (.xlsx format)
- **JSON files** with structured test data
- **Database** connections (MySQL support)
- **POJO classes** for type-safe data handling

### Reporting & Logging
- **ExtentReports** with rich HTML reports and screenshots
- **Excel reports** with test execution summary
- **Log4j** comprehensive logging with multiple appenders
- **Screenshot capture** on test failure/success (configurable)
- **API request/response** logging

## 📁 Project Structure

```
selenium-cucumber-framework/
├── src/
│   ├── main/java/com/automation/
│   │   ├── api/              # API testing utilities
│   │   ├── constants/        # Framework constants and enums
│   │   ├── core/            # WebDriver management
│   │   ├── data/            # Test data POJOs
│   │   └── utils/           # Utility classes
│   └── test/
│       ├── java/com/automation/
│       │   ├── listeners/   # TestNG listeners
│       │   ├── runners/     # Cucumber runners
│       │   └── steps/       # Step definitions
│       └── resources/
│           ├── features/    # Cucumber feature files
│           ├── properties/  # Configuration files
│           └── testdata/    # Test data files
├── reports/                 # Generated reports
├── logs/                   # Application logs
├── test-output/            # Screenshots and TestNG output
└── pom.xml
```

## ⚙️ Configuration

### Properties File (`src/test/resources/properties/config.properties`)

The framework uses a properties file for configuration. **For newbies, only this file needs to be modified** to change test behavior:

```properties
# Browser Configuration
browser=chrome                    # chrome, firefox, edge, safari, chrome_headless, firefox_headless, edge_headless
headless=false                   # true/false

# Environment
environment=dev                  # dev, qa, prod

# Application URLs
base.url=https://demo.opencart.com
api.base.url=https://reqres.in/api

# Timeouts (seconds)
implicit.wait=10
explicit.wait=20
page.load.timeout=30

# Reporting
take.screenshot.on.failure=true
take.screenshot.on.pass=false
```

### Environment Variable Override

Any property can be overridden using environment variables by converting to UPPERCASE and replacing dots with underscores:

```bash
export BROWSER=firefox
export HEADLESS=true
export BASE_URL=https://staging.example.com
```

## 🛠️ Setup Instructions

### Prerequisites
- Java 11 or higher
- Maven 3.6 or higher
- Git

### Installation

1. **Clone the repository:**
   ```bash
   git clone https://github.com/your-repo/selenium-cucumber-framework.git
   cd selenium-cucumber-framework
   ```

2. **Install dependencies:**
   ```bash
   mvn clean install -DskipTests
   ```

3. **Verify setup:**
   ```bash
   mvn test -Dtest=TestRunner
   ```

## 🏃‍♂️ Running Tests

### Command Line Execution

**Run all tests:**
```bash
mvn test
```

**Run specific test runner:**
```bash
# UI tests only
mvn test -Dtest=UiTestRunner

# API tests only
mvn test -Dtest=ApiTestRunner
```

**Run with specific browser:**
```bash
mvn test -Dbrowser=firefox
mvn test -Dbrowser=chrome_headless
```

**Run with environment variables:**
```bash
mvn test -Denvironment=qa -Dheadless=true
```

**Run specific feature:**
```bash
mvn test -Dcucumber.filter.tags="@api"
mvn test -Dcucumber.filter.tags="@ui"
```

**Parallel execution:**
```bash
mvn test -Dparallel.execution=true -Dthread.count=3
```

### IDE Execution

Run any of the runner classes directly from your IDE:
- `TestRunner.java` - All tests
- `UiTestRunner.java` - UI tests only
- `ApiTestRunner.java` - API tests only

## 📊 Test Data Management

### Excel Files
```java
// Reading test data from Excel
ExcelReader excelReader = new ExcelReader("src/test/resources/testdata/TestData.xlsx");
List<TestData> testDataList = excelReader.readTestData("TestData");
TestData testData = excelReader.readTestDataByTestCaseId("TestData", "TC001");
```

### JSON Files
```java
// Reading test data from JSON
JsonDataReader jsonReader = new JsonDataReader("src/test/resources/testdata/TestData.json");
List<TestData> testDataList = jsonReader.readTestData();
TestData testData = jsonReader.readTestDataByTestCaseId("TC001");
```

### Database
```java
// Reading test data from database
DatabaseManager dbManager = DatabaseManager.getInstance();
TestData testData = dbManager.getTestDataByTestCaseId("TC001");
List<TestData> enabledTests = dbManager.getEnabledTestData();
```

## 🔌 API Testing

### Basic API Test Example
```java
// Using API client
ApiClient apiClient = ApiClient.getInstance();
Response response = apiClient.get("/users/2");

// Validate response
new ApiValidator(response)
    .validateStatusCode(200)
    .validateResponseTime(3000)
    .validateJsonField("data.id", "2")
    .validateContentType("application/json");
```

### Cucumber API Steps
```gherkin
@api
Scenario: Get user details
  Given I have the API client configured
  When I send a GET request to "/users/2"
  Then the response status code should be 200
  And the response should contain field "data.email"
  And the response time should be less than 3000 milliseconds
```

## 🖥️ UI Testing

### Basic UI Test Example
```java
// Using WebDriver through DriverManager
WebDriver driver = DriverManager.getInstance().getDriver();
driver.get("https://demo.opencart.com");

// Take screenshot
ScreenshotManager.getInstance().captureScreenshot("homepage");
```

### Cucumber UI Steps
```gherkin
@ui
Scenario: Search for a product
  Given I navigate to the application
  When I enter "iPhone" in field with xpath "//input[@name='search']"
  And I click on element with xpath "//button[@type='submit']"
  Then I should see text "iPhone" on the page
```

## 📈 Reporting

### ExtentReports (HTML)
- Rich HTML reports with screenshots
- Test execution timeline
- System information
- API request/response details
- Located in: `reports/ExtentReport_[timestamp].html`

### Excel Reports
- Test execution summary
- Detailed test results with status
- Pass/fail statistics
- Located in: `reports/TestResults_[timestamp].xlsx`

### Logging
- Comprehensive application logs
- Separate test results log
- Located in: `logs/` directory

## 🔧 Customization for Newbies

**For users new to automation, focus on these files:**

1. **Configuration:** `src/test/resources/properties/config.properties`
   - Change browser, URLs, timeouts
   - Enable/disable screenshots

2. **Test Data:** `src/test/resources/testdata/TestData.json`
   - Update test data for your application

3. **Feature Files:** `src/test/resources/features/`
   - Modify existing scenarios
   - Add new test scenarios using existing steps

4. **Application URLs:** Update in config.properties
   ```properties
   base.url=https://your-application.com
   api.base.url=https://your-api.com
   ```

## 🚀 Advanced Configuration

### Custom Browser Options
Modify `DriverManager.java` to add custom browser capabilities:

```java
private ChromeOptions getChromeOptions(boolean isHeadless) {
    ChromeOptions options = new ChromeOptions();
    // Add your custom options here
    options.addArguments("--disable-notifications");
    return options;
}
```

### Custom Step Definitions
Create new step definition classes extending `BaseSteps`:

```java
public class CustomSteps extends BaseSteps {
    @Given("I perform custom action")
    public void customAction() {
        // Your implementation
    }
}
```

## 🐛 Troubleshooting

### Common Issues

1. **WebDriver not found:**
   ```bash
   # Update WebDriverManager in DriverManager.java or set system property
   System.setProperty("webdriver.chrome.driver", "/path/to/chromedriver");
   ```

2. **Port already in use (Remote execution):**
   ```bash
   # Kill existing processes or change port in config.properties
   lsof -ti:4444 | xargs kill -9
   ```

3. **Tests fail in headless mode:**
   ```properties
   # Enable screenshots and check element visibility
   take.screenshot.on.failure=true
   headless=false
   ```

### Debug Mode
Enable debug logging in `log4j2.xml`:
```xml
<Logger name="com.automation" level="DEBUG" additivity="false">
```

## 📝 Best Practices

1. **Page Object Model:** Implement page objects for complex UI interactions
2. **Data-Driven Testing:** Use Excel/JSON for multiple test data sets
3. **Environment Management:** Use different property files for each environment
4. **Parallel Execution:** Configure thread count based on available resources
5. **Screenshot Strategy:** Capture screenshots on failure, optionally on pass
6. **API Testing:** Validate both positive and negative scenarios
7. **Logging:** Use appropriate log levels (INFO, DEBUG, ERROR)

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🆘 Support

For questions and support:
- Create an issue in the GitHub repository
- Check the troubleshooting section above
- Review the sample tests for implementation examples

---

**Happy Testing! 🎯**
