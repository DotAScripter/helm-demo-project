package com.hdp.jpod;


//Representation of services in the cluster
public enum ClusterService {
    CPPOD("Cppod", "CPPOD_SERVICE_IP", "CPPOD_SERVICE_PORT", true),
    PYPOD("Pypod", "PYPOD_SERVICE_IP", "PYPOD_SERVICE_PORT", true),
    GREETINGS("Greetings", "GREETINGS_SERVICE_IP", "GREETINGS_SERVICE_PORT", true),
    JPOD_GREETER("Jpod greeter", "JPOD_GREETER_SERVICE_IP", "JPOD_GREETER_SERVICE_PORT", false),
    JPOD_STATUS("Jpod status", "JPOD_STATUS_SERVICE_IP", "JPOD_STATUS_SERVICE_PORT", true),
    JPOD_HTTP("Jpod http", "JPOD_HTTP_SERVICE_IP", "JPOD_HTTP_PORT", false);

    private String serviceName;
    private String serviceIPEnv;
    private String servicePortEnv;
    private boolean statusCheckEnabled;

    ClusterService(String serviceName, String serviceIPEnv, String servicePortEnv, boolean statusCheckEnabled) {
        this.serviceName = serviceName;
        this.serviceIPEnv = serviceIPEnv;
        this.servicePortEnv = servicePortEnv;
        this.statusCheckEnabled = statusCheckEnabled;
    }

    public int readPortEnv(String servicePortEnv) {
        String servicePortStr = System.getenv(servicePortEnv);
        if (servicePortStr == null || servicePortStr.length() == 0) {
            LogHandler.getInstance().error("Port env: " + servicePortEnv + " is not set. Setting port to 0");
            return 0; // if env variable is not properly set, port will be 0.
        }
        return Integer.parseInt(servicePortStr);
    }

    public String readIPEnv(String serviceIPEnv) {
        String serviceIPStr = System.getenv(serviceIPEnv);
        if (serviceIPStr == null || serviceIPStr.length() == 0) {
            LogHandler.getInstance().error("IP env: " + serviceIPEnv + " is not set. Setting IP to null");
            return null; // if env variable is not properly set, IP will be null.
        }
        return serviceIPStr;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getServiceIP() {
        return readIPEnv(serviceIPEnv);
    }
    
    public int getServicePort() {
        return readPortEnv(servicePortEnv);
    }   

    public boolean isStatusCheckEnabled() {
        return statusCheckEnabled;
    }
}