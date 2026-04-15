output "document_bucket_name" {
  value = aws_s3_bucket.documents.bucket
}

output "database_endpoint" {
  value = aws_db_instance.rag.address
}

output "helm_release_name" {
  value = helm_release.document_qa_rag.name
}

output "kubernetes_namespace" {
  value = helm_release.document_qa_rag.namespace
}
