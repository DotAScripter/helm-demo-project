package client_test

import (
	"context"
	"flag"
	"fmt"
	"log"
	"net"
	"testing"

	"github.com/DotAScripter/helm-demo-project/greetings/client"
	pb "github.com/DotAScripter/helm-demo-project/greetings/proto/helloworld"
	"github.com/stretchr/testify/assert"
	"google.golang.org/grpc"
)

const (
	localhost = "localhost"
	port      = "50051"
)

type server struct {
	pb.UnimplementedGreeterServer
}

func TestSayHello(t *testing.T) {
	go startServer(t)

	client, err := client.NewClient(localhost, port)
	assert.NoError(t, err)
	defer client.Shutdown()

	ctx := context.Background()
	s, err := client.SayHello(ctx)
	assert.NoError(t, err)
	assert.Equal(t, "Hello hello", s)
}

func (s *server) SayHello(ctx context.Context, in *pb.HelloRequest) (*pb.HelloReply, error) {
	log.Printf("Received: %v", in.GetName())
	return &pb.HelloReply{Message: "Hello " + in.GetName()}, nil
}

func startServer(t *testing.T) {
	t.Helper()
	flag.Parse()
	lis, err := net.Listen("tcp", fmt.Sprintf(":%s", port))
	assert.NoError(t, err)
	s := grpc.NewServer()
	pb.RegisterGreeterServer(s, &server{})
	log.Printf("server listening at %v", lis.Addr())
	err = s.Serve(lis)
	assert.NoError(t, err)
}
