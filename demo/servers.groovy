def deploy(id) {
    unstash 'war'
    if (id.equals("staging") || id.equals("production") || id.equals("qa")) {
        sh "cp x.war OracleWebLogic/samples/1221-appdeploy-${id}/${id}.war"
        //If there are already docker images with the same name, delete their corresponding containers and then delete them
        sh "[ -z \$(docker images -q 1221-appdeploy-${id}) ] || docker rm \$(docker stop \$(docker ps -a -q --filter ancestor=1221-appdeploy-${id})) || \
            docker rmi 1221-appdeploy-${id}"
        sh "sleep 20"
        sh "docker build -t 1221-appdeploy-${id} OracleWebLogic/samples/1221-appdeploy-${id}/"
        sh "docker run -d -p 8001:8001 1221-appdeploy-${id}"
        sh "sleep 30"

    }

}

def undeploy(id) {
    sh "rm x.war"
}

def runWithServer(body) {
    def id = 'qa'.toString()
    deploy id
    try {
        body.call id
    } finally {
        undeploy id
    }
}

this
