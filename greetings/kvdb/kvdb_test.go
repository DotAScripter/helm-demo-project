package kvdb_test

import (
	"context"
	"testing"

	"github.com/DotAScripter/helm-demo-project/greetings/kvdb"
	"github.com/alicebob/miniredis"
	"github.com/redis/go-redis/v9"
	"github.com/stretchr/testify/assert"
)

const (
	localhost = "localhost"
	key       = "test_key"
	value     = "test_val"
)

func TestSetAndGet(t *testing.T) {
	mr := getMiniRedis(t)
	defer mr.Close()

	client := kvdb.NewKVDB(localhost, mr.Port())
	defer client.Close()

	ctx := context.Background()

	err := client.Set(ctx, key, value)
	assert.NoError(t, err)

	val, err := client.Get(ctx, key)
	assert.NoError(t, err)
	assert.Equal(t, value, val)
}

func TestGetError(t *testing.T) {
	mr := getMiniRedis(t)
	defer mr.Close()

	client := kvdb.NewKVDB(localhost, mr.Port())
	defer client.Close()

	ctx := context.Background()

	val, err := client.Get(ctx, key)
	assert.ErrorIs(t, err, redis.Nil)
	assert.Empty(t, val)
}

func TestSetError(t *testing.T) {
	mr := getMiniRedis(t)
	defer mr.Close()
	mr.RequireAuth("pw")

	client := kvdb.NewKVDB(localhost, mr.Port())
	defer client.Close()

	ctx := context.Background()

	err := client.Set(ctx, key, value)
	assert.Error(t, err)
}

func getMiniRedis(t *testing.T) *miniredis.Miniredis {
	t.Helper()
	mr, err := miniredis.Run()
	assert.NoErrorf(t, err, "Failed to start miniredis: %v", err)
	return mr
}
