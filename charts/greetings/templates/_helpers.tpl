{{/* _helpers.tpl or deployment.yaml */}}
{{- define "greetings.name" -}}
{{- printf "%s-%s" .Release.Name "greetings" -}}
{{- end -}}