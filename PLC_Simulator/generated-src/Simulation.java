import java.util.Map;
import java.util.HashMap;

import runtime.ISimulationRuntime;

public class Simulation implements ISimulationRuntime {

    private final Map<String,Object> memory = new HashMap<>();
    private final Map<String, IProcess> processMap = new HashMap<>();
    private final long taskTimeMs;
    private final MX_220 plant_controller;

    public Simulation() {
    	memory.put("_global_time", 0L);
        memory.put("CUR_INTERVAL", 100);
        memory.put("ONE_SECOND", 1000);
        memory.put("CP_Troom_X_X_X", 20.0);
        memory.put("CP_G_U_BE_P", 0.014);
        memory.put("CP_V_U_X_B", 1500.0);
        memory.put("CP_V_U_BE_X", 30.0);
        memory.put("CP_V_U_E_R", 30.0);
        memory.put("CP_V_U_EU_X", 30.0);
        memory.put("CP_V_U_U_R", 30.0);
        memory.put("CP_V_U_UB_X", 30.0);
        memory.put("CP_Ccool_U_X_X", 3450.0);
        memory.put("CP_Ccool_U_U_R", 103500.0);
        memory.put("CP_Ccool_U_E_R", 103500.0);
        memory.put("DP_T_U_EU_U", 20.0);
        memory.put("CP_C_UCp_X_S", 18000.0);
        memory.put("CP_Ceqpt_U_U_X", 556.0);
        memory.put("CP_C_U_X_U", 18000.0);
        memory.put("CP_Ktrans_U_X_E", 90.0);
        memory.put("CP_Ktrans_U_X_U", 90.0);
        memory.put("CP_Ktrans_U_X_R", 90.0);
        memory.put("CP_G_Cp_X_Cp", 19.83);
        memory.put("CP_Kcp_Cp_X_Cp", 4.12);
        memory.put("AC_Tsep_Cp_E_S", 14.0);
        memory.put("AC_Qeqpt_U_U_U", 0.0);
        memory.put("AS_G_U_BE_P", 0.0f);
        memory.put("AS_T_U_UB_B", 20.0);
        memory.put("AS_T_U_X_B", 20.0);
        memory.put("AS_T_U_BE_B", 20.0);
        memory.put("AS_T_U_BE_X", 20.0);
        memory.put("AS_T_U_BE_E", 20.0);
        memory.put("AS_T_U_E_R", 20.0);
        memory.put("AS_T_U_EU_E", 20.0);
        memory.put("AS_T_U_EU_X", 20.0);
        memory.put("AS_T_U_EU_U", 20.0);
        memory.put("AS_T_U_U_R", 20.0);
        memory.put("AS_Tcool_U_U_R", 20.0);
        memory.put("AS_T_U_UB_U", 20.0);
        memory.put("AS_T_U_UB_X", 20.0);
        memory.put("AS_T_UCp_E_S", 20.0);
        memory.put("AS_Teqpt_U_U_X", 20.0);
        memory.put("AS_P_Cp_ECp_X", 0.0f);
        memory.put("AS_T_Cp_ECp_X", 20.0);
        memory.put("AS_P_Cp_CpCd_Cp", 0.0f);
        memory.put("AS_T_Cp_CpCd_Cp", 20.0);
        memory.put("AS_dV_Cp_ECp_Cp", 0.0f);
        memory.put("AS_dV_Cp_CpCd_Cp", 0.0f);
        memory.put("DA_X_U_BE_P", false);
        memory.put("DA_X_Cp_X_Cp", false);

        this.taskTimeMs = 100L;
        this.plant_controller = new MX_220(memory, processMap);

        Map<String,String> pump_aliases = new HashMap<>();
        pump_aliases.put("state", "DA_X_U_BE_P");
        pump_aliases.put("power", "CP_G_U_BE_P");
        pump_aliases.put("Fout", "AS_G_U_BE_P");
        MX_220.Pump pump = new MX_220.Pump("pump", memory, pump_aliases, processMap);
        plant_controller.registerProcess(pump);
        pump.start();

        Map<String,String> u_Tank_aliases = new HashMap<>();
        u_Tank_aliases.put("Vtank", "CP_V_U_X_B");
        u_Tank_aliases.put("G", "AS_G_U_BE_P");
        u_Tank_aliases.put("Tin", "AS_T_U_UB_B");
        u_Tank_aliases.put("Tinside", "AS_T_U_X_B");
        u_Tank_aliases.put("Tout", "AS_T_U_BE_B");
        MX_220.Tank u_Tank = new MX_220.Tank("u_Tank", memory, u_Tank_aliases, processMap);
        plant_controller.registerProcess(u_Tank);
        u_Tank.start();

        Map<String,String> u_BE_pipe_aliases = new HashMap<>();
        u_BE_pipe_aliases.put("Vtank", "CP_V_U_BE_X");
        u_BE_pipe_aliases.put("G", "AS_G_U_BE_P");
        u_BE_pipe_aliases.put("Tin", "AS_T_U_BE_B");
        u_BE_pipe_aliases.put("Tinside", "AS_T_U_BE_X");
        u_BE_pipe_aliases.put("Tout", "AS_T_U_BE_E");
        MX_220.Tank u_BE_pipe = new MX_220.Tank("u_BE_pipe", memory, u_BE_pipe_aliases, processMap);
        plant_controller.registerProcess(u_BE_pipe);
        u_BE_pipe.start();

        Map<String,String> u_Evaporator_aliases = new HashMap<>();
        u_Evaporator_aliases.put("Vtank", "CP_V_U_E_R");
        u_Evaporator_aliases.put("G", "AS_G_U_BE_P");
        u_Evaporator_aliases.put("Tin", "AS_T_U_BE_E");
        u_Evaporator_aliases.put("Tinside", "AS_T_U_E_R");
        u_Evaporator_aliases.put("Tout", "AS_T_U_EU_E");
        MX_220.Tank u_Evaporator = new MX_220.Tank("u_Evaporator", memory, u_Evaporator_aliases, processMap);
        plant_controller.registerProcess(u_Evaporator);
        u_Evaporator.start();

        Map<String,String> u_EvaporatorHeatTransfer_aliases = new HashMap<>();
        u_EvaporatorHeatTransfer_aliases.put("Ktrans", "CP_Ktrans_U_X_E");
        u_EvaporatorHeatTransfer_aliases.put("Cobj", "CP_C_UCp_X_S");
        u_EvaporatorHeatTransfer_aliases.put("Ccool", "CP_Ccool_U_E_R");
        u_EvaporatorHeatTransfer_aliases.put("Tobj", "AS_T_UCp_E_S");
        u_EvaporatorHeatTransfer_aliases.put("Tcool", "AS_T_U_E_R");
        MX_220.CoolantHeatTransfer u_EvaporatorHeatTransfer = new MX_220.CoolantHeatTransfer("u_EvaporatorHeatTransfer", memory, u_EvaporatorHeatTransfer_aliases, processMap);
        plant_controller.registerProcess(u_EvaporatorHeatTransfer);
        u_EvaporatorHeatTransfer.start();

        Map<String,String> u_DebugTseptum_aliases = new HashMap<>();
        u_DebugTseptum_aliases.put("AC", "AC_Tsep_Cp_E_S");
        u_DebugTseptum_aliases.put("AS", "AS_T_UCp_E_S");
        MX_220.AC2AS_Debug u_DebugTseptum = new MX_220.AC2AS_Debug("u_DebugTseptum", memory, u_DebugTseptum_aliases, processMap);
        plant_controller.registerProcess(u_DebugTseptum);
        u_DebugTseptum.start();

        Map<String,String> u_EU_pipe_aliases = new HashMap<>();
        u_EU_pipe_aliases.put("Vtank", "CP_V_U_EU_X");
        u_EU_pipe_aliases.put("G", "AS_G_U_BE_P");
        u_EU_pipe_aliases.put("Tin", "AS_T_U_EU_E");
        u_EU_pipe_aliases.put("Tinside", "AS_T_U_EU_X");
        u_EU_pipe_aliases.put("Tout", "AS_T_U_EU_U");
        MX_220.Tank u_EU_pipe = new MX_220.Tank("u_EU_pipe", memory, u_EU_pipe_aliases, processMap);
        plant_controller.registerProcess(u_EU_pipe);
        u_EU_pipe.start();

        Map<String,String> u_User_aliases = new HashMap<>();
        u_User_aliases.put("Vtank", "CP_V_U_U_R");
        u_User_aliases.put("G", "AS_G_U_BE_P");
        u_User_aliases.put("Tin", "AS_T_U_EU_U");
        u_User_aliases.put("Tinside", "AS_Tcool_U_U_R");
        u_User_aliases.put("Tout", "AS_T_U_UB_U");
        MX_220.Tank u_User = new MX_220.Tank("u_User", memory, u_User_aliases, processMap);
        plant_controller.registerProcess(u_User);
        u_User.start();

        Map<String,String> u_Equipment_aliases = new HashMap<>();
        u_Equipment_aliases.put("Qeqpt", "AC_Qeqpt_U_U_U");
        u_Equipment_aliases.put("Ceqpt", "CP_Ceqpt_U_U_X");
        u_Equipment_aliases.put("Teqpt", "AS_Teqpt_U_U_X");
        MX_220.Equipment u_Equipment = new MX_220.Equipment("u_Equipment", memory, u_Equipment_aliases, processMap);
        plant_controller.registerProcess(u_Equipment);
        u_Equipment.start();

        Map<String,String> u_UserRadiatorHeatTransfer_aliases = new HashMap<>();
        u_UserRadiatorHeatTransfer_aliases.put("Ktrans", "CP_Ktrans_U_X_R");
        u_UserRadiatorHeatTransfer_aliases.put("Cobj", "CP_Ceqpt_U_U_X");
        u_UserRadiatorHeatTransfer_aliases.put("Ccool", "CP_C_U_X_U");
        u_UserRadiatorHeatTransfer_aliases.put("Tobj", "AS_Teqpt_U_U_X");
        u_UserRadiatorHeatTransfer_aliases.put("Tcool", "AS_T_U_U_R");
        MX_220.CoolantHeatTransfer u_UserRadiatorHeatTransfer = new MX_220.CoolantHeatTransfer("u_UserRadiatorHeatTransfer", memory, u_UserRadiatorHeatTransfer_aliases, processMap);
        plant_controller.registerProcess(u_UserRadiatorHeatTransfer);
        u_UserRadiatorHeatTransfer.start();

        Map<String,String> u_UserRadiatorCoolantHeatTransfer_aliases = new HashMap<>();
        u_UserRadiatorCoolantHeatTransfer_aliases.put("Ktrans", "CP_Ktrans_U_X_U");
        u_UserRadiatorCoolantHeatTransfer_aliases.put("Cobj", "CP_C_U_X_U");
        u_UserRadiatorCoolantHeatTransfer_aliases.put("Ccool", "CP_Ccool_U_U_R");
        u_UserRadiatorCoolantHeatTransfer_aliases.put("Tobj", "AS_T_U_U_R");
        u_UserRadiatorCoolantHeatTransfer_aliases.put("Tcool", "AS_Tcool_U_U_R");
        MX_220.CoolantHeatTransfer u_UserRadiatorCoolantHeatTransfer = new MX_220.CoolantHeatTransfer("u_UserRadiatorCoolantHeatTransfer", memory, u_UserRadiatorCoolantHeatTransfer_aliases, processMap);
        plant_controller.registerProcess(u_UserRadiatorCoolantHeatTransfer);
        u_UserRadiatorCoolantHeatTransfer.start();

        Map<String,String> u_UB_pipe_aliases = new HashMap<>();
        u_UB_pipe_aliases.put("Vtank", "CP_V_U_UB_X");
        u_UB_pipe_aliases.put("G", "AS_G_U_BE_P");
        u_UB_pipe_aliases.put("Tin", "AS_T_U_UB_U");
        u_UB_pipe_aliases.put("Tinside", "AS_T_U_UB_X");
        u_UB_pipe_aliases.put("Tout", "AS_T_U_UB_B");
        MX_220.Tank u_UB_pipe = new MX_220.Tank("u_UB_pipe", memory, u_UB_pipe_aliases, processMap);
        plant_controller.registerProcess(u_UB_pipe);
        u_UB_pipe.start();
    }

