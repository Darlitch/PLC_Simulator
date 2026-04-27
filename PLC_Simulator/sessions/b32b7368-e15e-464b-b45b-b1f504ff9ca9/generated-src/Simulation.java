import java.util.Map;
import java.util.HashMap;

import runtime.ISimulationRuntime;

public class Simulation implements ISimulationRuntime {

    private final Map<String,Object> memory = new HashMap<>();
    private final Map<String, IProcess> processMap = new HashMap<>();
    private final long taskTimeMs;
    private final HandDryer handDryer;

    public Simulation() {
    	memory.put("_global_time", 0L);
        this.taskTimeMs = 100L;
        memory.put("hands", false);
        memory.put("control", false);

        this.handDryer = new HandDryer(memory, processMap);

        Map<String,String> handDryer1_aliases = new HashMap<>();
        HandDryer.HandDryer1 handDryer1 = new HandDryer.HandDryer1("handDryer1", memory, handDryer1_aliases, processMap);
        handDryer.registerProcess(handDryer1);
        handDryer1.start();
    }

    @Override
    public void step() {
    	handDryer.runIter(taskTimeMs);
    }

    @Override
    public void updateInputs(Map<String, Object> values) {
    	handDryer.updateInputs(values);
    }

    @Override
    public Map<String,Object> dumpInputs() {
    	Map<String,Object> res = new HashMap<>();
    	res.putAll(handDryer.dumpInputs());
    	return res;
    }

    @Override
    public Map<String,Object> dumpOutputs() {
    	Map<String,Object> res = new HashMap<>();
    	res.putAll(handDryer.dumpOutputs());
    	return res;
    }

    @Override
    public Map<String,Object> dumpGlobals() {
    	Map<String,Object> res = new HashMap<>();
    	res.putAll(handDryer.dumpGlobals());
    	return res;
    }

    @Override
    public Map<String,Object> dumpVars() {
    	Map<String,Object> res = new HashMap<>();
    	res.putAll(handDryer.dumpVars());
    	return res;
    }

    @Override
    public Map<String,String> dumpProcessStates() {
    	Map<String,String> res = new HashMap<>();
    	res.putAll(handDryer.dumpProcessStates());
    	return res;
    }

    @Override
    public Map<String,Long> dumpProcessTimers() {
    	Map<String,Long> res = new HashMap<>();
    	res.putAll(handDryer.dumpProcessTimers());
    	return res;
    }

}
