{{/* Pod name */}}
{{- define "greetings.name" -}}
    {{- .Chart.Name | trunc 63 | trimSuffix "-" -}}
{{- end -}}

{{/* Container name */}}
{{- define "greetings.container-name" -}}
    {{- printf "greetings" -}}
{{- end -}}

{{/* Service name */}}
{{- define "greetings.service-name" -}}
    {{- printf "%s-service" .Chart.Name -}}
{{- end -}}