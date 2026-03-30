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
    public ResponseEntity<SimulationSnapshot> loadModel(@RequestBody LoadModelRequest request) throws Exception {
        return ResponseEntity.ok(simulationService.loadModel(request));
    }

    @PostMapping("/model/reload")
    public ResponseEntity<SimulationSnapshot> reloadCurrentModel() throws Exception {
        return ResponseEntity.ok(simulationService.reloadCurrentModel());
    }

    @PostMapping("/simulation/start")
    public ResponseEntity<SimulationSnapshot> start() {
        return ResponseEntity.ok(simulationService.start());
    }

    @PostMapping("/simulation/pause")
    public ResponseEntity<SimulationSnapshot> pause() {
        return ResponseEntity.ok(simulationService.pause());
    }

    @PostMapping("/simulation/resume")
    public ResponseEntity<SimulationSnapshot> resume() {
        return ResponseEntity.ok(simulationService.resume());
    }

    @PostMapping("/simulation/stop")
    public ResponseEntity<SimulationSnapshot> stop() {
        return ResponseEntity.ok(simulationService.stop());
    }

    @PostMapping("/simulation/step")
    public ResponseEntity<SimulationSnapshot> step() {
        return ResponseEntity.ok(simulationService.step());
    }

    @PostMapping("/simulation/inputs")
    public ResponseEntity<SimulationSnapshot> updateInputs(@RequestBody UpdateInputsRequest request) {
        return ResponseEntity.ok(simulationService.updateInputs(request.values()));
    }

    @GetMapping("/simulation/state")
    public ResponseEntity<SimulationSnapshot> state() {
        return ResponseEntity.ok(simulationService.getSnapshot());
    }

    @GetMapping("/simulation/status")
    public ResponseEntity<String> status() {
        return ResponseEntity.ok(simulationService.getStatus().name());
    }
}
