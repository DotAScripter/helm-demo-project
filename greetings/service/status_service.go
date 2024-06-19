package service

import (
	"context"

	"github.com/DotAScripter/helm-demo-project/greetings/proto/status"
)

type StatusServer struct {
	status.UnimplementedStatusServer
}

func (s *StatusServer) CheckStatus(ctx context.Context, req *status.StatusCheckRequest) (*status.StatusCheckResponse, error) {
	response := &status.StatusCheckResponse{
		Status: status.StatusCheckResponse_OK,
	}
	return response, nil
}
