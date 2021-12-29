package com.donutmegacorp.donutqueueapp.errorhandling.boundary;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.TraceContext;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;

@Service
@Slf4j
@lombok.RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TracingService {
    private final Tracer tracer;

    public <T> T wrapActionInSpan(
            final String spanName,
            final Function<Span, T> action
    ) {
        final Span span = tracer.nextSpan().name(spanName);
        try (
                @SuppressWarnings("unused") // reason: syntax forces variable declaration
                final Tracer.SpanInScope s = tracer.withSpan(span)
        ) {
            return action.apply(span);
        }
    }

    public static String getTraceId(
            final Span span
    ) {
        final String alternativeNewTraceId = UUID.randomUUID().toString();
        if (Objects.isNull(span)) {
            LOG.warn(
                    "span is null, new trace ID = {}",
                    alternativeNewTraceId
            );
            return alternativeNewTraceId;
        }
        final TraceContext context = span.context();
        if (Objects.isNull(context)) {
            LOG.warn(
                    "context is null, span = {}, new trace ID = {}",
                    span, alternativeNewTraceId
            );
            return alternativeNewTraceId;
        }
        final String traceId = context.traceId();
        if (Objects.isNull(traceId) || traceId.isBlank()) {
            LOG.warn(
                    "trace ID is null or blank, span = {}, context = {}, new trace ID = {}",
                    span, context, alternativeNewTraceId
            );
            return alternativeNewTraceId;
        }
        return traceId;
    }
}
