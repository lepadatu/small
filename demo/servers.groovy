def deploy(id) {
    unstash 'war'
    if (id.equals("staging") || id.equals("production") || id.equals("qa")) {
        sh "cp x.war OracleWebLogic/samples/1221-appdeploy-${id}/container-scripts/${id}.war"
        //If there are already docker images with the same name, delete their corresponding containers and then delete them
        //sh "[ -z \$(docker images -q 1221-appdeploy-qa) ] || docker rm \$(docker stop \$(docker ps -a -q --filter ancestor=1221-appdeploy-qa)) || \
        //    docker rmi 1221-appdeploy-qa"
        //Stop & remove containers running on port 8001
        sh "docker ps | grep \".*:8001\" | awk '{print \$1}' | xargs -I {} sh -c \"docker stop {} ; docker rm {}\""
        sh "docker build -t 1221-appdeploy-${id} OracleWebLogic/samples/1221-appdeploy-${id}/"
        sh "docker run -d -p 8001:8001 1221-appdeploy-${id}"
        sh "sleep 15"

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
