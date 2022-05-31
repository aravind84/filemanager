# Filemanager

## About

Filemanager is an utility to manage file uploads exposed by endpoints/APIs

- GET /image/all - List all uploaded images/files
- GET /image/duplicates - Returns the duplicate images/files
- GET /image/{id:.+} - Returns a specific image/file based on the Id passed in the request
- POST /image - Upload an image/file to the temp storage.

### Technology and Tools used

- Spring boot
- Java 8
- gradle
- Postman
- Eclipse
- Make


## Prerequisites

This project assume below software/tools are readily available in the local or a cluster on cloud to run the application
- docker >2.10.5
- kind
- k8s >1.21
- go 1.17.6
- helm3


## Build and Deployment


Tips: below command will list all the stages in the make process

```
make help
```

The application is built and deployed into local kind cluster via Makefile that exposes different stages as below


```
1. Create kind cluster in local machine
2. Build the spring boot app via gradle
3. Build docker image and push it into the local cluster registry
4. Deploy monitoring tools and its dependencies. Prometheus and grafana.
5. Deploy the filemanager applicaiton into the kind cluster

```

Once the source code is downloaded to the destination machine run below commands to bring up the local kind cluster and deploy all the apps and dependencies - considering the root directory is **filemanager**

```
cd filemanager
make
```

## How to test the application:


If the make is successful as per above step, then the below components are up and running in the k8s cluster :)

1. Prometheus - **namespace: monitoring**
2. Grafana - **namespace: monitoring**
3. Filemanager application - **namespace: filemanager**

To start with accessing this stack, follow below simple steps to tunnel into their respective service and access them via a web browser

```
kubectl -n monitoring port-forward svc/prometheus-kube-prometheus-prometheus 9090:9090
kubectl -n monitoring port-forward svc/prometheus-grafana 9080:80
kubectl -n filemanager port-forward svc/filemanager 8090:80
```
I've used Postman to access the file maanger API endpoints and web browser for Prometheus and Grafana.

*Please Note: Sine the desired k8s cluster is unknown at the moment, I've used the basic kubectl port forwarding to get this working. If the actual cluster has required supporting tools then the ingress can be enabled and a DNS can be used to access the services. To achieve this below components needs to be in place in the k8s cluster*

- ngnix-ingress controller
- external-dns
- certmanager


## Monitoring and Dashboards

Have used below 2 out-of-the-box dashboards from the grafana community to make the monitoring simple and quick. 

* K8s cluster monitoring: https://grafana.com/grafana/dashboards/315
* JVM monitoring: https://grafana.com/grafana/dashboards/4701 

To import them into grafana, 

```
1. GO to dashboard view
2. in Manage section => Select import button from the top right corner
3. Provide the Id of the dashboard to import - Here it is 315 and 4701
4. Import
```

In addition to these dashboards, I've created a simple dashboard which monitors the API requests to the pods.

```
1. GO to dashboard view
2. in Manage section => Select import button from the top right corner
3. Copy/Paste the JSON file filemanager/grafana_dashboards/filemanager_http_req_dashboard.json into the panel Json section
4. Import
```

![/assets/filemanager-dashboard.png]

TIPS: 
_- Start some transactions so that the graph will plot if not there will be no data shown on the tiles._
_- Change the time range to 5mins or 15mins to see the data clearly. Mostly the default time range for the dashboard imports are 24hrs which is a long time window for visualization._


## Incomplete

- Alertmanager configuration not done.

## Improvements

- Code and helm chart cleanup. Due to time constraint I did not cleanup the helm charts and so it may have unnecessary code here and there.
- Build the applicaiton with latest java version. I've extended the readily available spring boot app example to meet the project requirement. So, the application can be even more sleek and compatible if built on latest Java version.
- Fail safe Make process. As explained in below section, the failure scenarios were not handled in the Makefile which needs improvement.
- Loki setup to push the application logs into Grafana will help to monitor and troubleshoot application related issue which is missing as the logs are not shipped anywhere.


### How to cleanup the stack?

```
cd filemanager
make clean
```

*Please note: If the deployment of the charts aren't successful then the clean will fail complaining about the release not available. So, this is not fail safe handled process, considering it is going to be a smooth install process and a cleanup. But a repetitive process of commissioning and decommissioning the stack any number of time.*



	
Grafana login pwd:
user: admin
pass: prom-operator