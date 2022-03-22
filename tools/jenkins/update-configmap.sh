envValue=$1
APP_NAME=$2
OPENSHIFT_NAMESPACE=$3
APP_NAME_UPPER=${APP_NAME^^}

TZVALUE="America/Vancouver"
SOAM_KC_REALM_ID="master"

SOAM_KC_LOAD_USER_ADMIN=$(oc -n "$OPENSHIFT_NAMESPACE"-"$envValue" -o json get secret sso-admin-"${envValue}" | sed -n 's/.*"username": "\(.*\)"/\1/p' | base64 --decode)
SOAM_KC_LOAD_USER_PASS=$(oc -n "$OPENSHIFT_NAMESPACE"-"$envValue" -o json get secret sso-admin-"${envValue}" | sed -n 's/.*"password": "\(.*\)",/\1/p' | base64 --decode)
DB_JDBC_CONNECT_STRING=$(oc -n "$OPENSHIFT_NAMESPACE"-"$envValue" -o json get configmaps "${APP_NAME}"-"${envValue}"-setup-config | sed -n 's/.*"DB_JDBC_CONNECT_STRING": "\(.*\)",/\1/p')
DB_PWD=$(oc -n "$OPENSHIFT_NAMESPACE"-"$envValue" -o json get configmaps "${APP_NAME}"-"${envValue}"-setup-config | sed -n "s/.*\"DB_PWD_${APP_NAME_UPPER}\": \"\(.*\)\",/\1/p")
DB_USER=$(oc -n "$OPENSHIFT_NAMESPACE"-"$envValue" -o json get configmaps "${APP_NAME}"-"${envValue}"-setup-config | sed -n "s/.*\"DB_USER_${APP_NAME_UPPER}\": \"\(.*\)\",/\1/p")
SPLUNK_TOKEN=$(oc -n "$OPENSHIFT_NAMESPACE"-"$envValue" -o json get configmaps "${APP_NAME}"-"${envValue}"-setup-config | sed -n "s/.*\"SPLUNK_TOKEN_${APP_NAME_UPPER}\": \"\(.*\)\"/\1/p")
SOAM_KC=soam-$envValue.apps.silver.devops.gov.bc.ca
NATS_CLUSTER=educ_nats_cluster
NATS_URL="nats://nats.${OPENSHIFT_NAMESPACE}-${envValue}.svc.cluster.local:4222"

echo Fetching SOAM token
TKN=$(curl -s \
  -d "client_id=admin-cli" \
  -d "username=$SOAM_KC_LOAD_USER_ADMIN" \
  -d "password=$SOAM_KC_LOAD_USER_PASS" \
  -d "grant_type=password" \
  "https://$SOAM_KC/auth/realms/$SOAM_KC_REALM_ID/protocol/openid-connect/token" | jq -r '.access_token')

