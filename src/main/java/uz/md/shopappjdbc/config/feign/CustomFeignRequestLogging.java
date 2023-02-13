package uz.md.shopappjdbc.config.feign;

import feign.Logger;
import feign.Request;
import feign.Response;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

import static feign.Logger.Level.HEADERS;

@Slf4j
public class CustomFeignRequestLogging extends Logger {

    @Override
    protected void logRequest(String configKey, Level logLevel, Request request) {

        if (logLevel.ordinal() >= HEADERS.ordinal()) {
            super.logRequest(configKey, logLevel, request);
        } else {
            int bodyLength = 0;
            if (request.body() != null) {
                byte[] body = request.body();
                byte[] bytes = new byte[body.length];
                System.arraycopy(body, 0, bytes, 0, bytes.length);

                InputStreamReader isr = new InputStreamReader(new ByteArrayInputStream(bytes), StandardCharsets.UTF_8);
                String s = new BufferedReader(isr)
                        .lines()
                        .collect(Collectors.joining("\n"));
                log.info("Request body: " + s);
            } else {
                log.info("Request body: null");
            }
            if (request.body() != null) {
                bodyLength = request.body().length;
            }

            log(configKey, "---> %s %s HTTP/1.1 (%s-byte body) ", request.httpMethod().name(), request.url(), bodyLength);
        }
    }

    @Override
    protected Response logAndRebufferResponse(String configKey, Level logLevel, Response response, long elapsedTime)
            throws IOException {
        if (logLevel.ordinal() >= HEADERS.ordinal()) {
            super.logAndRebufferResponse(configKey, logLevel, response, elapsedTime);
        } else {

            byte[] bytes = response.body().asInputStream().readAllBytes();
            response = response
                    .toBuilder()
                    .body(new ByteArrayInputStream(bytes), bytes.length)
                    .build();

            InputStreamReader isr = new InputStreamReader(new ByteArrayInputStream(bytes), StandardCharsets.UTF_8);
            String body = new BufferedReader(isr)
                    .lines()
                    .collect(Collectors.joining("\n"));
            log.info("Response body: " + body);
            int status = response.status();
            Request request = response.request();
            log(configKey, "<--- %s %s HTTP/1.1 %s (%sms) ", request.httpMethod().name(), request.url(), status, elapsedTime);
        }
        return response;
    }

    @Override
    protected void log(String configKey, String format, Object... args) {
        log.info(format(configKey, format, args));
    }

    protected String format(String configKey, String format, Object... args) {
        return String.format(methodTag(configKey) + format, args);
    }
}
