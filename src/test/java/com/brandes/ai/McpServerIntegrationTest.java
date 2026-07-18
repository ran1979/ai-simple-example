package com.brandes.ai;

import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.client.transport.HttpClientStreamableHttpTransport;
import io.modelcontextprotocol.spec.McpSchema;
import io.modelcontextprotocol.spec.McpSchema.CallToolResult;
import io.modelcontextprotocol.spec.McpSchema.ListToolsResult;
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

    @Test
    void shouldDiscoverAndExecuteMcpTool() {
        // 1. Arrange: Map the transport directly to your server-side STREAMABLE /mcp path
        String mcpUrl = "http://localhost:" + port + "/mcp";
        HttpClientStreamableHttpTransport transport = HttpClientStreamableHttpTransport.builder(mcpUrl).build();

        // 2. Act: Build the client utilizing the official factory spec matching Spring AI 2.0+
        McpSyncClient mcpClient = McpClient.sync(transport)
                .requestTimeout(Duration.ofSeconds(10))
                .build();

        mcpClient.initialize();

        // 3. Assert: Tool Discovery Checklist
        ListToolsResult toolsResult = mcpClient.listTools();
        assertThat(toolsResult.tools())
                .anyMatch(tool -> tool.name().equals("supportLevel"));
        McpSchema.ServerCapabilities serverCapabilities = mcpClient.getServerCapabilities();
        IO.println(serverCapabilities);

        // 4. Assert: End-to-End Tool Execution
        CallToolResult executionResult = mcpClient.callTool(
                new McpSchema.CallToolRequest("supportLevel", Map.of("email", "12345"))
        );

        // 5. Verify the tool response payload contents
        assertThat(executionResult.content()).isNotEmpty();
        String responseText = executionResult.content().get(0).toString();
        assertThat(responseText).contains("None google support level");

        // 6. Cleanup
        mcpClient.close();
    }
}
