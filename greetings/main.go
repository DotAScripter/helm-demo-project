package main

import (
	"context"
	"fmt"
	"net/http"
	"os"

	"github.com/redis/go-redis/v9"
)

const defaultPort = "3000"

func handler(w http.ResponseWriter, r *http.Request) {
	fmt.Println("Got request!")
	fmt.Fprintf(w, "Hello, World!\n")
}

func main() {
	fmt.Println("Hello world!")

	httpPort := os.Getenv("HTTP_PORT")

	// Check if the environment variable is set
	if httpPort == "" {
		fmt.Println("HTTP_PORT is not set using default:", defaultPort)
		httpPort = defaultPort
	} else {
		fmt.Println("HTTP_PORT is set to:", httpPort)
	}

	redisClient := redis.NewClient(&redis.Options{
		Addr:     "redis:6379",
		Password: "", // no password set
		DB:       0,  // use default DB
	})

	http.HandleFunc("/", handler)
	http.HandleFunc("/redis/set", func(w http.ResponseWriter, r *http.Request) {
		fmt.Println("Got request! /redis/set")
		err := redisClient.Set(context.Background(), "testkey", "testval", 0).Err()
		if err != nil {
			fmt.Println("/redis/set NOK, err:", err)
			fmt.Fprintf(w, "/redis/set NOK, err:%v\n", err)
			return
		}
		fmt.Fprintf(w, "/redis/set OK\n")
	})
	http.HandleFunc("/redis/get", func(w http.ResponseWriter, r *http.Request) {
		fmt.Println("Got request! /redis/get")
		val, err := redisClient.Get(context.Background(), "testkey").Result()
		if err != nil {
			fmt.Println("/redis/get NOK, err:", err)
			fmt.Fprintf(w, "/redis/get NOK, err:%v\n", err)
			return
		}
		fmt.Fprintf(w, "/redis/get OK val:%s\n", val)
	})
	fmt.Println("Server is listening on port", httpPort)
	err := http.ListenAndServe(fmt.Sprintf(":%s", httpPort), nil)
	if err != nil {
		fmt.Println("Error starting server:", err)
	}
	select {}
}
