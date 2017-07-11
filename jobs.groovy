def gitrepo1 = 'MNT-Lab/groovy-scripts'
def gitrepo2 = 'MNT-Lab/build-principals'
def gitrepoforked = 'VadzimTarasiuk/jboss-eap-quickstarts'
def versionbranch = '7.1.0.Beta'
def branchname = 'vtarasiuk'

/** Name Section **/

/** Setting master-job name*/
def lord = 'MNT-CD-module9-build-job'

/** Setting list of child job names  (hardcode)*/


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
            remote {
                github (gitrepo1)
                name (branchname)
            }
            extensions {
                relativeTargetDirectory('$WORKSPACE/scripts')
            }
        }
        git {
            remote {
                github (gitrepo2)
                name (branchname)
            }
            extensions {
                relativeTargetDirectory('$WORKSPACE/buildp')
            }
        }
        git {
            remote {
                github (gitrepoforked)
                name (versionbranch)
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
        shell('tar -zcvf $ARTIFACT_NAME.tar.gz -C jboss-eap/helloworld/target/ helloworld.war && cp $ARTIFACT_NAME.tar.gz ./scripts/')
        groovyScriptFile ('scripts/pull-push.groovy -p push -a $ARTIFACT_NAME')
    }
    publishers {
        archiveArtifacts('*.tar.gz')
    }
}

