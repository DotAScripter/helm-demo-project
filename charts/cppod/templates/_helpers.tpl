{{/* Pod name */}}
{{- define "cpp.name" -}}
    {{- .Chart.Name | trunc 63 | trimSuffix "-" -}}
{{- end -}}

{{/* Container name */}}
{{- define "cpp.container-name" -}}
    {{- printf "cppapp" -}}
{{- end -}}

{{/* Service name */}}
{{- define "cpp.service-name" -}}
    {{- printf "%s-service" .Chart.Name -}}
{{- end -}}
