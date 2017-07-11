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

String someScript = ('''
//Get list of Artifacts from Nexus3 to Jenkins
def nexusURL = "http://localhost:52471"
def repo = "project-releases"
def cred = "nexus-service-user:jenkins"
def query = """ { "action": "coreui_Component",    "method":"readAssets",    "data":[{"page":"1", "start":"0",    "limit":"300", "sort":[{"property":"name","direction":"ASC"}],    "filter":[{"property":"repositoryName","value":"${repo}"}]}],    "type":"rpc",    "tid":15\t} """
def connection = new URL( "${nexusURL}/service/extdirect" )
        .openConnection() as HttpURLConnection
def auth = "${cred}".getBytes().encodeBase64().toString()

connection.setRequestMethod("POST")
//allow write to connection
connection.doOutput = true
// set some headers
connection.setRequestProperty("Authorization" , "Basic ${auth}")
connection.setRequestProperty( "Content-Type", "application/json" )
connection.setRequestProperty( "Accept", "*/*" )
connection.setRequestProperty( "User-Agent", 'groovy-2.4.4' )
// write query to Output in JSON format
def writer = new OutputStreamWriter(connection.outputStream)
writer.write(query)
writer.flush()
writer.close()
connection.connect()
//parser Response from Nexus3
def listArtifacts=[]

def slurper = new groovy.json.JsonSlurper()
def response = connection.inputStream.text
def parsed = slurper.parseText(response)

parsed.result.data.each {
    if (it.name.matches(~/.+.tar.gz/)) {
        def cutStr = it.name.substring(it.name.lastIndexOf("/")+1 , it.name.length())
        listArtifacts.add(cutStr)

    }
}
listArtifacts.sort { a, b ->
    (a,b) = [a, b].collect { (it =~ /\\d+/)[-1] as Integer }
    b <=> a}

return listArtifacts
''')

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
                //credentials('b50d63c2-8c84-4d1c-b557-4ee892a1591f')
                github (gitrepo1)
            }
            extensions {
                relativeTargetDirectory('$WORKSPACE/scripts')
            }
        }
        git {
            branch(branchname)
            remote {
                //credentials('b50d63c2-8c84-4d1c-b557-4ee892a1591f')
                github (gitrepo2)
            }
            extensions {
                relativeTargetDirectory('$WORKSPACE/buildp')
            }
        }
        git {
            branch(versionbranch)
            remote {
                //credentials('b50d63c2-8c84-4d1c-b557-4ee892a1591f')
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
        groovyScriptFile ('pull-push.groovy') {
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
            choiceType('SINGLE_SELECT')
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
                //credentials('b50d63c2-8c84-4d1c-b557-4ee892a1591f')
                github(gitrepo1)
            }
            extensions {
                relativeTargetDirectory('$WORKSPACE/scripts')
            }
        }
        git {
            branch(branchname)
            remote {
                //credentials('b50d63c2-8c84-4d1c-b557-4ee892a1591f')
                github(gitrepo2)
            }
            extensions {
                relativeTargetDirectory('$WORKSPACE/buildp')
            }
        }
    }
    steps {
        shell ('cp scripts/pull-push.groovy ./')
        groovyScriptFile('pull-push.groovy') {
            scriptParam('-p pull')
            scriptParam('-a $ARTIFACT_NAME')
        }
        shell ('tar -xzf $ARTIFACT_NAME')
    }
    publishers {
            archiveArtifacts('*.tar.gz')
    }
}


