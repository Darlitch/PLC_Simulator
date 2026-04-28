package app.api;

import app.api.dto.GeneratedSourcesResponse;
import app.api.dto.LoadModelRequest;
import app.api.dto.UpdateInputsRequest;
import app.service.PlcSimulationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import simulator.SimulationSnapshot;

@RestController
@RequestMapping("/api")
@Tag(name = "PLC Simulation", description = "Load PoST models and control simulation sessions")
public class PlcSimulationController {
    private final PlcSimulationService simulationService;

    public PlcSimulationController(PlcSimulationService simulationService) {
        this.simulationService = simulationService;
    }

    @PostMapping("/model/load")
    @Operation(summary = "Load model", description = "Stores the PoST source for the current session, generates Java code, compiles it, and prepares the simulation.")
    public ResponseEntity<SimulationSnapshot> loadModel(
            @Parameter(description = "Client-provided session identifier", required = true)
            @RequestHeader("Session-Id") String sessionId,
            @RequestBody LoadModelRequest request
    ) throws Exception {
        return ResponseEntity.ok(simulationService.loadModel(sessionId, request));
    }

    @PostMapping("/model/reload")
    @Operation(summary = "Reload current model", description = "Regenerates and recompiles the model that is currently associated with the session.")
    public ResponseEntity<SimulationSnapshot> reloadCurrentModel(
            @Parameter(description = "Client-provided session identifier", required = true)
            @RequestHeader("Session-Id") String sessionId
    ) throws Exception {
        return ResponseEntity.ok(simulationService.reloadCurrentModel(sessionId));
    }

    @GetMapping("/model/generated-sources")
    @Operation(summary = "Get generated sources", description = "Returns generated Java source files for the current session.")
    public ResponseEntity<GeneratedSourcesResponse> getGeneratedSources(
            @Parameter(description = "Client-provided session identifier", required = true)
            @RequestHeader("Session-Id") String sessionId
    ) throws Exception {
        return ResponseEntity.ok(simulationService.getGeneratedSources(sessionId));
    }

    @PostMapping("/simulation/start")
    @Operation(summary = "Start simulation", description = "Starts cyclic execution of the loaded simulation.")
    public ResponseEntity<SimulationSnapshot> start(
            @Parameter(description = "Client-provided session identifier", required = true)
            @RequestHeader("Session-Id") String sessionId
    ) throws Exception {
        return ResponseEntity.ok(simulationService.start(sessionId));
    }

    @PostMapping("/simulation/pause")
    @Operation(summary = "Pause simulation", description = "Pauses cyclic execution while keeping the loaded state in memory.")
    public ResponseEntity<SimulationSnapshot> pause(
            @Parameter(description = "Client-provided session identifier", required = true)
            @RequestHeader("Session-Id") String sessionId
    ) throws Exception {
        return ResponseEntity.ok(simulationService.pause(sessionId));
    }

    @PostMapping("/simulation/resume")
    @Operation(summary = "Resume simulation", description = "Resumes cyclic execution after a pause.")
    public ResponseEntity<SimulationSnapshot> resume(
            @Parameter(description = "Client-provided session identifier", required = true)
            @RequestHeader("Session-Id") String sessionId
    ) throws Exception {
        return ResponseEntity.ok(simulationService.resume(sessionId));
    }

    @PostMapping("/simulation/stop")
    @Operation(summary = "Stop simulation", description = "Stops execution and reloads the model to reset runtime state.")
    public ResponseEntity<SimulationSnapshot> stop(
            @Parameter(description = "Client-provided session identifier", required = true)
            @RequestHeader("Session-Id") String sessionId
    ) throws Exception {
        return ResponseEntity.ok(simulationService.stop(sessionId));
    }

    @PostMapping("/simulation/step")
    @Operation(summary = "Step simulation", description = "Executes a single simulation cycle.")
    public ResponseEntity<SimulationSnapshot> step(
            @Parameter(description = "Client-provided session identifier", required = true)
            @RequestHeader("Session-Id") String sessionId
    ) throws Exception {
        return ResponseEntity.ok(simulationService.step(sessionId));
    }

    @PostMapping("/simulation/inputs")
    @Operation(summary = "Update inputs", description = "Applies input values to the loaded simulation and returns the updated snapshot.")
    public ResponseEntity<SimulationSnapshot> updateInputs(
            @Parameter(description = "Client-provided session identifier", required = true)
            @RequestHeader("Session-Id") String sessionId,
            @RequestBody UpdateInputsRequest request
    ) throws Exception {
        return ResponseEntity.ok(simulationService.updateInputs(sessionId, request.values()));
    }

    @GetMapping("/simulation/state")
    @Operation(summary = "Get simulation state", description = "Returns the current simulation snapshot for the session.")
    public ResponseEntity<SimulationSnapshot> state(
            @Parameter(description = "Client-provided session identifier", required = true)
            @RequestHeader("Session-Id") String sessionId
    ) throws Exception {
        return ResponseEntity.ok(simulationService.getSnapshot(sessionId));
    }

    @GetMapping("/simulation/status")
    @Operation(summary = "Get simulation status", description = "Returns the current lifecycle status of the simulation.")
    public ResponseEntity<String> status(
            @Parameter(description = "Client-provided session identifier", required = true)
            @RequestHeader("Session-Id") String sessionId
    ) throws Exception {
        return ResponseEntity.ok(simulationService.getStatus(sessionId).name());
    }
}
