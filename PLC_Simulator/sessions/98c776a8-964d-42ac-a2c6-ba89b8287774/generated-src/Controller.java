import java.util.Map;
import java.util.List;
import java.util.Set;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.HashSet;

public class Controller {

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

    public Controller(Map<String,Object> memory, Map<String, IProcess> processMap) {
        this.memory = memory;
        this.processMap = processMap;

        registerTo(inputNames, "lightsArray1");
        registerTo(inputNames, "lightsArray2");
        registerTo(inputNames, "sensor");

        registerTo(outputNames, "red2");
        registerTo(outputNames, "red1");
        registerTo(outputNames, "green2");
        registerTo(outputNames, "green1");
        registerTo(outputNames, "yellow1");
        registerTo(outputNames, "yellow2");

        registerTo(globalNames, "lightsArray1");
        registerTo(globalNames, "lightsArray2");
        registerTo(globalNames, "red2");
        registerTo(globalNames, "red1");
        registerTo(globalNames, "green2");
        registerTo(globalNames, "green1");
        registerTo(globalNames, "sensor");
        registerTo(globalNames, "NUMBER_OF_LIGHTS");
        registerTo(globalNames, "yellow1");
        registerTo(globalNames, "yellow2");
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

    static class Light extends BaseProcess {
	
        enum State {
            Light,
            Stop,
            Error
        }

        public Light(String instanceName, Map<String,Object> memory, Map<String,String> aliases, Map<String, IProcess> globalProcesses) {
            super(instanceName, memory, aliases, globalProcesses);

        }

        private State state = State.Stop;

        private long timerBaseTime;

        public void start() {
            state = State.Light;
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
                case Light -> state = State.Light;
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
                case Light -> {
                    writeVar("b_light", true);
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

    static class Control extends BaseProcess {
	
        enum State {
            Work,
            delay10,
            delay30,
            Stop,
            Error
        }

        public Control(String instanceName, Map<String,Object> memory, Map<String,String> aliases, Map<String, IProcess> globalProcesses) {
            super(instanceName, memory, aliases, globalProcesses);

            localMemory.put("prev_light", 0);
            localMemory.put("alight", 0);
            localMemory.put("pressed", false);
        }

        private State state = State.Stop;

        private long timerBaseTime;

        public void start() {
            state = State.Work;
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
                case Work -> state = State.delay10;
                case delay10 -> state = State.delay30;
                case delay30 -> state = State.Work;
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
                case Work -> {
                    if (readBool("pressed")) {
                        writeVar("prev_light", 0);
                        writeVar("pressed", false);
                    }
                    else if (((((isInactive(getProcess("pRed"))) && (isActive(getProcess("pGreen"))))) || (((isInactive(getProcess("pGreen"))) && (isActive(getProcess("pRed"))))))) {
                        int __start = ((Number)(1)).intValue();
                        int __end   = ((Number)(2)).intValue();
                        int __step  = ((Number)(1)).intValue();

                        if (__step == 0)
                            throw new RuntimeException("FOR step cannot be zero");

                        writeVar("alight", __start);

                        while (loopCond("alight", __end, __step)) {
                            if (getArrayBool("rLightsArray", readInt("alight"), 0)) {
                                writeVar("prev_light", readInt("alight"));
                            }
                            setArrayValue("rLightsArray", readInt("alight"), 0, false);
                            writeVar("alight", readInt("alight") + __step);
                        }
                        getProcess("pRed").stop();
                        getProcess("pYellow").start();
                        getProcess("pGreen").stop();
                        setState(State.delay10);
                    }
                    else if ((readInt("prev_light") == 0)) {
                        int __start = ((Number)(1)).intValue();
                        int __end   = ((Number)(2)).intValue();
                        int __step  = ((Number)(1)).intValue();

                        if (__step == 0)
                            throw new RuntimeException("FOR step cannot be zero");

                        writeVar("alight", __start);

                        while (loopCond("alight", __end, __step)) {
                            setArrayValue("rLightsArray", readInt("alight"), 0, false);
                            writeVar("alight", readInt("alight") + __step);
                        }
                        getProcess("pRed").stop();
                        getProcess("pYellow").stop();
                        getProcess("pGreen").start();
                        setState(State.delay30);
                    }
                    else if ((readInt("prev_light") == 2)) {
                        int __start = ((Number)(1)).intValue();
                        int __end   = ((Number)(2)).intValue();
                        int __step  = ((Number)(1)).intValue();

                        if (__step == 0)
                            throw new RuntimeException("FOR step cannot be zero");

                        writeVar("alight", __start);

                        while (loopCond("alight", __end, __step)) {
                            setArrayValue("rLightsArray", readInt("alight"), 0, false);
                            writeVar("alight", readInt("alight") + __step);
                        }
                        getProcess("pRed").start();
                        getProcess("pYellow").stop();
                        getProcess("pGreen").stop();
                        setState(State.delay30);
                    }
                }
                case delay10 -> {
                    if (((Long)memory.get("_global_time")) - this.timerBaseTime >= 10000L) {
                        setState(State.Work);
                        if (readBool("control_sensor")) {
                            writeVar("pressed", true);
                        }
                    }
                }
                case delay30 -> {
                    if ((readBool("control_sensor") && isActive(getProcess("pRed")))) {
                        writeVar("pressed", true);
                        setState(State.Work);
                    }
                    if (((Long)memory.get("_global_time")) - this.timerBaseTime >= 30000L) {
                        writeVar("pressed", false);
                        setState(State.Work);
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
