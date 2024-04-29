package client

import (
	"context"
	"fmt"
	"log/slog"

	pb "github.com/DotAScripter/helm-demo-project/greetings/proto"
	"google.golang.org/grpc"
	"google.golang.org/grpc/credentials/insecure"
)

const (
	name = "hello"
)

type clientImpl struct {
	client pb.GreeterClient
	conn   *grpc.ClientConn
}

func NewClient(host, port string) (*clientImpl, error) {
	// Set up a connection to the server.
	conn, err := grpc.Dial(fmt.Sprintf("%s:%s", host, port), grpc.WithTransportCredentials(insecure.NewCredentials()))
	if err != nil {
		slog.Error(fmt.Sprintf("Error dialing server: %v", err))
	}
	return &clientImpl{
		client: pb.NewGreeterClient(conn),
		conn:   conn,
	}, nil
}

func (c *clientImpl) SayHello(ctx context.Context) (string, error) {
	r, err := c.client.SayHello(ctx, &pb.HelloRequest{Name: name})
	if err != nil {
		slog.Error(fmt.Sprintf("could not greet: %v", err))
		return "", err
	}

	return r.GetMessage(), nil
}

func (c *clientImpl) Shutdown() {
	c.conn.Close()
}
