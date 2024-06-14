package com.hdp.jpod;


//Representation of services in the cluster
public enum ClusterService {
     // serviceIPEnv and servicePortEnv should represent the envs set in helm charts
    CPPOD("Cppod", "CPPOD_SERVICE_IP", "CPPOD_SERVICE_PORT", true),
    PYPOD("Pypod", "PYPOD_SERVICE_IP", "PYPOD_SERVICE_PORT", true),
    JPOD("Jpod", "JPOD_SERVICE_IP", "JPOD_SERVICE_PORT", false),
    JPODHTTP("Jpod http", "JPOD_SERVICE_IP", "JPOD_HTTP_PORT", false);

    public final static String DEFAULT_SERVICE_IP = "localhost";
    public final static int DEFAULT_SERVICE_PORT = 8082;
    public final static int DEFAULT_HTTP_PORT = 8083;

    String serviceName;
    String serviceIPEnv;
    String servicePortEnv;
    boolean statusCheckEnabled;

    ClusterService(String serviceName, String serviceIPEnv, String servicePortEnv, boolean statusCheckEnabled) {
        this.serviceName = serviceName;
        this.serviceIPEnv = serviceIPEnv;
        this.servicePortEnv = servicePortEnv;
        this.statusCheckEnabled = statusCheckEnabled;
    }

    public int readPortEnv(String servicePortEnv) {
        String servicePortStr = System.getenv(servicePortEnv);
        if (servicePortStr == null || servicePortStr.length() == 0) {
            if (this == JPOD) { // if no port is set for Jpod service port env, set it to default
                return DEFAULT_SERVICE_PORT;
            } else if (this == JPODHTTP) { // if no port is set for Jpod http port env, set it to default
                return DEFAULT_HTTP_PORT;
            }
            return 0; // if env variables are not properly set, port will be 0
        }
        return Integer.parseInt(servicePortStr);
    }

    public String readIPEnv(String serviceIPEnv) {
        String serviceIPStr = System.getenv(serviceIPEnv);
        if (serviceIPStr == null || serviceIPStr.length() == 0) {
            if (this == JPOD) { // if no IP is set for Jpod service IP env, set it to default
                return DEFAULT_SERVICE_IP;
            }
            return null; // if env variables are not properly set, IP will be null
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