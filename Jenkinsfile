pipeline {
     agent {
        node {label 'maven'}
    }
     environment {
        APPLICATION_NAME = 'RichHelloWorld'
        GIT_REPO="https://github.com/RichJJones100/RichHelloWorld.git"
        GIT_BRANCH="master"
        STAGE_TAG = "promoteToStage"
        DEV_PROJECT = "dev"
        STAGE_PROJECT = "stage"
        TEMPLATE_NAME = "RichHelloWorld"
        ARTIFACT_FOLDER = "target"
        PORT = 8080;
    }
    stages {
		stage("Checkout") {
		 steps {
			git url: "${GIT_REPO}", branch: "${GIT_BRANCH}"
		 }
		}
		stage('Unit tests') {
			steps {
				echo "-=- execute unit tests -=-"
				sh "mvn test"
				junit 'target/surefire-reports/*.xml'
			}
		}
		stage("Build JAR") {
		 steps {
			sh "mvn clean package -DskipTests -Popenshift"
			/**stash name:"jar", includes:"target/ROOT.jar"**/
		 }
		}
		 stage('Store Artifacts'){
			steps{
				script{
				def safeBuildName  = "${APPLICATION_NAME}_${BUILD_NUMBER}",
					artifactFolder = "${ARTIFACT_FOLDER}",
					fullFileName   = "${safeBuildName}.tar.gz",
					applicationZip = "${artifactFolder}/${fullFileName}"
					applicationDir = ["target",
									  "Dockerfile",
										].join(" ");
			def needTargetPath = !fileExists("${artifactFolder}")
			if (needTargetPath) {
				sh "mkdir ${artifactFolder}"
			}
			sh "touch ${applicationZip}"
			sh "tar --exclude=${applicationZip} -czvf ${applicationZip} ${applicationDir}"
			archiveArtifacts artifacts: "${applicationZip}", excludes: null, onlyIfSuccessful: true
			}
			}
		  }
		  stage('Create Image Builder') {
            when {
                expression {
                    openshift.withCluster() {
                        openshift.withProject(DEV_PROJECT) {
                            return !openshift.selector("bc", "${TEMPLATE_NAME}").exists();
                        }
                }
            }
        }
        steps {
            script {
                openshift.withCluster() {
                    openshift.withProject(DEV_PROJECT) {
					echo "New build for ${TEMPLATE_NAME}"
                        openshift.newBuild("--name=${TEMPLATE_NAME}", "--docker-image=registry.access.redhat.com/redhat-openjdk-18/openjdk18-openshift", "--binary=true")
                        }
                    }
                }
            }
        }

        stage('Build Image') {
            steps {
                script {
                    openshift.withCluster() {
                        openshift.withProject(env.DEV_PROJECT) {
                            openshift.selector("bc", "$TEMPLATE_NAME").startBuild("--from-archive=${ARTIFACT_FOLDER}/${APPLICATION_NAME}_${BUILD_NUMBER}.tar.gz", "--wait=true")
                        }
                    }
                }
            }
        }
		  stage("Deploy") {
	      when {
			  expression {
				  openshift.withCluster() {
					  openshift.withProject(env.DEV_PROJECT) {
						  return !openshift.selector('dc', "${TEMPLATE_NAME}").exists()
					  }
				  }
			  }
            }
            steps {
                script {
                    openshift.withCluster() {
                        openshift.withProject(env.DEV_PROJECT) {
                            def app = openshift.newApp("${TEMPLATE_NAME}:latest")
                            app.narrow("svc").expose("--port=${PORT}");
                            def dc = openshift.selector("dc", "${TEMPLATE_NAME}")
                            while (dc.object().spec.replicas != dc.object().status.availableReplicas) {
                                sleep 10
                            }
                        }
                    }
                }
            }
		  }
        }
}