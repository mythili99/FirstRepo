package com.automation.api;

import io.restassured.response.Response;

import java.util.Map;

/**
 * User API class extending BaseAPI for user management operations
 */
public class UserAPI extends BaseAPI {

    public Response getAllUsers(String endpoint) {
        return get(endpoint);
    }

    public Response getUserById(String endpoint) {
        return get(endpoint);
    }

    public Response createUser(String endpoint, Map<String, String> userData) {
        return post(endpoint, userData);
    }

    public Response updateUser(String endpoint, Map<String, String> userData) {
        return put(endpoint, userData);
    }

    public Response deleteUser(String endpoint) {
        return delete(endpoint);
    }

    public Response patchUser(String endpoint, Map<String, String> userData) {
        return patch(endpoint, userData);
    }

    public Response getAllUsersWithHeaders(String endpoint, Map<String, String> headers) {
        return get(endpoint, headers);
    }

    public Response getAllUsersWithQueryParams(String endpoint, Map<String, String> queryParams) {
        return get(endpoint, queryParams, getDefaultHeaders());
    }

    public Response createUserWithAuth(String endpoint, Map<String, String> userData, String token) {
        return post(endpoint, userData, getAuthHeaders(token));
    }

    public Response updateUserWithAuth(String endpoint, Map<String, String> userData, String token) {
        return put(endpoint, userData, getAuthHeaders(token));
    }

    public Response deleteUserWithAuth(String endpoint, String token) {
        return delete(endpoint, getAuthHeaders(token));
    }
}