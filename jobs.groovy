def gitrepo1 = 'MNT-Lab/groovy-scripts'
def gitrepo2 = 'MNT-Lab/build-principals'
def gitrepoforked = 'VadzimTarasiuk/jboss-eap-quickstarts'
def versionbranch = '7.1.0.Beta'
def branchname = 'vtarasiuk'

/** Name Section **/

/** Setting master-job name*/
def lord = 'MNT-CD-module9-build-job'


/**Job Section**/

/** Create Master job*/
mavenJob("${lord}") {
    parameters {
        parameters {
            stringParam('ARTIFACT_NAME', 'helloworld-$BUILD_NUMBER', 'Artifact name: suffix + build Number')
        }
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
        shell('tar -zcvf $ARTIFACT_NAME.tar.gz -C jboss-eap/helloworld/target/ helloworld.war && cp $ARTIFACT_NAME.tar.gz scripts/$ARTIFACT_NAME.tar.gz')
        groovyScriptFile ('scripts/pull-push.groovy -p push -a $ARTIFACT_NAME')
    }
    publishers {
        archiveArtifacts('*.tar.gz')
    }
}

