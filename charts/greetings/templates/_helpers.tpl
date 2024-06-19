{{/* Pod name */}}
{{- define "greetings.name" -}}
    {{- .Chart.Name | trunc 63 | trimSuffix "-" -}}
{{- end -}}

{{/* Container name */}}
{{- define "greetings.container-name" -}}
    {{- printf "greetings" -}}
{{- end -}}

{{/* Status service name */}}
{{- define "greetings.status-service-name" -}}
    {{- printf "%s-status-service" .Chart.Name -}}
{{- end -}}

{{/* Service name */}}
{{- define "greetings.service-name" -}}
    {{- printf "%s-service" .Chart.Name -}}
{{- end -}}