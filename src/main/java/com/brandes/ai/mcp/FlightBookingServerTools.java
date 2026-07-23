package com.brandes.ai.mcp;

import io.modelcontextprotocol.spec.McpSchema;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.context.McpSyncRequestContext;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class FlightBookingServerTools {
    @McpTool(description = "Book a flight ticket")
    public String bookFlight(String flightId, McpSyncRequestContext context) {
        //Check if we need to elicit a missing preference (Form Mode)
        var formRequest = McpSchema.ElicitFormRequest.builder(
                        "Please choose a seat preference",
                        Map.of("type", "object",
                                "properties", Map.of("seatPreference",
                                        Map.of("type", "string", "enum", java.util.List.of("Window", "Aisle"))),
                                "required", java.util.List.of("seatPreference")))
                .build();

        // Send elicitation request to client and wait for response
        McpSchema.ElicitResult result = context.elicit(formRequest);

        if (result.action() == McpSchema.ElicitResult.Action.ACCEPT) {
            String seat = (String) result.content().get("seatPreference");
            return "Flight " + flightId + " booked with a " + seat + " seat!";
        }

        return "Booking cancelled.";
    }
}
