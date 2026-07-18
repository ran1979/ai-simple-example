package com.brandes.ai.configuration;

import com.brandes.ai.mcp.SupportService;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;

@Deprecated
/**
 * this @Configuration class which defines a @Bean is needed only if you use the @Tool and not the @McpTool annotation.
 */
public class MCPServerConfig {

    public ToolCallbackProvider supportTools(SupportService service) {
        return MethodToolCallbackProvider.builder()
                .toolObjects(service)
                .build();
    }
}