echo
echo Writing scope READ_DIGITALID
curl -sX POST "https://$SOAM_KC/auth/admin/realms/$SOAM_KC_REALM_ID/client-scopes" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TKN" \
  -d "{\"description\": \"Read scope for digital ID\",\"id\": \"READ_DIGITALID\",\"name\": \"READ_DIGITALID\",\"protocol\": \"openid-connect\",\"attributes\" : {\"include.in.token.scope\" : \"true\",\"display.on.consent.screen\" : \"false\"}}"

echo
echo Writing scope WRITE_DIGITALID
curl -sX POST "https://$SOAM_KC/auth/admin/realms/$SOAM_KC_REALM_ID/client-scopes" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TKN" \
  -d "{\"description\": \"Write scope for digital ID\",\"id\": \"WRITE_DIGITALID\",\"name\": \"WRITE_DIGITALID\",\"protocol\": \"openid-connect\",\"attributes\" : {\"include.in.token.scope\" : \"true\",\"display.on.consent.screen\" : \"false\"}}"

echo
echo Writing scope READ_DIGITALID_CODETABLE
curl -sX POST "https://$SOAM_KC/auth/admin/realms/$SOAM_KC_REALM_ID/client-scopes" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TKN" \
  -d "{\"description\": \"SOAM send email scope\",\"id\": \"READ_DIGITALID_CODETABLE\",\"name\": \"READ_DIGITALID_CODETABLE\",\"protocol\": \"openid-connect\",\"attributes\" : {\"include.in.token.scope\" : \"true\",\"display.on.consent.screen\" : \"false\"}}"

###########################################################
#Setup for config-map
###########################################################
SPLUNK_URL="gww.splunk.educ.gov.bc.ca"
FLB_CONFIG="[SERVICE]
   Flush        1
   Daemon       Off
   Log_Level    debug
   HTTP_Server   On
   HTTP_Listen   0.0.0.0
   HTTP_Port     2020
   Parsers_File parsers.conf
[INPUT]
   Name   tail
   Path   /mnt/log/*
   Exclude_Path *.gz,*.zip
   Parser docker
   Mem_Buf_Limit 20MB
[FILTER]
   Name record_modifier
   Match *
   Record hostname \${HOSTNAME}
[OUTPUT]
   Name   stdout
   Match  *
[OUTPUT]
   Name  splunk
   Match *
   Host  $SPLUNK_URL
   Port  443
   TLS         On
   TLS.Verify  Off
   Message_Key $APP_NAME
   Splunk_Token $SPLUNK_TOKEN
"
PARSER_CONFIG="
[PARSER]
    Name        docker
    Format      json
"
echo
echo Creating config map "$APP_NAME"-config-map
oc create -n "$OPENSHIFT_NAMESPACE"-"$envValue" configmap "$APP_NAME"-config-map --from-literal=TZ="$TZVALUE" --from-literal=TOKEN_ISSUER_URL="https://$SOAM_KC/auth/realms/$SOAM_KC_REALM_ID" --from-literal=NATS_URL="$NATS_URL" --from-literal=NATS_CLUSTER="$NATS_CLUSTER" --from-literal=JDBC_URL="$DB_JDBC_CONNECT_STRING" --from-literal=ORACLE_USERNAME="$DB_USER" --from-literal=ORACLE_PASSWORD="$DB_PWD" --from-literal=SPRING_SECURITY_LOG_LEVEL=INFO --from-literal=SPRING_WEB_LOG_LEVEL=INFO --from-literal=APP_LOG_LEVEL=INFO --from-literal=SPRING_BOOT_AUTOCONFIG_LOG_LEVEL=INFO --from-literal=SPRING_SHOW_REQUEST_DETAILS=false --from-literal=HIBERNATE_STATISTICS=false --from-literal=NATS_MAX_RECONNECT=60 --from-literal=PURGE_RECORDS_EVENT_AFTER_DAYS=365 --from-literal=SCHEDULED_JOBS_PURGE_OLD_EVENT_RECORDS_CRON="@midnight" --dry-run -o yaml | oc apply -f -
echo
echo Setting environment variables for "$APP_NAME"-$SOAM_KC_REALM_ID application
oc -n "$OPENSHIFT_NAMESPACE"-"$envValue" set env --from=configmap/"$APP_NAME"-config-map dc/"$APP_NAME"-$SOAM_KC_REALM_ID

echo Creating config map "$APP_NAME"-flb-sc-config-map
oc create -n "$OPENSHIFT_NAMESPACE"-"$envValue" configmap "$APP_NAME"-flb-sc-config-map --from-literal=fluent-bit.conf="$FLB_CONFIG" --from-literal=parsers.conf="$PARSER_CONFIG" --dry-run -o yaml | oc apply -f -

echo Removing un-needed config entries
oc -n "$OPENSHIFT_NAMESPACE"-"$envValue" set env dc/"$APP_NAME"-$SOAM_KC_REALM_ID KEYCLOAK_PUBLIC_KEY-
