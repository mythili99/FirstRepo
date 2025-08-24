# WebDriver Executables for Restricted Organizations

This folder contains WebDriver executables for different operating systems. This approach is used when automatic driver download through WebDriverManager is not allowed due to organizational restrictions.

## Folder Structure

```
drivers/
├── windows/
│   ├── chromedriver.exe
│   ├── geckodriver.exe
│   └── msedgedriver.exe
├── mac/
│   ├── chromedriver
│   ├── geckodriver
│   └── msedgedriver
└── linux/
    ├── chromedriver
    ├── geckodriver
    └── msedgedriver
```

## How to Set Up

### 1. Download Drivers Manually

#### Chrome Driver
- Visit: https://chromedriver.chromium.org/
- Download the version that matches your Chrome browser version
- Extract and place in the appropriate OS folder

#### Firefox Driver (GeckoDriver)
- Visit: https://github.com/mozilla/geckodriver/releases
- Download the latest version for your OS
- Extract and place in the appropriate OS folder

#### Edge Driver
- Visit: https://developer.microsoft.com/en-us/microsoft-edge/tools/webdriver/
- Download the version that matches your Edge browser version
- Extract and place in the appropriate OS folder

### 2. File Permissions

#### Windows
- Ensure `.exe` files are not blocked by Windows Defender
- Right-click → Properties → Unblock if necessary

#### Mac/Linux
- Make drivers executable: `chmod +x drivers/mac/chromedriver`
- Ensure drivers are in your PATH or use absolute paths

### 3. Version Compatibility

Always ensure driver versions match your browser versions:
- Chrome Driver version should match Chrome browser version
- GeckoDriver should be compatible with Firefox version
- Edge Driver should match Edge browser version

## Configuration

The framework automatically detects your OS and uses the appropriate driver from this folder structure. Update the `config.properties` file if you need to change the drivers folder path:

```properties
drivers.folder.path=drivers
use.custom.driver.manager=true
```

## Troubleshooting

### Driver Not Found Error
If you see "Driver not found" errors, ensure:
1. Drivers are placed in the correct OS-specific subfolder
2. File names match exactly (case-sensitive on Mac/Linux)
3. File permissions are correct
4. Drivers are compatible with your browser version

### Permission Denied Error
- Windows: Run as Administrator or unblock files
- Mac/Linux: Use `chmod +x` to make files executable

### Version Mismatch Error
- Update your browser to match driver version, or
- Download the correct driver version for your browser

## Security Notes

- Only download drivers from official sources
- Verify file checksums when available
- Keep drivers updated for security patches
- Scan downloaded files with antivirus software

## Alternative Setup

If you prefer to use WebDriverManager (when allowed):
1. Set `use.custom.driver.manager=false` in config.properties
2. Remove or rename this drivers folder
3. The framework will automatically download drivers as needed
