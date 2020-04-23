namespace=$1

if [ ! -f home/jenkins/workspace/$namespace-tools/keycloak-9.0.3.zip ]
then
    echo 'Downloading and unzipping keycloak setup files...'
    wget -P /home/jenkins/workspace/$namespace-tools -nc https://downloads.jboss.org/keycloak/9.0.3/keycloak-9.0.3.zip
    cd /home/jenkins/workspace/$namespace-tools
    unzip keycloak-9.0.3.zip
fi