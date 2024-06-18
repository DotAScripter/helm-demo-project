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

{{/* Status service name */}}
{{- define "jpod.status-service-name" -}}
    {{- printf "%s-status-service" .Chart.Name -}}
{{- end -}}

{{/* Greeter service name */}}
{{- define "jpod.greeter-service-name" -}}
    {{- printf "%s-greeter-service" .Chart.Name -}}
{{- end -}}