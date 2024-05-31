{{/* Pod name */}}
{{- define "py.name" -}}
    {{- .Chart.Name | trunc 63 | trimSuffix "-" -}}
{{- end -}}

{{/* Container name */}}
{{- define "py.container-name" -}}
    {{- printf "app" -}}
{{- end -}}

{{/* Service name */}}
{{- define "py.service-name" -}}
    {{- printf "%s-service" .Chart.Name -}}
{{- end -}}

{{/* Status service name */}}
{{- define "status.service-name" -}}
    {{- printf "%s-status-service" .Chart.Name -}}
{{- end -}}

{{/* Greeter service name */}}
{{- define "greeter.service-name" -}}
    {{- printf "%s-greeter-service" .Chart.Name -}}
{{- end -}}
