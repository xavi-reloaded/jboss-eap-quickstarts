/*
 * JBoss, Home of Professional Open Source
 * Copyright 2015, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.as.quickstarts.kitchensink.exception.handlers;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import org.apache.deltaspike.core.api.exception.control.ExceptionHandler;
import org.apache.deltaspike.core.api.exception.control.Handles;
import org.apache.deltaspike.core.api.exception.control.event.ExceptionEvent;
import org.jboss.as.quickstarts.kitchensink.exception.annotation.RestRequest;

/**
 * This handler handles exceptions and builds an error message using {@link Status} and {@link Response} entities
 * 
 * @author <a href="mailto:benevides@redhat.com">Rafael Benevides</a>
 * 
 */
@ExceptionHandler
public class RestExceptionHandler {

    @Inject
    private Logger log;

    public void handleGenericException(@Handles @RestRequest ExceptionEvent<Throwable> evt, ResponseBuilder builder) {
        // Handle generic exceptions
        Map<String, String> responseObj = new HashMap<>();
        responseObj.put("error", evt.getException().getMessage());
        builder.status(Response.Status.BAD_REQUEST).entity(responseObj);
        // Mark as handled
        evt.handled();
    }

    public void handleValidationException(@Handles @RestRequest ExceptionEvent<ValidationException> evt, ResponseBuilder builder) {
        // Handle the unique constrain violation
        Map<String, String> responseObj = new HashMap<>();
        responseObj.put("email", "Email taken");
        builder.status(Response.Status.CONFLICT).entity(responseObj);
        // Mark as handled
        evt.handled();
    }

    public void handleWebApplicationException(@Handles @RestRequest ExceptionEvent<WebApplicationException> evt,
            ResponseBuilder builder) {
        // Handle Web Application exceptions
        builder.status(evt.getException().getResponse().getStatus());
        // Mark as handled
        evt.handled();
    }

    public void handleConstraintViolationException(@Handles @RestRequest ExceptionEvent<ConstraintViolationException> evt,
            ResponseBuilder builder) {
        // Handle bean validation issues
        builder.status(Status.BAD_REQUEST).entity(createViolationEntity(evt.getException().getConstraintViolations()));
        // Mark as handled
        evt.handled();
    }

    private Map<String, String> createViolationEntity(Set<ConstraintViolation<?>> violations) {
        log.fine("Validation completed. violations found: " + violations.size());

        Map<String, String> responseObj = new HashMap<>();

        for (ConstraintViolation<?> violation : violations) {
            responseObj.put(violation.getPropertyPath().toString(), violation.getMessage());
        }

        return responseObj;
    }

}
