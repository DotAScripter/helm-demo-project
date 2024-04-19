package kvdb

import (
	"context"
	"fmt"
	"log/slog"

	"github.com/redis/go-redis/v9"
)

type kvdbImpl struct {
	client *redis.Client
}

func NewKVDB(host, port string) *kvdbImpl {
	return &kvdbImpl{
		client: redis.NewClient(&redis.Options{
			Addr:     fmt.Sprintf("%s:%s", host, port),
			Password: "", // no password set
			DB:       0,  // use default DB
		}),
	}
}

func (db *kvdbImpl) Set(ctx context.Context, key string, val any) error {
	err := db.client.Set(ctx, key, val, 0).Err()
	if err != nil {
		slog.Warn(fmt.Sprintf("Failed to Set key:%s, err:%v", key, err))
		return err
	}
	return nil
}

func (db *kvdbImpl) Get(ctx context.Context, key string) (string, error) {
	val, err := db.client.Get(ctx, key).Result()
	if err != nil {
		slog.Warn(fmt.Sprintf("Failed to Get key:%s, err:%v", key, err))
		return "", err
	}
	return val, nil
}
