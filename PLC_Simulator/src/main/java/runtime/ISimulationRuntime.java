package runtime;

import java.util.Map;

public interface ISimulationRuntime {
    void step();

    void updateInputs(Map<String, Object> values);

    Map<String,Object> dumpInputs();

    Map<String,Object> dumpOutputs();

    Map<String,Object> dumpGlobals();

    Map<String,Object> dumpVars();

    Map<String,String> dumpProcessStates();

    Map<String,Long> dumpProcessTimers();
}
