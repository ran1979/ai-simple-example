package com.brandes.ai.mcp;

import io.modelcontextprotocol.spec.McpSchema;
import org.springframework.ai.mcp.annotation.McpResource;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;

import org.springframework.ai.mcp.annotation.context.DefaultMcpSyncRequestContext;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Service
public class SupportService {
    @McpTool(name = "supportLevel", description = "get support level by users email")
    public String getSupportLevel(
            @McpToolParam(description = "the users email to do the checkon", required = true)
            String email) {
        if (email.endsWith("@gmail.com")) {
            return "Google support level";

        } else return "None google support level";
    }


    //this resource will appear as resource template because of the dynamic uri
    @McpResource(
            uri = "logs://{serviceName}/{date}",
            name = "System Logs",
            description = "Read logs for a specific service and date"
    )
    public String readLog(
            @McpToolParam(description = "Service Name") String serviceName,
            @McpToolParam(description = "Date YYYY-MM-DD") String date
    ) {
        return "Logs for " + serviceName + " on " + date + ": No errors found.";
    }


    //this resource will appear as resource because its has no dymanic params
    @McpResource(
            uri = "config://application.properties",
            name = "Configuration",
            description = "Get the configurations"
    )
    public String getConfig() throws IOException {
        Properties properties = new Properties();
        InputStream inputStream =
                getClass().getClassLoader().getResourceAsStream("application.properties");
        properties.load(inputStream);
        return properties.toString();
    }

    //this resource will appear as resource because its has no dymanic params
    @McpResource(
            uri = "config://application-ollama.yaml",
            name = "ollamaConfig",
            description = "Get the ollama config",
            mimeType = "text/yaml"
    )
    public String getOllamaConfig(DefaultMcpSyncRequestContext context) throws IOException {
        String uri = ((McpSchema.ReadResourceRequest) context.request()).uri().split("//")[1];
        Properties properties = new Properties();
        InputStream inputStream =
                getClass().getClassLoader().getResourceAsStream(uri);
        properties.load(inputStream);
        return properties.toString();
    }
}
