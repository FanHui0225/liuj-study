package com.stereo.via.metrics;

import com.codahale.metrics.*;
import com.codahale.metrics.ganglia.GangliaReporter;
import com.codahale.metrics.health.HealthCheck;
import com.codahale.metrics.health.HealthCheckRegistry;
import info.ganglia.gmetric4j.gmetric.GMetric;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static com.codahale.metrics.MetricRegistry.name;

public final class Metrics {
    private static final Logger logger = LoggerFactory.getLogger(Metrics.class);
    private String hostName;
    private final MetricRegistry metrics = new MetricRegistry();
    private final HealthCheckRegistry healthChecks = new HealthCheckRegistry();
    private final GMetric ganglia;
    private final GangliaReporter reporter;

    private Metrics(String gangliaGroup, int gangliaPort, GMetric.UDPAddressingMode gangliaUdpAddressingMode, int gangliaTTL) throws Exception {
        try {
            hostName = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            logger.error("metrics unknown hostName", e);
            throw e;
        }
        try {
            ganglia = new GMetric(gangliaGroup, gangliaPort, gangliaUdpAddressingMode, gangliaTTL);
        } catch (IOException e) {
            logger.error("metrics ganglia val error", e);
            throw e;
        }
        reporter = GangliaReporter.forRegistry(metrics)
                .convertRatesTo(TimeUnit.SECONDS)
                .convertDurationsTo(TimeUnit.MILLISECONDS)
                .build(ganglia);
    }

    public static Metrics build(String gangliaGroup, int gangliaPort, GMetric.UDPAddressingMode gangliaUdpAddressingMode, int gangliaTTL) throws Exception {
        return new Metrics(gangliaGroup, gangliaPort, gangliaUdpAddressingMode, gangliaTTL);
    }

    public Counter addCounter(String counterName) {
        Counter counter = metrics.counter(name(hostName, counterName));
        return counter;
    }

    public Histogram addHistogram(String histogramName) {
        Histogram histogram = metrics.histogram(name(hostName, histogramName));
        return histogram;
    }

    public Meter addMeter(String meterName) {
        Meter meter = metrics.meter(name(hostName, meterName));
        return meter;
    }

    public Metrics start() {
        reporter.start(5, TimeUnit.SECONDS);
        return this;
    }

    public Metrics stop() {
        reporter.stop();
        return this;
    }


    public static class DatabaseHealthCheck extends HealthCheck {

        public DatabaseHealthCheck() {
        }

        @Override
        public HealthCheck.Result check() throws Exception {
            Random random = new Random();
            if (random.nextBoolean()) {
                return HealthCheck.Result.healthy();
            } else {
                return HealthCheck.Result.unhealthy("Cannot connect to database");
            }
        }
    }


}
