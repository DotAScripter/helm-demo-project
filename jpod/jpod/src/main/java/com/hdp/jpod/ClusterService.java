package com.hdp.jpod;


//Representation of services in the cluster
public enum ClusterService {
    UNKNOWN(""),
    GREETINGS("HTTP_PORT"), // servicePortEnv should represent the envs set in helm charts
    JPOD("JPOD_HTTP_PORT");

    public final static int DEFAULT_SERVER_PORT = 8082;

    String servicePortEnv;
    ClusterService(String servicePortEnv) {
        this.servicePortEnv = servicePortEnv;
    }

    public int getServicePort() {
        String servicePortStr = System.getenv(servicePortEnv);
        if (servicePortStr == null || servicePortStr.length() == 0) {
            if (this == JPOD) { // if no port is set for Jpod env, set it to default
                return DEFAULT_SERVER_PORT;
            }
            return 0; // if env variables are not properly set, port will be 0
        }
        return Integer.parseInt(servicePortStr);
    }
}