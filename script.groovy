import groovy.json.JsonSlurper

def query = """ { "action": "coreui_Component",    "method":"readAssets",    "data":[{"page":"1", "start":"0",    "limit":"300", "sort":[{"property":"name","direction":"ASC"}],    "filter":[{"property":"repositoryName","value":"task8"}]}],    "type":"rpc",    "tid":15	} """
def connection = new URL( "http://nexus/service/extdirect" )
        .openConnection() as HttpURLConnection
def auth = "admin:admin123".getBytes().encodeBase64().toString()

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

/*
def listArtifacts = []
if ( connection.responseCode == 200){
    // get the JSON response

    def json = connection.getInputStream() { inStream ->
        new JsonSlurper().parse( inStream as InputStream )
    }
def object = json
    // extract some data from the JSON, printing a report
        println item.title
    println "Temperature: ${item.condition?.temp}, Condition: ${item.condition?.text}"

    show some forecasts
    println "Forecasts:"
    item.forecast.each { f ->
        println " * ${f.date} - Low: ${f.low}, High: ${f.high}, Condition: ${f.text}"

    }

} else {
    println connection.responseCode + ": " + connection.inputStream.text
}
*/

println connection.responseCode + ": " + connection.inputStream.text
