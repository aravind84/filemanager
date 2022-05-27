1. Install kind : go install sigs.k8s.io/kind@v0.14.0 && export PATH=$PATH:$(go env GOPATH)/bin && ./kind-cluster.sh
2. Create cluster with kind-cluster.sh
3. gradle clean build
   a. ./gradlew clean build
4. Docker build image with Dockerfile and push to local registry 
  a. docker build -t filemanager:1.0 . 
  b. docker tag filemanager:3.0 localhost:5001/filemanager:3.0
  c. docker push localhost:5001/filemanager:3.0
5. Deploy prometheus helm chart
   a. helm repo add prometheus-community https://prometheus-community.github.io/helm-charts
   b. helm repo update
   c. helm -n monitoring install prometheus prometheus-community/kube-prometheus-stack --create-namespace=true
6. Deploy filemanager helm chart
   a. helm -n filemanager install filemanager . --create-namespace
7. Check the prometheus UI




Pending:
	Push metrics to prometheus
	


Pre-requisites:
	This project assume below software/tools are already available in the machine the runs the applications
	1. docker >2.10.5
	2. kind
	3. k8s >1.21
	4. go 1.17.6
	5. helm3
	6. 