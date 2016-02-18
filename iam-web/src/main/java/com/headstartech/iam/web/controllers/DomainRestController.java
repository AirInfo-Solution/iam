package com.headstartech.iam.web.controllers;

import com.headstartech.iam.common.dto.Domain;
import com.headstartech.iam.common.exceptions.IAMException;
import com.headstartech.iam.core.services.DomainService;
import com.headstartech.iam.web.hateoas.assemblers.DomainResourceAssembler;
import com.headstartech.iam.web.hateoas.resources.DomainResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.PagedResources;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping(value = "/domains")
public class DomainRestController {

    private final DomainService domainService;
    private final DomainResourceAssembler domainResourceAssembler;

    @Autowired
    public DomainRestController(DomainService domainService, DomainResourceAssembler domainResourceAssembler) {
        this.domainService = domainService;
        this.domainResourceAssembler = domainResourceAssembler;
    }

    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Void> createCommand(@RequestBody final Domain domain) throws IAMException {
        final String id = domainService.createDomain(domain);
        final HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(
                ServletUriComponentsBuilder
                        .fromCurrentRequest()
                        .path("/{id}")
                        .buildAndExpand(id)
                        .toUri()
        );
        return new ResponseEntity<>(httpHeaders, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaTypes.HAL_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public DomainResource getDomain(@PathVariable("id") final String id) throws IAMException {
        return this.domainResourceAssembler.toResource(domainService.getDomain(id));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateDomain(
            @PathVariable("id") final String id,
            @RequestBody final Domain domain
    ) throws IAMException {
        domainService.updateDomain(id, domain);
    }

    @RequestMapping(method = RequestMethod.GET, produces = MediaTypes.HAL_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public PagedResources<DomainResource> getDomains(@PageableDefault(page = 0, size = 10) final Pageable page,
                                             final PagedResourcesAssembler<Domain> assembler) {
        return assembler.toResource(
                domainService.getDomains(page),
                domainResourceAssembler);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteDomain(@PathVariable("id") final String id) throws IAMException {
        domainService.deleteDomain(id);
    }

    @RequestMapping(method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAllDomains() throws IAMException {
        domainService.deleteAllDomains();
    }
}
