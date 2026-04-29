# Secrets Management By Service

This file lists GitHub Actions secrets by service for this workspace.

Add secrets in:
Settings -> Secrets and variables -> Actions

## Service: order-platform

Workflows:
- order-platform/.github/workflows/deploy-cloud-run.yml
- order-platform/.github/workflows/ci.yml

### Required Secrets (Deployment)

1. GCP_SA_KEY
- Used by: Google auth step
- Purpose: Service account JSON for deploying to Cloud Run and pushing images

2. GCP_PROJECT_ID
- Used by: gcloud setup, Artifact Registry, Cloud Run deploy
- Purpose: Target GCP project

3. SPRING_DATASOURCE_URL
- Used by: Cloud Run env generation
- Purpose: MySQL JDBC connection string

4. SPRING_DATASOURCE_USERNAME
- Used by: Cloud Run env generation
- Purpose: MySQL username

5. SPRING_DATASOURCE_PASSWORD
- Used by: Cloud Run env generation
- Purpose: MySQL password

6. SPRING_DATA_MONGODB_URI
- Used by: Cloud Run env generation
- Purpose: Mongo cache connection string
- Note: Keep required because Mongo cache is enabled for order-platform

### Optional Secrets (Deployment)

1. GCP_REGION
- Default if missing: us-central1
- Purpose: Cloud Run and Artifact Registry region

2. CLOUD_RUN_SERVICE
- Default if missing: order-platform
- Purpose: Cloud Run service name override

### Built-in GitHub Secret Used

1. GITHUB_TOKEN
- Used by: GitHub Actions automatically
- Note: Not manually created by you

## Service: school

Workflow:
- school/.github/workflows/ci.yml

### Required Secrets

No custom repository secret is strictly required for current build/test flow.

### Built-in GitHub Secret Used

1. GITHUB_TOKEN
- Used by: GHCR login in docker-build job
- Note: Automatically provided by GitHub Actions

### Optional Secrets

1. CODECOV_TOKEN
- Used by: codecov/codecov-action (if your repository/setup requires token)
- Note: Not explicitly referenced in current workflow file, but may be needed depending on Codecov settings

## Copy/Paste Secret Checklist

Create these in GitHub repository secrets for deployment:
- GCP_SA_KEY
- GCP_PROJECT_ID
- SPRING_DATASOURCE_URL
- SPRING_DATASOURCE_USERNAME
- SPRING_DATASOURCE_PASSWORD
- SPRING_DATA_MONGODB_URI

Optional but recommended:
- GCP_REGION
- CLOUD_RUN_SERVICE
- CODECOV_TOKEN

## Verification

1. Run order-platform deploy workflow manually.
2. Confirm Cloud Run env generation passes.
3. Confirm deployment health check is 200.
4. Run school CI and confirm build/test/docker jobs succeed.
