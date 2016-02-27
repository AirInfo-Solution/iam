package com.headstartech.iam.client;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.headstartech.iam.common.dto.Domain;
import com.headstartech.iam.common.dto.Permission;
import com.headstartech.iam.common.dto.Role;
import com.headstartech.iam.common.dto.User;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestOperations;

import java.net.URI;
import java.net.URISyntaxException;

public class DefaultIAMClient implements IAMClient {

    private final RestOperations restOperations;
    private final String baseRestURL;

    public DefaultIAMClient(RestOperations restOperations, String baseRestURL) {
        this.restOperations = restOperations;
        this.baseRestURL = baseRestURL;
    }

    @Override
    public Domain createDomain(Domain domain) {
        RequestEntity<Domain> request = RequestEntity.post(toURI(getDomainsBaseURL())).accept(MediaTypes.HAL_JSON).contentType(MediaType.APPLICATION_JSON).body(domain);
        ResponseEntity<DomainResource> responseEntity = restOperations.exchange(request, new ParameterizedTypeReference<DomainResource>() {});
        return responseEntity.getBody().getContent();
    }

    @Override
    public Domain getDomain(String domainId) {
        RequestEntity<Void> request = RequestEntity.get(toURI(getDomainURL(domainId))).accept(MediaTypes.HAL_JSON).build();
        ResponseEntity<DomainResource> responseEntity = restOperations.exchange(request, new ParameterizedTypeReference<DomainResource>() {});
        return responseEntity.getBody().getContent();
    }

    @Override
    public Domain updateDomain(Domain domain) {
        RequestEntity<Domain> request = RequestEntity.put(toURI(getDomainURL(domain.getId()))).accept(MediaTypes.HAL_JSON).contentType(MediaType.APPLICATION_JSON).body(domain);
        ResponseEntity<DomainResource> responseEntity = restOperations.exchange(request, new ParameterizedTypeReference<DomainResource>() {});
        return responseEntity.getBody().getContent();
    }

    @Override
    public void deleteDomain(String id) {
        restOperations.delete(getDomainURL(id));
    }

    @Override
    public User createUser(String domainId, User user) {
        RequestEntity<User> request = RequestEntity.post(toURI(getUsersBaseURL(domainId))).accept(MediaTypes.HAL_JSON).contentType(MediaType.APPLICATION_JSON).body(user);
        ResponseEntity<UserResource> responseEntity = restOperations.exchange(request, new ParameterizedTypeReference<UserResource>() {});
        return responseEntity.getBody().getContent();
    }


    @Override
    public User getUser(String domainId, String userId) {
        RequestEntity<Void> request = RequestEntity.get(toURI(getUserURL(domainId, userId))).accept(MediaTypes.HAL_JSON).build();
        ResponseEntity<UserResource> responseEntity = restOperations.exchange(request, new ParameterizedTypeReference<UserResource>() {});
        return responseEntity.getBody().getContent();
    }

    @Override
    public User updateUser(String domainId, User user) {
        RequestEntity<User> request = RequestEntity.put(toURI(getUserURL(domainId, user.getId()))).accept(MediaTypes.HAL_JSON).contentType(MediaType.APPLICATION_JSON).body(user);
        ResponseEntity<UserResource> responseEntity = restOperations.exchange(request, new ParameterizedTypeReference<UserResource>() {});
        return responseEntity.getBody().getContent();
    }

    @Override
    public void deleteUser(String domainId, String userId) {
        restOperations.delete(getUserURL(domainId, userId));
    }

    @Override
    public Role createRole(String domainId, Role roleId) {
        RequestEntity<Role> request = RequestEntity.post(toURI(getRolesBaseURL(domainId))).accept(MediaTypes.HAL_JSON).contentType(MediaType.APPLICATION_JSON).body(roleId);
        ResponseEntity<RoleResource> responseEntity = restOperations.exchange(request, new ParameterizedTypeReference<RoleResource>() {});
        return responseEntity.getBody().getContent();
    }


