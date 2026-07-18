package com.brandes.ai.mcp;

import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Service;

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
}
