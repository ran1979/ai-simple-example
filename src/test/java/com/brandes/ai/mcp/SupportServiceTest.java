package com.brandes.ai.mcp;

import io.modelcontextprotocol.server.McpSyncServerExchange;
import io.modelcontextprotocol.spec.McpSchema;
import org.junit.jupiter.api.Test;
import org.springframework.ai.mcp.annotation.context.DefaultMcpSyncRequestContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

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

    @Test
    void readLogFormatsServiceAndDate() {
        assertThat(service.readLog("payments", "2026-07-23"))
                .isEqualTo("Logs for payments on 2026-07-23: No errors found.");
    }

    @Test
    void getConfigReturnsApplicationProperties() throws Exception {
        assertThat(service.getConfig())
                .contains("spring.application.name=ai")
                .contains("server.port=8080");
    }

    @Test
    void getOllamaConfigReadsUriFromContextAndReturnsYamlProperties() throws Exception {
        McpSchema.ReadResourceRequest request =
                new McpSchema.ReadResourceRequest("config://application-ollama.yaml");
        McpSyncServerExchange exchange = mock(McpSyncServerExchange.class);
        DefaultMcpSyncRequestContext context = (DefaultMcpSyncRequestContext) DefaultMcpSyncRequestContext.builder()
                .request(request)
                .exchange(exchange)
                .build();

        assertThat(service.getOllamaConfig(context))
                .contains("pull-model-strategy=when_missing")
                .contains("temperature=0.7");
    }
}
