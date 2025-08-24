# Enhanced UI + API Automation Framework

This enhanced automation framework now includes **Allure reporting**, **retry mechanism**, and **custom driver management** for restricted organizations.

## 🆕 New Features Added

### 1. **Allure Reporting Integration**
- **Modern HTML reports** with interactive dashboards
- **Step-by-step test execution** tracking
- **Rich attachments** (screenshots, logs, JSON, HTML)
- **Test categorization** (Epic, Feature, Story)
- **Severity levels** and test metadata
- **Issue and requirement linking**
- **Environment information** capture

### 2. **Retry Mechanism**
- **Automatic test retry** for flaky tests
- **Configurable retry count** (default: 2)
- **Retry information** in Allure reports
- **TestNG integration** with IRetryAnalyzer
- **Configurable retry intervals**

### 3. **Custom Driver Management**
- **Local driver storage** for restricted organizations
- **OS-specific driver folders** (Windows, Mac, Linux)
- **No automatic downloads** - manual driver placement
- **Version compatibility** checking
- **Detailed error messages** with setup instructions

## 🚀 Quick Start

### Prerequisites
- Java 11+
- Maven 3.6+
- Chrome, Firefox, or Edge browser
- WebDriver executables (see Driver Setup section)

### 1. **Clone and Setup**
```bash
git clone <repository-url>
cd ui-api-automation-framework
mvn clean install
```

### 2. **Driver Setup (Required for Restricted Organizations)**
```bash
# Create driver folder structure
mkdir -p drivers/windows drivers/mac drivers/linux

# Download and place drivers in appropriate folders:
# Windows: drivers/windows/chromedriver.exe
# Mac: drivers/mac/chromedriver
# Linux: drivers/linux/chromedriver
```

### 3. **Run Tests**
```bash
# Run all tests
mvn test

# Run specific test suite
mvn test -DsuiteXmlFile=testng.xml

# Run with specific browser
mvn test -Dbrowser=firefox
```

### 4. **Generate Allure Report**
```bash
# Generate report from results
mvn allure:report

# Open report in browser
mvn allure:serve
```

## 📁 Project Structure

```
├── src/
│   ├── main/java/com/automation/
│   │   ├── core/
│   │   │   ├── ConfigManager.java          # Configuration management
│   │   │   ├── CustomDriverManager.java    # Custom driver management
│   │   │   ├── WebDriverManager.java       # Original WebDriver manager
│   │   │   ├── RetryAnalyzer.java         # Retry mechanism
│   │   │   └── LoggerManager.java         # Logging management
│   │   ├── reporting/
│   │   │   ├── AllureReportManager.java   # Allure reporting utilities
│   │   │   └── ExtentReportManager.java   # ExtentReports (legacy)
│   │   └── data/
│   │       └── TestDataManager.java       # Test data management
│   └── test/java/com/automation/
│       ├── tests/
│       │   └── SampleAllureTest.java      # Sample Allure test
│       └── runners/
│           ├── UITestRunner.java           # UI test execution
│           ├── APITestRunner.java          # API test execution
│           └── IntegrationTestRunner.java  # Integration tests
├── drivers/                                # WebDriver executables
│   ├── windows/                           # Windows drivers
│   ├── mac/                               # Mac drivers
│   └── linux/                             # Linux drivers
├── allure.properties                      # Allure configuration
├── testng.xml                            # TestNG configuration
└── pom.xml                               # Maven dependencies
```

## ⚙️ Configuration

### **Driver Configuration**
```properties
# Enable custom driver manager
use.custom.driver.manager=true
drivers.folder.path=drivers

# Browser settings
browser=chrome
headless=false
implicit.wait=10
explicit.wait=20
```

### **Allure Configuration**
```properties
# Allure reporting settings
allure.results.directory=target/allure-results
allure.report.directory=target/allure-report
allure.report.version=2.24.0
```

### **Retry Configuration**
```properties
# Retry mechanism settings
retry.count=2
retry.interval=1000
retry.analyzer=com.automation.listeners.RetryAnalyzer
```

## 🧪 Using Allure Reporting

### **Basic Test Annotations**
```java
@Epic("User Management")
@Feature("Login Functionality")
@Story("User Authentication")
@Severity(SeverityLevel.CRITICAL)
@Description("Test user login with valid credentials")
public class LoginTest {
    
    @Test
    public void testValidLogin() {
        // Test implementation
    }
}
```

### **Adding Steps and Attachments**
```java
@Autowired
private AllureReportManager allureManager;

@Test
public void testWithAllure() {
    // Add test steps
    allureManager.addStep("Starting test execution");
    allureManager.addStepWithValue("Processing data", "test data");
    
    // Add attachments
    allureManager.addScreenshot("Test Screenshot", screenshotPath);
    allureManager.addTextAttachment("Test Logs", logContent);
    allureManager.addJsonAttachment("API Response", jsonResponse);
    
    // Add test metadata
    allureManager.addTestLabel("component", "ui");
    allureManager.addTestTag("smoke");
    allureManager.addTestIssue("BUG-123");
}
```

### **Test Links and Requirements**
```java
@Link(name = "Test Documentation", url = "https://docs.example.com")
@Issue("PROJ-123")
@TmsLink("TMS-456")
public class TestWithLinks {
    // Test implementation
}
```

## 🔄 Retry Mechanism

### **Automatic Retry**
Tests automatically retry on failure based on configuration:
```properties
retry.count=2          # Retry failed tests 2 times
retry.interval=1000    # Wait 1 second between retries
```

