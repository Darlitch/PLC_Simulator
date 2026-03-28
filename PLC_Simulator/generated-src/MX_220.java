import java.util.Map;
import java.util.List;
import java.util.Set;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.HashSet;

public class MX_220 {

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

    public MX_220(Map<String,Object> memory, Map<String, IProcess> processMap) {
        this.memory = memory;
        this.processMap = processMap;

        registerTo(inputNames, "DA_X_U_BE_P");
        registerTo(inputNames, "AS_T_U_UB_U");
        registerTo(inputNames, "CP_V_U_E_R");
        registerTo(inputNames, "CP_Ktrans_U_X_E");
        registerTo(inputNames, "CP_C_UCp_X_S");
        registerTo(inputNames, "CP_V_U_X_B");
        registerTo(inputNames, "CP_V_U_BE_X");
        registerTo(inputNames, "AS_T_U_BE_E");
        registerTo(inputNames, "CP_C_U_X_U");
        registerTo(inputNames, "AS_T_U_BE_B");
        registerTo(inputNames, "AS_T_U_EU_E");
        registerTo(inputNames, "CP_Ktrans_U_X_U");
        registerTo(inputNames, "CP_V_U_UB_X");
        registerTo(inputNames, "CP_Ktrans_U_X_R");
        registerTo(inputNames, "CP_V_U_EU_X");
        registerTo(inputNames, "CP_Ceqpt_U_U_X");
        registerTo(inputNames, "AS_T_U_EU_U");
        registerTo(inputNames, "CP_Ccool_U_U_R");
        registerTo(inputNames, "AS_G_U_BE_P");
        registerTo(inputNames, "AC_Tsep_Cp_E_S");
        registerTo(inputNames, "CP_G_U_BE_P");
        registerTo(inputNames, "AS_T_U_UB_B");
        registerTo(inputNames, "CP_Ccool_U_E_R");
        registerTo(inputNames, "AC_Qeqpt_U_U_U");
        registerTo(inputNames, "CP_V_U_U_R");

        registerTo(outputNames, "AS_T_U_BE_X");
        registerTo(outputNames, "AS_Tcool_U_U_R");
        registerTo(outputNames, "AS_T_U_U_R");
        registerTo(outputNames, "AS_T_U_UB_U");
        registerTo(outputNames, "AS_Teqpt_U_U_X");
        registerTo(outputNames, "AS_T_UCp_E_S");
        registerTo(outputNames, "AS_T_U_EU_U");
        registerTo(outputNames, "AS_T_U_EU_X");
        registerTo(outputNames, "AS_G_U_BE_P");
        registerTo(outputNames, "AS_T_U_UB_X");
        registerTo(outputNames, "AS_T_U_E_R");
        registerTo(outputNames, "AS_T_U_BE_E");
        registerTo(outputNames, "AS_T_U_UB_B");
        registerTo(outputNames, "AS_T_U_BE_B");
        registerTo(outputNames, "AS_T_U_EU_E");
        registerTo(outputNames, "AS_T_U_X_B");

        registerTo(globalNames, "AS_T_U_BE_X");
        registerTo(globalNames, "AS_Tcool_U_U_R");
        registerTo(globalNames, "DA_X_U_BE_P");
        registerTo(globalNames, "DA_X_Cp_X_Cp");
        registerTo(globalNames, "AS_T_U_UB_U");
        registerTo(globalNames, "AS_Teqpt_U_U_X");
        registerTo(globalNames, "CP_V_U_E_R");
        registerTo(globalNames, "CP_Ktrans_U_X_E");
        registerTo(globalNames, "CP_C_UCp_X_S");
        registerTo(globalNames, "AS_T_U_UB_X");
        registerTo(globalNames, "CP_V_U_X_B");
        registerTo(globalNames, "CP_V_U_BE_X");
        registerTo(globalNames, "AS_T_U_E_R");
        registerTo(globalNames, "CP_Ccool_U_X_X");
        registerTo(globalNames, "AS_T_U_BE_E");
        registerTo(globalNames, "CP_C_U_X_U");
        registerTo(globalNames, "AS_T_U_BE_B");
        registerTo(globalNames, "AS_T_U_EU_E");
        registerTo(globalNames, "AS_T_Cp_ECp_X");
        registerTo(globalNames, "AS_dV_Cp_ECp_Cp");
        registerTo(globalNames, "CP_Ktrans_U_X_U");
        registerTo(globalNames, "DP_T_U_EU_U");
        registerTo(globalNames, "CP_V_U_UB_X");
        registerTo(globalNames, "AS_T_U_X_B");
        registerTo(globalNames, "CP_Ktrans_U_X_R");
        registerTo(globalNames, "AS_T_U_U_R");
        registerTo(globalNames, "AS_dV_Cp_CpCd_Cp");
        registerTo(globalNames, "CP_V_U_EU_X");
        registerTo(globalNames, "AS_T_UCp_E_S");
        registerTo(globalNames, "CP_Ceqpt_U_U_X");
        registerTo(globalNames, "AS_T_U_EU_U");
        registerTo(globalNames, "CP_Ccool_U_U_R");
        registerTo(globalNames, "AS_T_U_EU_X");
        registerTo(globalNames, "AS_G_U_BE_P");
        registerTo(globalNames, "AS_P_Cp_ECp_X");
        registerTo(globalNames, "AC_Tsep_Cp_E_S");
        registerTo(globalNames, "CP_G_U_BE_P");
        registerTo(globalNames, "AS_T_U_UB_B");
        registerTo(globalNames, "CP_Troom_X_X_X");
        registerTo(globalNames, "CP_Ccool_U_E_R");
        registerTo(globalNames, "CP_Kcp_Cp_X_Cp");
        registerTo(globalNames, "AC_Qeqpt_U_U_U");
        registerTo(globalNames, "CP_G_Cp_X_Cp");
        registerTo(globalNames, "AS_T_Cp_CpCd_Cp");
        registerTo(globalNames, "CUR_INTERVAL");
        registerTo(globalNames, "CP_V_U_U_R");
        registerTo(globalNames, "ONE_SECOND");
        registerTo(globalNames, "AS_P_Cp_CpCd_Cp");
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

    static class AC2AS_Debug extends BaseProcess {
	
        enum State {
            Calc,
            Stop,
            Error
        }

        public AC2AS_Debug(String instanceName, Map<String,Object> memory, Map<String,String> aliases, Map<String, IProcess> globalProcesses) {
            super(instanceName, memory, aliases, globalProcesses);

        }

        private State state = State.Stop;

        private long timerBaseTime;

        public void start() {
            state = State.Calc;
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
                case Calc -> state = State.Calc;
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
                case Calc -> {
                    writeVar("AS", readFloat("AC"));
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

    static class Compressor extends BaseProcess {
	
        enum State {
            Calc,
            Stop,
            Error
        }

        public Compressor(String instanceName, Map<String,Object> memory, Map<String,String> aliases, Map<String, IProcess> globalProcesses) {
            super(instanceName, memory, aliases, globalProcesses);

        }

        private State state = State.Stop;

        private long timerBaseTime;

        public void start() {
            state = State.Calc;
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
                case Calc -> state = State.Calc;
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
                case Calc -> {
                    if (readBool("state")) {
                        writeVar("dVin", (readFloat("G") * 100));
                        writeVar("Pout", ((float)((Math.pow(
			(double)readFloat("CR"),
			(double)1.16
		) * readFloat("Pin")))));
                        writeVar("Tout", ((float)((Math.pow(
			(double)readFloat("CR"),
			(double)0.16
		) * readFloat("Tin")))));
                        writeVar("dVout", (readFloat("CR") * readFloat("dVin")));
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

    static class Pump extends BaseProcess {
	
        enum State {
            CalcFlow,
            Stop,
            Error
        }

        public Pump(String instanceName, Map<String,Object> memory, Map<String,String> aliases, Map<String, IProcess> globalProcesses) {
            super(instanceName, memory, aliases, globalProcesses);

        }

        private State state = State.Stop;

        private long timerBaseTime;

        public void start() {
            state = State.CalcFlow;
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
                case CalcFlow -> state = State.CalcFlow;
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
                case CalcFlow -> {
                    if (readBool("state")) {
                        writeVar("Fout", ((float)(0.014)));
                    }
                    else {
                        writeVar("Fout", ((float)(0.0)));
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

    static class Tank extends BaseProcess {
	
        enum State {
            CalcTemperatures,
            Stop,
            Error
        }

        public Tank(String instanceName, Map<String,Object> memory, Map<String,String> aliases, Map<String, IProcess> globalProcesses) {
            super(instanceName, memory, aliases, globalProcesses);

            localMemory.put("dV", 0.0f);
        }

        private State state = State.Stop;

        private long timerBaseTime;

        public void start() {
            state = State.CalcTemperatures;
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
                case CalcTemperatures -> state = State.CalcTemperatures;
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
                case CalcTemperatures -> {
                    writeVar("dV", (readFloat("G") * 100));
                    writeVar("Tout", readFloat("Tinside"));
                    writeVar("Tinside", ((float)(((double)((((readFloat("Tinside") * ((30.0 - readFloat("dV")))) + (readFloat("Tin") * readFloat("dV"))))) / (double)(30.0)))));
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

    static class Equipment extends BaseProcess {
	
        enum State {
            CalcTemperature,
            Stop,
            Error
        }

        public Equipment(String instanceName, Map<String,Object> memory, Map<String,String> aliases, Map<String, IProcess> globalProcesses) {
            super(instanceName, memory, aliases, globalProcesses);

        }

        private State state = State.Stop;

        private long timerBaseTime;

        public void start() {
            state = State.CalcTemperature;
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
                case CalcTemperature -> state = State.CalcTemperature;
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
                case CalcTemperature -> {
                    writeVar("Teqpt", ((float)((readFloat("Teqpt") + ((double)((readFloat("Qeqpt") * 100)) / (double)(556.0))))));
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

    static class CoolantHeatTransfer extends BaseProcess {
	
        enum State {
            CalcTemperatures,
            Stop,
            Error
        }

        public CoolantHeatTransfer(String instanceName, Map<String,Object> memory, Map<String,String> aliases, Map<String, IProcess> globalProcesses) {
            super(instanceName, memory, aliases, globalProcesses);

            localMemory.put("Q", 0.0f);
            localMemory.put("C", 0.0f);
        }

        private State state = State.Stop;

        private long timerBaseTime;

        public void start() {
            state = State.CalcTemperatures;
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
                case CalcTemperatures -> state = State.CalcTemperatures;
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
                case CalcTemperatures -> {
                    writeVar("Q", ((float)(((((readFloat("Tobj") - readFloat("Tcool"))) * 90.0) * 100))));
                    if (((((double)(18000.0)) < ((double)(103500.0))))) {
                        writeVar("C", ((float)(18000.0)));
                    }
                    else {
                        writeVar("C", ((float)(103500.0)));
                    }
                    if (((((double)(readFloat("Q"))) > ((double)(0.0))))) {
                        if (((((double)(readFloat("Q"))) > ((double)((((double)((((readFloat("Tobj") - readFloat("Tcool"))) * readFloat("C"))) / (double)(2.0)))))))) {
                            writeVar("Q", ((float)(((double)((((readFloat("Tobj") - readFloat("Tcool"))) * readFloat("C"))) / (double)(2.0)))));
                        }
                    }
                    else {
                        if (((((double)(readFloat("Q"))) < ((double)((((double)((((readFloat("Tobj") - readFloat("Tcool"))) * readFloat("C"))) / (double)(2.0)))))))) {
                            writeVar("Q", ((float)(((double)((((readFloat("Tobj") - readFloat("Tcool"))) * readFloat("C"))) / (double)(2.0)))));
                        }
                    }
                    writeVar("Tobj", ((float)(((double)((((readFloat("Tobj") * 18000.0) - readFloat("Q")))) / (double)(18000.0)))));
                    writeVar("Tcool", ((float)(((double)(((((readFloat("Tcool") * 103500.0)) + readFloat("Q")))) / (double)(103500.0)))));
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
