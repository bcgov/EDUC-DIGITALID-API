envValue=$1
APP_NAME=$2
OPENSHIFT_NAMESPACE=$3

TZVALUE="America/Vancouver"
SOAM_KC_REALM_ID="master"
KCADM_FILE_BIN_FOLDER="/home/jenkins/workspace/${OPENSHIFT_NAMESPACE}-tools/keycloak-9.0.3/bin"
DB_JDBC_CONNECT_STRING=$(oc -o json get configmaps ${APP_NAME}-config | sed -n 's/.*"DB_JDBC_CONNECT_STRING": "\(.*\)",/\1/p')
DB_PWD=$(oc -o json get configmaps ${APP_NAME}-config | sed -n 's/.*"DB_PWD_API_DIGITALID": "\(.*\)",/\1/p')
DB_USER=$(oc -o json get configmaps ${APP_NAME}-config | sed -n 's/.*"DB_USER_API_DIGITALID": "\(.*\)",/\1/p')


###########################################################
#Fetch the public key
###########################################################
getPublicKey(){
    executorID= $KCADM_FILE_BIN_FOLDER/kcadm.sh get keys -r $SOAM_KC_REALM_ID | grep -Po 'publicKey" : "\K([^"]*)'
}

echo Fetching public key from SOAM
soamFullPublicKey="-----BEGIN PUBLIC KEY----- $(getPublicKey) -----END PUBLIC KEY-----"
newline=$'\n'
formattedPublicKey="${soamFullPublicKey:0:26}${newline}${soamFullPublicKey:27:64}${newline}${soamFullPublicKey:91:64}${newline}${soamFullPublicKey:155:64}${newline}${soamFullPublicKey:219:64}${newline}${soamFullPublicKey:283:64}${newline}${soamFullPublicKey:347:64}${newline}${soamFullPublicKey:411:9}${newline}${soamFullPublicKey:420}"


###########################################################
#Setup for digitalid-api-config-map
###########################################################
echo
echo Creating config map $APP_NAME-config-map 
oc create -n $OPENSHIFT_NAMESPACE-$envValue configmap $APP_NAME-config-map --from-literal=TZ=$TZVALUE --from-literal=JDBC_URL=$DB_JDBC_CONNECT_STRING --from-literal=ORACLE_USERNAME="$DB_USER" --from-literal=ORACLE_PASSWORD="$DB_PWD" --from-literal=KEYCLOAK_PUBLIC_KEY="$soamFullPublicKey" --from-literal=SPRING_SECURITY_LOG_LEVEL=INFO --from-literal=SPRING_WEB_LOG_LEVEL=INFO --from-literal=APP_LOG_LEVEL=INFO --from-literal=SPRING_BOOT_AUTOCONFIG_LOG_LEVEL=INFO --from-literal=SPRING_SHOW_REQUEST_DETAILS=false --dry-run -o yaml | oc apply -f -
echo
echo Setting environment variables for $APP_NAME-$SOAM_KC_REALM_ID application
oc project $OPENSHIFT_NAMESPACE-$envValue
oc set env --from=configmap/$APP_NAME-config-map dc/$APP_NAME-$SOAM_KC_REALM_ID