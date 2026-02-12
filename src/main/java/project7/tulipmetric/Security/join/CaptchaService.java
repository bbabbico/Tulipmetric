package project7.tulipmetric.Security.join;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

@Slf4j
@Service
@RequiredArgsConstructor
public class CaptchaService {

    private static final String VERIFY_URL = "https://www.google.com/recaptcha/api/siteverify";

    @Value("${recaptcha.secret-key:}")
    private String secretKey;

    private final RestClient restClient = RestClient.create();

    public boolean verify(String captchaToken) {
        if (secretKey == null || secretKey.isBlank()) {
            log.warn("reCAPTCHA secret key is not configured.");
            return false;
        }

        if (captchaToken == null || captchaToken.isBlank()) {
            log.warn("reCAPTCHA token is missing.");
            return false;
        }

        try {
            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("secret", secretKey);
            body.add("response", captchaToken);

            CaptchaVerifyResponse response = restClient.post()
                    .uri(VERIFY_URL)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(body)
                    .retrieve()
                    .body(CaptchaVerifyResponse.class);

            boolean success = response != null && Boolean.TRUE.equals(response.success());

            if (!success) {
                log.warn("reCAPTCHA verification failed. response={}", response);
            }

            return success;
        } catch (Exception e) {
            log.error("Failed to verify reCAPTCHA token.", e);
            return false;
        }
    }

    private record CaptchaVerifyResponse(Boolean success) {
    }
}
