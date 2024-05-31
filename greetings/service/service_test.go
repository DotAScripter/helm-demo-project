package service

import (
	"context"
	"errors"
	"fmt"
	"net/http"
	"net/http/httptest"
	"testing"

	"github.com/stretchr/testify/assert"
	"github.com/stretchr/testify/mock"
)

const (
	port = "8080"
)

type mockKVDB struct {
	mock.Mock
}

type mockHelloer struct {
	mock.Mock
}

func (m *mockKVDB) Set(ctx context.Context, key string, val any) error {
	args := m.Called(ctx, key, val)
	return args.Error(0)
}

func (m *mockKVDB) Get(ctx context.Context, key string) (string, error) {
	args := m.Called(ctx, key)
	return args.String(0), args.Error(1)
}

func (m *mockHelloer) SayHello(ctx context.Context) (string, error) {
	args := m.Called(ctx)
	return args.String(0), args.Error(1)
}

func TestService(t *testing.T) {
	kvdbMock := new(mockKVDB)
	cppodClientMock := new(mockHelloer)
	jpodClientMock := new(mockHelloer)
	pypodClientMock := new(mockHelloer)
	service := NewService(port, kvdbMock, cppodClientMock, jpodClientMock, pypodClientMock)
	service.Start()
}

func TestHandleHelloCppod(t *testing.T) {
	kvdbMock := new(mockKVDB)
	cppodClientMock := new(mockHelloer)
	jpodClientMock := new(mockHelloer)
	pypodClientMock := new(mockHelloer)
	service := NewService(port, kvdbMock, cppodClientMock, jpodClientMock, pypodClientMock)

	response := "Hello, World!"

	req, err := http.NewRequest(http.MethodGet, helloCppod, nil)
	assert.NoError(t, err)

	rr := httptest.NewRecorder()
	handler := http.HandlerFunc(service.helloCppod)

	cppodClientMock.On("SayHello", mock.Anything).Return(response, nil)

	handler.ServeHTTP(rr, req)

	assert.Equal(t, http.StatusOK, rr.Code)
	assert.Contains(t, rr.Body.String(), fmt.Sprintf("%s Response:%s", helloCppod, response))
}

func TestHandleHelloJpod(t *testing.T) {
	kvdbMock := new(mockKVDB)
	cppodClientMock := new(mockHelloer)
	jpodClientMock := new(mockHelloer)
	pypodClientMock := new(mockHelloer)
	service := NewService(port, kvdbMock, cppodClientMock, jpodClientMock, pypodClientMock)

	response := "Hello, World!"

	req, err := http.NewRequest(http.MethodGet, helloJpod, nil)
	assert.NoError(t, err)

	rr := httptest.NewRecorder()
	handler := http.HandlerFunc(service.helloJpod)

	jpodClientMock.On("SayHello", mock.Anything).Return(response, nil)

	handler.ServeHTTP(rr, req)

	assert.Equal(t, http.StatusOK, rr.Code)
	assert.Contains(t, rr.Body.String(), fmt.Sprintf("%s Response:%s", helloJpod, response))
}

func TestHandleHelloPypod(t *testing.T) {
	kvdbMock := new(mockKVDB)
	cppodClientMock := new(mockHelloer)
	jpodClientMock := new(mockHelloer)
	pypodClientMock := new(mockHelloer)
	service := NewService(port, kvdbMock, cppodClientMock, jpodClientMock, pypodClientMock)

	response := "Hello, World!"

	req, err := http.NewRequest(http.MethodGet, helloPypod, nil)
	assert.NoError(t, err)

	rr := httptest.NewRecorder()
	handler := http.HandlerFunc(service.helloPypod)

	pypodClientMock.On("SayHello", mock.Anything).Return(response, nil)

	handler.ServeHTTP(rr, req)

	assert.Equal(t, http.StatusOK, rr.Code)
	assert.Contains(t, rr.Body.String(), fmt.Sprintf("%s Response:%s", helloPypod, response))
}

func TestHandleRedisSet(t *testing.T) {
	kvdbMock := new(mockKVDB)
	cppodClientMock := new(mockHelloer)
	jpodClientMock := new(mockHelloer)
	pypodClientMock := new(mockHelloer)
	service := NewService(port, kvdbMock, cppodClientMock, jpodClientMock, pypodClientMock)

	req, err := http.NewRequest(http.MethodGet, redisSetPath, nil)
	assert.NoError(t, err)

	rr := httptest.NewRecorder()
	handler := http.HandlerFunc(service.handleRedisSet)

	kvdbMock.On("Set", mock.Anything, key, value).Return(nil)

	handler.ServeHTTP(rr, req)

	assert.Equal(t, http.StatusOK, rr.Code)
	assert.Contains(t, rr.Body.String(), fmt.Sprintf("%s OK", redisSetPath))
}

func TestHandleRedisGet(t *testing.T) {
	kvdbMock := new(mockKVDB)
	cppodClientMock := new(mockHelloer)
	jpodClientMock := new(mockHelloer)
	pypodClientMock := new(mockHelloer)
	service := NewService(port, kvdbMock, cppodClientMock, jpodClientMock, pypodClientMock)

	req, err := http.NewRequest(http.MethodGet, redisGetPath, nil)
	assert.NoError(t, err)

	rr := httptest.NewRecorder()
	handler := http.HandlerFunc(service.handleRedisGet)

	kvdbMock.On("Get", mock.Anything, key).Return(value, nil)

	handler.ServeHTTP(rr, req)

	assert.Equal(t, http.StatusOK, rr.Code)
	assert.Contains(t, rr.Body.String(), fmt.Sprintf("%s OK val:%s", redisGetPath, value))
}

func TestHandleRedisSetError(t *testing.T) {
	kvdbMock := new(mockKVDB)
	cppodClientMock := new(mockHelloer)
	jpodClientMock := new(mockHelloer)
	pypodClientMock := new(mockHelloer)
	service := NewService(port, kvdbMock, cppodClientMock, jpodClientMock, pypodClientMock)

	req, err := http.NewRequest(http.MethodGet, redisSetPath, nil)
	assert.NoError(t, err)

	rr := httptest.NewRecorder()
	handler := http.HandlerFunc(service.handleRedisSet)

	expectedErr := errors.New("redis failure")

	kvdbMock.On("Set", mock.Anything, key, value).Return(expectedErr)

	handler.ServeHTTP(rr, req)

	assert.Equal(t, http.StatusBadRequest, rr.Code)
	assert.Contains(t, rr.Body.String(), fmt.Sprintf("%s NOK, err:%v", redisSetPath, expectedErr))
}

func TestHandleRedisGetError(t *testing.T) {
	kvdbMock := new(mockKVDB)
	cppodClientMock := new(mockHelloer)
	jpodClientMock := new(mockHelloer)
	pypodClientMock := new(mockHelloer)
	service := NewService(port, kvdbMock, cppodClientMock, jpodClientMock, pypodClientMock)

	req, err := http.NewRequest(http.MethodGet, redisGetPath, nil)
	assert.NoError(t, err)

	rr := httptest.NewRecorder()
	handler := http.HandlerFunc(service.handleRedisGet)

	expectedErr := errors.New("redis failure")

	kvdbMock.On("Get", mock.Anything, key).Return("", expectedErr)

	handler.ServeHTTP(rr, req)

	assert.Equal(t, http.StatusBadRequest, rr.Code)
	assert.Contains(t, rr.Body.String(), fmt.Sprintf("%s NOK, err:%v", redisGetPath, expectedErr))
}
