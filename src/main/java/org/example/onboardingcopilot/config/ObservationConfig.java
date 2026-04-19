package org.example.onboardingcopilot.config;

import io.micrometer.context.ContextRegistry;
import io.micrometer.context.ThreadLocalAccessor;
import io.micrometer.observation.ObservationRegistry;
import io.micrometer.observation.aop.ObservedAspect;
import io.micrometer.tracing.Tracer;
import io.micrometer.tracing.handler.DefaultTracingObservationHandler;
import io.micrometer.tracing.otel.bridge.OtelBaggageManager;
import io.micrometer.tracing.otel.bridge.OtelCurrentTraceContext;
import io.micrometer.tracing.otel.bridge.OtelTracer;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import io.opentelemetry.exporter.otlp.trace.OtlpGrpcSpanExporter;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.export.BatchSpanProcessor;
import io.opentelemetry.semconv.ServiceAttributes;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import java.util.Collections;

@Configuration
@Profile("!test")
public class ObservationConfig {

    @Bean
    public ObservedAspect observedAspect(ObservationRegistry observationRegistry) {
        return new ObservedAspect(observationRegistry);
    }

    @Bean
    public OtelCurrentTraceContext otelCurrentTraceContext() {
        return new OtelCurrentTraceContext();
    }

    @Bean
    public OtelBaggageManager otelBaggageManager(OtelCurrentTraceContext otelCurrentTraceContext) {
        return new OtelBaggageManager(otelCurrentTraceContext, Collections.emptyList(), Collections.emptyList());
    }

    @Bean
    @Primary
    public OpenTelemetry openTelemetry() {
        OtlpGrpcSpanExporter exporter = OtlpGrpcSpanExporter.builder()
                .setEndpoint("http://otel-collector:4317")
                .build();

        SdkTracerProvider tracerProvider = SdkTracerProvider.builder()
                .addSpanProcessor(BatchSpanProcessor.builder(exporter).build())
                .setResource(Resource.getDefault().toBuilder()
                        .put(ServiceAttributes.SERVICE_NAME, "r2-copilot")
                        .build())
                .build();

        OpenTelemetry otel = OpenTelemetrySdk.builder()
                .setTracerProvider(tracerProvider)
                .buildAndRegisterGlobal();

        // Register OTel Context as a Micrometer ThreadLocalAccessor so that
        // contextCapture() captures the active span at subscription time (on the
        // virtual thread that owns the OTel scope) and
        // Hooks.enableAutomaticContextPropagation() restores it on every Reactor
        // scheduler thread — the same mechanism that already handles MDC.
        // A thread-local Scope is used to track and close the OTel scope correctly.
        ThreadLocal<Scope> scopeHolder = new ThreadLocal<>();
        ContextRegistry.getInstance().registerThreadLocalAccessor(
                new ThreadLocalAccessor<Context>() {
                    @Override
                    public Object key() { return Context.class; }

                    @Override
                    public Context getValue() {
                        return Context.current();
                    }

                    @Override
                    public void setValue(Context value) {
                        scopeHolder.set(value.makeCurrent());
                    }

                    @Override
                    public void setValue() {
                        Scope scope = scopeHolder.get();
                        if (scope != null) {
                            scope.close();
                            scopeHolder.remove();
                        }
                    }
                }
        );

        return otel;
    }

    @Bean
    public Tracer tracer(OpenTelemetry openTelemetry, OtelCurrentTraceContext otelCurrentTraceContext, OtelBaggageManager otelBaggageManager) {
        return new OtelTracer(
                openTelemetry.getTracer("onboarding-copilot"),
                otelCurrentTraceContext,
                event -> {},
                otelBaggageManager
        );
    }

    @Bean
    public ObservationRegistry observationRegistry(Tracer tracer) {
        ObservationRegistry registry = ObservationRegistry.create();
        registry.observationConfig()
                .observationHandler(new DefaultTracingObservationHandler(tracer));
        return registry;
    }
}
