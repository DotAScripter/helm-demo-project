{{/* _helpers.tpl or deployment.yaml */}}
{{- define "redis.name" -}}
{{- printf "%s-%s" .Release.Name "redis" -}}
{{- end -}}