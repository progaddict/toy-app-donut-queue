package com.donutmegacorp.donutqueueapp.errorhandling.control;

import com.donutmegacorp.donutqueueapp.errorhandling.boundary.TracingService;
import com.donutmegacorp.donutqueueapp.errorhandling.entity.ErrorModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE + 500)
@Slf4j
@lombok.RequiredArgsConstructor(onConstructor = @__(@Autowired))
class ValidationErrorHandler {
    private final TracingService tracingService;

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ErrorModel handleConstraintViolationException(final ConstraintViolationException error) {
        return getErrorModel(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ErrorModel handleMethodArgumentNotValidException(final MethodArgumentNotValidException error) {
        return getErrorModel(error);
    }

    private ErrorModel getErrorModel(final Exception error) {
        return tracingService.wrapActionInSpan(
                "validation-error-handler",
                span -> {
                    LOG.error("validation error", error);
                    span.error(error);
                    return ErrorModel.builder()
                            .traceId(TracingService.getTraceId(span))
                            .message(error.getMessage())
                            .build();
                }
        );
    }
}
