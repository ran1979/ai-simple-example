package com.brandes.ai.mcp;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SupportServiceTest {

    private final SupportService service = new SupportService();

    @Test
    void gmailAddressGetsGoogleSupportLevel() {
        assertThat(service.getSupportLevel("user@gmail.com")).isEqualTo("Google support level");
    }

    @Test
    void nonGmailAddressGetsNoneGoogleSupportLevel() {
        assertThat(service.getSupportLevel("user@yahoo.com")).isEqualTo("None google support level");
    }
}