    @Override
    public Role getRole(String domainId, String roleId) {
        RequestEntity<Void> request = RequestEntity.get(toURI(getRoleURL(domainId, roleId))).accept(MediaTypes.HAL_JSON).build();
        ResponseEntity<RoleResource> responseEntity = restOperations.exchange(request, new ParameterizedTypeReference<RoleResource>() {});
        return responseEntity.getBody().getContent();
    }

    @Override
    public Role updateRole(String domainId, Role roleId) {
        RequestEntity<Role> request = RequestEntity.put(toURI(getRoleURL(domainId, roleId.getId()))).accept(MediaTypes.HAL_JSON).contentType(MediaType.APPLICATION_JSON).body(roleId);
        ResponseEntity<RoleResource> responseEntity = restOperations.exchange(request, new ParameterizedTypeReference<RoleResource>() {});
        return responseEntity.getBody().getContent();
    }

    @Override
    public void deleteRole(String domainId, String roleId) {
        restOperations.delete(getRoleURL(domainId, roleId));
    }

    @Override
    public Permission createPermission(String domainId, Permission permissionId) {
        RequestEntity<Permission> request = RequestEntity.post(toURI(getPermissionsBaseURL(domainId))).accept(MediaTypes.HAL_JSON).contentType(MediaType.APPLICATION_JSON).body(permissionId);
        ResponseEntity<PermissionResource> responseEntity = restOperations.exchange(request, new ParameterizedTypeReference<PermissionResource>() {});
        return responseEntity.getBody().getContent();
    }

    @Override
    public Permission getPermission(String domainId, String permissionId) {
        RequestEntity<Void> request = RequestEntity.get(toURI(getPermissionURL(domainId, permissionId))).accept(MediaTypes.HAL_JSON).build();
        ResponseEntity<PermissionResource> responseEntity = restOperations.exchange(request, new ParameterizedTypeReference<PermissionResource>() {});
        return responseEntity.getBody().getContent();
    }

    @Override
    public Permission updatePermission(String domainId, Permission permissionId) {
        RequestEntity<Permission> request = RequestEntity.put(toURI(getPermissionURL(domainId, permissionId.getId()))).accept(MediaTypes.HAL_JSON).contentType(MediaType.APPLICATION_JSON).body(permissionId);
        ResponseEntity<PermissionResource> responseEntity = restOperations.exchange(request, new ParameterizedTypeReference<PermissionResource>() {});
        return responseEntity.getBody().getContent();
    }

    @Override
    public void deletePermission(String domainId, String permissionId) {
        restOperations.delete(getPermissionURL(domainId, permissionId));
    }

    private String getDomainsBaseURL() {
        return baseRestURL + "/domains";
    }

    private String getDomainURL(String domainId) {
        return String.format("%s/%s", getDomainsBaseURL(), domainId);
    }

    private String getUsersBaseURL(String domainId) {
        return String.format("%s/%s/users", getDomainsBaseURL(), domainId);
    }

    private String getUserURL(String domainId, String userId) {
        return String.format("%s/%s", getUsersBaseURL(domainId), userId);
    }

    private String getRolesBaseURL(String domainId) {
        return String.format("%s/%s/roles", getDomainsBaseURL(), domainId);
    }

    private String getRoleURL(String domainId, String roleId) {
        return String.format("%s/%s", getRolesBaseURL(domainId), roleId);
    }

    private String getPermissionsBaseURL(String domainId) {
        return String.format("%s/%s/permissions", getDomainsBaseURL(), domainId);
    }

    private String getPermissionURL(String domainId, String permissionId) {
        return String.format("%s/%s", getPermissionsBaseURL(domainId), permissionId);
    }

    private URI toURI(String s) {
        try {
            return new URI(s);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    static class DomainResource extends Resource<Domain> {
        @JsonCreator
        public DomainResource(Domain domain) {
            super(domain);
        }
    }

    static class UserResource extends Resource<User> {
        @JsonCreator
        public UserResource(User user) {
            super(user);
        }
    }

    static class RoleResource extends Resource<Role> {
        @JsonCreator
        public RoleResource(Role role) {
            super(role);
        }
    }

    static class PermissionResource extends Resource<Permission> {
        @JsonCreator
        public PermissionResource (Permission permission) {
            super(permission);
        }
    }
}
