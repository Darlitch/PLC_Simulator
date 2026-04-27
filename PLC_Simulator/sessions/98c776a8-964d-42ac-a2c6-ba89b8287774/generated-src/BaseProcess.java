import java.util.Map;
import java.util.List;
import java.util.HashMap;

public abstract class BaseProcess implements IProcess {

    protected final String instanceName;
    protected final Map<String,IProcess> processRefs = new HashMap<>();
    protected final Map<String,Object> memory;
    protected final Map<String,Object> localMemory = new HashMap<>();
    protected final Map<String,String> aliases;
    protected final Map<String, IProcess> globalProcesses;

    protected BaseProcess(String instanceName, Map<String,Object> memory, 
    	Map<String,String> aliases, Map<String, IProcess> globalProcesses) {
        this.instanceName = instanceName;
        this.memory = memory;
        this.aliases = aliases;
        this.globalProcesses = globalProcesses;
    }
    
    @Override
    public void dumpLocalVars(Map<String,Object> out) {
        for (Map.Entry<String,Object> e : localMemory.entrySet()) {
            out.put(instanceName + "_" + e.getKey(), e.getValue());
        }
    }
    
    public void setProcess(String name, IProcess p) {
        processRefs.put(name, p);
    }
    
    protected IProcess getProcess(String name) {
        IProcess p = processRefs.get(name);
    
        if (p != null)
            return p;
    
        p = globalProcesses.get(name);
    
        if (p != null)
            return p;
    
        throw new RuntimeException("Process not found: " + name);
    }

    protected String resolve(String name) {
        String current = name;
        if (aliases != null) {
            while (aliases.containsKey(current)) {
                current = aliases.get(current);
            }
        }
        return current;
    }

    protected Object read(String name) {
        if (localMemory.containsKey(name))
            return localMemory.get(name);
            
        String resolved = resolve(name);
    
        return memory.get(resolved);
    }

    protected void writeVar(String name, Object value) {
        if (localMemory.containsKey(name)) {
                localMemory.put(name, value);
                return;
            }
        
        String resolved = resolve(name);
        memory.put(resolved, value);
    }
    
    protected boolean isActive(IProcess p) {
        String s = p.getStateName();
        return !s.equals("Stop") && !s.equals("Error");
    }

    protected boolean isInactive(IProcess p) {
        String s = p.getStateName();
        return s.equals("Stop") || s.equals("Error");
    }

    protected boolean isStop(IProcess p) {
        return p.getStateName().equals("Stop");
    }

    protected boolean isError(IProcess p) {
        return p.getStateName().equals("Error");
    }

    protected boolean loopCond(String var, int end, int step) {
        int value = readInt(var);
        return (step >= 0 && value <= end)
            || (step < 0 && value >= end);
    }

    // ===== SAFE READ =====

    protected int readInt(String name) {
        Object v = read(name);
        if (v instanceof Number)
            return ((Number) v).intValue();
        throw new RuntimeException("Expected INT for " + name + ", got " + v);
    }

    protected long readLong(String name) {
        Object v = read(name);
        if (v instanceof Number)
            return ((Number) v).longValue();
        throw new RuntimeException("Expected LONG for " + name + ", got " + v);
    }

    protected double readDouble(String name) {
        Object v = read(name);
        if (v instanceof Number)
            return ((Number) v).doubleValue();
        throw new RuntimeException("Expected DOUBLE for " + name + ", got " + v);
    }

    protected float readFloat(String name) {
        Object v = read(name);
        if (v instanceof Number)
            return ((Number) v).floatValue();
        throw new RuntimeException("Expected FLOAT for " + name + ", got " + v);
    }

    protected boolean readBool(String name) {
        Object v = read(name);
        if (v instanceof Boolean)
            return (Boolean) v;
        throw new RuntimeException("Expected BOOL for " + name + ", got " + v);
    }
    
    protected void setArrayValue(String name, int index, int start, Object value) {
        memory.put(resolveArrayCell(name, index, start), value);
    }
    
    protected Object getArrayValue(String name, int index, int start) {
        return memory.get(resolveArrayCell(name, index, start));
    }
    
    protected int getArrayInt(String name, int index, int start) {
        Object v = getArrayValue(name, index, start);
        if (v instanceof Number)
            return ((Number) v).intValue();
        throw new RuntimeException("Expected INT in array " + name + ", got " + v);
    }
    
    protected long getArrayLong(String name, int index, int start) {
        Object v = getArrayValue(name, index, start);
        if (v instanceof Number)
            return ((Number) v).longValue();
        throw new RuntimeException("Expected LONG in array " + name + ", got " + v);
    }
    
    protected float getArrayFloat(String name, int index, int start) {
        Object v = getArrayValue(name, index, start);
        if (v instanceof Number)
            return ((Number) v).floatValue();
        throw new RuntimeException("Expected FLOAT in array " + name + ", got " + v);
    }
    
    protected double getArrayDouble(String name, int index, int start) {
        Object v = getArrayValue(name, index, start);
        if (v instanceof Number)
            return ((Number) v).doubleValue();
        throw new RuntimeException("Expected DOUBLE in array " + name + ", got " + v);
    }
    
    protected boolean getArrayBool(String name, int index, int start) {
        Object v = getArrayValue(name, index, start);
        if (v instanceof Boolean)
            return (Boolean) v;
        throw new RuntimeException("Expected BOOL in array " + name + ", got " + v);
    }
    
    private String resolveArrayCell(String name, int index, int start) {
        List<String> list = (List<String>) memory.get(resolve(name));
    
        int offset = index - start;
    
        if (offset < 0 || offset >= list.size()) {
            throw new RuntimeException(
                "Array index out of bounds: " + name + "[" + index + "]"
            );
        }
    
        return list.get(offset);
    }
}