    @Override
    public void step() {
    	plant_controller.runIter(taskTimeMs);
    }

    @Override
    public void updateInputs(Map<String, Object> values) {
    	plant_controller.updateInputs(values);
    }

    @Override
    public Map<String,Object> dumpInputs() {
    	Map<String,Object> res = new HashMap<>();
    	res.putAll(plant_controller.dumpInputs());
    	return res;
    }

    @Override
    public Map<String,Object> dumpOutputs() {
    	Map<String,Object> res = new HashMap<>();
    	res.putAll(plant_controller.dumpOutputs());
    	return res;
    }

    @Override
    public Map<String,Object> dumpGlobals() {
    	Map<String,Object> res = new HashMap<>();
    	res.putAll(plant_controller.dumpGlobals());
    	return res;
    }

    @Override
    public Map<String,Object> dumpVars() {
    	Map<String,Object> res = new HashMap<>();
    	res.putAll(plant_controller.dumpVars());
    	return res;
    }

    @Override
    public Map<String,String> dumpProcessStates() {
    	Map<String,String> res = new HashMap<>();
    	res.putAll(plant_controller.dumpProcessStates());
    	return res;
    }

    @Override
    public Map<String,Long> dumpProcessTimers() {
    	Map<String,Long> res = new HashMap<>();
    	res.putAll(plant_controller.dumpProcessTimers());
    	return res;
    }

}
