terraform {
  required_version = ">= 1.6.0"

  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
    helm = {
      source  = "hashicorp/helm"
      version = "~> 2.13"
    }
  }
}

provider "aws" {
  region = var.aws_region
}

provider "helm" {
  kubernetes {
    config_path    = var.kubeconfig_path
    config_context = var.kubeconfig_context
  }
}

resource "aws_s3_bucket" "documents" {
  bucket = var.document_bucket_name
}

resource "aws_s3_bucket_public_access_block" "documents" {
  bucket                  = aws_s3_bucket.documents.id
  block_public_acls       = true
  block_public_policy     = true
  ignore_public_acls      = true
  restrict_public_buckets = true
}

resource "aws_s3_bucket_server_side_encryption_configuration" "documents" {
  bucket = aws_s3_bucket.documents.id

  rule {
    apply_server_side_encryption_by_default {
      sse_algorithm = "AES256"
    }
  }
}

resource "aws_db_subnet_group" "rag" {
  name       = "${var.name_prefix}-db"
  subnet_ids = var.database_subnet_ids
}

resource "aws_security_group" "database" {
  name        = "${var.name_prefix}-database"
  description = "PostgreSQL access for Document Q&A RAG"
  vpc_id      = var.vpc_id
}

resource "aws_security_group_rule" "database_ingress" {
  type              = "ingress"
  from_port         = 5432
  to_port           = 5432
  protocol          = "tcp"
  security_group_id = aws_security_group.database.id
  cidr_blocks       = var.database_allowed_cidrs
}

resource "aws_security_group_rule" "database_egress" {
  type              = "egress"
  from_port         = 0
  to_port           = 0
  protocol          = "-1"
  security_group_id = aws_security_group.database.id
  cidr_blocks       = ["0.0.0.0/0"]
}

resource "aws_db_instance" "rag" {
  identifier             = "${var.name_prefix}-postgres"
  engine                 = "postgres"
  engine_version         = var.postgres_engine_version
  instance_class         = var.postgres_instance_class
  allocated_storage      = var.postgres_allocated_storage
  db_name                = var.postgres_database_name
  username               = var.postgres_username
  password               = var.db_password
  db_subnet_group_name   = aws_db_subnet_group.rag.name
  vpc_security_group_ids = [aws_security_group.database.id]
  multi_az               = var.multi_az
  storage_encrypted      = true
  backup_retention_period = var.backup_retention_days
  skip_final_snapshot    = var.skip_final_snapshot
}

resource "helm_release" "document_qa_rag" {
  name             = "document-qa-rag"
  chart            = "../../../charts/document-qa-rag"
  namespace        = var.kubernetes_namespace
  create_namespace = true

  set {
    name  = "image.repository"
    value = var.image_repository
  }

  set {
    name  = "image.tag"
    value = var.image_tag
  }

  set {
    name  = "postgresql.enabled"
    value = "false"
  }

  set {
    name  = "app.datasourceUrl"
    value = "jdbc:postgresql://${aws_db_instance.rag.address}:5432/${var.postgres_database_name}"
  }

  set {
    name  = "app.datasourceUsername"
    value = var.postgres_username
  }

  set_sensitive {
    name  = "app.datasourcePassword"
    value = var.db_password
  }

  set {
    name  = "app.authMode"
    value = "oidc"
  }

  set {
    name  = "app.oidcIssuerUri"
    value = var.oidc_issuer_uri
  }

  set {
    name  = "app.kafkaBootstrapServers"
    value = var.kafka_bootstrap_servers
  }

  set {
    name  = "app.storageProvider"
    value = "s3"
  }

  set {
    name  = "app.storageS3Bucket"
    value = aws_s3_bucket.documents.bucket
  }

  set {
    name  = "app.storageS3Region"
    value = var.aws_region
  }

  set {
    name  = "app.embeddingProvider"
    value = var.embedding_provider
  }

  set {
    name  = "app.generationProvider"
    value = var.generation_provider
  }

  set_sensitive {
    name  = "app.openAiApiKey"
    value = var.openai_api_key
  }
}
