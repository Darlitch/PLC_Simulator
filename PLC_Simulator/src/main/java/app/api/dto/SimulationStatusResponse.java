package app.api.dto;

import simulator.SimulationStatus;

public record SimulationStatusResponse(
        String modelPath,
        SimulationStatus status
) {
}
