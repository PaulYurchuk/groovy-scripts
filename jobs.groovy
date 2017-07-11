import hudson.*

def gitrepo1 = 'MNT-Lab/groovy-scripts'
def gitrepo2 = 'MNT-Lab/build-principals'
def gitrepoforked = 'VadzimTarasiuk/jboss-eap-quickstarts'
def versionbranch = '7.1.0.Beta'
def branchname = 'vtarasiuk'

/** Name Section **/

/** Setting master-job name*/
def builder = 'MNT-CD-module9-build-job'
def deployer = 'MNT-CD-module9-deploy-job'

String someScript = readFile ("scripts/list.groovy")

/**Job Section**/

/** Create Master job*/
mavenJob(builder) {
    parameters {
        stringParam('ARTIFACT_NAME', 'helloworld-$BUILD_NUMBER', 'Artifact name: suffix + build Number')
    }
    multiscm {
        git {
            branch(branchname)
            remote {
                credentials('b50d63c2-8c84-4d1c-b557-4ee892a1591f')
                github (gitrepo1)
            }
            extensions {
                relativeTargetDirectory('$WORKSPACE/scripts')
            }
        }
        git {
            branch(branchname)
            remote {
                credentials('b50d63c2-8c84-4d1c-b557-4ee892a1591f')
                github (gitrepo2)
            }
            extensions {
                relativeTargetDirectory('$WORKSPACE/buildp')
            }
        }
        git {
            branch(versionbranch)
            remote {
                credentials('b50d63c2-8c84-4d1c-b557-4ee892a1591f')
                github (gitrepoforked)
            }
            extensions {
                relativeTargetDirectory('$WORKSPACE/jboss-eap')
            }
        }
    }
    preBuildSteps {
        shell ("jboss-eap/helloworld/autofill.sh")
    }
    rootPOM ('jboss-eap/helloworld/pom.xml')
    goals ('clean install -DskipTests')
    postBuildSteps ('SUCCESS') {
        shell('tar -zcvf $ARTIFACT_NAME.tar.gz -C jboss-eap/helloworld/target/ helloworld.war && cp scripts/pull-push.groovy ./')
        groovyScriptFile ('pull-push.groovy','Binary') {
            scriptParam('-p push')
            scriptParam('-a $ARTIFACT_NAME')
        }
    }
    publishers {
        archiveArtifacts('*.tar.gz')
    }
}
/** Job for deploy*/
job (deployer){
    parameters {
        activeChoiceParam('ARTIFACT_NAME') {
            choiceType('MULTI_SELECT')
            description('Choose the artifact')
            groovyScript {
                script(someScript)
            }
        }
    }
    multiscm {
        git {
            branch(branchname)
            remote {
                credentials('b50d63c2-8c84-4d1c-b557-4ee892a1591f')
                github(gitrepo1)
            }
            extensions {
                relativeTargetDirectory('$WORKSPACE/scripts')
            }
        }
        git {
            branch(branchname)
            remote {
                credentials('b50d63c2-8c84-4d1c-b557-4ee892a1591f')
                github(gitrepo2)
            }
            extensions {
                relativeTargetDirectory('$WORKSPACE/buildp')
            }
        }
    }
}

