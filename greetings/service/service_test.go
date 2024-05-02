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

func (m *mockKVDB) Set(ctx context.Context, key string, val any) error {
	args := m.Called(ctx, key, val)
	return args.Error(0)
}

func (m *mockKVDB) Get(ctx context.Context, key string) (string, error) {
	args := m.Called(ctx, key)
	return args.String(0), args.Error(1)
}

func TestService(t *testing.T) {
	kvdbMock := new(mockKVDB)
	service := NewService(port, kvdbMock)
	service.Start()
}

func TestHandleHelloWorld(t *testing.T) {
	req, err := http.NewRequest(http.MethodGet, redisSetPath, nil)
	assert.NoError(t, err)

	rr := httptest.NewRecorder()
	handler := http.HandlerFunc(helloWorldHandler)

	handler.ServeHTTP(rr, req)

	assert.Equal(t, http.StatusOK, rr.Code)
	assert.Contains(t, rr.Body.String(), "Hello, World!")
}

func TestHandleRedisSet(t *testing.T) {
	kvdbMock := new(mockKVDB)
	service := NewService(port, kvdbMock)

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
	service := NewService(port, kvdbMock)

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
	service := NewService(port, kvdbMock)

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
	service := NewService(port, kvdbMock)

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