### **Manual Retry Control**
```java
@Test(retryAnalyzer = RetryAnalyzer.class)
public void testWithRetry() {
    // This test will retry up to 2 times on failure
}

// Or use global configuration in testng.xml
        <parameter name="retryAnalyzer" value="com.automation.listeners.RetryAnalyzer"/>
```

### **Retry Information in Reports**
- Allure reports show retry attempts
- Test execution history with retry details
- Performance metrics for retried tests

## 🚗 Custom Driver Management

### **Driver Folder Structure**
```
drivers/
├── windows/
│   ├── chromedriver.exe      # Chrome driver for Windows
│   ├── geckodriver.exe       # Firefox driver for Windows
│   └── msedgedriver.exe      # Edge driver for Windows
├── mac/
│   ├── chromedriver          # Chrome driver for Mac
│   ├── geckodriver           # Firefox driver for Mac
│   └── msedgedriver          # Edge driver for Mac
└── linux/
    ├── chromedriver          # Chrome driver for Linux
    ├── geckodriver           # Firefox driver for Linux
    └── msedgedriver          # Edge driver for Linux
```

### **Driver Version Compatibility**
- **Chrome**: Driver version must match Chrome browser version
- **Firefox**: GeckoDriver should be compatible with Firefox version
- **Edge**: Driver version must match Edge browser version

### **Switching Between Driver Managers**
```properties
# Use custom driver manager (local drivers)
use.custom.driver.manager=true

# Use WebDriverManager (automatic download)
use.custom.driver.manager=false
```

## 📊 Allure Report Features

### **Dashboard**
- **Test execution summary** with pass/fail statistics
- **Trend analysis** over time
- **Environment information** display
- **Test duration** metrics

### **Test Details**
- **Step-by-step execution** with timestamps
- **Screenshot attachments** for visual debugging
- **Log files** and error details
- **Test metadata** and categorization

### **Filtering and Search**
- **By severity level** (BLOCKER, CRITICAL, NORMAL, MINOR, TRIVIAL)
- **By feature/epic/story**
- **By test status** (passed, failed, skipped)
- **By execution time** and date

### **Attachments**
- **Screenshots** (PNG format)
- **Text files** (logs, configuration)
- **JSON data** (API responses, test data)
- **HTML content** (test summaries, reports)

## 🔧 Maven Commands

### **Test Execution**
```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=SampleAllureTest

# Run with specific suite
mvn test -DsuiteXmlFile=testng.xml

# Run with parameters
mvn test -Dbrowser=firefox -Dheadless=true
```

### **Allure Reporting**
```bash
# Generate report
mvn allure:report

# Serve report locally
mvn allure:serve

# Clean results
mvn allure:clean
```

### **Build and Package**
```bash
# Clean and compile
mvn clean compile

# Run tests and package
mvn clean package

# Install to local repository
mvn clean install
```

## 🐛 Troubleshooting

### **Driver Issues**
```
Driver not found at path: drivers/windows/chromedriver.exe
```
**Solution**: Ensure drivers are placed in correct OS-specific folders with correct names.

### **Allure Report Issues**
```
Allure results directory is empty
```
**Solution**: Run tests first, then generate report with `mvn allure:report`.

### **Retry Mechanism Issues**
```
Retry analyzer not working
```
**Solution**: Check `testng.xml` configuration and ensure `RetryAnalyzer` is properly configured.

### **Permission Issues (Windows)**
```
Access denied when running drivers
```
**Solution**: Right-click driver file → Properties → Unblock, or run as Administrator.

## 📈 Best Practices

### **Allure Reporting**
1. **Use descriptive step names** for better readability
2. **Add relevant attachments** (screenshots, logs, data)
3. **Categorize tests** with Epic/Feature/Story annotations
4. **Set appropriate severity levels** for test prioritization
5. **Link tests to requirements** and issues for traceability

### **Retry Mechanism**
1. **Use retries sparingly** - only for genuinely flaky tests
2. **Set reasonable retry counts** (1-3 is usually sufficient)
3. **Investigate root causes** of flaky tests rather than relying on retries
4. **Monitor retry statistics** to identify problematic tests

### **Driver Management**
1. **Keep drivers updated** to match browser versions
2. **Use consistent driver versions** across team
3. **Document driver setup** for new team members
4. **Test driver compatibility** before deployment

## 🔄 Migration from WebDriverManager

### **Step 1: Update Configuration**
```properties
# Change from automatic to manual driver management
use.custom.driver.manager=true
drivers.folder.path=drivers
```

### **Step 2: Create Driver Folders**
```bash
mkdir -p drivers/windows drivers/mac drivers/linux
```

### **Step 3: Download and Place Drivers**
- Download drivers from official sources
- Place in appropriate OS folders
- Ensure correct file names and permissions

### **Step 4: Update Test Classes**
```java
// Change from WebDriverManager to CustomDriverManager
// Before:
WebDriver driver = WebDriverManager.getInstance().getDriver();

// After:
WebDriver driver = CustomDriverManager.getInstance().getDriver();
```

## 📚 Additional Resources

- **Allure Documentation**: https://docs.qameta.io/allure/
- **TestNG Documentation**: https://testng.org/doc/
- **Selenium Documentation**: https://selenium.dev/documentation/
- **ChromeDriver Downloads**: https://chromedriver.chromium.org/
- **GeckoDriver Downloads**: https://github.com/mozilla/geckodriver/releases

## 🤝 Contributing

1. **Fork the repository**
2. **Create a feature branch**
3. **Make your changes**
4. **Add tests for new functionality**
5. **Update documentation**
6. **Submit a pull request**

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

---

**Happy Testing! 🚀**
