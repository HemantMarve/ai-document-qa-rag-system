{{- define "document-qa-rag.name" -}}
{{- default .Chart.Name .Values.nameOverride | trunc 63 | trimSuffix "-" -}}
{{- end -}}
{{- define "document-qa-rag.fullname" -}}
{{- printf "%s" (include "document-qa-rag.name" .) | trunc 63 | trimSuffix "-" -}}
{{- end -}}
