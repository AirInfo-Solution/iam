/*
 *
 *  Copyright 2015 Netflix, Inc.
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 *
 */
package com.headstartech.iam.web.controllers;

import com.headstartech.iam.common.exceptions.IAMException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.io.IOException;

@ControllerAdvice
public class ExceptionMapper {

    private static final String NEW_LINE = "\n";

    @ExceptionHandler(IAMException.class)
    public void handleBadRequest(
            final HttpServletResponse response,
            final IAMException e
    ) throws IOException {
        response.sendError(e.getErrorCode(), e.getLocalizedMessage());
    }

    /**
     * Handle constraint violation exceptions.
     *
     * @param response The HTTP response
     * @param cve      The exception to handle
     * @throws IOException on error in sending error
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public void handleConstraintViolation(
            final HttpServletResponse response,
            final ConstraintViolationException cve
    ) throws IOException {
        final StringBuilder builder = new StringBuilder();
        for (final ConstraintViolation<?> cv : cve.getConstraintViolations()) {
            if (builder.length() != 0) {
                builder.append(NEW_LINE);
            }
            builder.append(cv.getMessage());
        }
        response.sendError(HttpStatus.PRECONDITION_FAILED.value(), builder.toString());
    }
}
