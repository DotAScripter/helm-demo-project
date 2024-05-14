{{/* Pod name */}}
{{- define "jpod.name" -}}
    {{- .Chart.Name | trunc 63 | trimSuffix "-" -}}
{{- end -}}

{{/* Container name */}}
{{- define "jpod.container-name" -}}
    {{- printf "jpod" -}}
{{- end -}}

{{/* Service name */}}
{{- define "jpod.service-name" -}}
    {{- printf "%s-service" .Chart.Name -}}
{{- end -}}