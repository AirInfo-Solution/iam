package com.headstartech.iam.core.jpa.services;

import com.headstartech.iam.common.dto.AuthenticateRequest;
import com.headstartech.iam.common.dto.AuthenticateResponse;
import com.headstartech.iam.common.dto.Domain;
import com.headstartech.iam.common.exceptions.IAMBadRequestException;
import com.headstartech.iam.common.exceptions.IAMConflictException;
import com.headstartech.iam.common.exceptions.IAMException;
import com.headstartech.iam.common.exceptions.IAMNotFoundException;
import com.headstartech.iam.core.annotations.TransactionalService;
import com.headstartech.iam.core.jpa.entities.DomainEntity;
import com.headstartech.iam.core.jpa.entities.PermissionEntity;
import com.headstartech.iam.core.jpa.entities.UserEntity;
import com.headstartech.iam.core.jpa.repositories.JpaDomainRepository;
import com.headstartech.iam.core.jpa.repositories.JpaUserRepository;
import com.headstartech.iam.core.services.DomainService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@TransactionalService
public class JpaDomainService implements DomainService {

    private final JpaDomainRepository domainRepo;
    private final JpaUserRepository userRepo;

    @Autowired
    public JpaDomainService(JpaDomainRepository domainRepo, JpaUserRepository userRepo) {
        this.domainRepo = domainRepo;
        this.userRepo = userRepo;
    }

    @Override
    public String createDomain(Domain domain) throws IAMException {
        if (StringUtils.isNotBlank(domain.getId()) && domainRepo.exists(domain.getId())) {
            throw new IAMConflictException("A domain with id " + domain.getId() + " already exists.");
        }

        final DomainEntity domainEntity = new DomainEntity();
        domainEntity.setId(StringUtils.isBlank(domain.getId()) ? UUID.randomUUID().toString() : domain.getId());
        domainEntity.setDescription(domain.getDescription());

        return domainRepo.save(domainEntity).getId();
    }

    @Override
    public void updateDomain(String id, Domain domain) throws IAMException {
        if (!this.domainRepo.exists(id)) {
            throw new IAMNotFoundException("No domain exists with the given id, unable to update.");
        }
        if (!id.equals(domain.getId())) {
            throw new IAMBadRequestException("Domain id inconsistent with id passed in.");
        }

        final DomainEntity domainEntity = findDomain(id);
        domainEntity.setDescription(domain.getDescription());
        domainRepo.save(domainEntity);
    }

    @Override
    public Domain getDomain(String id) throws IAMException {
        return findDomain(id).getDTO();
    }

    @Override
    public void deleteDomain(String id) throws IAMException {
        DomainEntity domainEntity = findDomain(id);
        domainRepo.delete(domainEntity);
    }

    @Override
    public Page<Domain> getDomains(Pageable page) {
        Page<DomainEntity> domainEntities = domainRepo.findAll(page);
        return domainEntities.map(DomainEntity::getDTO);
    }

    @Override
    public void deleteAllDomains() throws IAMException {
        for (DomainEntity domainEntity : domainRepo.findAll()) {
            deleteDomain(domainEntity.getId());
        }
    }

    @Override
    public AuthenticateResponse authenticateUser(String domainId, AuthenticateRequest authenticateRequest) throws IAMException {
        DomainEntity domainEntity = findDomain(domainId);
        UserEntity userEntity = findUserByUserName(domainId, authenticateRequest.getUserName());
        AuthenticateResponse response = new AuthenticateResponse();
        response.setPermissions(new HashSet<>());
        if(authenticate(userEntity, authenticateRequest)) {
            Set<String> permissions = userEntity.getRoles().stream().map(role -> role.getPermissions()).flatMap(p -> p.stream()).map(pe -> pe.getName()).collect(Collectors.toSet());
            response.setPermissions(permissions);
            response.setAuthenticationSuccessful(true);
        } else {
            response.setAuthenticationSuccessful(false);
        }
        return response;
    }

    private boolean authenticate(UserEntity userEntity, AuthenticateRequest authenticateRequest) {
        return userEntity.getPassword().equals(authenticateRequest.getPassword());
    }

    private DomainEntity findDomain(final String id) throws IAMException {
        final DomainEntity domainEntity= domainRepo.findOne(id);
        if (domainEntity!= null) {
            return domainEntity;
        } else {
            throw new IAMNotFoundException("No domain with id " + id + " exists.");
        }
    }

    private UserEntity findUserByUserName(final String domainId, final String userName) throws IAMException {
        // TODO: might return more than 1 user
        final UserEntity userEntity = userRepo.findByUserName(userName);
        if (userEntity!= null) {
            if(!userEntity.getDomain().getId().equals(domainId)) {
                throw new IAMNotFoundException("No user with userName " + userName + " exists.");
            }

            return userEntity;
        } else {
            throw new IAMNotFoundException("No user with userName " + userName + " exists.");
        }
    }

}
