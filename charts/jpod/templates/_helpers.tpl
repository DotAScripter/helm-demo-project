{{/* _helpers.tpl or deployment.yaml */}}
{{- define "jpod.name" -}}
{{- printf "%s-%s" .Release.Name "jpod" -}}
{{- end -}}