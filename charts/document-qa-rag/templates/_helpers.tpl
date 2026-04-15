{{- define "document-qa-rag.name" -}}
{{- default .Chart.Name .Values.nameOverride | trunc 63 | trimSuffix "-" -}}
{{- end -}}
{{- define "document-qa-rag.fullname" -}}
{{- printf "%s" (include "document-qa-rag.name" .) | trunc 63 | trimSuffix "-" -}}
{{- end -}}
{{- define "document-qa-rag.serviceAccountName" -}}
{{- if .Values.serviceAccount.create -}}
{{- default (include "document-qa-rag.fullname" .) .Values.serviceAccount.name -}}
{{- else -}}
{{- default "default" .Values.serviceAccount.name -}}
{{- end -}}
{{- end -}}
