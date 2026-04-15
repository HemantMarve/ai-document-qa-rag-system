variable "aws_region" {
  type    = string
  default = "us-east-1"
}

variable "name_prefix" {
  type    = string
  default = "document-qa-rag"
}

variable "vpc_id" {
  type = string
}

variable "database_subnet_ids" {
  type = list(string)
}

variable "database_allowed_cidrs" {
  type    = list(string)
  default = []
}

variable "document_bucket_name" {
  type = string
}

variable "postgres_engine_version" {
  type    = string
  default = "16.3"
}

variable "postgres_instance_class" {
  type    = string
  default = "db.t4g.medium"
}

variable "postgres_allocated_storage" {
  type    = number
  default = 50
}

variable "postgres_database_name" {
  type    = string
  default = "ragdb"
}

variable "postgres_username" {
  type    = string
  default = "rag"
}

variable "db_password" {
  type      = string
  sensitive = true
}

variable "multi_az" {
  type    = bool
  default = false
}

variable "backup_retention_days" {
  type    = number
  default = 7
}

variable "skip_final_snapshot" {
  type    = bool
  default = false
}

variable "kubeconfig_path" {
  type    = string
  default = "~/.kube/config"
}

variable "kubeconfig_context" {
  type    = string
  default = null
}

variable "kubernetes_namespace" {
  type    = string
  default = "document-qa-rag"
}

variable "image_repository" {
  type    = string
  default = "ghcr.io/hemantmarve/document-qa-rag-system"
}

variable "image_tag" {
  type = string
}

variable "oidc_issuer_uri" {
  type = string
}

variable "kafka_bootstrap_servers" {
  type = string
}

variable "embedding_provider" {
  type    = string
  default = "openai"
}

variable "generation_provider" {
  type    = string
  default = "openai"
}

variable "openai_api_key" {
  type      = string
  sensitive = true
  default   = ""
}
