# AWS Terraform Starter

This starter provisions the production-adjacent pieces that should not live inside the application Helm chart:

- S3 bucket for original document files.
- PostgreSQL RDS instance for metadata, chunks, and pgvector search.
- Helm release of the application into an existing Kubernetes cluster.

It assumes you already have:

- An AWS account and credentials configured.
- A Kubernetes cluster reachable from your local kubeconfig or CI runner.
- A Kafka bootstrap endpoint from MSK, Redpanda, Confluent Cloud, or another provider.
- An OIDC issuer for production auth.

## Usage

```bash
terraform init
terraform plan \
  -var='db_password=replace-with-strong-password' \
  -var='oidc_issuer_uri=https://issuer.example.com/' \
  -var='kafka_bootstrap_servers=broker.example.com:9092' \
  -var='openai_api_key=sk-...'
terraform apply
```

Use a remote backend such as S3 + DynamoDB locking before team usage.

## Notes

- The RDS instance uses standard PostgreSQL. Confirm your selected engine version supports the `vector` extension in your AWS region before production use.
- For high availability, set `multi_az=true`, tune backup retention, and use private subnets.
- For production secrets, prefer External Secrets Operator or AWS Secrets Manager instead of passing secrets directly into Helm values.
