//Get list of Artifacts from Nexus3 to Jenkins
def nexusURL = "http://nexus"
def repo = "task8"
def cred = "admin:admin123"
def query = """ { "action": "coreui_Component",    "method":"readAssets",    "data":[{"page":"1", "start":"0",    "limit":"300", "sort":[{"property":"name","direction":"ASC"}],    "filter":[{"property":"repositoryName","value":"${repo}"]}],    "type":"rpc",    "tid":15	} """
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

listArtifacts.reverse()
