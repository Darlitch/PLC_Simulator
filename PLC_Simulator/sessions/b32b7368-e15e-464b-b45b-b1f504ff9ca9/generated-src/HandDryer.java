import java.util.Map;
import java.util.List;
import java.util.Set;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.HashSet;

public class HandDryer {

    private final Map<String,Object> memory;
    private final List<IProcess> processes = new ArrayList<>();
    private final Map<String, IProcess> processMap;
    private final Set<String> inputNames = new HashSet<>();
    private final Set<String> outputNames = new HashSet<>();
    private final Set<String> globalNames = new HashSet<>();

    private void registerTo(Set<String> target, String name) {
        Object value = memory.get(name);

        if (value instanceof List) {
            List<String> list = (List<String>) value;
            for (String cell : list) {
                target.add(cell);
            }
        } else {
            target.add(name);
        }
    }

    public HandDryer(Map<String,Object> memory, Map<String, IProcess> processMap) {
        this.memory = memory;
        this.processMap = processMap;

        registerTo(inputNames, "hands");

        registerTo(outputNames, "control");

    }

    public void runIter(long cycleTimeMs) {
	
        memory.put(
            "_global_time",
            ((Long)memory.get("_global_time")) + cycleTimeMs
        );

        for (IProcess p : processes)
            p.run();
    }

    public void registerProcess(IProcess p) {
        processes.add(p);
        processMap.put(((BaseProcess)p).instanceName, p);
    }

    public Map<String,String> dumpProcessStates() {

        Map<String,String> res = new HashMap<>();

        for (IProcess p : processes)
            p.dumpStates(res);

        return res;
    }

    public Map<String,Long> dumpProcessTimers() {

        Map<String,Long> res = new HashMap<>();

        for (IProcess p : processes)
            p.dumpTimers(res);

        return res;
    }

    public Map<String,Object> dumpInputs() {

        Map<String,Object> res = new HashMap<>();

        for (String n : inputNames)
            res.put(n, memory.get(n));

        return res;
    }

    public void updateInputs(Map<String,Object> values) {

        if (values == null || values.isEmpty())
            return;

        for (Map.Entry<String,Object> e : values.entrySet()) {
            String n = e.getKey();

            if (inputNames.contains(n))
                memory.put(n, e.getValue());
        }
    }

    public Map<String,Object> dumpOutputs() {

        Map<String,Object> res = new HashMap<>();

        for (String n : outputNames)
            res.put(n, memory.get(n));

        return res;
    }

    public Map<String,Object> dumpGlobals() {

        Map<String,Object> res = new HashMap<>();

        for (String n : globalNames)
            res.put(n, memory.get(n));

        return res;
    }

    public Map<String,Object> dumpVars() {

        Map<String,Object> res = new HashMap<>();

        for (IProcess p : processes)
            p.dumpLocalVars(res);

        return res;
    }

    static class HandDryer1 extends BaseProcess {
	
        enum State {
            Wait,
            Work,
            Stop,
            Error
        }

        public HandDryer1(String instanceName, Map<String,Object> memory, Map<String,String> aliases, Map<String, IProcess> globalProcesses) {
            super(instanceName, memory, aliases, globalProcesses);

        }

        private State state = State.Stop;

        private long timerBaseTime;

        public void start() {
            state = State.Wait;
            timerBaseTime = ((Long)memory.get("_global_time"));
        }

        public void stop() {
            state = State.Stop;
            timerBaseTime = ((Long)memory.get("_global_time"));
        }

        public void error() {
            state = State.Error;
            timerBaseTime = ((Long)memory.get("_global_time"));
        }

        public void setState(State s) {
            state = s;
            timerBaseTime = ((Long)memory.get("_global_time"));
        }

        public void setNext() {

            switch(state) {
                case Wait -> state = State.Work;
                case Work -> state = State.Wait;
                default -> { }
            }

            timerBaseTime = ((Long)memory.get("_global_time"));
        }

        public State getState() {
            return state;
        }

        @Override
        public void run() {

            switch(state) {
                case Wait -> {
                    if (readBool("hands")) {
                        writeVar("control", true);
                        setNext();
                    }
                }
                case Work -> {
                    if (readBool("hands")) {
                        timerBaseTime = ((Long)memory.get("_global_time"));
                    }
                    if (((Long)memory.get("_global_time")) - this.timerBaseTime >= 2000L) {
                        writeVar("control", false);
                        setState(State.Wait);
                    }
                }
                case Stop, Error -> { }
            }
        }

        @Override
        public void dumpStates(Map<String,String> out) {
            out.put(instanceName + "_state", state.name());
        }

        @Override
        public void dumpTimers(Map<String,Long> out) {
            out.put(instanceName + "_time", timerBaseTime);
        }

        @Override
        public String getStateName() {
            return state.name();
        }

    }

}
