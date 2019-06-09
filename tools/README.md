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

### Additional Setup Files/Scripts

* **jenkins/docker/contrib/jenkins/configuration**

	We need to make some additional configuration changes to the BCDevOps CICD Jenkins Basic install.  Under the [jenkins/docker/contrib/jenkins/configuration](jenkins/docker/contrib/jenkins/configuration) directory, we have additional start up scripts and configuration overrides.

* **org.jenkinsci.plugins.workflow.libs.GlobalLibraries.xml**

	We pull in [BCDevOps Jenkins Pipeline Shared Lib](https://github.com/BCDevOps/jenkins-pipeline-shared-lib).  This provides us some functions for examining the Git repos and commits. We can use these functions in Jenkinsfiles and other grovy scripts.

* **scriptApproval.xml**

	For our configuration groovy scripts, we need to allow certain jenkins and third party plugin scripts to run.  We override the restrictions here.

* **init.groovy.d/003-create-jobs.groovy**

	This groovy script will build 2 jobs in Jenkins.  One that will build the master branch, and one that will build pull requests.  
	
	To add or change the jobs, this is where you want to go.  The name of this file is important, as it needs to get run *BEFORE* the 003-register-github-webhooks.groovy included in the basic install.  Scripts are run alphabetically.  The jobs need to be created before the github webhooks are created.  Our jobs script will read secrets and configmaps created during this setup; described below.
	
	These jobs are configured to use [Jenkinsfile](../Jenkinsfile) and [Jenkinsfile.cicd](../Jenkinsfile.cicd) found at the root of this repository.  These Jenkinsfiles will make use of the OpenShift ConfigMaps we will create below.

* **init.groovy.d/100-jenkins-config-set-admin-address.groovy**

	This groovy script will update the admin email address.  It is not necessary, but an example of further customization to Jenkins.  If you are swiping this, keep in mind the email address is hardcoded.

### Prerequisites

You should have your 4 OpenShift namespaces (dev, test, prod, tools) and you should have admin access.

You will need a github account and token (preferrably a team shared account) with access to your repo: [New Personal Access Token](https://github.com/settings/tokens/new?scopes=repo,read:user,user:email,admin:repo_hook).

## Setup Jenkins

The following commands setup up Jenkins and uses this repository and specific OpenShift project namespaces.

### environment variables
For the following examples, we will be using environment variable substitutions.  Set your environment variables as necessary.  This is not required, but will make using the provided OpenShift commands much easier.  

#### namespaces
```sh
export tools=z208i4-tools
export dev=z208i4-dev
export test=z208i4-test
export prod=z208i4-prod
```

#### github repository and credentials
```sh
export gh_username=<github account>
export gh_password=<personal access token, see above>
export repo_owner=bcgov
export repo_name=nr-messaging-service-showcase
export repo_url=https://github.com/bcgov/nr-messaging-service-showcase
```

#### application details
```sh
export app_name=mssc
export app_domain=pathfinder.gov.bc.ca
```

### login to openshift
Login via web console, click your login name at top tight and click "Copy Login Command".  Go to your terminal, go to your project root and paste the copy command.

### navigate to the jenkins scripts.

```sh
cd tools/jenkins
```

### create secrets
The BCDevOps CICD Jenkins Basic install requires a template github secret and a template for the slave.  This will create the secrets named as it requires.

```sh
oc -n $tools process -f 'openshift/secrets.json' -p GH_USERNAME=$gh_username -p GH_PASSWORD=$gh_password | oc  -n $tools create -f -
```

### create config map for related namespaces
For our custom jobs scripts and Jenkinsfiles.  

```sh
oc -n $tools process -f 'openshift/ns-config.json' -p DEV=$dev -p TEST=$test -p PROD=$prod -p TOOLS=$tools | oc  -n $tools create -f -
```

### create config map for the application
For our custom jobs scripts and Jenkinsfiles.  

```sh
oc -n $tools process -f 'openshift/jobs-config.json' -p REPO_OWNER=$repo_owner -p REPO_NAME=$repo_name -p APP_NAME=$app_name -p APP_DOMAIN=$app_domain | oc -n $tools create -f -
```


### process the build config templates...

These build configs have no build triggers, we start them manually - we don't want OpenShift to automatically deploy on a configuration change or an image change.  

The parameters and labels we are providing match up with the BCDevOps pipeline-cli.  Although we are not using the pipeline-cli, we try to align ourselves with its philosophies.  We will consider this deployment of Jenkins to be our "prod" deployment.  We are not providing all the labels pipeline-cli would, but the most important ones for identifying the app and the environment.  

#### master

```sh
oc -n $tools process -f 'openshift/build-master.yaml' -p NAME=jenkins -p SUFFIX=-prod -p VERSION=prod-1.0.0 -p SOURCE_REPOSITORY_URL=$repo_url -p SOURCE_REPOSITORY_REF=master -l app-name=jenkins -l env-name=prod -l env-id=0 -l app=jenkins-prod -o yaml | oc -n $tools create -f -
```

##### build and follow...

```sh
oc -n $tools start-build bc/jenkins-prod -F
```

#### slave

```sh
oc -n $tools process -f 'openshift/build-slave.yaml' -p NAME=jenkins -p SUFFIX=-prod -p VERSION=prod-1.0.0 -p SLAVE_NAME=main -p SOURCE_IMAGE_STREAM_TAG=jenkins:prod-1.0.0 -l app-name=jenkins -l env-name=prod -l env-id=0 -l app=jenkins-prod -o yaml | oc -n $tools create -f -
```

##### build and follow...

```sh
oc -n $tools start-build bc/jenkins-slave-main-prod -F
```


### process the deployment templates

#### master
When this command completes, it will output a listing of objects it has created.  Ignore the following error (or similar):  
> Error from server (AlreadyExists): imagestreams.image.openshift.io "jenkins" already exists

```sh
oc -n $tools process -f 'openshift/deploy-master.yaml' -p NAME=jenkins -p SUFFIX=-prod -p VERSION=prod-1.0.0 -p ROUTE_HOST=jenkins-prod-$tools.$app_domain -p GH_USERNAME=$gh_username -p GH_PASSWORD=$gh_password -l app-name=jenkins -l env-name=prod -l env-id=0 -l app=jenkins-prod -o yaml | oc -n $tools create -f -

```

#### slave

```sh
oc -n $tools process -f 'openshift/deploy-slave.yaml' -p NAME=jenkins -p SUFFIX=-prod -p VERSION=prod-1.0.0 -p SLAVE_NAME=build -p 'SLAVE_LABELS=build deploy test ui-test' -p SLAVE_EXECUTORS=3 -p CPU_REQUEST=300m -p CPU_LIMIT=500m -p MEMORY_REQUEST=2Gi -p MEMORY_LIMIT=2Gi -l app-name=jenkins -l env-name=prod -l env-id=0 -l app=jenkins-prod -o yaml | oc -n $tools create -f -
```

#### add service account access to other projects

```
oc -n $dev policy add-role-to-user admin system:serviceaccount:$tools:jenkins-prod
oc -n $test policy add-role-to-user admin system:serviceaccount:$tools:jenkins-prod
oc -n $prod policy add-role-to-user admin system:serviceaccount:$tools:jenkins-prod
```

### cleanup
This will not clean up the initial secret and config maps we explicitly created

```
oc delete all,template,secret,configmap,pvc,serviceaccount,rolebinding --selector app=jenkins-prod -n $tools
```




