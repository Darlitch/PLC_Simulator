package app.api;

import app.api.dto.LoadModelRequest;
import app.api.dto.UpdateInputsRequest;
import app.service.PlcSimulationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import simulator.SimulationSnapshot;

@RestController
@RequestMapping("/api")
public class PlcSimulationController {
    private final PlcSimulationService simulationService;

    public PlcSimulationController(PlcSimulationService simulationService) {
        this.simulationService = simulationService;
    }

    @PostMapping("/model/load")
    public ResponseEntity<SimulationSnapshot> loadModel(
            @RequestHeader("Session-Id") String sessionId,
            @RequestBody LoadModelRequest request
    ) throws Exception {
        return ResponseEntity.ok(simulationService.loadModel(sessionId, request));
    }

    @PostMapping("/model/reload")
    public ResponseEntity<SimulationSnapshot> reloadCurrentModel(
            @RequestHeader("Session-Id") String sessionId
    ) throws Exception {
        return ResponseEntity.ok(simulationService.reloadCurrentModel(sessionId));
    }

    @PostMapping("/simulation/start")
    public ResponseEntity<SimulationSnapshot> start(@RequestHeader("Session-Id") String sessionId) throws Exception {
        return ResponseEntity.ok(simulationService.start(sessionId));
    }

    @PostMapping("/simulation/pause")
    public ResponseEntity<SimulationSnapshot> pause(@RequestHeader("Session-Id") String sessionId) throws Exception {
        return ResponseEntity.ok(simulationService.pause(sessionId));
    }

    @PostMapping("/simulation/resume")
    public ResponseEntity<SimulationSnapshot> resume(@RequestHeader("Session-Id") String sessionId) throws Exception {
        return ResponseEntity.ok(simulationService.resume(sessionId));
    }

    @PostMapping("/simulation/stop")
    public ResponseEntity<SimulationSnapshot> stop(@RequestHeader("Session-Id") String sessionId) throws Exception {
        return ResponseEntity.ok(simulationService.stop(sessionId));
    }

    @PostMapping("/simulation/step")
    public ResponseEntity<SimulationSnapshot> step(@RequestHeader("Session-Id") String sessionId) throws Exception {
        return ResponseEntity.ok(simulationService.step(sessionId));
    }

    @PostMapping("/simulation/inputs")
    public ResponseEntity<SimulationSnapshot> updateInputs(
            @RequestHeader("Session-Id") String sessionId,
            @RequestBody UpdateInputsRequest request
    ) throws Exception {
        return ResponseEntity.ok(simulationService.updateInputs(sessionId, request.values()));
    }

    @GetMapping("/simulation/state")
    public ResponseEntity<SimulationSnapshot> state(@RequestHeader("Session-Id") String sessionId) throws Exception {
        return ResponseEntity.ok(simulationService.getSnapshot(sessionId));
    }

    @GetMapping("/simulation/status")
    public ResponseEntity<String> status(@RequestHeader("Session-Id") String sessionId) throws Exception {
        return ResponseEntity.ok(simulationService.getStatus(sessionId).name());
    }
}
