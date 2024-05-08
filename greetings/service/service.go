package service

import (
	"context"
	"fmt"
	"log/slog"
	"net/http"
)

const (
	redisSetPath   = "/redis/set"
	redisGetPath   = "/redis/get"
	key            = "testkey"
	value          = "testvalue"
	helloWorldPath = "/hello"
)

type (
	kvdb interface {
		Set(context.Context, string, any) error
		Get(context.Context, string) (string, error)
	}

	helloer interface {
		SayHello(ctx context.Context) (string, error)
	}

	serviceImpl struct {
		kvdb    kvdb
		port    string
		server  *http.Server
		helloer helloer
	}
)

func NewService(port string, kvdb kvdb, helloer helloer) *serviceImpl {
	return &serviceImpl{
		kvdb: kvdb,
		port: port,
		server: &http.Server{
			Addr: fmt.Sprintf(":%s", port),
		},
		helloer: helloer,
	}
}

func (s *serviceImpl) Start() {
	http.HandleFunc(helloWorldPath, s.helloWorldHandler)
	http.HandleFunc(redisSetPath, s.handleRedisSet)
	http.HandleFunc(redisGetPath, s.handleRedisGet)
	go func() {
		slog.Debug("HTTP Server started", "port", s.port)
		err := s.server.ListenAndServe()
		if err != nil {
			slog.Error(fmt.Sprintf("Error starting server: %v", err))
		}
	}()
}

func (s *serviceImpl) helloWorldHandler(w http.ResponseWriter, r *http.Request) {
	slog.Debug("Got request", "path", helloWorldPath)
	resp, err := s.helloer.SayHello(context.Background())
	if err != nil {
		fmt.Fprintf(w, "%s NOK, err:%v\n", helloWorldPath, err)
		return
	}
	fmt.Fprintf(w, "%s Response:%s\n", helloWorldPath, resp)
}

func (s *serviceImpl) handleRedisSet(w http.ResponseWriter, r *http.Request) {
	slog.Debug("Got request", "path", redisSetPath)
	err := s.kvdb.Set(context.Background(), key, value)
	if err != nil {
		slog.Error(fmt.Sprintf("Failed to handle request on path %s, err: %v", redisSetPath, err))
		w.WriteHeader(http.StatusBadRequest)
		fmt.Fprintf(w, "%s NOK, err:%v\n", redisSetPath, err)
		return
	}
	fmt.Fprintf(w, "%s OK\n", redisSetPath)
}

func (s *serviceImpl) handleRedisGet(w http.ResponseWriter, r *http.Request) {
	slog.Debug("Got request", "path", redisGetPath)
	val, err := s.kvdb.Get(context.Background(), key)
	if err != nil {
		slog.Error(fmt.Sprintf("Failed to handle request on path %s, err: %v", redisGetPath, err))
		w.WriteHeader(http.StatusBadRequest)
		fmt.Fprintf(w, "%s NOK, err:%v\n", redisGetPath, err)
		return
	}
	fmt.Fprintf(w, "%s OK val:%s\n", redisGetPath, val)
}
