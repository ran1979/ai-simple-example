package com.brandes.ai;

import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.client.transport.HttpClientStreamableHttpTransport;
import io.modelcontextprotocol.spec.McpSchema;
import io.modelcontextprotocol.spec.McpSchema.CallToolResult;
import io.modelcontextprotocol.spec.McpSchema.ListResourcesResult;
import io.modelcontextprotocol.spec.McpSchema.ListResourceTemplatesResult;
import io.modelcontextprotocol.spec.McpSchema.ListToolsResult;
import io.modelcontextprotocol.spec.McpSchema.ReadResourceResult;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.time.Duration;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class McpServerIntegrationTest {

    @LocalServerPort
    private int port;

    private McpSyncClient mcpClient;

    @BeforeEach
    void connect() {
        String mcpUrl = "http://localhost:" + port + "/mcp";
        HttpClientStreamableHttpTransport transport = HttpClientStreamableHttpTransport.builder(mcpUrl).build();
        mcpClient = McpClient.sync(transport)
                .requestTimeout(Duration.ofSeconds(10))
                .build();
        mcpClient.initialize();
    }

    @AfterEach
    void disconnect() {
        mcpClient.close();
    }

    @Test
    void shouldDiscoverAndExecuteMcpTool() {
        // 1. Assert: Tool Discovery Checklist
        ListToolsResult toolsResult = mcpClient.listTools();
        assertThat(toolsResult.tools())
                .anyMatch(tool -> tool.name().equals("supportLevel"));
        McpSchema.ServerCapabilities serverCapabilities = mcpClient.getServerCapabilities();
        IO.println(serverCapabilities);

        // 2. Assert: End-to-End Tool Execution
        CallToolResult executionResult = mcpClient.callTool(
                new McpSchema.CallToolRequest("supportLevel", Map.of("email", "12345"))
        );

        // 3. Verify the tool response payload contents
        assertThat(executionResult.content()).isNotEmpty();
        String responseText = executionResult.content().get(0).toString();
        assertThat(responseText).contains("None google support level");
    }

    @Test
    void shouldDiscoverAndReadStaticResource() {
        ListResourcesResult resourcesResult = mcpClient.listResources();
        assertThat(resourcesResult.resources())
                .anyMatch(resource -> resource.uri().equals("config://application.properties"));

        ReadResourceResult result = mcpClient.readResource(
                new McpSchema.ReadResourceRequest("config://application.properties"));

        assertThat(result.contents()).isNotEmpty();
        String text = ((McpSchema.TextResourceContents) result.contents().get(0)).text();
        assertThat(text).contains("spring.application.name=ai");
    }

    @Test
    void shouldDiscoverResourceTemplateAndReadLog() {
        ListResourceTemplatesResult templatesResult = mcpClient.listResourceTemplates();
        assertThat(templatesResult.resourceTemplates())
                .anyMatch(template -> template.uriTemplate().equals("logs://{serviceName}/{date}"));

        ReadResourceResult result = mcpClient.readResource(
                new McpSchema.ReadResourceRequest("logs://payments/2026-07-23"));

        assertThat(result.contents()).isNotEmpty();
        String text = ((McpSchema.TextResourceContents) result.contents().get(0)).text();
        assertThat(text).isEqualTo("Logs for payments on 2026-07-23: No errors found.");
    }
}
