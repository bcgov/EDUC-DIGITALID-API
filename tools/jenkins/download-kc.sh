namespace=$1

if [ ! -f /tmp/keycloak-9.0.3.zip ]
then
    echo 'Downloading and unzipping keycloak setup files...'
    wget -P /tmp -nc https://downloads.jboss.org/keycloak/9.0.3/keycloak-9.0.3.zip
    cd /tmp
    unzip keycloak-9.0.3.zip
fi