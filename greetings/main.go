package main

import (
	"fmt"
	"time"
)

func main() {
	fmt.Println("Hello world!")
	go func() {
		for {
			time.Sleep(1 * time.Second)
			fmt.Println("I am alive")
		}
	}()
	select {}
}
