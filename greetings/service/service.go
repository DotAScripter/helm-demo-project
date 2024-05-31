package service

import (
	"context"
	"fmt"
	"log/slog"
	"net/http"
)

const (
	redisSetPath = "/redis/set"
	redisGetPath = "/redis/get"
	key          = "testkey"
	value        = "testvalue"
	helloCppod   = "/hello/cppod"
	helloJpod    = "/hello/jpod"
	helloPypod   = "/hello/pypod"
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
		kvdb         kvdb
		port         string
		server       *http.Server
		cppodHelloer helloer
		jpodHelloer  helloer
		pypodHelloer helloer
	}
)

func NewService(port string, kvdb kvdb, cppodHelloer, jpodHelloer, pypodHelloer helloer) *serviceImpl {
	return &serviceImpl{
		kvdb: kvdb,
		port: port,
		server: &http.Server{
			Addr: fmt.Sprintf(":%s", port),
		},
		cppodHelloer: cppodHelloer,
		jpodHelloer:  jpodHelloer,
		pypodHelloer: pypodHelloer,
	}
}

func (s *serviceImpl) Start() {
	http.HandleFunc(helloCppod, s.helloCppod)
	http.HandleFunc(helloJpod, s.helloJpod)
	http.HandleFunc(helloPypod, s.helloPypod)
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

func (s *serviceImpl) helloCppod(w http.ResponseWriter, r *http.Request) {
	slog.Debug("Got request", "path", helloCppod)
	resp, err := s.cppodHelloer.SayHello(context.Background())
	if err != nil {
		fmt.Fprintf(w, "%s NOK, err:%v\n", helloCppod, err)
		return
	}
	fmt.Fprintf(w, "%s Response:%s\n", helloCppod, resp)
}

func (s *serviceImpl) helloJpod(w http.ResponseWriter, r *http.Request) {
	slog.Debug("Got request", "path", helloJpod)
	resp, err := s.jpodHelloer.SayHello(context.Background())
	if err != nil {
		fmt.Fprintf(w, "%s NOK, err:%v\n", helloJpod, err)
		return
	}
	fmt.Fprintf(w, "%s Response:%s\n", helloJpod, resp)
}

func (s *serviceImpl) helloPypod(w http.ResponseWriter, r *http.Request) {
	slog.Debug("Got request", "path", helloPypod)
	resp, err := s.pypodHelloer.SayHello(context.Background())
	if err != nil {
		fmt.Fprintf(w, "%s NOK, err:%v\n", helloPypod, err)
		return
	}
	fmt.Fprintf(w, "%s Response:%s\n", helloPypod, resp)
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
