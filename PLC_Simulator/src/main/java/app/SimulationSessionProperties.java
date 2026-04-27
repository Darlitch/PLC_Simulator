package app;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "simulation.sessions")
public class SimulationSessionProperties {
    private String rootDir = "sessions";
    private Duration idleUnloadAfter = Duration.ofMinutes(15);
    private Duration deleteAfterUnload = Duration.ofHours(24);

    public String getRootDir() {
        return rootDir;
    }

    public void setRootDir(String rootDir) {
        this.rootDir = rootDir;
    }

    public Duration getIdleUnloadAfter() {
        return idleUnloadAfter;
    }

    public void setIdleUnloadAfter(Duration idleUnloadAfter) {
        this.idleUnloadAfter = idleUnloadAfter;
    }

    public Duration getDeleteAfterUnload() {
        return deleteAfterUnload;
    }

    public void setDeleteAfterUnload(Duration deleteAfterUnload) {
        this.deleteAfterUnload = deleteAfterUnload;
    }
}
