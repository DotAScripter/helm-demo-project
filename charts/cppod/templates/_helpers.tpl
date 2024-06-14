{{/* Pod name */}}
{{- define "cpp.name" -}}
    {{- .Chart.Name | trunc 63 | trimSuffix "-" -}}
{{- end -}}

{{/* Container name */}}
{{- define "cpp.container-name" -}}
    {{- printf "cppapp" -}}
{{- end -}}

{{/* Greeter service name */}}
{{- define "cpp.greeter-service-name" -}}
    {{- printf "%s-greeter-service" .Chart.Name -}}
{{- end -}}

{{/* Status service name */}}
{{- define "cpp.status-service-name" -}}
    {{- printf "%s-status-service" .Chart.Name -}}
{{- end -}}
