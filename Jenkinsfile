pipeline{
    agent any
        stage('Stack creation'){
           steps {
                jobDsl targets: "jobs.groovy"
            }
        }
    }
}