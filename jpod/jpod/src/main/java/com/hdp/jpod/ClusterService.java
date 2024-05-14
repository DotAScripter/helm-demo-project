package com.hdp.jpod;


//Representation of services in the cluster
public enum ClusterService {
    UNKNOWN(""),
    GREETINGS("HTTP_PORT"), // servicePortEnv should represent the envs set in helm charts
    JPOD("JPOD_SERVICE_PORT"),
    JPODHTTP("JPOD_HTTP_PORT");

    public final static int DEFAULT_SERVICE_PORT = 8082;
    public final static int DEFAULT_HTTP_PORT = 8083;

    String servicePortEnv;
    ClusterService(String servicePortEnv) {
        this.servicePortEnv = servicePortEnv;
    }

    public int getServicePort() {
        String servicePortStr = System.getenv(servicePortEnv);
        if (servicePortStr == null || servicePortStr.length() == 0) {
            if (this == JPOD) { // if no port is set for Jpod service port env, set it to default
                return DEFAULT_SERVICE_PORT;
            }
            if (this == JPODHTTP) { // if no port is set for Jpod http port env, set it to default
                return DEFAULT_HTTP_PORT;
            }
            return 0; // if env variables are not properly set, port will be 0
        }
        return Integer.parseInt(servicePortStr);
    }
}