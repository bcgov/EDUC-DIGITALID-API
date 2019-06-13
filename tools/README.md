# Tools

The Natural Resources Showcase Applications are currently hosted in [RedHat OpenShift](https://www.openshift.com) operated by [BCDevExchange](https://bcdevexchange.org).  We follow the guidelines and principles of the [BCDevOps](https://github.com/BCDevOps) team. We make every attempt to use, or build off the tooling and scripts that BCDevOps (through the [DevHub](https://developer.gov.bc.ca)) provides.    

As part of the BCDevOps community, for each project, we will have 4 OpenShift namespaces:  

* Dev - development "environment", will contain a deployment from the master branch and N deployments for [pull requests](https://help.github.com/en/articles/about-pull-requests).  
* Test - test and Quality Assurance, will contain deployment from master branch. Promotions to be manually approved. 
* Prod - production. The user ready application. Promotions to be manually approved.  
* Tools - devops namespace.   

The tools directory contains all the infrastructure-as-code that we use devops. Although there are many ways to build/test/deploy, this is the way we are doing it.  We currently rely less on OpenShift to do promotions, and more on our own Continuous Integration/Continuous Delivery ([CI/CD](https://en.wikipedia.org/wiki/CI/CD)).  This affords us more control over the workflow and add tooling as needed.  

## Jenkins Setup Overview

Uses BCDevOps CICD Jenkins Basic install.  This Jenkins install includes a lot of customization, of particular note is that it will register GitHub webhooks!  [link](https://github.com/BCDevOps/openshift-components/tree/cvarjao-update-jenkins-basic/cicd/jenkins-basic)

The commands, labels, and naming conventions follow the Pull Request Pipeline principles of the BCDevOps pipeline-cli [link] (https://github.com/BCDevOps/pipeline-cli).

The jobs that Jenkins creates and uses will also follow those principles and build out an "environment" for each pull request.

### Prerequisites

* You should have your 4 OpenShift namespaces (dev, test, prod, tools) and you should have admin access.
* You should have the password for the GitHub educ-pen-registry account. 
* You should have the OpenShift CLI OC tooling installed and configured.
* Administrator access to change environment variables on your machine.
* You have cloned this EDUC-PEN repository on your machine.

## Setup Jenkins

The following commands setup up Jenkins and uses this repository and specific OpenShift project namespaces. The setup below was done using a Windows 10 PC. The environment variables will likely need to change format. 

### Environment variables
For the following examples, we will be using environment variable substitutions.

#### Namespaces
```sh
export tools=c2mvws-tools
export dev=c2mvws-dev
export test=c2mvws-test
export prod=c2mvws-prod
```

#### GitHub repository and credentials
```sh
export gh_username=educ-pen-registry
export gh_password=<personal access token, see above>
export repo_owner=bcgov
export repo_name=EDUC-PEN
export repo_url=https://github.com/bcgov/EDUC-PEN
```

#### Application details
```sh
export app_name=PEN
export app_domain=pathfinder.gov.bc.ca
```

### Login to OpenShift
Login via web console, click your login name at top tight and click "Copy Login Command".  Go to your terminal, navigate to your project root and paste the copy command.

### Navigate to the Jenkins scripts.

```sh
cd tools/jenkins
```

### Create secrets
The BCDevOps CICD Jenkins Basic install requires a template github secret and a template for the slave.  This will create the secrets named as it requires.

```sh
oc process -n $env:tools -f 'openshift/secrets.json' -p GH_USERNAME=$env:gh_username -p GH_PASSWORD=$env:gh_password | oc  -n $env:tools create -f -
```

### Create config map for related namespaces
For our custom jobs scripts and Jenkinsfiles.  

```sh
oc process -n $env:tools -f 'openshift/ns-config.json' -p DEV=$env:dev -p TEST=$env:test -p PROD=$env:prod -p TOOLS=$env:tools | oc  -n $env:tools create -f -
```

### Create config map for the application
For our custom jobs scripts and Jenkinsfiles.  

```sh
oc process -n $env:tools -f 'openshift/jobs-config.json' -p REPO_OWNER=$env:repo_owner -p REPO_NAME=$env:repo_name -p APP_NAME=$env:app_name -p APP_DOMAIN=$env:app_domain | oc -n $env:tools create -f -
```


### Process the build config templates...

These build configs have no build triggers, we start them manually - we don't want OpenShift to automatically deploy on a configuration change or an image change.  

The parameters and labels we are providing match up with the BCDevOps pipeline-cli.  Although we are not using the pipeline-cli, we try to align ourselves with its philosophies.  We will consider this deployment of Jenkins to be our "prod" deployment.  We are not providing all the labels pipeline-cli would, but the most important ones for identifying the app and the environment.  

#### Setup master

```sh
oc process -n $env:tools -f 'openshift/build-master.yaml' -p NAME=jenkins -p SUFFIX=-prod -p VERSION=prod-1.0.0 -p SOURCE_REPOSITORY_URL=$env:repo_url -p SOURCE_REPOSITORY_REF=master -l app-name=jenkins -l env-name=prod -l env-id=0 -l app=jenkins-prod -o yaml | oc -n $env:tools create -f -
```

##### Build and follow master...

```sh
oc start-build -n $env:tools bc/jenkins-prod -F
```

#### Setup slave

```sh
oc process -n $env:tools -f 'openshift/build-slave.yaml' -p NAME=jenkins -p SUFFIX=-prod -p VERSION=prod-1.0.0 -p SLAVE_NAME=main -p SOURCE_IMAGE_STREAM_TAG=jenkins:prod-1.0.0 -l app-name=jenkins -l env-name=prod -l env-id=0 -l app=jenkins-prod -o yaml | oc -n $env:tools create -f -
```

##### Build and follow slave...

```sh
oc start-build -n $env:tools bc/jenkins-slave-main-prod -F
```

### Process the deployment templates

#### master
When this command completes, it will output a listing of objects it has created.  Ignore the following error (or similar):  
> Error from server (AlreadyExists): imagestreams.image.openshift.io "jenkins" already exists

```sh
oc process -n $env:tools -f 'openshift/deploy-master.yaml' -p NAME=jenkins -p SUFFIX=-prod -p VERSION=prod-1.0.0 -p ROUTE_HOST=jenkins-prod-$env:tools.$env:app_domain -p GH_USERNAME=$env:gh_username -p GH_PASSWORD=$env:gh_password -l app-name=jenkins -l env-name=prod -l env-id=0 -l app=jenkins-prod -o yaml | oc -n $env:tools create -f -
```

#### slave

```sh
oc -n $env:tools process -f 'openshift/deploy-slave.yaml' -p NAME=jenkins -p SUFFIX=-prod -p VERSION=prod-1.0.0 -p SLAVE_NAME=build -p 'SLAVE_LABELS=build deploy test ui-test' -p SLAVE_EXECUTORS=3 -p CPU_REQUEST=300m -p CPU_LIMIT=500m -p MEMORY_REQUEST=2Gi -p MEMORY_LIMIT=2Gi -l app-name=jenkins -l env-name=prod -l env-id=0 -l app=jenkins-prod -o yaml | oc -n $env:tools create -f -
```

#### Add service account access to other projects

```
oc -n $env:dev policy add-role-to-user admin system:serviceaccount:$env:tools:jenkins-prod
oc -n $env:test policy add-role-to-user admin system:serviceaccount:$env:tools:jenkins-prod
oc -n $env:prod policy add-role-to-user admin system:serviceaccount:$env:tools:jenkins-prod
```

### Cleanup (WARNING: Use only when required)
This will not clean up the initial secret and config maps we explicitly created

```
oc delete all,template,secret,configmap,pvc,serviceaccount,rolebinding --selector app=jenkins-prod -n $tools
```




