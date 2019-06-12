pipeline {
    agent any

    stages {
        stage('Build') {
            agent { label 'master' }
            steps {
                script {
                    openshift.withCluster() {
	                    openshift.withProject() {
	                    def builds = openshift.selector("bc", "pen-registry-api").related('builds')
		                    timeout(5) { 
		                    	builds.untilEach(1) {
		                     	 return (it.object().status.phase == "Complete")
		                    	}
	                    	}
                		}
            		}
            	}
            }
        }
        stage('Test') {
            steps {
                echo 'Testing..'
            }
        }
        stage('Deploy') {
            steps {
                echo 'Deploying....'
            }
        }
    }
}