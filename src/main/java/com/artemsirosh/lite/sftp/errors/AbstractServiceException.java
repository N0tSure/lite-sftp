package com.artemsirosh.lite.sftp.errors;

import org.springframework.core.NestedRuntimeException;
import org.springframework.lang.NonNull;

/**
 * This exception will be thrown as response for business logic rules
 * violation.
 */
public abstract class AbstractServiceException extends NestedRuntimeException {

    public AbstractServiceException(@NonNull final String msg) {
        super(msg);
    }

    public AbstractServiceException(@NonNull final String msg, @NonNull final Throwable cause) {
        super(msg, cause);
    }
}
