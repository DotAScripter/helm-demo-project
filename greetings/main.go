package main

import (
	"log/slog"
	"os"

	"github.com/DotAScripter/helm-demo-project/greetings/client"
	"github.com/DotAScripter/helm-demo-project/greetings/kvdb"
	"github.com/DotAScripter/helm-demo-project/greetings/service"
)

const (
	defaultHttpPort  = "3000"
	defaultRedisHost = "redis"
	defaultRedisPort = "6379"
)

func readEnv(name, defaultVal string) string {
	envVal := os.Getenv(name)
	if envVal == "" {
		return defaultVal
	}
	return envVal
}

func main() {
	var logLevel = new(slog.LevelVar) // Info by default
	logLevel.Set(slog.LevelDebug)
	logger := slog.New(slog.NewJSONHandler(os.Stdout, &slog.HandlerOptions{Level: logLevel}))
	slog.SetDefault(logger)

	httpPort := readEnv("HTTP_PORT", defaultHttpPort)
	redisHost := readEnv("REDIS_HOST", defaultRedisHost)
	redisPort := readEnv("REDIS_PORT", defaultRedisPort)

	slog.Info("Started", "httpPort", httpPort, "redisHost", redisHost, "redisPort", redisPort)

	kvdb := kvdb.NewKVDB(redisHost, redisPort)

	cppServiceHost := readEnv("CPP_SERVICE_HOST", "")
	cppServicePort := readEnv("CPP_SERVICE_PORT", "")
	slog.Info("CppService", "cppServicePort", cppServicePort, "cppServiceHost", cppServiceHost)
	helloClient, err := client.NewClient(cppServiceHost, cppServicePort)
	if err != nil {
		panic(err)
	}
	service := service.NewService(httpPort, kvdb, helloClient)

	service.Start()

	select {}
}
