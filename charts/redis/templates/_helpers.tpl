{{/* Pod name */}}
{{- define "redis.name" -}}
    {{- .Chart.Name | trunc 63 | trimSuffix "-" -}}
{{- end -}}

{{/* Container name */}}
{{- define "redis.container-name" -}}
    {{- printf "redis" -}}
{{- end -}}

{{/* Service name */}}
{{- define "redis.service-name" -}}
    {{- printf "%s-service" .Chart.Name -}}
{{- end -}}
