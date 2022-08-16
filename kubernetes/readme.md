## Vault instructions

Enable Kubernetes auth:
```shell
vault auth enable kubernetes
```

Configure Kubernetes auth:
```shell
vault write auth/kubernetes/config \
    kubernetes_host="https://$KUBERNETES_PORT_443_TCP_ADDR:443" \
    token_reviewer_jwt="$(cat /var/run/secrets/kubernetes.io/serviceaccount/token)" \
    kubernetes_ca_cert=@/var/run/secrets/kubernetes.io/serviceaccount/ca.crt \
    issuer="https://kubernetes.default.svc.cluster.local"
```

Create read policy for database engine:
```shell
vault policy write internal-app - <<EOF
path "database/creds/default" {
  capabilities = ["read"]
}
EOF
```

Create auth role for the target namespace `demo-apps:
```shell
vault write auth/kubernetes/role/internal-app \
    bound_service_account_names=internal-app \
    bound_service_account_namespaces=demo-apps \
    policies=internal-app \
    ttl=24h
```

Enable database engine:
```shell
vault secrets enable database
```

Configure connection with PostgreSQL - target namespace `demo-apps`:
```shell
vault write database/config/postgres \
    plugin_name=postgresql-database-plugin \
    allowed_roles="default" \
    connection_url="postgresql://{{username}}:{{password}}@postgresql.demo-apps:5432?sslmode=disable" \
    username="postgres" \
    password="admin123"
```

Configure user/pass creation:
```shell
vault write database/roles/default db_name=postgres \
    creation_statements="CREATE ROLE \"{{name}}\" WITH LOGIN PASSWORD '{{password}}' VALID UNTIL '{{expiration}}';GRANT SELECT, UPDATE, INSERT ON ALL TABLES IN SCHEMA public TO \"{{name}}\";GRANT USAGE,  SELECT ON ALL SEQUENCES IN SCHEMA public TO \"{{name}}\";" \
    default_ttl="1m" \
    max_ttl="60m"
```