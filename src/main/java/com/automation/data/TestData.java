package com.automation.data;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * POJO class for test data management
 * This class represents test data structure that can be populated from Excel, JSON, or Database
 */
public class TestData {
    
    @JsonProperty("testCaseId")
    private String testCaseId;
    
    @JsonProperty("username")
    private String username;
    
    @JsonProperty("password")
    private String password;
    
    @JsonProperty("email")
    private String email;
    
    @JsonProperty("firstName")
    private String firstName;
    
    @JsonProperty("lastName")
    private String lastName;
    
    @JsonProperty("phoneNumber")
    private String phoneNumber;
    
    @JsonProperty("address")
    private String address;
    
    @JsonProperty("city")
    private String city;
    
    @JsonProperty("zipCode")
    private String zipCode;
    
    @JsonProperty("country")
    private String country;
    
    @JsonProperty("productName")
    private String productName;
    
    @JsonProperty("productPrice")
    private String productPrice;
    
    @JsonProperty("quantity")
    private String quantity;
    
    @JsonProperty("searchTerm")
    private String searchTerm;
    
    @JsonProperty("expectedResult")
    private String expectedResult;
    
    @JsonProperty("testDescription")
    private String testDescription;
    
    @JsonProperty("environment")
    private String environment;
    
    @JsonProperty("runFlag")
    private String runFlag;
    
    // Default constructor
    public TestData() {}
    
    // Constructor with essential fields
    public TestData(String testCaseId, String username, String password, String email) {
        this.testCaseId = testCaseId;
        this.username = username;
        this.password = password;
        this.email = email;
    }
    
    // Getters and Setters
    public String getTestCaseId() {
        return testCaseId;
    }
    
    public void setTestCaseId(String testCaseId) {
        this.testCaseId = testCaseId;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getFirstName() {
        return firstName;
    }
    
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    public String getLastName() {
        return lastName;
    }
    
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    public String getPhoneNumber() {
        return phoneNumber;
    }
    
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public String getCity() {
        return city;
    }
    
    public void setCity(String city) {
        this.city = city;
    }
    
    public String getZipCode() {
        return zipCode;
    }
    
    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }
    
    public String getCountry() {
        return country;
    }
    
    public void setCountry(String country) {
        this.country = country;
    }
    
    public String getProductName() {
        return productName;
    }
    
    public void setProductName(String productName) {
        this.productName = productName;
    }
    
    public String getProductPrice() {
        return productPrice;
    }
    
    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }
    
    public String getQuantity() {
        return quantity;
    }
    
    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }
    
    public String getSearchTerm() {
        return searchTerm;
    }
    
    public void setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
    }
    
    public String getExpectedResult() {
        return expectedResult;
    }
    
    public void setExpectedResult(String expectedResult) {
        this.expectedResult = expectedResult;
    }
    
    public String getTestDescription() {
        return testDescription;
    }
    
    public void setTestDescription(String testDescription) {
        this.testDescription = testDescription;
    }
    
    public String getEnvironment() {
        return environment;
    }
    
    public void setEnvironment(String environment) {
        this.environment = environment;
    }
    
    public String getRunFlag() {
        return runFlag;
    }
    
    public void setRunFlag(String runFlag) {
        this.runFlag = runFlag;
    }
    
    @Override
    public String toString() {
        return "TestData{" +
                "testCaseId='" + testCaseId + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", testDescription='" + testDescription + '\'' +
                ", environment='" + environment + '\'' +
                ", runFlag='" + runFlag + '\'' +
                '}';
    }
}