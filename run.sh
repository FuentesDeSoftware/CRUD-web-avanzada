#!/usr/bin/env bash
# Levanta SQL Server (Docker), crea la base, compila y despliega en Tomcat 11.
#   ./run.sh         -> todo
#   ./run.sh stop    -> detiene Tomcat y el contenedor
set -euo pipefail

cd "$(dirname "$0")"

CONTAINER=sqlserver-empresa
SA_PASSWORD="${DB_PASSWORD:-HolaMundo#2026}"
CATALINA_HOME="${CATALINA_HOME:-$HOME/.local/share/tomcat11}"
APP=holamundoWeb

if [[ "${1:-}" == "stop" ]]; then
    "$CATALINA_HOME/bin/shutdown.sh" || true
    docker stop "$CONTAINER" || true
    exit 0
fi

# 1. SQL Server
if ! docker ps --format '{{.Names}}' | grep -qx "$CONTAINER"; then
    if docker ps -a --format '{{.Names}}' | grep -qx "$CONTAINER"; then
        echo "-> Iniciando contenedor $CONTAINER"
        docker start "$CONTAINER" >/dev/null
    else
        echo "-> Creando contenedor $CONTAINER (SQL Server 2022)"
        docker run -d --name "$CONTAINER" \
            -e ACCEPT_EULA=Y -e "MSSQL_SA_PASSWORD=$SA_PASSWORD" \
            -p 1433:1433 mcr.microsoft.com/mssql/server:2022-latest >/dev/null
    fi
fi

echo "-> Esperando a que SQL Server acepte conexiones..."
for i in $(seq 1 60); do
    if docker exec "$CONTAINER" /opt/mssql-tools18/bin/sqlcmd -C -S localhost -U sa -P "$SA_PASSWORD" \
        -Q "SELECT 1" >/dev/null 2>&1; then
        break
    fi
    [[ $i == 60 ]] && { echo "SQL Server no respondio"; docker logs --tail 20 "$CONTAINER"; exit 1; }
    sleep 2
done

echo "-> Creando base EmpresaDB"
docker exec -i "$CONTAINER" /opt/mssql-tools18/bin/sqlcmd -C -S localhost -U sa -P "$SA_PASSWORD" -b \
    < sql/EmpresaDB.sql

# 2. Compilar
echo "-> Compilando"
mvn -q package

# 3. Desplegar en Tomcat
echo "-> Desplegando en Tomcat ($CATALINA_HOME)"
rm -rf "$CATALINA_HOME/webapps/$APP" "$CATALINA_HOME/webapps/$APP.war"
cp "target/$APP-1.0-SNAPSHOT.war" "$CATALINA_HOME/webapps/$APP.war"
if ! curl -s -o /dev/null http://localhost:8080/; then
    DB_PASSWORD="$SA_PASSWORD" "$CATALINA_HOME/bin/startup.sh" >/dev/null
fi

echo "-> Listo: http://localhost:8080/$APP/"
echo "   Logs: $CATALINA_HOME/logs/catalina.out"
