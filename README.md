# job-executor-pod
This is POC application to demonstrate on how can we run the job that will monitor and manipulate pods in a Kubernetes cluster.
## Tools and Library used:
You will need
* Java 17
* Spring Boot 3.1.3
* Kubernetes client 15.0.1
* Spring Boot's Quartz
* Spring Boot's Webflux
## Running the application
### Prepare environment
1. Setup a Kubernetes environment
2. Create docker registry secret by replacing the credential and running the command:
```
  kubectl create secret docker-registry gcm-k3s-secret \
  --docker-server=registry.hub.docker.com/glennmorales \
  --docker-username=DOCKER_USER \
  --docker-password=DOCKER_PASSWORD
  ```
3. Deploy Edge
   1. Copy and update kubernetes/edge.yaml. Replace environment variables and docker image to use.
   2. Then run
   ```shell
      kubectl apply -f kubernetes/edge.yaml
   ```
4. Running locally   
   1. Run JobExecutorPocApplication class in InteliJ IDE or run below command.
   ```shell
    ./mvnw spring-boot:run
   ```
5. Running in Kubernetes cluster
    1. Build and push docker image
    ```shell
      # build docker image
       ./mvnw spring-boot:build-image
      # tag docker image
       docker tag job-executor-poc:0.0.1-SNAPSHOT glennmorales/job-executor-poc:1
      # push docker image
       docker push glennmorales/job-executor-poc:1
    ```
   2. Deploy the app (for AWS EC2, below commands might require admin privileges )
   ```shell
      #create app service account
      kubectl apply -f kubernetes/job-executor-job-svc-accnt.yaml
      #deploy the app service account
      kubectl apply -f kubernetes/job-executor-poc.yaml
   ```
   3. Ensure the pods are running by executing below commands
   ```shell
      kubectl get pods
   ```
   
### Testing the application
1. Once the app is running, it should display the pods and its status in the logs
   ```shell
      kubectl logs -f [POD_NAME]
   ```
   then it should something like this every N seconds ...
   ```shell
      Listing available pods ...
      Pod Name: job-executor-poc-dp-866676dbfd-blp2c   Status: Running
      Pod Name: edge-deployment-58cbc8669c-p77n6   Status: Running
   ```
2. The app is consuming message from Wiremock UI to mimic request from EdgeLink for updating and scaling cluster instances. 
    WireMock URl: https://app.wiremock.cloud/mock-apis/9vzq6/stubs/a56be236-e789-45d5-8d0a-1ceb20820ccc
    Request Name: Update Pods
      
   1. To scale Edge, set requestType to "scale" and numberOfInstance for the number of instances. Save the request.
     ```json
   {
    "requestId": 1,
    "requestType": "scale",
    "numberOfInstance": 1,
    "image": "glennmorales/testsr15:gcm-SR-15-0-25-2-4825"
   }
    ```
   logs should show if the number of instance got change. Or verify it by running ```kubectl get pods```
   ```shell
     Scaling pods to 1 instance ...
     Successfully scaled edge App to 1 number of instance
   ```
   2.  To update Edge, set requestType to "updateImage" and image for the new image name to use. Save the request.
    ```json
    {
        "requestId": 1,
        "requestType": "updateImage",
        "numberOfInstance": 1,
        "image": "glennmorales/testsr15:gcm-SR-15-0-25-2-4825"
    }
    ```
      
    logs should show if the number of instance got change. Or verify it by running ```kubectl get pods```, it should bring down the old pods and create new ones using the new image
      
     ```shell
      Changing app image to glennmorales/testsr15:gcm-SR-15-0-25-2-4825 ...
      Successfully changed edge app image to glennmorales/testsr15:gcm-SR-15-0-25-2-4825
     ```
     
### Monitoring    
Setup dashboard for monitoring. 
see guide here -> https://vertexinc.atlassian.net/wiki/spaces/ITE/pages/4125557127/Kubernetes+Dashboard

