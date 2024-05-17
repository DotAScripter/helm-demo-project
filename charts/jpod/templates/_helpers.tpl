{{/* Pod name */}}
{{- define "jpod.name" -}}
    {{- .Chart.Name | trunc 63 | trimSuffix "-" -}}
{{- end -}}

{{/* Container name */}}
{{- define "jpod.container-name" -}}
    {{- printf "jpod" -}}
{{- end -}}

{{/* HTTP Service name */}}
{{- define "jpod.http-service-name" -}}
    {{- printf "%s-http-service" .Chart.Name -}}
{{- end -}}

{{/* gRPC service name */}}
{{- define "jpod.grpc-service-name" -}}
    {{- printf "%s-grpc-service" .Chart.Name -}}
{{- end -}}
