{{/* _helpers.tpl or deployment.yaml */}}
{{- define "cppod.name" -}}
{{- printf "%s-%s" .Release.Name "cppod" -}}
{{- end -}}