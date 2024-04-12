package main

import (
	"fmt"
	"net/http"
	"os"
)

const defaultPort = "3000"

func handler(w http.ResponseWriter, r *http.Request) {
	fmt.Println("Got request!")
	fmt.Fprintf(w, "Hello, World!")
}

func main() {
	fmt.Println("Hello world!")
	// go func() {
	// 	for {
	// 		time.Sleep(1 * time.Second)
	// 		fmt.Println("I am alive")
	// 	}
	// }()

	httpPort := os.Getenv("HTTP_PORT")

	// Check if the environment variable is set
	if httpPort == "" {
		fmt.Println("HTTP_PORT is not set using default:", defaultPort)
		httpPort = defaultPort
	} else {
		fmt.Println("HTTP_PORT is set to:", httpPort)
	}

	http.HandleFunc("/", handler)
	fmt.Println("Server is listening on port", httpPort)
	err := http.ListenAndServe(fmt.Sprintf(":%s", httpPort), nil)
	if err != nil {
		fmt.Println("Error starting server:", err)
	}
	select {}
}
